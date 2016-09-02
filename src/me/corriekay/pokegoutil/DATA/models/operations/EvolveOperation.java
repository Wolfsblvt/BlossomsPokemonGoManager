package me.corriekay.pokegoutil.DATA.models.operations;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class EvolveOperation extends Operation {

    public EvolveOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMOperationResult doOperation() {
        return new BPMOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return getConfig().getInt(ConfigKey.DELAY_EVOLVE_MAX);
    }

    @Override
    protected int getMinDelay() {
        return getConfig().getInt(ConfigKey.DELAY_EVOLVE_MIN);
    }

    @Override
    public OperationID getOperationID() {
        return OperationID.EVOLVE;
    }

    @Override
    public BPMOperationResult validateOperation() {
        if (pokemon.isInGym()) {
            return new BPMOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        int candies = pokemon.getCandies();
        int candiesToEvolve = pokemon.getCandies2Evlv();

        if (candiesToEvolve == 0) {
            return new BPMOperationResult("Pokemon cannot be evolved", OperationError.NOT_EVOLVABLE);
        }

        if (candies < candiesToEvolve) {
            return new BPMOperationResult(String.format(
                    "Insufficent candies, needed %d but had %d ",
                    candiesToEvolve,
                    candies),
                    OperationError.INSUFFICENT_CANDIES);
        }

        return new BPMOperationResult();
    }

}
