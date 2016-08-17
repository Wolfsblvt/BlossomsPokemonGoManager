package me.corriekay.pokegoutil.DATA.models;

import POGOProtos.Enums.PokemonFamilyIdOuterClass;
import POGOProtos.Enums.PokemonIdOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class PokemonTableModel extends AbstractTableModel {

    PokemonModel pt;

    private final ArrayList<Pokemon> pokeCol = new ArrayList<>();
    private final ArrayList<Integer> numIdCol = new ArrayList<>();//0
    private final ArrayList<String> nickCol = new ArrayList<>(),//1
            speciesCol = new ArrayList<>(),//2
            ivCol = new ArrayList<>();//3
    private final ArrayList<Double> levelCol = new ArrayList<>();//4
    private final ArrayList<Integer> atkCol = new ArrayList<>(),//5
            defCol = new ArrayList<>(),//6
            stamCol = new ArrayList<>();//7
    private final ArrayList<String> type1Col = new ArrayList<>(),//8
            type2Col = new ArrayList<>(),//9
            move1Col = new ArrayList<>(),//10
            move2Col = new ArrayList<>();//11
    private final ArrayList<Integer> cpCol = new ArrayList<>(),//12
            hpCol = new ArrayList<>(),//13
            maxCpCurrentCol = new ArrayList<>(),//14
            maxCpCol = new ArrayList<>(),//15
            maxEvolvedCpCurrentCol = new ArrayList<>(),//16
            maxEvolvedCpCol = new ArrayList<>(),//17
            candiesCol = new ArrayList<>();//18
    private final ArrayList<String> candies2EvlvCol = new ArrayList<>();//19
    private final ArrayList<Integer> dustToLevelCol = new ArrayList<>();//20
    private final ArrayList<String> pokeballCol = new ArrayList<>(),//21
            caughtCol = new ArrayList<>(),//22
            favCol = new ArrayList<>();//23
    private final ArrayList<Long> duelAbilityCol = new ArrayList<>();//24
    private final ArrayList<Long> gymOffenseCol = new ArrayList<>();//25
    private final ArrayList<Long> gymDefenseCol = new ArrayList<>();//26
    private final ArrayList<String> move1RatingCol = new ArrayList<>(),//27
            move2RatingCol = new ArrayList<>();//28
    private final ArrayList<String> cpEvolvedCol = new ArrayList<>();//29

    @Deprecated
    private PokemonTableModel(PokemonGo go, List<Pokemon> pokes, PokemonModel pt) {
        this.pt = pt;
        MutableInt i = new MutableInt();
        pokes.forEach(p -> {
            pokeCol.add(i.getValue(), p);
            numIdCol.add(i.getValue(), p.getMeta().getNumber());
            nickCol.add(i.getValue(), p.getNickname());
            speciesCol.add(i.getValue(),
                    PokeHandler.getLocalPokeName(p));
            levelCol.add(i.getValue(), (double) p.getLevel());
            ivCol.add(i.getValue(), Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));
            cpCol.add(i.getValue(), p.getCp());
            atkCol.add(i.getValue(), p.getIndividualAttack());
            defCol.add(i.getValue(), p.getIndividualDefense());
            stamCol.add(i.getValue(), p.getIndividualStamina());
            type1Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType1().toString().toLowerCase()));
            type2Col.add(i.getValue(), StringUtils.capitalize(p.getMeta().getType2().toString().toLowerCase().replaceAll("none", "")));

            Double dps1 = PokemonUtils.dpsForMove(p, true);
            Double dps2 = PokemonUtils.dpsForMove(p, false);

            move1Col.add(i.getValue(), WordUtils.capitalize(p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " ")) + " (" + String.format("%.2f", dps1) + "dps)");
            move2Col.add(i.getValue(), WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " (" + String.format("%.2f", dps2) + "dps)");
            hpCol.add(i.getValue(), p.getMaxStamina());

            int trainerLevel = 1;
            try {
                trainerLevel = go.getPlayerProfile().getStats().getLevel();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // Max CP calculation for current PokemonModel
            PokemonMeta pokemonMeta = p.getMeta();
            int maxCpCurrent = 0, maxCp = 0;
            if (pokemonMeta == null) {
                System.out.println("Error: Cannot find meta data for " + p.getPokemonId().name());
            } else {
                int attack = p.getIndividualAttack() + pokemonMeta.getBaseAttack();
                int defense = p.getIndividualDefense() + pokemonMeta.getBaseDefense();
                int stamina = p.getIndividualStamina() + pokemonMeta.getBaseStamina();
                maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
                maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
                maxCpCurrentCol.add(i.getValue(), maxCpCurrent);
                maxCpCol.add(i.getValue(), maxCp);
            }

            // Max CP calculation for highest evolution of current PokemonModel
            PokemonFamilyIdOuterClass.PokemonFamilyId familyId = p.getPokemonFamily();
            PokemonIdOuterClass.PokemonId highestFamilyId = PokemonMetaRegistry.getHightestForFamily(familyId);

            // Eeveelutions exception handling
            if (familyId.getNumber() == PokemonFamilyIdOuterClass.PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
                if (p.getPokemonId().getNumber() == PokemonIdOuterClass.PokemonId.EEVEE.getNumber()) {
                    PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.VAPOREON);
                    PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.FLAREON);
                    PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.JOLTEON);
                    if (vap != null && fla != null && jol != null) {
                        Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                            int comb1 = PokemonCpUtils.getMaxCp(m1.getBaseAttack(), m1.getBaseDefense(), m1.getBaseStamina());
                            int comb2 = PokemonCpUtils.getMaxCp(m2.getBaseAttack(), m2.getBaseDefense(), m2.getBaseStamina());
                            return comb1 - comb2;
                        };
                        highestFamilyId = PokemonIdOuterClass.PokemonId.forNumber(Collections.max(Arrays.asList(vap, fla, jol), cMeta).getNumber());
                    }
                } else {
                    // This is one of the eeveelutions, so PokemonMetaRegistry.getHightestForFamily() returns Eevee.
                    // We correct that here
                    highestFamilyId = p.getPokemonId();
                }
            }

            PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
            if (highestFamilyId == p.getPokemonId()) {
                maxEvolvedCpCurrentCol.add(i.getValue(), maxCpCurrent);
                maxEvolvedCpCol.add(i.getValue(), maxCp);
                cpEvolvedCol.add(i.getValue(), "-");
            } else if (highestFamilyMeta == null) {
                System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
            } else {
                int attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
                int defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
                int stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();
                maxEvolvedCpCurrentCol.add(i.getValue(), PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
                maxEvolvedCpCol.add(i.getValue(), PokemonCpUtils.getMaxCp(attack, defense, stamina));
                cpEvolvedCol.add(i.getValue(), String.valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, p.getLevel())));
            }

            try {
                candiesCol.add(i.getValue(), p.getCandy());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (p.getCandiesToEvolve() != 0)
                candies2EvlvCol.add(i.getValue(), String.valueOf(p.getCandiesToEvolve()));
            else
                candies2EvlvCol.add(i.getValue(), "-");
            dustToLevelCol.add(i.getValue(), p.getStardustCostsForPowerup());
            pokeballCol.add(i.getValue(), WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
            caughtCol.add(i.getValue(), DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs())));
            favCol.add(i.getValue(), (p.isFavorite()) ? "True" : "");
            duelAbilityCol.add(i.getValue(), PokemonUtils.duelAbility(p));
            gymOffenseCol.add(i.getValue(), PokemonUtils.gymOffense(p));
            gymDefenseCol.add(i.getValue(), PokemonUtils.gymDefense(p));
            move1RatingCol.add(i.getValue(), PokemonUtils.moveRating(p, true));
            move2RatingCol.add(i.getValue(), PokemonUtils.moveRating(p, false));
            i.increment();
        });
    }

    private Pokemon getPokemonByIndex(int i) {
        try {
            return pokeCol.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return "Nickname";
            case 2:
                return "Species";
            case 3:
                return "IV %";
            case 4:
                return "Lvl";
            case 5:
                return "Atk";
            case 6:
                return "Def";
            case 7:
                return "Stam";
            case 8:
                return "Type 1";
            case 9:
                return "Type 2";
            case 10:
                return "Move 1";
            case 11:
                return "Move 2";
            case 12:
                return "CP";
            case 13:
                return "HP";
            case 14:
                return "Max CP (Cur)";
            case 15:
                return "Max CP (40)";
            case 16:
                return "Max Evolved CP (Cur)";
            case 17:
                return "Max Evolved CP (40)";
            case 18:
                return "Candies";
            case 19:
                return "To Evolve";
            case 20:
                return "Stardust";
            case 21:
                return "Caught With";
            case 22:
                return "Time Caught";
            case 23:
                return "Favorite";
            case 24:
                return "Duel Ability";
            case 25:
                return "Gym Offense";
            case 26:
                return "Gym Defense";
            case 27:
                return "Move 1 Rating";
            case 28:
                return "Move 2 Rating";
            case 29:
                return "CP Evolved";
            default:
                return "UNKNOWN?";
        }
    }

    @Override
    public int getColumnCount() {
        return 30;
    }

    @Override
    public int getRowCount() {
        return pokeCol.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return numIdCol.get(rowIndex);
            case 1:
                return nickCol.get(rowIndex);
            case 2:
                return speciesCol.get(rowIndex);
            case 3:
                return ivCol.get(rowIndex);
            case 4:
                return levelCol.get(rowIndex);
            case 5:
                return atkCol.get(rowIndex);
            case 6:
                return defCol.get(rowIndex);
            case 7:
                return stamCol.get(rowIndex);
            case 8:
                return type1Col.get(rowIndex);
            case 9:
                return type2Col.get(rowIndex);
            case 10:
                return move1Col.get(rowIndex);
            case 11:
                return move2Col.get(rowIndex);
            case 12:
                return cpCol.get(rowIndex);
            case 13:
                return hpCol.get(rowIndex);
            case 14:
                return maxCpCurrentCol.get(rowIndex);
            case 15:
                return maxCpCol.get(rowIndex);
            case 16:
                return maxEvolvedCpCurrentCol.get(rowIndex);
            case 17:
                return maxEvolvedCpCol.get(rowIndex);
            case 18:
                return candiesCol.get(rowIndex);
            case 19:
                return candies2EvlvCol.get(rowIndex);
            case 20:
                return dustToLevelCol.get(rowIndex);
            case 21:
                return pokeballCol.get(rowIndex);
            case 22:
                return caughtCol.get(rowIndex);
            case 23:
                return favCol.get(rowIndex);
            case 24:
                return duelAbilityCol.get(rowIndex);
            case 25:
                return gymOffenseCol.get(rowIndex);
            case 26:
                return gymDefenseCol.get(rowIndex);
            case 27:
                return move1RatingCol.get(rowIndex);
            case 28:
                return move2RatingCol.get(rowIndex);
            case 29:
                return cpEvolvedCol.get(rowIndex);
            default:
                return null;
        }
    }
}

