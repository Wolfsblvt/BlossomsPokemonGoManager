package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.*;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import org.apache.commons.lang3.StringUtils;

public final class PokemonUtils {
    /** Prevent initializing this class. */
    private PokemonUtils() {
    }

    /**
     * Damage bonus from a critical hit - currently no damage bonus in game, change when game is fixed
     */
    public static final double CRIT_DAMAGE_BONUS = 0;

    /**
     * Time in ms for a powermove to be performed
     */
    public static final int MOVE2_CHARGE_DELAY_MS = 500;

    public static double ivRating(Pokemon p) {
        if (ConfigNew.getConfig().getBool(ConfigKey.ALTERNATIVE_IV_CALCULATION)) {
            PokemonMeta meta = p.getMeta();
            double cpMax = (meta.getBaseAttack() + 15) * Math.pow(meta.getBaseDefense() + 15, 0.5) * Math.pow(meta.getBaseStamina() + 15, 0.5);
            double cpMin = meta.getBaseAttack() * Math.pow(meta.getBaseDefense(), 0.5) * Math.pow(meta.getBaseStamina(), 0.5);
            double cpIv = (meta.getBaseAttack() + p.getIndividualAttack()) * Math.pow(meta.getBaseDefense() + p.getIndividualDefense(), 0.5) * Math.pow(meta.getBaseStamina() + p.getIndividualStamina(), 0.5);
            return (cpIv - cpMin) / (cpMax - cpMin);
        } else {
            return (p.getIndividualAttack() + p.getIndividualDefense() + p.getIndividualStamina()) / 45.0;
        }
    }

    public static String convertTeamColorToName(int teamValue) {
        Team[] teams = Team.values();

        for (Team team : teams) {
            if (team.getValue() == teamValue) {
                return StringUtils.capitalize(team.toString().toLowerCase().replaceAll("team_", ""));
            }
        }
        return "UNKNOWN_TEAM";
    }

    public static String moveRating(Pokemon p, boolean primary) {
        PokemonMeta pMeta = p.getMeta();

        double highestDps = 0;
        PokemonMove[] moves = (primary) ? pMeta.getQuickMoves() : pMeta.getCinematicMoves();
        for (PokemonMove move : moves) {
            double dps = dpsForMove(p, move, primary);
            if (dps > highestDps) highestDps = dps;
        }

        // Now rate it
        double currentDps = dpsForMove(p, primary);
        return Utilities.percentageWithTwoCharacters(currentDps, highestDps);
    }

    /**
     * Calculates the no weave dps for current move. Just plain damage, without dodging or any other attack.
     *
     * @param p       A Pokemon object.
     * @param primary If it should be calculated for the primary more or the secondary.
     * @return The clean dps.
     */
    public static double dpsForMove(Pokemon p, boolean primary) {
        PokemonMove move = (primary) ? p.getMove1() : p.getMove2();
        return dpsForMove(p, move, primary);
    }

    /**
     * Calculates the no weave dps for current move. Just plain damage, without dodging or any other attack.
     *
     * @param p       A Pokemon object.
     * @param move    The move to calculate the dps for.
     * @param primary If it should be calculated for the primary more or the secondary.
     * @return The clean dps.
     */
    private static double dpsForMove(Pokemon p, PokemonMove move, boolean primary) {
        PokemonMoveMeta meta = PokemonMoveMetaRegistry.getMeta(move);
        if (primary) {
            double dps1 = (double) meta.getPower() / (double) meta.getTime() * 1000;
            if (p.getMeta().getType1().equals(meta.getType()) || p.getMeta().getType2().equals(meta.getType()))
                dps1 = dps1 * 1.25;
            return dps1;
        } else {
            double dps2 = (double) meta.getPower() / (double) (meta.getTime() + 500) * 1000;
            if (p.getMeta().getType1().equals(meta.getType()) || p.getMeta().getType2().equals(meta.getType()))
                dps2 = dps2 * 1.25;
            return dps2;
        }
    }

