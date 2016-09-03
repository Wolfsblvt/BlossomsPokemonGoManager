package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BpmOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;

public class TransferOperation extends Operation {

    /**
     * Instantiate TransferOperation. Only used in mocking.
     */
    protected TransferOperation() {
        // For mocking
        super();
    }

    /**
     * Instantiate TransferOperation with a pokemon.
     *
     * @param pokemon pokemon to transfer
     */
    public TransferOperation(final PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BpmOperationResult doOperation() throws LoginFailedException, RemoteServerException {

        final Pokemon poke = pokemon.getPokemon();
        final int candies = poke.getCandy();
        final Result transferResult = poke.transferPokemon();

        if (transferResult != Result.SUCCESS) {
            return new BpmOperationResult(String.format(
                    "Error transferring %s, result: %s",
                    PokeHandler.getLocalPokeName(poke),
                    transferResult.toString()),
                    OperationError.TRANSFER_FAIL);
        }

        final int newCandies = poke.getCandy();
        final BpmOperationResult result = new BpmOperationResult();

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
    public OperationId getOperationId() {
        return OperationId.TRANSFER;
    }

    @Override
    public BpmOperationResult validateOperation() {
        if (pokemon.isIsFavorite()) {
            return new BpmOperationResult("Pokemon is favorite.", OperationError.IS_FAVORITE);
        }

        if (pokemon.isInGym()) {
            return new BpmOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        return new BpmOperationResult();
    }
}
