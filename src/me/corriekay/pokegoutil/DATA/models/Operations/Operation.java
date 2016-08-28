package me.corriekay.pokegoutil.DATA.models.Operations;

import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

abstract class Operation {

    private OperationID id;
    private Integer delay;
    private PokemonModel pokemonModel;
    private ConfigNew config = ConfigNew.getConfig();

    public Operation(OperationID id, PokemonModel pokemon) {
        this.id = id;
        this.delay = getRandomDelay();
        this.pokemonModel = pokemon;
    }

    public abstract BPMResult doOperation();

    public void execute() {
        BPMResult result = doOperation();

        if (result.isSuccess()) {
            System.out.println(id.getActionVerbFinished() + " " + pokemonModel.getSummary());
        } else {
            System.out.println(result.getErrorMessage());
        }
        System.out.println("Waiting " + delay.toString() + " ms before next operation");
        Utilities.sleep(delay);
    }

    public ConfigNew getConfig() {
        return config;
    }

    public Integer getDelay() {
        return delay;
    }

    public OperationID getId() {
        return id;
    }

    public abstract int getMaxDelay();

    public abstract int getMinDelay();

    public PokemonModel getPokemonModel() {
        return pokemonModel;
    }

    public int getRandomDelay() {
        return Utilities.getRandom(getMaxDelay(), getMaxDelay());
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public void setId(OperationID id) {
        this.id = id;
    }

    public void setPokemonModel(PokemonModel pokemonModel) {
        this.pokemonModel = pokemonModel;
    }

    public String toString() {
        return id.getActionName() + " " + pokemonModel.getSummary();
    }
}
