package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;

import java.util.EnumMap;
import java.util.Map;

/**
 * A Cache class which calculates and saves several values for Pokémon to make them easily available.
 */
public class PokemonValueCache {
    private EnumMap<PokemonId, PokemonPerformanceStats> map;
    public final PokemonPerformanceStats highestStats;

    /**
     * Creates the cache and calculates all values.
     */
    public PokemonValueCache() {
        map = new EnumMap<>(PokemonId.class);

        long highestDuelAbility = 0;
        double highestGymOffense = 0;
        long highestGymDefense = 0;

        EnumMap<PokemonId, PokemonMeta> pokemonMetas = PokemonMetaRegistry.getMeta();
        for (Map.Entry<PokemonId, PokemonMeta> entry : pokemonMetas.entrySet()) {

            // TODO: Calculate Pokemon values
            PokemonPerformanceStats stats = new PokemonPerformanceStats(entry.getKey(), 1l, 1d, 1l);

            map.put(entry.getKey(), stats);

            // Save if the stats are highest until now
            if (stats.duelAbility > highestDuelAbility) {
                highestDuelAbility = stats.duelAbility;
            }
            if (stats.gymOffense > highestGymOffense) {
                highestGymOffense = stats.gymOffense;
            }
            if (stats.gymDefense > highestGymDefense) {
                highestGymDefense = stats.gymDefense;
            }
        }

        highestStats = new PokemonPerformanceStats(null, highestDuelAbility, highestGymOffense, highestGymDefense);
    }

    public PokemonPerformanceStats getStats(PokemonId id) {
        return map.get(id);
    }


    private class PokemonPerformanceStats {
        final PokemonId id;
        final long duelAbility;
        final double gymOffense;
        final long gymDefense;

        private PokemonPerformanceStats(PokemonId id, long duelAbility, double gymOffense, long gymDefense) {
            this.id = id;
            this.duelAbility = duelAbility;
            this.gymOffense = gymOffense;
            this.gymDefense = gymDefense;
        }
    }
}
