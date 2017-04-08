package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.exceptions.InvalidCurrencyException;

import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class RenameOperation extends Operation {

    /**
     * Instantiate RenameOperation. Only used in mocking.
     */
    protected RenameOperation() {
        // For mocking
        super();
    }

    /**
     * Instantiate RenameOperation with a pokemon.
     *
     * @param pokemon pokemon to rename
     */
    public RenameOperation(final PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BpmOperationResult doOperation() {
        return new BpmOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_RENAME_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_RENAME_MIN);
    }

    @Override
    public OperationId getOperationId() {
        return OperationId.RENAME;
    }

    @Override
    public BpmOperationResult validateOperation()
            throws InvalidCurrencyException {
        // I think we need to check if in gym? Wasn't checked in previous code.
        // if (pokemon.isInGym()) {
        // return new BPMResult("Pokemon is in gym");
        // }

        return new BpmOperationResult();
    }

}
