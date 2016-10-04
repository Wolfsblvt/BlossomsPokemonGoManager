package me.corriekay.pokegoutil.gui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.javafx.collections.ObservableListWrapper;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import me.corriekay.pokegoutil.data.managers.PokemonBagManager;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.data.models.operations.Operation;
import me.corriekay.pokegoutil.gui.enums.ColumnId;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

/**
 * The PokemonTableController is use to display the pokemons in a grid.
 */
public class PokemonTableController extends BaseController<GridPane> {
    private static final int TABLE_HEIGHT_PADDING = 30;
    private static final String DASH = "-";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TableView<PokemonModel> pokemonTableView;

    private final List<TableColumn<PokemonModel, ?>> columns = new ArrayList<>();
    private final GridPane gridPane;

    /**
     * Instantiate the PokemonTableController onto the parent pane.
     *
     * @param gridPane parent pane
     */
    public PokemonTableController(final GridPane gridPane) {
        super();
        this.gridPane = gridPane;
        initializeController();
    }

    /**
     * Request confirmation on the selected operation.
     *
     * @param operation operation to be done
     */
    private void comfirmOperation(final OperationId operation) {
        final List<Operation> operations = Operation.generateOperations(operation, getSelectedItems());
        new OperationConfirmationController(new ObservableListWrapper<>(operations));
    }

    /**
     * Get the saved column ordering from config.
     *
     * @return saved column ordering from config
     */
    private ArrayList<ColumnId> getColumnOrderFromConfig() {
        final ArrayList<ColumnId> columnOrder = new ArrayList<>();
        final String config = ConfigNew.getConfig().getString(ConfigKey.COLUMN_ORDER_POKEMON_TABLE);
        final ColumnId[] colIds = ColumnId.values();

        if (config == null || config.isEmpty()) {
            columnOrder.addAll(Arrays.asList(colIds));
        } else {
            final String[] split = config.split(DASH);
            for (final String s : split) {
                columnOrder.add(colIds[Integer.valueOf(s)]);
            }
        }

        return columnOrder;
    }

    /**
     * Get the columns in the grid view.
     *
     * @return columns in the grid view
     */
    public ObservableList<TableColumn<PokemonModel, ?>> getColumns() {
        return pokemonTableView.getColumns();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/PokemonTable.fxml";
    }

    /**
     * Get the selected items.
     *
     * @return selected items
     */
    public ObservableList<PokemonModel> getSelectedItems() {
        return pokemonTableView.getSelectionModel().getSelectedItems();
    }

    /**
     * Initialize the columns in the grid view.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initColumns() {
        columns.clear();

        final ArrayList<ColumnId> columnOrder = getColumnOrderFromConfig();

        columnOrder.forEach(c -> {
            final TableColumn<PokemonModel, Property> col = new TableColumn<>(c.getTitle());
            switch (c) {
                case NICKNAME :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().nicknameProperty());
                    break;
                case NUMBER :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().numIdProperty());
                    break;
                case SPECIES :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().speciesProperty());
                    break;
                case IV :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().ivProperty());
                    break;
                case LEVEL :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().levelProperty());
                    break;
                case ATTACK :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().atkProperty());
                    break;
                case DEFENSE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().defProperty());
                    break;
                case STAMINA :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().stamProperty());
                    break;
                case TYPE1 :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().type1Property());
                    break;
                case TYPE2 :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().type2Property());
                    break;
                case MOVE1 :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().move1Property());
                    break;
                case MOVE2 :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().move2Property());
                    break;
                case CP :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().cpProperty());
                    break;
                case HP :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().hpProperty());
                    break;
                case MAXCPCURRENT :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().maxCpCurrentProperty());
                    break;
                case MAXCP :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().maxCpProperty());
                    break;
                case MAXEVOLVEDCPCURRENT :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().maxEvolvedCpCurrentProperty());
                    break;
                case MAXEVOLVEDCP :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().maxEvolvedCpProperty());
                    break;
                case CANDIES :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().candiesProperty());
                    break;
                case CANDIES2EVOLVE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().candies2EvlvProperty());
                    break;
                case STARDUST2LVL :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().dustToLevelProperty());
                    break;
                case CAUGHTPOKEBALL :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().pokeballProperty());
                    break;
                case CAUGHTDATE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().caughtDateProperty());
                    break;
                case FAVORITE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().isFavoriteProperty());
                    break;
                case DUELABILITY :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().duelAbilityProperty());
                    break;
                case GYMOFFENSE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().gymOffenseProperty());
                    break;
                case GYMDEFENSE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().gymDefenseProperty());
                    break;
                case CPEVOLVED :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().cpEvolvedProperty());
                    break;
                case EVOLVABLE :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().evolvableProperty());
                    break;
                case DUELABILITYIV :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().duelAbilityIvProperty());
                    break;
                case GYMOFFENSEIV :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().gymOffenseIvProperty());
                    break;
                case GYMDEFENSEIV :
                    col.setCellValueFactory(cellData -> (Property) cellData.getValue().gymDefenseIvProperty());
                    break;
            }
            columns.add(col);
        });
    }

    @FXML
    private void initialize() {
        pokemonTableView.getColumns().clear();
        initColumns();
        pokemonTableView.getColumns().addAll(columns);
        pokemonTableView.setItems(PokemonBagManager.getAllPokemon());
        pokemonTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pokemonTableView.prefHeightProperty().bind(Bindings.size(pokemonTableView.getItems())
                .multiply(pokemonTableView.getFixedCellSize()).add(TABLE_HEIGHT_PADDING));
        pokemonTableView.getColumns().addListener(
                (ListChangeListener<? super TableColumn<PokemonModel, ?>>) c -> {
                    saveOrderToConfig();
                });
        initRightClickMenu();
    }

    @Override
    public void initializeController() {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(classLoader.getResource(getFxmlLayout()));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(classLoader);
        try {
            final Node root = loader.load();
            gridPane.getChildren().clear();
            gridPane.getChildren().addAll(root);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Right click menu event.
     */
    private void initRightClickMenu() {
        final ContextMenu cm = new ContextMenu();
        final OperationId[] operations = OperationId.values();
        for (int i = 0; i < operations.length; i++) {
            final OperationId operation = operations[i];
            final String actionName = operation.getActionName();
            final MenuItem cmItem = new MenuItem(actionName);
            cmItem.setOnAction(e -> {
                comfirmOperation(operation);
            });
            cm.getItems().add(cmItem);
        }

        pokemonTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cm.show(pokemonTableView, e.getScreenX(), e.getScreenY());
            }
        });
    }

    /**
     * Save column ordering to config.
     */
    private void saveOrderToConfig() {
        String columnOrder = "";
        int i = 0;
        for (final TableColumn<PokemonModel, ?> col : getColumns()) {
            if (i != 0) {
                columnOrder += DASH;
            }
            columnOrder += String.valueOf(ColumnId.get(col.getText()).ordinal());
            i++;
        }

        ConfigNew.getConfig().setString(ConfigKey.COLUMN_ORDER_POKEMON_TABLE, columnOrder);
    }

    @Override
    public void setGuiControllerSettings() {
        // Nothing to set
    }
}
