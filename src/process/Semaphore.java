package process;
import java.util.LinkedList;
import java.util.Queue;

public class Semaphore {
	
	private Queue<Process> processQueue = new LinkedList<Process>();
	private int intLock = 0;
	
	
	/*
	 * A process is allowed to enter its critical section if intLock == 0;
	 * Otherwise, it will be added to the Semaphore Queue and it's status 
	 * changed to WAIT, and returned to the CPU where it will be put in the 
	 * waiting queue;
	 */
	public Process acquire(Process p) {
		if (intLock == 0) {
			intLock = 1;
			p.setSemaphoreAcquired(true);
		}
		else if (p.isSemaphoreAcquired() == true){
			
		}
		else {
			p.setProcessState(State.WAIT);
			processQueue.add(p);
		}
		System.out.println(processQueue.size());
		return p;
		
	}
	
	/*
	 * This will be called anytime a process finished 
	 */
	public Process release(Process p) {
			intLock = 0;
			p.setSemaphoreAcquired(false);
			return p;
	}

	public Queue<Process> getProcessQueue() {
		return processQueue;
	}

	public void setProcessQueue(Queue<Process> processQueue) {
		this.processQueue = processQueue;
	}

	public int getIntLock() {
		return intLock;
	}

	public void setIntLock(int intLock) {
		this.intLock = intLock;
	}
	
}
