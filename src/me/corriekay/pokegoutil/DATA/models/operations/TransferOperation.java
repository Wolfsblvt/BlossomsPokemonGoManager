package me.corriekay.pokegoutil.DATA.models.operations;

import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class TransferOperation extends Operation {

    public TransferOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMResult doOperation() {                
        return new BPMResult("Not implemented");
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
    public BPMResult validateOperation() {
        if (pokemon.isIsFavorite()) {
            return new  BPMResult("Pokemon is favourite.");
        }
        
        if (pokemon.isInGym()) {
            return new BPMResult("Pokemon is in gym");
        }
        
        return new BPMResult();
    }
}
