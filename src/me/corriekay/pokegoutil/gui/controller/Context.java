package me.corriekay.pokegoutil.gui.controller;

import javafx.beans.property.Property;
import javafx.scene.control.TableColumn;
import me.corriekay.pokegoutil.data.models.PokemonModel;

public class Context {
    private columsFactory columsfactory;
    
    public Context(columsFactory _columsfactory) {
        this.columsfactory = _columsfactory;
    }
    
    public void setColums(TableColumn<PokemonModel, Property> col) {
        columsfactory.setValue(col);
    }
}
