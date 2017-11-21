package memory;

/**
 * Implementation of a CPU's registers. The register class calls each individual register a slot. Each slot holds one page.
 */
public class Register {

    private Page[] slots;

    /**
     * Constructs a Register with 4 slots
     */
    public Register() {
        this(4);
    }

    /**
     * Constructs a Register with the given number of slots
     *
     * @param slots number of slots for the register
     */
    public Register(int slots) {
        this.slots = new Page[slots];
    }

    /**
     * @return the slots in the register
     */
    public Page[] getSlots() {
        return slots;
    }

    public boolean contains(Page page) {
        for (Page slot : slots) {
            if (slot.equals(page)) return true;
        }
        return false;
    }
}
