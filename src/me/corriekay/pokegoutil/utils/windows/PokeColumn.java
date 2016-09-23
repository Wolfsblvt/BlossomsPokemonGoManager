package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;

import me.corriekay.pokegoutil.data.enums.ColumnType;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.CollectionHelper;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonValueCache;

/**
 * A class that holds data relevant for each column
 */
public enum PokeColumn {
    POKEDEX_ID(0, "#", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getMeta().getNumber();
        }
    },
    NICKNAME(1, "Nickname", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return p.getNickname();
        }
    },
    SPECIES(2, "Species", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokeHandler.getLocalPokeName(p);
        }
    },
    IV_RATING(3, "IV %", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.ivRating(p);
        }
    },
    LEVEL(4, "Lvl", ColumnType.DOUBLE) {
        @Override
        public Object get(final Pokemon p) {
            return p.getLevel();
        }
    },
    IV_ATTACK(5, "Atk", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualAttack();
        }
    },
    IV_DEFENSE(6, "Def", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualDefense();
        }
    },
    IV_STAMINA(7, "Stam", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualStamina();
        }
    },
    TYPE_1(8, "Type 1", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return StringUtils.capitalize(p.getMeta().getType1().toString().toLowerCase());
        }
    },
    TYPE_2(9, "Type 2", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return StringUtils.capitalize(p.getMeta().getType2().toString().toLowerCase().replaceAll("none", ""));
        }
    },
    MOVE_1(10, "Move 1", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            final Double dps1 = PokemonUtils.dpsForMove(p, true);
            return WordUtils.capitalize(
                p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " "))
                + " (" + String.format("%.2f", dps1) + "dps)";
        }
    },
    MOVE_2(11, "Move 2", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            final Double dps2 = PokemonUtils.dpsForMove(p, false);
            return WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " ("
                + String.format("%.2f", dps2) + "dps)";
        }
    },
    CP(12, "CP", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCp();
        }
    },
    HP(13, "HP", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getMaxStamina();
        }
    },
    MAX_CP_CUR(14, "Max CP (Cur)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            try {
                return p.getMaxCpForPlayer();
            } catch (NoSuchItemException e) {
                return -1;
            }
        }
    },
    MAX_CP_40(15, "Max CP (40)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            try {
                return p.getMaxCp();
            } catch (NoSuchItemException e) {
                return -1;
            }
        }
    },
    MAX_CP_EVOLVED_CUR(16, "Max CP Evolved (Cur)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getMaxCpFullEvolveAndPowerupForPlayer();
        }
    },
    MAX_CP_EVOLVED_40(17, "Max CP Evolved (40)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCpFullEvolveAndPowerup();
        }
    },
    CANDIES(18, "Candies", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCandy();
        }
    },
    CANDIES_TO_EVOLVE(19, "To Evolve", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            if (p.getCandiesToEvolve() != 0) {
                return String.valueOf(p.getCandiesToEvolve());
            } else {
                return "-";
            }
        }
    },
    STARDUST_TO_POWERUP(20, "Stardust", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getStardustCostsForPowerup();
        }
    },
    CAUGHT_WITH(21, "Caught With", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " "));
        }
    },
    CAUGHT_TIME(22, "Caught Time", ColumnType.DATE) {
        @Override
        public Object get(final Pokemon p) {
            return DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs()));
        }
    },
    FAVORITE(23, "Favorite", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return (p.isFavorite()) ? "Yes" : "";
        }
    },
    DUEL_ABILITY(24, "Duel Ability", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.duelAbility(p), PokemonValueCache.getHighestStats().duelAbility);
        }
    },
    GYM_OFFENSE(25, "Gym Offense", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.gymOffense(p), PokemonValueCache.getHighestStats().gymOffense);
        }
    },
    GYM_DEFENSE(26, "Gym Defense", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.gymDefense(p), PokemonValueCache.getHighestStats().gymDefense);
        }
    },
    CP_EVOLVED(27, "CP Evolved", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCpAfterFullEvolve();
        }
    },
    EVOLVABLE_COUNT(28, "Evolvable", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            if (p.getCandiesToEvolve() != 0) {
                int candies = p.getCandy();
                int candiesToEvolve = p.getCandiesToEvolve();

                int evolvable = (int) ((double) candies / candiesToEvolve);
                int rest = (candies % candiesToEvolve);
                final boolean transferAfterEvolve = ConfigNew.getConfig().getBool(ConfigKey.TRANSFER_AFTER_EVOLVE);

                // We iterate and get how many candies are added while evolving and if that can make up for some more evolves
                int newEvolvable = evolvable;
                do {
                    final int candyGiven = newEvolvable + (transferAfterEvolve ? newEvolvable : 0);
                    newEvolvable = (int) ((double) (candyGiven + rest) / candiesToEvolve);
                    evolvable = evolvable + newEvolvable;
                    rest = (candyGiven + rest) % candiesToEvolve;
                } while (newEvolvable > 0);

                return String.valueOf(evolvable);
            } else {
                return "-";
            }
        }
    },
    DUEL_ABILITY_RATING(29, "Duel Ability Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.duelAbility(p), PokemonValueCache.getStats(p.getPokemonId()).duelAbility);
        }
    },
    GYM_OFFENSE_RATING(30, "Gym Offense Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.gymOffense(p), PokemonValueCache.getStats(p.getPokemonId()).gymOffense);
        }
    },
    GYM_DEFENSE_RATING(31, "Gym Defense Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonUtils.gymDefense(p), PokemonValueCache.getStats(p.getPokemonId()).gymDefense);
        }
    };


    public final int id;
    public final String name;
    public final ColumnType columnType;
    public final ArrayList data;

    /**
     * Constructor to create the enum entries.
     *
     * @param id         The id of the column.
     * @param name       The name of the column.
     * @param columnType The type of the column.
     */
    PokeColumn(int id, String name, ColumnType columnType) {
        this.id = id;
        this.name = name;
        this.columnType = columnType;
        this.data = CollectionHelper.provideArrayList(columnType.clazz);
    }

    public static PokeColumn getForId(int id) {
        for (final PokeColumn column : PokeColumn.values()) {
            if (column.id == id) {
                return column;
            }
        }
        // If not found, we throw an exception
        throw new NoSuchElementException("There is no column with id " + id);
    }

    /**
     * Returns the comparator for the given column, based on the column type.
     *
     * @return The comparator.
     */
    public Comparator getComparator() {
        return columnType.comparator;
    }

    /**
     * Returns the table cell renderer for the given column, based on the column type.
     *
     * @return The cell renderer.
     */
    public TableCellRenderer getCellRenderer() {
        return columnType.tableCellRenderer;
    }

    /**
     * This method must be overwritten and should return what should be the
     * data that has to be displayed.
     *
     * @param p The Pokémon that of that row.
     * @return The data that has to be displayed
     */
    public abstract Object get(Pokemon p);
}
