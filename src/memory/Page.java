package memory;

import process.Process;

public class Page {
    private Process process;

    public Page(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
