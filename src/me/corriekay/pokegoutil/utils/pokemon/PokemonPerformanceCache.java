package me.corriekay.pokegoutil.utils.pokemon;

import java.util.EnumMap;

import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass.PokemonSettings;

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

        PokemonPerformance<Long> globalHighestDuelAbility = PokemonPerformance.DEFAULT_LONG;
        PokemonPerformance<Double> globalHighestGymOffense = PokemonPerformance.DEFAULT_DOUBLE;
        PokemonPerformance<Long> globalHighestGymDefense = PokemonPerformance.DEFAULT_LONG;

        for (final PokemonId pokemonId : PokemonId.values()) {
            // We skip Pokemon that are currently not available for the global highest value collection
            if (PokemonUtils.NOT_EXISTING_POKEMON_LIST.contains(pokemonId)) {
                continue;
            }

            final PokemonSettings settings = PokemonMeta.getPokemonSettings(pokemonId);

            PokemonPerformance<Long> highestDuelAbility = PokemonPerformance.DEFAULT_LONG;
            PokemonPerformance<Double> highestGymOffense = PokemonPerformance.DEFAULT_DOUBLE;
            PokemonPerformance<Long> highestGymDefense = PokemonPerformance.DEFAULT_LONG;
            for (final PokemonMove move1 : settings.getQuickMovesList()) {
                for (final PokemonMove move2 : settings.getCinematicMovesList()) {
                    final long duelAbility = PokemonCalculationUtils.duelAbility(pokemonId, move1, move2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (duelAbility > highestDuelAbility.value) {
                        highestDuelAbility = new PokemonPerformance<>(pokemonId, duelAbility, move1, move2);
                    }
                    final double gymOffense = PokemonCalculationUtils.gymOffense(pokemonId, move1, move2, PokemonUtils.MAX_IV);
                    if (gymOffense > highestGymOffense.value) {
                        highestGymOffense = new PokemonPerformance<>(pokemonId, gymOffense, move1, move2);
                    }
                    final long gymDefense = PokemonCalculationUtils.gymDefense(pokemonId, move1, move2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (gymDefense > highestGymDefense.value) {
                        highestGymDefense = new PokemonPerformance<>(pokemonId, gymDefense, move1, move2);
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


}
