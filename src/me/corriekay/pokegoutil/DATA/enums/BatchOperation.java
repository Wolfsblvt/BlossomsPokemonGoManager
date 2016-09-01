package me.corriekay.pokegoutil.DATA.enums;

public enum BatchOperation {
    RENAME("Rename"),
    EVOLVE("Evolve"),
    POWER_UP("Power-Up"),
    TRANSFER("Transfer"),
    FAVORITE("Toggle Favorite");

    private final String friendlyName;

    BatchOperation(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }
}
