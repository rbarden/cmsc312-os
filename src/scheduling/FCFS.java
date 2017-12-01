package scheduling;

import hardware.Clock;
import memory.MemoryManager;
import memory.Port;
import process.Process;
import process.Semaphore;
import process.State;

import javax.swing.*;
import java.util.ArrayList;

public class FCFS implements Scheduler {

	private ArrayList<Process> newQueue;
	private ArrayList<Process> readyQueue;
	private ArrayList<Process> waitingQueue;

	private final String type = "FCFS";
	
	private Clock clock;
	private MemoryManager mmu;
	private ArrayList<Semaphore> semList;
	

	public FCFS(Clock c, ArrayList<Semaphore> s) {
		newQueue = new ArrayList<Process>();
		readyQueue = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
		clock = c;
		semList = s;
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
			mmu.deallocate(p);
			readyQueue.add(p);
		}

	}

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
				terminated = p;
				toRemoveTerm.add(p);
			}
		}
		readyQueue.removeAll(toRemoveTerm);
		
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
			Process process = waitingQueue.get(i);
			if (process.getIOTimeRemaining() == 0) {
				if (process.getNumChildren() > 0) {
					// Checks if child has terminated, if not, stay here in wait
					Port pcommport = process.getFromChildPort();
					if (pcommport != null) {
						if (!pcommport.isChildTerminated()) {
							process.setProcessState(State.WAIT);
							continue;
						} else {
							process.decrementChildren();
							JOptionPane.showMessageDialog(null, "I am " + process.getName() + ". A child just terminated with data: " + pcommport.read());
						}
					}
				}
				process.setProcessState(State.READY);
				readyQueue.add(0, process);
				toRemove.add(process);
			} else {
				process.setProcessState(State.WAIT);
				process.derimentIOTimeReamaining();
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
	
	public MemoryManager getMmu() {
		return mmu;
	}

	public void setMMU(MemoryManager mmu) {
		this.mmu = mmu;
	}

}
