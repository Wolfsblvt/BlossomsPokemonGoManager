package me.corriekay.pokegoutil.DATA.models.operations;

import java.util.ArrayList;
import java.util.List;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.DATA.models.BpmOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

public abstract class Operation {

    public static List<Operation> generateOperations(
            OperationId operationID,
            ObservableList<PokemonModel> pokemonList) {
        List<Operation> operationList = new ArrayList<Operation>();

        pokemonList.forEach(model -> {
            Operation operation = generateOperation(operationID, model);
            operationList.add(operation);
        });

        return operationList;
    }

    public static Operation generateOperation(OperationId operationId, PokemonModel model) {
        switch (operationId) {
            case EVOLVE:
                return new EvolveOperation(model);
            case FAVORITE:
                return new FavoriteOperation(model);
            case POWERUP:
                return new PowerupOperation(model);
            case RENAME:
                return new RenameOperation(model);
            case TRANSFER:
                return new TransferOperation(model);
            default:
                throw new IllegalArgumentException(
                        String.format("OperationID <%s> has not been handled!", operationId));
        }
    }

    private Integer delay;
    public PokemonModel pokemon;

    protected ConfigNew config = ConfigNew.getConfig();

    protected Operation() {
        // For mocking
    }

    public Operation(PokemonModel pokemon) {
        this.delay = getRandomDelay();
        this.pokemon = pokemon;
    }

    protected abstract BpmOperationResult doOperation() throws LoginFailedException, RemoteServerException;

    public BpmOperationResult execute() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        BpmOperationResult result = validateOperation();

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

    public abstract OperationId getOperationID();

    private int getRandomDelay() {
        return Utilities.getRandom(getMaxDelay(), getMaxDelay());
    }

    @Override
    public String toString() {
        return getOperationID().getActionName() + " " + pokemon.getSummary();
    }

    public abstract BpmOperationResult validateOperation()
            throws InvalidCurrencyException, LoginFailedException, RemoteServerException;
}
