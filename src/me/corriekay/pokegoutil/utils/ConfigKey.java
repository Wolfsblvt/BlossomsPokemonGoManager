package me.corriekay.pokegoutil.utils;

import javax.swing.*;

public enum ConfigKey {

    DEVELOPFLAG("develop", false, Boolean.class),

    LOGIN_SAVE_AUTH("login.SaveAuth", false, Boolean.class),
    LOGIN_GOOGLE_AUTH_TOKEN("login.GoogleAuthToken", null, String.class),
    LOGIN_PTC_USERNAME("login.PTCUsername", null, String.class),
    LOGIN_PTC_PASSWORD("login.PTCPassword", null, String.class),

    WINDOW_WIDTH("options.window.width", 800, Integer.class),
    WINDOW_HEIGHT("options.window.height", 650, Integer.class),
    WINDOW_POS_X("options.window.posx", 0, Integer.class),
    WINDOW_POS_Y("options.window.posy", 0, Integer.class),

    SORT_COLINDEX_1("options.sort.1.colIndex", 0, Integer.class),
    SORT_ORDER_1("options.sort.1.order", SortOrder.ASCENDING.toString(), String.class),
    SORT_COLINDEX_2("options.sort.2.colIndex", 12, Integer.class),
    SORT_ORDER_2("options.sort.2.order", SortOrder.DESCENDING.toString(), String.class),

    RENAME_PATTERN("options.pattern.rename", "", String.class),
    
    COLUMN_ORDER_POKEMON_TABLE("options.column.pokemontable", null, String.class),

    TRANSFER_AFTER_EVOLVE("settings.transferAfterEvolve", false, Boolean.class),
    SHOW_BULK_POPUP("settings.popupAfterBulk", true, Boolean.class),
    INCLUDE_FAMILY("settings.includeFamily", true, Boolean.class),
    ALTERNATIVE_IV_CALCULATION("settings.alternativeIvCalculation", false, Boolean.class),

    LANGUAGE("options.lang", "en", String.class),
    FONT_SIZE("options.fontsize", 0, Integer.class),

    DELAY_RENAME_MIN("delay.rename.min", 1000, Integer.class),
    DELAY_RENAME_MAX("delay.rename.max", 5000, Integer.class),
    DELAY_TRANSFER_MIN("delay.transfer.min", 1000, Integer.class),
    DELAY_TRANSFER_MAX("delay.transfer.max", 5000, Integer.class),
    DELAY_EVOLVE_MIN("delay.evolve.min", 3000, Integer.class),
    DELAY_EVOLVE_MAX("delay.evolve.max", 12000, Integer.class),
    DELAY_POWERUP_MIN("delay.powerUp.min", 1000, Integer.class),
    DELAY_POWERUP_MAX("delay.powerUp.max", 5000, Integer.class),
    DELAY_FAVORITE_MIN("delay.favorite.min", 1000, Integer.class),
    DELAY_FAVORITE_MAX("delay.favorite.max", 3000, Integer.class),;

    public final String keyName;
    private Object defaultValue;
    private Class<?> cls;

    private <T> ConfigKey(String keyName, Object defaultValue, Class<T> cls) {
        this.keyName = keyName;
        this.defaultValue = defaultValue;
        this.cls = cls;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() throws ClassCastException {
        return (T) cls.cast(defaultValue);
    }
}
