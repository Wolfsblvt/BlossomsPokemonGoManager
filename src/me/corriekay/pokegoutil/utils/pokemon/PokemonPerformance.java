package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonMoveOuterClass;

/**
 * A class to save a specific performance of a given Pokémon with given moveset.
 */
public final class PokemonPerformance {
    public static final PokemonPerformance DEFAULT = new PokemonPerformance(null, 0, null, null);

    public final PokemonIdOuterClass.PokemonId pokemonId;
    public final double value;
    public final PokemonMoveOuterClass.PokemonMove move1;
    public final PokemonMoveOuterClass.PokemonMove move2;

    /**
     * Creates an instance of this performance stats object.
     * This is just an internal data class, so can only be created from inside the package.
     *
     * @param pokemonId The Pokémon ID.
     * @param value     The value for this performance
     * @param move1     The Primary Move.
     * @param move2     The Secondary Move.
     */
    PokemonPerformance(final PokemonIdOuterClass.PokemonId pokemonId, final double value, final PokemonMoveOuterClass.PokemonMove move1, final PokemonMoveOuterClass.PokemonMove move2) {
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
