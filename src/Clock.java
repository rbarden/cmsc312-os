
public class Clock {
	int clock;
	
	public Clock(){
		clock = 0;
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}
	
	public void incrementClock(){
		clock++;
	}

}
