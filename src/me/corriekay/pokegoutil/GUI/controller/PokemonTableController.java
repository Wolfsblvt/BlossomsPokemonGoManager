package me.corriekay.pokegoutil.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
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

    @FXML
    private ScrollPane scrollPane;

    private List<TableColumn<PokemonModel, ?>> columns = new ArrayList<>();

    public PokemonTableController(AnchorPane pane) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(classLoader.getResource(fxmlLayout));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(classLoader);
        try {
            final Node root = loader.load();
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

        TableColumn<PokemonModel, String> type1Col = new TableColumn<>("Type 1");
        type1Col.setCellValueFactory(cellData -> cellData.getValue().type1Property());
        columns.add(type1Col);

        TableColumn<PokemonModel, String> type2Col = new TableColumn<>("Type 2");
        type2Col.setCellValueFactory(cellData -> cellData.getValue().type1Property());
        columns.add(type2Col);

        TableColumn<PokemonModel, String> move1Col = new TableColumn<>("Move 1");
        move1Col.setCellValueFactory(cellData -> cellData.getValue().move1Property());
        columns.add(move1Col);

        TableColumn<PokemonModel, String> move2Col = new TableColumn<>("Move 2");
        move2Col.setCellValueFactory(cellData -> cellData.getValue().move2Property());
        columns.add(move2Col);

        TableColumn<PokemonModel, Integer> CPCol = new TableColumn<>("CP");
        CPCol.setCellValueFactory(cellData -> cellData.getValue().cpProperty().asObject());
        columns.add(CPCol);

        TableColumn<PokemonModel, Integer> HPCol = new TableColumn<>("HP");
        HPCol.setCellValueFactory(cellData -> cellData.getValue().hpProperty().asObject());
        columns.add(HPCol);

        TableColumn<PokemonModel, Integer> maxCpCurrentCol = new TableColumn<>("Max CP (Current)");
        maxCpCurrentCol.setCellValueFactory(cellData -> cellData.getValue().maxCpCurrentProperty().asObject());
        columns.add(maxCpCurrentCol);

        TableColumn<PokemonModel, Integer> maxCpCol = new TableColumn<>("Max CP");
        maxCpCol.setCellValueFactory(cellData -> cellData.getValue().maxCpProperty().asObject());
        columns.add(maxCpCol);

        TableColumn<PokemonModel, Integer> maxEvolvedCPCurrentCol = new TableColumn<>("Max Evolved CP (Current)");
        maxEvolvedCPCurrentCol.setCellValueFactory(cellData -> cellData.getValue().maxEvolvedCpCurrentProperty().asObject());
        columns.add(maxEvolvedCPCurrentCol);

        TableColumn<PokemonModel, Integer> maxEvolvedCPCol = new TableColumn<>("Max Evolved CP");
        maxEvolvedCPCol.setCellValueFactory(cellData -> cellData.getValue().maxEvolvedCpProperty().asObject());
        columns.add(maxEvolvedCPCol);

        TableColumn<PokemonModel, Integer> candiesCol = new TableColumn<>("Candies");
        candiesCol.setCellValueFactory(cellData -> cellData.getValue().candiesProperty().asObject());
        columns.add(candiesCol);

        TableColumn<PokemonModel, String> candies2EvolveCol = new TableColumn<>("To evolve");
        candies2EvolveCol.setCellValueFactory(cellData -> cellData.getValue().candies2EvlvProperty());
        columns.add(candies2EvolveCol);

        TableColumn<PokemonModel, Integer> dust2LevelCol = new TableColumn<>("Stardust");
        dust2LevelCol.setCellValueFactory(cellData -> cellData.getValue().dustToLevelProperty().asObject());
        columns.add(dust2LevelCol);

        TableColumn<PokemonModel, String> pokeballCol = new TableColumn<>("Caught With");
        pokeballCol.setCellValueFactory(cellData -> cellData.getValue().pokeballProperty());
        columns.add(pokeballCol);

        TableColumn<PokemonModel, String> caughtDateCol = new TableColumn<>("Time Caught");
        caughtDateCol.setCellValueFactory(cellData -> cellData.getValue().caughtDateProperty());
        columns.add(caughtDateCol);

        TableColumn<PokemonModel, Boolean> favoriteCol = new TableColumn<>("Favorite");
        favoriteCol.setCellValueFactory(cellData -> cellData.getValue().isFavoriteProperty());
        columns.add(favoriteCol);

        TableColumn<PokemonModel, Long> duelAbilityCol = new TableColumn<>("Duel Ability");
        duelAbilityCol.setCellValueFactory(cellData -> cellData.getValue().duelAbilityProperty().asObject());
        columns.add(duelAbilityCol);

        TableColumn<PokemonModel, Double> gymOffenseCol = new TableColumn<>("Gym Offense");
        gymOffenseCol.setCellValueFactory(cellData -> cellData.getValue().gymOffenseProperty().asObject());
        columns.add(gymOffenseCol);

        TableColumn<PokemonModel, Long> gymDefenseCol = new TableColumn<>("Gym Defense");
        gymDefenseCol.setCellValueFactory(cellData -> cellData.getValue().gymDefenseProperty().asObject());
        columns.add(gymDefenseCol);

        TableColumn<PokemonModel, String> move1RatingCol = new TableColumn<>("Move 1 Rating");
        move1RatingCol.setCellValueFactory(cellData -> cellData.getValue().move1RatingProperty());
        columns.add(move1RatingCol);

        TableColumn<PokemonModel, String> move2RatingCol = new TableColumn<>("Move 2 Rating");
        move2RatingCol.setCellValueFactory(cellData -> cellData.getValue().move2RatingProperty());
        columns.add(move2RatingCol);

        TableColumn<PokemonModel, String> cpEvolvedCol = new TableColumn<>("CP Evolved");
        cpEvolvedCol.setCellValueFactory(cellData -> cellData.getValue().cpEvolvedProperty());
        columns.add(cpEvolvedCol);

        TableColumn<PokemonModel, String> evolvableCol = new TableColumn<>("Evolvable");
        evolvableCol.setCellValueFactory(cellData -> cellData.getValue().evolvableProperty());
        columns.add(evolvableCol);

        TableColumn<PokemonModel, Long> duelAbilityIVCol = new TableColumn<>("Duel Ability IV");
        duelAbilityIVCol.setCellValueFactory(cellData -> cellData.getValue().duelAbilityIVProperty().asObject());
        columns.add(duelAbilityIVCol);

        TableColumn<PokemonModel, Double> gymOffenseIVCol = new TableColumn<>("Gym Offense IV");
        gymOffenseIVCol.setCellValueFactory(cellData -> cellData.getValue().gymOffenseIVProperty().asObject());
        columns.add(gymOffenseIVCol);

        TableColumn<PokemonModel, Long> gymDefenseIVCol = new TableColumn<>("Gym Defense IV");
        gymDefenseIVCol.setCellValueFactory(cellData -> cellData.getValue().gymDefenseIVProperty().asObject());
        columns.add(gymDefenseIVCol);
    }
}
