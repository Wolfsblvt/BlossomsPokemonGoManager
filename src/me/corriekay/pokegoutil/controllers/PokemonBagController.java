package me.corriekay.pokegoutil.controllers;

import com.pokegoapi.api.PokemonGo;

/*
 * This controller takes care of handling pokémon
 */
public class PokemonBagController {
	
	private static final PokemonBagController S_INSTANCE = new PokemonBagController();
	private static boolean sIsInit = false;
	
	private PokemonGo go;
		
	private PokemonBagController(){
		
	}
	
	public static PokemonBagController getInstance(){
		return S_INSTANCE;
	}
	
	public static void initialize(PokemonGo go){
		if(sIsInit)
			return;
		
		S_INSTANCE.go = go;
		
		sIsInit = true;
	}
}
