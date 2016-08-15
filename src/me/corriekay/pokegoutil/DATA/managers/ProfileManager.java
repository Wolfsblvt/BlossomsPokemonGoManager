package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;

public class ProfileManager {
    private static final ProfileManager S_INSTANCE = new ProfileManager();
    private static boolean sIsInit = false;

    private PokemonGo go;

    private ProfileManager() {

    }

    public static void initialize(PokemonGo go) {
        if (sIsInit)
            return;

        S_INSTANCE.go = go;

        sIsInit = true;
    }

    public static PlayerProfile getProfile() {
        return sIsInit ? S_INSTANCE.go.getPlayerProfile() : null;
    }
}
