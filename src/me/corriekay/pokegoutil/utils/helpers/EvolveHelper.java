package me.corriekay.pokegoutil.utils.helpers;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

/**
 * Helper class for evolving with itens
 */
public final class EvolveHelper implements Comparable<EvolveHelper> {

    private PokemonId pokemonToEvolve;
    private ItemId itemToEvolve;
    private boolean evolveWithItem;
    
    public EvolveHelper(PokemonId pokemon, ItemId item) {
        this.pokemonToEvolve = pokemon;
        this.itemToEvolve = item;
        this.evolveWithItem = false;
    }

    @Override
    public String toString() {
        return getItemToEvolve() + " (" + getPokemonToEvolve() + ")";
    }
    
    public String getPokemonToEvolve() {
        return PokemonUtils.getLocalPokeName(this.pokemonToEvolve.getNumber());
    }
    
    public String getItemToEvolve() {
        return PokemonUtils.formatItem(this.itemToEvolve);
    }

    public ItemId getItemToEvolveId() {
        return this.itemToEvolve;
    }

    public void setItemToEvolve(ItemId itemToEvolve) {
        this.itemToEvolve = itemToEvolve;
    }

    public boolean isEvolveWithItem() {
        return evolveWithItem;
    }

    public void setEvolveWithItem(boolean evolveWithItem) {
        this.evolveWithItem = evolveWithItem;
    }

    @Override
    public int compareTo(EvolveHelper o) {
        return this.toString().compareTo(o.toString());
    }
}
