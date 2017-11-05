
public interface SchedulerInterface {
	Process getReadyProcess();
	void schedule(Process process);
	Process updateScheduler();
}
