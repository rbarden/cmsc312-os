import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSlider;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

public class GUIPanel extends JPanel{
	/*
	 * Text field for command line. 
	 */
	private JTextField commandLineTextField;
	
	/*
	 * JButtons named appropriately.
	 */
	private JButton btnCommandLineEnter;
	private JButton btnReset;
	
	
	/*
	 * JSlider to adjust the clock speed.
	 */
	private JSlider slider;
	
	/*
	 * JLabels: Named Appropriately.
	 */
	private JLabel lblNewLabel;
	private JLabel consoleLbl;
	private JLabel consoleLblMiddle;
	private JLabel consoleLblBottom;
	private JLabel lblInputQueue;
	private JLabel lblWaitingQueue;
	private JLabel lblFinishedProcesses;
	private JLabel lblCurrentClockTime;
	private JLabel clockLabel;
	private JLabel lblExecutionSpeed;
	private JLabel lblMemoryUsed;
	
	/*
	 * JTables to hold the queue contents.
	 */
	private JTable readyQueueTable;
	private JTable waitingQueueTable;
	private JTable finishedQueueTable;
	
	/*
	 * These are the models for the JTables.
	 */
	private DefaultTableModel dTMInputQueue;
	private DefaultTableModel dTMWaitingQueue;
	private DefaultTableModel dTMFinishedQueue;

	/*
	 * These scroll panes hold the JTables.
	 */
	private JScrollPane readyScrollPane;
	private JScrollPane waitingScrollPane;
	private JScrollPane finishedScrollPane;
	private JButton btnGenerateRandomProcess;
	private JTextField stepTextField;
	private JButton btnStep;
	
	
	
	
	
	/*
	 * The GUIPanel Constructor
	 */
	public GUIPanel() {
		setLayout(null);
		setup();
		setVisible(true);
	}
	
		/*
		 * The setup method. This method instantiaties all of the components.
		 */
	public void setup(){
		this.setSize(800,500);
		/*
		 * Text Field instantiations; Command Line and # of Steps.
		 */
		commandLineTextField = new JTextField();
		commandLineTextField.setBounds(20, 400, 543, 26);
		add(commandLineTextField);
		
		stepTextField = new JTextField();
		stepTextField.setText("0000");
		stepTextField.setBounds(444, 432, 61, 26);
		stepTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		add(stepTextField);
		stepTextField.setColumns(10);
		
		/*
		 * JButtons
		 */
		btnCommandLineEnter= new JButton("Enter");
		btnCommandLineEnter.setBounds(566, 400, 117, 29);
		add(btnCommandLineEnter);
		
		btnReset = new JButton("Reset");
		btnReset.setBounds(677, 400, 117, 29);
		add(btnReset);
		
		btnGenerateRandomProcess = new JButton("Generate Random Process");
		btnGenerateRandomProcess.setBounds(566, 432, 228, 29);
		add(btnGenerateRandomProcess);
		
		btnStep = new JButton("Step");
		btnStep.setBounds(501, 432, 62, 29);
		add(btnStep);
		/*
		 * JSlider for execution speed
		 */
		slider = new JSlider(JSlider.HORIZONTAL,5, 50, 5);
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
		        System.out.println(value);
				
			}
			
		});
		slider.setMajorTickSpacing(15);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setBounds(142, 432, 190, 50);
		add(slider);
		
		/*
		 * JLabels
		 */
		lblExecutionSpeed = new JLabel("Execution Speed:");
		lblExecutionSpeed.setBounds(30, 438, 128, 16);
		add(lblExecutionSpeed);
		
		
		lblMemoryUsed = new JLabel("Memory used: 0000/4096");
		lblMemoryUsed.setBounds(20, 16, 218, 16);
		add(lblMemoryUsed);
		
		lblNewLabel = new JLabel("Memory Available: 4096");
		lblNewLabel.setBounds(613, 16, 151, 16);
		add(lblNewLabel);
		
		lblInputQueue = new JLabel("Input Queue:");
		lblInputQueue.setBounds(20, 44, 81, 16);
		add(lblInputQueue);
		
		lblWaitingQueue = new JLabel("Waiting Queue:");
		lblWaitingQueue.setBounds(205, 44, 95, 16);
		add(lblWaitingQueue);
		
		lblFinishedProcesses = new JLabel("Finished Processes:");
		lblFinishedProcesses.setBounds(390, 44, 128, 16);
		add(lblFinishedProcesses);
		
		lblCurrentClockTime = new JLabel("Current Clock Time:");
		lblCurrentClockTime.setBounds(577, 70, 128, 16);
		add(lblCurrentClockTime);
		
		consoleLbl = new JLabel("Output: ");
		consoleLbl.setBounds(23, 372, 774, 16);
		add(consoleLbl);
		
		clockLabel = new JLabel("0000000");
		clockLabel.setBounds(644, 98, 61, 16);
		add(clockLabel);
		
		/*
		 * Table Headers
		 */
		String[] tableColumns = {"Process: ", "Status"};
		
		/*
		 * Mock 2D Arrays for JTable rows/columns
		 */
		String[][] inputData = new String[50][2];
		String[][] waitingData = new String[50][2];
		String[][] finishedData = new String[50][2];
		for (int i = 0; i < 50; i++){
			for (int j = 0; j < 2; j++){
				inputData[i][j] = "-";
				waitingData[i][j] = "-";
				finishedData[i][j] = "-";
				
			}
		}
		
		/*
		 * DefaultTableModel Instantiations 
		 */
		dTMInputQueue = new DefaultTableModel(inputData, tableColumns);
		dTMWaitingQueue = new DefaultTableModel(waitingData, tableColumns);
		dTMFinishedQueue = new DefaultTableModel(finishedData, tableColumns);

		/*
		 * Table setups
		 */
		readyQueueTable = new JTable();
		readyQueueTable.setModel(dTMInputQueue);
		readyQueueTable.setBounds(20, 69, 175, 290);
		readyQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		readyScrollPane = new JScrollPane(readyQueueTable);
		readyScrollPane.setBounds(20, 69, 175, 290);
		add(readyScrollPane);
		
		
		waitingQueueTable = new JTable();
		waitingQueueTable.setModel(dTMWaitingQueue);
		waitingQueueTable.setBounds(205, 69, 175, 290);
		waitingQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		waitingScrollPane = new JScrollPane(waitingQueueTable);
		waitingScrollPane.setBounds(205, 69, 175, 290);
		add(waitingScrollPane);
		
		finishedQueueTable = new JTable();
		finishedQueueTable.setModel(dTMFinishedQueue);
		finishedQueueTable.setBounds(390, 69, 175, 290);
		finishedQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		finishedScrollPane = new JScrollPane(finishedQueueTable);
		finishedScrollPane.setBounds(390, 69, 175, 290);
		add(finishedScrollPane);
		
	
		
		
		
		
		
		
		/*
		 * Adding actionListeners to the buttons
		 */
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnReset Clicked!");
				
			}
			
		});
		
		btnCommandLineEnter.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnCommandLineEnter Clicked!");
				
			}
			
		});
		
		btnStep.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnStep Clicked!");
				
			}
			
		});
		
		btnGenerateRandomProcess.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("btnGenerateRandomProcess Clicked!");
				
			}
			
		});
		
		
		
		
		
		
		
		
	}
}
