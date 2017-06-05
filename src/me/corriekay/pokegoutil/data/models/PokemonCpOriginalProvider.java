package me.corriekay.pokegoutil.data.models;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

public class PokemonCpOriginalProvider extends PokemonCpProvider {

    public PokemonCpOriginalProvider(Pokemon pokemon) {
        super(pokemon);
    }

    @Override
    protected int getMaxCpValue(Pokemon pokemon) throws NoSuchItemException {
        return pokemon.getMaxCp();
    }

    @Override
    protected int getMaxEvolvedCpValue(Pokemon pokemon, PokemonId pokemonId) {
        return pokemon.getCpFullEvolveAndPowerup(pokemonId);
    }

}
