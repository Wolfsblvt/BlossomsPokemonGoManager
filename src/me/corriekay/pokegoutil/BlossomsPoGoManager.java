package me.corriekay.pokegoutil;

import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;


public class BlossomsPoGoManager {
	
	private static Console console;
	public static final String VERSION = "0.1.1-Beta";

	public static void main(String[] args) throws Exception {
		Utilities.setNativeLookAndFeel();
		console = new Console("Console", 0, 0, true);
		console.setVisible(false);
		
		AccountController.initialize(console);
		AccountController.logOn();
	}
}
