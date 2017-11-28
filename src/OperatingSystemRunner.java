import hardware.CPU;
import hardware.Clock;
import memory.MMU;
import memory.MMUVirtual;
import memory.MemoryManager;
import process.Process;
import process.Semaphore;
import process.State;
import scheduling.Scheduler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OperatingSystemRunner extends JFrame {

	public static ArrayList<Process> finishedQueue = new ArrayList<Process>();
	public static int timeQuantum = 25;

	public static GUIPanel pan;
	public static Clock clock;
	public static Scheduler scheduler;
	public static CPU cpu;
	public static MemoryManager mmu;
	public static ArrayList<Semaphore> semaphoreList;

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		clock = new Clock();
		scheduler = null;
		semaphoreList = new ArrayList<Semaphore>();
		pan = new GUIPanel(clock, semaphoreList);

		for (int i = 0; i < 10; i++) {
			semaphoreList.add(new Semaphore());
		}

		cpu = new CPU(clock, semaphoreList);
		new OperatingSystemRunner(pan);

		int executionSpeedSliderVal;

		/*
		 * This is the beginning of the main loop. It begins by getting the value of the
		 * slider which determines execution speed. It updates the GUIPanel clock label,
		 * and checks to see if the reset button was clicked. It then checks the command
		 * line input, and manages that.
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
			e.printStackTrace();
		}

	}

	/*
	 * This method is initializing this class's JFrame settings
	 */
	public OperatingSystemRunner(JPanel p) {
		add(p);
		setSize(1000, 520);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	/*
	 * This method represents the loop of the operating system.
	 */
	public static void execute() throws InterruptedException {
		/*
		 * Initialize the MMU
		 */
		if (mmu == null) {
			if (pan.isTxtVirtualMemorySizeIsVisible()) {
				mmu = new MMUVirtual(Integer.parseInt(pan.getTxtVirtualMemorySize().getText()),
						Integer.parseInt(pan.getTxtMainMemorySize().getText()));
			} else {
				mmu = new MMU();
			}
		}
		/*
		 * Initial scheduler setup
		 */
		if (scheduler == null) {
			scheduler = pan.getSchedulerIF();
			scheduler.setMMU(mmu);
			if (scheduler.getType().equals("FCFS")) {
				timeQuantum = 1;
			} else if (scheduler.getType().equals("Round Robin")) {
				timeQuantum = 25;
			}
		}

		/*
		 * Allow for continuous running
		 */
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
			scheduler = pan.resetSchedulerIF();
			pan.setReset(false);

		}

		/*
		 * Check the command line
		 */
		if (!pan.getInputString().isEmpty()) {
			pan.setInputString(commandLineInterpreter(pan.getInputString()));
		}

		/*
		 * Check for new processes, if the exist, add them and schedule them. Set the
		 * memory for them.
		 */
		if (!pan.getNewProcesses().isEmpty()) {
			for (Process p : pan.getNewProcesses()) {
				p.setProcessState(State.NEW);
				scheduler.addNewProcess(p);
				scheduler.schedule(p);
			}
			pan.emptyNewProcessArray();
		}

		updatePanelTables();

		/*
		 * This begins the main 'cpu loop'; While the time quantum has not expired, and
		 * cpu.isContinueCurrentExecution() (This is a boolean value that is switched on
		 * the condition that the calculate time expire, or any other interrupt occurs)
		 * is true: The CPU is run on the process. Time quantum is decremented, and the
		 * clock incremented.
		 * 
		 * The thread is allowed to sleep for each loop for a seam-less execution,
		 * although I question if this is necessary.
		 */
		int currentTimeQuantum = timeQuantum;
		if (!scheduler.getReadyQueue().isEmpty()) {
			Process p = scheduler.getReadyProcess();
			p.setAgingCounter(0);
			if (scheduler.getType().equals("FCFS") && scheduler.getWaitingQueue().isEmpty()) {
				mmu.load(p, cpu);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pan.updateRegisterTable(cpu.getRegister(), pan.getdTMRegisters(), pan.getRegisterTable());
						pan.updateCacheTable(cpu.getCache(), pan.getdTMCache(), pan.getCacheTable());
					}
				});
				executeCPU(currentTimeQuantum, p);

			} else if (scheduler.getType().equals("FCFS") && !scheduler.getWaitingQueue().isEmpty()) {

			} else {
				mmu.load(p, cpu);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pan.updateRegisterTable(cpu.getRegister(), pan.getdTMRegisters(), pan.getRegisterTable());
						pan.updateCacheTable(cpu.getCache(), pan.getdTMCache(), pan.getCacheTable());
					}
				});
				executeCPU(currentTimeQuantum, p);

			}

			if (!scheduler.getType().equals("FCFS")) {
				pan.getLblCurrentProcessName().setText("");
				pan.getOperationLabel().setText("");
			}
			scheduler.schedule(p);

		}

		/*
		 * If any process was selected for removal, it is returned by the
		 * updateScheduler method and added to the finishedQueue which is used to
		 * populate the finished table.
		 */
		try {
			Process term = scheduler.updateScheduler();
			if (!term.equals(null)) {
				finishedQueue.add(term);
			}
		} catch (NullPointerException n) {
			// System.out.println("NPE for terminated processes.");
		}

		/*
		 * Updating the memory and setting the memory labels in the JPanel
		 */
		pan.setMemoryUsedLbl("Memory Used: " + String.format("%4d/%4d", (mmu.getTotalMemorySize() - mmu.getFreeMemorySize()), mmu.getTotalMemorySize()));
		pan.setLblMemoryAvailable("Memory Available: " + String.format("%4d", mmu.getFreeMemorySize()));

		updatePanelTables();
	}

	/*
	 * 
	 * 
	 */
	public static void updatePanelTables() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pan.updateQueueTable(scheduler.getReadyQueue(), pan.getdTMInputQueue(), pan.getReadyQueueTable());
				pan.updateQueueTable(scheduler.getWaitingQueue(), pan.getdTMWaitingQueue(), pan.getWaitingQueueTable());
				pan.updateQueueTable(finishedQueue, pan.getdTMFinishedQueue(), pan.getFinishedQueueTable());
			}
		});
	}

	/*
	 * This is the execute method to death with processes moving through the CPU The
	 * inner if condition exists so that processes run for time quantum while
	 * incrementing the clock in Round Robin. This statement is skipped when the
	 * FCFS scheduler is being used.
	 */
	public static void executeCPU(int continuousTimeQuantum, Process p) throws InterruptedException {
		pan.getLblCurrentProcessName().setText(p.getName());
		while (continuousTimeQuantum > 0 && cpu.isContinueCurrentExecution()) {
			cpu.run(p);
			continuousTimeQuantum--;
			pan.getOperationLabel().setText(cpu.getProcessOperation());
			if (continuousTimeQuantum > 0 && cpu.isContinueCurrentExecution()) {
				clock.incrementClock();
				pan.setClockLbl(String.format("%06d", clock.getClock()));
				Thread.sleep(1000 / pan.getSliderValue());
				scheduler.updateWaitingProcesses();
				updatePanelTables();
			}

		}
		if (!cpu.getOutput().isEmpty()) {
			pan.setConsole("Output: " + cpu.getOutput() + " on clock cycle " + clock.getClock());
			cpu.setOutput("");
		}
	}

	/*
	 * This method is getting the input from the JPanel command line input, and
	 * using the string in the main loop.
	 */
	public static String commandLineInterpreter(String s) {
		String rtnString = "";
		if (s.contains(",")) {
			String[] splitStr = s.split(",");
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
				String memString = "The amount of memory used: " + (mmu.getTotalMemorySize() - mmu.getFreeMemorySize())
						+ "\nThe amount of memory available: " + mmu.getFreeMemorySize();
				JOptionPane.showMessageDialog(null, memString);
				break;
			case "reset":
				pan.setClockLbl("000000");
				pan.setIsSteadyRun(false);
				clock.setClock(0);
				scheduler = pan.resetSchedulerIF();
				finishedQueue.clear();
				updatePanelTables();
				pan.setConsole("");
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
	 * This method opens a JOptionPan showing all of the processes that have entered
	 * the system but have not yet terminated.
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
