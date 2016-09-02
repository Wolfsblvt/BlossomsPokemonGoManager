package me.corriekay.pokegoutil.DATA.models.operations;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class TransferOperation extends Operation {

    public TransferOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMOperationResult doOperation() {
        return new BPMOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return getConfig().getInt(ConfigKey.DELAY_TRANSFER_MAX);
    }

    @Override
    protected int getMinDelay() {
        return getConfig().getInt(ConfigKey.DELAY_TRANSFER_MIN);
    }

    @Override
    public OperationID getOperationID() {
        return OperationID.TRANSFER;
    }

    @Override
    public BPMOperationResult validateOperation() {
        if (pokemon.isIsFavorite()) {
            return new BPMOperationResult("Pokemon is favorite.", OperationError.IS_FAVORITE);
        }

        if (pokemon.isInGym()) {
            return new BPMOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        return new BPMOperationResult();
    }
}
