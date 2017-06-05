package me.corriekay.pokegoutil.data.models;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

public class PokemonCpCurrentProvider extends PokemonCpProvider {

    public PokemonCpCurrentProvider(Pokemon pokemon) {
        super(pokemon);
    }

    @Override
    protected int getMaxCpValue(Pokemon pokemon) throws NoSuchItemException {
        return pokemon.getMaxCpForPlayer();
    }

    @Override
    protected int getMaxEvolvedCpValue(Pokemon pokemon, PokemonId pokemonId) {
        return pokemon.getMaxCpFullEvolveAndPowerupForPlayer(pokemonId);
    }

}