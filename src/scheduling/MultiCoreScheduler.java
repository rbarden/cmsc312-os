package scheduling;

import process.Process;

import java.util.ArrayList;

import memory.MMU;
import memory.MemoryManager;
import memory.MultiCoreMemoryManager;

public interface MultiCoreScheduler {
	/*
	 * Return the type of scheduler (As a String)
	 */
	String getType();
	/*
	 * Method to dequeue the readyQueue
	 */
	Process getReadyProcess();
	
	/*
	 * A method to reschedule a process after it leaves the CPU
	 */
	void schedule(Process process);
	
	/*
	 * Method for scheduler upkeep. The method will return any methods
	 * that exit the system
	 */
	Process updateScheduler();
	
	/*
	 * A method to add processes to the newQueue
	 */
	void addNewProcess(Process p);
	
	/*
	 * A method to update the waiting queue
	 */
	ArrayList<Process> updateWaitingProcesses();
	
	/*
	 * Methods to return the queues
	 */
	ArrayList<Process> getNewQueue();
	ArrayList<Process> getWaitingQueue();
	ArrayList<Process> getReadyQueue();
	
	/*
	 * MMU getters and setters
	 */
	public void setMMU(MultiCoreMemoryManager m);
}
