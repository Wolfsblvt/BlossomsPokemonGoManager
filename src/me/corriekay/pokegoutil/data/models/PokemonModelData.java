package me.corriekay.pokegoutil.data.models;

import com.pokegoapi.api.pokemon.Pokemon;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import me.corriekay.pokegoutil.data.managers.AccountManager;

public class PokemonModelData {
    public IntegerProperty numId;
    public StringProperty nickname;
    public StringProperty species;
    public DoubleProperty level;
    public StringProperty iv;
    public IntegerProperty atk;
    public IntegerProperty def;
    public IntegerProperty stam;
    public StringProperty type1;
    public StringProperty type2;
    public StringProperty move1;
    public StringProperty move2;
    public IntegerProperty cp;
    public IntegerProperty hp;
    public IntegerProperty maxCp;
    public IntegerProperty maxCpCurrent;
    public IntegerProperty maxEvolvedCpCurrent;
    public IntegerProperty maxEvolvedCp;
    public IntegerProperty candies;
    public IntegerProperty candies2Evlv;
    public IntegerProperty dustToLevel;
    public StringProperty pokeball;
    public StringProperty caughtDate;
    public BooleanProperty isFavorite;
    public LongProperty duelAbility;
    public DoubleProperty gymOffense;
    public LongProperty gymDefense;
    public LongProperty duelAbilityIv;
    public DoubleProperty gymOffenseIv;
    public LongProperty gymDefenseIv;
    public StringProperty cpEvolved;
    public StringProperty evolvable;
    public Pokemon pokemon;
    public AccountManager accountManager;
    public PokemonModelData(IntegerProperty numId, StringProperty nickname, StringProperty species, DoubleProperty level, StringProperty iv, IntegerProperty atk, IntegerProperty def,
            IntegerProperty stam, StringProperty type1, StringProperty type2, StringProperty move1, StringProperty move2, IntegerProperty cp, IntegerProperty hp, IntegerProperty maxCp,
            IntegerProperty maxCpCurrent, IntegerProperty maxEvolvedCpCurrent, IntegerProperty maxEvolvedCp, IntegerProperty candies, IntegerProperty candies2Evlv, IntegerProperty dustToLevel,
            StringProperty pokeball, StringProperty caughtDate, BooleanProperty isFavorite, LongProperty duelAbility, DoubleProperty gymOffense, LongProperty gymDefense, LongProperty duelAbilityIv,
            DoubleProperty gymOffenseIv, LongProperty gymDefenseIv, StringProperty cpEvolved, StringProperty evolvable, AccountManager accountManager) {
        this.numId = numId;
        this.nickname = nickname;
        this.species = species;
        this.level = level;
        this.iv = iv;
        this.atk = atk;
        this.def = def;
        this.stam = stam;
        this.type1 = type1;
        this.type2 = type2;
        this.move1 = move1;
        this.move2 = move2;
        this.cp = cp;
        this.hp = hp;
        this.maxCp = maxCp;
        this.maxCpCurrent = maxCpCurrent;
        this.maxEvolvedCpCurrent = maxEvolvedCpCurrent;
        this.maxEvolvedCp = maxEvolvedCp;
        this.candies = candies;
        this.candies2Evlv = candies2Evlv;
        this.dustToLevel = dustToLevel;
        this.pokeball = pokeball;
        this.caughtDate = caughtDate;
        this.isFavorite = isFavorite;
        this.duelAbility = duelAbility;
        this.gymOffense = gymOffense;
        this.gymDefense = gymDefense;
        this.duelAbilityIv = duelAbilityIv;
        this.gymOffenseIv = gymOffenseIv;
        this.gymDefenseIv = gymDefenseIv;
        this.cpEvolved = cpEvolved;
        this.evolvable = evolvable;
        this.accountManager = accountManager;
    }
}