package memory;

import java.util.PriorityQueue;

public class Cache {

    private PriorityQueue<Page> pages;
    private int freePages;

    /**
     * Constructs a Cache with 32 pages
     */
    public Cache() {
        this(32);
    }

    /**
     * Constructs a Cache with the given number of Pages
     *
     * @param pages number of pages in the cache
     */
    public Cache(int pages) {
        this.pages = new PriorityQueue<>(pages);
        this.freePages = pages;
    }

    /**
     * @return the pages in the Cache
     */
    public PriorityQueue<Page> getPages() {
        return pages;
    }

    /**
     * @return the number of free pages in the cache
     */
    public int getFreePages() {
        return freePages;
    }

    public void setFreePages(int freePages) {
        this.freePages = freePages;
    }
}
