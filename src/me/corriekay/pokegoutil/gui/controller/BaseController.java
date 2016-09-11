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
    public static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private static final String iconPath = "icon/PokeBall-icon.png";

    private static Image iconImage;

    static {
        final URL icon = classLoader.getResource(iconPath);
        iconImage = new Image(icon.toExternalForm());
    }

    public Scene rootScene;
    private Stage stage;
    private String title;
    final public GuiControllerSettings guiControllerSettings = new GuiControllerSettings();


    /**
     * BaseController constructor. Call initializeController() to initialize controller.
     */
    public BaseController() {
        setGuiControllerSettings();
    }

    /**
     * Get the path of the FxmlLayout file.
     *
     * @return path of the FxmlLayout file
     */
    abstract public String getFxmlLayout();

    /**
     * Set the settings used to initialize the controller.
     */
    abstract public void setGuiControllerSettings();

    /**
     * Get the title of the window.
     *
     * @return title of the window
     */
    public String getTitle() {
        return title;
    }

    /**
     * Instantiate the controller and set all the settings. Sets the primary stage to this controller.<br>
     * Not done in constructor due to variables in parent class being initialize only after super() call.
     */
    public void initializeController() {
        final FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(getFxmlLayout()));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());

        stage = new Stage();
        stage.getIcons().add(iconImage);
        stage.setTitle(guiControllerSettings.getTitle());
        stage.setResizable(guiControllerSettings.isResizeable());
        stage.setMaximized(guiControllerSettings.isMaximized());
        stage.initStyle(guiControllerSettings.getStageStyle());
        stage.setScene(rootScene);
        if (guiControllerSettings.isChangeToPrimaryStage()) {
            BlossomsPoGoManager.setNewPrimaryStage(stage);
        } else {
            stage.show();
        }

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
