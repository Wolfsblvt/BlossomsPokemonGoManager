package me.corriekay.pokegoutil.DATA.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Enums.PokemonFamilyIdOuterClass;
import POGOProtos.Enums.PokemonIdOuterClass;
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
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

public class PokemonModel {
    private final IntegerProperty numId = new SimpleIntegerProperty();
    private final StringProperty nickname = new SimpleStringProperty();
    private final StringProperty species = new SimpleStringProperty();
    private final DoubleProperty level = new SimpleDoubleProperty();
    private final StringProperty iv = new SimpleStringProperty();
    private final IntegerProperty atk = new SimpleIntegerProperty();
    private final IntegerProperty def = new SimpleIntegerProperty();
    private final IntegerProperty stam = new SimpleIntegerProperty();
    private final StringProperty type1 = new SimpleStringProperty();
    private final StringProperty type2 = new SimpleStringProperty();
    private final StringProperty move1 = new SimpleStringProperty();
    private final StringProperty move2 = new SimpleStringProperty();
    private final IntegerProperty cp = new SimpleIntegerProperty();
    private final IntegerProperty hp = new SimpleIntegerProperty();
    private final IntegerProperty maxCp = new SimpleIntegerProperty();
    private final IntegerProperty maxCpCurrent = new SimpleIntegerProperty();
    private final IntegerProperty maxEvolvedCpCurrent = new SimpleIntegerProperty();
    private final IntegerProperty maxEvolvedCp = new SimpleIntegerProperty();
    private final IntegerProperty candies = new SimpleIntegerProperty();
    private final IntegerProperty candies2Evlv = new SimpleIntegerProperty();
    private final IntegerProperty dustToLevel = new SimpleIntegerProperty();
    private final StringProperty pokeball = new SimpleStringProperty();
    private final StringProperty caughtDate = new SimpleStringProperty();
    private final BooleanProperty isFavorite = new SimpleBooleanProperty();
    private final LongProperty duelAbility = new SimpleLongProperty();
    private final DoubleProperty gymOffense = new SimpleDoubleProperty();
    private final LongProperty gymDefense = new SimpleLongProperty();
    private final LongProperty duelAbilityIv = new SimpleLongProperty();
    private final DoubleProperty gymOffenseIv = new SimpleDoubleProperty();
    private final LongProperty gymDefenseIv = new SimpleLongProperty();
    private final StringProperty move1Rating = new SimpleStringProperty();
    private final StringProperty move2Rating = new SimpleStringProperty();
    private final StringProperty cpEvolved = new SimpleStringProperty();
    private final StringProperty evolvable = new SimpleStringProperty();

    private Pokemon pokemon;
    private final AccountManager accountManager = AccountManager.getInstance();

    public PokemonModel(final Pokemon pokemon) {
        this.pokemon = pokemon;
        initialze();
    }

    private void initialze() {
        final PokemonMeta meta = pokemon.getMeta() != null ? pokemon.getMeta() : new PokemonMeta();

        setNumId(meta.getNumber());
        setNickname(pokemon.getNickname());
        setSpecies(PokeHandler.getLocalPokeName(pokemon));
        setLevel(pokemon.getLevel());
        setIv(Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(pokemon)));
        setAtk(pokemon.getIndividualAttack());
        setDef(pokemon.getIndividualDefense());
        setStam(pokemon.getIndividualStamina());
        setType1(StringUtils.capitalize(meta.getType1().toString().toLowerCase()));
        setType2(StringUtils.capitalize(meta.getType2().toString().toLowerCase()));

        final Double dps1 = PokemonUtils.dpsForMove(pokemon, true);
        final Double dps2 = PokemonUtils.dpsForMove(pokemon, false);
        setMove1(WordUtils.capitalize(
                pokemon.getMove1().toString().toLowerCase()
                .replaceAll("_fast", "").replaceAll("_", " "))
                + " (" + String.format("%.2f", dps1) + "dps)");
        setMove2(WordUtils.capitalize(
                pokemon.getMove2().toString().toLowerCase()
                .replaceAll("_", " "))
                + " (" + String.format("%.2f", dps2) + "dps)");

        setCp(pokemon.getCp());
        setHp(pokemon.getMaxStamina());

        int trainerLevel = -1;

        try {
            trainerLevel = accountManager.getPlayerProfile().getStats().getLevel();
        } catch (LoginFailedException | RemoteServerException e) {
            System.out.println("Error: Cannot find meta data for trainer level");
        }

