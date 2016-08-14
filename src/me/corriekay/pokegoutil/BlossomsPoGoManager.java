package me.corriekay.pokegoutil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.DATA.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.ui.Console;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;

import javax.swing.*;
import java.io.IOException;


public class BlossomsPoGoManager extends Application {

    public static final String VERSION = "v0.1.2-alpha.2";

    private ClassLoader classLoader = getClass().getClassLoader();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (ConfigNew.getConfig().getBool(ConfigKey.DEVELOPFLAG))
            openGUIChooser(primaryStage);
        else
            openOldGui();
    }

    private void openOldGui() {
        SwingUtilities.invokeLater(new Runnable() {
            private Console console;

            @Override
            public void run() {
                UIHelper.setNativeLookAndFeel();
                console = new Console("Console", 0, 0, true);
                console.setVisible(false);
                AccountController.initialize(console);
                AccountController.logOn();
            }
        });
    }

    public void openGUIChooser(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(classLoader.getResource("layout/ChooseGUIWindow.fxml"));
        } catch (IOException e) {
            System.err.println("Problem loading .fxml file: " + e.toString());
            return;
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(classLoader.getResource("icon/PokeBall-icon.png").toExternalForm()));
        primaryStage.setTitle("Choose a GUI");
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
