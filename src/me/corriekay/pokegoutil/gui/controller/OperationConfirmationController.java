package me.corriekay.pokegoutil.gui.controller;

import com.pokegoapi.exceptions.InvalidCurrencyException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.Operation;
import me.corriekay.pokegoutil.utils.Utilities;

/**
 * The OperationConfirmationController is use to handle all operations related actions.
 */
public class OperationConfirmationController extends BaseController<AnchorPane> {

    @FXML
    private ListView<Operation> operationListView;

    @FXML
    private ComboBox<?> chooseSettingComboBox;

    @FXML
    private AnchorPane settingPane;

    @FXML
    private Label waitingLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button cancelBtn;

    public OperationConfirmationController(final ObservableList<Operation> operations) {
        super();
        initializeController();
        operationListView.setItems(operations);
    }

    @FXML
    public void initialize() {
        startBtn.setOnAction(this::startOperations);
        pauseBtn.setOnAction(this::pauseOperations);
        cancelBtn.setOnAction(this::cancelOperations);
    }

    private void startOperations(final ActionEvent actionEvent) {
        operationListView.getItems().forEach(operation -> doOperation(operation));
        System.out.println("Batch Operation Done");
    }

    private void doOperation(final Operation operation){
        final PokemonModel pokemon = operation.pokemon;
        BpmOperationResult result = null;
        try {
            result = operation.execute();
            if (result.isSuccess()) {
                result.getSuccessMessageList().forEach(msg -> System.out.println(msg));

                System.out.println(String.format(
                        "%s %s",
                        operation.getOperationId().getActionVerbFinished(),
                        pokemon.getSummary()));
            } else {
                System.out.println(String.format(
                        "Skipping %s due to <%s>",
                        pokemon.getSummary(),
                        result.getErrorMessage()));
            }
        } catch (InvalidCurrencyException e) {
            System.out.println(String.format(
                    "Error %s %s! %s",
                    operation.getOperationId().getActionVerbDuring(),
                    pokemon.getSpecies(),
                    Utilities.getRealExceptionMessage(e)));
        }
        operation.doDelay();

        if(result != null && result.hasNextOperation()){
            doOperation(Operation.generateOperation(result.getNextOperation(), pokemon));
        }
    }

    private void pauseOperations(final ActionEvent actionEvent) {
    }

    private void cancelOperations(final ActionEvent actionEvent) {
        rootScene.getWindow().hide();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/ConfirmOperationWindow.fxml";
    }

    @Override
    public void setGuiControllerSettings() {
        guiControllerSettings.setTitle("Please Review Operations");
        guiControllerSettings.setStageStyle(StageStyle.UTILITY);
        guiControllerSettings.setResizeable(false);
        guiControllerSettings.setChangeToPrimaryStage(false);
    }
}
