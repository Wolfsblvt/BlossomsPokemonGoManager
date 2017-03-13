package me.corriekay.pokegoutil.data.models.operations;

import java.util.ArrayList;
import java.util.List;

import com.pokegoapi.exceptions.InvalidCurrencyException;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

public abstract class Operation {

    public static List<Operation> generateOperations(
            final OperationId operationId,
            final ObservableList<PokemonModel> pokemonList) {
        final List<Operation> operationList = new ArrayList<Operation>();

        pokemonList.forEach(model -> {
            final Operation operation = generateOperation(operationId, model);
            operationList.add(operation);
        });

        return operationList;
    }

    public static Operation generateOperation(final OperationId operationId, final PokemonModel model) {
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

    public Operation(final PokemonModel pokemon) {
        this.delay = getRandomDelay();
        this.pokemon = pokemon;
    }

    /**
     * Abstract method to create the operation.
     * @return BpmOperationResult with the operation created.
     */
    protected abstract BpmOperationResult doOperation();

    /**
     * Execute the desired operation.
     * @return BpmOperationResult with the result of the operation
     * @throws InvalidCurrencyException when an invalidCurrencyException occours
     */
    public BpmOperationResult execute() throws InvalidCurrencyException {
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

    public abstract OperationId getOperationId();

    private int getRandomDelay() {
        return Utilities.getRandom(getMaxDelay(), getMaxDelay());
    }

    @Override
    public String toString() {
        return getOperationId().getActionName() + " " + pokemon.getSummary();
    }

    public abstract BpmOperationResult validateOperation()
            throws InvalidCurrencyException;
}
