package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.api.pokemon.PokemonMoveMeta;
import com.pokegoapi.api.pokemon.PokemonMoveMetaRegistry;

import java.util.EnumMap;
import java.util.Map;

/**
 * A Cache class which calculates and saves several values for Pokémon to make them easily available.
 */
public final class PokemonValueCache {
    private static final EnumMap<PokemonId, PokemonPerformanceStats> map;
    private static final PokemonPerformanceStats highestStats;

    /**
     * Creates the cache and calculates all values.
     */
    static {
        map = new EnumMap<>(PokemonId.class);

        long globalHighestDuelAbility = 0;
        double globalHighestGymOffense = 0;
        long globalHighestGymDefense = 0;

        EnumMap<PokemonId, PokemonMeta> pokemonMetas = PokemonMetaRegistry.getMeta();
        for (Map.Entry<PokemonId, PokemonMeta> entry : pokemonMetas.entrySet()) {
            final PokemonId pokemonId = entry.getKey();
            final PokemonMeta meta = entry.getValue();

            // TODO: Calculate Pokemon values
            long highestDuelAbility = 0;
            double highestGymOffense = 0;
            long highestGymDefense = 0;
            for (PokemonMove move1 : meta.getQuickMoves()) {
                PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(move1);
                for (PokemonMove move2 : meta.getCinematicMoves()) {
                    PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(move2);

                    // Calculate duelAbility
                    long duelAbility = PokemonUtils.duelAbility(pokemonId, pm1, pm2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (duelAbility > highestDuelAbility) {
                        highestDuelAbility = duelAbility;
                    }
                    double gymOffense = PokemonUtils.gymOffense(pokemonId, pm1, pm2, PokemonUtils.MAX_IV);
                    if (gymOffense > highestGymOffense) {
                        highestGymOffense = gymOffense;
                    }
                    long gymDefense = PokemonUtils.gymDefense(pokemonId, pm1, pm2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (gymDefense > highestGymDefense) {
                        highestGymDefense = gymDefense;
                    }
                }
            }
            PokemonPerformanceStats stats = new PokemonPerformanceStats(pokemonId, highestDuelAbility, highestGymOffense, highestGymDefense);

            map.put(pokemonId, stats);

            // Save if the stats are highest until now
            if (stats.duelAbility > globalHighestDuelAbility) {
                globalHighestDuelAbility = stats.duelAbility;
            }
            if (stats.gymOffense > globalHighestGymOffense) {
                globalHighestGymOffense = stats.gymOffense;
            }
            if (stats.gymDefense > globalHighestGymDefense) {
                globalHighestGymDefense = stats.gymDefense;
            }
        }

        highestStats = new PokemonPerformanceStats(null, globalHighestDuelAbility, globalHighestGymOffense, globalHighestGymDefense);
    }

    public static PokemonPerformanceStats getHighestStats() {
        return highestStats;
    }

    public static PokemonPerformanceStats getStats(PokemonId id) {
        return map.get(id);
    }


    public static final class PokemonPerformanceStats {
        private final PokemonId id;
        public final long duelAbility;
        public final double gymOffense;
        public final long gymDefense;

        private PokemonPerformanceStats(PokemonId id, long duelAbility, double gymOffense, long gymDefense) {
            this.id = id;
            this.duelAbility = duelAbility;
            this.gymOffense = gymOffense;
            this.gymDefense = gymDefense;
        }
    }
}
