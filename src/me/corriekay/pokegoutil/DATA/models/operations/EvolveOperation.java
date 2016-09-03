package me.corriekay.pokegoutil.DATA.models.operations;

import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.DATA.models.BPMOperationResult;
import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;

public class EvolveOperation extends Operation {

    protected EvolveOperation() {
        // For mocking
    }

    public EvolveOperation(PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BPMOperationResult doOperation() throws LoginFailedException, RemoteServerException {
        EvolutionResult evolutionResult = pokemon.getPokemon().evolve();

        if (!evolutionResult.isSuccessful()) {
            return new BPMOperationResult(String.format(
                    "Error evolving %s, result: %s",
                    pokemon.getSpecies(),
                    evolutionResult.getResult().toString()),
                    OperationError.EVOLVE_FAIL);
        }

        Pokemon poke = pokemon.getPokemon();
        int candies = poke.getCandy();
        int candiesToEvolve = poke.getCandiesToEvolve();
        int cp = poke.getCp();
        int hp = poke.getMaxStamina();
        
        Pokemon newPoke = evolutionResult.getEvolvedPokemon();
        int newCandies = newPoke.getCandy();
        int newCp = newPoke.getCp();
        int newHp = newPoke.getStamina();
        int candyRefund = 1;
        
        pokemon.setPokemon(newPoke);
        
        BPMOperationResult result = new BPMOperationResult();
        
        result.addSuccessMessage(String.format(
                "Evolving %s. Evolve result: %s",
                PokeHandler.getLocalPokeName(poke),
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
            result.setNextOperation(OperationID.TRANSFER);
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
