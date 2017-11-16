package scheduling;

import hardware.Clock;
import process.Process;
import process.State;

import java.util.ArrayList;

public class FCFS implements Scheduler {

	private ArrayList<Process> newQueue;
	private ArrayList<Process> readyQueue;
	private ArrayList<Process> waitingQueue;

	private int totalMemoryUsed = 0;
	private int totalMemoryAvailable = 4096;
	private final String type = "scheduling.FCFS";
	
	private Clock clock;

	public FCFS(Clock c) {
		newQueue = new ArrayList<Process>();
		readyQueue = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
		clock = c;
	}

	@Override
	public Process getReadyProcess() {
		Process p = readyQueue.remove(0);
		p.setHasEnteredCPU(true);
		return p;
	}

	@Override
	public void schedule(Process p) {
		if (p.getProcessState() == State.READY) {
			if (p.hasEnteredCPU()) {
				readyQueue.add(0, p);
			} else {
				readyQueue.add(p);
			}
		} else if (p.getProcessState() == State.WAIT) {
			waitingQueue.add(p);
		} else if (p.getProcessState() == State.EXIT) {
			readyQueue.add(p);
		}

	}

	public void pollNewQueue() {
		if (!newQueue.isEmpty()) {
			if (newQueue.get(0).getProcessMemory() <= totalMemoryAvailable) {
				totalMemoryAvailable -= newQueue.get(0).getProcessMemory();
				totalMemoryUsed += newQueue.get(0).getProcessMemory();
				newQueue.get(0).setProcessState(State.READY);
				newQueue.get(0).setArrivalTime(clock.getClock());
				readyQueue.add(newQueue.get(0));
				newQueue.remove(0);
			}
		}
	}

	@Override
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
				System.out.println("process.Process Removed Ready");
				terminated = p;
				toRemoveTerm.add(p);
				totalMemoryAvailable += p.getProcessMemory();
				totalMemoryUsed -= p.getProcessMemory();
			}
		}
		readyQueue.removeAll(toRemoveTerm);

		/*
		 * If processes are in the waiting queue, they must be evaluated and
		 * their IO values are decremented and if they equal 0, the process is
		 * moved back to the ready queue.
		 */
		updateWaitingProcesses();

		return terminated;
	}
	
	public ArrayList<Process> updateWaitingProcesses(){
		ArrayList<Process> toRemove = new ArrayList<>();
		for (int i = 0; i < waitingQueue.size(); i++) {
			if (waitingQueue.get(i).getIOTimeRemaining() == 0) {
				waitingQueue.get(i).setProcessState(State.READY);
				readyQueue.add(0, waitingQueue.get(i));
				toRemove.add(waitingQueue.get(i));
			} else {
				waitingQueue.get(i).setProcessState(State.WAIT);
				waitingQueue.get(i).derimentIOTimeReamaining();
			}
		}
		waitingQueue.removeAll(toRemove);
		return toRemove;
	}

	@Override
	public void addNewProcess(Process p) {
		newQueue.add(p);
	}

	@Override
	public int getMemoryUsed() {
		return totalMemoryUsed;
	}

	@Override
	public ArrayList<Process> getNewQueue() {
		return newQueue;
	}

	@Override
	public ArrayList<Process> getWaitingQueue() {
		return waitingQueue;
	}

	@Override
	public ArrayList<Process> getReadyQueue() {
		return readyQueue;
	}
	
	public String getType(){
		return type;
	}

}
