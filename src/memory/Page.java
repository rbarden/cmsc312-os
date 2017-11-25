package memory;

import process.Process;

public class Page implements Comparable {
    private int id;
    private Process process;
    private int lastAccess;

    public Page(int id) {
        this.id = id;
        this.process = null;
        this.lastAccess = 0;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void setLastAccess(int lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return id == page.id;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.lastAccess, ((Page) o).lastAccess);
    }
    
    public String toString() {
    		return this.process.getName() + " " + this.id; 
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    
    
}
