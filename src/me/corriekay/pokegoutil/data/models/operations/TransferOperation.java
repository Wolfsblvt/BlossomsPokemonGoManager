package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.request.RequestFailedException;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

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
    protected BpmOperationResult doOperation() {

        final Pokemon poke = pokemon.getPokemon();
        final int candies = poke.getCandy();

        final Result transferResult;
        final String errorTransferingString = "Error transferring %s, result: %s";
        try {
            transferResult = poke.transferPokemon();
        } catch (RequestFailedException e) {
            e.printStackTrace();
            return new BpmOperationResult(String.format(
                    errorTransferingString,
                    PokemonUtils.getLocalPokeName(poke),
                    e.getMessage()),
                    OperationError.EVOLVE_FAIL);
        }
        
        if (transferResult != Result.SUCCESS) {
            return new BpmOperationResult(String.format(
                errorTransferingString,
                PokemonUtils.getLocalPokeName(poke),
                transferResult.toString()),
                OperationError.TRANSFER_FAIL);
        }

        final int newCandies = poke.getCandy();
        final BpmOperationResult result = new BpmOperationResult();

        result.addSuccessMessage(String.format(
            "Transferring %s, Result: Success!",
            PokemonUtils.getLocalPokeName(poke)));

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
