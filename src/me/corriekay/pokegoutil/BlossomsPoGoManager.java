package me.corriekay.pokegoutil;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javafx.stage.Stage;
import me.corriekay.pokegoutil.data.managers.AccountController;
import me.corriekay.pokegoutil.data.managers.GlobalSettingsController;
import me.corriekay.pokegoutil.gui.controller.ChooseGuiWindowController;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.windows.WindowStuffHelper;

/**
 * The main project class. Contains the runtime stuff.
 */
public class BlossomsPoGoManager {

    private static Stage sPrimaryStage;

    /**
     * Entry point of the application.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        GlobalSettingsController.setup();
        //        launch(args);
        new BlossomsPoGoManager().start(null);
    }

    /**
     * Get the current primary stage.
     *
     * @return current primary stage
     */
    public static Stage getPrimaryStage() {
        return sPrimaryStage;
    }

    /**
     * Set the new primary stage and hide the previous.
     *
     * @param stage new primary stage
     */
    public static void setNewPrimaryStage(final Stage stage) {
        if (sPrimaryStage != null && sPrimaryStage.isShowing()) {
            sPrimaryStage.hide();
        }
        sPrimaryStage = stage;
    }

    //    @Override
    /**
     * Legacy start method from JavaFX nature. 
     * @param primaryStage Received when have JavaFX nature
     */
    public void start(final Stage primaryStage) {
        setupGlobalExceptionHandling();

        if (ConfigNew.getConfig().getBool(ConfigKey.DEVELOPFLAG)) {
            new ChooseGuiWindowController();
            sPrimaryStage.show();
        } else {
            openOldGui();
        }
    }

    /**
     * Sets up the global exception handler.
     */
    private static void setupGlobalExceptionHandling() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            exception.printStackTrace();

            // Gather exception messages
            final List<String> result = new ArrayList<>();
            Throwable current = exception;
            while (current != null) {
                result.add(current.getClass().getSimpleName() + StringLiterals.COLON_SEPARATOR + current.getLocalizedMessage());
                current = current.getCause();
            }
            SwingUtilities.invokeLater(() -> {
                final String[] options = new String[] {"Continue anyway", "Exit"};
                final int continueChoice = JOptionPane.showOptionDialog(
                        WindowStuffHelper.ALWAYS_ON_TOP_PARENT,
                        String.join(StringLiterals.NEWLINE, result)
                        + StringLiterals.NEWLINE
                        + StringLiterals.NEWLINE + "Application got a critical error."
                        + StringLiterals.NEWLINE + "You can report the error on GitHub or Discord."
                        + StringLiterals.NEWLINE
                        + StringLiterals.NEWLINE + "It is possible to continue here, but do note that the application might not work as expected."
                        + StringLiterals.NEWLINE + "Close and restart if that's the case.",
                        "General Unhandled Error",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null, options, options[0]);
                if (continueChoice == 1) {
                    // If exit is chosen, we exit here.
                    System.exit(-1);
                }
            });
        });
    }

    /**
     * Opens the old GUI.
     */
    private void openOldGui() {
        //        SwingUtilities.invokeLater(() -> {
        UIHelper.setNativeLookAndFeel();
        AccountController.initialize();
        AccountController.logOn();
        //        });
    }
}
