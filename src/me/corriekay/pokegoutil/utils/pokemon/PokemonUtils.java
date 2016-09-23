package me.corriekay.pokegoutil.utils.pokemon;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.player.Team;

/**
 * General Pokemon helper functions
 */
public final class PokemonUtils {

    /** Prevent initializing this class. */
    private PokemonUtils() {
    }

    /**
     * Convert team int value to string.
     *
     * @param teamValue team int value
     * @return team string value
     */
    public static String convertTeamColorToName(final int teamValue) {
        final Team[] teams = Team.values();

        for (final Team team : teams) {
            if (team.getValue() == teamValue) {
                return StringUtils.capitalize(team.toString().toLowerCase().replaceAll("team_", ""));
            }
        }
        return "UNKNOWN_TEAM";
    }
}

