package memory;

import process.Process;

/**
 * Interprocess Communication Port
 */
public class Port {

    private boolean childTerminated;
    private boolean parentTerminated;
    private int data;

    /**
     * Constructs a port for parent and child processes to communicate
     *
     * @param parent the parent process of the pair
     * @param child the child process of the pair
     */
    public Port(Process parent, Process child) {
        this.childTerminated = false;
        this.parentTerminated = false;
        this.data = Integer.MIN_VALUE;

        parent.setCommunicationPort(this);
        child.setCommunicationPort(this);
    }

    public boolean isChildTerminated() {
        return childTerminated;
    }

    public void setChildTerminated(boolean childTerminated) {
        this.childTerminated = childTerminated;
    }

    public boolean isParentTerminated() {
        return parentTerminated;
    }

    public void setParentTerminated(boolean parentTerminated) {
        this.parentTerminated = parentTerminated;
    }

    public int read() {
        return data;
    }

    public void write(int data) {
        this.data = data;
    }
}
