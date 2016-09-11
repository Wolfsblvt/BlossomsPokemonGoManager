package me.corriekay.pokegoutil;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.stage.Stage;
import me.corriekay.pokegoutil.data.managers.AccountController;
import me.corriekay.pokegoutil.data.managers.GlobalSettingsController;
import me.corriekay.pokegoutil.gui.controller.ChooseGuiWindowController;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;


public class BlossomsPoGoManager extends Application {

    public static final String VERSION = "v0.1.2-alpha.2";
    private static Stage sPrimaryStage;

    public static void main(final String[] args) {
        GlobalSettingsController.setup();
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return sPrimaryStage;
    }

    public static void setNewPrimaryStage(final Stage stage) {
        if (sPrimaryStage != null && sPrimaryStage.isShowing()) {
            sPrimaryStage.hide();
        }
        sPrimaryStage = stage;
    }

    @Override
    public void start(final Stage primaryStage) {

        if (ConfigNew.getConfig().getBool(ConfigKey.DEVELOPFLAG)) {
            new ChooseGuiWindowController();
            sPrimaryStage.show();
        } else {
            openOldGui();
        }
    }

    private void openOldGui() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIHelper.setNativeLookAndFeel();
                AccountController.initialize();
                AccountController.logOn();
            }
        });
    }
}
