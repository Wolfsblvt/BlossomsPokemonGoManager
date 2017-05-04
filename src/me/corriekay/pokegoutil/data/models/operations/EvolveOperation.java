package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;

import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

public class EvolveOperation extends Operation {

 
    /**
     * Instantiate EvolveOperation. Only used in mocking.
     */
    protected EvolveOperation() {
        // For mocking
        super();
    }

    /**
     * Instantiate EvolveOperation with a pokemon.
     *
     * @param pokemon pokemon to evolve
     */
    public EvolveOperation(final PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BpmOperationResult doOperation() throws LoginFailedException, RemoteServerException {
        
        final EvolutionResult evolutionResult;
        final String erroEvolvingString = "Error evolving %s, result: %s";
        try {
            evolutionResult = pokemon.getPokemon().evolve();
        } catch (CaptchaActiveException e) {
            e.printStackTrace();
            return new BpmOperationResult(String.format(
                    erroEvolvingString,
                    pokemon.getSpecies(),
                    "Captcha active in account"),
                    OperationError.EVOLVE_FAIL);
        } catch (HashException e) {
            return new BpmOperationResult(String.format(
                    erroEvolvingString,
                    pokemon.getSpecies(),
                    "Error with Hash: " + e.getMessage()),
                    OperationError.EVOLVE_FAIL);
        }

        if (!evolutionResult.isSuccessful()) {
            return new BpmOperationResult(String.format(
                    erroEvolvingString,
                    pokemon.getSpecies(),
                    evolutionResult.getResult().toString()),
                    OperationError.EVOLVE_FAIL);
        }
        
        EvolveElement ExEle = new EvolveElement(pokemon.getPokemon());
        EvolveElement NewEle = new EvolveElement(evolutionResult.getEvolvedPokemon());

        pokemon.setPokemon(NewEle.getPokemon());

        final BpmOperationResult result = new BpmOperationResult();

        successMessage(evolutionResult, ExEle, NewEle, result);

        if (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE)){
            result.setNextOperation(OperationId.TRANSFER);
        }

        return result;
    }

    private void successMessage(final EvolutionResult evolutionResult, EvolveElement ExEle, EvolveElement NewEle, final BpmOperationResult result) {
        result.addSuccessMessage(String.format(
                "Evolving %s. Evolve result: %s",
                PokemonUtils.getLocalPokeName(ExEle.getPokemon()),
                evolutionResult.getResult().toString()));

        result.addSuccessMessage(String.format(
                "Stat changes: "
                        + "(Candies: %d[%d-%d+%d], "
                        + "CP: %d[+%d], "
                        + "HP: %d[+%d])",
                        NewEle.candies, ExEle.candies, ExEle.candiesToEvolve, ExEle.candyRefund,
                        NewEle.cp, (NewEle.cp - ExEle.cp),
                        NewEle.hp, (NewEle.hp - ExEle.hp)));
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_EVOLVE_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_EVOLVE_MIN);
    }

    @Override
    public OperationId getOperationId() {
        return OperationId.EVOLVE;
    }

    @Override
    public BpmOperationResult validateOperation() {
        if (pokemon.isInGym()) {
            return new BpmOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        final int candies = pokemon.getCandies();
        final int candiesToEvolve = pokemon.getCandies2Evlv();

        if (candiesToEvolve == 0) {
            return new BpmOperationResult("Pokemon cannot be evolved", OperationError.NOT_EVOLVABLE);
        }

        if (candies < candiesToEvolve) {
            return new BpmOperationResult(String.format(
                    "Insufficent candies, needed %d but had %d ",
                    candiesToEvolve,
                    candies),
                    OperationError.INSUFFICENT_CANDIES);
        }

        return new BpmOperationResult();
    }
}
