package me.corriekay.pokegoutil.utils.windows;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;

import POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

@SuppressWarnings({"serial", "rawtypes"})

public class PokemonTableModel extends AbstractTableModel {

    PokemonTable pt;

    private final ArrayList<Pokemon> pokeCol = new ArrayList<>();
    private final PokemonGo go;

    private final ArrayList<AbstractMap.SimpleEntry<String, ArrayList>> data;

    @Deprecated
    PokemonTableModel(final PokemonGo go, final List<Pokemon> pokes, final PokemonTable pt) {
        this.pt = pt;
        this.go = go;

        data = new ArrayList<>();

        data.add(new AbstractMap.SimpleEntry<>("Id", new ArrayList<Integer>())); // 0
        data.add(new AbstractMap.SimpleEntry<>("Nickname", new ArrayList<String>()));// 1
        data.add(new AbstractMap.SimpleEntry<>("Species", new ArrayList<String>()));// 2
        data.add(new AbstractMap.SimpleEntry<>("IV %", new ArrayList<String>()));// 3
        data.add(new AbstractMap.SimpleEntry<>("Lvl", new ArrayList<Double>()));// 4
        data.add(new AbstractMap.SimpleEntry<>("Atk", new ArrayList<Integer>()));// 5
        data.add(new AbstractMap.SimpleEntry<>("Def", new ArrayList<Integer>()));// 6
        data.add(new AbstractMap.SimpleEntry<>("Stam", new ArrayList<Integer>()));// 7
        data.add(new AbstractMap.SimpleEntry<>("Type 1", new ArrayList<String>()));// 8
        data.add(new AbstractMap.SimpleEntry<>("Type 2", new ArrayList<String>()));// 9
        data.add(new AbstractMap.SimpleEntry<>("Move 1", new ArrayList<String>()));// 10
        data.add(new AbstractMap.SimpleEntry<>("Move 2", new ArrayList<String>()));// 11
        data.add(new AbstractMap.SimpleEntry<>("CP", new ArrayList<Integer>()));// 12
        data.add(new AbstractMap.SimpleEntry<>("HP", new ArrayList<Integer>()));// 13
        data.add(new AbstractMap.SimpleEntry<>("Max CP (Cur)", new ArrayList<Integer>()));// 14
        data.add(new AbstractMap.SimpleEntry<>("Max CP (40)", new ArrayList<Integer>()));// 15
        data.add(new AbstractMap.SimpleEntry<>("Max Evolved CP (Cur)", new ArrayList<Integer>()));// 16
        data.add(new AbstractMap.SimpleEntry<>("Max Evolved CP (40)", new ArrayList<Integer>()));// 17
        data.add(new AbstractMap.SimpleEntry<>("Candies", new ArrayList<Integer>()));// 18
        data.add(new AbstractMap.SimpleEntry<>("To Evolve", new ArrayList<String>()));// 19
        data.add(new AbstractMap.SimpleEntry<>("Stardust", new ArrayList<Integer>()));// 20
        data.add(new AbstractMap.SimpleEntry<>("Caught With", new ArrayList<String>()));// 21
        data.add(new AbstractMap.SimpleEntry<>("Time Caught", new ArrayList<String>()));// 22
        data.add(new AbstractMap.SimpleEntry<>("Favorite", new ArrayList<String>()));// 23
        data.add(new AbstractMap.SimpleEntry<>("Duel Ability", new ArrayList<Long>()));// 24
        data.add(new AbstractMap.SimpleEntry<>("Gym Offense", new ArrayList<Double>()));// 25
        data.add(new AbstractMap.SimpleEntry<>("Gym Defense", new ArrayList<Long>()));// 26
        data.add(new AbstractMap.SimpleEntry<>("Move 1 Rating", new ArrayList<String>()));// 27
        data.add(new AbstractMap.SimpleEntry<>("Move 2 Rating", new ArrayList<String>()));// 28
        data.add(new AbstractMap.SimpleEntry<>("CP Evolved", new ArrayList<String>()));// 29
        data.add(new AbstractMap.SimpleEntry<>("Evolvable", new ArrayList<String>()));// 30
        data.add(new AbstractMap.SimpleEntry<>("Duel Ability IV", new ArrayList<Long>()));
        data.add(new AbstractMap.SimpleEntry<>("Gym Offense IV", new ArrayList<Double>()));
        data.add(new AbstractMap.SimpleEntry<>("Gym Defense IV", new ArrayList<Long>()));

        ChangeTableData(pokes);
    }

    private ArrayList getColumnList(final int columnIndex) {
        return data.get(columnIndex).getValue();
    }

