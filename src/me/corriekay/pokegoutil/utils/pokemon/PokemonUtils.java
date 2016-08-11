package me.corriekay.pokegoutil.utils.pokemon;

import com.pokegoapi.api.player.Team;
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
}
