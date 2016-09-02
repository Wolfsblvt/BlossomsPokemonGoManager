package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.DATA.models.BPMResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class PowerupOperation extends Operation {

    public PowerupOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMResult doOperation() {
        return new BPMResult("Not implemented");
    }

    @Override
    protected int getMaxDelay() {
        return getConfig().getInt(ConfigKey.DELAY_POWERUP_MAX);
    }

    @Override
    protected int getMinDelay() {
        return getConfig().getInt(ConfigKey.DELAY_POWERUP_MIN);
    }

    @Override
    public OperationID getOperationID() {
        return OperationID.POWERUP;
    }

    @Override
    public BPMResult validateOperation() throws InvalidCurrencyException, LoginFailedException, RemoteServerException{
        if (pokemon.isInGym()) {
            return new BPMResult("Pokemon is in gym");
        }

        int candies = pokemon.getCandies();
        int candiesToEvolve = pokemon.getCandyCostsForPowerup();
        if (candies < candiesToEvolve) {
            return new BPMResult(String.format(
                    "Insufficent candies, needed %d but had %d ",
                    candiesToEvolve,
                    candies));
        }
        
        int stardust = AccountManager.getInstance().getPlayerAccount().getStardust();
        int stardustToPowerUp = pokemon.getStardustCostsForPowerup();
        
        if(stardust < stardustToPowerUp){
            return new BPMResult(String.format(
                    "Insufficent stardust, needed %d but had %d ",
                    stardustToPowerUp,
                    stardust));
        }

        return new BPMResult();
    }
}