        // Max CP calculation for current PokemonModel

        int attack = pokemon.getIndividualAttack() + meta.getBaseAttack();
        int defense = pokemon.getIndividualDefense() + meta.getBaseDefense();
        int stamina = pokemon.getIndividualStamina() + meta.getBaseStamina();
        final int maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
        final int maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
        setMaxCp(maxCp);
        setMaxCpCurrent(maxCpCurrent);

        // Max CP calculation for highest evolution of current PokemonModel
        final PokemonFamilyIdOuterClass.PokemonFamilyId familyId = pokemon.getPokemonFamily();
        PokemonIdOuterClass.PokemonId highestFamilyId = PokemonMetaRegistry.getHightestForFamily(familyId);

        // Eeveelutions exception handling
        if (familyId.getNumber() == PokemonFamilyIdOuterClass.PokemonFamilyId.FAMILY_EEVEE.getNumber()) {
            if (pokemon.getPokemonId().getNumber() == PokemonIdOuterClass.PokemonId.EEVEE.getNumber()) {
                final PokemonMeta vap = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.VAPOREON);
                final PokemonMeta fla = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.FLAREON);
                final PokemonMeta jol = PokemonMetaRegistry.getMeta(PokemonIdOuterClass.PokemonId.JOLTEON);
                if (vap != null && fla != null && jol != null) {
                    final Comparator<PokemonMeta> cMeta = (m1, m2) -> {
                        final int comb1 = PokemonCpUtils.getMaxCp(
                                m1.getBaseAttack(),
                                m1.getBaseDefense(),
                                m1.getBaseStamina());
                        final int comb2 = PokemonCpUtils.getMaxCp(
                                m2.getBaseAttack(),
                                m2.getBaseDefense(),
                                m2.getBaseStamina());
                        return comb1 - comb2;
                    };
                    highestFamilyId = PokemonIdOuterClass.PokemonId.forNumber(
                            Collections.max(Arrays.asList(vap, fla, jol), cMeta).getNumber());
                }
            } else {
                // This is one of the eeveelutions, so PokemonMetaRegistry.getHightestForFamily() returns Eevee.
                // We correct that here
                highestFamilyId = pokemon.getPokemonId();
            }
        }

        final PokemonMeta highestFamilyMeta = PokemonMetaRegistry.getMeta(highestFamilyId);
        if (highestFamilyMeta == null) {
            System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
        } else {
            if (highestFamilyId == pokemon.getPokemonId()) {
                setMaxEvolvedCpCurrent(maxCpCurrent);
                setMaxEvolvedCp(maxCp);
                setCpEvolved("-");
            } else {
                attack = highestFamilyMeta.getBaseAttack() + pokemon.getIndividualAttack();
                defense = highestFamilyMeta.getBaseDefense() + pokemon.getIndividualDefense();
                stamina = highestFamilyMeta.getBaseStamina() + pokemon.getIndividualStamina();
                setMaxEvolvedCpCurrent(PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
                setMaxEvolvedCp(PokemonCpUtils.getMaxCp(attack, defense, stamina));
                setCpEvolved(String.valueOf(
                        PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, pokemon.getLevel())));
            }
        }

        int candies = -1;
        try {
            candies = pokemon.getCandy();
        } catch (LoginFailedException | RemoteServerException e) {
            System.out.println("Error: Unable to get candies");
        }

        setCandies(candies);
        if (pokemon.getCandiesToEvolve() != 0) {
            setCandies2Evlv(pokemon.getCandiesToEvolve());
            // Rounded down candies / toEvolve
            setEvolvable(String.valueOf((int) ((double) candies / pokemon.getCandiesToEvolve())));

        } else {
            setCandies2Evlv(0);
            setEvolvable("-");
        }
        setDustToLevel(pokemon.getStardustCostsForPowerup());
        setPokeball(WordUtils.capitalize(
                pokemon.getPokeball().toString().toLowerCase()
                .replaceAll("item_", "").replaceAll("_", " ")));
        setCaughtDate(DateHelper.toString(DateHelper.fromTimestamp(pokemon.getCreationTimeMs())));
        setIsFavorite(pokemon.isFavorite());
        setDuelAbility(PokemonUtils.duelAbility(pokemon, false));
        setGymOffense(PokemonUtils.gymOffense(pokemon, false));
        setGymDefense(PokemonUtils.gymDefense(pokemon, false));

        setDuelAbilityIv(PokemonUtils.duelAbility(pokemon, true));
        setGymOffenseIv(PokemonUtils.gymOffense(pokemon, true));
        setGymDefenseIv(PokemonUtils.gymDefense(pokemon, true));
        setMove1Rating(PokemonUtils.moveRating(pokemon, true));
        setMove2Rating(PokemonUtils.moveRating(pokemon, false));
    }

    // Bunch of getters and setters

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void setPokemon(final Pokemon pokemon) {
        this.pokemon = pokemon;
        initialze();
    }

    public int getNumId() {
        return numId.get();
    }

    public IntegerProperty numIdProperty() {
        return numId;
    }

    public void setNumId(final int numId) {
        this.numId.set(numId);
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname.set(nickname);
    }

    public String getSpecies() {
        return species.get();
    }

    public StringProperty speciesProperty() {
        return species;
    }

    public void setSpecies(final String species) {
        this.species.set(species);
    }

    public double getLevel() {
        return level.get();
    }

    public DoubleProperty levelProperty() {
        return level;
    }

    public void setLevel(final double level) {
        this.level.set(level);
    }

    public String getIv() {
        return iv.get();
    }

    public StringProperty ivProperty() {
        return iv;
    }

    public void setIv(final String iv) {
        this.iv.set(iv);
    }

    public int getAtk() {
        return atk.get();
    }

    public IntegerProperty atkProperty() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk.set(atk);
    }

    public int getDef() {
        return def.get();
    }

    public IntegerProperty defProperty() {
        return def;
    }

    public void setDef(final int def) {
        this.def.set(def);
    }

    public int getStam() {
        return stam.get();
    }

    public IntegerProperty stamProperty() {
        return stam;
    }

    public void setStam(final int stam) {
        this.stam.set(stam);
    }

    public String getType1() {
        return type1.get();
    }

    public StringProperty type1Property() {
        return type1;
    }

    public void setType1(final String type1) {
        this.type1.set(type1);
    }

    public String getType2() {
        return type2.get();
    }

    public StringProperty type2Property() {
        return type2;
    }

    public void setType2(final String type2) {
        this.type2.set(type2);
    }

    public String getMove1() {
        return move1.get();
    }

    public StringProperty move1Property() {
        return move1;
    }

    public void setMove1(final String move1) {
        this.move1.set(move1);
    }

    public String getMove2() {
        return move2.get();
    }

    public StringProperty move2Property() {
        return move2;
    }

    public void setMove2(final String move2) {
        this.move2.set(move2);
    }

    public int getCp() {
        return cp.get();
    }

    public IntegerProperty cpProperty() {
        return cp;
    }

    public void setCp(final int cp) {
        this.cp.set(cp);
    }

    public int getHp() {
        return hp.get();
    }

    public IntegerProperty hpProperty() {
        return hp;
    }

    public void setHp(final int hp) {
        this.hp.set(hp);
    }

    public int getMaxCp() {
        return maxCp.get();
    }

    public IntegerProperty maxCpProperty() {
        return maxCp;
    }

    public void setMaxCp(final int maxCp) {
        this.maxCp.set(maxCp);
    }

    public int getMaxCpCurrent() {
        return maxCpCurrent.get();
    }

    public IntegerProperty maxCpCurrentProperty() {
        return maxCpCurrent;
    }

    public void setMaxCpCurrent(final int maxCpCurrent) {
        this.maxCpCurrent.set(maxCpCurrent);
    }

    public int getMaxEvolvedCpCurrent() {
        return maxEvolvedCpCurrent.get();
    }

    public IntegerProperty maxEvolvedCpCurrentProperty() {
        return maxEvolvedCpCurrent;
    }

    public void setMaxEvolvedCpCurrent(final int maxEvolvedCpCurrent) {
        this.maxEvolvedCpCurrent.set(maxEvolvedCpCurrent);
    }

    public int getMaxEvolvedCp() {
        return maxEvolvedCp.get();
    }

    public IntegerProperty maxEvolvedCpProperty() {
        return maxEvolvedCp;
    }

    public void setMaxEvolvedCp(final int maxEvolvedCp) {
        this.maxEvolvedCp.set(maxEvolvedCp);
    }

    public int getCandies() {
        return candies.get();
    }

    public IntegerProperty candiesProperty() {
        return candies;
    }

    public void setCandies(final int candies) {
        this.candies.set(candies);
    }

    public int getCandies2Evlv() {
        return candies2Evlv.get();
    }

    public IntegerProperty candies2EvlvProperty() {
        return candies2Evlv;
    }

    public void setCandies2Evlv(final int candies2Evlv) {
        this.candies2Evlv.set(candies2Evlv);
    }

    public int getDustToLevel() {
        return dustToLevel.get();
    }

    public IntegerProperty dustToLevelProperty() {
        return dustToLevel;
    }

    public void setDustToLevel(final int dustToLevel) {
        this.dustToLevel.set(dustToLevel);
    }

    public String getPokeball() {
        return pokeball.get();
    }

    public StringProperty pokeballProperty() {
        return pokeball;
    }

    public void setPokeball(final String pokeball) {
        this.pokeball.set(pokeball);
    }

    public String getCaughtDate() {
        return caughtDate.get();
    }

    public StringProperty caughtDateProperty() {
        return caughtDate;
    }

    public void setCaughtDate(final String caughtDate) {
        this.caughtDate.set(caughtDate);
    }

    public boolean isIsFavorite() {
        return isFavorite.get();
    }

    public BooleanProperty isFavoriteProperty() {
        return isFavorite;
    }

    public void setIsFavorite(final boolean isFavorite) {
        this.isFavorite.set(isFavorite);
    }

    public long getDuelAbility() {
        return duelAbility.get();
    }

    public LongProperty duelAbilityProperty() {
        return duelAbility;
    }

    public void setDuelAbility(final long duelAbility) {
        this.duelAbility.set(duelAbility);
    }

    public double getGymOffense() {
        return gymOffense.get();
    }

    public DoubleProperty gymOffenseProperty() {
        return gymOffense;
    }

    public void setGymOffense(final double gymOffense) {
        this.gymOffense.set(gymOffense);
    }

    public long getGymDefense() {
        return gymDefense.get();
    }

    public LongProperty gymDefenseProperty() {
        return gymDefense;
    }

    public void setGymDefense(final long gymDefense) {
        this.gymDefense.set(gymDefense);
    }

    public long getDuelAbilityIv() {
        return duelAbilityIv.get();
    }

    public LongProperty duelAbilityIvProperty() {
        return duelAbilityIv;
    }

    public void setDuelAbilityIv(final long duelAbilityIv) {
        this.duelAbilityIv.set(duelAbilityIv);
    }

    public double getGymOffenseIv() {
        return gymOffenseIv.get();
    }

    public DoubleProperty gymOffenseIvProperty() {
        return gymOffenseIv;
    }

    public void setGymOffenseIv(final double gymOffenseIv) {
        this.gymOffenseIv.set(gymOffenseIv);
    }

    public long getGymDefenseIv() {
        return gymDefenseIv.get();
    }

    public LongProperty gymDefenseIvProperty() {
        return gymDefenseIv;
    }

    public void setGymDefenseIv(final long gymDefenseIv) {
        this.gymDefenseIv.set(gymDefenseIv);
    }

    public String getMove1Rating() {
        return move1Rating.get();
    }

    public StringProperty move1RatingProperty() {
        return move1Rating;
    }

    public void setMove1Rating(final String move1Rating) {
        this.move1Rating.set(move1Rating);
    }

    public String getMove2Rating() {
        return move2Rating.get();
    }

    public StringProperty move2RatingProperty() {
        return move2Rating;
    }

    public void setMove2Rating(final String move2Rating) {
        this.move2Rating.set(move2Rating);
    }

    public String getCpEvolved() {
        return cpEvolved.get();
    }

    public StringProperty cpEvolvedProperty() {
        return cpEvolved;
    }

    public void setCpEvolved(final String cpEvolved) {
        this.cpEvolved.set(cpEvolved);
    }

    public String getEvolvable() {
        return evolvable.get();
    }

    public StringProperty evolvableProperty() {
        return evolvable;
    }

    public void setEvolvable(final String evolvable) {
        this.evolvable.set(evolvable);
    }

    public String getSummary() {
        return getNickname() + " (" + getSpecies() + ")" + " IV: " + getIv() + " CP: " + getCp();
    }

    public boolean isInGym() {
        return !pokemon.getDeployedFortId().isEmpty();
    }

    public int getCandyCostsForPowerup() {
        return pokemon.getCandyCostsForPowerup();
    }

    public int getStardustCostsForPowerup() {
        return pokemon.getStardustCostsForPowerup();
    }
}
