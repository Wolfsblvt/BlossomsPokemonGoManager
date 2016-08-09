package me.corriekay.pokegoutil;

import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;


public class BlossomsPoGoManager extends Application{
	
	public static final String VERSION = "0.1.1-Beta";
	//public static final FXMLLoader LOADER = new FXMLLoader();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage){
		//@SuppressWarnings("static-access")
		if (Config.getConfig().getBool("develop", false))
			openGUIChooser(primaryStage);
		else
			openOldGui();
	}
	
	private void openOldGui() {
    	SwingUtilities.invokeLater(new Runnable() {
            private Console console;

			@Override
            public void run() {            	
        		Utilities.setNativeLookAndFeel();
        		console = new Console("Console", 0, 0, true);
        		console.setVisible(false);
        		AccountController.initialize(console);
				AccountController.logOn();				
            }
        });    	    	
	}

	public void openGUIChooser(Stage primaryStage) {
		if (primaryStage!=null) {
			Parent root;
			ClassLoader classLoader = getClass().getClassLoader();
			try {
				root = (Parent) FXMLLoader.load(classLoader.getResource("layout/ChooseGUIWindow.fxml"));
			} catch (IOException e) {
				System.err.println("Problem loading .fxml file: " + e.toString());
				return;
			}
			primaryStage.setScene(new Scene(root));
			URL image = classLoader.getResource("icon/PokeBall-icon.png");
			primaryStage.getIcons().add(new Image(image.toExternalForm()));
			primaryStage.setTitle("Choose a GUI");
			primaryStage.initStyle(StageStyle.UTILITY);
			primaryStage.show();
		}
	}
}
