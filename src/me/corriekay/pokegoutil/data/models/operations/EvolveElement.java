package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.api.pokemon.Pokemon;

public class EvolveElement {
    
    public
        Pokemon poke;
        int candies;
        int candiesToEvolve;
        int cp;
        int hp;
        int candyRefund;
        
    public EvolveElement(Pokemon _poke)
    {
        poke = _poke;
        candies = poke.getCandy();
        candiesToEvolve = poke.getCandiesToEvolve();
        cp = poke.getCp();
        hp = poke.getMaxStamina();
        candyRefund = 1;
    }
    
    public Pokemon getPokemon()
    {
        return poke;
    }
    
    
}
