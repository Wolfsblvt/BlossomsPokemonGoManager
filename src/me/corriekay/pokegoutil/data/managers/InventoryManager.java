package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

/*
 * This controller takes care of interactions with the inventory items
 */
public final class InventoryManager extends ManagerInitializer {
    private final InventoryManager S_INSTANCE = new InventoryManager();

    private Inventories inventories;

    private InventoryManager() {

    }

    public Inventories getInventories() throws LoginFailedException, RemoteServerException {
        return sIsInit ? S_INSTANCE.inventories : null;
    }

    @Override
    protected void mInitialize(PokemonGo go) {
        // TODO Auto-generated method stub
        S_INSTANCE.inventories = go.getInventories();
    }
}
