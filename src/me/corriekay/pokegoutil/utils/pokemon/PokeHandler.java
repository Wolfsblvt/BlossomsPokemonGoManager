package me.corriekay.pokegoutil.utils.pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.request.RequestFailedException;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import me.corriekay.pokegoutil.data.enums.PokeColumn;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.TriConsumer;
import me.corriekay.pokegoutil.utils.helpers.UnicodeHelper;

public class PokeHandler {

    private final ArrayList<Pokemon> mons;

    public PokeHandler(final Pokemon pokemon) {
        this(new Pokemon[] {pokemon});
    }

    public PokeHandler(final Pokemon[] pokemon) {
        this(Arrays.asList(pokemon));
    }

    public PokeHandler(final List<Pokemon> pokemon) {
        mons = new ArrayList<>(pokemon);
    }

    // region Static helper methods to handle Pokémon

    public static PokeNick generatePokemonNickname(final String pattern, final Pokemon pokemon) {
        final PokeNick nick = new PokeNick(pattern, pokemon);
        return nick;
    }

    /**
     * Rename a single Pokemon based on a pattern
     *
     * @param pattern The pattern to use for renaming
     * @param pokemon The Pokemon to rename
     * @return The result of type <c>NicknamePokemonResponse.Result</c>
     */
    public static NicknamePokemonResponse.Result renameWithPattern(final String pattern, final Pokemon pokemon) {
        final PokeNick pokeNick = generatePokemonNickname(pattern, pokemon);

        if (pokeNick.toString().equals(pokemon.getNickname())) {
            // Why renaming to the same nickname?
            return NicknamePokemonResponse.Result.UNSET; // We need to use UNSET here. No chance to extend the enum
        }

        // Actually renaming the Pokémon with the calculated nickname
        try {
            final NicknamePokemonResponse.Result result = pokemon.renamePokemon(pokeNick.toString());
            return result;
        } catch (RequestFailedException e) {
            System.out.println("Error while renaming "
                + PokemonUtils.getLocalPokeName(pokemon) + "(" + pokemon.getNickname() + ")! "
                + Utilities.getRealExceptionMessage(e));
            return NicknamePokemonResponse.Result.UNRECOGNIZED;
        }
    }

    // endregion

    /**
     * Rename a bunch of Pokemon based on a pattern.
     *
     * @param pattern         The pattern to use for renaming
     * @param perPokeCallback Will be called for each Pokémon that has been (tried) to
     *                        rename.
     * @return A LinkedHashMap with each Pokémon as key and the result as value.
     */
    public Map<Pokemon, NicknamePokemonResponse.Result> bulkRenameWithPattern(final String pattern,
            final TriConsumer<NicknamePokemonResponse.Result, Pokemon, String> perPokeCallback) {
        final Map<Pokemon, NicknamePokemonResponse.Result> results = new LinkedHashMap<>();

        mons.forEach(p -> {
            final String previousNickname = p.getNickname();
            final NicknamePokemonResponse.Result result = renameWithPattern(pattern, p);
            if (perPokeCallback != null) {
                perPokeCallback.accept(result, p, previousNickname);
            }
            results.put(p, result);
        });

        return results;
    }

