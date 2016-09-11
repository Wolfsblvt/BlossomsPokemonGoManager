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

/**
 * The base controller used for all javaFx GUIs.
 *
 * @param <T> layout class to be used in the controller
 */
public abstract class BaseController<T extends Pane> {
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final String iconPath = "icon/PokeBall-icon.png";

    private static Image iconImage;

    static {
        final URL icon = classLoader.getResource(iconPath);
        iconImage = new Image(icon.toExternalForm());
    }

    private final Stage stage;
    private String title;

    /**
     * Instantiate the controller and set all the settings. Sets the primary stage to this controller.
     */
    public BaseController() {
        final FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(getFxmlLayout()));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        final Scene rootScene = new Scene(fxmlLoader.getRoot());

        final GuiControllerSettings guiControllerSettings = getGuiControllerSettings();
        stage = new Stage();
        stage.getIcons().add(iconImage);
        stage.setTitle(guiControllerSettings.getTitle());
        stage.setResizable(guiControllerSettings.isResizeable());
        stage.setScene(rootScene);
        BlossomsPoGoManager.setNewPrimaryStage(stage);
    }

    /**
     * Get the path of the FxmlLayout file.
     *
     * @return path of the FxmlLayout file
     */
    abstract public String getFxmlLayout();

    /**
     * Get the settings used to initialize the controller.
     *
     * @return settings used to initialize the controller
     */
    abstract public GuiControllerSettings getGuiControllerSettings();

    /**
     * Get the title of the window.
     *
     * @return title of the window
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the current title of the window.
     *
     * @param title title of the window
     */
    public void setTitle(final String title) {
        this.title = title;
        stage.setTitle(title);
    }
}
