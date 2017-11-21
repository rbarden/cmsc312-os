package memory;

import process.Process;

import java.util.ArrayList;

public class MMUVirtual implements MemoryManager {

    private ArrayList<Page> virtualMemory;
    private ArrayList<Page> mainMemory;
    private ArrayList<Page> freePages;


    /**
     * Constructs an MMU with virtual memory with capacity 8912 virtual and 4096 main memories.
     */
    public MMUVirtual() {
        this(8912, 4096);
    }

    /**
     * Constructs an MMU with virtual memory with the given capacities.
     *
     * @param virtualCapacity capacity of virtual memory
     * @param mainCapacity capacity of main memory
     */
    public MMUVirtual(int virtualCapacity, int mainCapacity) {
        this.virtualMemory = new ArrayList<>(virtualCapacity);
        for(int i = 0; i < virtualCapacity; i++) {
            virtualMemory.add(new Page(i));
        }

        this.freePages = new ArrayList<>(virtualCapacity);
        freePages.addAll(mainMemory);

        this.mainMemory = new ArrayList<>(mainCapacity);
    }

    @Override
    public int getFreeMemorySize() {
        return freePages.size();
    }

    @Override
    public int getTotalMemorySize() {
        return virtualMemory.size();
    }

    @Override
    public boolean allocate(Process process) {
        int memoryNeeded = process.getProcessMemorySize();

        if(memoryNeeded > getFreeMemorySize()) return false;

        ArrayList<Page> memoryToAllocate = new ArrayList<>();
        for(int i = 0; i < memoryNeeded; i++) {
            Page page = freePages.remove(0);
            page.setProcess(process);
            memoryToAllocate.add(page);
        }
        process.setAllocatedMemory(memoryToAllocate);

        return true;
    }

    @Override
    public boolean deallocate(Process process) {
        for(Page page : process.getAllocatedMemory()) {
            page.setProcess(null);
            freePages.add(page);
        }
        return true;
    }
}
