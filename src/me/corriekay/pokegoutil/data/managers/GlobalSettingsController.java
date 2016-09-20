package me.corriekay.pokegoutil.data.managers;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

/**
 * Global controller to access all settings controllers.
 */
public final class GlobalSettingsController {

    private static GlobalSettingsController instance;
    private LogController logController;

    /**
     * Instantiate a GlobalSettingsController.
     */
    private GlobalSettingsController() {
        consoleSetup();
    }

    /**
     * Setup the GlobalSettingController if it has not been initialized.
     */
    public static void setup() {
        if (instance == null) {
            instance = new GlobalSettingsController();
        }
    }

    /**
     * Get instance of GlobalSettingController.
     *
     * @return instance of GlobalSettingController
     */
    public static GlobalSettingsController getGlobalSettingsController() {
        if (instance == null) {
            setup();
        }
        return instance;
    }

    /**
     * Setup of console.
     */
    private void consoleSetup() {
        logController = new LogController();
        ConsolePrintStream.setup(logController);
    }

    /**
     * Get the log controller instance.
     *
     * @return log controller instance
     */
    public LogController getLogController() {
        return logController;
    }
}
