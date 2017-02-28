package me.corriekay.pokegoutil.data.enums;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.table.TableCellRenderer;

import com.pokegoapi.api.pokemon.Evolutions;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.google.common.geometry.S2CellId;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Enums.PokemonTypeOuterClass.PokemonType;
import me.corriekay.pokegoutil.utils.AutoIncrementer;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.CollectionHelper;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.helpers.LocationHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonPerformanceCache;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.windows.renderer.CellRendererHelper;

/**
 * A class that holds data relevant for each column.
 * Cleka 19.2.2017: renamed "name" to "heading",
 * otherwise .name returns something different than .name()!
 */
public enum PokeColumn {
    AUTO_INCREMENT("Row", ColumnType.AUTO_INCREMENT) {
        @Override
        public Object get(final Pokemon p) {
            return null;
        }
    },
    POKEDEX_ID("#", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getPokemonId().getNumber();
        }
    },
    NICKNAME("Nickname", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return p.getNickname();
        }
    },
    CP("CP", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCp();
        }
    },
    SPECIES("Species", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.getLocalPokeName(p);
        }
    },
    IV_RATING("IV %", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonCalculationUtils.ivRating(p);
        }
    },
    LEVEL("Lvl", ColumnType.DOUBLE) {
        @Override
        public Object get(final Pokemon p) {
            return (double) p.getLevel();
        }
    },
    IV_ATTACK("Atk", ColumnType.INT, CellRendererHelper.IV) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualAttack();
        }
    },
    IV_DEFENSE("Def", ColumnType.INT, CellRendererHelper.IV) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualDefense();
        }
    },
    IV_STAMINA("Stam", ColumnType.INT, CellRendererHelper.IV) {
        @Override
        public Object get(final Pokemon p) {
            return p.getIndividualStamina();
        }
    },
    TYPE_1("Type 1", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.formatType(p.getSettings().getType());
        }
    },
    TYPE_2("Type 2", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.formatType(p.getSettings().getType2());
        }
    },
    MOVE_1("Move 1", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.formatMove(p.getMove1())
                + PokemonUtils.formatDps(PokemonCalculationUtils.dpsForMove(p, true));
        }
    },
    MOVE_2("Move 2", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.formatMove(p.getMove2())
                + PokemonUtils.formatDps(PokemonCalculationUtils.dpsForMove(p, false));
        }
    },
    DPS_1("DPS 1", ColumnType.DPS1VALUE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonCalculationUtils.dpsForMove(p, true);
        }
    },
    DPS_2("DPS 2", ColumnType.DPS2VALUE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonCalculationUtils.dpsForMove(p, false);
        }
    },
    MOVETYPE_1("Movetype 1", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            final PokemonMove move = p.getMove1();
            PokemonType type = PokemonMeta.getMoveSettings(move).getPokemonType();
            return PokemonUtils.formatType(type);
        }
    },
    MOVETYPE_2("Movetype 2", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            final PokemonMove move = p.getMove2();
            PokemonType type = PokemonMeta.getMoveSettings(move).getPokemonType();
            return PokemonUtils.formatType(type);
        }
    },
    HP("HP", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getMaxStamina();
        }
    },
    EVOLVABLE_COUNT("Evolvable", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            if (p.getCandiesToEvolve() > 1) {
                final int candies = p.getCandy();
                final int candiesToEvolve = p.getCandiesToEvolve();

                int evolvable = (int) ((double) candies / candiesToEvolve);
                int rest = candies % candiesToEvolve;
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
                return StringLiterals.NO_VALUE_SIGN;
            }
        }
    },
    CANDIES("Candies", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getCandy();
        }
    },
    CANDIES_TO_EVOLVE("To Evolve", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            if (p.getCandiesToEvolve() != 0) {
                return String.valueOf(p.getCandiesToEvolve());
            } else {
                return StringLiterals.NO_VALUE_SIGN;
            }
        }
    },
    STARDUST_TO_POWERUP("Stardust", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            return p.getStardustCostsForPowerup();
        }
    },
    MAX_CP_CUR("Max CP (Cur)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            try {
                return p.getMaxCpForPlayer();
            } catch (NoSuchItemException e) {
                return -1;
            }
        }
    },
    MAX_CP_40("Max CP (40)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            try {
                return p.getMaxCp();
            } catch (NoSuchItemException e) {
                return -1;
            }
        }
    },
    MAX_CP_EVOLVED_CUR("Max CP Evolved (Cur)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            final List<PokemonIdOuterClass.PokemonId> highest = Evolutions.getHighest(p.getPokemonId());
            final PokemonIdOuterClass.PokemonId highestUpgradedFamily = highest.get(0);
            return p.getMaxCpFullEvolveAndPowerupForPlayer(highestUpgradedFamily);
        }
    },
    MAX_CP_EVOLVED_40("Max CP Evolved (40)", ColumnType.INT) {
        @Override
        public Object get(final Pokemon p) {
            final List<PokemonIdOuterClass.PokemonId> highest = Evolutions.getHighest(p.getPokemonId());
            final PokemonIdOuterClass.PokemonId highestUpgradedFamily = highest.get(0);
            return p.getCpFullEvolveAndPowerup(highestUpgradedFamily);
        }
    },
    CP_EVOLVED("CP Evolved", ColumnType.NULLABLE_INT) {
        @Override
        public Object get(final Pokemon p) {
            final List<PokemonIdOuterClass.PokemonId> highest = Evolutions.getHighest(p.getPokemonId());
            final PokemonIdOuterClass.PokemonId highestUpgradedFamily = highest.get(0);
            final int cpAfterFullyEvolve;
            if (highest.contains(p.getPokemonId())) {     
                cpAfterFullyEvolve = p.getCp();     
            } else {
                cpAfterFullyEvolve = p.getCpAfterFullEvolve(highestUpgradedFamily);
            }
            
            if (cpAfterFullyEvolve != p.getCp()) {
                return String.valueOf(cpAfterFullyEvolve);
            } else {
                return StringLiterals.NO_VALUE_SIGN;
            }
        }
    },
    CAUGHT_WITH("Caught With", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonUtils.formatItem(p.getPokeball());
        }
    },
    CAUGHT_TIME("Caught Time", ColumnType.DATE) {
        @Override
        public Object get(final Pokemon p) {
            return DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs()));
        }
    },
    FAVORITE("Favorite", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return (p.isFavorite()) ? YES : "";
        }
    },
    GYM("Gym", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return (p.isDeployed()) ? YES : "";
        }
    },
    DUEL_ABILITY_RATING("Duel Ability Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.duelAbility(p), PokemonPerformanceCache.getStats(p.getPokemonId()).duelAbility.value);
        }
    },
    GYM_OFFENSE_RATING("Gym Offense Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.gymOffense(p), PokemonPerformanceCache.getStats(p.getPokemonId()).gymOffense.value);
        }
    },
    GYM_DEFENSE_RATING("Gym Defense Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.gymDefense(p), PokemonPerformanceCache.getStats(p.getPokemonId()).gymDefense.value);
        }
    },
    DUEL_ABILITY("Duel Ability", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.duelAbility(p), PokemonPerformanceCache.getHighestStats().duelAbility.value);
        }
    },
    GYM_OFFENSE("Gym Offense", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.gymOffense(p), PokemonPerformanceCache.getHighestStats().gymOffense.value);
        }
    },
    GYM_DEFENSE("Gym Defense", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return Utilities.percentage(PokemonCalculationUtils.gymDefense(p), PokemonPerformanceCache.getHighestStats().gymDefense.value);
        }
    },
    MOVE_1_RATING("Move 1 Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonCalculationUtils.moveRating(p, true);
        }
    },
    MOVE_2_RATING("Move 2 Rating", ColumnType.PERCENTAGE) {
        @Override
        public Object get(final Pokemon p) {
            return PokemonCalculationUtils.moveRating(p, false);
        }
    },
    CAUGHT_COORDINATES("Caught Coordinates", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            final S2CellId cell = new S2CellId(p.getCapturedS2CellId());
            final int locationDecimals = 6;
            return LocationHelper.getCoordinates(cell).toString(locationDecimals);
        }
    },
    CAUGHT_CITY("Caught City", ColumnType.FUTURE_STRING) {
        @Override
        public Object get(final Pokemon p) {
            final S2CellId cell = new S2CellId(p.getCapturedS2CellId());
            return LocationHelper.getLocation(cell).thenApply(location -> location.city);
        }
    },
    CAUGHT_LOCATION("Caught Location", ColumnType.FUTURE_STRING) {
        @Override
        public Object get(final Pokemon p) {
            final S2CellId cell = new S2CellId(p.getCapturedS2CellId());
            return LocationHelper.getLocation(cell).thenApply(location -> location.formattedLocation);
        }
    },
    COSTUME("Costume", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return p.getPokemonDisplay() != null ? p.getPokemonDisplay().getCostume().toString() : "";
        }
    },
    GENDER("Gender", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return p.getPokemonDisplay() != null ? p.getPokemonDisplay().getGender().toString() : "";
        }
    },
    SHINY("Shiny", ColumnType.STRING) {
        @Override
        public Object get(final Pokemon p) {
            return p.getPokemonDisplay() != null ? (p.getPokemonDisplay().getShiny() ? YES : "")
                    : "";
        }
    },
    PID("PID", ColumnType.LONG) {
        @Override
        public Object get(final Pokemon p) {
            return p.getId();
        }
    };

    private static final String YES = "Yes";
    public final int id;
    public final String heading;
    public final ColumnType columnType;
    public final ArrayList data;

    private TableCellRenderer customCellRenderer;

    /**
     * Constructor to create the enum entries.
     *
     * @param heading    The name of the column.
     * @param columnType The type of the column.
     */
    PokeColumn(final String heading, final ColumnType columnType) {
        this.id = Internal.AUTO_INCREMENTER.get();
        this.heading = heading;
        this.columnType = columnType;
        this.data = CollectionHelper.provideArrayList(columnType.clazz);
    }

    /**
     * Constructor to create an enum entry with a custom cell renderer.
     *
     * @param heading            The name of the column.
     * @param columnType         The type of the column.
     * @param customCellRenderer The custom cell renderer.
     */
    PokeColumn(final String heading, final ColumnType columnType, final TableCellRenderer customCellRenderer) {
        this(heading, columnType);
        this.customCellRenderer = customCellRenderer;
    }

    /**
     * Gets the PokeColumn enum for given id.
     *
     * @param id The id.
     * @return The column.
     */
    public static PokeColumn getForId(final int id) {
        for (final PokeColumn column : PokeColumn.values()) {
            if (column.id == id) {
                return column;
            }
        }
        // If not found, we throw an exception
        throw new NoSuchElementException("There is no column with id " + id);
    }

    /**
     * Gets the PokeColumn enum for given heading.
     *
     * @param heading The heading.
     * @return The column.
     */
    public static PokeColumn getForHeading(final String heading) {
        for (final PokeColumn column : PokeColumn.values()) {
            if (column.heading.equals(heading)) {
                return column;
            }
        }
        // If not found, we throw an exception
        throw new NoSuchElementException("There is no column with heading " + heading);
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
        return customCellRenderer != null ? customCellRenderer : columnType.tableCellRenderer;
    }

    /**
     * This method must be overwritten and should return what should be the
     * data that has to be displayed.
     *
     * @param p The Pokémon that of that row.
     * @return The data that has to be displayed
     */
    public abstract Object get(Pokemon p);

    /**
     * We need an wrapper for the auto-incrementer here, so that we can access it statically.
     */
    private static class Internal {
        static final AutoIncrementer AUTO_INCREMENTER = new AutoIncrementer();
    }
}
