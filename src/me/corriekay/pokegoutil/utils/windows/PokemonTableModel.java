package me.corriekay.pokegoutil.utils.windows;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

@SuppressWarnings({ "serial", "rawtypes" })

public class PokemonTableModel extends AbstractTableModel {

    PokemonTable pt;

    private ArrayList<Pokemon> pokeCol = new ArrayList<>();
    private PokemonGo go;

    private ArrayList<AbstractMap.SimpleEntry<String, ArrayList>> data;

    @Deprecated
    PokemonTableModel(PokemonGo go, List<Pokemon> pokes, PokemonTable pt) {
        this.pt = pt;
        this.go = go;

        data = new ArrayList<AbstractMap.SimpleEntry<String, ArrayList>>();

        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Id", new ArrayList<Integer>())); // 0
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Nickname", new ArrayList<String>()));// 1
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Species", new ArrayList<String>()));// 2
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("IV %", new ArrayList<String>()));// 3
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Lvl", new ArrayList<Double>()));// 4
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Atk", new ArrayList<Integer>()));// 5
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Def", new ArrayList<Integer>()));// 6
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Stam", new ArrayList<Integer>()));// 7
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Type 1", new ArrayList<String>()));// 8
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Type 2", new ArrayList<String>()));// 9
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Move 1", new ArrayList<String>()));// 10
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Move 2", new ArrayList<String>()));// 11
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("CP", new ArrayList<Integer>()));// 12
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("HP", new ArrayList<Integer>()));// 13
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Max CP (Cur)", new ArrayList<Integer>()));// 14
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Max CP (40)", new ArrayList<Integer>()));// 15
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Max Evolved CP (Cur)", new ArrayList<Integer>()));// 16
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Max Evolved CP (40)", new ArrayList<Integer>()));// 17
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Candies", new ArrayList<Integer>()));// 18
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("To Evolve", new ArrayList<String>()));// 19
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Stardust", new ArrayList<Integer>()));// 20
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Caught With", new ArrayList<String>()));// 21
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Time Caught", new ArrayList<String>()));// 22
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Favorite", new ArrayList<String>()));// 23
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Duel Ability", new ArrayList<Long>()));// 24
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Gym Offense", new ArrayList<Long>()));// 25
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Gym Defense", new ArrayList<Long>()));// 26
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Move 1 Rating", new ArrayList<String>()));// 27
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Move 2 Rating", new ArrayList<String>()));// 28
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("CP Evolved", new ArrayList<String>()));// 29
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Evolvable", new ArrayList<String>()));// 30
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Duel Ability IV", new ArrayList<Long>()));
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Gym Offense IV", new ArrayList<Double>()));
        data.add(new AbstractMap.SimpleEntry<String, ArrayList>("Gym Defense IV", new ArrayList<Long>()));
      
        ChangeTableData(pokes);
    }

    private ArrayList getColumnList(int columnIndex) {
        return data.get(columnIndex).getValue();
    }

    @SuppressWarnings("unchecked")
    public void ChangeTableData(List<Pokemon> pokes) {
        ClearTable();

        MutableInt i = new MutableInt();

        pokes.forEach(p -> {
            pokeCol.add(i.getValue(), p);
            getColumnList(0).add(i.getValue(), p.getMeta().getNumber());
            getColumnList(1).add(i.getValue(), p.getNickname());
            getColumnList(2).add(i.getValue(), PokeHandler.getLocalPokeName(p));
            getColumnList(3).add(i.getValue(), Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));
            getColumnList(4).add(i.getValue(), p.getLevel());
            getColumnList(12).add(i.getValue(), p.getCp());
            getColumnList(5).add(i.getValue(), p.getIndividualAttack());
            getColumnList(6).add(i.getValue(), p.getIndividualDefense());
            getColumnList(7).add(i.getValue(), p.getIndividualStamina());
            getColumnList(8).add(i.getValue(), StringUtils.capitalize(p.getMeta().getType1().toString().toLowerCase()));
            getColumnList(9).add(i.getValue(), StringUtils.capitalize(
                    p.getMeta().getType2().toString().toLowerCase().replaceAll("none", "")));

            Double dps1 = PokemonUtils.dpsForMove(p, true);
            Double dps2 = PokemonUtils.dpsForMove(p, false);

            getColumnList(10).add(i.getValue(), WordUtils.capitalize(
                    p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " "))
                    + " (" + String.format("%.2f", dps1) + "dps)");
            getColumnList(11)
                    .add(i.getValue(),
                            WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " ("
                                    + String.format("%.2f", dps2) + "dps)");
            getColumnList(13).add(i.getValue(), p.getMaxStamina());

            int trainerLevel = 1;
            try {
                trainerLevel = go.getPlayerProfile().getStats().getLevel();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // Max CP calculation for current Pokemon
            PokemonMeta pokemonMeta = PokemonMetaRegistry.getMeta(p.getPokemonId());
            int maxCpCurrent = 0, maxCp = 0;
            if (pokemonMeta == null) {
                System.out.println("Error: Cannot find meta data for " + p.getPokemonId().name());
            } else {
                int attack = p.getIndividualAttack() + pokemonMeta.getBaseAttack();
                int defense = p.getIndividualDefense() + pokemonMeta.getBaseDefense();
                int stamina = p.getIndividualStamina() + pokemonMeta.getBaseStamina();
                maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
                maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
                getColumnList(14).add(i.getValue(), maxCpCurrent);
                getColumnList(15).add(i.getValue(), maxCp);
            }

            // Max CP calculation for highest evolution of current Pokemon
            PokemonFamilyId familyId = p.getPokemonFamily();
            PokemonId highestFamilyId = PokemonMetaRegistry.getHightestForFamily(familyId);

            // Eeveelutions exception handling
            if (familyId.getNumber() == PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
                if (p.getPokemonId().getNumber() == PokemonId.EEVEE.getNumber()) {
                    PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonId.VAPOREON);
                    PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonId.FLAREON);
                    PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonId.JOLTEON);
                    if (vap != null && fla != null && jol != null) {
                        Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                            int comb1 = PokemonCpUtils.getMaxCp(m1.getBaseAttack(), m1.getBaseDefense(),
                                    m1.getBaseStamina());
                            int comb2 = PokemonCpUtils.getMaxCp(m2.getBaseAttack(), m2.getBaseDefense(),
                                    m2.getBaseStamina());
                            return comb1 - comb2;
                        };
                        highestFamilyId = PokemonId
                                .forNumber(Collections.max(Arrays.asList(vap, fla, jol), cMeta).getNumber());
                    }
                } else {
                    // This is one of the eeveelutions, so
                    // PokemonMetaRegistry.getHightestForFamily() returns
                    // Eevee.
                    // We correct that here
                    highestFamilyId = p.getPokemonId();
                }
            }

            PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
            if (highestFamilyId == p.getPokemonId()) {
                getColumnList(16).add(i.getValue(), maxCpCurrent);
                getColumnList(17).add(i.getValue(), maxCp);
                getColumnList(29).add(i.getValue(), "-");
            } else if (highestFamilyMeta == null) {
                System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
            } else {
                int attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
                int defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
                int stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();

                getColumnList(16).add(i.getValue(),
                        PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
                getColumnList(17).add(i.getValue(), PokemonCpUtils.getMaxCp(attack, defense, stamina));
                getColumnList(29).add(i.getValue(), String
                        .valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, p.getLevel())));
            }

            int candies = 0;
            try {
                candies = p.getCandy();
                getColumnList(18).add(i.getValue(), candies);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (p.getCandiesToEvolve() != 0) {
                getColumnList(19).add(i.getValue(), String.valueOf(p.getCandiesToEvolve()));
                getColumnList(30).add(i.getValue(), String.valueOf(GetEvolvable(candies, p.getCandiesToEvolve())));
            } else {
                getColumnList(19).add(i.getValue(), "-");
                getColumnList(30).add(i.getValue(), "-");
            }
            getColumnList(20).add(i.getValue(), p.getStardustCostsForPowerup());
            getColumnList(21).add(i.getValue(), WordUtils.capitalize(
                    p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
            getColumnList(22).add(i.getValue(), DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs())));
            getColumnList(23).add(i.getValue(), (p.isFavorite()) ? "True" : "");
            getColumnList(24).add(i.getValue(),PokemonUtils.duelAbility(p, false));
            getColumnList(25).add(i.getValue(),PokemonUtils.gymOffense(p, false));
            getColumnList(26).add(i.getValue(),PokemonUtils.gymDefense(p, false));
            getColumnList(27).add(i.getValue(),PokemonUtils.moveRating(p, true));
            getColumnList(28).add(i.getValue(),PokemonUtils.moveRating(p, false));
            getColumnList(31).add(i.getValue(),PokemonUtils.duelAbility(p, true));
            getColumnList(32).add(i.getValue(),PokemonUtils.gymOffense(p, true));
            getColumnList(33).add(i.getValue(),PokemonUtils.gymDefense(p, false));
            
            i.increment();
        });

        fireTableDataChanged();
    }

    private void ClearTable() {
        pokeCol.clear();
        for (SimpleEntry<String, ArrayList> entry : data) {
            entry.getValue().clear();
        }
    }

    // Rounded down candies / toEvolve
    private int GetEvolvable(int candies, int candiesToEvolve) {
        return (int) ((double) candies / candiesToEvolve);
    }

    public Pokemon getPokemonByIndex(int i) {
        try {
            return pokeCol.get(pt.convertRowIndexToModel(i));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return data.get(columnIndex).getKey();
    }

    @Override
    public int getColumnCount() {
        // return 31;
        return data.size();
    }

    @Override
    public int getRowCount() {
        return pokeCol.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(columnIndex).getValue().get(rowIndex);
    }
}