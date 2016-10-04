package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

/**
 * Holds the Duel Ability, Gym Offense and Gym Defense Perfomances for given Pokémon.
 */
public final class PokemonPerformanceStats {
    public final PokemonId pokemonId;
    public final PokemonPerformance<Long> duelAbility;
    public final PokemonPerformance<Double> gymOffense;
    public final PokemonPerformance<Long> gymDefense;

    /**
     * Creates an instance of this performance stats object.
     * This is just an internal data class, so can only be created from inside the package.
     *
     * @param pokemonId   The Pokémon ID.
     * @param duelAbility The Duel Ability Performance.
     * @param gymOffense  The Gym Offense Performance.
     * @param gymDefense  The Gym Defense Performance.
     */
    PokemonPerformanceStats(final PokemonId pokemonId,
                            final PokemonPerformance<Long> duelAbility,
                            final PokemonPerformance<Double> gymOffense,
                            final PokemonPerformance<Long> gymDefense) {
        this.pokemonId = pokemonId;
        this.duelAbility = duelAbility;
        this.gymOffense = gymOffense;
        this.gymDefense = gymDefense;
    }
}
