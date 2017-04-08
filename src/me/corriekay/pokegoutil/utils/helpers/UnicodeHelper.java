package me.corriekay.pokegoutil.utils.helpers;

import POGOProtos.Enums.PokemonTypeOuterClass.PokemonType;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

/** 
 * Class to Help get the Unicode character.
 * @author FernandoTBarros
 */
public enum UnicodeHelper {
    NUMBER_0(0x24EA, "0"),
    NUMBER_1(0x2460, "1"),
    NUMBER_2(0x2461, "2"),
    NUMBER_3(0x2462, "3"),
    NUMBER_4(0x2463, "4"),
    NUMBER_5(0x2464, "5"),
    NUMBER_6(0x2465, "6"),
    NUMBER_7(0x2466, "7"),
    NUMBER_8(0x2467, "8"),
    NUMBER_9(0x2468, "9"),
    NUMBER_10(0x2469, "10"),
    NUMBER_11(0x24EB, "11"),
    NUMBER_12(0x24EC, "12"),
    NUMBER_13(0x24ED, "13"),
    NUMBER_14(0x24EE, "14"),
    NUMBER_15(0x24EF, "15"),
    TYPE_BUG(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_BUG), PokemonType.POKEMON_TYPE_BUG.toString()),
    TYPE_DARK(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_DARK), PokemonType.POKEMON_TYPE_DARK.toString()),
    TYPE_DRAGON(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_DRAGON), PokemonType.POKEMON_TYPE_DRAGON.toString()),
    TYPE_ELETRIC(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_ELETRIC), PokemonType.POKEMON_TYPE_ELECTRIC.toString()),
    TYPE_FAIRY(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_FAIRY), PokemonType.POKEMON_TYPE_FAIRY.toString()),
    TYPE_FIGHTING(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_FIGHTING), PokemonType.POKEMON_TYPE_FIGHTING.toString()),
    TYPE_FIRE(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_FIRE), PokemonType.POKEMON_TYPE_FIRE.toString()),
    TYPE_FLYING(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_FLYING), PokemonType.POKEMON_TYPE_FLYING.toString()),
    TYPE_GHOST(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_GHOST), PokemonType.POKEMON_TYPE_GHOST.toString()),
    TYPE_GRASS(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_GRASS), PokemonType.POKEMON_TYPE_GRASS.toString()),
    TYPE_GROUND(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_GROUND), PokemonType.POKEMON_TYPE_GROUND.toString()),
    TYPE_ICE(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_ICE), PokemonType.POKEMON_TYPE_ICE.toString()),
    TYPE_NORMAL(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_NORMAL), PokemonType.POKEMON_TYPE_NORMAL.toString()),
    TYPE_POISON(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_POISON), PokemonType.POKEMON_TYPE_POISON.toString()),
    TYPE_PSYCHIC(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_PSYCHIC), PokemonType.POKEMON_TYPE_PSYCHIC.toString()),
    TYPE_ROCK(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_ROCK), PokemonType.POKEMON_TYPE_ROCK.toString()),
    TYPE_STEEL(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_STEEL), PokemonType.POKEMON_TYPE_STEEL.toString()),
    TYPE_WATER(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_TYPE_WATER), PokemonType.POKEMON_TYPE_WATER.toString()),
    SHIELD(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_SHIELD), "shield"),
    SWORD(ConfigNew.getConfig().getInt(ConfigKey.UNICODE_ICON_SWORD), "sword"),
    FEMALE(0x2640, "female"),
    MALE(0x2642, "male"),
    NONE(0x2260, "none");

    private int hexaCode;
    private String identifier;

    /** 
     * Default constructor.
     * @param paramHexaCode Unicode character of this Enum
     * @param paramIdentifier String identifier of this Enum 
     */
    UnicodeHelper(final int paramHexaCode, final String paramIdentifier) {
        this.hexaCode = paramHexaCode;
        this.identifier = paramIdentifier;
    }

    /** 
     * Method to get Unicode character based on String.
     * @param identifier String to identify the Unicode Character that should be
     *        returned
     * @return unicode character in String 
     */
    public static String get(final String identifier) {
        for (final UnicodeHelper uni : values()) {
            if (uni.identifier.equals(identifier)) {
                return Character.toString((char) uni.hexaCode);
            }
        }
        return "";
    }
}
