package me.corriekay.pokegoutil;

import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import me.corriekay.pokegoutil.data.managers.GlobalSettingsController;
import me.corriekay.pokegoutil.gui.controller.LoginController;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.FileHelper;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.utils.windows.WindowStuffHelper;

/**
 * The main project class. Contains the runtime stuff.
 */
public class BlossomsPoGoManager extends Application {

    private static Stage sPrimaryStage;
    private static JFrame mainWindow;

    /**
     * Entry point of the application.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        GlobalSettingsController.setup();
        launch(args);
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

    /**
     * Get the current main window.
     *
     * @return current main window
     */
    public static JFrame getMainWindow() {
        return mainWindow;
    }

    /**
     * Set the new main window.
     *
     * @param window main window
     */
    public static void setNewMainWindow(final JFrame window) {
        if(BlossomsPoGoManager.mainWindow != null && BlossomsPoGoManager.mainWindow.isVisible()) {
            BlossomsPoGoManager.mainWindow.setVisible(false);
        }
        BlossomsPoGoManager.mainWindow = window;
    }

    @Override
    public void start(final Stage primaryStage) {
        setupGlobalExceptionHandling();
        new LoginController();
        SwingUtilities.invokeLater(() -> {
            UIHelper.setNativeLookAndFeel();
            JFrame frame = new JFrame();
            final JFXPanel jfxPanel = new JFXPanel();
            frame.getContentPane().add(jfxPanel);
            frame.setSize(310, 370);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(FileHelper.loadImage("icon/PokeBall-icon.png"));
            frame.setTitle(BlossomsPoGoManager.getPrimaryStage().getTitle());
            frame.setVisible(true);
            setNewMainWindow(frame);
            Platform.runLater(() -> {
                jfxPanel.setScene(BlossomsPoGoManager.getPrimaryStage().getScene());
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        Platform.runLater(() -> System.exit(0));
                    }
                });
            });
        });
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
    }
    
//    private void openOldGui() {
//        SwingUtilities.invokeLater(() -> {
//            UIHelper.setNativeLookAndFeel();
//            AccountController.initialize();
//            AccountController.logOn();
//        });
//    }
}
