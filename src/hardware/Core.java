package hardware;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import memory.Port;
import process.Process;
import process.Semaphore;
import process.State;

public class Core {

	private Process process;
	private String processOperation;
	private String output = "";
	private boolean continueCurrentExecution = true;

	private ArrayList<Process> newChildren = new ArrayList<>();
	private ArrayList<Semaphore> semlist;

	public Core(ArrayList<Semaphore> semList) {
		this.semlist = semList;
	}

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
					process.setTimeQuantumCounter(process.getTimeQuantumCounter() - 1);
					process.setProcessState(State.READY);
				}
			} else if (splitComm[0].equals("exe")) {
				continueCurrentExecution = false;
				process.setProcessState(State.EXIT);
				process.setProgramCounter(process.getProgramCounter() + 1);
				process.setTimeQuantumCounter(0);
				Process parent = process.getParentProcess();
				if (parent != null) {
					Port port = process.getCommunicationPort();
					port.setChildTerminated(true);
					port.write(new Random().nextInt());
					parent.decrementChildren();
				}
			} else if (splitComm[0].equals("out")) {
				continueCurrentExecution = false;
				output = splitComm[1] + " " + splitComm[2] + " " + splitComm[3];
				process.setProcessState(State.READY);
				process.setProgramCounter(process.getProgramCounter() + 1);
				process.setTimeQuantumCounter(0);
			} else if (splitComm[0].equals("criticalsection")) {
				int criticalSectionTimeRemaining = Integer.parseInt(splitComm[1].trim());
				int whichSempahoreSlot;
				if (p.getName().endsWith("c")) {
					whichSempahoreSlot = Integer.parseInt(p.getName().substring(1, p.getName().length() - 1)) % 10;
				} else {
					whichSempahoreSlot = Integer.parseInt(p.getName().substring(1)) % 10;
				}
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
						process.setTimeQuantumCounter(process.getTimeQuantumCounter() - 1);
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
			process.setTimeQuantumCounter(0);
		} else if (pComm.equals("yield")) {
			processOperation = pComm;
			continueCurrentExecution = false;
			process.setProcessState(State.READY);
			process.setProgramCounter(process.getProgramCounter() + 1);
			process.setTimeQuantumCounter(0);
		} else if (pComm.equals("fork")) {
			Process child = new Process(process);
			new Port(process, child);
			child.setProgramCounter(child.getProgramCounter() + 1);
			newChildren.add(child);
			process.setProcessState(State.WAIT);
			process.incrementChildren();
			process.setProgramCounter(process.getProgramCounter() + 1);
			process.setTimeQuantumCounter(0);
			continueCurrentExecution = false;
		}
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

	public boolean isContinueCurrentExecution() {
		return continueCurrentExecution;
	}

	public void setContinueCurrentExecution(boolean continueCurrentExecution) {
		this.continueCurrentExecution = continueCurrentExecution;
	}

	public ArrayList<Process> getNewChildren() {
		return newChildren;
	}

	public void setNewChildren(ArrayList<Process> newChildren) {
		this.newChildren = newChildren;
	}

	public ArrayList<Semaphore> getSemlist() {
		return semlist;
	}

	public void setSemlist(ArrayList<Semaphore> semlist) {
		this.semlist = semlist;
	}

}