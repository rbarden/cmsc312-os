import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OperatingSystemRunner extends JFrame {

	public static ArrayList<Process> finishedQueue = new ArrayList<Process>();
	public static final int timeQuantum = 25;

	public static GUIPanel pan;
	public static Memory memory;
	public static Clock clock;
	public static SchedulerInterface scheduler;
	public static CPU cpu;

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		clock = new Clock();
		pan = new GUIPanel();
		memory = new Memory();
		scheduler = new RoundRobin(timeQuantum, clock);
		cpu = new CPU();

		new OperatingSystemRunner(pan);
		int executionSpeedSliderVal;

		/*
		 * This is the beginning of the main loop. It begins by getting the
		 * value of the slider which determines execution speed. It updates the
		 * GUIPanel clock label, and checks to see if the reset button was
		 * clicked. It then checks the command line input, and manages that.
		 */
		try {
			while (true) {
				executionSpeedSliderVal = pan.getSliderValue();
				if (pan.getIsSteadyRun()) {
					/*
					 * This branch is dealing with steady execution
					 */
					execute();
				} else if (pan.getNumOfSteps() > 0) {
					execute();
					pan.setNumOfSteps(pan.getNumOfSteps() - 1);
				}
				Thread.sleep(1000 / executionSpeedSliderVal);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * This method is initializing this class's JFrame settings
	 */
	public OperatingSystemRunner(JPanel p) {
		add(p);
		setSize(800, 520);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	/*
	 * This method represents the loop of the operating system.
	 */
	public static void execute() throws InterruptedException {

		cpu.setContinueCurrentExecution(true);

		clock.incrementClock();
		pan.setClockLbl(String.format("%06d", clock.getClock()));

		/*
		 * Check the reset boolean, if switched, reset the clock.
		 */
		if (pan.getReset()) {
			pan.setClockLbl("000000");
			pan.setIsSteadyRun(false);
			clock.setClock(0);
			scheduler = new RoundRobin(timeQuantum, clock);
			pan.setReset(false);

		}

		/*
		 * Check the command line
		 */
		if (!pan.getInputString().isEmpty()) {
			pan.setInputString(commandLineInterpreter(pan.getInputString()));
		}

		/*
		 * Check for new processes, if the exist, add them and schedule them.
		 * Set the memory for them.
		 */
		if (!pan.getNewProcesses().isEmpty()) {
			for (Process p : pan.getNewProcesses()) {
				p.setProcessState(State.NEW);
				scheduler.addNewProcess(p);
				scheduler.schedule(p);
				memory.setMemoryUsed(scheduler.getMemoryUsed());
			}
			pan.emptyNewProcessArray();
		}

		updatePanelTables();

		/*
		 * This begins the main 'cpu loop'; While the time quantum has not
		 * expired, and cpu.isContinueCurrentExecution() (This is a boolean
		 * value that is switched on the condition that the calculate time
		 * expire, or any other interrupt occurs) is true: The CPU is run on the
		 * process. Time quantum is decremented, and the clock incremented.
		 * 
		 * The thread is allowed to sleep for each loop for a seam-less
		 * execution, although I question if this is necessary.
		 */
		int currentTimeQuantum = timeQuantum;
		if (!scheduler.getReadyQueue().isEmpty()) {
			Process p = scheduler.getReadyProcess();
			pan.getLblCurrentProcessName().setText(p.getName());
			while (currentTimeQuantum > 0 && cpu.isContinueCurrentExecution()) {
				cpu.run(p);
				currentTimeQuantum--;
				clock.incrementClock();
				pan.setClockLbl(String.format("%06d", clock.getClock()));
				pan.getOperationLabel().setText(cpu.getProcessOperation());
				Thread.sleep(1000 / pan.getSliderValue());
			}
			if (!cpu.getOutput().isEmpty()) {
				pan.setConsole(
						"Output: " + cpu.getOutput() + " on clock cycle " + clock.getClock());
			}
			pan.getLblCurrentProcessName().setText("");
			pan.getOperationLabel().setText("");
			scheduler.schedule(p);

		}

		/*
		 * If any process was selected for removal, it is returned by the
		 * updateScheduler method and added to the finishedQueue which is used
		 * to populate the finished table.
		 */
		try {
			Process term = scheduler.updateScheduler();
			if (!term.equals(null)) {
				finishedQueue.add(term);
			}
		} catch (NullPointerException n) {
			// System.out.println("NPE for teminated processes.");
		}

		/*
		 * Updating the memory and setting the memory labels in the JPanel
		 */
		memory.setMemoryUsed(scheduler.getMemoryUsed());
		pan.setMemoryUsedLbl("Memory Used: " + String.format("%4d", memory.getMemoryUsed()) + "/4096");
		pan.setLblMemoryAvailable("Memory Available: " + String.format("%4d", memory.getAvailableMemory()));

		updatePanelTables();
	}

	/*
	 * 
	 * 
	 */
	public static void updatePanelTables() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pan.updateReadyQueueTable(scheduler.getReadyQueue());
				pan.updateWaitingQueueTable(scheduler.getWaitingQueue());
				pan.updateFinishedQueueTable(finishedQueue);
			}
		});
	}

	/*
	 * This method is getting the input from the JPanel command line input, and
	 * using the string in the main loop.
	 */
	public static String commandLineInterpreter(String s) {
		String rtnString = "";
		if (s.contains(",")) {
			String[] splitStr = s.split(",");
			System.out.println(splitStr[1]);
			rtnString = splitStr[1].trim();
			try {
				Process p = FileReader.createProcess(rtnString);
				p.setProcessState(State.NEW);
				scheduler.addNewProcess(p);
				scheduler.schedule(p);
			} catch (FileNotFoundException f) {
				JOptionPane.showMessageDialog(null, "That file does not exist");
			}
		} else {
			switch (s) {
			case "proc":
				proc();
				break;
			case "mem":
				String memString = "The amount of memory used: " + memory.getMemoryUsed()
						+ "\nThe amount of memory available: " + memory.getAvailableMemory();
				JOptionPane.showMessageDialog(null, memString);
				break;
			case "reset":
				pan.setClockLbl("000000");
				pan.setIsSteadyRun(false);
				clock.setClock(0);
				scheduler = new RoundRobin(timeQuantum, clock);
				break;
			case "exit":
				System.exit(0);
				break;
			case "exe":
				System.out.println("exe");
				break;
			}
		}
		return rtnString;
	}

	/*
	 * This method opens a JOptionPan showing all of the processes that have
	 * entered the system but have not yet terminated.
	 */
	public static void proc() {
		String tot = "";
		for (Process p : scheduler.getReadyQueue()) {
			tot += p.toString() + "\n";
		}
		for (Process p : scheduler.getWaitingQueue()) {
			tot += p.toString() + "\n";
		}
		for (Process p : scheduler.getNewQueue()) {
			tot += p.toString() + "\n";
		}
		JOptionPane.showMessageDialog(null, tot);
	}

}
