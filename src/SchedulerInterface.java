import java.util.ArrayList;

public interface SchedulerInterface {
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
	 * Method to return the amount of memory currently allocated
	 */
	int getMemoryUsed();
	
	
	/*
	 * Methods to return the queues
	 */
	ArrayList<Process> getNewQueue();
	ArrayList<Process> getWaitingQueue();
	ArrayList<Process> getReadyQueue();
}
