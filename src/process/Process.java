package process;

import memory.Page;

import java.util.ArrayList;

public class Process {

		public State processState = State.NEW;
		public String name;
		
		public int calculateTimeRemaining;
		public int programCounter;
		public int priority;
		public int processMemorySize;
		private ArrayList<Page> allocatedMemory;
		public int iOTimeRemaining;
		public int arrivalTime;
		public boolean hasEnteredCPU = false;
		
		public ArrayList<String> processCommands;

		
		public Process(State processState, String name, int programCounter, int priority, int processMemorySize,
					   int iOTimeRemaining, ArrayList<String> processCommands, boolean blockedStatus) {
			super();
			this.processState = processState;
			this.name = name;
			this.programCounter = programCounter;
			this.priority = priority;
			this.processMemorySize = processMemorySize;
			this.iOTimeRemaining = iOTimeRemaining;
			this.processCommands = processCommands;
			
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

		public int getProcessMemorySize() {
			return processMemorySize;
		}

		public void setProcessMemorySize(int processMemorySize) {
			this.processMemorySize = processMemorySize;
		}

		public ArrayList<String> getProcessCommands() {
			return processCommands;
		}

		public void setProcessCommands(ArrayList<String> processCommands) {
			this.processCommands = processCommands;
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

		public int getArrivalTime() {
			return arrivalTime;
		}

		public void setArrivalTime(int arrivalTime) {
			this.arrivalTime = arrivalTime;
		}

		@Override
		public String toString() {
			return "process.State: " + processState + ", Name: " + name + ", PC: " + programCounter
					+ ", PR: " + priority + ", MEM: " + processMemorySize + ", IOTR: "
					+ iOTimeRemaining + ", AT: " + arrivalTime 
					+ "]";
		}

		public int getCalculateTimeRemaining() {
			return calculateTimeRemaining;
		}

		public void setCalculateTimeRemaining(int calculateTimeRemaining) {
			this.calculateTimeRemaining = calculateTimeRemaining;
		}

		public int getiOTimeRemaining() {
			return iOTimeRemaining;
		}

		public void setiOTimeRemaining(int iOTimeRemaining) {
			this.iOTimeRemaining = iOTimeRemaining;
		}

		public boolean hasEnteredCPU() {
			return hasEnteredCPU;
		}

		public void setHasEnteredCPU(boolean hasEnteredCPU) {
			this.hasEnteredCPU = hasEnteredCPU;
		}

		public ArrayList<Page> getAllocatedMemory() {
			return allocatedMemory;
		}

		public void setAllocatedMemory(ArrayList<Page> allocatedMemory) {
			this.allocatedMemory = allocatedMemory;
		}
}
