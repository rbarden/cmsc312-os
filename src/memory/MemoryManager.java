package memory;

import process.Process;

public interface MemoryManager {
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
