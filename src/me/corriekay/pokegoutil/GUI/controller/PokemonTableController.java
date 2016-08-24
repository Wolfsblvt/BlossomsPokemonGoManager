package me.corriekay.pokegoutil.GUI.controller;

import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import me.corriekay.pokegoutil.DATA.managers.PokemonBagManager;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.ColumnID;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PokemonTableController extends AnchorPane {
    private final String fxmlLayout = "layout/PokemonTable.fxml";
    private ClassLoader classLoader = getClass().getClassLoader();

    @FXML
    private ScrollPane scrollPane;

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
        initColumns();
        pokemonTableView.getColumns().addAll(columns);
        pokemonTableView.setItems(PokemonBagManager.getAllPokemon());
        pokemonTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pokemonTableView.getColumns().addListener((ListChangeListener) c -> {
            saveOrderToConfig();
        });
        initRightClickMenu();
    }

    private void initRightClickMenu() {
        final ContextMenu cm = new ContextMenu();
        MenuItem cmItem1 = new MenuItem("Transfer");
        cmItem1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // transfer
                System.out.println("transfer");
            }
        });
        MenuItem cmItem2 = new MenuItem("Level");
        cmItem2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // level
                System.out.println("level");
            }
        });
        MenuItem cmItem3 = new MenuItem("Evolve");
        cmItem3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // evolve
                System.out.println("evolve");
            }
        });
        MenuItem cmItem4 = new MenuItem("Rename");
        cmItem4.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // evolve
                System.out.println("rename");
            }
        });
        cm.getItems().add(cmItem1);
        cm.getItems().add(cmItem2);
        cm.getItems().add(cmItem3);
        cm.getItems().add(cmItem4);

        pokemonTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cm.show(pokemonTableView, e.getScreenX(), e.getScreenY());
            }
        });
    }

    private ArrayList<ColumnID> getColumnOrderFromConfig() {
        ArrayList<ColumnID> list = new ArrayList<>();
        String config = ConfigNew.getConfig().getString(ConfigKey.COLUMN_ORDER_POKEMON_TABLE);
        if (config == null || config.isEmpty())
            return list;
        String[] split = config.split("-");
        ColumnID[] ids = ColumnID.values();
        for (String s : split) {
            list.add(ids[Integer.valueOf(s)]);
        }
        return list;
    }

    private void saveOrderToConfig() {
        String columnOrder = "";
        int i = 0;
        for (Object c : getColumns()){
            if (i!=0)
                columnOrder+="-";
            columnOrder += String.valueOf(ColumnID.get(((TableColumn)c).getText()).ordinal());
            i++;
        }

        ConfigNew.getConfig().setString(ConfigKey.COLUMN_ORDER_POKEMON_TABLE,columnOrder);
    }

    public ObservableList getColumns() {
        return pokemonTableView.getColumns();
    }

    public ObservableList getSelectedItems(){
        return pokemonTableView.getSelectionModel().getSelectedItems();
    }

    private void initColumns() {
        columns.clear();

        ArrayList<ColumnID> columnOrder = getColumnOrderFromConfig();
        // set default values
        if (columnOrder.isEmpty()) {
            ColumnID[] ids = ColumnID.values();
            for (int i = 0; i < ids.length; i++) {
                columnOrder.add(ids[i]);
            }
        }
        columnOrder.forEach(c -> {
            TableColumn<PokemonModel, Property> col = new TableColumn<>(c.getTitle());
            switch (c) {
                case NICKNAME:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().nicknameProperty());
                    break;
                case NUMBER:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().numIdProperty());
                    break;
                case SPECIES:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().speciesProperty());
                    break;
                case IV:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().IVProperty());
                    break;
                case LEVEL:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().levelProperty());
                    break;
                case ATTACK:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().atkProperty());
                    break;
                case DEFENSE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().defProperty());
                    break;
                case STAMINA:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().stamProperty());
                    break;
                case TYPE1:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().type1Property());
                    break;
                case TYPE2:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().type2Property());
                    break;
                case MOVE1:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().move1Property());
                    break;
                case MOVE2:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().move2Property());
                    break;
                case CP:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().cpProperty());
                    break;
                case HP:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().hpProperty());
                    break;
                case MAXCPCURRENT:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().maxCpCurrentProperty());
                    break;
                case MAXCP:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().maxCpProperty());
                    break;
                case MAXEVOLVEDCPCURRENT:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().maxEvolvedCpCurrentProperty());
                    break;
                case MAXEVOLVEDCP:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().maxEvolvedCpProperty());
                    break;
                case CANDIES:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().candiesProperty());
                    break;
                case CANDIES2EVOLVE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().candies2EvlvProperty());
                    break;
                case STARDUST2LVL:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().dustToLevelProperty());
                    break;
                case CAUGHTPOKEBALL:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().pokeballProperty());
                    break;
                case CAUGHTDATE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().caughtDateProperty());
                    break;
                case FAVORITE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().isFavoriteProperty());
                    break;
                case DUELABILITY:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().duelAbilityProperty());
                    break;
                case GYMOFFENSE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().gymOffenseProperty());
                    break;
                case GYMDEFENSE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().gymDefenseProperty());
                    break;
                case MOVE1RATING:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().move1RatingProperty());
                    break;
                case MOVE2RATING:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().move2RatingProperty());
                    break;
                case CPEVOLVED:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().cpEvolvedProperty());
                    break;
                case EVOLVABLE:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().evolvableProperty());
                    break;
                case DUELABILITYIV:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().duelAbilityIVProperty());
                    break;
                case GYMOFFENSEIV:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().gymOffenseIVProperty());
                    break;
                case GYMDEFENSEIV:
                    col.setCellValueFactory(cellData ->(Property)cellData.getValue().gymDefenseIVProperty());
                    break;
            }
            columns.add(col);
        } );
    }
}
