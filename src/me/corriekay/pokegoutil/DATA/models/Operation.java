package me.corriekay.pokegoutil.DATA.models;

import com.pokegoapi.api.pokemon.Pokemon;
import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.GUI.enums.OperationID;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;

import java.util.ArrayList;

public class Operation {
    private OperationID id;
    private Integer delay;
    private Pokemon pokemon;

    public Operation(OperationID id,Pokemon pokemon) {
        this.id = id;
        this.delay = setDelayForOperation(id);
        this.pokemon = pokemon;
    }

    public static ArrayList<Operation> makeOperationList(String operation, ObservableList<PokemonModel> list) {
        ArrayList<Operation> returnList = new ArrayList<>();
        list.forEach(pokemonModel -> {
            returnList.add(new Operation(OperationID.get(operation), pokemonModel.getPokemon()));
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
        if(this.id == null || this.delay == 0 || this.pokemon == null)
            return;
        switch (this.id) {
            case RENAME:
                rename(this.pokemon);
                break;
            case TRANSFER:
                transfer(this.pokemon);
                break;
            case POWERUP:
                powerUp(this.pokemon);
                break;
            case EVOLVE:
                evolve(this.pokemon);
                break;
            case FAVORITE:
                favorite(this.pokemon);
                break;
        }
    }
    private void rename(Pokemon pokemon) {
        System.out.println("rename");
    }

    private void transfer(Pokemon pokemon) {
        System.out.println("transfer");
    }

    private void powerUp(Pokemon pokemon) {
        System.out.println("powerup");
    }

    private void evolve(Pokemon pokemon) {
        System.out.println("evolve");
    }

    private void favorite(Pokemon pokemon) {
        System.out.println("favorite");
    }

}
