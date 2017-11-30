package process;

import memory.Page;
import memory.Port;

import java.util.ArrayList;


public class Process implements Comparable<Process>{

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
		private int agingCounter = 0;
		private boolean semaphoreAcquired = false;
		private int timeQuantumCounter = 25;
		

		

		public ArrayList<String> processCommands;

		public Process parentProcess;
		private int children;

		private Port communicationPort;

		public Process(Process parentProcess) {
			this.parentProcess = parentProcess;
			this.processState = State.NEW;
			this.name = parentProcess.getName() + "c";
			this.programCounter = parentProcess.getProgramCounter();
			this.priority = parentProcess.getPriority();
			this.processMemorySize = parentProcess.getProcessMemorySize();
			this.iOTimeRemaining = parentProcess.getiOTimeRemaining();
			this.processCommands = parentProcess.getProcessCommands();
		}
		
		public Process(State processState, String name, int programCounter, int priority, int processMemorySize,
					   int iOTimeRemaining, ArrayList<String> processCommands, boolean blockedStatus, Process parentProcess) {
			super();
			this.processState = processState;
			this.name = name;
			this.programCounter = programCounter;
			this.priority = priority;
			this.processMemorySize = processMemorySize;
			this.iOTimeRemaining = iOTimeRemaining;
			this.processCommands = processCommands;
			this.parentProcess = parentProcess;
			
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
			return "State: " + processState + ", Name: " + name + ", PC: " + programCounter
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

		@Override
		public int compareTo(Process o) {
			int i = 0;
			if (this.priority > o.priority) {
				i = -1;
			}
			else if (this.priority < o.priority) {
				i = 1;
			}
			else {
				i = 0;
			}
			return i;
		}

		public Process getParentProcess() {
			return parentProcess;
		}

		public void setParentProcess(Process parentProcess) {
			this.parentProcess = parentProcess;
		}
		
		public boolean isSemaphoreAcquired() {
			return semaphoreAcquired;
		}

		public void setSemaphoreAcquired(boolean semaphoreAcquired) {
			this.semaphoreAcquired = semaphoreAcquired;
		}

		public int getAgingCounter() {
			return agingCounter;
		}

		public void setAgingCounter(int agingCounter) {
			this.agingCounter = agingCounter;
		}

		public boolean isHasEnteredCPU() {
			return hasEnteredCPU;
		}

		public Port getCommunicationPort() {
			return communicationPort;
		}

		public void setCommunicationPort(Port communicationPort) {
			this.communicationPort = communicationPort;
		}

		public int getNumChildren() {
			return children;
		}

		public void incrementChildren() {
			children++;
		}

		public void decrementChildren() {
			children--;
		}
		
		public int getTimeQuantumCounter() {
			return timeQuantumCounter;
		}

		public void setTimeQuantumCounter(int timeQuantumCounter) {
			this.timeQuantumCounter = timeQuantumCounter;
		}
}
