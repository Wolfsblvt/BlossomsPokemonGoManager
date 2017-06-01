package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonBag;
import me.corriekay.pokegoutil.data.models.PokemonModel;

/*
 * This controller takes care of handling pokémon
 */
public class PokemonBagManager extends ManagerInitializer{

    private final PokemonBagManager S_INSTANCE = new PokemonBagManager();
    private PokemonBag pokemonBag;

    private PokemonBagManager() {

    }

    public ObservableList<PokemonModel> getAllPokemon() {
        return sIsInit ? S_INSTANCE.pokemonBag.getAllPokemon() : null;
    }

    public int getNbPokemon() {
        return sIsInit ? S_INSTANCE.pokemonBag.getNumberPokemon() : 0;
    }

    @Override
    protected void mInitialize(PokemonGo go) {
        // TODO Auto-generated method stub
        S_INSTANCE.pokemonBag = new PokemonBag(go.getInventories().getPokebank().getPokemons());
    }
}
