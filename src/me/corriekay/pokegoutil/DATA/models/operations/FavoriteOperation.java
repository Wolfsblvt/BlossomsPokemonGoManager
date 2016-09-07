package me.corriekay.pokegoutil.DATA.models.operations;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BpmOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class FavoriteOperation extends Operation {

    /**
     * Instantiate EvolveOperation. Only used in mocking.
     */
    protected FavoriteOperation() {
        // For mocking
        super();
    }

    /**
     * Instantiate FavoriteOperation with a pokemon.
     *
     * @param pokemon pokemon to favorite
     */
    public FavoriteOperation(final PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BpmOperationResult doOperation() {
        return new BpmOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_FAVORITE_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_FAVORITE_MIN);
    }

    @Override
    public OperationId getOperationId() {
        return OperationId.FAVORITE;
    }

    @Override
    public BpmOperationResult validateOperation() {
        // I think we need to check if in gym? Wasn't checked in previous code.
        // if (pokemon.isInGym()) {
        // return new BPMResult("Pokemon is in gym");
        // }

        return new BpmOperationResult();
    }

}
