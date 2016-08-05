package me.corriekay.pokegoutil.controllers;

import com.pokegoapi.api.PokemonGo;

/*this controller does the login/log off, and different account information (aka player data)
 * 
 */
public final class AccountController {
	
	private static final AccountController S_INSTANCE = new AccountController();
	private static boolean sIsInit = false;
	
	private PokemonGo go;
		
	private AccountController(){
		
	}
	
	public static AccountController getInstance(){
		return S_INSTANCE;
	}
	
	public static void initialize(PokemonGo go){
		if(sIsInit)
			return;
		
		S_INSTANCE.go = go;
		
		sIsInit = true;
	}
	
}
