package me.corriekay.pokegoutil.utils.helpers;

import POGOProtos.Enums.PokemonTypeOuterClass.PokemonType;

/** Class to Help get the Unicode character.
 * @author Fernando */
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
    TYPE_BUG(0x2042, PokemonType.POKEMON_TYPE_BUG.toString()),
    TYPE_DARK(0x263D, PokemonType.POKEMON_TYPE_DARK.toString()),
    TYPE_DRAGON(0x26E9, PokemonType.POKEMON_TYPE_DRAGON.toString()),
    TYPE_ELETRIC(0x2607, PokemonType.POKEMON_TYPE_ELECTRIC.toString()),
    TYPE_FAIRY(0x2764, PokemonType.POKEMON_TYPE_FAIRY.toString()),
    TYPE_FIGHTING(0x270A, PokemonType.POKEMON_TYPE_FIGHTING.toString()),
    TYPE_FIRE(0x2668, PokemonType.POKEMON_TYPE_FIRE.toString()),
    TYPE_FLYING(0x2708, PokemonType.POKEMON_TYPE_FLYING.toString()),
    TYPE_GHOST(0x26B0, PokemonType.POKEMON_TYPE_GHOST.toString()),
    TYPE_GRASS(0x2E19, PokemonType.POKEMON_TYPE_GRASS.toString()),
    TYPE_GROUND(0x26F0, PokemonType.POKEMON_TYPE_GROUND.toString()),
    TYPE_ICE(0x2744, PokemonType.POKEMON_TYPE_ICE.toString()),
    TYPE_NORMAL(0x2734, PokemonType.POKEMON_TYPE_NORMAL.toString()),
    TYPE_POISON(0x2620, PokemonType.POKEMON_TYPE_POISON.toString()),
    TYPE_PSYCHIC(0x269B, PokemonType.POKEMON_TYPE_PSYCHIC.toString()),
    TYPE_ROCK(0x25C9, PokemonType.POKEMON_TYPE_ROCK.toString()),
    TYPE_STEEL(0x26D3, PokemonType.POKEMON_TYPE_STEEL.toString()),
    TYPE_WATER(0x26C6, PokemonType.POKEMON_TYPE_WATER.toString()),
    SHIELD(0x26E8, "shield"),
    SWORD(0x2694, "sword");

    private int hexaCode;
    private String identifier;

    /** Default constructor.
     * @param paramHexaCode Unicode character of this Enum
     * @param paramIdentifier String identifier of this Enum */
    UnicodeHelper(int paramHexaCode, String paramIdentifier) {
        this.hexaCode = paramHexaCode;
        this.identifier = paramIdentifier;
    }

    /** Method to get Unicode character based on String.
     * @param identifier String to identify the Unicode Character that should be
     *        returned
     * @return unicode character in String */
    public static String get(String identifier) {
        for (UnicodeHelper uni : values()) {
            if (uni.identifier.equals(identifier)) {
                return Character.toString((char) uni.hexaCode);
            }
        }
        return "";
    }
}
