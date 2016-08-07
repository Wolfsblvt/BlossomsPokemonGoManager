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

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getClassLoader().getResource("me/corriekay/pokegoutil/GUI/view/ChooseGUIWindow.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.getIcons().add(new Image("/res/PokeBall-icon.png"));
		primaryStage.setTitle("Choose a GUI");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.show();
	}
}
