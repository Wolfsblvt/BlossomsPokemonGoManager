package me.corriekay.pokegoutil.DATA.models.operations;

import java.util.ArrayList;
import java.util.List;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

public abstract class Operation {

    public static List<Operation> generateOperations(
            OperationID operationID,
            ObservableList<PokemonModel> pokemonList) {
        List<Operation> operationList = new ArrayList<Operation>();

        pokemonList.forEach(model -> {
            Operation operation;
            switch (operationID) {
                case EVOLVE:
                    operation = new EvolveOperation(model);
                    break;
                case FAVORITE:
                    operation = new FavoriteOperation(model);
                    break;
                case POWERUP:
                    operation = new PowerupOperation(model);
                    break;
                case RENAME:
                    operation = new RenameOperation(model);
                    break;
                case TRANSFER:
                    operation = new TransferOperation(model);
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("OperationID <%s> has not been handled!", operationID));
            }
            operationList.add(operation);
        });

        return operationList;
    }

    private Integer delay;
    public PokemonModel pokemon;

    protected ConfigNew config = ConfigNew.getConfig();

    protected Operation() {
        //For mocking
    }

    public Operation(PokemonModel pokemon) {
        this.delay = getRandomDelay();
        this.pokemon = pokemon;
    }

    protected abstract BPMOperationResult doOperation();

    public BPMOperationResult execute() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        BPMOperationResult result = validateOperation();

        if (result.isSuccess()) {
            result = doOperation();
        }

        return result;
    }

    public void doDelay() {
        System.out.println("Waiting " + delay.toString() + " ms before next operation");
        Utilities.sleep(delay);
    }

    public Integer getDelay() {
        return delay;
    }

    protected abstract int getMaxDelay();

    protected abstract int getMinDelay();

    public abstract OperationID getOperationID();

    private int getRandomDelay() {
        return Utilities.getRandom(getMaxDelay(), getMaxDelay());
    }

    @Override
    public String toString() {
        return getOperationID().getActionName() + " " + pokemon.getSummary();
    }

    public abstract BPMOperationResult validateOperation()
            throws InvalidCurrencyException, LoginFailedException, RemoteServerException;
}
