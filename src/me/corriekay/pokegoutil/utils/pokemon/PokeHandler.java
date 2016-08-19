package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.api.pokemon.PokemonMoveMetaRegistry;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.PokeNames;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeHandler {

    private ArrayList<Pokemon> mons;

    public PokeHandler(Pokemon pokemon) {
        this(new Pokemon[]{pokemon});
    }

    public PokeHandler(Pokemon[] pokemon) {
        this(Arrays.asList(pokemon));
    }

    public PokeHandler(List<Pokemon> pokemon) {
        mons = new ArrayList<>(pokemon);
    }

    // region Static helper methods to handle Pokémon

    public static PokeNick generatePokemonNickname(String pattern, Pokemon pokemon) {
        PokeNick nick = new PokeNick(pattern, pokemon);
        return nick;
    }

    /**
     * Rename a single Pokemon based on a pattern
     *
     * @param pattern The pattern to use for renaming
     * @param pokemon The Pokemon to rename
     * @return The result of type <c>NicknamePokemonResponse.Result</c>
     */
    public static NicknamePokemonResponse.Result renameWithPattern(String pattern, Pokemon pokemon) {
        PokeNick pokeNick = generatePokemonNickname(pattern, pokemon);

        if (pokeNick.equals(pokemon.getNickname())) {
            // Why renaming to the same nickname?
            return NicknamePokemonResponse.Result.UNSET; // We need to use UNSET here. No chance to extend the enum
        }

        // Actually renaming the Pokémon with the calculated nickname
        try {
            NicknamePokemonResponse.Result result = pokemon.renamePokemon(pokeNick.toString());
            return result;
        } catch (LoginFailedException | RemoteServerException e) {
            System.out.println("Error while renaming " + getLocalPokeName(pokemon) + "(" + pokemon.getNickname() + ")! " + Utilities.getRealExceptionMessage(e));
            return NicknamePokemonResponse.Result.UNRECOGNIZED;
        }
    }

    /**
     * Returns the Name for the Pokémon with <c>id</c> in the current language.
     *
     * @param id The Pokémon ID
     * @return The translated Pokémon name
     */
    public static String getLocalPokeName(int id) {
        // TODO: change call to getConfigItem to config class once implemented
        String lang = ConfigNew.getConfig().getString(ConfigKey.LANGUAGE);

        Locale locale;
        String[] langar = lang.split("_");
        if (langar.length == 1) {
            locale = new Locale(langar[0]);
        } else {
            locale = new Locale(langar[0], langar[1]);
        }

        String name = PokeNames.getDisplayName(id, locale);
        // For non-latin
        if (!Utilities.isLatin(name)) {
             return name;
        }

        try {
            name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Returns the Name of the Pokémon <c>pokemon</c> in the current language.
     *
     * @param pokemon The Pokémon
     * @return The translated Pokémon name
     */
    public static String getLocalPokeName(Pokemon pokemon) {
        return getLocalPokeName(pokemon.getPokemonId().getNumber());
    }

    // endregion

    /**
     * Rename a bunch of Pokemon based on a pattern
     *
     * @param pattern         The pattern to use for renaming
     * @param perPokeCallback Will be called for each Pokémon that has been (tried) to
     *                        rename.
     * @return A <c>LinkedHashMap</c> with each Pokémon as key and the result as
     * value.
     */
    public LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> bulkRenameWithPattern(String pattern,
                                                                                        BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback) {

        LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> results = new LinkedHashMap<>();

        mons.forEach(p -> {
            NicknamePokemonResponse.Result result = renameWithPattern(pattern, p);
            if (perPokeCallback != null) {
                perPokeCallback.accept(result, p);
            }
            results.put(p, result);
        });

        return results;
    }

    public void bulkRenameWithPattern(String pattern) {
        bulkRenameWithPattern(pattern, null);
    }

    /**
     * This enum represents the definition of placeholder strings for Pokémon
     * renaming. To add a new placeholder rule, add a new enum constant, pass a
     * friendly Name as constructor parameter and override the <c>get</c> method
     * to return what should be the replacement for the enum. The enum constant
     * serves as placeholder.
     * <p>
     * Example: String "%cp%_%name%" for a 200cp Magikarp will result in a
     * renaming of that Magikarp to "200_Magicarp".
     */
    public enum ReplacePattern {
        NICK("Nickname") {
            @Override
            public String get(Pokemon p) {
                return p.getNickname();
            }
        },
        NAME("Pokémon Name") {
            @Override
            public String get(Pokemon p) {
                return getLocalPokeName(p);
            }
        },
        NAME_4("Pokémon Name (First four letters)") {
            @Override
            public String get(Pokemon p) {
                String name = getLocalPokeName(p);
                return (name.length() <= 4) ? name : name.substring(0, 3) + ".";
            }
        },
        NAME_6("Pokémon Name (First six letters)") {
            @Override
            public String get(Pokemon p) {
                String name = getLocalPokeName(p);
                return (name.length() <= 4) ? name : name.substring(0, 5) + ".";
            }
        },
        NAME_8("Pokémon Name (First eight letters)") {
            @Override
            public String get(Pokemon p) {
                String name = getLocalPokeName(p);
                return (name.length() <= 8) ? name : name.substring(0, 7) + ".";
            }
        },
        CP("Combat Points") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getCp());
            }
        },
        CP_EVOLVED("CP if pokemon was fully evolved (equals %cp% for highest species in the family)") {
            @Override
            public String get(Pokemon p) {
                PokemonFamilyId familyId = p.getPokemonFamily();
		PokemonId highestFamilyId = PokemonMetaRegistry.getHightestForFamily(familyId);
		int iv_attack = p.getIndividualAttack();
		int iv_defense = p.getIndividualDefense();
		int iv_stamina = p.getIndividualStamina();
		float level = p.getLevel();
                if (familyId.getNumber() == PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
                    if (p.getPokemonId().getNumber() == PokemonId.EEVEE.getNumber()) {
                        PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonId.VAPOREON);
                        PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonId.FLAREON);
                        PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonId.JOLTEON);
                        if (vap != null && fla != null && jol != null) {
                            Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                                int comb1 = PokemonCpUtils.getCpForPokemonLevel(m1.getBaseAttack() + iv_attack, m1.getBaseDefense() + iv_defense, m1.getBaseStamina() + iv_stamina, level);
                                int comb2 = PokemonCpUtils.getCpForPokemonLevel(m2.getBaseAttack() + iv_attack, m2.getBaseDefense() + iv_defense, m2.getBaseStamina() + iv_stamina, level);
                                return comb1 - comb2;
                            };
                            highestFamilyId = PokemonId.forNumber(Collections.max(Arrays.asList(vap, fla, jol), cMeta).getNumber());
                        }
                    } else {
                        // This is one of the eeveelutions, so PokemonMetaRegistry.getHightestForFamily() returns Eevee.
                        // We correct that here
                        highestFamilyId = p.getPokemonId();
		    }
		}
		/** 
		 * This calculation is redundant for pokemon already evolved, but as
		 * rename has delays anyway, this won't hurt performance.
		 */
		PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
		int attack = highestFamilyMeta.getBaseAttack() + iv_attack;
		int defense = highestFamilyMeta.getBaseDefense() + iv_defense;
		int stamina = highestFamilyMeta.getBaseStamina() + iv_stamina;
		return String.valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, level));
	    }
        },
        HP("Hit Points") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getMaxStamina());
            }
        },
        LEVEL("Pokémon Level") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getLevel());
            }
        },
        IV_RATING("IV Rating in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                return Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p));
            }
        },
        IV_HEX("IV Values in hexadecimal, like \"9FA\" (F = 15)") {
            @Override
            public String get(Pokemon p) {
                return (Integer.toHexString(p.getIndividualAttack()) + Integer.toHexString(p.getIndividualDefense()) + Integer.toHexString(p.getIndividualStamina())).toUpperCase();
            }
        },
        IV_ATT("IV Attack") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getIndividualAttack());
            }
        },
        IV_DEF("IV Defense") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getIndividualDefense());
            }
        },
        IV_STAM("IV Stamina") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getIndividualStamina());
            }
        },
        DUEL_ABILITY_RATING("Duel Ability in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                long duelAbility = PokemonUtils.duelAbility(p, false);
                return Utilities.percentageWithTwoCharacters(duelAbility, PokemonUtils.DUEL_ABILITY_MAX);
            }
        },
        GYM_OFFENSE_RATING("Gym Offense in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                double gymOffense = PokemonUtils.gymOffense(p, false);
                return Utilities.percentageWithTwoCharacters(gymOffense, PokemonUtils.GYM_OFFENSE_MAX);
            }
        },
        GYM_DEFENSE_RATING("Gym Defense in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                long gymDefense = PokemonUtils.gymDefense(p, false);
                return Utilities.percentageWithTwoCharacters(gymDefense, PokemonUtils.GYM_DEFENSE_MAX);
            }
        },
        DUEL_ABILITY_IV_RATING("Duel Ability (w IVs) in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                long duelAbility = PokemonUtils.duelAbility(p, true);
                return Utilities.percentageWithTwoCharacters(duelAbility, PokemonUtils.DUEL_ABILITY_IV_MAX);
            }
        },
        GYM_OFFENSE_IV_RATING("Gym Offense (w IVs) in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                double gymOffense = PokemonUtils.gymOffense(p, true);
                return Utilities.percentageWithTwoCharacters(gymOffense, PokemonUtils.GYM_OFFENSE_IV_MAX);
            }
        },
        GYM_DEFENSE_IV_RATING("Gym Defense (w IVs) in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                long gymDefense = PokemonUtils.gymDefense(p, true);
                return Utilities.percentageWithTwoCharacters(gymDefense, PokemonUtils.GYM_DEFENSE_IV_MAX);
            }
        },
        MAX_CP("Maximum possible CP (with Trainer Level 40)") {
            @Override
            public String get(Pokemon p) {
                PokemonMeta meta = p.getMeta();
                int attack = meta.getBaseAttack() + p.getIndividualAttack();
                int defense = meta.getBaseDefense() + p.getIndividualDefense();
                int stamina = meta.getBaseStamina() + p.getIndividualStamina();
                return String.valueOf(PokemonCpUtils.getMaxCp(attack, defense, stamina));
            }
        },
        MOVE_TYPE_1("Move 1 type (Fire)") {
            @Override
            public String get(Pokemon p) {
                String type = PokemonMoveMetaRegistry.getMeta(p.getMove1()).getType().toString();
                return StringUtils.capitalize(type.toLowerCase());
            }
        },
        MOVE_TYPE_2("Move 2 type (Fire)") {
            @Override
            public String get(Pokemon p) {
                String type = PokemonMoveMetaRegistry.getMeta(p.getMove2()).getType().toString();
                return StringUtils.capitalize(type.toLowerCase());
            }
        },
        MOVE_TYPE_1_SHORT("Move 1 abbreviated (Ghost = Gh)") {
            @Override
            public String get(Pokemon p) {
                String type = PokemonMoveMetaRegistry.getMeta(p.getMove1()).getType().toString();
                return abbreviateType(type);
            }
        },
        MOVE_TYPE_2_SHORT("Move 2 abbreviated (Ghost = Gh)") {
            @Override
            public String get(Pokemon p) {
                String type = PokemonMoveMetaRegistry.getMeta(p.getMove2()).getType().toString();
                return abbreviateType(type);
            }
        },
        DPS_1("Damage per second for Move 1") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(Math.round(PokemonUtils.dpsForMove(p, true)));
            }
        },
        DPS_2("Damage per second for Move 2") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(Math.round(PokemonUtils.dpsForMove(p, false)));
            }
        },
        DPS_1_RATING("Rating for Move 1 (Percentage of max possible) in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                return PokemonUtils.moveRating(p, true);
            }
        },
        DPS_2_RATING("Rating for Move 2 (Percentage of max possible) in two digits (XX for 100%)") {
            @Override
            public String get(Pokemon p) {
                return PokemonUtils.moveRating(p, false);
            }
        },
        TYPE_1("Pokémon Type 1 abbreviated (Ghost = Gh)") {
            @Override
            public String get(Pokemon p) {
                String type = p.getMeta().getType1().toString();
                return abbreviateType(type);
            }
        },
        TYPE_2("Pokémon Type 2 abbreviated (Ghost = Gh)") {
            @Override
            public String get(Pokemon p) {
                String type = p.getMeta().getType2().toString();
                return abbreviateType(type);
            }
        },
        ID("Pokédex Id") {
            @Override
            public String get(Pokemon p) {
                return String.valueOf(p.getPokemonId().getNumber());
            }
        };

        private static String abbreviateType(String type) {
            if (type.equalsIgnoreCase("fire") || type.equalsIgnoreCase("ground")) {
                return type.substring(0, 1).toUpperCase() + type.substring(type.length() - 1).toLowerCase();
            } else {
                return StringUtils.capitalize(type.substring(0, 2).toLowerCase());
            }
        }

        private final String friendlyName;

        ReplacePattern(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Returns the friendly name for the replacement pattern. Can be used to
         * generate explanations for possible placeholders automatically.
         */
        public String toString() {
            return friendlyName;
        }

        /**
         * This method must be overwritten and should return what should be the
         * replacement.
         *
         * @param p The Pokémon that receives the new nick name.
         * @return The value that the placeholder should be replaced with
         */
        public abstract String get(Pokemon p);
    }
}
