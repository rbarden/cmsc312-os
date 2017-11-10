import java.util.ArrayList;

public class RoundRobin implements SchedulerInterface {
	/*
	 * The queue representations
	 */
	private ArrayList<Process> readyQueue;
	private ArrayList<Process> waitingQueue;
	private ArrayList<Process> newQueue;

	private int totalMemoryUsed = 0;
	private int totalMemoryAvailable = 4096;
	private final int TIME_QUANTUM;
	private int timeQuantumRemaining;
	private final String type = "Round Robin";
	
	Clock clock;

	/*
	 * Constructor initializes time quantum and each of the queues
	 */
	public RoundRobin(int q, Clock c) {
		readyQueue = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
		newQueue = new ArrayList<Process>();
		TIME_QUANTUM = q;
		clock = c;
	}

	/*
	 * This method checks the new queue, and if their is room in the memory, 
	 * any processes in the queue will be added to the ready queue. 
	 */
	public void pollNewQueue() {
		ArrayList<Process> rem = new ArrayList<Process>();
		for (int i = 0; i < newQueue.size(); i++) {
			if (newQueue.get(i).getProcessMemory() <= totalMemoryAvailable) {
				totalMemoryAvailable -= newQueue.get(i).getProcessMemory();
				totalMemoryUsed += newQueue.get(i).getProcessMemory();
				newQueue.get(i).setProcessState(State.READY);
				newQueue.get(i).setArrivalTime(clock.getClock());
				readyQueue.add(newQueue.get(i));
				rem.add(newQueue.get(i));
			}

		}
		newQueue.removeAll(rem);
	}

	public void addNewProcess(Process p) {
		newQueue.add(p);
	}

	public void enqueueReadyQueue(Process p) {
		readyQueue.add(p);
	}

	public void enqueueWaitingQueue(Process p) {
		waitingQueue.add(p);
	}

	public void enqueuenewQueue(Process p) {
		newQueue.add(p);
	}

	@Override
	/*
	 * This will return the process at the from of the ready queue,
	 * and remove that from the ArrayList. It represents a queue pop. 
	 */
	public Process getReadyProcess() {
		Process ret = readyQueue.get(0);
		readyQueue.remove(0);
		return ret;
	}

	@Override
	/*
	 * This will take a process and schedule it appropriately
	 */
	public void schedule(Process p) {
		if (p.getProcessState() == State.READY) {
			enqueueReadyQueue(p);
		} else if (p.getProcessState() == State.WAIT) {
			enqueueWaitingQueue(p);
		} else if (p.getProcessState() == State.EXIT) {
			readyQueue.add(p);
		}
	}

	/*
	 * This should be called once every clock cycle. It will check each process
	 * in the ready and waiting queues, and if any process state equals EXIT, it
	 * is returned. Only one process should be allowed to terminate per cycle.
	 * 
	 * This method also checks the IOQueue. If any process has finished
	 * servicing its IO request, such that its iOTimeRemaining equals 0, the
	 * process is re-scheduled. Otherwise, the variable is decremented.
	 * 
	 * @return Process that has terminated, or null in the case of no terminated
	 * process.
	 */
	public Process updateScheduler() {
		System.out.println(clock.getClock());
		pollNewQueue();
		Process terminated = null;
		/*
		 * The CPU will allow for processes to be rescheduled with a status of
		 * EXIT. These processes are removed here.
		 */
		ArrayList<Process> toRemoveTerm = new ArrayList<>();
		for (Process p : readyQueue) {
			if (p.getProcessState() == State.EXIT) {
				System.out.println("Process Removed Ready");
				terminated = p;
				toRemoveTerm.add(p);
				totalMemoryAvailable += p.getProcessMemory();
				totalMemoryUsed -= p.getProcessMemory();

			}
		}
		readyQueue.removeAll(toRemoveTerm);

		/*
		 * If processes are in the waiting queue, they must be evaluated
		 * and their IO values are decremented and if they equal 0, the
		 * process is moved back to the ready queue. 
		 */
		updateWaitingProcesses();

		/*
		 * If the waiting queue is not empty and the ready queue has room for
		 * the process, remove it from the waiting queue and add it to the ready
		 * queue.
		 */
		if (!waitingQueue.isEmpty()) {
			for (int i = 0; i < waitingQueue.size(); i++) {
				if (waitingQueue.get(i).getProcessState() == State.READY) {
					readyQueue.add(waitingQueue.get(i));
					waitingQueue.remove(i);
				}
			}
		}
		return terminated;
	}
	
	
	/*
	 * If processes are in the waiting queue, they must be evaluated
	 * and their IO values are decremented and if they equal 0, the
	 * process is moved back to the ready queue. 
	 */
	public ArrayList<Process> updateWaitingProcesses(){
		ArrayList<Process> toRemove = new ArrayList<>();
		for (int i = 0; i < waitingQueue.size(); i++) {
			if (waitingQueue.get(i).getIOTimeRemaining() == 0) {
				waitingQueue.get(i).setProcessState(State.READY);
				enqueueReadyQueue(waitingQueue.get(i));
				toRemove.add(waitingQueue.get(i));
			} else {
				waitingQueue.get(i).setProcessState(State.WAIT);
				waitingQueue.get(i).derimentIOTimeReamaining();
			}
		}
		waitingQueue.removeAll(toRemove);
		return toRemove;
	}
	
	
	/*
	 * Getters and setters 
	 */

	public ArrayList<Process> getReadyQueue() {
		return readyQueue;
	}

	public void setReadyQueue(ArrayList<Process> readyQueue) {
		this.readyQueue = readyQueue;
	}

	public ArrayList<Process> getWaitingQueue() {
		return waitingQueue;
	}

	public void setWaitingQueue(ArrayList<Process> waitingQueue) {
		this.waitingQueue = waitingQueue;
	}

	public ArrayList<Process> getNewQueue() {
		return newQueue;
	}

	public void setNewQueue(ArrayList<Process> newQueue) {
		this.newQueue = newQueue;
	}

	public int getTimeQuantumRemaining() {
		return timeQuantumRemaining;
	}

	public void setTimeQuantumRemaining(int timeQuantumRemaining) {
		this.timeQuantumRemaining = timeQuantumRemaining;
	}

	public int getTIME_QUANTUM() {
		return TIME_QUANTUM;
	}

	public void settotalMemoryUsed(int totalMemoryUsed) {
		this.totalMemoryUsed = totalMemoryUsed;
	}

	public int getMemoryUsed() {
		return totalMemoryUsed;
	}

	public int getTotalMemoryAvailable() {
		return totalMemoryAvailable;
	}

	public void setTotalMemoryAvailable(int totalMemoryAvailable) {
		this.totalMemoryAvailable = totalMemoryAvailable;
	}
	
	public String getType(){
		return type;
	}

}
