package me.corriekay.pokegoutil.controllers;

import com.pokegoapi.api.PokemonGo;

/*
 * This controller takes care of interactions with the inventory items
 */
public final class InventoryController {	
	
	private static final InventoryController S_INSTANCE = new InventoryController();
	private static boolean sIsInit = false;
	
	private PokemonGo go;
		
	private InventoryController(){
		
	}
	
	public static InventoryController getInstance(){
		return S_INSTANCE;
	}
	
	public static void initialize(PokemonGo go){
		if(sIsInit)
			return;
		
		S_INSTANCE.go = go;
		
		sIsInit = true;
	}
}
