package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.request.RequestFailedException;

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
    protected BpmOperationResult doOperation() {
        
        final EvolutionResult evolutionResult;
        final String erroEvolvingString = "Error evolving %s, result: %s";
        try {
            evolutionResult = pokemon.getPokemon().evolve();
        } catch (RequestFailedException e) {
            e.printStackTrace();
            return new BpmOperationResult(String.format(
                    erroEvolvingString,
                    pokemon.getSpecies(),
                    e.getMessage()),
                    OperationError.EVOLVE_FAIL);
        }

        if (!evolutionResult.isSuccessful()) {
            return new BpmOperationResult(String.format(
                    erroEvolvingString,
                    pokemon.getSpecies(),
                    evolutionResult.getResult().toString()),
                    OperationError.EVOLVE_FAIL);
        }

        final Pokemon poke = pokemon.getPokemon();
        final int candies = poke.getCandy();
        final int candiesToEvolve = poke.getCandiesToEvolve();
        final int cp = poke.getCp();
        final int hp = poke.getMaxStamina();

        final Pokemon newPoke = evolutionResult.getEvolvedPokemon();
        final int newCandies = newPoke.getCandy();
        final int newCp = newPoke.getCp();
        final int newHp = newPoke.getStamina();
        final int candyRefund = 1;

        pokemon.setPokemon(newPoke);

        final BpmOperationResult result = new BpmOperationResult();

        result.addSuccessMessage(String.format(
                "Evolving %s. Evolve result: %s",
                PokemonUtils.getLocalPokeName(poke),
                evolutionResult.getResult().toString()));

        result.addSuccessMessage(String.format(
                "Stat changes: "
                        + "(Candies: %d[%d-%d+%d], "
                        + "CP: %d[+%d], "
                        + "HP: %d[+%d])",
                        newCandies, candies, candiesToEvolve, candyRefund,
                        newCp, (newCp - cp),
                        newHp, (newHp - hp)));

        if (config.getBool(ConfigKey.TRANSFER_AFTER_EVOLVE)){
            result.setNextOperation(OperationId.TRANSFER);
        }

        return result;
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
