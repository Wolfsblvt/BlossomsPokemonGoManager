package me.corriekay.pokegoutil.utils.pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.*;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class PokemonUtils {
    private static final int NORMAL_MULTIPLIER = 1;
    private static final double STAB_MULTIPLIER = 1.25;

    /**
     * The maximum for an individual value.
     */
    public static final int MAX_IV = 15;

    /**
     * Damage bonus from a critical hit - currently no damage bonus in game, change when game is fixed
     */
    public static final double CRIT_DAMAGE_BONUS = 0;

    /**
     * Time in ms for a powermove to be performed
     */
    public static final int MOVE2_CHARGE_DELAY_MS = 500;

    /** Prevent initializing this class. */
    private PokemonUtils() {
    }

    public static double ivRating(final Pokemon p) {
        if (ConfigNew.getConfig().getBool(ConfigKey.ALTERNATIVE_IV_CALCULATION)) {
            final PokemonMeta meta = p.getMeta();
            final double cpMax = (meta.getBaseAttack() + MAX_IV)
                * Math.pow(meta.getBaseDefense() + MAX_IV, 0.5)
                * Math.pow(meta.getBaseStamina() + MAX_IV, 0.5);
            final double cpMin = meta.getBaseAttack()
                * Math.pow(meta.getBaseDefense(), 0.5)
                * Math.pow(meta.getBaseStamina(), 0.5);
            final double cpIv = (meta.getBaseAttack() + p.getIndividualAttack())
                * Math.pow(meta.getBaseDefense() + p.getIndividualDefense(), 0.5)
                * Math.pow(meta.getBaseStamina() + p.getIndividualStamina(), 0.5);
            return (cpIv - cpMin) / (cpMax - cpMin);
        } else {
            return (p.getIndividualAttack() + p.getIndividualDefense() + p.getIndividualStamina()) / (3 * MAX_IV);
        }
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

    public static String moveRating(final Pokemon p, final boolean primary) {
        final PokemonMeta pMeta = p.getMeta();

        double highestDps = 0;
        final PokemonMove[] moves = primary ? pMeta.getQuickMoves() : pMeta.getCinematicMoves();
        for (final PokemonMove move : moves) {
            PokemonMoveMeta moveMeta = PokemonMoveMetaRegistry.getMeta(move);
            double dps = dpsForMove(p.getPokemonId(), moveMeta, primary);
            if (dps > highestDps) {
                highestDps = dps;
            }
        }

        // Now rate it
        final double currentDps = dpsForMove(p, primary);
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
        PokemonMove move = primary ? p.getMove1() : p.getMove2();
        PokemonMoveMeta moveMeta = PokemonMoveMetaRegistry.getMeta(move);
        return dpsForMove(p.getPokemonId(), moveMeta, primary);
    }

    /**
     * Calculates the no weave dps for current move. Just plain damage, without dodging or any other attack.
     *
     * @param pokemonId The pokemonId to check for.
     * @param moveMeta  The move to calculate the dps for.
     * @param primary   If it should be calculated for the primary more or the secondary.
     * @return The clean dps.
     */
    private static double dpsForMove(PokemonId pokemonId, PokemonMoveMeta moveMeta, boolean primary) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        if (primary) {
            double dps1 = (double) moveMeta.getPower() / (double) moveMeta.getTime() * 1000;
            if (meta.getType1().equals(moveMeta.getType()) || meta.getType2().equals(moveMeta.getType())) {
                dps1 = dps1 * 1.25;
            }
            return dps1;
        } else {
            double dps2 = (double) moveMeta.getPower() / (double) (moveMeta.getTime() + 500) * 1000;
            if (meta.getType1().equals(moveMeta.getType()) || meta.getType2().equals(moveMeta.getType())) {
                dps2 = dps2 * 1.25;
            }
            return dps2;
        }
    }

    /**
     * Duel Ability is Tankiness * Gym Offense. A reasonable measure if you don't often/ever dodge,
     * as then you can only attack for as long as you  can stay positive on HP.
     *
     * @param p A Pokemon object
     * @return Rating of a Pokemon's overall attacking power considering damage, health & defense
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long duelAbility(Pokemon p) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        return duelAbility(p.getPokemonId(), pm1, pm2, p.getIndividualAttack(), p.getIndividualDefense(), p.getIndividualStamina());
    }

    /**
     * Duel Ability is Tankiness * Gym Offense. A reasonable measure if you don't often/ever dodge,
     * as then you can only attack for as long as you  can stay positive on HP.
     *
     * @param pokemonId The id of the pokemon
     * @param pm1       The first move of the pokemon
     * @param pm2       The second move of the pokemon
     * @param attackIV  The attackIV of the pokemon
     * @param defenseIV The defenseIV of the pokemon
     * @param staminaIV The staminaIV of the pokemon
     * @return Rating of a Pokemon's overall attacking power considering damage, health & defense
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long duelAbility(PokemonId pokemonId, PokemonMoveMeta pm1, PokemonMoveMeta pm2, int attackIV, int defenseIV, int staminaIV) {
        double duelAbility = PokemonUtils.gymOffense(pokemonId, pm1, pm2, attackIV) * PokemonUtils.tankiness(pokemonId, defenseIV, staminaIV);
        return Math.round(duelAbility);
    }

    /**
     * Gym Offense takes the better of No Weave/Weave Damage over 100s and multiplies by the
     * Pokemon's base attack to arrive at a ranking of raw damage output.
     *
     * @param p A Pokemon object
     * @return Rating of a Pokemon's pure offensive ability over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static double gymOffense(Pokemon p) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        return gymOffense(p.getPokemonId(), pm1, pm2, p.getIndividualAttack());
    }

    /**
     * Gym Offense takes the better of No Weave/Weave Damage over 100s and multiplies by the
     * Pokemon's base attack to arrive at a ranking of raw damage output.
     *
     * @param pokemonId The id of the pokemon
     * @param pm1       The first move of the pokemon
     * @param pm2       The second move of the pokemon
     * @param attackIV  The attackIV of the pokemon
     * @return Rating of a Pokemon's pure offensive ability over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static double gymOffense(final PokemonId pokemonId, PokemonMoveMeta pm1, PokemonMoveMeta pm2, final int attackIV) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        return Math.max(PokemonUtils.dpsForMove(pokemonId, pm1, true) * 100, PokemonUtils.weaveDps(pokemonId, pm1, pm2, 0)) * (meta.getBaseAttack() + attackIV);
    }

    /**
     * Gym Defense takes the calculated Gym Weave Damage over 100s and multiplies by Tankiness
     * to arrive at a ranking of how much damage a Pokemon will output when defending a gym.
     *
     * @param p A Pokemon object
     * @return Rating of a Pokemon's AI controlled gym defense over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long gymDefense(Pokemon p) {
        PokemonMoveMeta pm1 = PokemonMoveMetaRegistry.getMeta(p.getMove1());
        PokemonMoveMeta pm2 = PokemonMoveMetaRegistry.getMeta(p.getMove2());
        return gymDefense(p.getPokemonId(), pm1, pm2, p.getIndividualAttack(), p.getIndividualDefense(), p.getIndividualStamina());
    }

    /**
     * Gym Defense takes the calculated Gym Weave Damage over 100s and multiplies by Tankiness
     * to arrive at a ranking of how much damage a Pokemon will output when defending a gym.
     *
     * @param pokemonId The id of the pokemon
     * @param pm1       The first move of the pokemon
     * @param pm2       The second move of the pokemon
     * @param attackIV  The attackIV of the pokemon
     * @param defenseIV The defenseIV of the pokemon
     * @param staminaIV The staminaIV of the pokemon
     * @return Rating of a Pokemon's AI controlled gym defense over time considering move set
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long gymDefense(PokemonId pokemonId, PokemonMoveMeta pm1, PokemonMoveMeta pm2, int attackIV, int defenseIV, int staminaIV) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
        double gymDefense = PokemonUtils.weaveDps(pokemonId, pm1, pm2, 2000) * (meta.getBaseAttack() + attackIV) * PokemonUtils.tankiness(pokemonId, defenseIV, staminaIV);
        return Math.round(gymDefense);
    }

    /**
     * Tankiness is basically Base HP * Base Def. An approximation of a Pokemon's relative ability
     * to soak damage compared to other species.
     * <p>
     * Used for duel ability & gym defense calculations
     *
     * @param p A Pokemon object
     * @return Rating of a Pokemon's tankiness :)
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long tankiness(Pokemon p) {
        return tankiness(p.getPokemonId(), p.getIndividualDefense(), p.getIndividualStamina());
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
     * @return Rating of a Pokemon's tankiness :)
     * @link https://www.reddit.com/r/TheSilphRoad/comments/4vcobt/posthotfix_pokemon_go_full_moveset_rankings/
     * @link i607ch00
     */
    public static long tankiness(PokemonId pokemonId, int defenseIV, int staminaIV) {
        PokemonMeta meta = PokemonMetaRegistry.getMeta(pokemonId);
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
        double moveOneStab = (meta.getType1().equals(pm1.getType()) || meta.getType2().equals(pm1.getType())) ? STAB_MULTIPLIER : NORMAL_MULTIPLIER;
        double moveTwoStab = (meta.getType1().equals(pm2.getType()) || meta.getType2().equals(pm2.getType())) ? STAB_MULTIPLIER : NORMAL_MULTIPLIER;

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
        final double weaveEnergyUsageRatio;

        if (Math.abs(pm2.getEnergy()) == 100) {
            weaveEnergyUsageRatio = Math.ceil((double) Math.abs(pm2.getEnergy()) / (double) pm1.getEnergy());
        } else {
            weaveEnergyUsageRatio = (double) Math.abs(pm2.getEnergy()) / (double) pm1.getEnergy();
        }

        //=IF(AB2=100,CEILING(AB2/U2),AB2/U2)*T2+(AA2+$AL$1)
        //=IF(AB2=100,CEILING(AB2/U2),AB2/U2)*(T2+2000)+(AA2+$AL$1)
        final double weaveCycleLength = weaveEnergyUsageRatio
            * (pm1.getTime() + additionalDelay)
            + pm2.getTime() + PokemonUtils.MOVE2_CHARGE_DELAY_MS;

        //=FLOOR(100000/AD2)
        //*(X2*(1+Y2*0.25) * (1+($AJ$1*Z2/100)))
        //+CEILING(FLOOR(100000/AD2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))
        //*(R2*(1+(S2*0.25)))
        //+FLOOR((100000-(FLOOR(100000/AD2)*(AA2+$AL$1)+CEILING(FLOOR(100000/AD2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*T2))/T2)
        //*(R2*(1+(S2*0.25)))
        //=FLOOR(100000/AF2)*(X2*(1+Y2*0.25)*(1+($AJ$1*Z2/100)))+CEILING(FLOOR(100000/AF2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*(R2*(1+(S2*0.25)))+FLOOR((100000-(FLOOR(100000/AF2)*(AA2+$AL$1)+CEILING(FLOOR(100000/AF2)*IF(AB2=100,CEILING(AB2/U2),AB2/U2))*(T2+2000)))/(T2+2000))*(R2*(1+(S2*0.25)))
        final double weaveDPS = Math.floor(100000 / weaveCycleLength)
            * (pm2.getPower() * moveTwoStab * (1 + (PokemonUtils.CRIT_DAMAGE_BONUS * pm2.getCritChance())))
            + Math.ceil(Math.floor(100000 / weaveCycleLength) * weaveEnergyUsageRatio)
            * (pm1.getPower() * moveOneStab)
            + Math.floor((100000 - (Math.floor(100000 / weaveCycleLength) * (pm2.getTime() + PokemonUtils.MOVE2_CHARGE_DELAY_MS) + Math.ceil(Math.floor(100000 / weaveCycleLength) * weaveEnergyUsageRatio) * (pm1.getTime() + additionalDelay))) / (pm1.getTime() + additionalDelay))
            * (pm1.getPower() * moveOneStab);

        return weaveDPS;
    }

    /**
     * Get cp comparator for max cp.
     *
     * @return cp comparator for max cp
     */
    public static Comparator<PokemonMeta> getMaxCpComperator() {
        final Comparator<PokemonMeta> cMeta = (m1, m2) -> {
            final int comb1 = PokemonCpUtils.getMaxCp(m1.getBaseAttack(), m1.getBaseDefense(),
                m1.getBaseStamina());
            final int comb2 = PokemonCpUtils.getMaxCp(m2.getBaseAttack(), m2.getBaseDefense(),
                m2.getBaseStamina());
            return comb1 - comb2;
        };
        return cMeta;
    }

    /**
     * Get cp comparator based on stats and current level of eevee.
     *
     * @param p eevee pokemon
     * @return cp comparator based on stats and current level of eevee
     */
    public static Comparator<PokemonMeta> getEeveeCpComperator(final Pokemon p) {
        final int ivAttack = p.getIndividualAttack();
        final int ivDefense = p.getIndividualDefense();
        final int ivStamina = p.getIndividualStamina();
        final float level = p.getLevel();

        final Comparator<PokemonMeta> cMeta = (m1, m2) -> {
            final int comb1 = PokemonCpUtils.getCpForPokemonLevel(
                m1.getBaseAttack() + ivAttack,
                m1.getBaseDefense() + ivDefense,
                m1.getBaseStamina() + ivStamina,
                level);
            final int comb2 = PokemonCpUtils.getCpForPokemonLevel(
                m2.getBaseAttack() + ivAttack,
                m2.getBaseDefense() + ivDefense,
                m2.getBaseStamina() + ivStamina,
                level);
            return comb1 - comb2;
        };
        return cMeta;
    }

    /**
     * Get the evolutions of eevee.
     *
     * @return evolution of eevee
     */
    public static List<PokemonMeta> getEeveeEvolutions() {
        final PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonId.VAPOREON);
        final PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonId.FLAREON);
        final PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonId.JOLTEON);

        if (vap == null || fla == null || jol == null) {
            return null;
        }
        return Arrays.asList(vap, fla, jol);
    }
}
