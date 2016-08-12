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

    /**
        * Duel Ability is Tankiness * Gym Offense. A reasonable measure if you don't often/ever dodge,
        * as then you can only attack for as long as you  can stay positive on HP.
        *
        * @param p A Pokemon object
        * @return Rating of a Pokemon's overall attacking power considering damage, health & defense
        * @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
        * @see i607ch00
        */
    public static Long duelAbility(Pokemon p) {
        return Long.valueOf(PokemonUtils.gymOffense(p)) * Long.valueOf(PokemonUtils.tankiness(p));
    }

    /**
        * Gym Offense takes the better of No Weave/Weave Damage over 100s and multiplies by the
        * Pokemon's base attack to arrive at a ranking of raw damage output.
        *
        * @param p A Pokemon object
        * @return Rating of a Pokemon's pure offensive ability over time considering move set
        * @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
        * @see i607ch00
        */
    public static Integer gymOffense(Pokemon p) {
        Double gymOffense = Math.max(PokemonUtils.dpsForMove1(p) * 100, PokemonUtils.weaveDPS(p, 0)) * p.getMeta().getBaseAttack();
        return gymOffense.intValue();
    }

    /**
        * Gym Defense takes the calculated Gym Weave Damage over 100s and multiplies by Tankiness
        * to arrive at a ranking of how much damage a Pokemon will output when defending a gym.
        *
        * @param p A Pokemon object
        * @return Rating of a Pokemon's AI controlled gym defense over time considering move set
        * @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
        * @see i607ch00
        */
    public static Long gymDefense(Pokemon p) {
        Double gymDefense = PokemonUtils.weaveDPS(p, 2000) * p.getMeta().getBaseAttack() * PokemonUtils.tankiness(p);
        return gymDefense.longValue();
    }

    /**
        * Tankiness is basically Base HP * Base Def. An approximation of a Pokemon's relative ability
        * to soak damage compared to other species.
        *
        * Used for duel ability & gym defense calculations
        *
        * @param p A Pokemon object
        * @return Rating of a Pokemon's tankiness :)
        * @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
        * @see i607ch00
        */
    public static Integer tankiness(Pokemon p) {
        return p.getMeta().getBaseStamina() * p.getMeta().getBaseDefense();
    }

    /**
        * Weave Damage/100s is determined by figuring out the total Power achieved over 100 seconds
        * by using basic attack enough to charge up enough energy to do a charge attack, and then
        * using charge attack as soon as possible to not waste energy. It is highlighted in green if doing
        * this is the best way to output damage for a moveset.
        *
        * @param p A Pokemon object
        * @param additionalDelay Allow a delay in milliseconds for gym offense (0ms) vs gym defense (2000ms)
        * @return Damage over 100 seconds for a Pokemon's moveset
        * @see https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
        * @see i607ch00
        */
    public static Double weaveDPS(Pokemon p, Integer additionalDelay) {
        Double critDamageBonus = 0.5;
        Integer chargeDelayMS = 500;

        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        Double moveOneStab = (p.getMeta().getType1().equals(pm1.getType()) || p.getMeta().getType2().equals(pm1.getType())) ? 1.25 : 1;
        Double moveTwoStab = (p.getMeta().getType1().equals(pm2.getType()) || p.getMeta().getType2().equals(pm2.getType())) ? 1.25 : 1;

        //=CEILING(AB621/U621)
        //   *(R621*(1+S621*0.25))
        //   +X621*(1+Y621*0.25)*(1+($AJ$1*Z621/100))
        //AB = Move 2 Energy
        //U = Move 1 Energy
        //R = Move 1 Power
        //S = Move 1 Stab
        //X = Move 2 Power
        //Y = Move 2 Stab
        //AJ = Crit Damage Bonus
        //Z = Move 2 Crit Chance
        Double weaveCycleDmg = Math.ceil(Math.abs(pm2.getEnergy()) / pm1.getEnergy())
                * (pm1.getPower() * moveOneStab)
                + pm2.getPower() * moveTwoStab * (1 + (critDamageBonus * pm2.getCritChance()));

        //=CEILING(AB621/U621)
        // *T621
        // +(AA621+$AL$1)
        //=CEILING(AB621/U621)*(T621+2000)+(AA621+$AL$1)
        //AB  = Move 2 Energy
        //U = Move 1 Energy
        //T = Move 1 Speed
        //AA = Move 2 Speed
        //AL1 = Charge Delay
        Double weaveCycleLength = Math.ceil(Math.abs(pm2.getEnergy()) / pm1.getEnergy())
                * (pm1.getTime() + additionalDelay)
                + (pm2.getTime() + chargeDelayMS);

        //=AC621*FLOOR(100000/AD621)+(R621*(1+(S621*0.25)))*FLOOR(MOD(100000,AD621)/T621)
        //=AC621*FLOOR(100000/AF621)+(R621*(1+(S621*0.25)))*FLOOR(MOD(100000,AF621)/(2000+T621))
        //AC = Weave Cycle Damage
        //AD = Weave Cycle Length (ms)
        //R = Move 1  Power
        //S = Move 1  Stab
        //AD = Weave Cycle Length (ms)
        //T = Move 1  Speed
        Double weaveDPS = weaveCycleDmg * Math.floor(100000 / weaveCycleLength)
                + (pm1.getPower() * moveOneStab)
                * Math.floor((100000 % weaveCycleLength) / (additionalDelay + pm1.getTime()));

        return weaveDPS;
    }
}
