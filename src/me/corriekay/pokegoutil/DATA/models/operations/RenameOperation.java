package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BpmOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationId;
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
            throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        // I think we need to check if in gym? Wasn't checked in previous code.
        // if (pokemon.isInGym()) {
        // return new BPMResult("Pokemon is in gym");
        // }

        return new BpmOperationResult();
    }

}
