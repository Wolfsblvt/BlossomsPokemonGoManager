package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.api.pokemon.PokemonMoveMeta;
import com.pokegoapi.api.pokemon.PokemonMoveMetaRegistry;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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

        PokemonPerformance globalHighestDuelAbility = PokemonPerformance.DEFAULT;
        PokemonPerformance globalHighestGymOffense = PokemonPerformance.DEFAULT;
        PokemonPerformance globalHighestGymDefense = PokemonPerformance.DEFAULT;

        EnumMap<PokemonId, PokemonMeta> pokemonMetas = PokemonMetaRegistry.getMeta();
        for (final Map.Entry<PokemonId, PokemonMeta> entry : pokemonMetas.entrySet()) {
            final PokemonId pokemonId = entry.getKey();
            final PokemonMeta meta = entry.getValue();

            // We skip Pokémon that are currently not available
            List<PokemonId> notAvailablePokemon = Arrays.asList(
                PokemonId.DITTO,
                PokemonId.ARTICUNO,
                PokemonId.ZAPDOS,
                PokemonId.MOLTRES,
                PokemonId.MEWTWO,
                PokemonId.MEW,
                PokemonId.UNRECOGNIZED
            );
            if (notAvailablePokemon.contains(pokemonId)) {
                continue;
            }

            PokemonPerformance highestDuelAbility = PokemonPerformance.DEFAULT;
            PokemonPerformance highestGymOffense = PokemonPerformance.DEFAULT;
            PokemonPerformance highestGymDefense = PokemonPerformance.DEFAULT;
            for (PokemonMove move1 : meta.getQuickMoves()) {
                PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(move1);
                for (PokemonMove move2 : meta.getCinematicMoves()) {
                    PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(move2);

                    long duelAbility = PokemonUtils.duelAbility(pokemonId, pm1, pm2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (duelAbility > highestDuelAbility.value) {
                        highestDuelAbility = new PokemonPerformance(pokemonId, duelAbility, pm1, pm2);
                    }
                    double gymOffense = PokemonUtils.gymOffense(pokemonId, pm1, pm2, PokemonUtils.MAX_IV);
                    if (gymOffense > highestGymOffense.value) {
                        highestGymOffense = new PokemonPerformance(pokemonId, gymOffense, pm1, pm2);
                    }
                    long gymDefense = PokemonUtils.gymDefense(pokemonId, pm1, pm2, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV, PokemonUtils.MAX_IV);
                    if (gymDefense > highestGymDefense.value) {
                        highestGymDefense = new PokemonPerformance(pokemonId, gymDefense, pm1, pm2);
                    }
                }
            }
            PokemonPerformanceStats stats = new PokemonPerformanceStats(pokemonId, highestDuelAbility, highestGymOffense, highestGymDefense);

            map.put(pokemonId, stats);

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

        highestStats = new PokemonPerformanceStats(null, globalHighestDuelAbility, globalHighestGymOffense, globalHighestGymDefense);

        // TODO: Remove debug logging, or advance logging to logging class
        System.out.println("Highest Duel Ability: " + globalHighestDuelAbilityPokemon);
        System.out.println("Highest Gym Offense: " + globalHighestGymOffensePokemon);
        System.out.println("Highest Gym Defense: " + globalHighestGymDefensePokemon);
    }

    public static PokemonPerformanceStats getHighestStats() {
        return highestStats;
    }

    public static PokemonPerformanceStats getStats(PokemonId id) {
        return map.get(id);
    }


    /**
     * A class to save the best perfomances of a given Pokémon.
     */
    public static final class PokemonPerformanceStats {
        public final PokemonId id;
        public final PokemonPerformance duelAbility;
        public final PokemonPerformance gymOffense;
        public final PokemonPerformance gymDefense;

        private PokemonPerformanceStats(PokemonId id, PokemonPerformance duelAbility, PokemonPerformance gymOffense, PokemonPerformance gymDefense) {
            this.id = id;
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

        public final PokemonId id;
        public final double value;
        public final PokemonMoveMeta move1;
        public final PokemonMoveMeta move2;

        private PokemonPerformance(PokemonId id, double value, PokemonMoveMeta move1, PokemonMoveMeta move2) {
            this.id = id;
            this.value = value;
            this.move1 = move1;
            this.move2 = move2;
        }

        @Override
        public String toString() {
            return PokeHandler.getLocalPokeName(id.getNumber()) + " with "
                + move1.getMove().toString(); // TODO: Finish building string
        }
    }
}
