package me.corriekay.pokegoutil.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;

/**
 * The LuckyEggGrinderController is use to handle all lucky egg related actions.
 */
public class LuckyEggGrinderController extends BaseController<AnchorPane> {

    @FXML
    private BorderPane luckyEggGrinderPane;

    @FXML
    private AnchorPane commandFooterPane;

    @FXML
    private ProgressBar GrinderProgressBar;

    @FXML
    private Button GrindBtn;

    @FXML
    private Label progressLabel;

    @FXML
    private Button transferBtn;

    @FXML
    private Label operationLbl;

    @FXML
    private TableView<?> listToTransferTableView;

    @FXML
    private TableColumn<?, ?> nick_tabCol;

    @FXML
    private TableColumn<?, ?> name_tabCol;

    @FXML
    private TableColumn<?, ?> iv_tabCol;

    @FXML
    private TableColumn<?, ?> CP_tabCol;

    @FXML
    private TableColumn<?, ?> ads_tabCol;

    @FXML
    private TableColumn<?, ?> fav_tabCol;

    @FXML
    private AnchorPane optionsPane;

    @FXML
    private ComboBox<?> optionsComboBox;

    @FXML
    private CheckBox noTransferLowerThanRadioBtn;

    @FXML
    private Spinner<?> noTransferLowerThanSpinner;

    @FXML
    private CheckBox transferFavRadioBtn;

    @FXML
    private AnchorPane commandRequestPane;

    @FXML
    private Label headerLabel;

    @FXML
    private CheckBox caterpieRadioBtn1;

    @FXML
    private CheckBox weedleRadioBtn1;

    @FXML
    private CheckBox pidgeyRadioBtn1;

    @FXML
    private Label requestLbl;

    @FXML
    private Button eggActivateBtn;

    @FXML
    private Label eggNbLbl;

    @FXML
    private Label timeLeftOnEggLbl;

    @FXML
    private RadioButton caterpieRadioBtn2;

    @FXML
    private RadioButton weedleRadioBtn2;

    @FXML
    private CheckBox enableOptionsChkBox;

    public LuckyEggGrinderController() {
        super();
        initializeController();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/LuckyEggGrinder.fxml";
    }

    @FXML
    void initialize() {
        // Not done
    }

    @FXML
    void onEnableOptions(final ActionEvent event) {
        // Not done
    }

    @FXML
    void onNoTransferThanIV(final ActionEvent event) {
        // Not done
    }

    @FXML
    void onTransferFav(final ActionEvent event) {
        // Not done
    }

    @Override
    public void setGuiControllerSettings() {
        guiControllerSettings.setTitle("LuckyEggGrinder");
        guiControllerSettings.setStageStyle(StageStyle.UTILITY);
        guiControllerSettings.setResizeable(false);
        guiControllerSettings.setChangeToPrimaryStage(false);
    }
}
