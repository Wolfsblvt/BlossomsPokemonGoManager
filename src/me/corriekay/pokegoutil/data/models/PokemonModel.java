package me.corriekay.pokegoutil.data.models;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.pokemon.Evolutions;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.NoSuchItemException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass.PokemonSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

public class PokemonModel {
    private static final String UNDERSCORE = "_";
    private PokemonModelData data = new PokemonModelData(new SimpleIntegerProperty(), new SimpleStringProperty(), new SimpleStringProperty(), new SimpleDoubleProperty(), new SimpleStringProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleStringProperty(),
            new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(), new SimpleIntegerProperty(),
            new SimpleStringProperty(), new SimpleStringProperty(), new SimpleBooleanProperty(), new SimpleLongProperty(), new SimpleDoubleProperty(), new SimpleLongProperty(), new SimpleLongProperty(), new SimpleDoubleProperty(), new SimpleLongProperty(), new SimpleStringProperty(), new SimpleStringProperty(), AccountManager.getInstance());

    public PokemonModel(final Pokemon pokemon) {
        this.data.pokemon = pokemon;
        initialize();
    }

    public IntegerProperty atkProperty() {
        return data.atk;
    }

    // Bunch of getters and setters

    public IntegerProperty candies2EvlvProperty() {
        return data.candies2Evlv;
    }

    public IntegerProperty candiesProperty() {
        return data.candies;
    }

    public StringProperty caughtDateProperty() {
        return data.caughtDate;
    }

    public StringProperty cpEvolvedProperty() {
        return data.cpEvolved;
    }

    public IntegerProperty cpProperty() {
        return data.cp;
    }

    public IntegerProperty defProperty() {
        return data.def;
    }

    public LongProperty duelAbilityIvProperty() {
        return data.duelAbilityIv;
    }

    public LongProperty duelAbilityProperty() {
        return data.duelAbility;
    }

    public IntegerProperty dustToLevelProperty() {
        return data.dustToLevel;
    }

    public StringProperty evolvableProperty() {
        return data.evolvable;
    }

    public int getAtk() {
        return data.atk.get();
    }

    public int getCandies() {
        return data.candies.get();
    }

    public int getCandies2Evlv() {
        return data.candies2Evlv.get();
    }

    public int getCandyCostsForPowerup() {
        return data.pokemon.getCandyCostsForPowerup();
    }

    public String getCaughtDate() {
        return data.caughtDate.get();
    }

    public int getCp() {
        return data.cp.get();
    }

    public String getCpEvolved() {
        return data.cpEvolved.get();
    }

    public int getDef() {
        return data.def.get();
    }

    public long getDuelAbility() {
        return data.duelAbility.get();
    }

    public long getDuelAbilityIv() {
        return data.duelAbilityIv.get();
    }

    public int getDustToLevel() {
        return data.dustToLevel.get();
    }

    public String getEvolvable() {
        return data.evolvable.get();
    }

    public long getGymDefense() {
        return data.gymDefense.get();
    }

    public long getGymDefenseIv() {
        return data.gymDefenseIv.get();
    }

    public double getGymOffense() {
        return data.gymOffense.get();
    }

    public double getGymOffenseIv() {
        return data.gymOffenseIv.get();
    }

    public int getHp() {
        return data.hp.get();
    }

    public String getIv() {
        return data.iv.get();
    }

    public double getLevel() {
        return data.level.get();
    }

    public int getMaxCp() {
        return data.maxCp.get();
    }

    public int getMaxCpCurrent() {
        return data.maxCpCurrent.get();
    }

    public int getMaxEvolvedCp() {
        return data.maxEvolvedCp.get();
    }

    public int getMaxEvolvedCpCurrent() {
        return data.maxEvolvedCpCurrent.get();
    }

    public String getMove1() {
        return data.move1.get();
    }

    public String getMove2() {
        return data.move2.get();
    }

    public String getNickname() {
        return data.nickname.get();
    }

    public int getNumId() {
        return data.numId.get();
    }

    public String getPokeball() {
        return data.pokeball.get();
    }

    public Pokemon getPokemon() {
        return data.pokemon;
    }

    public String getSpecies() {
        return data.species.get();
    }

    public int getStam() {
        return data.stam.get();
    }

    public int getStardustCostsForPowerup() {
        return data.pokemon.getStardustCostsForPowerup();
    }

    public String getSummary() {
        return String.format(
            "%s (%s) IV: %s CP: %d",
            getNickname(), getSpecies(),
            getIv(), getCp());
    }

    public String getType1() {
        return data.type1.get();
    }

    public String getType2() {
        return data.type2.get();
    }

    public LongProperty gymDefenseIvProperty() {
        return data.gymDefenseIv;
    }

    public LongProperty gymDefenseProperty() {
        return data.gymDefense;
    }

    public DoubleProperty gymOffenseIvProperty() {
        return data.gymOffenseIv;
    }

