package me.corriekay.pokegoutil.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class LuckyEggGrinderController extends AnchorPane {

    private final String fxmlLayout = "layout/LuckyEggGrinder.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();
    private Scene rootScene;
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
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource(fxmlLayout));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        rootScene = new Scene(fxmlLoader.getRoot());
        Stage stage = new Stage();
        icon = classLoader.getResource("icon/PokeBall-icon.png");
        stage.getIcons().add(new Image(icon.toExternalForm()));
        stage.setTitle("LuckyEggGrinder");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(rootScene);
        stage.show();
    }

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
