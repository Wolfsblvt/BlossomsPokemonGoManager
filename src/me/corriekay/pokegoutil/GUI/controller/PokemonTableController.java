package me.corriekay.pokegoutil.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import me.corriekay.pokegoutil.DATA.managers.PokemonBagManager;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokemonTableController extends AnchorPane {
    private final String fxmlLayout = "layout/PokemonTable.fxml";
    private ClassLoader classLoader = getClass().getClassLoader();

    @FXML
    private TableView pokemonTableView;

    private List<TableColumn<PokemonModel, ?>> columns = new ArrayList<>();

    public PokemonTableController(AnchorPane pane) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(classLoader.getResource(fxmlLayout));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(classLoader);
        try {
            final Node root = (Node)loader.load();
            pane.getChildren().clear();
            pane.getChildren().addAll(root);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        pokemonTableView.getColumns().clear();
        setColumns();
        pokemonTableView.getColumns().addAll(columns);
        pokemonTableView.setItems(PokemonBagManager.getAllPokemon());
        pokemonTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /*AnchorPane.setTopAnchor(pokemonTableView, 0.0);
        AnchorPane.setLeftAnchor(pokemonTableView, 0.0);
        AnchorPane.setRightAnchor(pokemonTableView, 0.0);
        AnchorPane.setBottomAnchor(pokemonTableView, 0.0);*/
    }

    /**
     * data types:
     * 0 Integer - Pokemon Number
     * 1 String - Nickname
     * 2 String - Type / Pokemon
     * 3 String(Percentage) - IV Rating
     * 4 Double - Level
     * 5 Integer - Attack
     * 6 Integer - Defense
     * 7 Integer - Stamina
     * 8 String - Type 1
     * 9 String - Type 2
     * 10 String - Move 1
     * 11 String - Move 2
     * 12 Integer - CP
     * 13 Integer - HP
     * 14 Integer - Max CP (Current)
     * 15 Integer - Max CP
     * 16 Integer - Max Evolved CP (Current)
     * 17 Integer - Max Evolved CP
     * 18 Integer - Candies of type
     * 19 String(Nullable Int) - Candies to Evolve
     * 20 Integer - Star Dust to level
     * 21 String - Pokeball Type
     * 22 String(Date) - Caught at
     * 23 Boolean - Favorite
     * 24 Long - duelAbility
     * 25 Integer - gymOffense
     * 26 Integer - gymDefense
     * 27 String(Percentage) - Move 1 Rating
     * 28 String(Percentage) - Move 2 Rating
     * 29 String(Nullable Int) - CP Evolved
     */
    private void setColumns() {
        columns.clear();

        TableColumn<PokemonModel, Integer> numberCol = new TableColumn<>("Id");
        numberCol.setCellValueFactory(cellData -> cellData.getValue().numIdProperty().asObject());
        columns.add(numberCol);

        TableColumn<PokemonModel, String> nickCol = new TableColumn<>("Nickname");
        nickCol.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        columns.add(nickCol);

        TableColumn<PokemonModel, String> speciesCol = new TableColumn<>("Species");
        speciesCol.setCellValueFactory(cellData -> cellData.getValue().speciesProperty());
        columns.add(speciesCol);

        TableColumn<PokemonModel, String> ivCol = new TableColumn<>("IV %");
        ivCol.setCellValueFactory(cellData -> cellData.getValue().IVProperty());
        columns.add(ivCol);

        TableColumn<PokemonModel, Double> lvlCol = new TableColumn<>("Lvl");
        lvlCol.setCellValueFactory(cellData -> cellData.getValue().levelProperty().asObject());
        columns.add(lvlCol);

        TableColumn<PokemonModel, Integer> atkCol = new TableColumn<>("Atk");
        atkCol.setCellValueFactory(cellData -> cellData.getValue().atkProperty().asObject());
        columns.add(atkCol);

        TableColumn<PokemonModel, Integer> defCol = new TableColumn<>("Def");
        defCol.setCellValueFactory(cellData -> cellData.getValue().defProperty().asObject());
        columns.add(defCol);

        TableColumn<PokemonModel, Integer> stamCol = new TableColumn<>("Stam");
        stamCol.setCellValueFactory(cellData -> cellData.getValue().stamProperty().asObject());
        columns.add(stamCol);
    }
}
