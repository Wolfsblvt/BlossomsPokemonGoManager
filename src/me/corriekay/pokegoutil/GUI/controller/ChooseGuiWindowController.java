package me.corriekay.pokegoutil.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.DATA.managers.AccountController;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.ui.Console;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

public class ChooseGuiWindowController extends Pane {

    private final String fxmlLayout = "layout/ChooseGUIWindow.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();
    private Scene rootScene;
    @FXML
    private Button oldGuiBtn;

    @FXML
    private Button newGuiBtn;

    public ChooseGuiWindowController() {
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(fxmlLayout));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());
        Stage stage = new Stage();
        stage.setScene(rootScene);
        icon = classLoader.getResource("icon/PokeBall-icon.png");
        stage.getIcons().add(new Image(icon.toExternalForm()));
        stage.setTitle("Choose a GUI");
        stage.setResizable(false);

        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    @FXML
    void initialize() {
        oldGuiBtn.setOnAction(this::onOldGuiBtnClicked);
        newGuiBtn.setOnAction(this::onNewGuiBtnClicked);
    }

    private void onClose(WindowEvent windowEvent) {
        System.exit(0);
    }

    @FXML
    void onNewGuiBtnClicked(ActionEvent event) {
        new LoginController();

        BlossomsPoGoManager.getPrimaryStage().show();
    }

    @FXML
    void onOldGuiBtnClicked(ActionEvent event) {
        rootScene.getWindow().hide();
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
}
