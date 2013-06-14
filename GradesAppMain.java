import java.awt.event.*;
import java.util.ArrayList;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.JOptionPane;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class GradesAppMain {
	//login data
	public static final String HOME_URL = "https://ps01.bergen.org/public/home.html";
	public static final String GRADES_URL = "https://ps01.bergen.org/guardian/home.html";
	final static String serviceName="PS+Parent+Portal", credentialType="User+Id+and+Password+Credential", pcasServerUrl="/";
	static String pstoken, contextData;

	public static float[] STUDENT_GPAS;

	public static void main(String[] args) throws Exception {
		System.out.println("Program running");

		final GradesAppLoginGUI logingui = new GradesAppLoginGUI();
		logingui.loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logingui.frame.setVisible(false);
				loginToPS(logingui.unameField.getText(), new String(logingui.pwField.getPassword()));
			}
		});
		logingui.frame.setVisible(true);
	}

	static void loginToPS(String username, String password){
		try {
			//get login page
			System.out.println("getting login page");
			Connection.Response resp = Jsoup.connect(HOME_URL).method(Connection.Method.GET).execute();
			//get some hidden login values
			pstoken = resp.parse().body().getElementsByAttributeValue("name", "pstoken").val();
			contextData = resp.parse().body().getElementsByAttributeValue("name", "contextData").val();
			//login to form
			System.out.println("sending login form now");
			resp = Jsoup.connect(GRADES_URL)
					.data("pstoken",pstoken)
					.data("contextData",contextData)
					.data("serviceName",serviceName)
					.data("pcasServerUrl",pcasServerUrl)
					.data("credentialType",credentialType)
					.data("account",username)
					.data("ldappassword",password)
					.data("pw",sStringToHMACMD5(contextData, Base64.encodeBytes(MD5("password").getBytes())))
					.cookies(resp.cookies())
					.userAgent("Mozilla")
					.method(Connection.Method.POST)
					.execute();
			System.out.println("calling parseAndCalcGPA()");
			parsePSPage(resp.parse());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An error has occured.\nYou might need to sign in to the BCA wifi network.");
			e.printStackTrace();
		}
	}

	static void parsePSPage(Document gradespage){
		System.out.println("parsing GPA now...");

		if(gradespage.select("div.feedback-alert").hasText()) {
			JOptionPane.showMessageDialog(null, "Wrong password. Please try again");
			System.exit(0);
		}

		final ArrayList<String> studentClasses = new ArrayList<String>();
		final ArrayList<Integer> studentClassMods = new ArrayList<Integer>();
		final ArrayList<float[]> studentClassGPAs = new ArrayList<float[]>();

		//won't happen because we catch it in getGrades()
		//TODO: check for wrong password 

		//grades table
		Elements rows = gradespage.select(
				"html > body > div#container > div#content > div#content-main " +
				"> div#quickLookup > table.grid:first-child > tbody > tr.center");
		for(int i=2;i<rows.size()-1;i++){	//for each class, ignoring unrelated rows in the table
			Element curClass = rows.get(i);	//current class element
			String rawClassName = curClass.children().get(11).text();
			if(rawClassName.charAt(0) == '~') continue; //classes starting with "~" are not counted in the GPA

			String curClassName = rawClassName.substring(0, rawClassName.indexOf((char)160));	//name
			int curClassMods = getModsFromString(curClass.children().get(0).text());			//mods
			float[] tempGpaArr = new float[4];
			tempGpaArr[0]=letterGradeToGPA(curClass.children().get(12).text()); //t1
			tempGpaArr[1]=letterGradeToGPA(curClass.children().get(13).text()); //t2
			tempGpaArr[2]=letterGradeToGPA(curClass.children().get(14).text()); //t3
			tempGpaArr[3]=letterGradeToGPA(curClass.children().get(15).text()); //year

			studentClasses.add(curClassName);
			studentClassMods.add(curClassMods);
			studentClassGPAs.add(tempGpaArr);

			//DEBUG
			System.out.println("\nclass: " + curClassName);
			System.out.println("mods: "  + curClassMods);
			for(float gpa:tempGpaArr) System.out.println(gpa);
		}

		final GradesAppProjElecChooser chooserGui = new GradesAppProjElecChooser(studentClasses.toArray(new String[] {}));
		chooserGui.setVisible(true);
		chooserGui.btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooserGui.setVisible(false);
				for(int i=0;i<chooserGui.table.getColumnCount();i++) {
					//2 mods/1 credit
					if((boolean)chooserGui.table.getValueAt(i, 1)) studentClassMods.set(i, 2);
					calcAndDisplayGpa(studentClasses, studentClassMods, studentClassGPAs);
				}
			}
		});

	}
	static void calcAndDisplayGpa(ArrayList<String> studentClasses, ArrayList<Integer> studentClassMods, ArrayList<float[]> studentClassGPAs) {
		/*
		 * GPA formula with BCA mods:
		 * gpa = sum(mods/2 * GPA)
		 * 		 -----------------
		 *          sum(mods/2)
		 */
		float t1gpa=0, t2gpa=0, t3gpa=0, ygpa=0;
		float numerator, denominator; //numerator and denominator of formula

		for(int tri=0;tri<4;tri++) {
			numerator=0;
			denominator=0;
			for(int i=0;i<studentClasses.size();i++) {
				if(studentClassGPAs.get(i)[tri] != -1){
					numerator += ((studentClassMods.get(i)/2) * studentClassGPAs.get(i)[tri]);
					denominator += (studentClassMods.get(i)/2); 
				}
			}
			if(tri==0) t1gpa = numerator/denominator;
			else if(tri==1) t2gpa = numerator/denominator;
			else if(tri==2) t3gpa = numerator/denominator;
			else if(tri==3) ygpa = numerator/denominator;
		}

		STUDENT_GPAS = new float[] {t1gpa, t2gpa, t3gpa, ygpa};
		if(STUDENT_GPAS == null) JOptionPane.showMessageDialog(null, "An error occurred. This shouldn't be happening...");
		else{
			GradesAppMainGUI displayGradesGui = new GradesAppMainGUI(STUDENT_GPAS);
			displayGradesGui.frame.setVisible(true);
		}
	}
	//parse the mods string
	static int getModsFromString(String input){
		int totalMods = 0;
		for(String range:input.split(" ")){
			String[] temp = {range.substring(0, range.indexOf('(')), range.substring(range.indexOf('(')+1,range.length()-1)};
			//temp[0] is mods, temp[1] is day of week
			String parseableDayOfWeek = temp[1]
					.replaceAll("M", "2")
					.replaceAll("T", "3")
					.replaceAll("W", "4")
					.replaceAll("R", "5")
					.replaceAll("F", "6");
			for(String s:parseableDayOfWeek.split(",")){
				totalMods += stringRangeToLen(temp[0])*stringRangeToLen(s);
			}
		}
		return totalMods;
	}
	static int stringRangeToLen(String range){		//form: a-b, where a<b OR a
		String from="",to="";
		int dashloc = range.indexOf('-');
		if(dashloc != -1){
			from=range.substring(0, range.indexOf('-'));
			to=range.substring(range.indexOf('-')+1);
		}
		int answer;
		if(dashloc == -1) answer= 1;	//no dash
		else answer= Integer.parseInt(to)-Integer.parseInt(from)+1;
		return answer;
	}
	static float letterGradeToGPA(String grade){
		if(grade.indexOf(' ') != -1) grade = grade.substring(0, grade.indexOf(' '));
		switch(grade){
		case "--":
			return (float)-1;
		case "A":
			return (float)4.0;
		case "A-":
			return (float)3.8;
		case "B+":
			return (float)3.33;
		case "B":
			return (float)3.0;
		case "B-":
			return (float)2.8;
		case "C+":
			return (float)2.33;
		case "C":
			return (float)2.0;
		case "C-":
			return (float)1.8;
		case "D+":
			return (float)1.33;
		case "D":
			return (float)1.0;
		case "F":
			return (float)0.0;
		default:
			return (float)-1;
		}
	}
	//just some necessary functions to get login form data
	//not made by me
	public static String sStringToHMACMD5(String s, String keyString)
	{
		String sEncodedString = null;
		try
		{
			SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(key);

			byte[] bytes = mac.doFinal(s.getBytes("ASCII"));

			StringBuffer hash = new StringBuffer();

			for (int i=0; i<bytes.length; i++) {
				String hex = Integer.toHexString(0xFF &  bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			sEncodedString = hash.toString();
		}
		catch (Exception e) {e.printStackTrace();}
		return sEncodedString ;
	}
	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
}