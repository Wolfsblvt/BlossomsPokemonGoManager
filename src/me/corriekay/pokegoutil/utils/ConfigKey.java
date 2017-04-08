package me.corriekay.pokegoutil.utils;

import javax.swing.SortOrder;

/**
 * Enum class that handle all the keys and the default values in config.json file.
 */
public enum ConfigKey {

    DEVELOPFLAG("develop", false, Type.BOOLEAN),

    LOGIN_SAVE_AUTH("login.saveAuth", false, Type.BOOLEAN),
    LOGIN_LAST_TYPE("login.lastType", null, Type.STRING),
    LOGIN_GOOGLE_AUTH_TOKEN("login.google.authToken", null, Type.STRING),
    LOGIN_GOOGLE_APP_USERNAME("login.google.username", null, Type.STRING),
    LOGIN_GOOGLE_APP_PASSWORD("login.google.password", null, Type.STRING),
    LOGIN_PTC_USERNAME("login.ptc.username", null, Type.STRING),
    LOGIN_PTC_PASSWORD("login.ptc.password", null, Type.STRING),
    LOGIN_POKEHASHKEY("login.pokehash.key", null, Type.STRING),

    WINDOW_WIDTH("options.window.width", 800, Type.INTEGER),
    WINDOW_HEIGHT("options.window.height", 650, Type.INTEGER),
    WINDOW_POS_X("options.window.posx", 0, Type.INTEGER),
    WINDOW_POS_Y("options.window.posy", 0, Type.INTEGER),
    WINDOW_MAXIMIZE("options.window.maximize", false, Type.BOOLEAN),

    SORT_ENUM_1("options.sort.1.column", null, Type.STRING),
    SORT_ORDER_1("options.sort.1.order", SortOrder.ASCENDING.toString(), Type.STRING),
    SORT_ENUM_2("options.sort.2.column", null, Type.STRING),
    SORT_ORDER_2("options.sort.2.order", SortOrder.DESCENDING.toString(), Type.STRING),

    RENAME_PATTERN("options.pattern.rename", "", Type.STRING),

    // used by PokemonTableController, FXML stuff... is that actually used?
    COLUMN_ORDER_POKEMON_TABLE("options.column.pokemontable", null, Type.STRING),
    // used by the methods in PokemonTable.java itself (poor man's solution)
    POKEMONTABLE_COLUMNORDER("options.pokemontable.columnorder", null, Type.STRING),

    TRANSFER_AFTER_EVOLVE("settings.transferAfterEvolve", false, Type.BOOLEAN),
    SHOW_BULK_POPUP("settings.popupAfterBulk", true, Type.BOOLEAN),
    INCLUDE_FAMILY("settings.includeFamily", true, Type.BOOLEAN),
    ALTERNATIVE_IV_CALCULATION("settings.alternativeIvCalculation", false, Type.BOOLEAN),
    HP_SORT_ON_FULL("settings.sortOnFullHP", true, Type.BOOLEAN),
    
    LANGUAGE("options.lang", "en", Type.STRING),
    FONT_SIZE("options.font.size", 12, Type.INTEGER),
    ROW_PADDING("options.row.padding", 3, Type.INTEGER),
    COLOR_ALPHA("options.color.alpha", 170, Type.INTEGER),
    SKIP_VERSION("options.skipversion", null, Type.STRING),

    DELAY_RENAME_MIN("delay.rename.min", 1000, Type.INTEGER),
    DELAY_RENAME_MAX("delay.rename.max", 5000, Type.INTEGER),
    DELAY_TRANSFER_MIN("delay.transfer.min", 1000, Type.INTEGER),
    DELAY_TRANSFER_MAX("delay.transfer.max", 5000, Type.INTEGER),
    DELAY_EVOLVE_MIN("delay.evolve.min", 3000, Type.INTEGER),
    DELAY_EVOLVE_MAX("delay.evolve.max", 12000, Type.INTEGER),
    DELAY_POWERUP_MIN("delay.powerUp.min", 1000, Type.INTEGER),
    DELAY_POWERUP_MAX("delay.powerUp.max", 5000, Type.INTEGER),
    DELAY_FAVORITE_MIN("delay.favorite.min", 1000, Type.INTEGER),
    DELAY_FAVORITE_MAX("delay.favorite.max", 3000, Type.INTEGER),

