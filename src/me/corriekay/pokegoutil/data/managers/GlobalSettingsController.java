package me.corriekay.pokegoutil.data.managers;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

public class GlobalSettingsController {

    static GlobalSettingsController instance;

    private LogController logController;

    private GlobalSettingsController() {
        consoleSetup();
    }

    public static void setup() {
        if (instance == null) {
            instance = new GlobalSettingsController();
        }
    }

    public static GlobalSettingsController getGlobalSettingsController() {
        if (instance == null) {
            setup();
        }
        return instance;
    }

    private void consoleSetup() {
        logController = new LogController();
        ConsolePrintStream.setup(logController);

        setDefaultCharset("UTF8");
    }

    public LogController getLogController() {
        return logController;
    }

    /**
     * Set the default charset for the console, so that characters are displayed correctly.
     *
     * @param charsetName The name of the charset.
     */
    private void setDefaultCharset(final String charsetName) {
        try {
            final Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, Charset.forName(charsetName));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ConsolePrintStream.printException(ex);
        }
    }
}
