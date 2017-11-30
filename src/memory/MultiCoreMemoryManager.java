package memory;

import hardware.CPU;
import hardware.MultiCoreCPU;
import process.Process;

public interface MultiCoreMemoryManager {
    /**
     * Allocates memory to a given process.
     *
     * @param process the process that needs memory allocated
     * @return true if allocation was successful
     */
    boolean allocate(Process process);

    /**
     * Frees memory previously allocated to a given process.
     *
     * @param process the process to free all of it's memory
     * @return true if de-allocation was successful
     */
    boolean deallocate(Process process);

    /**
     * Ensures a process is in memory and loads it into a CPU's cache and registers
     *
     * @param process process to load
     * @param cpu the CPU on which to load the process
     * @return true if loading was successful
     */
    boolean load(Process process, MultiCoreCPU cpu);

    /**
     * Returns the amount of free memory in the system
     *
     * @return the amount of free memory
     */
    int getFreeMemorySize();

    /**
     * Returns the amount of total memory in the system
     *
     * @return the amount of total memory
     */
    int getTotalMemorySize();
}
