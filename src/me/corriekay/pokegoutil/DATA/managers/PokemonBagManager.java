package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;

/*
 * This controller takes care of handling pokémon
 */
public class PokemonBagManager {

    private static final PokemonBagManager S_INSTANCE = new PokemonBagManager();
    private static boolean sIsInit = false;

    private PokemonGo go;

    private PokemonBagManager() {

    }

    public static PokemonBagManager getInstance() {
        return S_INSTANCE;
    }

    public static void initialize(PokemonGo go) {
        if (sIsInit)
            return;

        S_INSTANCE.go = go;

        sIsInit = true;
    }
}
