package me.corriekay.pokegoutil.data.enums;

/**
 * An enum containing all different login types.
 */
public enum LoginType {
    GOOGLE_AUTH,
    GOOGLE_APP_PASSWORD,
    PTC,

    ALL,
    NONE;

    /**
     * Checks if the given login type is a google login.
     *
     * @param loginType The given login type.
     * @return Weather or not the login type is google.
     */
    public static boolean isGoogle(final LoginType loginType) {
        return loginType == GOOGLE_APP_PASSWORD || loginType == GOOGLE_AUTH;
    }
}
