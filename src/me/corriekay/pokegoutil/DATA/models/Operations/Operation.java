package me.corriekay.pokegoutil.DATA.models.Operations;

import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

abstract class Operation {

    private Integer delay;
    private PokemonModel pokemonModel;
    private ConfigNew config = ConfigNew.getConfig();

    public Operation(PokemonModel pokemon) {
        this.delay = getRandomDelay();
        this.pokemonModel = pokemon;
    }

    protected abstract BPMResult doOperation();

    public void execute() {
        BPMResult result = doOperation();

        if (result.isSuccess()) {
            System.out.println(getOperationID().getActionVerbFinished() + " " + pokemonModel.getSummary());
        } else {
            System.out.println(result.getErrorMessage());
        }
        System.out.println("Waiting " + delay.toString() + " ms before next operation");
        Utilities.sleep(delay);
    }

    protected ConfigNew getConfig() {
        return config;
    }

    public Integer getDelay() {
        return delay;
    }

    protected abstract int getMaxDelay();

    protected abstract int getMinDelay();

    public abstract OperationID getOperationID();

    public PokemonModel getPokemonModel() {
        return pokemonModel;
    }

    private int getRandomDelay() {
        return Utilities.getRandom(getMaxDelay(), getMaxDelay());
    }

    @Override
    public String toString() {
        return getOperationID().getActionName() + " " + pokemonModel.getSummary();
    }
}
