package me.corriekay.pokegoutil.GUI.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.DATA.models.operations.Operation;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;

import java.io.IOException;
import java.net.URL;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

public class OperationConfirmationController extends AnchorPane {

    private final String fxmlLayout = "layout/ConfirmOperationWindow.fxml";
    private final URL icon;
    private ClassLoader classLoader = getClass().getClassLoader();

    private Scene rootScene;

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

    public OperationConfirmationController(ObservableList<Operation> operations) {
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
        stage.setTitle("Please Review Operations");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(rootScene);

        operationListView.setItems(operations);

        stage.show();
    }

    @FXML
    public void initialize() {
        startBtn.setOnAction(this::startOperations);
        pauseBtn.setOnAction(this::pauseOperations);
        cancelBtn.setOnAction(this::cancelOperations);
    }

    private void startOperations(ActionEvent actionEvent) {
        operationListView.getItems().forEach(operation -> {
            PokemonModel pokemon = operation.pokemon;
            try {
                BPMOperationResult result = operation.execute();
                if (result.isSuccess()) {
                    System.out.println(String.format(
                            "%s %s",
                            operation.getOperationID().getActionVerbFinished(),
                            pokemon.getSummary()));
                } else {
                    System.out.println(String.format(
                            "Skipping %s due to <%s>",
                            pokemon.getSummary(),
                            result.getErrorMessage()));
                }
            } catch (InvalidCurrencyException | LoginFailedException | RemoteServerException e) {
                System.out.println(String.format(
                        "Error %s %s! %s",
                        operation.getOperationID().getActionVerbDuring(),
                        PokeHandler.getLocalPokeName(pokemon.getPokemon()),
                        Utilities.getRealExceptionMessage(e)));
            }
            operation.doDelay();
        });
        System.out.println("Batch Operation Done");
    }

    private void pauseOperations(ActionEvent actionEvent) {
    }

    private void cancelOperations(ActionEvent actionEvent) {
        rootScene.getWindow().hide();
    }
}
