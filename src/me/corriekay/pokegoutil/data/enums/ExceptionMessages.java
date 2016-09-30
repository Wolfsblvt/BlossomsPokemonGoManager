package me.corriekay.pokegoutil.data.enums;

import me.corriekay.pokegoutil.utils.StringLiterals;

/**
 * An enum containing the possible Exception messages that can be logged.
 */
public enum ExceptionMessages {
    COULD_NOT_READ("Could not read file"),
    COULD_NOT_SAVE("Could not save file"),
    COULD_NOT_QUERY_LOCATION("Could query location"),
    COULD_NOT_LOAD_LOCATIONS("Locations couldn't be loaded");


    private String message;

    /**
     * Internal constructor of the enum elements.
     *
     * @param message The enum message.
     */
    ExceptionMessages(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message + StringLiterals.DOT;
    }

    /**
     * Returns the exception message for given exception.
     *
     * @param exception The exception message.
     * @return The text with exception included.
     */
    public String with(final Exception exception) {
        return with(exception.toString());
    }

    /**
     * Returns the exception text with additional text.
     *
     * @param text The text.
     * @return The text.
     */
    public String with(final String text) {
        return this.message + StringLiterals.COLON_SEPARATOR + text + StringLiterals.DOT;
    }
}
