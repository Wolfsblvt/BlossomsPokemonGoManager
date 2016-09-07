package me.corriekay.pokegoutil.utils;

import javax.swing.*;

public enum ConfigKey {

    DEVELOPFLAG("develop", false, Type.BOOLEAN),

    LOGIN_SAVE_AUTH("login.SaveAuth", false, Type.BOOLEAN),
    LOGIN_GOOGLE_AUTH_TOKEN("login.GoogleAuthToken", null, Type.STRING),
    LOGIN_PTC_USERNAME("login.PTCUsername", null, Type.STRING),
    LOGIN_PTC_PASSWORD("login.PTCPassword", null, Type.STRING),

    WINDOW_WIDTH("options.window.width", 800, Type.INTEGER),
    WINDOW_HEIGHT("options.window.height", 650, Type.INTEGER),
    WINDOW_POS_X("options.window.posx", 0, Type.INTEGER),
    WINDOW_POS_Y("options.window.posy", 0, Type.INTEGER),

    SORT_COLINDEX_1("options.sort.1.colIndex", 0, Type.INTEGER),
    SORT_ORDER_1("options.sort.1.order", SortOrder.ASCENDING.toString(), Type.STRING),
    SORT_COLINDEX_2("options.sort.2.colIndex", 12, Type.INTEGER),
    SORT_ORDER_2("options.sort.2.order", SortOrder.DESCENDING.toString(), Type.STRING),

    RENAME_PATTERN("options.pattern.rename", "", Type.STRING),

    COLUMN_ORDER_POKEMON_TABLE("options.column.pokemontable", null, Type.STRING),

    TRANSFER_AFTER_EVOLVE("settings.transferAfterEvolve", false, Type.BOOLEAN),
    SHOW_BULK_POPUP("settings.popupAfterBulk", true, Type.BOOLEAN),
    INCLUDE_FAMILY("settings.includeFamily", true, Type.BOOLEAN),
    ALTERNATIVE_IV_CALCULATION("settings.alternativeIvCalculation", false, Type.BOOLEAN),

    LANGUAGE("options.lang", "en", Type.STRING),
    FONT_SIZE("options.fontsize", 0, Type.INTEGER),

    DELAY_RENAME_MIN("delay.rename.min", 1000, Type.INTEGER),
    DELAY_RENAME_MAX("delay.rename.max", 5000, Type.INTEGER),
    DELAY_TRANSFER_MIN("delay.transfer.min", 1000, Type.INTEGER),
    DELAY_TRANSFER_MAX("delay.transfer.max", 5000, Type.INTEGER),
    DELAY_EVOLVE_MIN("delay.evolve.min", 3000, Type.INTEGER),
    DELAY_EVOLVE_MAX("delay.evolve.max", 12000, Type.INTEGER),
    DELAY_POWERUP_MIN("delay.powerUp.min", 1000, Type.INTEGER),
    DELAY_POWERUP_MAX("delay.powerUp.max", 5000, Type.INTEGER),
    DELAY_FAVORITE_MIN("delay.favorite.min", 1000, Type.INTEGER),
    DELAY_FAVORITE_MAX("delay.favorite.max", 3000, Type.INTEGER),;

    public final String keyName;
    public final Object defaultValue;
    public final Type type;

    ConfigKey(String keyName, Object defaultValue, Type type) {
        this.keyName = keyName;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() throws ClassCastException {
        Class<?> clazz = type.getClazz();
        return (T) clazz.cast(defaultValue);
    }

    /**
     * Enum which provides the default classes for the ConfigKeys.
     */
    enum Type {
        BOOLEAN(Boolean.class),
        STRING(String.class),
        INTEGER(Integer.class),
        DOUBLE(Double.class);

        private Class<?> clazz;

        /**
         * Constructor to create one element of this enum.
         *
         * @param clazz The class to parse.
         * @param <T>   Type of the class, also return type.
         */
        <T> Type(Class<T> clazz) {
            this.clazz = clazz;
        }

        /**
         * Gets the class.
         *
         * @return The class type which can be used for casting.
         */
        public Class<?> getClazz() {
            return this.clazz;
        }
    }
}