    /**
     * Duel Ability is Tankiness * Gym Offense. A reasonable measure if you don't often/ever dodge,
     * as then you can only attack for as long as you  can stay positive on HP.
     *
     * @param p     A Pokemon object
     * @param useIV Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's overall attacking power considering damage, health & defense
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long duelAbility(Pokemon p, boolean useIV) {
        double duelAbility = PokemonUtils.gymOffense(p, useIV) * PokemonUtils.tankiness(p, useIV);
        return Math.round(duelAbility);
    }

    /**
     * Gym Offense takes the better of No Weave/Weave Damage over 100s and multiplies by the
     * Pokemon's base attack to arrive at a ranking of raw damage output.
     *
     * @param p     A Pokemon object
     * @param useIV Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's pure offensive ability over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static double gymOffense(Pokemon p, boolean useIV) {

        int attackIV = (useIV) ? p.getIndividualAttack() : 0;

        return Math.max(PokemonUtils.dpsForMove(p, true) * 100, PokemonUtils.weaveDps(p, 0)) * (p.getMeta().getBaseAttack() + attackIV);
    }

    /**
     * Gym Defense takes the calculated Gym Weave Damage over 100s and multiplies by Tankiness
     * to arrive at a ranking of how much damage a Pokemon will output when defending a gym.
     *
     * @param p     A Pokemon object
     * @param useIV Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's AI controlled gym defense over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long gymDefense(Pokemon p, boolean useIV) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        return gymDefense(p.getPokemonId(), pm1, pm2, p.getIndividualAttack(), p.getIndividualDefense(), p.getIndividualStamina(), useIV);
    }

    /**
     * Gym Defense takes the calculated Gym Weave Damage over 100s and multiplies by Tankiness
     * to arrive at a ranking of how much damage a Pokemon will output when defending a gym.
     *
     * @param p     A Pokemon object
     * @param useIV Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's AI controlled gym defense over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long gymDefense(PokemonId pokemonId, PokemonMoveMeta pm1, PokemonMoveMeta pm2, int attackIV, int defenseIV, int staminaIV, boolean useIV) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        attackIV = (useIV) ? attackIV : 0;

        double gymDefense = PokemonUtils.weaveDps(pokemonId, pm1, pm2, 2000) * (meta.getBaseAttack() + attackIV) * PokemonUtils.tankiness(pokemonId, defenseIV, staminaIV, useIV);
        return Math.round(gymDefense);
    }

    /**
     * Tankiness is basically Base HP * Base Def. An approximation of a Pokemon's relative ability
     * to soak damage compared to other species.
     * <p>
     * Used for duel ability & gym defense calculations
     *
     * @param p     A Pokemon object
     * @param useIV Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's tankiness :)
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long tankiness(Pokemon p, boolean useIV) {
        return tankiness(p.getPokemonId(), p.getIndividualDefense(), p.getIndividualStamina(), useIV);
    }

    /**
     * Tankiness is basically Base HP * Base Def. An approximation of a Pokemon's relative ability
     * to soak damage compared to other species.
     * <p>
     * Used for duel ability & gym defense calculations
     *
     * @param pokemonId The id of the pokemon
     * @param defenseIV The defenseIV of the pokemon
     * @param staminaIV The staminaIV of the pokemon
     * @param useIV     Use a pokemon's IV values in the calculations
     * @return Rating of a Pokemon's tankiness :)
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long tankiness(PokemonId pokemonId, int defenseIV, int staminaIV, boolean useIV) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        defenseIV = (useIV) ? defenseIV : 0;
        staminaIV = (useIV) ? staminaIV : 0;

        return (meta.getBaseStamina() + staminaIV) * (meta.getBaseDefense() + defenseIV);
    }

    /**
     * Weave Damage/100s is determined by figuring out the total Power achieved over 100 seconds
     * by using basic attack enough to charge up enough energy to do a charge attack, and then
     * using charge attack as soon as possible to not waste energy. It is highlighted in green if doing
     * this is the best way to output damage for a moveset.
     *
     * @param p               The pokemon
     * @param additionalDelay Allow a delay in milliseconds for gym offense (0ms) vs gym defense (2000ms)
     * @return Damage over 100 seconds for a Pokemon's moveset
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static double weaveDps(Pokemon p, int additionalDelay) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        return weaveDps(p.getPokemonId(), pm1, pm2, additionalDelay);
    }

    /**
     * Weave Damage/100s is determined by figuring out the total Power achieved over 100 seconds
     * by using basic attack enough to charge up enough energy to do a charge attack, and then
     * using charge attack as soon as possible to not waste energy. It is highlighted in green if doing
     * this is the best way to output damage for a moveset.
     *
     * @param pokemonId       The id of the pokemon
     * @param pm1             The first move of the pokemon
     * @param pm2             The second move of the pokemon
     * @param additionalDelay Allow a delay in milliseconds for gym offense (0ms) vs gym defense (2000ms)
     * @return Damage over 100 seconds for a Pokemon's moveset
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static double weaveDps(PokemonId pokemonId, PokemonMoveMeta pm1, PokemonMoveMeta pm2, int additionalDelay) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        double moveOneStab = (meta.getType1().equals(pm1.getType()) || meta.getType2().equals(pm1.getType())) ? 1.25 : 1;
        double moveTwoStab = (meta.getType1().equals(pm2.getType()) || meta.getType2().equals(pm2.getType())) ? 1.25 : 1;

        //Translation reference
        //R = Move 1 Power
        //S = Move 1 Stab
        //T = Move 1 Speed
        //U = Move 1 Energy
        //X = Move 2 Power
        //Y = Move 2 Stab
        //Z = Move 2 Crit Chance
        //AA = Move 2 Speed
        //AB = Move 2 Energy
        //AC = Weave Cycle Damage
        //AD = Average Weave Cycle Length (ms)
        //AF = Average Gym Weave Cycle Length (ms)
        //AJ1 = Crit Damage Bonus
        //AL1 = Charge Delay

        //=IF(AB2=100,CEILING(AB2/U2),AB2/U2)
        double weaveEnergyUsageRatio = (Math.abs(pm2.getEnergy()) == 100) ? Math.ceil((double) Math.abs(pm2.getEnergy()) / (double) pm1.getEnergy()) : (double) Math.abs(pm2.getEnergy()) / (double) pm1.getEnergy();

        //=IF(AB2=100,CEILING(AB2/U2),AB2/U2)*T2+(AA2+$AL$1)
        //=IF(AB2=100,CEILING(AB2/U2),AB2/U2)*(T2+2000)+(AA2+$AL$1)
        double weaveCycleLength = weaveEnergyUsageRatio * (pm1.getTime() + additionalDelay) + (pm2.getTime() + PokemonUtils.MOVE2_CHARGE_DELAY_MS);

        //=FLOOR(100000/AD2)
        //*(X2*(1+Y2*0.25) * (1+($AJ$1*Z2/100)))
        //+CEILING(FLOOR(100000/AD2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))
        //*(R2*(1+(S2*0.25)))
        //+FLOOR((100000-(FLOOR(100000/AD2)*(AA2+$AL$1)+CEILING(FLOOR(100000/AD2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*T2))/T2)
        //*(R2*(1+(S2*0.25)))
        //=FLOOR(100000/AF2)*(X2*(1+Y2*0.25)*(1+($AJ$1*Z2/100)))+CEILING(FLOOR(100000/AF2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*(R2*(1+(S2*0.25)))+FLOOR((100000-(FLOOR(100000/AF2)*(AA2+$AL$1)+CEILING(FLOOR(100000/AF2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*(T2+2000)))/(T2+2000))*(R2*(1+(S2*0.25)))
        double weaveDPS = Math.floor(100000 / weaveCycleLength)
                * (pm2.getPower() * moveTwoStab * (1 + (PokemonUtils.CRIT_DAMAGE_BONUS * pm2.getCritChance())))
                + Math.ceil(Math.floor(100000 / weaveCycleLength) * weaveEnergyUsageRatio)
                * (pm1.getPower() * moveOneStab)
                + Math.floor((100000 - (Math.floor(100000 / weaveCycleLength) * (pm2.getTime() + PokemonUtils.MOVE2_CHARGE_DELAY_MS) + Math.ceil(Math.floor(100000 / weaveCycleLength) * weaveEnergyUsageRatio) * (pm1.getTime() + additionalDelay))) / (pm1.getTime() + additionalDelay))
                * (pm1.getPower() * moveOneStab);

        return weaveDPS;
    }
}
