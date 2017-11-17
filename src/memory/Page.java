package memory;

import process.Process;

public class Page {
    private int id;
    private Process process;

    public Page(int id) {
        this.id = id;
        this.process = null;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return id == page.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
