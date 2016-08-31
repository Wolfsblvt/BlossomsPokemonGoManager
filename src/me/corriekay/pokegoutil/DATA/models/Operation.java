package me.corriekay.pokegoutil.DATA.models;

import com.pokegoapi.api.pokemon.Pokemon;
import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

import java.util.ArrayList;

@Deprecated
public class Operation {
    private OperationID id;
    private Integer delay;
    private PokemonModel pokemonModel;

    public Operation(OperationID id,PokemonModel pokemon) {
        this.id = id;
        this.delay = setDelayForOperation(id);
        this.pokemonModel = pokemon;
    }

    public static ArrayList<Operation> makeOperationList(String operation, ObservableList<PokemonModel> list) {
        ArrayList<Operation> returnList = new ArrayList<>();
        list.forEach(model -> {
            returnList.add(new Operation(OperationID.get(operation), model));
        });
        return returnList;
    }

    private static Integer setDelayForOperation(OperationID id) {
        ConfigKey minDelayKey;
        ConfigKey maxDelayKey;
        switch (id) {
            case RENAME:
                minDelayKey = ConfigKey.DELAY_RENAME_MIN;
                maxDelayKey = ConfigKey.DELAY_RENAME_MAX;
                break;
            case TRANSFER:
                minDelayKey = ConfigKey.DELAY_TRANSFER_MIN;
                maxDelayKey = ConfigKey.DELAY_TRANSFER_MAX;
                break;
            case POWERUP:
                minDelayKey = ConfigKey.DELAY_POWERUP_MIN;
                maxDelayKey = ConfigKey.DELAY_POWERUP_MAX;
                break;
            case EVOLVE:
                minDelayKey = ConfigKey.DELAY_EVOLVE_MIN;
                maxDelayKey = ConfigKey.DELAY_EVOLVE_MAX;
                break;
            case FAVORITE:
                minDelayKey = ConfigKey.DELAY_FAVORITE_MIN;
                maxDelayKey = ConfigKey.DELAY_FAVORITE_MAX;
                break;
            default:
                return 0;
        }
        ConfigNew config = ConfigNew.getConfig();
        return Utilities.getRandom(config.getInt(minDelayKey), config.getInt(maxDelayKey));
    }

    public void execute(){
        if(this.id == null || this.delay == 0 || this.pokemonModel == null)
            return;
        Pokemon p = this.pokemonModel.getPokemon();
        System.out.println(id.getActionVerbDuring() + " " + pokemonModel.getSummary());
        switch (this.id) {
            case RENAME:
                rename(p);
                break;
            case TRANSFER:
                transfer(p);
                break;
            case POWERUP:
                powerUp(p);
                break;
            case EVOLVE:
                evolve(p);
                break;
            case FAVORITE:
                favorite(p);
                break;
        }
        System.out.println(id.getActionVerbFinished() + " " + pokemonModel.getSummary());
        System.out.println("Waiting " + this.delay.toString() + " ms before next operation");
        Utilities.sleep(this.delay);
    }
    private void rename(Pokemon pokemon) {
        //System.out.println(id.getActionVerbDuring() + pokemonModel.getSummary());
    }

    private void transfer(Pokemon pokemon) {
        //System.out.println(id.getActionVerbDuring() + " " + pokemonModel.getSummary());
    }

    private void powerUp(Pokemon pokemon) {
        //System.out.println(id.getActionVerbDuring() + " " + pokemonModel.getSummary());
    }

    private void evolve(Pokemon pokemon) {
        //System.out.println(id.getActionVerbDuring() + " " + pokemonModel.getSummary());
    }

    private void favorite(Pokemon pokemon) {
        //System.out.println(id.getActionVerbDuring() + " " + pokemonModel.getSummary());
    }

    @Override
    public String toString(){
        return id.getActionName() + " " + pokemonModel.getSummary();
    }
}
