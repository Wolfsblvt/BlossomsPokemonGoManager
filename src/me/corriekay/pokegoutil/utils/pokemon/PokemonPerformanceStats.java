package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Holds the Duel Ability, Gym Offense and Gym Defense Perfomances for given Pokémon.
 */
public final class PokemonPerformanceStats {
    public final PokemonIdOuterClass.PokemonId pokemonId;
    public final PokemonPerformance duelAbility;
    public final PokemonPerformance gymOffense;
    public final PokemonPerformance gymDefense;

    /**
     * Creates an instance of this performance stats object.
     * This is just an internal data class, so can only be created from inside the package.
     *
     * @param pokemonId   The Pokémon ID.
     * @param duelAbility The Duel Ability Performance.
     * @param gymOffense  The Gym Offense Performance.
     * @param gymDefense  The Gym Defense Performance.
     */
    PokemonPerformanceStats(final PokemonIdOuterClass.PokemonId pokemonId,
                            final PokemonPerformance duelAbility,
                            final PokemonPerformance gymOffense,
                            final PokemonPerformance gymDefense) {
        this.pokemonId = pokemonId;
        this.duelAbility = duelAbility;
        this.gymOffense = gymOffense;
        this.gymDefense = gymDefense;
    }
}
