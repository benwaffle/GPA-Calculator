import java.awt.*;

import javax.swing.*;

public class GradesAppMainGUI {
	public JFrame frame;
	private JLabel[] labels = new JLabel[8];
	private JPanel[] tabs = new JPanel[4];

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GradesAppMainGUI window = new GradesAppMainGUI(new float[] {1,2,3,4}); //for testing
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GradesAppMainGUI(float[] gpas) {
		if(gpas.length != 4) {
			System.out.println("error in GradesAppMainGUI. gpas[] is not of length 4");
			System.exit(1);
		}
		initialize(gpas);
	}

	@SuppressWarnings("serial")
	private void initialize(float[] gpas) {
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

		for(int i=0;i<4;i++) {
			labels[i] = new JLabel("GPA:") {{
				setBounds(0, 0, 100, 25);
				setHorizontalAlignment(SwingConstants.CENTER);
				setFont(new Font("Lucida Grande", Font.PLAIN, 20));
			}};
			labels[i+4] = new JLabel(Float.toString(gpas[i])) {{
				setBounds(6, 6, 505, 250);
				setFont(new Font("Lucida Grande", Font.PLAIN, 99));
				setHorizontalAlignment(SwingConstants.CENTER);
			}};
		}
		
		for(int i=0;i<4;i++) {
			tabs[i] = new JPanel() {{
				setBorder(null);
				setLayout(null);
			}};
			tabs[i].add(labels[i]);
			tabs[i].add(labels[i+4]);
		}

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP) {{
			addTab("Tri 1", null, tabs[0], null); 
			addTab("Tri 2", null, tabs[1], null); 
			addTab("Tri 3", null, tabs[2], null); 
			addTab("Total", null, tabs[3], null); 
			setBounds(0, 0, 517, 291);
		}};
		frame.getContentPane().add(tabbedPane);
	}
}
