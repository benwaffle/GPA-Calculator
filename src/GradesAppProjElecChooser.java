import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class GradesAppProjElecChooser {
	private JFrame frame;
	public JTable table;
	private JScrollPane scrollPane;
	public JButton btnDone;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GradesAppProjElecChooser window = new GradesAppProjElecChooser(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GradesAppProjElecChooser(String[] classList) {
		//data
		if(classList == null) classList = new String[] {"class1","class2","class3"};
		int classSize = classList.length;
		Object[][] tableData = new Object[classSize][2];
		for(int i=0;i<classSize;i++){
			tableData[i][0] = classList[i];
			tableData[i][1] = false;
		}

		//gui
		Rectangle scr = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setBounds(scr.width/2-450/2, scr.height/2-300/2, 450, 300);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String[] columnNames = {"Class","Elective or Project"};

		table = new JTable();
		table.setModel(new DefaultTableModel(tableData,columnNames) {
			private static final long serialVersionUID = 4356299507444609132L;
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 1) return Boolean.class;
				return super.getColumnClass(columnIndex);
			}
			@Override
			public boolean isCellEditable(int row, int col) {
				if(col == 0) return false;
				return super.isCellEditable(row, col);
			}
		});
		table.getColumnModel().getColumn(1).setMinWidth(110);
		table.getColumnModel().getColumn(1).setMaxWidth(110);
		
		frame.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 35, 450, 212);
		table.setFillsViewportHeight(true);
		frame.getContentPane().add(scrollPane);
		
		JLabel lblPleaseSelectWhich = new JLabel("Please select which classes are projects or electives");
		lblPleaseSelectWhich.setBounds(6, 6, 438, 16);
		frame.getContentPane().add(lblPleaseSelectWhich);
		
		btnDone = new JButton("Done");
		btnDone.setBounds(380, 249, 70, 29);
		frame.getContentPane().add(btnDone);
	}
	
	public void setVisible(boolean option){
		frame.setVisible(option);
	}
}