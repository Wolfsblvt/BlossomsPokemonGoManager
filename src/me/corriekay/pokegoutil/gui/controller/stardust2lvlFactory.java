package me.corriekay.pokegoutil.gui.controller;

import javafx.beans.property.Property;
import javafx.scene.control.TableColumn;
import me.corriekay.pokegoutil.data.models.PokemonModel;

public class stardust2lvlFactory implements columsFactory{
    @Override
    public void setValue(TableColumn<PokemonModel, Property> col) {
        col.setCellValueFactory(cellData -> (Property) cellData.getValue().dustToLevelProperty());
    }
}
