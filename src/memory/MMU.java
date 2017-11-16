package memory;

import process.Process;

import java.util.ArrayList;

public class MMU implements MemoryManager {

    private int mainMemoryCapacity;
    private ArrayList<Page> mainMemory;
    private ArrayList<Page> freePages;

    public MMU(int mainMemoryCapacity) {
        this.mainMemory = new ArrayList<Page>(mainMemoryCapacity);
        this.mainMemoryCapacity = mainMemoryCapacity;
    }

    @Override
    public boolean allocate(Process process) {
        return false;
    }

    @Override
    public boolean deallocate(Process process) {
        return false;
    }
}
