package me.corriekay.pokegoutil.DATA.models.operations;

import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class EvolveOperation extends Operation {

    public EvolveOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMResult doOperation() {
        return new BPMResult("Not implemented");
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
    protected BPMResult validateOperation() {
        if (pokemon.isInGym()) {
            return new BPMResult("Pokemon is in gym");
        }

        int candies = pokemon.getCandies();
        int candiesToEvolve = pokemon.getCandies2Evlv();
        
        if (candiesToEvolve == 0) {
            return new BPMResult("Pokemon cannot be evolved");
        }

        if (candies < candiesToEvolve) {
            return new BPMResult(String.format(
                    "Insufficent candies, needed %d but had %d ",
                    candiesToEvolve,
                    candies));
        }

        return new BPMResult();
    }

}
