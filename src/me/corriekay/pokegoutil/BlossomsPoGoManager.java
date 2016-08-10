package me.corriekay.pokegoutil;

import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;


public class BlossomsPoGoManager {
	
	private static Console console;
	public static final String VERSION = "v0.1.2-alpha.1";

	public static void main(String[] args) throws Exception {
		Utilities.setNativeLookAndFeel();
		console = new Console("Console", 0, 0, true);
		console.setVisible(false);
		
		AccountController.initialize(console);
		AccountController.logOn();
	}
}
