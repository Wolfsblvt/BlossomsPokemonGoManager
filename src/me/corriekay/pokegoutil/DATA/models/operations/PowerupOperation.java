package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class PowerupOperation extends Operation {

    public PowerupOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMOperationResult doOperation() {
        return new BPMOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_POWERUP_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_POWERUP_MIN);
    }

    @Override
    public OperationID getOperationID() {
        return OperationID.POWERUP;
    }

    @Override
    public BPMOperationResult validateOperation()
            throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        if (pokemon.isInGym()) {
            return new BPMOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        int candies = pokemon.getCandies();
        int candiesToPowerup = pokemon.getCandyCostsForPowerup();
        if (candies < candiesToPowerup) {
            return new BPMOperationResult(String.format(
                    "Insufficent candies, needed %d but had %d ",
                    candiesToPowerup,
                    candies),
                    OperationError.INSUFFICENT_CANDIES);
        }

        int stardust = AccountManager.getInstance().getPlayerAccount().getStardust();
        int stardustToPowerUp = pokemon.getStardustCostsForPowerup();

        if (stardust < stardustToPowerUp) {
            return new BPMOperationResult(String.format(
                    "Insufficent stardust, needed %d but had %d ",
                    stardustToPowerUp,
                    stardust),
                    OperationError.INSUFFICENT_STARDUSTS);
        }

        return new BPMOperationResult();
    }
}
