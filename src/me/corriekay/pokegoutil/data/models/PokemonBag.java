package me.corriekay.pokegoutil.data.models;

import com.pokegoapi.api.pokemon.Pokemon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PokemonBag {
    // list of pokemons with different informations that we use to display
    private ObservableList<PokemonModel> mons = FXCollections.observableArrayList();

    public PokemonBag(List<Pokemon> list){
        list.forEach(pokemon -> { mons.add(new PokemonModel(pokemon)); });
    }

    public ObservableList<PokemonModel> getAllPokemon() {
        return mons;
    }

    public int getNumberPokemon() {
        return mons.size();
    }
}
