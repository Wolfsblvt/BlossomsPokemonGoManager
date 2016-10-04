package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

/**
 * A class to save a specific performance of a given Pokémon with given moveset.
 * @param <T> The type of the value that is saved
 */
public final class PokemonPerformance<T> {
    public static final PokemonPerformance<Long> DEFAULT_LONG = new PokemonPerformance<>(null, 0L, null, null);
    public static final PokemonPerformance<Double> DEFAULT_DOUBLE = new PokemonPerformance<>(null, 0d, null, null);

    public final PokemonId pokemonId;
    public final T value;
    public final PokemonMove move1;
    public final PokemonMove move2;

    /**
     * Creates an instance of this performance stats object.
     * This is just an internal data class, so can only be created from inside the package.
     *
     * @param pokemonId The Pokémon ID.
     * @param value     The value for this performance
     * @param move1     The Primary Move.
     * @param move2     The Secondary Move.
     */
    PokemonPerformance(final PokemonId pokemonId, final T value, final PokemonMove move1, final PokemonMove move2) {
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
