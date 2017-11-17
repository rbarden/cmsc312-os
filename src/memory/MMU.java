package memory;

import process.Process;

import java.util.ArrayList;

/**
 * Implementation of the Memory Manager interface without virtual memory.
 */
public class MMU implements MemoryManager {

    private ArrayList<Page> mainMemory;
    private ArrayList<Page> freePages;

    /**
     * Constructs an MMU with initial capacity 4096
     */
    public MMU() {
        this(4096);
    }

    /**
     * Constructs an MMU with the given initial capacity
     *
     * @param capacity capacity of main memory in "memory units"
     */
    public MMU(int capacity) {
        this.mainMemory = new ArrayList<>(capacity);
        for(int i = 0; i < capacity; i++) {
            mainMemory.add(new Page(i));
        }

        this.freePages = new ArrayList<>(capacity);
        freePages.addAll(mainMemory);
    }

    @Override
    public boolean allocate(Process process) {
        int memoryNeeded = process.getProcessMemory();

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

    @Override
    public int getFreeMemorySize() {
        return freePages.size();
    }

    @Override
    public int getTotalMemorySize() {
        return mainMemory.size();
    }
}