    DEVICE_INFO_USE_CUSTOM("deviceInfo.useCustom", false, Type.BOOLEAN),
    DEVICE_INFO_CUSTOM_ANDROID_BOARD_NAME("deviceInfo.custom.android.boardName", null, Type.STRING),
    DEVICE_INFO_CUSTOM_ANDROID_BOOTLOADER("deviceInfo.custom.android.bootloader", null, Type.STRING),
    DEVICE_INFO_CUSTOM_DEVICE_BRAND("deviceInfo.custom.device.brand", null, Type.STRING),
    DEVICE_INFO_CUSTOM_DEVICE_ID("deviceInfo.custom.device.id", null, Type.STRING),
    DEVICE_INFO_CUSTOM_DEVICE_MODEL("deviceInfo.custom.device.model", null, Type.STRING),
    DEVICE_INFO_CUSTOM_DEVICE_MODELBOOT("deviceInfo.custom.device.modelBoot", null, Type.STRING),
    DEVICE_INFO_CUSTOM_DEVICE_MODELIDENTIFIER("deviceInfo.custom.device.modelIdentifier", null, Type.STRING),
    DEVICE_INFO_CUSTOM_FIRMWARE_BRAND("deviceInfo.custom.firmware.brand", null, Type.STRING),
    DEVICE_INFO_CUSTOM_FIRMWARE_FINGERPRINT("deviceInfo.custom.firmware.fingerprint", null, Type.STRING),
    DEVICE_INFO_CUSTOM_FIRMWARE_TAGS("deviceInfo.custom.firmware.tags", null, Type.STRING),
    DEVICE_INFO_CUSTOM_FIRMWARE_TYPE("deviceInfo.custom.firmware.type", null, Type.STRING),
    DEVICE_INFO_CUSTOM_HARDWARE_MANUFACUTER("deviceInfo.custom.hardware.manufacturer", null, Type.STRING),
    DEVICE_INFO_CUSTOM_HARDWARE_MODEL("deviceInfo.custom.hardware.model", null, Type.STRING),

    //Default unicode icons for UnicodeHelper class, it can now be customized in config.json file
    UNICODE_ICON_TYPE_BUG("unicode.type_bug", 0x2042, Type.INTEGER),
    UNICODE_ICON_TYPE_DARK("unicode.type_dark", 0x263D, Type.INTEGER),
    UNICODE_ICON_TYPE_DRAGON("unicode.type_dragon", 0x26E9, Type.INTEGER),
    UNICODE_ICON_TYPE_ELETRIC("unicode.type_eletric", 0x2607, Type.INTEGER),
    UNICODE_ICON_TYPE_FAIRY("unicode.type_fairy", 0x2764, Type.INTEGER),
    UNICODE_ICON_TYPE_FIGHTING("unicode.type_fighting", 0x270A, Type.INTEGER),
    UNICODE_ICON_TYPE_FIRE("unicode.type_fire", 0x2668, Type.INTEGER),
    UNICODE_ICON_TYPE_FLYING("unicode.type_flying", 0x2708, Type.INTEGER),
    UNICODE_ICON_TYPE_GHOST("unicode.type_ghost", 0x26B0, Type.INTEGER),
    UNICODE_ICON_TYPE_GRASS("unicode.type_grass", 0x2E19, Type.INTEGER),
    UNICODE_ICON_TYPE_GROUND("unicode.type_ground", 0x26F0, Type.INTEGER),
    UNICODE_ICON_TYPE_ICE("unicode.type_ice", 0x2744, Type.INTEGER),
    UNICODE_ICON_TYPE_NORMAL("unicode.type_normal", 0x2734, Type.INTEGER),
    UNICODE_ICON_TYPE_POISON("unicode.type_poison", 0x2620, Type.INTEGER),
    UNICODE_ICON_TYPE_PSYCHIC("unicode.type_psychic", 0x269B, Type.INTEGER),
    UNICODE_ICON_TYPE_ROCK("unicode.type_rock", 0x25C9, Type.INTEGER),
    UNICODE_ICON_TYPE_STEEL("unicode.type_steel", 0x26D3, Type.INTEGER),
    UNICODE_ICON_TYPE_WATER("unicode.type_water", 0x26C6, Type.INTEGER),
    UNICODE_ICON_SHIELD("unicode.shield", 0x26E8, Type.INTEGER),
    UNICODE_ICON_SWORD("unicode.sword", 0x2694, Type.INTEGER);
    
    public final String keyName;
    public final Object defaultValue;
    public final Type type;

    /**
     * Default constructor for ConfigKey enum.
     * @param keyName name of the key
     * @param defaultValue default value of the key
     * @param type of the key
     */
    ConfigKey(final String keyName, final Object defaultValue, final Type type) {
        this.keyName = keyName;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Auxiliary method to return the defaultValue of the key.
     * @param <T> the type of the return
     * @return the default object returned
     * @throws ClassCastException when a Class can't be cast to T
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() throws ClassCastException {
        final Class<?> clazz = type.getClazz();
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
        <T> Type(final Class<T> clazz) {
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
