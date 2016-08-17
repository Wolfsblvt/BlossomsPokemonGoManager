package me.corriekay.pokegoutil.GUI.Model;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;


public class PokemonTableViewSelectionModel extends TableView.TableViewSelectionModel {
    /**
     * Builds a default TableViewSelectionModel instance with the provided
     * TableView.
     *
     * @param tableView The TableView upon which this selection model should
     *                  operate.
     * @throws NullPointerException TableView can not be null.
     */
    public PokemonTableViewSelectionModel(TableView tableView) {
        super(tableView);
    }

    @Override
    public ObservableList<TablePosition> getSelectedCells() {
        return null;
    }

    @Override
    public boolean isSelected(int row, TableColumn column) {
        return false;
    }

    @Override
    public void select(int row, TableColumn column) {

    }

    @Override
    public void clearAndSelect(int row, TableColumn column) {

    }

    @Override
    public void clearSelection(int row, TableColumn column) {

    }

    @Override
    public void selectLeftCell() {

    }

    @Override
    public void selectRightCell() {

    }

    @Override
    public void selectAboveCell() {

    }

    @Override
    public void selectBelowCell() {

    }
}
