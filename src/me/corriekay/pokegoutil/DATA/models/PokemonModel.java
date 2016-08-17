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

    private final IntegerProperty numId;
    private final StringProperty nickname;
    private final StringProperty species;
    private final DoubleProperty level;
    private final StringProperty IV;
    private final IntegerProperty atk;
    private final IntegerProperty def;
    private final IntegerProperty stam;
    private final StringProperty type1;
    private final StringProperty type2;
    private final StringProperty move1;
    private final StringProperty move2;
    private final IntegerProperty cp;
    private final IntegerProperty hp;
    private final IntegerProperty maxCp;
    private final IntegerProperty maxCpCurrent;
    private final IntegerProperty maxEvolvedCpCurrent;
    private final IntegerProperty maxEvolvedCp;
    private final IntegerProperty candies;
    private final StringProperty candies2Evlv;
    private final IntegerProperty dustToLevel;
    private final StringProperty pokeball;
    private final StringProperty caughtDate;
    private final BooleanProperty isFavorite;
    private final LongProperty duelAbility;
    private final LongProperty gymOffense;
    private final LongProperty gymDefense;
    private final StringProperty move1Rating;
    private final StringProperty move2Rating;
    private final StringProperty cpEvolved;

    private PokemonModel() {
        this(null);
    }

    public PokemonModel(Pokemon p) {
        PokemonMeta meta = p.getMeta() != null? p.getMeta():new PokemonMeta();

        this.numId = new SimpleIntegerProperty(meta.getNumber());
        this.nickname = new SimpleStringProperty(p.getNickname()) ;
        this.species = new SimpleStringProperty(PokeHandler.getLocalPokeName(p));
        this.level = new SimpleDoubleProperty((double) p.getLevel());
        this.IV = new SimpleStringProperty(Utilities.percentageWithTwoCharacters(PokemonUtils.ivRating(p)));
        this.atk = new SimpleIntegerProperty(p.getIndividualAttack());
        this.def = new SimpleIntegerProperty(p.getIndividualDefense());
        this.stam = new SimpleIntegerProperty(p.getIndividualStamina());
        this.type1 = new SimpleStringProperty(StringUtils.capitalize(meta.getType1().toString().toLowerCase()));
        this.type2 = new SimpleStringProperty(StringUtils.capitalize(meta.getType2().toString().toLowerCase()));

        Double dps1 = PokemonUtils.dpsForMove(p, true);
        Double dps2 = PokemonUtils.dpsForMove(p, false);
        this.move1 = new SimpleStringProperty(WordUtils.capitalize(p.getMove1().toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " ")) + " (" + String.format("%.2f", dps1) + "dps)");
        this.move2 = new SimpleStringProperty(WordUtils.capitalize(p.getMove2().toString().toLowerCase().replaceAll("_", " ")) + " (" + String.format("%.2f", dps2) + "dps)");

        this.cp = new SimpleIntegerProperty(p.getCp());
        this.hp = new SimpleIntegerProperty(p.getMaxStamina());

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
        this.maxCp = new SimpleIntegerProperty(maxCp);
        this.maxCpCurrent = new SimpleIntegerProperty(maxCpCurrent);

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
            this.maxEvolvedCpCurrent = new SimpleIntegerProperty(maxCpCurrent);
            this.maxEvolvedCp = new SimpleIntegerProperty(maxCp);
            this.cpEvolved = new SimpleStringProperty("-");
        } else {
            attack = highestFamilyMeta.getBaseAttack() + p.getIndividualAttack();
            defense = highestFamilyMeta.getBaseDefense() + p.getIndividualDefense();
            stamina = highestFamilyMeta.getBaseStamina() + p.getIndividualStamina();
            this.maxEvolvedCpCurrent = new SimpleIntegerProperty(PokemonCpUtils.getMaxCpForTrainerLevel(attack, defense, stamina, trainerLevel));
            this.maxEvolvedCp = new SimpleIntegerProperty(PokemonCpUtils.getMaxCp(attack, defense, stamina));
            this.cpEvolved = new SimpleStringProperty(String.valueOf(PokemonCpUtils.getCpForPokemonLevel(attack, defense, stamina, p.getLevel())));
        }

        int candies = 0;
        try {
            candies = p.getCandy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.candies = new SimpleIntegerProperty(candies);
        if (p.getCandiesToEvolve() != 0)
            this.candies2Evlv = new SimpleStringProperty(String.valueOf(p.getCandiesToEvolve()));
        else
            this.candies2Evlv = new SimpleStringProperty("-");
        this.dustToLevel = new SimpleIntegerProperty(p.getStardustCostsForPowerup());
        this.pokeball = new SimpleStringProperty(WordUtils.capitalize(p.getPokeball().toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " ")));
        this.caughtDate = new SimpleStringProperty(DateHelper.toString(DateHelper.fromTimestamp(p.getCreationTimeMs())));
        this.isFavorite = new SimpleBooleanProperty(p.isFavorite());
        this.duelAbility = new SimpleLongProperty(PokemonUtils.duelAbility(p));
        this.gymOffense = new SimpleLongProperty(PokemonUtils.gymOffense(p));
        this.gymDefense = new SimpleLongProperty(PokemonUtils.gymDefense(p));
        this.move1Rating = new SimpleStringProperty(PokemonUtils.moveRating(p, true));
        this.move2Rating = new SimpleStringProperty(PokemonUtils.moveRating(p, false));
    }

    // Bunch of getters and setters:
    //
    //

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