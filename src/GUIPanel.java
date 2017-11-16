import hardware.Clock;
import process.Process;
import scheduling.FCFS;
import scheduling.RoundRobin;
import scheduling.Scheduler;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSlider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JComboBox;

public class GUIPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Text field for command line.
	 */
	private JTextField commandLineTextField;
	private JTextField stepTextField;
	private JTextField numberOfRanFileTF;

	/*
	 * JButtons named appropriately.
	 */
	private JButton btnCommandLineEnter;
	private JButton btnReset;
	private JButton btnGenerateRandomProcess;
	private JButton btnStep;
	private JButton btnStart;
	private JButton btnStop;

	/*
	 * JSlider to adjust the clock speed.
	 */
	private JSlider slider;

	/*
	 * JLabels: Named Appropriately.
	 */
	private JLabel lblMemoryAvailable;
	private JLabel consoleLbl;
	private JLabel lblInputQueue;
	private JLabel lblWaitingQueue;
	private JLabel lblFinishedProcesses;
	private JLabel lblCurrentClockTime;
	private JLabel clockLabel;
	private JLabel lblExecutionSpeed;
	private JLabel lblMemoryUsed;
	private JLabel lblNumber;
	private JLabel lblCurrentCpuProcess;
	private JLabel lblCurrentProcessName;
	private JLabel operationLabel;

	public JLabel getLblCurrentProcessName() {
		return lblCurrentProcessName;
	}

	public void setLblCurrentProcessName(JLabel lblCurrentProcessName) {
		this.lblCurrentProcessName = lblCurrentProcessName;
	}

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

	/*
	 * If this is true, the clock should be reset to zero.
	 */
	private boolean reset = false;
	
	/*
	 * The clock value
	 */
	
	private Clock clock;
	/*
	 * The string from the command line
	 */
	private String inputString = "";

	/*
	 * Boolean and int for controlling the stepping through or steady run of the
	 * program.
	 */
	private boolean isSteadyRun = false;
	private int numOfSteps = -1;

	/*
	 * ArrayList of new Processes; should be emptied every cycle.
	 */

	private ArrayList<Process> newProcesses = new ArrayList<>();
	
	/*
	 * The drop down menu to select a scheduler from.
	 */
	private JComboBox<String> schedulerSelecterCB;
	
	/*
	 * The scheduler chosen from the drop down menu
	 */
	public Scheduler schedulerIF = null;
	

	/*
	 * The GUIPanel Constructor
	 */
	public GUIPanel(Clock c) {
		setLayout(null);
		setup();
		setVisible(true);
		clock = c;
	}

	/*
	 * The setup method. This method instantiates all of the components.
	 */
	public void setup() {
		this.setSize(800, 500);
		/*
		 * Text Field instantiations; Command Line and # of Steps.
		 */
		commandLineTextField = new JTextField();
		commandLineTextField.setBounds(20, 400, 543, 26);
		add(commandLineTextField);

		stepTextField = new JTextField();
		stepTextField.setText("0000");
		stepTextField.setBounds(459, 432, 46, 26);
		stepTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		add(stepTextField);
		stepTextField.setColumns(10);

		numberOfRanFileTF = new JTextField();
		numberOfRanFileTF.setBounds(649, 460, 141, 26);
		numberOfRanFileTF.setHorizontalAlignment(JTextField.CENTER);
		add(numberOfRanFileTF);
		numberOfRanFileTF.setColumns(10);

		/*
		 * JButtons
		 */
		btnCommandLineEnter = new JButton("Enter");
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

		btnStart = new JButton("Start");
		btnStart.setBounds(282, 338, 128, 29);
		btnStart.setEnabled(false);
		add(btnStart);

		btnStop = new JButton("Stop");
		btnStop.setBounds(402, 338, 128, 29);
		add(btnStop);

		/*
		 * JSlider for execution speed
		 */
		slider = new JSlider(JSlider.HORIZONTAL, 5, 101, 5);
		slider.addChangeListener(new ChangeListener() {

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
		slider.setBounds(142, 432, 290, 50);
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

		lblMemoryAvailable = new JLabel("Memory Available: 4096");
		lblMemoryAvailable.setBounds(613, 16, 151, 16);
		add(lblMemoryAvailable);

		lblInputQueue = new JLabel("Ready Queue:");
		lblInputQueue.setBounds(20, 44, 100, 16);
		add(lblInputQueue);

		lblWaitingQueue = new JLabel("Waiting Queue:");
		lblWaitingQueue.setBounds(285, 44, 95, 16);
		add(lblWaitingQueue);

		lblFinishedProcesses = new JLabel("Finished Processes:");
		lblFinishedProcesses.setBounds(542, 44, 128, 16);
		add(lblFinishedProcesses);

		lblCurrentClockTime = new JLabel("Current Clock Time:");
		lblCurrentClockTime.setBounds(20, 343, 128, 16);
		add(lblCurrentClockTime);

		consoleLbl = new JLabel("Output: ");
		consoleLbl.setBounds(23, 372, 774, 16);
		add(consoleLbl);

		clockLabel = new JLabel("000000");
		clockLabel.setBounds(160, 344, 61, 16);
		add(clockLabel);
		
		lblNumber = new JLabel("Number:");
		lblNumber.setBounds(576, 465, 61, 16);
		add(lblNumber);
		
		lblCurrentCpuProcess = new JLabel("CPU Process:");
		lblCurrentCpuProcess.setBounds(542, 343, 141, 16);
		add(lblCurrentCpuProcess);
		
		lblCurrentProcessName = new JLabel("");
		lblCurrentProcessName.setBounds(631, 343, 159, 16);
		add(lblCurrentProcessName);
		
		operationLabel = new JLabel("");
		operationLabel.setBounds(631, 365, 123, 16);
		add(operationLabel);
		
		/*
		 * Values for the drop down combo box for the scheduler. 
		 */
		String[] dropDownSchedulers = {"Select a scheduler: ", "Round Robin", "FCFS"};
		
		/*
		 * Instantiating the drop down combo box for the schedulers
		 */
		schedulerSelecterCB = new JComboBox<String>();
		schedulerSelecterCB.addItem(dropDownSchedulers[0]);
		schedulerSelecterCB.addItem(dropDownSchedulers[1]);
		schedulerSelecterCB.addItem(dropDownSchedulers[2]);
		schedulerSelecterCB.setBounds(296, 12, 209, 27);
		add(schedulerSelecterCB);

		/*
		 * The action listener for the drop down scheduler menu.
		 */
		schedulerSelecterCB.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String sched = schedulerSelecterCB.getSelectedItem().toString();
				if (sched.equals("Round Robin")){
					schedulerIF = new RoundRobin(25, clock);
					btnStart.setEnabled(true);
				}
				else if (sched.equals("FCFS")){
					schedulerIF = new FCFS(clock);
					btnStart.setEnabled(true);
				}
				
			}
			
		});
		

		
		/*
		 * Table Headers
		 */
		String[] tableColumns = { "Process: ", "State", "Arrival: " };

		/*
		 * Mock 2D Arrays for JTable rows/columns
		 */
		String[][] inputData = new String[50][3];
		String[][] waitingData = new String[50][3];
		String[][] finishedData = new String[50][3];
		for (int i = 0; i < 50; i++) {

			inputData[i][0] = "-";
			inputData[i][1] = "-";
			inputData[i][2] = "-";
			waitingData[i][0] = "-";
			waitingData[i][1] = "-";
			waitingData[i][2] = "-";
			finishedData[i][0] = "-";
			finishedData[i][1] = "-";
			finishedData[i][2] = "-";

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
		readyQueueTable.setBounds(20, 69, 250, 290);
		readyQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		readyScrollPane = new JScrollPane(readyQueueTable);
		readyScrollPane.setBounds(20, 69, 250, 262);
		add(readyScrollPane);

		waitingQueueTable = new JTable();
		waitingQueueTable.setModel(dTMWaitingQueue);
		waitingQueueTable.setBounds(205, 69, 248, 290);
		waitingQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		waitingScrollPane = new JScrollPane(waitingQueueTable);
		waitingScrollPane.setBounds(282, 69, 248, 262);
		add(waitingScrollPane);

		finishedQueueTable = new JTable();
		finishedQueueTable.setModel(dTMFinishedQueue);
		finishedQueueTable.setBounds(390, 69, 248, 290);
		finishedQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		finishedScrollPane = new JScrollPane(finishedQueueTable);
		finishedScrollPane.setBounds(542, 69, 248, 262);
		add(finishedScrollPane);
		
		

		

		/*
		 * Adding actionListeners to the buttons
		 */

		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isSteadyRun = false;
				setReset(true);
				clockLabel.setText("000000");

			}

		});

		/*
		 * This is the ActionListener for the command line enter button. It
		 * checks to make sure that the input is not null and is of one of the
		 * correct forms.
		 */
		btnCommandLineEnter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str[] = commandLineTextField.getText().split(" ");

				if (!(str[0].toLowerCase().trim().equals("load")) && !(str[0].toLowerCase().trim().equals("mem"))
						&& !(str[0].toLowerCase().trim().equals("exit"))
						&& !(str[0].toLowerCase().trim().equals("proc"))
						&& !(str[0].toLowerCase().trim().equals("reset"))
						&& !(str[0].toLowerCase().trim().equals("exe"))) {
					JOptionPane.showMessageDialog(null, "There is no command " + str[0]);

				} else if (!str[0].trim().isEmpty()) {
					if (str.length > 1) {
						if (!str[1].trim().isEmpty()) {
							System.out.println("Test");
							setInputString(str[0].trim() + "," + str[1].trim());
						}
					} else {
						setInputString(str[0]);
					}

				} else {
					JOptionPane.showMessageDialog(null, "There is no input in the command line.");
				}

				commandLineTextField.setText("");
			}
		});

		/*
		 * This is the actionListener for the step button. It is meant to set
		 * the number of steps to execute. To step incrementally, enter 1. The
		 * method will ensure that the input is an integer.
		 */
		btnStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isSteadyRun == false) {
					String str = stepTextField.getText();
					try {
						int num = Integer.parseInt(str);
						setNumOfSteps(num);
						System.out.println(num);
					} catch (NumberFormatException im) {
						JOptionPane.showMessageDialog(null, "The input must be an integer.");
					}
				}
			}

		});

		btnGenerateRandomProcess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = numberOfRanFileTF.getText();
				if (!str.isEmpty()) {
					try {
						int num = Integer.parseInt(str);
						for (int i = 0; i < num; i++) {
							
							long currentTime = System.currentTimeMillis();
							Date date = new Date(currentTime); // if you really have long
							String result = new SimpleDateFormat("ssSSS").format(date.getTime());
							
							
							Process process = GenerateRandomProcess.generate("p" + result);
							newProcesses.add(process);
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InputMismatchException e1) {
						JOptionPane.showMessageDialog(null,
								"You must enter an integer for the number of random processes!");
					}
				} else {
					JOptionPane.showMessageDialog(null, "You must name the process!");
				}
				numberOfRanFileTF.setText("");

			}

		});

		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setIsSteadyRun(false);

			}
		});

		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setIsSteadyRun(true);

			}

		});

	}

	/*
	 * 
	 */
	public void updateQueueTable(ArrayList<Process> pArr, DefaultTableModel dtm, JTable table) {
		String[] headers = { "Process: ", "State", "Arrival: " };
		String[][] data = new String[50][3];
		for (int i = 0; i < 50; i++) {

			if (i < pArr.size()) {
				data[i][0] = pArr.get(i).getName();
				data[i][1] = pArr.get(i).getProcessState().toString();
				data[i][2] = String.valueOf(pArr.get(i).getArrivalTime());
			} else {
				data[i][0] = "-";
				data[i][1] = "-";
				data[i][2] = "-";
			}

		}
		dtm = new DefaultTableModel(data, headers);
		table.setModel(dtm);
	}
	
	/*
	 * Getters and setters for the GUI variables.
	 */

	public void setConsole(String s) {
		consoleLbl.setText(s);
	}

	public int getSliderValue() {
		return slider.getValue();
	}

	public String getLblMemoryAvailable() {
		return lblMemoryAvailable.getText();
	}

	public void setLblMemoryAvailable(String s) {
		lblMemoryAvailable.setText(s);
	}

	public String getConsoleLbl() {
		return consoleLbl.getText();
	}

	public void setConsoleLbl(String s) {
		consoleLbl.setText(s);
	}

	public String getClockLbl() {
		return clockLabel.getText();
	}

	public void setClockLbl(String s) {
		clockLabel.setText(s);
	}

	public String getMemoryUsedLbl() {
		return lblMemoryUsed.getText();
	}

	public void setMemoryUsedLbl(String s) {
		lblMemoryUsed.setText(s);
	}

	public boolean getReset() {
		return reset;
	}

	public void setReset(Boolean b) {
		reset = b;
	}

	public String getInputString() {
		return inputString;
	}

	public void setInputString(String s) {
		inputString = s;
	}

	public boolean getIsSteadyRun() {
		return isSteadyRun;
	}

	public void setIsSteadyRun(boolean b) {
		isSteadyRun = b;
	}

	public int getNumOfSteps() {
		return numOfSteps;
	}

	public void setNumOfSteps(int i) {
		numOfSteps = i;
	}

	public ArrayList<Process> getNewProcesses() {
		return newProcesses;
	}

	public void emptyNewProcessArray() {
		newProcesses.clear();
	}

	public JLabel getOperationLabel() {
		return operationLabel;
	}

	public void setOperationLabel(JLabel operationLabel) {
		this.operationLabel = operationLabel;
	}

	public Scheduler getSchedulerIF() {
		return schedulerIF;
	}

	public void setSchedulerIF(Scheduler schedulerIF) {
		this.schedulerIF = schedulerIF;
	}
	
	public Scheduler resetSchedulerIF(){
		Scheduler si = null;
		if (schedulerSelecterCB.getSelectedItem().toString().equals("Round Robin")){
			si = new RoundRobin(25,clock);
		}
		else if (schedulerSelecterCB.getSelectedItem().toString().equals("FCFS")){
			si = new FCFS(clock);
		}
		return si;
	}

	public JTable getReadyQueueTable() {
		return readyQueueTable;
	}

	public void setReadyQueueTable(JTable readyQueueTable) {
		this.readyQueueTable = readyQueueTable;
	}

	public JTable getWaitingQueueTable() {
		return waitingQueueTable;
	}

	public void setWaitingQueueTable(JTable waitingQueueTable) {
		this.waitingQueueTable = waitingQueueTable;
	}

	public JTable getFinishedQueueTable() {
		return finishedQueueTable;
	}

	public void setFinishedQueueTable(JTable finishedQueueTable) {
		this.finishedQueueTable = finishedQueueTable;
	}

	public DefaultTableModel getdTMInputQueue() {
		return dTMInputQueue;
	}

	public void setdTMInputQueue(DefaultTableModel dTMInputQueue) {
		this.dTMInputQueue = dTMInputQueue;
	}

	public DefaultTableModel getdTMWaitingQueue() {
		return dTMWaitingQueue;
	}

	public void setdTMWaitingQueue(DefaultTableModel dTMWaitingQueue) {
		this.dTMWaitingQueue = dTMWaitingQueue;
	}

	public DefaultTableModel getdTMFinishedQueue() {
		return dTMFinishedQueue;
	}

	public void setdTMFinishedQueue(DefaultTableModel dTMFinishedQueue) {
		this.dTMFinishedQueue = dTMFinishedQueue;
	}
	
	
	
}
