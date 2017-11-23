package memory;

import hardware.CPU;
import process.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Implementation of the Memory Manager interface without virtual memory.
 */
public class MMU implements MemoryManager {

    private ArrayList<Page> mainMemory;
    private ArrayList<Page> freePages;

    /**
     * Constructs an MMU with capacity 4096 main memory
     */
    public MMU() {
        this(4096);
    }

    /**
     * Constructs an MMU with the given capacity
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

    @Override
    public boolean load(Process process, CPU cpu) {
        Cache cache = cpu.getCache();
        PriorityQueue<Page> cachePages = cpu.getCache().getPages();

        ArrayList<Page> processAllocatedMemory = process.getAllocatedMemory();

        ArrayList<Page> potentialRegisterPages = new ArrayList<>(processAllocatedMemory.size());

        for (Page page : processAllocatedMemory) {
            if(new Random().nextInt(100) < 40) {
                page.setLastAccess(new Random().nextInt(1000));
                potentialRegisterPages.add(page);
                
                if (cachePages.contains(page)) continue;

                if (cache.getFreePages() > 0) {
                    cachePages.add(page);
                    cache.setFreePages(cache.getFreePages() - 1);
                    continue;
                }

                cachePages.poll();
                cachePages.add(page);
            }
        }

        Collections.shuffle(potentialRegisterPages);
        for (int i = 0; i < cpu.getRegister().getSlots().length; i++) {
            cpu.getRegister().getSlots()[i] = potentialRegisterPages.get(i);
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
