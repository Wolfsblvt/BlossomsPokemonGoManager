package me.corriekay.pokegoutil.utils.helpers;

/**
 * Exception class to help validate IV value inside method.
 */
public class InvalidIvValueException extends Exception {

    private static final long serialVersionUID = 7145962829371471669L;

    /**
     * Default constructor to show a invalid msg on console.
     */
    public InvalidIvValueException() {
        super("Please select a valid IV value (0-100)");
    }
}
