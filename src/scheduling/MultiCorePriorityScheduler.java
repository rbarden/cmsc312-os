package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import javax.swing.JOptionPane;

import hardware.Clock;
import memory.MultiCoreMemoryManager;
import memory.Port;
import process.Process;
import process.Semaphore;
import process.State;

public class MultiCorePriorityScheduler implements MultiCoreScheduler {
	private PriorityQueue<Process> readyQueue;
	private ArrayList<Process> waitingQueue;
	private ArrayList<Process> newQueue;

	private final String type = "Priority Scheduler";
	
	private Clock clock;
	private MultiCoreMemoryManager mmu;
	private ArrayList<Semaphore> semList;
	
	private int lastClock = 0;
	
	public MultiCorePriorityScheduler(Clock c, ArrayList<Semaphore> s) {
		newQueue = new ArrayList<Process>();
		readyQueue = new PriorityQueue<Process>(20, (a,b) -> b.getPriority() - a.getPriority());
		waitingQueue = new ArrayList<Process>();
		clock = c;
		semList = s;
	}
	
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Process getReadyProcess() {
		return readyQueue.poll();
	}

	@Override
	public void schedule(Process p) {
		if (p.getProcessState() == State.READY) {
			readyQueue.add(p);		
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
		int clockdifference = clock.getClock() - lastClock;
		lastClock = clock.getClock();
		
		pollNewQueue();
		Process terminated = null;
		/*
		 * The hardware.CPU will allow for processes to be rescheduled with a status of
		 * EXIT. These processes are removed here.
		 * 
		 * This is also where the Aging is implemented; The agingCounter for each 
		 * process is set to the clock here. If any process has remained for more than 
		 * 2000 clock cycles, it's priority is increased. 
		 */
		ArrayList<Process> toRemoveTerm = new ArrayList<>();
		for (Process p : readyQueue) {

			p.setAgingCounter(p.getAgingCounter() + clockdifference);
			if (p.getAgingCounter() > 2000 && p.getPriority() < 10) {
				p.setPriority(p.getPriority() + 1);
				p.setAgingCounter(0);
			}
			
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

	@Override
	public void addNewProcess(Process p) {
		newQueue.add(p);
	}

	@Override
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
				readyQueue.add(process);
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
	public ArrayList<Process> getNewQueue() {
		return newQueue;
	}

	@Override
	public ArrayList<Process> getWaitingQueue() {
		return waitingQueue;
	}

	@Override
	public ArrayList<Process> getReadyQueue() {
		ArrayList<Process> list = new ArrayList<Process>();
		list.addAll(readyQueue);
		Collections.sort(list);
		return list;
	}

	@Override
	public void setMMU(MultiCoreMemoryManager m) {
		this.mmu = m;
	}

}
