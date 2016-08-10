package me.corriekay.pokegoutil.GUI.controller;

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

public class LuckyEggGrinderController extends AnchorPane{

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
    private RadioButton noTransferLowerThanRadioBtn;

    @FXML
    private Spinner<?> noTransferLowerThanSpinner;

    @FXML
    private RadioButton transferFavRadioBtn;

    @FXML
    private AnchorPane commandRequestPane;

    @FXML
    private Label headerLabel;

    @FXML
    private RadioButton caterpieRadioBtn1;

    @FXML
    private RadioButton weedleRadioBtn1;

    @FXML
    private RadioButton pidgeyRadioBtn1;

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

    @FXML
    void onEnableOptions(ActionEvent event) {

    }

    @FXML
    void onNoTransferThanIV(ActionEvent event) {

    }

    @FXML
    void onTransferFav(ActionEvent event) {

    }

    
    @FXML
    void initialize() {
    	
    }
}
