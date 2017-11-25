package memory;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Memory {

    private PriorityQueue<Page> pages;
    private ArrayList<Page> freePages;
    private int freePagesSize;

    /**
     * Constructs a Main Memory with 4096 pages
     */
    public Memory() {
        this(32);
    }

    /**
     * Constructs a Main Memory with the given number of Pages
     *
     * @param pages number of pages in the memory
     */
    public Memory(int pages) {
        this.pages = new PriorityQueue<>(pages);
        this.freePages = new ArrayList<>(pages);
        
        for (int i = 0; i < pages; i++) {
            this.pages.add(new Page(i));
        }

        this.freePages.addAll(this.pages);

        this.freePagesSize = pages;
    }

    /**
     * @return the pages in the memory
     */
    public PriorityQueue<Page> getPages() {
        return pages;
    }

    /**
     * @return the number of free pages in the memory
     */
    public ArrayList<Page> getFreePages() {
        return freePages;
    }

    public void setFreePages(ArrayList<Page> freePages) {
        this.freePages = freePages;
    }

    public int getFreePagesSize() {
        return freePagesSize;
    }

    public void setFreePagesSize(int freePagesSize) {
        this.freePagesSize = freePagesSize;
    }
}