    public void bulkRenameWithPattern(final String pattern) {
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
            public String get(final Pokemon p) {
                return PokeColumn.NICKNAME.get(p).toString();
            }
        },
        NAME("Pokémon Name") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.SPECIES.get(p).toString();
            }
        },
        NAME_2("Pokémon Name (First two letters) [2]") {
            @Override
            public String get(final Pokemon p) {
                final int length = 2;
                return StringUtils.substring(PokeColumn.SPECIES.get(p).toString(), 0, length);
            }
        },
        NAME_4("Pokémon Name (First four letters) [4]") {
            @Override
            public String get(final Pokemon p) {
                final int length = 4;
                return StringUtils.substring(PokeColumn.SPECIES.get(p).toString(), 0, length);
            }
        },
        NAME_6("Pokémon Name (First six letters) [6]") {
            @Override
            public String get(final Pokemon p) {
                final int length = 6;
                return StringUtils.substring(PokeColumn.SPECIES.get(p).toString(), 0, length);
            }
        },
        NAME_8("Pokémon Name (First eight letters) [8]") {
            @Override
            public String get(final Pokemon p) {
                final int length = 8;
                return StringUtils.substring(PokeColumn.SPECIES.get(p).toString(), 0, length);
            }
        },
        CP("Combat Points") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.CP.get(p).toString();
            }
        },
        CP_EVOLVED("CP if pokemon was fully evolved (equals %cp% for highest species in the family)") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.CP_EVOLVED.get(p).toString();
            }
        },
        HP("Hit Points") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.HP.get(p).toString();
            }
        },
        LEVEL("Pokémon Level") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.LEVEL.get(p).toString();
            }
        },
        IV_RATING("IV Rating in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.IV_RATING.get(p));
            }
        },
        IV_HEX("IV Values in hexadecimal, like \"9FA\" (F = 15) [3]") {
            @Override
            public String get(final Pokemon p) {
                return (Integer.toHexString((int) PokeColumn.IV_ATTACK.get(p))
                    + Integer.toHexString((int) PokeColumn.IV_DEFENSE.get(p))
                    + Integer.toHexString((int) PokeColumn.IV_STAMINA.get(p))).toUpperCase();
            }
        },
        IV_ATT("IV Attack [2]") {
            @Override
            public String get(final Pokemon p) {
                return pad((int) PokeColumn.IV_ATTACK.get(p), 2);
            }
        },
        IV_DEF("IV Defense [2]") {
            @Override
            public String get(final Pokemon p) {
                return pad((int) PokeColumn.IV_DEFENSE.get(p), 2);
            }
        },
        IV_STAM("IV Stamina [2]") {
            @Override
            public String get(final Pokemon p) {
                return pad((int) PokeColumn.IV_STAMINA.get(p), 2);
            }
        },
        IV_ATT_UNI("IV Attack Unicode (⓯  for 15) [1]") {
            @Override
            public String get(final Pokemon p) {
                return UnicodeHelper.get(PokeColumn.IV_ATTACK.get(p).toString());
            }
        },
        IV_DEF_UNI("IV Defense Unicode (⓯  for 15) [1]") {
            @Override
            public String get(final Pokemon p) {
                return UnicodeHelper.get(PokeColumn.IV_DEFENSE.get(p).toString());
            }
        },
        IV_STAM_UNI("IV Stamina Unicode (⓯  for 15) {1]") {
            @Override
            public String get(final Pokemon p) {
                return UnicodeHelper.get(PokeColumn.IV_STAMINA.get(p).toString());
            }
        },
        DUEL_ABILITY("Duel Ability in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.DUEL_ABILITY.get(p));
            }
        },
        GYM_OFFENSE("Gym Offense in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.GYM_OFFENSE.get(p));
            }
        },
        GYM_DEFENSE("Gym Defense in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.GYM_DEFENSE.get(p));
            }
        },
        DUEL_ABILITY_RATING("Duel Ability Rating in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.DUEL_ABILITY_RATING.get(p));
            }
        },
        GYM_OFFENSE_RATING("Gym Offense Rating in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.GYM_OFFENSE_RATING.get(p));
            }
        },
        GYM_DEFENSE_RATING("Gym Defense Rating in two digits (XX for 100%) [2]") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.GYM_DEFENSE_RATING.get(p));
            }
        },
        MAX_CP("Maximum possible CP (with Trainer Level 40)") {
            @Override
            public String get(final Pokemon p) {
                return PokeColumn.MAX_CP_40.get(p).toString();
            }
        },
        MOVE_TYPE_1("Move 1 abbreviated (Ghost = Gh) [2]") {
            @Override
            public String get(final Pokemon p) {
                final String type = PokemonUtils.formatType(PokemonMeta.getMoveSettings(p.getMove1()).getPokemonType());
                return PokemonUtils.hasStab(p.getPokemonId(), p.getMove1()) ? abbreviateType(type).toUpperCase() : abbreviateType(type).toLowerCase();
            }
        },
        MOVE_TYPE_2("Move 2 abbreviated (Ghost = Gh) [2]") {
            @Override
            public String get(final Pokemon p) {
                final String type = PokemonUtils.formatType(PokemonMeta.getMoveSettings(p.getMove2()).getPokemonType());
                return PokemonUtils.hasStab(p.getPokemonId(), p.getMove2()) ? abbreviateType(type).toUpperCase() : abbreviateType(type).toLowerCase();
            }
        },
        MOVE_TYPE_1_UNI("Move 1 abbreviated (Eletric = ⚡) [1]") {
            @Override
            public String get(final Pokemon p) {
                final String type = PokemonMeta.getMoveSettings(p.getMove1()).getPokemonType().toString();
                return UnicodeHelper.get(type);
            }
        },
        MOVE_TYPE_2_UNI("Move 2 abbreviated (Eletric = ⚡) [1]") {
            @Override
            public String get(final Pokemon p) {
                final String type = PokemonMeta.getMoveSettings(p.getMove2()).getPokemonType().toString();
                return UnicodeHelper.get(type);
            }
        },
        DPS_1("Damage per second for Move 1") {
            @Override
            public String get(final Pokemon p) {
                return String.valueOf(Math.round(PokemonCalculationUtils.dpsForMove(p, true)));
            }
        },
        DPS_2("Damage per second for Move 2") {
            @Override
            public String get(final Pokemon p) {
                return String.valueOf(Math.round(PokemonCalculationUtils.dpsForMove(p, false)));
            }
        },
        MOVE_1_RATING("Rating for Move 1 (Percentage of max possible) in two digits (XX for 100%)") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.MOVE_1_RATING.get(p));
            }
        },
        MOVE_2_RATING("Rating for Move 2 (Percentage of max possible) in two digits (XX for 100%)") {
            @Override
            public String get(final Pokemon p) {
                return padPercentage((double) PokeColumn.MOVE_2_RATING.get(p));
            }
        },
        TYPE_1("Pokémon Type 1 abbreviated (Ghost = Gh) [2]") {
            @Override
            public String get(final Pokemon p) {
                final String type = p.getSettings().getType().toString();
                return abbreviateType(type);
            }
        },
        TYPE_2("Pokémon Type 2 abbreviated (Ghost = Gh) [2]") {
            @Override
            public String get(final Pokemon p) {
                final String type = p.getSettings().getType2().toString();
                return abbreviateType(type);
            }
        },
        TYPE_1_UNI("Pokémon Type 1 Unicode (Eletric = ⚡) [1]") {
            @Override
            public String get(final Pokemon p) {
                final String type = p.getSettings().getType().toString();
                return UnicodeHelper.get(type);
            }
        },
        TYPE_2_UNI("Pokémon Type 2 Unicode (Eletric = ⚡) [1]") {
            @Override
            public String get(final Pokemon p) {
                final String type = p.getSettings().getType2().toString();
                return UnicodeHelper.get(type);
            }
        },
        SWORD_UNICODE("Sword in Unicode symbol ⚔  to represent the best offensive moveset [1]") {
            @Override
            public String get(final Pokemon p) {
                return UnicodeHelper.get("sword");
            }
        },
        SHIELD_UNICODE("Shield in Unicode symbol ⛨  to represent the best defensive moveset [1]") {
            @Override
            public String get(final Pokemon p) {
                return UnicodeHelper.get("shield");
            }
        },
        ID("Pokédex Id [3]") {
            @Override
            public String get(final Pokemon p) {
                final int length = 3;
                return pad((int) PokeColumn.POKEDEX_ID.get(p), length);
            }
        };

        /**
         * Abbreviate Movement/Pokémon type, to two character.
         * "Gr" is Grass, so we make Ground "Gd". "Fi" is Fire, so we make Fighting "Fg".
         *
         * @param type The type.
         * @return The abbreviated type with two characters.
         */
        private static String abbreviateType(final String type) {
            if ("none".equalsIgnoreCase(type)) {
                return "__";
            } else if ("fighting".equalsIgnoreCase(type) || "ground".equalsIgnoreCase(type)) {
                // "Gr" is Grass, so we make Ground "Gd". "Fi" is Fire, so we make Fighting "Fg"
                return type.substring(0, 1).toUpperCase() + type.substring(type.length() - 1).toLowerCase();
            } else {
                return StringUtils.capitalize(type.substring(0, 2).toLowerCase());
            }
        }

        /**
         * Displays a number with given length. Pads if needed, makes 100 to XX if needed.
         *
         * @param number The number to pad.
         * @param length The number of characters to display.
         * @return The two-length-number.
         */
        private static String pad(final int number, final int length) {
            final int max = (int) Math.pow(10, length);
            return (number < max) ? StringUtils.leftPad(String.valueOf(number), length, '0') : StringUtils.repeat('X', length);
        }

        /**
         * Display a percentage with two characters, based on the pad() function.
         *
         * @param percentage The percentage (eg. 0.75013).
         * @return The two-length-percentage.
         */
        private static String padPercentage(final double percentage) {
            final int number = (int) Math.round(percentage * Utilities.PERCENTAGE_FACTOR);
            return pad(number, 2);
        }

        private final String friendlyName;

        ReplacePattern(final String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Returns the friendly name for the replacement pattern. Can be used to
         * generate explanations for possible placeholders automatically.
         */
        @Override
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
        public abstract String get(final Pokemon p);
    }
}
