package me.corriekay.pokegoutil.GUI.controller;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class LuckyEggGrinderController extends AnchorPane{

	@FXML
    private ResourceBundle resources;

    @FXML
    private ProgressBar GrinderProgressBar;

    @FXML
    private Button GrindBtn;

    @FXML
    private Label headerLabel;

    @FXML
    private RadioButton caterpieRadioBtn;

    @FXML
    private RadioButton weedleRadioBtn;

    @FXML
    private RadioButton pidgeyRadioBtn;

    @FXML
    private Label progressLabel;

    @FXML
    private Button transferBtn;

    @FXML
    private Pane selectPkPane;

    @FXML
    private ComboBox<String> optionComboBox;

    @FXML
    private Label requestLabel;

    @FXML
    private Button eggActivateBtn;

    @FXML
    private Label eggNbLbl;

    @FXML
    private Label operationLbl;

    public LuckyEggGrinderController() {
    }
    
    @FXML
    void initialize() {
    	
    }
}
