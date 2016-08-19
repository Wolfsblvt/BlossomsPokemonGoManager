package me.corriekay.pokegoutil.DATA.models;

import POGOProtos.Enums.PokemonFamilyIdOuterClass;
import POGOProtos.Enums.PokemonIdOuterClass;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMeta;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import javafx.beans.property.*;
import me.corriekay.pokegoutil.DATA.managers.AccountManager;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokemonCpUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class PokemonModel {
    private final IntegerProperty numId = new SimpleIntegerProperty();
    private final StringProperty nickname = new SimpleStringProperty();
    private final StringProperty species = new SimpleStringProperty();
    private final DoubleProperty level = new SimpleDoubleProperty();
    private final StringProperty IV = new SimpleStringProperty();
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
    private final StringProperty candies2Evlv = new SimpleStringProperty();
    private final IntegerProperty dustToLevel = new SimpleIntegerProperty();
    private final StringProperty pokeball = new SimpleStringProperty();
    private final StringProperty caughtDate = new SimpleStringProperty();
    private final BooleanProperty isFavorite = new SimpleBooleanProperty();
    private final LongProperty duelAbility = new SimpleLongProperty();
    private final LongProperty gymOffense = new SimpleLongProperty();
    private final LongProperty gymDefense = new SimpleLongProperty();
    private final StringProperty move1Rating = new SimpleStringProperty();
    private final StringProperty move2Rating = new SimpleStringProperty();
    private final StringProperty cpEvolved = new SimpleStringProperty();

    public PokemonModel(Pokemon p) {
        super();
        PokemonMeta meta = p.getMeta() != null? p.getMeta():new PokemonMeta();

        setNumId(meta.getNumber());
        setNickname(p.getNickname());
        setSpecies(PokeHandler.getLocalPokeName(p));
        setLevel((double) p.getLevel());
        setIV(Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));
        setAtk(p.getIndividualAttack());
        setDef(p.getIndividualDefense());
        setStam(p.getIndividualStamina());
        setType1(StringUtils.capitalize(meta.getType1().toString().toLowerCase()));
        setType2(StringUtils.capitalize(meta.getType2().toString().toLowerCase()));

        Double dps1 = PokemonUtils.dpsForMove(p, true);
        Double dps2 = PokemonUtils.dpsForMove(p, false);
        setMove1(WordUtils.capitalize(p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " ")) + " (" + String.format("%.2f", dps1) + "dps)");
        setMove2(WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " (" + String.format("%.2f", dps2) + "dps)");

        setCp(p.getCp());
        setHp(p.getMaxStamina());

        int trainerLevel = 1;
        try {
            trainerLevel = AccountManager.getPlayerProfile().getStats().getLevel();
        } catch (Exception e1) {
            System.out.println("Error: Cannot find meta data for trainer level");
        }

        // Max CP calculation for current PokemonModel
        int maxCpCurrent = 0, maxCp = 0;

        int attack = p.getIndividualAttack() + meta.getBaseAttack();
        int defense = p.getIndividualDefense() + meta.getBaseDefense();
        int stamina = p.getIndividualStamina() + meta.getBaseStamina();
        maxCpCurrent = PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel);
        maxCp = PokemonCpUtils.getMaxCp(attack, defense, stamina);
        setMaxCp(maxCp);
        setMaxCpCurrent(maxCpCurrent);

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
        if (highestFamilyMeta == null) {
            System.out.println("Error: Cannot find meta data for " + highestFamilyId.name());
        }
        if (highestFamilyId == p.getPokemonId()) {
            setMaxEvolvedCpCurrent(maxCpCurrent);
            setMaxEvolvedCp(maxCp);
            setCpEvolved("-");
        } else {
            attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
            defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
            stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();
            setMaxEvolvedCpCurrent(PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
            setMaxEvolvedCp(PokemonCpUtils.getMaxCp(attack, defense, stamina));
            setCpEvolved(String.valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, p.getLevel())));
        }

        int candies = 0;
        try {
            candies = p.getCandy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCandies(candies);
        if (p.getCandiesToEvolve() != 0)
            setCandies2Evlv(String.valueOf(p.getCandiesToEvolve()));
        else
            setCandies2Evlv("-");
        setDustToLevel(p.getStardustCostsForPowerup());
        setPokeball(WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
        setCaughtDate(DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs())));
        setIsFavorite(p.isFavorite());
        setDuelAbility(PokemonUtils.duelAbility(p));
        setGymOffense(PokemonUtils.gymOffense(p));
        setGymDefense(PokemonUtils.gymDefense(p));
        setMove1Rating(PokemonUtils.moveRating(p, true));
        setMove2Rating(PokemonUtils.moveRating(p, false));
    }

    // Bunch of getters and setters


    public int getNumId() {
        return numId.get();
    }

    public IntegerProperty numIdProperty() {
        return numId;
    }

    public void setNumId(int numId) {
        this.numId.set(numId);
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public String getSpecies() {
        return species.get();
    }

    public StringProperty speciesProperty() {
        return species;
    }

    public void setSpecies(String species) {
        this.species.set(species);
    }

    public double getLevel() {
        return level.get();
    }

    public DoubleProperty levelProperty() {
        return level;
    }

    public void setLevel(double level) {
        this.level.set(level);
    }

    public String getIV() {
        return IV.get();
    }

    public StringProperty IVProperty() {
        return IV;
    }

    public void setIV(String IV) {
        this.IV.set(IV);
    }

    public int getAtk() {
        return atk.get();
    }

    public IntegerProperty atkProperty() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk.set(atk);
    }

    public int getDef() {
        return def.get();
    }

    public IntegerProperty defProperty() {
        return def;
    }

    public void setDef(int def) {
        this.def.set(def);
    }

    public int getStam() {
        return stam.get();
    }

    public IntegerProperty stamProperty() {
        return stam;
    }

    public void setStam(int stam) {
        this.stam.set(stam);
    }

    public String getType1() {
        return type1.get();
    }

    public StringProperty type1Property() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1.set(type1);
    }

    public String getType2() {
        return type2.get();
    }

    public StringProperty type2Property() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2.set(type2);
    }

    public String getMove1() {
        return move1.get();
    }

    public StringProperty move1Property() {
        return move1;
    }

    public void setMove1(String move1) {
        this.move1.set(move1);
    }

    public String getMove2() {
        return move2.get();
    }

    public StringProperty move2Property() {
        return move2;
    }

    public void setMove2(String move2) {
        this.move2.set(move2);
    }

    public int getCp() {
        return cp.get();
    }

    public IntegerProperty cpProperty() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp.set(cp);
    }

    public int getHp() {
        return hp.get();
    }

    public IntegerProperty hpProperty() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp.set(hp);
    }

    public int getMaxCp() {
        return maxCp.get();
    }

    public IntegerProperty maxCpProperty() {
        return maxCp;
    }

    public void setMaxCp(int maxCp) {
        this.maxCp.set(maxCp);
    }

    public int getMaxCpCurrent() {
        return maxCpCurrent.get();
    }

    public IntegerProperty maxCpCurrentProperty() {
        return maxCpCurrent;
    }

    public void setMaxCpCurrent(int maxCpCurrent) {
        this.maxCpCurrent.set(maxCpCurrent);
    }

    public int getMaxEvolvedCpCurrent() {
        return maxEvolvedCpCurrent.get();
    }

    public IntegerProperty maxEvolvedCpCurrentProperty() {
        return maxEvolvedCpCurrent;
    }

    public void setMaxEvolvedCpCurrent(int maxEvolvedCpCurrent) {
        this.maxEvolvedCpCurrent.set(maxEvolvedCpCurrent);
    }

    public int getMaxEvolvedCp() {
        return maxEvolvedCp.get();
    }

    public IntegerProperty maxEvolvedCpProperty() {
        return maxEvolvedCp;
    }

    public void setMaxEvolvedCp(int maxEvolvedCp) {
        this.maxEvolvedCp.set(maxEvolvedCp);
    }

    public int getCandies() {
        return candies.get();
    }

    public IntegerProperty candiesProperty() {
        return candies;
    }

    public void setCandies(int candies) {
        this.candies.set(candies);
    }

    public String getCandies2Evlv() {
        return candies2Evlv.get();
    }

    public StringProperty candies2EvlvProperty() {
        return candies2Evlv;
    }

    public void setCandies2Evlv(String candies2Evlv) {
        this.candies2Evlv.set(candies2Evlv);
    }

    public int getDustToLevel() {
        return dustToLevel.get();
    }

    public IntegerProperty dustToLevelProperty() {
        return dustToLevel;
    }

    public void setDustToLevel(int dustToLevel) {
        this.dustToLevel.set(dustToLevel);
    }

    public String getPokeball() {
        return pokeball.get();
    }

    public StringProperty pokeballProperty() {
        return pokeball;
    }

    public void setPokeball(String pokeball) {
        this.pokeball.set(pokeball);
    }

    public String getCaughtDate() {
        return caughtDate.get();
    }

    public StringProperty caughtDateProperty() {
        return caughtDate;
    }

    public void setCaughtDate(String caughtDate) {
        this.caughtDate.set(caughtDate);
    }

    public boolean isIsFavorite() {
        return isFavorite.get();
    }

    public BooleanProperty isFavoriteProperty() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite.set(isFavorite);
    }

    public long getDuelAbility() {
        return duelAbility.get();
    }

    public LongProperty duelAbilityProperty() {
        return duelAbility;
    }

    public void setDuelAbility(long duelAbility) {
        this.duelAbility.set(duelAbility);
    }

    public long getGymOffense() {
        return gymOffense.get();
    }

    public LongProperty gymOffenseProperty() {
        return gymOffense;
    }

    public void setGymOffense(long gymOffense) {
        this.gymOffense.set(gymOffense);
    }

    public long getGymDefense() {
        return gymDefense.get();
    }

    public LongProperty gymDefenseProperty() {
        return gymDefense;
    }

    public void setGymDefense(long gymDefense) {
        this.gymDefense.set(gymDefense);
    }

    public String getMove1Rating() {
        return move1Rating.get();
    }

    public StringProperty move1RatingProperty() {
        return move1Rating;
    }

    public void setMove1Rating(String move1Rating) {
        this.move1Rating.set(move1Rating);
    }

    public String getMove2Rating() {
        return move2Rating.get();
    }

    public StringProperty move2RatingProperty() {
        return move2Rating;
    }

    public void setMove2Rating(String move2Rating) {
        this.move2Rating.set(move2Rating);
    }

    public String getCpEvolved() {
        return cpEvolved.get();
    }

    public StringProperty cpEvolvedProperty() {
        return cpEvolved;
    }

    public void setCpEvolved(String cpEvolved) {
        this.cpEvolved.set(cpEvolved);
    }
}