    public DoubleProperty gymOffenseProperty() {
        return data.gymOffense;
    }

    public IntegerProperty hpProperty() {
        return data.hp;
    }

    private void initialize() {
        final PokemonSettings settings = data.pokemon.getSettings();

        settingsInitial(settings);

        // Max CP calculation for current PokemonModel
        setMaxCPCurrent();

        // Max CP calculation for highest evolution of current PokemonModel
        setMaxCPHighest();

        int pokemonCandies = data.pokemon.getCandy();

        setCandies(pokemonCandies);
        if (data.pokemon.getCandiesToEvolve() != 0) {
            setCandies2Evlv(data.pokemon.getCandiesToEvolve());
            // Rounded down candies / toEvolve
            setEvolvable(String.valueOf((int) ((double) pokemonCandies / data.pokemon.getCandiesToEvolve())));

        } else {
            setCandies2Evlv(0);
            setEvolvable("-");
        }
        setDustToLevel(data.pokemon.getStardustCostsForPowerup());
        setPokeball(WordUtils.capitalize(
            data.pokemon.getPokeball().toString().toLowerCase()
                .replaceAll("item_", "").replaceAll(UNDERSCORE, " ")));
        setCaughtDate(DateHelper.toString(DateHelper.fromTimestamp(data.pokemon.getCreationTimeMs())));
        setIsFavorite(data.pokemon.isFavorite());
        setDuelAbility(PokemonCalculationUtils.duelAbility(data.pokemon));
        setGymOffense(PokemonCalculationUtils.gymOffense(data.pokemon));
        setGymDefense(PokemonCalculationUtils.gymDefense(data.pokemon));

        setDuelAbilityIv(PokemonCalculationUtils.duelAbility(data.pokemon));
        setGymOffenseIv(PokemonCalculationUtils.gymOffense(data.pokemon));
        setGymDefenseIv(PokemonCalculationUtils.gymDefense(data.pokemon));
    }

    private void setMaxCPHighest() {
        final List<PokemonId> highest = Evolutions.getHighest(data.pokemon.getPokemonId());
        int maxEvolvedCpVar = 0;
        int maxEvolvedCpCurrentVar = 0;
        //If Evolutions, Evolutions.getHighest return all evolutions in list, otherwise return just 1 element with the top evolution
        for (final PokemonId pokemonId : highest) {
            maxEvolvedCpVar = Math.max(maxEvolvedCpVar, data.pokemon.getCpFullEvolveAndPowerup(pokemonId));
            maxEvolvedCpCurrentVar = Math.max(maxEvolvedCpCurrentVar, data.pokemon.getMaxCpFullEvolveAndPowerupForPlayer(pokemonId));
        }

        setMaxEvolvedCp(maxEvolvedCpVar);
        setMaxEvolvedCpCurrent(maxEvolvedCpCurrentVar);
    }

    private void setMaxCPCurrent() {
        int maxCpCurrentVar = 0;
        int maxCpVar = 0;
        try {
            maxCpCurrentVar = data.pokemon.getMaxCpForPlayer();
            maxCpVar = data.pokemon.getMaxCp();
        } catch (NoSuchItemException e) {
            System.out.println(e.getMessage());
        }
        setMaxCp(maxCpVar);
        setMaxCpCurrent(maxCpCurrentVar);
    }

    private void settingsInitial(final PokemonSettings settings) {
        setNumId(settings.getPokemonIdValue());
        setNickname(data.pokemon.getNickname());
        setSpecies(PokemonUtils.getLocalPokeName(data.pokemon));
        setLevel(data.pokemon.getLevel());
        setIv(Utilities.percentageWithTwoCharacters(PokemonCalculationUtils.ivRating(data.pokemon)));
        setAtk(data.pokemon.getIndividualAttack());
        setDef(data.pokemon.getIndividualDefense());
        setStam(data.pokemon.getIndividualStamina());
        setType1(StringUtils.capitalize(settings.getType().toString().toLowerCase()));
        setType2(StringUtils.capitalize(settings.getType2().toString().toLowerCase()));

        final Double dps1 = PokemonCalculationUtils.dpsForMove(data.pokemon, true);
        final Double dps2 = PokemonCalculationUtils.dpsForMove(data.pokemon, false);
        setMove1(String.format("%s (%.2fdps)",
            WordUtils.capitalize(
                data.pokemon.getMove1().toString().toLowerCase()
                    .replaceAll("_fast", "").replaceAll(UNDERSCORE, " ")),
            dps1));
        setMove2(String.format("%s (%.2fdps)",
            WordUtils.capitalize(
                data.pokemon.getMove2().toString().toLowerCase()
                    .replaceAll("_fast", "").replaceAll(UNDERSCORE, " ")),
            dps2));

        setCp(data.pokemon.getCp());
        setHp(data.pokemon.getMaxStamina());
    }

