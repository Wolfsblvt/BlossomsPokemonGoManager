package me.corriekay.pokegoutil.utils.helpers;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

/**
 * Helper class for evolving with itens.
 */
public final class EvolveHelper implements Comparable<EvolveHelper> {

    private PokemonId pokemonToEvolve;
    private ItemId itemToEvolve;
    private boolean evolveWithItem;
    
    /**
     * Default constructor.
     * @param pokemon the pokemon that require item to evolve
     * @param item required to evolve to pokemon
     */
    public EvolveHelper(final PokemonId pokemon, final ItemId item) {
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

    public void setItemToEvolve(final ItemId itemToEvolve) {
        this.itemToEvolve = itemToEvolve;
    }

    public boolean isEvolveWithItem() {
        return evolveWithItem;
    }

    public void setEvolveWithItem(final boolean evolveWithItem) {
        this.evolveWithItem = evolveWithItem;
    }

    @Override
    public int compareTo(final EvolveHelper o) {
        return this.toString().compareTo(o.toString());
    }
}
