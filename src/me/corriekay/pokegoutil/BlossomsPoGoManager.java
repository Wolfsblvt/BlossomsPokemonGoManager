package me.corriekay.pokegoutil;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.corriekay.pokegoutil.DATA.controllers.AccountController;
import me.corriekay.pokegoutil.GUI.controller.ChooseGuiWindowController;
import me.corriekay.pokegoutil.utils.Config;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.ui.Console;

import javax.swing.*;


public class BlossomsPoGoManager extends Application {

    public static final String VERSION = "v0.1.2-alpha.2";
    private static Stage sPrimaryStage;

    private ClassLoader classLoader = getClass().getClassLoader();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (Config.getConfig().getBool("develop", false)) {
            new ChooseGuiWindowController();
            sPrimaryStage.show();
        }
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

    public static Stage getPrimaryStage(){
        return sPrimaryStage;
    }

    public static void setNewPrimaryStage(Stage stage){
        if(sPrimaryStage != null && sPrimaryStage.isShowing())
            sPrimaryStage.hide();
        sPrimaryStage = stage;
    }
}
