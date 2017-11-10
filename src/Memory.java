
public class Memory {
	private int totalMemory = 4096;
	private int memoryUsed;
	
	public int getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(int memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	
	public int getAvailableMemory(){
		return totalMemory - memoryUsed;
	}

}
