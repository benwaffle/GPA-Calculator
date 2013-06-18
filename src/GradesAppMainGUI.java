import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

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
			System.out.println("error in GradesAppMainGUI. gpas[] is not of length 4");
			System.exit(1);
		}
		initialize(gpas[0],gpas[1],gpas[2],gpas[3]);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(float tri1gpa, float tri2gpa, float tri3gpa, float yeargpa) {
		Rectangle scr = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setBounds(scr.width/2-517/2, scr.height/2-313/2, 517, 313);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 517, 291);
		frame.getContentPane().add(tabbedPane);
		
		JPanel tab1 = new JPanel();
		JPanel tab2 = new JPanel();
		JPanel tab3 = new JPanel();
		JPanel tab4 = new JPanel();
		
		JLabel tab1gpalbl = new JLabel("GPA:");
		JLabel tab2gpalbl = new JLabel("GPA:");
		JLabel tab3gpalbl = new JLabel("GPA:");
		JLabel tab4gpalbl = new JLabel("GPA:");
		
		JLabel tab1gpaval = new JLabel(""+tri1gpa);
		JLabel tab2gpaval = new JLabel(""+tri2gpa);
		JLabel tab3gpaval = new JLabel(""+tri3gpa);
		JLabel tab4gpaval = new JLabel(""+yeargpa);
		
		tab1.setBorder(null);
		tab2.setBorder(null);
		tab3.setBorder(null);
		tab4.setBorder(null);
		
		tab1.setLayout(null);
		tab2.setLayout(null);
		tab3.setLayout(null);
		tab4.setLayout(null);
		
		tabbedPane.addTab("Tri 1", null, tab1, null);
		tabbedPane.addTab("Tri 2", null, tab2, null);
		tabbedPane.addTab("Tri 3", null, tab3, null);
		tabbedPane.addTab("Total", null, tab4, null);
		
		tab1gpalbl.setBounds(236, 6, 80, 25);
		tab1.add(tab1gpalbl);
		tab1gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab1gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		
		tab1gpaval.setBounds(6, 6, 505, 250);
		tab1.add(tab1gpaval);
		tab1gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab1gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		
		tab2gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab2gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		tab2gpalbl.setBounds(236, 6, 80, 25);
		tab2.add(tab2gpalbl);
		
		tab2gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab2gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab2gpaval.setBounds(6, 6, 505, 250);
		tab2.add(tab2gpaval);
		
		tab3.setBackground(UIManager.getColor("TabbedPane.background"));
		
		tab3gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab3gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		tab3gpalbl.setBounds(236, 6, 80, 25);
		tab3.add(tab3gpalbl);
		
		tab3gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab3gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
		tab3gpaval.setBounds(6, 6, 505, 250);
		tab3.add(tab3gpaval);
		
		tab4gpalbl.setBounds(236, 6, 80, 25);
		tab4.add(tab4gpalbl);
		tab4gpalbl.setHorizontalAlignment(SwingConstants.CENTER);
		tab4gpalbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		
		tab4gpaval.setBounds(6, 6, 505, 250);
		tab4.add(tab4gpaval);
		tab4gpaval.setHorizontalAlignment(SwingConstants.CENTER);
		tab4gpaval.setFont(new Font("Lucida Grande", Font.PLAIN, 99));
	}
}
