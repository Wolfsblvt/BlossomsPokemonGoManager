package me.corriekay.pokegoutil.utils;

/**
 * An auto-incrementer that gives save increment numbers.
 */
public class AutoIncrementer {
    private int nextId;

    /**
     * Creates an auto-incrementer with starting index 0.
     */
    public AutoIncrementer() {
        this.nextId = 0;
    }

    /**
     * Creates an auto-incrementer with given starting index.
     *
     * @param start The starting index.
     */
    public AutoIncrementer(final int start) {
        this.nextId = start;
    }

    /**
     * Gets the next id of this auto-incrementer instance.
     *
     * @return The next id.
     */
    public int get() {
        final int current = nextId;
        nextId++;
        return current;
    }
}
