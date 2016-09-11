package me.corriekay.pokegoutil.gui.controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.gui.models.GuiControllerSettings;

public abstract class BaseController<T extends Pane> {
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final String iconPath = "icon/PokeBall-icon.png";

    private static Image iconImage;

    static {
        final URL icon = classLoader.getResource(iconPath);
        iconImage = new Image(icon.toExternalForm());
    }

    private final Scene rootScene;
    private final Stage stage;
    private String title;

    public BaseController() {

        final FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(getFxmlLayout()));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());

        final GuiControllerSettings guiControllerSettings = getGuiControllerSettings();
        stage = new Stage();
        stage.getIcons().add(iconImage);
        stage.setTitle(guiControllerSettings.getTitle());
        stage.setResizable(guiControllerSettings.isResizeable());
        stage.setScene(rootScene);
        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    abstract public String getFxmlLayout();

    abstract public GuiControllerSettings getGuiControllerSettings();

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
        stage.setTitle(title);
    }
}
