import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.*;

public class GradesAppLoginGUI {
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JLabel status;
	public JFrame frame;
	public JTextField unameField;
	public JPasswordField pwField;
	public JButton loginBtn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					GradesAppLoginGUI window = new GradesAppLoginGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GradesAppLoginGUI() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		Rectangle scr = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(scr.width/2 - 270/2, scr.height/2 - 174/2, 270, 174);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblLoginToPowerschool = new JLabel("Login to PowerSchool");
		lblLoginToPowerschool.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginToPowerschool.setBounds(6, 6, 242, 16);
		frame.getContentPane().add(lblLoginToPowerschool);
		
		unameField = new JTextField();
		unameField.setBounds(100, 34, 148, 28);
		frame.getContentPane().add(unameField);
		unameField.setColumns(10);
		
		lblUsername = new JLabel("Username:");
		lblUsername.setBounds(16, 40, 72, 16);
		frame.getContentPane().add(lblUsername);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(16, 80, 72, 16);
		frame.getContentPane().add(lblPassword);
		
		loginBtn = new JButton("Login");
		loginBtn.setBounds(151, 105, 97, 29);
		frame.getRootPane().setDefaultButton(loginBtn);
		frame.getContentPane().add(loginBtn);
		
		pwField = new JPasswordField();
		pwField.setBounds(100, 74, 148, 28);
		frame.getContentPane().add(pwField);
		
		status = new JLabel("");
		status.setBounds(0, 135, 264, 28);
		frame.getContentPane().add(status);
		
		frame.setVisible(true);
	}
}