    public BooleanProperty isFavoriteProperty() {
        return data.isFavorite;
    }

    public boolean isInGym() {
        return !data.pokemon.getDeployedFortId().isEmpty();
    }

    public boolean isIsFavorite() {
        return data.isFavorite.get();
    }

    public StringProperty ivProperty() {
        return data.iv;
    }

    public DoubleProperty levelProperty() {
        return data.level;
    }

    public IntegerProperty maxCpCurrentProperty() {
        return data.maxCpCurrent;
    }

    public IntegerProperty maxCpProperty() {
        return data.maxCp;
    }

    public IntegerProperty maxEvolvedCpCurrentProperty() {
        return data.maxEvolvedCpCurrent;
    }

    public IntegerProperty maxEvolvedCpProperty() {
        return data.maxEvolvedCp;
    }

    public StringProperty move1Property() {
        return data.move1;
    }

    public StringProperty move2Property() {
        return data.move2;
    }

    public StringProperty nicknameProperty() {
        return data.nickname;
    }

    public IntegerProperty numIdProperty() {
        return data.numId;
    }

    public StringProperty pokeballProperty() {
        return data.pokeball;
    }

    public void setAtk(final int atk) {
        this.data.atk.set(atk);
    }

    public void setCandies(final int candies) {
        this.data.candies.set(candies);
    }

    public void setCandies2Evlv(final int candies2Evlv) {
        this.data.candies2Evlv.set(candies2Evlv);
    }

    public void setCaughtDate(final String caughtDate) {
        this.data.caughtDate.set(caughtDate);
    }

    public void setCp(final int cp) {
        this.data.cp.set(cp);
    }

    public void setCpEvolved(final String cpEvolved) {
        this.data.cpEvolved.set(cpEvolved);
    }

    public void setDef(final int def) {
        this.data.def.set(def);
    }

    public void setDuelAbility(final long duelAbility) {
        this.data.duelAbility.set(duelAbility);
    }

    public void setDuelAbilityIv(final long duelAbilityIv) {
        this.data.duelAbilityIv.set(duelAbilityIv);
    }

    public void setDustToLevel(final int dustToLevel) {
        this.data.dustToLevel.set(dustToLevel);
    }

    public void setEvolvable(final String evolvable) {
        this.data.evolvable.set(evolvable);
    }

    public void setGymDefense(final long gymDefense) {
        this.data.gymDefense.set(gymDefense);
    }

    public void setGymDefenseIv(final long gymDefenseIv) {
        this.data.gymDefenseIv.set(gymDefenseIv);
    }

    public void setGymOffense(final double gymOffense) {
        this.data.gymOffense.set(gymOffense);
    }

    public void setGymOffenseIv(final double gymOffenseIv) {
        this.data.gymOffenseIv.set(gymOffenseIv);
    }

    public void setHp(final int hp) {
        this.data.hp.set(hp);
    }

    public void setIsFavorite(final boolean isFavorite) {
        this.data.isFavorite.set(isFavorite);
    }

    public void setIv(final String iv) {
        this.data.iv.set(iv);
    }

    public void setLevel(final double level) {
        this.data.level.set(level);
    }

    public void setMaxCp(final int maxCp) {
        this.data.maxCp.set(maxCp);
    }

    public void setMaxCpCurrent(final int maxCpCurrent) {
        this.data.maxCpCurrent.set(maxCpCurrent);
    }

    public void setMaxEvolvedCp(final int maxEvolvedCp) {
        this.data.maxEvolvedCp.set(maxEvolvedCp);
    }

    public void setMaxEvolvedCpCurrent(final int maxEvolvedCpCurrent) {
        this.data.maxEvolvedCpCurrent.set(maxEvolvedCpCurrent);
    }

    public void setMove1(final String move1) {
        this.data.move1.set(move1);
    }

    public void setMove2(final String move2) {
        this.data.move2.set(move2);
    }

    public void setNickname(final String nickname) {
        this.data.nickname.set(nickname);
    }

    public void setNumId(final int numId) {
        this.data.numId.set(numId);
    }

    public void setPokeball(final String pokeball) {
        this.data.pokeball.set(pokeball);
    }

    public void setPokemon(final Pokemon pokemon) {
        this.data.pokemon = pokemon;
        initialize();
    }

    public void setSpecies(final String species) {
        this.data.species.set(species);
    }

    public void setStam(final int stam) {
        this.data.stam.set(stam);
    }

    public void setType1(final String type1) {
        this.data.type1.set(type1);
    }

    public void setType2(final String type2) {
        this.data.type2.set(type2);
    }

    public StringProperty speciesProperty() {
        return data.species;
    }

    public IntegerProperty stamProperty() {
        return data.stam;
    }

    public StringProperty type1Property() {
        return data.type1;
    }

    public StringProperty type2Property() {
        return data.type2;
    }
}
