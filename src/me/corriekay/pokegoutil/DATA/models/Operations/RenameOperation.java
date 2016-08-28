package me.corriekay.pokegoutil.DATA.models.Operations;

import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class RenameOperation extends Operation {

    public RenameOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMResult doOperation() {
        return new BPMResult("Not implemented");
    }

    @Override
    protected int getMaxDelay() {
        return getConfig().getInt(ConfigKey.DELAY_RENAME_MAX);
    }

    @Override
    protected int getMinDelay() {
        return getConfig().getInt(ConfigKey.DELAY_RENAME_MIN);
    }

    @Override
    public OperationID getOperationID() {
        return OperationID.RENAME;
    }

}
