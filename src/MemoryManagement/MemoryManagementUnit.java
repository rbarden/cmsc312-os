package MemoryManagement;

public class MemoryManagementUnit implements MemoryManager {

    int memoryLimit;

    @Override
    public boolean allocate() {
        return false;
    }

    @Override
    public boolean deallocate() {
        return false;
    }
}
