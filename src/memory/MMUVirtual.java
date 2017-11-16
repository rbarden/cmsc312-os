package memory;

import process.Process;

public class MMUVirtual implements MemoryManager{
    @Override
    public boolean allocate(Process process) {
        return false;
    }

    @Override
    public boolean deallocate(Process process) {
        return false;
    }
}
