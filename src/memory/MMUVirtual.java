package memory;

import hardware.CPU;
import process.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class MMUVirtual implements MemoryManager {

    private Memory virtualMemory;
    private Memory mainMemory;

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
        this.virtualMemory = new Memory(virtualCapacity);
        this.mainMemory = new Memory(mainCapacity);
    }

    @Override
    public int getFreeMemorySize() {
        return virtualMemory.getFreePages().size();
    }

    @Override
    public int getTotalMemorySize() {
        return virtualMemory.getPages().size();
    }

    @Override
    public boolean allocate(Process process) {
        int memoryNeeded = process.getProcessMemorySize();
        

        ArrayList<Page> memoryToAllocate = new ArrayList<>();

        if(process.getParentProcess() != null) {
            memoryNeeded = 0;
            ArrayList<Page> parentMemory = process.getParentProcess().getAllocatedMemory();
            for(Page page: parentMemory) {
                if(new Random().nextInt(100) < 20) {
                    memoryNeeded++;
                    continue;
                }
                memoryToAllocate.add(page);
            }
        }
        
        if(memoryNeeded > getFreeMemorySize()) return false;

        for(int i = 0; i < memoryNeeded; i++) {
            Page page = virtualMemory.getFreePages().remove(0);
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
            virtualMemory.getFreePages().add(page);
        }
        return true;
    }

    @Override
    public boolean load(Process process, CPU cpu) {
        Cache cache = cpu.getCache();
        PriorityQueue<Page> cachePages = cache.getPages();

        PriorityQueue<Page> memoryPages = mainMemory.getPages();

        ArrayList<Page> processAllocatedMemory = process.getAllocatedMemory();

        ArrayList<Page> potentialRegisterPages = new ArrayList<>(processAllocatedMemory.size());

        for (Page page : processAllocatedMemory) {
            if(new Random().nextInt(100) < 40) {
                page.setLastAccess(cpu.getClock().getClock());
                potentialRegisterPages.add(page);

                if (cachePages.contains(page) && memoryPages.contains(page)) continue;

                if (!memoryPages.contains(page)) {
                    if(mainMemory.getFreePagesSize() > 0) {
                        memoryPages.add(page);
                        mainMemory.setFreePagesSize(mainMemory.getFreePagesSize() - 1);
                    }

                    memoryPages.poll();
                    memoryPages.add(page);
                }

                if(!cachePages.contains(page)) {
                    if (cache.getFreePages() > 0) {
                        cachePages.add(page);
                        cache.setFreePages(cache.getFreePages() - 1);
                        continue;
                    }

                    cachePages.poll();
                    cachePages.add(page);
                }
            }
        }

        Collections.shuffle(potentialRegisterPages);
        for (int i = 0; i < cpu.getRegister().getSlots().length; i++) {
            cpu.getRegister().getSlots()[i] = potentialRegisterPages.get(i);
        }

        return true;
    }
}
