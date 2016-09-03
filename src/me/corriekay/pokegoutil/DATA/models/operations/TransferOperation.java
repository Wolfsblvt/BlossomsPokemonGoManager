package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;

public class TransferOperation extends Operation {

    public TransferOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMOperationResult doOperation() throws LoginFailedException, RemoteServerException {

        Pokemon poke = pokemon.getPokemon();
        int candies = poke.getCandy();
        Result transferResult = poke.transferPokemon();

        if (transferResult != Result.SUCCESS) {
            return new BPMOperationResult(String.format(
                    "Error transferring %s, result: %s",
                    PokeHandler.getLocalPokeName(poke),
                    transferResult.toString()),
                    OperationError.TRANSFER_FAIL);
        }

        int newCandies = poke.getCandy();
        BPMOperationResult result = new BPMOperationResult();

        result.addSuccessMessage(String.format(
                "Transferring %s, Result: Success!",
                PokeHandler.getLocalPokeName(poke)));

        result.addSuccessMessage(String.format(
                "Stat changes: (Candies : %d[+%d])",
                newCandies,
                (newCandies - candies)));
                
        return result;
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_TRANSFER_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_TRANSFER_MIN);
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
