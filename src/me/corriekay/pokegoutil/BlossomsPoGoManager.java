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
	public static final FXMLLoader LOADER = new FXMLLoader();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		@SuppressWarnings("static-access")
		Parent root = (Parent) LOADER.load(getClass().getClassLoader().getResource("res/layout/ChooseGUIWindow.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.getIcons().add(new Image("/res/icon/PokeBall-icon.png"));
		primaryStage.setTitle("Choose a GUI");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.show();
	}
}
