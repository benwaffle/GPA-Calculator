import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.*;

public class GradesAppMainGUI {

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GradesAppMainGUI window = new GradesAppMainGUI(new float[] {0,0,0,0});
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GradesAppMainGUI(float[] gpas) {
		if(gpas.length != 4) {
			System.err.println("error in GradesAppMainGUI. gpas[] is not of length 4");
			System.exit(1);
		}
		initialize(gpas[0],gpas[1],gpas[2],gpas[3]);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(float tri1gpa, float tri2gpa, float tri3gpa, float yeargpa) {
//		try {
//		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		        if ("Nimbus".equals(info.getName())) {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            break;
//		        }
//		    }
//		} catch (Exception e) { e.printStackTrace(); }
		
		frame = new JFrame();
		frame.setBounds(100, 100, 517, 313);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 517, 291);
		tabbedPane.setBackground(UIManager.getColor("TabbedPane.background"));
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("TabbedPane.background"));
		panel.setBorder(null);
		tabbedPane.addTab("Tri 1", null, panel, null);
		panel.setLayout(null);
		
		JLabel tab1gpalbl = new JLabel("GPA:");
		tab1gpalbl.setBounds(236, 6, 45, 25);
		panel.add(tab1gpalbl);
		tab1gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab1gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		
		JLabel tab1gpaval = new JLabel(""+tri1gpa);
		tab1gpaval.setBounds(6, 6, 505, 250);
		panel.add(tab1gpaval);
		tab1gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab1gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(UIManager.getColor("TabbedPane.background"));
		panel_1.setBorder(null);
		tabbedPane.addTab("Tri 2", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel tab2gpalbl = new JLabel("GPA:");
		tab2gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab2gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		tab2gpalbl.setBounds(236, 6, 45, 25);
		panel_1.add(tab2gpalbl);
		
		JLabel tab2gpaval = new JLabel(""+tri2gpa);
		tab2gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab2gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab2gpaval.setBounds(6, 6, 505, 250);
		panel_1.add(tab2gpaval);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(UIManager.getColor("TabbedPane.background"));
		panel_2.setBorder(null);
		tabbedPane.addTab("Tri 3", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel tab3gpalbl = new JLabel("GPA:");
		tab3gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab3gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		tab3gpalbl.setBounds(236, 6, 45, 25);
		panel_2.add(tab3gpalbl);
		
		JLabel tab3gpaval = new JLabel(""+tri3gpa);
		tab3gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab3gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab3gpaval.setBounds(6, 6, 505, 250);
		panel_2.add(tab3gpaval);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(UIManager.getColor("TabbedPane.background"));
		panel_3.setBorder(null);
		tabbedPane.addTab("Total", null, panel_3, null);
		panel_3.setLayout(null);
		
		JLabel tab4gpalbl = new JLabel("GPA:");
		tab4gpalbl.setBounds(236, 6, 45, 25);
		panel_3.add(tab4gpalbl);
		tab4gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab4gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		
		JLabel tab4gpaval = new JLabel(""+yeargpa);
		tab4gpaval.setBounds(6, 6, 505, 250);
		panel_3.add(tab4gpaval);
		tab4gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab4gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
	}
}
