package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.*;
import me.corriekay.pokegoutil.utils.Utilities;
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

    public static double moveRating(Pokemon p, boolean primary) {
        PokemonMeta pMeta = p.getMeta();

        double highestDps = 0;
        PokemonMove[] moves = (primary) ? pMeta.getQuickMoves() : pMeta.getCinematicMoves();
        for (PokemonMove move : moves) {
            double dps = dpsForMove(p, move, primary);
            if (dps > highestDps) highestDps = dps;
        }

        // Now rate it
        double currentDps = dpsForMove(p, primary);
        return Utilities.percentage(currentDps / highestDps);
    }

    public static double dpsForMove(Pokemon p, boolean primary) {
        PokemonMove move = (primary) ? p.getMove1() : p.getMove2();
        return dpsForMove(p, move, primary);
    }

    private static double dpsForMove(Pokemon p, PokemonMove move, boolean primary) {
        PokemonMoveMeta meta = PokemonMoveMetaRegistry.getMeta(move);
        if (primary) {
            Double dps1 = (double) meta.getPower() / (double) meta.getTime() * 1000;
            if (p.getMeta().getType1().equals(meta.getType()) || p.getMeta().getType2().equals(meta.getType()))
                dps1 = dps1 * 1.25;
            return dps1;
        } else {
            Double dps2 = (double) meta.getPower() / (double) (meta.getTime() + 500) * 1000;
            if (p.getMeta().getType1().equals(meta.getType()) || p.getMeta().getType2().equals(meta.getType()))
                dps2 = dps2 * 1.25;
            return dps2;
        }
    }
}
