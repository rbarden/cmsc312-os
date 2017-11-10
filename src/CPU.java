import java.util.concurrent.ThreadLocalRandom;

public class CPU {
	/*
	 * A boolean to determine whether the quantum loop should continue or break, 
	 * and a process. 
	 */
	private boolean continueCurrentExecution = true;
	private Process process;
	private String processOperation;
	private String output = "";

	


	/*
	 * This CPU checks each process command from its original process file and executes its' commands 
	 * accordingly.
	 */
	public void run(Process p) {
		process = p;
		process.setProcessState(State.RUN);
		String pComm = process.getProcessCommands().get(0);
		if (pComm.contains(",")) {
			String splitComm[] = pComm.split(",");
			processOperation = splitComm[0] + " " + splitComm[1];
			if (splitComm[0].equals("calculate")) {
				int calcTimeRemaining = Integer.parseInt(splitComm[1]);
				if (calcTimeRemaining == 0) {
					continueCurrentExecution = false;
					process.getProcessCommands().remove(0);
					process.setProcessState(State.READY);
					process.setProgramCounter(process.getProgramCounter() + 1);
				} else {
					process.getProcessCommands().set(0, "calculate," + String.valueOf(calcTimeRemaining - 1));
					process.setProcessState(State.READY);
					process.setProgramCounter(process.getProgramCounter() + 1);
				}
			} else if (splitComm[0].equals("exe")) {
				continueCurrentExecution = false;
				process.getProcessCommands().remove(0);
				process.setProcessState(State.EXIT);
				process.setProgramCounter(process.getProgramCounter() + 1);
			} else if (splitComm[0].equals("out")) {
				output = splitComm[1] + " " + splitComm[2] + " " + splitComm[3] + " " + splitComm[4];
				process.getProcessCommands().remove(0);
				System.out.println(splitComm[1]);
				process.setProcessState(State.READY);
				process.setProgramCounter(process.getProgramCounter() + 1);
			}

		} else if (pComm.equals("io")) {
			processOperation = pComm;
			continueCurrentExecution = false;
			process.setIOTimeReamaining(ThreadLocalRandom.current().nextInt(25, 50 + 1));
			process.getProcessCommands().remove(0);
			process.setProcessState(State.WAIT);
			process.setProgramCounter(process.getProgramCounter() + 1);
		} else if (pComm.equals("yield")) {
			processOperation = pComm;
			continueCurrentExecution = false;
			process.getProcessCommands().remove(0);
			process.setProcessState(State.READY);
			process.setProgramCounter(process.getProgramCounter() + 1);
		}

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


}
