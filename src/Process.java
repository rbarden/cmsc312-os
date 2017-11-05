import java.util.ArrayList;

public class Process {

		public State processState = State.NEW;
		public String name;
		
		public int programCounter;
		public int priority;
		public int processMemory;
		public int iOTimeRemaining;
		
		public ArrayList<String> processCommands;
		
		public boolean blockedStatus;
		
		

		public Process(State processState, String name, int programCounter, int priority, int processMemory,
				int iOTimeRemaining, ArrayList<String> processCommands, boolean blockedStatus) {
			super();
			this.processState = processState;
			this.name = name;
			this.programCounter = programCounter;
			this.priority = priority;
			this.processMemory = processMemory;
			this.iOTimeRemaining = iOTimeRemaining;
			this.processCommands = processCommands;
			this.blockedStatus = blockedStatus;
		}

		public State getProcessState() {
			return processState;
		}

		public void setProcessState(State processState) {
			this.processState = processState;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getProgramCounter() {
			return programCounter;
		}

		public void setProgramCounter(int programCounter) {
			this.programCounter = programCounter;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public int getProcessMemory() {
			return processMemory;
		}

		public void setProcessMemory(int processMemory) {
			this.processMemory = processMemory;
		}

		public ArrayList<String> getProcessCommands() {
			return processCommands;
		}

		public void setProcessCommands(ArrayList<String> processCommands) {
			this.processCommands = processCommands;
		}

		public boolean isBlockedStatus() {
			return blockedStatus;
		}

		public void setBlockedStatus(boolean blockedStatus) {
			this.blockedStatus = blockedStatus;
		}
		
		public int getIOTimeRemaining(){
			return iOTimeRemaining;
		}
		
		public void setIOTimeReamaining(int i){
			iOTimeRemaining = i;
		}
		
		public void derimentIOTimeReamaining(){
			iOTimeRemaining--;
		}
		
		
		
		


}
