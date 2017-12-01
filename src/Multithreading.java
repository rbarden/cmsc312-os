import hardware.Clock;
import hardware.Core;
import hardware.MultiCoreCPU;
import memory.MultiCoreMMU;
import memory.MultiCoreMMUVirtual;
import memory.MultiCoreMemoryManager;
import process.Process;
import process.Semaphore;
import process.State;
import scheduling.MultiCoreFCFS;
import scheduling.MultiCorePriorityScheduler;
import scheduling.MultiCoreRoundRobin;
import scheduling.MultiCoreScheduler;
import scheduling.Scheduler;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Multithreading extends JFrame {

	public static ArrayList<Process> finishedQueue = new ArrayList<Process>();
	public static int timeQuantum = 25;
	public static GUIPanel pan;
	public static Clock clock;
	public static MultiCoreScheduler scheduler;
	public static MultiCoreCPU cpu;
	public static MultiCoreMemoryManager mmu;
	public static ArrayList<Semaphore> semaphoreList;
	static Process p = null;
	static Process p1 = null;
	static Process p2 = null;
	static Process p3 = null;

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		clock = new Clock();
		scheduler = null;
		semaphoreList = new ArrayList<Semaphore>();
		pan = new GUIPanel(clock, semaphoreList);
		pan.getLblCore().setVisible(true);
		pan.getLblCore1().setVisible(true);
		pan.getLblCore2().setVisible(true);
		pan.getLblCore3().setVisible(true);
		pan.getLblCurrentCpuProcess().setVisible(false);

		for (int i = 0; i < 10; i++) {
			semaphoreList.add(new Semaphore());
		}

		cpu = new MultiCoreCPU(clock, semaphoreList);

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
	public Multithreading(JPanel p) {
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
				mmu = new MultiCoreMMUVirtual(Integer.parseInt(pan.getTxtVirtualMemorySize().getText()),
						Integer.parseInt(pan.getTxtMainMemorySize().getText()));
			} else {
				mmu = new MultiCoreMMU();
			}
		}

		/*
		 * Initial scheduler setup
		 */
		if (scheduler == null) {
			Scheduler sched = pan.getSchedulerIF();
			if (sched.getType().equals("FCFS")) {
				scheduler = new MultiCoreFCFS(clock, semaphoreList);
			} else if (sched.getType().equals("Priority Scheduler")) {
				scheduler = new MultiCorePriorityScheduler(clock, semaphoreList);
			} else {
				scheduler = new MultiCoreRoundRobin(timeQuantum, clock, semaphoreList);
			}
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
		cpu.getCore1().setContinueCurrentExecution(true);
		cpu.getCore2().setContinueCurrentExecution(true);
		cpu.getCore3().setContinueCurrentExecution(true);
		cpu.getCore4().setContinueCurrentExecution(true);
		clock.incrementClock();
		pan.setClockLbl(String.format("%06d", clock.getClock()));

		/*
		 * Check the reset boolean, if switched, reset the clock.
		 */
		if (pan.getReset()) {
			pan.setClockLbl("000000");
			pan.setIsSteadyRun(false);
			clock.setClock(0);
			Scheduler sched = pan.getSchedulerIF();
			if (sched.getType().equals("FCFS")) {
				scheduler = new MultiCoreFCFS(clock, semaphoreList);
			} else if (sched.getType().equals("Priority Scheduler")) {
				scheduler = new MultiCorePriorityScheduler(clock, semaphoreList);
			} else {
				scheduler = new MultiCoreRoundRobin(timeQuantum, clock, semaphoreList);
			}
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

		if (p == null && !scheduler.getReadyQueue().isEmpty()) {
			p = getProcess();
			p = loadAndExecute(cpu.getCore1(), p, 1);
		} else {
			p = loadAndExecute(cpu.getCore1(), p, 1);

		}

		if (p1 == null && !scheduler.getReadyQueue().isEmpty()) {
			p1 = getProcess();
			p1 = loadAndExecute(cpu.getCore2(), p1, 2);
		} else {
			p1 = loadAndExecute(cpu.getCore2(), p1, 2);
		}

		if (p2 == null && !scheduler.getReadyQueue().isEmpty()) {
			p2 = getProcess();
			p2 = loadAndExecute(cpu.getCore3(), p2, 3);
		} else {
			p2 = loadAndExecute(cpu.getCore3(), p2, 3);
		}

		if (p3 == null && !scheduler.getReadyQueue().isEmpty()) {
			p3 = getProcess();
			p3 = loadAndExecute(cpu.getCore4(), p3, 4);
		} else {
			p3 = loadAndExecute(cpu.getCore4(), p3, 4);
		}

		/*
		 * If the processes are not null, they get rescheduled, otherwise they will be
		 * held to run longer.
		 */
		if (!(p == null)) {
			if (scheduler.getType().equals("Round Robin") && p.getTimeQuantumCounter() == 0) {
				p.setTimeQuantumCounter(25);
				scheduler.schedule(p);
				p = null;
			}else if (scheduler.getType().equals("Round Robin")){
				//Don't Schedule
			}else {
				scheduler.schedule(p);
				p = null;
			}
		}
		if (!(p1 == null)) {
			if (scheduler.getType().equals("Round Robin") && p1.getTimeQuantumCounter() == 0) {
				p1.setTimeQuantumCounter(25);
				scheduler.schedule(p1);
				p1 = null;
			}else if (scheduler.getType().equals("Round Robin")){
				//Don't Schedule
			} else {
				scheduler.schedule(p1);
				p1 = null;
			}
		}
		if (!(p2 == null)) {
			if (scheduler.getType().equals("Round Robin") && p2.getTimeQuantumCounter() == 0) {
				
				p2.setTimeQuantumCounter(25);
				scheduler.schedule(p2);
				p2 = null;
			}else if (scheduler.getType().equals("Round Robin")){
				//Don't Schedule
			} else {
				scheduler.schedule(p2);
				p2 = null;
			}
		}
		if (!(p3 == null)) {
			if (scheduler.getType().equals("Round Robin") && p3.getTimeQuantumCounter() == 0) {
				p3.setTimeQuantumCounter(25);
				scheduler.schedule(p3);
				p3 = null;
			}else if (scheduler.getType().equals("Round Robin")){
				//Don't Schedule
			} else {
				scheduler.schedule(p3);
				p3 = null;
			}
		}

		if (scheduler.getReadyQueue().isEmpty()) {
			pan.getCoreProcess().setText("");
			pan.getCoreProcess1().setText("");
			pan.getCoreProcess2().setText("");
			pan.getCoreProcess3().setText("");
		}

		getNewChildren(cpu.getCore1());
		getNewChildren(cpu.getCore2());
		getNewChildren(cpu.getCore3());
		getNewChildren(cpu.getCore4());

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
		}

		/*
		 * Updating the memory and setting the memory labels in the JPanel
		 */
		pan.setMemoryUsedLbl("Memory Used: " + String.format("%4d/%4d",
				(mmu.getTotalMemorySize() - mmu.getFreeMemorySize()), mmu.getTotalMemorySize()));
		pan.setLblMemoryAvailable("Memory Available: " + String.format("%4d", mmu.getFreeMemorySize()));

		updatePanelTables();
	}
	



	/*
	 * Method to get new children
	 */
	public static void getNewChildren(Core core) {
		if (!core.getNewChildren().isEmpty()) {
			boolean processRetrieved = false;
			for (Process pr : core.getNewChildren()) {
				scheduler.addNewProcess(pr);
				scheduler.schedule(pr);
			}
			processRetrieved = true;
			if (processRetrieved) {
				core.getNewChildren().clear();
			}
		}
	}

	public static Process loadAndExecute(Core core, Process p, int coreNum) {

		if (!(p == null)) {
			boolean validWaitingQueue = true;
			for (Process process : scheduler.getWaitingQueue()) {
				if (process.getNumChildren() == 0) {
					validWaitingQueue = false;
				}
			}

			if (scheduler.getType().equals("FCFS") && validWaitingQueue) {
				mmu.load(p, core);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pan.updateRegisterTable(core.getRegister(), pan.getdTMRegisters(), pan.getRegisterTable());
						pan.updateCacheTable(core.getCache(), pan.getdTMCache(), pan.getCacheTable());
					}
				});
				try {
					executeCPU(p, core, coreNum);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (scheduler.getType().equals("FCFS") && !scheduler.getWaitingQueue().isEmpty()) {

			} else {
				mmu.load(p, core);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pan.updateRegisterTable(core.getRegister(), pan.getdTMRegisters(), pan.getRegisterTable());
						pan.updateCacheTable(core.getCache(), pan.getdTMCache(), pan.getCacheTable());
					}
				});
				try {
					executeCPU(p, core, coreNum);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (!scheduler.getType().equals("FCFS")) {
				pan.getLblCurrentProcessName().setText("");
				pan.getOperationLabel().setText("");
			}

		}
		return p;
	}

	public static Process getProcess() {
		Process p = scheduler.getReadyProcess();
		p.setAgingCounter(0);
		return p;
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
	public static void executeCPU(Process p, Core core, int coreNum) throws InterruptedException {
		if (coreNum == 1) {
			pan.getCoreProcess().setText(p.getName());
		} else if (coreNum == 2) {
			pan.getCoreProcess1().setText(p.getName());
		} else if (coreNum == 3) {
			pan.getCoreProcess2().setText(p.getName());
		} else if (coreNum == 4) {
			pan.getCoreProcess3().setText(p.getName());
		}
		// pan.getLblCurrentProcessName().setText(p.getName());
		core.run(p);
		// pan.getOperationLabel().setText(core.getProcessOperation());

		if (!core.getOutput().isEmpty()) {
			pan.setConsole(
					"Output: " + core.getOutput() + " from Core " + coreNum + " on clock cycle " + clock.getClock());
			core.setOutput("");
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
				Scheduler sched = pan.getSchedulerIF();
				if (sched.getType().equals("FCFS")) {
					scheduler = new MultiCoreFCFS(clock, semaphoreList);
				} else if (sched.getType().equals("Priority Scheduler")) {
					scheduler = new MultiCorePriorityScheduler(clock, semaphoreList);
				} else {
					scheduler = new MultiCoreRoundRobin(timeQuantum, clock, semaphoreList);
				}
				finishedQueue.clear();
				updatePanelTables();
				pan.setConsole("");
				break;
			case "exit":
				System.exit(0);
				break;
			case "exe":
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
