package me.corriekay.pokegoutil.DATA.enums;

/**
 * Enum for all possible batch operations.
 */
public enum BatchOperation {
    RENAME("Rename"),
    EVOLVE("Evolve"),
    POWER_UP("Power-Up"),
    TRANSFER("Transfer"),
    FAVORITE("Toggle Favorite");

    private final String friendlyName;

    /**
     * Constructor to create a batch operation enum field.
     *
     * @param friendlyName The friendly name of that field.
     */
    BatchOperation(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }
}
