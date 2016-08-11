package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMoveMeta;
import com.pokegoapi.api.pokemon.PokemonMoveMetaRegistry;
import org.apache.commons.lang3.StringUtils;

public final class PokemonUtils {
    private PokemonUtils() { /* Prevent initializing this class */ }

    public static String convertTeamColorToName(int teamValue) {
        Team[] teams = Team.values();

        for (Team team : teams) {
            if (team.getValue() == teamValue) {
                return StringUtils.capitalize(team.toString().toLowerCase().replaceAll("team_", ""));
            }
        }
        return "UNKNOWN_TEAM";
    }

    public static Double dpsForMove1(Pokemon p) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        Double dps1 = (double) pm1.getPower() / (double) pm1.getTime() * 1000;
        if (p.getMeta().getType1().equals(pm1.getType()) || p.getMeta().getType2().equals(pm1.getType()))
            dps1 = dps1 * 1.25;
        return dps1;
    }

    public static Double dpsForMove2(Pokemon p) {
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        Double dps2 = (double) pm2.getPower() / (double) (pm2.getTime() + 500) * 1000;
        if (p.getMeta().getType1().equals(pm2.getType()) || p.getMeta().getType2().equals(pm2.getType()))
            dps2 = dps2 * 1.25;
        return dps2;
    }
}
