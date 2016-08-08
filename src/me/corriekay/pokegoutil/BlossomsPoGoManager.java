package me.corriekay.pokegoutil;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class BlossomsPoGoManager extends Application{
	
	public static final String VERSION = "0.1.1-Beta";
	//public static final FXMLLoader LOADER = new FXMLLoader();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage){
		//@SuppressWarnings("static-access")
		openGUIChooser(primaryStage);
	}
	
	public void openGUIChooser(Stage primaryStage) {
		if (primaryStage!=null) {
			Parent root;
			try {
				root = (Parent) FXMLLoader.load(getClass().getClassLoader().getResource("resources/layout/ChooseGUIWindow.fxml"));
			} catch (IOException e) {
				System.err.println("Problem loading .fxml file: " + e.toString());
				return;
			}
			primaryStage.setScene(new Scene(root));
			primaryStage.getIcons().add(new Image("resources/icon/PokeBall-icon.png"));
			primaryStage.setTitle("Choose a GUI");
			primaryStage.initStyle(StageStyle.UTILITY);
			primaryStage.show();
		}
	}
}
