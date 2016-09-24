package me.corriekay.pokegoutil.utils.pokemon;

import java.util.EnumMap;
import java.util.Map;

import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

/**
 * A Cache class which calculates and saves several values for Pokémon to make them easily available.
 */
public final class PokemonPerformanceCache {
    private static final EnumMap<PokemonId, PokemonPerformanceStats> MAP;
    private static final PokemonPerformanceStats HIGHEST_STATS;

    /**
     * Creates the cache and calculates all values.
     */
    static {
        MAP = new EnumMap<>(PokemonId.class);

        PokemonPerformance globalHighestDuelAbility = PokemonPerformance.DEFAULT;
        PokemonPerformance globalHighestGymOffense = PokemonPerformance.DEFAULT;
        PokemonPerformance globalHighestGymDefense = PokemonPerformance.DEFAULT;

        for (final Map.Entry<PokemonId, PokemonMeta> entry : PokemonMetaRegistry.getMeta().entrySet()) {
            final PokemonId pokemonId = entry.getKey();
            final PokemonMeta meta = entry.getValue();

            // We skip Pokémon that are currently not available
            if (PokemonUtils.NOT_EXISTING_POKEMON_LIST.contains(pokemonId)) {
                continue;
            }

            PokemonPerformance highestDuelAbility = PokemonPerformance.DEFAULT;
            PokemonPerformance highestGymOffense = PokemonPerformance.DEFAULT;
            PokemonPerformance highestGymDefense = PokemonPerformance.DEFAULT;
            for (final PokemonMove move1 : meta.getQuickMoves()) {
                for (final PokemonMove move2 : meta.getCinematicMoves()) {
                    final long duelAbility = PokemonCalculationUtils.duelAbility(pokemonId, move1, move2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (duelAbility > highestDuelAbility.value) {
                        highestDuelAbility = new PokemonPerformance(pokemonId, duelAbility, move1, move2);
                    }
                    final double gymOffense = PokemonCalculationUtils.gymOffense(pokemonId, move1, move2, PokemonUtils.MAX_IV);
                    if (gymOffense > highestGymOffense.value) {
                        highestGymOffense = new PokemonPerformance(pokemonId, gymOffense, move1, move2);
                    }
                    final long gymDefense = PokemonCalculationUtils.gymDefense(pokemonId, move1, move2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (gymDefense > highestGymDefense.value) {
                        highestGymDefense = new PokemonPerformance(pokemonId, gymDefense, move1, move2);
                    }
                }
            }
            final PokemonPerformanceStats stats = new PokemonPerformanceStats(pokemonId, highestDuelAbility, highestGymOffense, highestGymDefense);

            MAP.put(pokemonId, stats);

            // Save if the stats are highest until now
            if (stats.duelAbility.value > globalHighestDuelAbility.value) {
                globalHighestDuelAbility = stats.duelAbility;
            }
            if (stats.gymOffense.value > globalHighestGymOffense.value) {
                globalHighestGymOffense = stats.gymOffense;
            }
            if (stats.gymDefense.value > globalHighestGymDefense.value) {
                globalHighestGymDefense = stats.gymDefense;
            }
        }

        HIGHEST_STATS = new PokemonPerformanceStats(null, globalHighestDuelAbility, globalHighestGymOffense, globalHighestGymDefense);

        System.out.println("Highest Duel Ability: " + globalHighestDuelAbility.toString());
        System.out.println("Highest Gym Offense: " + globalHighestGymOffense.toString());
        System.out.println("Highest Gym Defense: " + globalHighestGymDefense.toString());
    }

    /** Prevent initializing this class. */
    private PokemonPerformanceCache() {
    }

    /**
     * Gets the highest overall performance stats.
     *
     * @return The performance stats.
     */
    public static PokemonPerformanceStats getHighestStats() {
        return HIGHEST_STATS;
    }

    /**
     * Gets the highest stats for given Pokémon.
     *
     * @param pokemonId The Pokémon ID.
     * @return The performance stats.
     */
    public static PokemonPerformanceStats getStats(final PokemonId pokemonId) {
        return MAP.get(pokemonId);
    }


    /**
     * Holds the Duel Ability, Gym Offense and Gym Defense Perfomances for given Pokémon.
     */
    public static final class PokemonPerformanceStats {
        public final PokemonId pokemonId;
        public final PokemonPerformance duelAbility;
        public final PokemonPerformance gymOffense;
        public final PokemonPerformance gymDefense;

        /**
         * Creates an instance of this performance stats object.
         * This is just an internal data class.
         *
         * @param pokemonId   The Pokémon ID.
         * @param duelAbility The Duel Ability Performance.
         * @param gymOffense  The Gym Offense Performance.
         * @param gymDefense  The Gym Defense Performance.
         */
        private PokemonPerformanceStats(final PokemonId pokemonId,
                                        final PokemonPerformance duelAbility,
                                        final PokemonPerformance gymOffense,
                                        final PokemonPerformance gymDefense) {
            this.pokemonId = pokemonId;
            this.duelAbility = duelAbility;
            this.gymOffense = gymOffense;
            this.gymDefense = gymDefense;
        }
    }

    /**
     * A class to save a specific performance of a given Pokémon with given moveset.
     */
    public static final class PokemonPerformance {
        public static final PokemonPerformance DEFAULT = new PokemonPerformance(null, 0, null, null);

        public final PokemonId pokemonId;
        public final double value;
        public final PokemonMove move1;
        public final PokemonMove move2;

        /**
         * Creates an instance of this performance stats object.
         * This is just an internal data class.
         *
         * @param pokemonId The Pokémon ID.
         * @param value     The value for this performance
         * @param move1     The Primary Move.
         * @param move2     The Secondary Move.
         */
        private PokemonPerformance(final PokemonId pokemonId, final double value, final PokemonMove move1, final PokemonMove move2) {
            this.pokemonId = pokemonId;
            this.value = value;
            this.move1 = move1;
            this.move2 = move2;
        }

        @Override
        public String toString() {
            return PokemonUtils.getLocalPokeName(pokemonId.getNumber()) + " with "
                + PokemonUtils.formatMove(move1) + " and "
                + PokemonUtils.formatMove(move2) + " has "
                + value;
        }
    }
}