    @SuppressWarnings("unchecked")
    public void ChangeTableData(final List<Pokemon> pokes) {
        ClearTable();

        final MutableInt i = new MutableInt();

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

            final Double dps1 = PokemonUtils.dpsForMove(p, true);
            final Double dps2 = PokemonUtils.dpsForMove(p, false);

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
            } catch (final Exception e1) {
                e1.printStackTrace();
            }

            // Max CP calculation for current Pokemon
            final PokemonMeta pokemonMeta = PokemonMetaRegistry.getMeta(p.getPokemonId());
            int maxCpCurrent = 0, maxCp = 0;
            if (pokemonMeta == null) {
                System.out.println("Error: Cannot find meta data for " + p.getPokemonId().name());
            } else {
                final int attack = p.getIndividualAttack() + pokemonMeta.getBaseAttack();
                final int defense = p.getIndividualDefense() + pokemonMeta.getBaseDefense();
                final int stamina = p.getIndividualStamina() + pokemonMeta.getBaseStamina();
                maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
                maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
                getColumnList(14).add(i.getValue(), maxCpCurrent);
                getColumnList(15).add(i.getValue(), maxCp);
            }

            // Max CP calculation for highest evolution of current Pokemon
            final PokemonFamilyId familyId = p.getPokemonFamily();
            PokemonId highestFamilyId = PokemonMetaRegistry.getHighestForFamily(familyId);

            // Eeveelutions exception handling
            if (familyId.getNumber() == PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
                if (p.getPokemonId().getNumber() == PokemonId.EEVEE.getNumber()) {
                    final PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonId.VAPOREON);
                    final PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonId.FLAREON);
                    final PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonId.JOLTEON);
                    if (vap != null && fla != null && jol != null) {
                        final Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                            final int comb1 = PokemonCpUtils.getMaxCp(m1.getBaseAttack(), m1.getBaseDefense(),
                                    m1.getBaseStamina());
                            final int comb2 = PokemonCpUtils.getMaxCp(m2.getBaseAttack(), m2.getBaseDefense(),
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

            final PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
            if (highestFamilyId == p.getPokemonId()) {
                getColumnList(16).add(i.getValue(), maxCpCurrent);
                getColumnList(17).add(i.getValue(), maxCp);
                getColumnList(29).add(i.getValue(), "-");
            } else if (highestFamilyMeta == null) {
                System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
            } else {
                final int attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
                final int defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
                final int stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();

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

            } catch (final Exception e) {
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
            getColumnList(24).add(i.getValue(), PokemonUtils.duelAbility(p, false));
            getColumnList(25).add(i.getValue(), PokemonUtils.gymOffense(p, false));
            getColumnList(26).add(i.getValue(), PokemonUtils.gymDefense(p, false));
            getColumnList(27).add(i.getValue(), PokemonUtils.moveRating(p, true));
            getColumnList(28).add(i.getValue(), PokemonUtils.moveRating(p, false));
            getColumnList(31).add(i.getValue(), PokemonUtils.duelAbility(p, true));
            getColumnList(32).add(i.getValue(), PokemonUtils.gymOffense(p, true));
            getColumnList(33).add(i.getValue(), PokemonUtils.gymDefense(p, true));

            i.increment();
        });

        fireTableDataChanged();
    }

    private void ClearTable() {
        pokeCol.clear();
        for (final SimpleEntry<String, ArrayList> entry : data) {
            entry.getValue().clear();
        }
    }

    // Rounded down candies / toEvolve
    private int GetEvolvable(final int candies, final int candiesToEvolve) {
        int evolvable = (int) ((double) candies / candiesToEvolve);
        int rest = (candies % candiesToEvolve);
        final boolean transferAfterEvolve = ConfigNew.getConfig().getBool(ConfigKey.TRANSFER_AFTER_EVOLVE);

        // We iterate and get how many candies are added while evolving and if that can make up for some more evolves
        int newEvolvable = evolvable;
        do {
            final int candyGiven = newEvolvable + (transferAfterEvolve ? newEvolvable : 0);
            newEvolvable = (int) ((double) (candyGiven + rest) / candiesToEvolve);
            evolvable = evolvable + newEvolvable;
            rest = (candyGiven + rest) % candiesToEvolve;
        } while (newEvolvable > 0);

        return evolvable;
    }

    public Pokemon getPokemonByIndex(final int i) {
        try {
            return pokeCol.get(pt.convertRowIndexToModel(i));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getColumnName(final int columnIndex) {
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
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return data.get(columnIndex).getValue().get(rowIndex);
    }
}
