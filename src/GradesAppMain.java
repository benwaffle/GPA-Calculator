/*
 * GPA Calculator v0.4
 * Copyright (c) 2013 Ben Iofel
 */

import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class GradesAppMain {
	//login data
	static final String HOME_URL = "https://ps01.bergen.org/public/home.html", GRADES_URL = "https://ps01.bergen.org/guardian/home.html";
	static final String serviceName="PS+Parent+Portal", credentialType="User+Id+and+Password+Credential", pcasServerUrl="/";
	static String pstoken, contextData;
	static final boolean DEBUG = true;
	public static float[] STUDENT_GPAS;

	public static void main(String[] args) throws Exception {
		if(DEBUG) System.out.println("Program running. Creating login GUI");
		final GradesAppLoginGUI logingui = new GradesAppLoginGUI();
		if(DEBUG) System.out.println("created login GUI");
		logingui.loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(DEBUG) System.out.println("login button clicked");
				logingui.frame.setVisible(false);
				parsePSPage(loginToPS(
						logingui.unameField.getText(), 
						new String(logingui.pwField.getPassword())
						));
			}
		});
		logingui.frame.setVisible(true);
		if(DEBUG) System.out.println("displayed login GUI");
	}

	static Document loginToPS(String username, String password){
		if(DEBUG) System.out.println("loginToPS() called");
		try {
			//get login page
			if(DEBUG) System.out.println("getting login page");
			Connection.Response resp = Jsoup.connect(HOME_URL).method(Connection.Method.GET).execute();
			if(DEBUG) System.out.println("got login page");
			//get some hidden login values
			pstoken = resp.parse().body().getElementsByAttributeValue("name", "pstoken").val();
			contextData = resp.parse().body().getElementsByAttributeValue("name", "contextData").val();
			//login to form
			if(DEBUG) System.out.println("sending login form now");
			resp = Jsoup.connect(GRADES_URL)
					.data("pstoken",pstoken)
					.data("contextData",contextData)
					.data("serviceName",serviceName)
					.data("pcasServerUrl",pcasServerUrl)
					.data("credentialType",credentialType)
					.data("account",username)
					.data("ldappassword",password)
					.data("pw",Base64.sStringToHMACMD5(contextData, Base64.encodeBytes(Base64.MD5("password").getBytes())))
					.cookies(resp.cookies())
					.userAgent("Mozilla")
					.method(Connection.Method.POST)
					.execute();
			if(DEBUG) System.out.println("got grades page, returning it");
			return resp.parse();
		} catch (Exception e) {
			if(DEBUG) System.out.println("error connecting to powerschool");
			JOptionPane.showMessageDialog(null, "An error has occured connecting to Powerschool.\nYou might need to sign in to the BCA wifi network.");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	static void parsePSPage(Document gradespage){
		if(DEBUG) System.out.println("parsePSPage called");

		if(gradespage.select("div.feedback-alert").hasText()) {
			JOptionPane.showMessageDialog(null, "Wrong password. Please try again");
			System.exit(0);
		}

		final ArrayList<String> studentClasses = new ArrayList<String>();
		final ArrayList<Integer> studentClassMods = new ArrayList<Integer>();
		final ArrayList<float[]> studentClassGPAs = new ArrayList<float[]>();

		if(DEBUG) System.out.println("getting grades");
		//grades table
		Elements rows = gradespage.select(
				"html > body > div#container > div#content > div#content-main " +
				"> div#quickLookup > table.grid:first-child > tbody > tr.center");
		if(DEBUG) System.out.println("looping through classes");
		for(int i=2;i<rows.size()-1;i++){	//for each class, ignoring unrelated rows in the table
			Element curClass = rows.get(i);	//current class element
			String rawClassName = curClass.children().get(11).text();
			if(rawClassName.charAt(0) == '~') continue; //classes starting with "~" are not counted in the GPA

			String curClassName = rawClassName.substring(0, rawClassName.indexOf((char)160));	//name
			int curClassMods = getModsFromString(curClass.children().get(0).text());			//mods
			float[] tempGpaArr = new float[4];
			tempGpaArr[0]=gradeToGPA(curClass.children().get(12).text(),-1); //t1
			tempGpaArr[1]=gradeToGPA(curClass.children().get(13).text(),-1); //t2
			tempGpaArr[2]=gradeToGPA(curClass.children().get(14).text(),-1); //t3

			float yearPcntAvg = 0;
			int trisWithGrades = 0;

			if(gradeToGPA(curClass.children().get(15).text(), -1) != -1) tempGpaArr[3] = gradeToGPA(curClass.children().get(15).text(), -1);
			else {
				for(int tri=1;tri<=3;tri++) if(gradeToGPA(curClass.children().get(11+tri).text(), -1) != -1) {
					yearPcntAvg += Float.parseFloat(curClass.children().get(11+tri).text().split(" ")[1]);
					trisWithGrades++;
				}
				tempGpaArr[3]=gradeToGPA(null, yearPcntAvg/trisWithGrades); //year	
			}

			studentClasses.add(curClassName);
			studentClassMods.add(curClassMods);
			studentClassGPAs.add(tempGpaArr);

			//DEBUG
			//System.out.println("\nclass: " + curClassName);
			//System.out.println("mods: "  + curClassMods);
			//for(float gpa:tempGpaArr) System.out.println(gpa);
		}

		if(DEBUG) System.out.println("creating chooserGUI");
		final GradesAppProjElecChooser chooserGui = new GradesAppProjElecChooser(studentClasses.toArray(new String[] {}));
		chooserGui.setVisible(true);
		chooserGui.btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(DEBUG) System.out.println("chooserGUI done button clicked");
				chooserGui.setVisible(false);
				for(int i=0;i<chooserGui.table.getRowCount();i++) {
					//2 mods = 1 credit
					if((boolean)chooserGui.table.getValueAt(i, 1)) studentClassMods.set(i, 2);
				}
				calcAndDisplayGpa(studentClasses, studentClassMods, studentClassGPAs);
			}
		});
	}
	static void calcAndDisplayGpa(ArrayList<String> studentClasses, ArrayList<Integer> studentClassMods, ArrayList<float[]> studentClassGPAs) {
		if(DEBUG) System.out.println("calcAndDisplayGpa() called");

		/*
		 * GPA formula with BCA mods:
		 * gpa = sum(mods/2 * GPA)
		 * 		 -----------------
		 *          sum(mods/2)
		 */
		float t1gpa=0, t2gpa=0, t3gpa=0, ygpa=0;
		float numerator, denominator; //numerator and denominator of formula

		if(DEBUG) System.out.println("calculating trimester GPAs");
		//trimesters
		for(int tri=0;tri<3;tri++) {
			numerator=0;
			denominator=0;
			for(int classIndex=0;classIndex<studentClasses.size();classIndex++) { //for each class
				if(studentClassGPAs.get(classIndex)[tri] != -1){	//if the GPA is not -1 ("--" or " ")
					numerator += ((studentClassMods.get(classIndex)/2) * studentClassGPAs.get(classIndex)[tri]);
					denominator += (studentClassMods.get(classIndex)/2); 
				}
			}
			if(tri==0) t1gpa = numerator/denominator;
			else if(tri==1) t2gpa = numerator/denominator;
			else if(tri==2) t3gpa = numerator/denominator;
		}

		if(DEBUG) System.out.println("calculating year gpa");
		//year
		numerator = 0; denominator = 0;
		for(int classIndex=0;classIndex<studentClasses.size();classIndex++) for(int tri=0;tri<3;tri++) {
			if(studentClassGPAs.get(classIndex)[tri] != -1) {
				numerator += studentClassMods.get(classIndex)/2 * studentClassGPAs.get(classIndex)[3];
				denominator += studentClassMods.get(classIndex)/2;
			}
		}
		ygpa = numerator/denominator;

		STUDENT_GPAS = new float[] {t1gpa, t2gpa, t3gpa, ygpa};

		if(DEBUG) System.out.println("creating GPA display GUI");
		GradesAppMainGUI displayGradesGui = new GradesAppMainGUI(STUDENT_GPAS);
		if(DEBUG) System.out.println("created GUI, setting visible");
		displayGradesGui.frame.setVisible(true);

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
	static int stringRangeToLen(String range){		//form: 'a-b' or 'a'
		String from="",to="";
		int dashloc = range.indexOf('-'), answer;
		if(dashloc != -1){
			from=range.substring(0, range.indexOf('-'));
			to=range.substring(range.indexOf('-')+1);
			answer= Integer.parseInt(to)-Integer.parseInt(from)+1;
		} else answer = 1;
		return answer;
	}
	static float gradeToGPA(String letterGrade, float numGrade){
		if(letterGrade == null && numGrade != -1) { 	//number -> gpa
			if(numGrade >= 92.5) return (float)4.0;	//A
			else if(numGrade >= 90) return (float)3.8; //A-
			else if(numGrade >= 87.5) return (float)3.33; //B+
			else if(numGrade >= 82.5) return (float)3.0; //B
			else if(numGrade >= 80) return (float)2.8; //B-
			else if(numGrade >= 77.5) return (float)2.33; //C+
			else if(numGrade >= 72.5) return (float)2.0; //C
			else if(numGrade >= 70) return (float)1.8; //C-
			else if(numGrade >= 67.5) return (float)1.33; //D+
			else if(numGrade >= 60) return (float)1.0; //D
			else return (float)0.0; //F
		} else if(letterGrade != null && numGrade == -1) { //letter -> gpa
			if(letterGrade.indexOf(' ') != -1) letterGrade = letterGrade.substring(0, letterGrade.indexOf(' '));
			if(letterGrade.equals("A")) return (float)4.0;
			else if(letterGrade.equals("A-")) return (float)3.8;
			else if(letterGrade.equals("B+")) return (float)3.33;
			else if(letterGrade.equals("B")) return (float)3.0;
			else if(letterGrade.equals("B-")) return (float)2.8;
			else if(letterGrade.equals("C+")) return (float)2.33;
			else if(letterGrade.equals("C")) return (float)2.0;
			else if(letterGrade.equals("C-")) return (float)1.8;
			else if(letterGrade.equals("D+")) return (float)1.33;
			else if(letterGrade.equals("D")) return (float)1.0;
			else if(letterGrade.equals("F")) return (float)0.0;
			else return (float)-1;
		} else {
			System.out.println("improper usage of gradeToGPA() function");
			return (float)-1;
		}
	}
}