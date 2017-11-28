package hardware;

import memory.Cache;
import memory.Register;
import process.Process;
import process.Semaphore;
import process.State;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CPU {
	/*
	 * A boolean to determine whether the quantum loop should continue or break, and
	 * a process.
	 */
	private boolean continueCurrentExecution = true;
	private Process process;
	private String processOperation;
	private String output = "";

	private ArrayList<Process> newChildren = new ArrayList<>();
	private ArrayList<Semaphore> semlist;

	private Register register = new Register();
	private Cache cache = new Cache();

	private Clock clock;
	


	

	public CPU(Clock clock, ArrayList<Semaphore> semList) {
		this.clock = clock;
		this.semlist = semList;
	}

	/*
	 * This hardware.CPU checks each process command from its original process file
	 * and executes its' commands accordingly.
	 */
	public void run(Process p) {
		process = p;
		process.setProcessState(State.RUN);
		String pComm = process.getProcessCommands().get(process.getProgramCounter());
		if (pComm.contains(",")) {
			String splitComm[] = pComm.split(",");
			processOperation = splitComm[0] + " " + splitComm[1];
			if (splitComm[0].equals("calculate")) {
				int calcTimeRemaining = Integer.parseInt(splitComm[1]);
				if (calcTimeRemaining == 0) {
					continueCurrentExecution = false;
					process.setProcessState(State.READY);
					process.setProgramCounter(process.getProgramCounter() + 1);
				} else {
					process.getProcessCommands().set(process.getProgramCounter(),
							"calculate," + String.valueOf(calcTimeRemaining - 1));
					process.setProcessState(State.READY);
				}
			} else if (splitComm[0].equals("exe")) {
				continueCurrentExecution = false;
				process.setProcessState(State.EXIT);
				process.setProgramCounter(process.getProgramCounter() + 1);
			} else if (splitComm[0].equals("out")) {
				continueCurrentExecution = false;
				output = splitComm[1] + " " + splitComm[2] + " " + splitComm[3];
				process.setProcessState(State.READY);
				process.setProgramCounter(process.getProgramCounter() + 1);
			} else if (splitComm[0].equals("criticalsection")) {
				int criticalSectionTimeRemaining = Integer.parseInt(splitComm[1].trim());
				int whichSempahoreSlot = Integer.parseInt(p.getName().substring(1)) % 10;
				process = semlist.get(whichSempahoreSlot).acquire(process);
				output = "CRITICAL SECTION - " + whichSempahoreSlot;

				if (!(process.getProcessState() == State.WAIT)) {
					if (criticalSectionTimeRemaining == 0) {
						continueCurrentExecution = false;
						process.setProcessState(State.READY);
						process.setProgramCounter(process.getProgramCounter() + 1);
						process = semlist.get(whichSempahoreSlot).release(process);
					} else {
						process.getProcessCommands().set(process.getProgramCounter(),
								"criticalsection," + String.valueOf(criticalSectionTimeRemaining - 1));
						process.setProcessState(State.READY);
					}
				}

			}

		} else if (pComm.equals("io")) {
			processOperation = pComm;
			continueCurrentExecution = false;
			process.setIOTimeReamaining(ThreadLocalRandom.current().nextInt(25, 50 + 1));
			process.setProcessState(State.WAIT);
			process.setProgramCounter(process.getProgramCounter() + 1);
		} else if (pComm.equals("yield")) {
			processOperation = pComm;
			continueCurrentExecution = false;
			process.setProcessState(State.READY);
			process.setProgramCounter(process.getProgramCounter() + 1);
		} else if (pComm.equals("fork")) {
			Process child = new Process(process);
			
			newChildren.add(child);
			process.setProcessState(State.WAIT);
		}

	}

	public ArrayList<Process> getNewChildren() {
		return newChildren;
	}

	public void setNewChildren(ArrayList<Process> newChildren) {
		this.newChildren = newChildren;
	}

	/*
	 * Getters and setters
	 */
	public boolean isContinueCurrentExecution() {
		return continueCurrentExecution;
	}

	public void setContinueCurrentExecution(boolean continueCurrentExecution) {
		this.continueCurrentExecution = continueCurrentExecution;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public String getProcessOperation() {
		return processOperation;
	}

	public void setProcessOperation(String processOperation) {
		this.processOperation = processOperation;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Register getRegister() {
		return register;
	}

	public Cache getCache() {
		return cache;
	}

	public Clock getClock() {
		return clock;
	}
	
	
}
