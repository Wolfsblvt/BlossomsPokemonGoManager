package me.corriekay.pokegoutil.data.models;

import java.util.List;

import com.pokegoapi.api.pokemon.Evolutions;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

public abstract class PokemonCpProvider {
    private Pokemon pokemon;
    
    public PokemonCpProvider(Pokemon pokemon) {
        this.pokemon = pokemon;
    }
    
    public int getMaxCp() {
        int maxCp = 0;
        try {
            maxCp = getMaxCpValue(pokemon);
        } catch (NoSuchItemException e) {
            System.out.println(e.getMessage());
        }
        return maxCp;
    }
    
    public int getMaxEvolvedCp() {
        final List<PokemonId> highest = Evolutions.getHighest(pokemon.getPokemonId());
        int maxEvolvedCp = 0;
        for (final PokemonId pokemonId : highest) {
            maxEvolvedCp = Math.max(maxEvolvedCp, getMaxEvolvedCpValue(pokemon, pokemonId));
        }
        return maxEvolvedCp;
    }
    
    protected abstract int getMaxCpValue(Pokemon pokemon) throws NoSuchItemException;
    protected abstract int getMaxEvolvedCpValue(Pokemon pokemon, PokemonId pokemonId);
}
