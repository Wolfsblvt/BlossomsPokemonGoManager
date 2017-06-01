package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;

public class ProfileManager extends ManagerInitializer{
    private PlayerProfile pp;
    private final ProfileManager S_INSTANCE = new ProfileManager();
    private ProfileManager() {

    }

    public PlayerProfile getProfile() {
        return sIsInit ? S_INSTANCE.pp : null;
    }

    @Override
    protected void mInitialize(PokemonGo go) {
        // TODO Auto-generated method stub
        S_INSTANCE.pp = go.getPlayerProfile();
    }
}
