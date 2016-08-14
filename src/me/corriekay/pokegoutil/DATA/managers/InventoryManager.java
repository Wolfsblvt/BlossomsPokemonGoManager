package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

/*
 * This controller takes care of interactions with the inventory items
 */
public final class InventoryManager {

    private static final InventoryManager S_INSTANCE = new InventoryManager();
    private static boolean sIsInit = false;

    private PokemonGo go;

    private InventoryManager() {

    }

    public static void initialize(PokemonGo go) {
        if (sIsInit)
            return;

        S_INSTANCE.go = go;

        sIsInit = true;
    }


    public static Inventories getInventories() throws LoginFailedException, RemoteServerException {
        return sIsInit ? S_INSTANCE.go.getInventories() : null;
    }
}
