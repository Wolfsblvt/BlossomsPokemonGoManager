package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;

public abstract class ManagerInitializer {
    protected boolean sIsInit = false;
    
    public void initialize(PokemonGo go) {
        if (!sIsInit) {
            try {
                mInitialize(go);
                sIsInit = true;
            } catch (Exception e) {
                // TODO sumthin here
            }
        }
    }
    
    protected abstract void mInitialize(PokemonGo go);
}
