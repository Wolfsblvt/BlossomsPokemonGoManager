package me.corriekay.pokegoutil.data.enums;

/**
 * Enum for all possible batch operations.
 */
public enum BatchOperation {
    RENAME("Rename"),
    EVOLVE("Evolve"),
    POWER_UP("Power-Up"),
    TRANSFER("Transfer"),
    FAVORITE("Toggle Favorite"),
    SET_FAVORITE_YES("Set Favorite Yes"),
    SET_FAVORITE_NO("Set Favorite No");

    private final String friendlyName;

    /**
     * Constructor to create a batch operation enum field.
     *
     * @param name The friendly name of that field.
     */
    BatchOperation(final String name) {
        this.friendlyName = name;
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }
}
