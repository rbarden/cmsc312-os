package scheduling;

import hardware.Clock;
import memory.MMU;
import memory.MemoryManager;
import memory.MultiCoreMemoryManager;
import memory.Port;
import process.Process;
import process.Semaphore;
import process.State;

import java.util.ArrayList;

public class MultiCoreRoundRobin implements MultiCoreScheduler {
	/*
	 * The queue representations
	 */
	private ArrayList<Process> readyQueue;
	private ArrayList<Process> waitingQueue;
	private ArrayList<Process> newQueue;

	private final int TIME_QUANTUM;
	private int timeQuantumRemaining;
	private final String type = "Round Robin";
	
	private Clock clock;
	private MultiCoreMemoryManager mmu;
	private ArrayList<Semaphore> semList;

	

	/*
	 * Constructor initializes time quantum and each of the queues
	 */
	public MultiCoreRoundRobin(int q, Clock c, ArrayList<Semaphore> s) {
		readyQueue = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
		newQueue = new ArrayList<Process>();
		TIME_QUANTUM = q;
		clock = c;
		semList = s;
	}

	/*
	 * This method checks the new queue, and if their is room in the memory, 
	 * any processes in the queue will be added to the ready queue. 
	 */
	public void pollNewQueue() {
		if (!newQueue.isEmpty()) {
			Process process = newQueue.remove(0);
			if (mmu.allocate(process)) {
				process.setProcessState(State.READY);
				process.setArrivalTime(clock.getClock());
				readyQueue.add(process);
			}
		}
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
			mmu.deallocate(p);
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
	 * @return process.Process that has terminated, or null in the case of no terminated
	 * process.
	 */
	public Process updateScheduler() {
		
		pollNewQueue();
		Process terminated = null;
		/*
		 * The hardware.CPU will allow for processes to be rescheduled with a status of
		 * EXIT. These processes are removed here.
		 */
		ArrayList<Process> toRemoveTerm = new ArrayList<>();
		for (Process p : readyQueue) {
			if (p.getProcessState() == State.EXIT) {
				terminated = p;
				toRemoveTerm.add(p);
			}
		}
		
		for (Semaphore s : semList) {
			if (s.getIntLock() == 0 && !(s.getProcessQueue().isEmpty())){
				Process pFromSema = s.getProcessQueue().peek();
				for (Process process : waitingQueue) {
					if (pFromSema.getName().equals(process.getName())){
						process.setProcessState(State.READY);
						s.getProcessQueue().poll();
					}
				}
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
			Process process = waitingQueue.get(i);
			if (process.getIOTimeRemaining() == 0) {
				if (process.getNumChildren() > 0) {
					// Checks if child has terminated, if not, stay here in wait
					Port pcommport = process.getCommunicationPort();
					if (process.getCommunicationPort() != null) {
						if (!pcommport.isChildTerminated()) {
							process.setProcessState(State.WAIT);
							continue;
						}
					}
				}
				process.setProcessState(State.READY);
				enqueueReadyQueue(process);
				toRemove.add(process);
			} else {
				process.setProcessState(State.WAIT);
				process.derimentIOTimeReamaining();
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
	
	public String getType(){
		return type;
	}
	
	public MultiCoreMemoryManager getMmu() {
		return mmu;
	}

	public void setMMU(MultiCoreMemoryManager mmu) {
		this.mmu = mmu;
	}

}
