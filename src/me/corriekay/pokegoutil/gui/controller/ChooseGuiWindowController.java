package me.corriekay.pokegoutil.gui.controller;

import javax.swing.SwingUtilities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.data.managers.AccountController;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;

/**
 * The ChooseGuiWindowController is use to handle loading of new or old gui.
 */
public class ChooseGuiWindowController extends BaseController<Pane> {

    @FXML
    private Button oldGuiBtn;

    @FXML
    private Button newGuiBtn;

    public ChooseGuiWindowController() {
        super();
        initializeController();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/ChooseGUIWindow.fxml";
    }

    @FXML
    void initialize() {
        oldGuiBtn.setOnAction(this::onOldGuiBtnClicked);
        newGuiBtn.setOnAction(this::onNewGuiBtnClicked);
    }

    // private void onClose(final WindowEvent windowEvent) {
    // System.exit(0);
    // }

    @FXML
    void onNewGuiBtnClicked(final ActionEvent event) {
        new LoginController();

        BlossomsPoGoManager.getPrimaryStage().show();
    }

    @FXML
    void onOldGuiBtnClicked(final ActionEvent event) {
        rootScene.getWindow().hide();
        SwingUtilities.invokeLater(new Runnable() {


            @Override
            public void run() {
                UIHelper.setNativeLookAndFeel();
                AccountController.initialize();
                AccountController.logOn();
            }
        });
    }

    @Override
    public void setGuiControllerSettings() {
        guiControllerSettings.setTitle("Choose a GUI");
        guiControllerSettings.setResizeable(false);
    }
}
