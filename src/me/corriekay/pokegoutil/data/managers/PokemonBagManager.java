package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonBag;
import me.corriekay.pokegoutil.data.models.PokemonModel;

/*
 * This controller takes care of handling pokémon
 */
public class PokemonBagManager {

    private static final PokemonBagManager S_INSTANCE = new PokemonBagManager();
    private static boolean sIsInit = false;
    private PokemonBag pokemonBag;

    private PokemonBagManager() {

    }

    public static void initialize(PokemonGo go) {
        if (sIsInit)
            return;
        try {
            S_INSTANCE.pokemonBag = new PokemonBag(go.getInventories().getPokebank().getPokemons());
        } catch (Exception e) {
            //TODO sumthin here
            return;
        }

        sIsInit = true;
    }

    public static ObservableList<PokemonModel>getAllPokemon(){
        return sIsInit ? S_INSTANCE.pokemonBag.getAllPokemon() : null;
    }

    public static int getNbPokemon(){
        return sIsInit ? S_INSTANCE.pokemonBag.getNumberPokemon() : 0;
    }
}
