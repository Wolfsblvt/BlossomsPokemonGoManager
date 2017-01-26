package me.corriekay.pokegoutil.utils.pokemon;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.main.PokemonMeta;
import com.pokegoapi.util.PokeDictionary;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import POGOProtos.Settings.Master.MoveSettingsOuterClass.MoveSettings;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass.PokemonSettings;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;

/**
 * General Pokemon helper functions.
 */
public final class PokemonUtils {
    public static final int MAX_IV = 15;

    /**
     * A list of all currently not existing Pokémon.
     */
    public static final List<PokemonId> NOT_EXISTING_POKEMON_LIST = Arrays.asList(
        PokemonId.ARTICUNO,
        PokemonId.ZAPDOS,
        PokemonId.MOLTRES,
        PokemonId.MEWTWO,
        PokemonId.MEW,
        PokemonId.UNRECOGNIZED,
        PokemonId.MISSINGNO
    );

    /** Prevent initializing this class. */
    private PokemonUtils() {
    }

    /**
     * Returns the Name for the Pokémon with given id in the current language.
     *
     * @param id The Pokémon ID
     * @return The translated Pokémon name
     */
    public static String getLocalPokeName(final int id) {
        final String lang = ConfigNew.getConfig().getString(ConfigKey.LANGUAGE);

        final Locale locale;
        final String[] langar = lang.split(StringLiterals.UNDERSCORE);
        if (langar.length == 1) {
            locale = new Locale(langar[0]);
        } else {
            locale = new Locale(langar[0], langar[1]);
        }

        return PokeDictionary.getDisplayName(id, locale);
    }

    /**
     * Returns the Name of the Pokémon pokemon in the current language.
     *
     * @param pokemon The Pokémon
     * @return The translated Pokémon name
     */
    public static String getLocalPokeName(final Pokemon pokemon) {
        return getLocalPokeName(pokemon.getPokemonId().getNumber());
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

    /**
     * Formats given Pokémon Type to a readable String.
     *
     * @param pokemonType The Pokémon Type.
     * @return Pokémon Type String.
     */
    public static String formatType(final POGOProtos.Enums.PokemonTypeOuterClass.PokemonType pokemonType) {
        return StringUtils.capitalize(pokemonType.toString().toLowerCase().replace("none", "").replace("pokemon_type_", ""));
    }

    /**
     * Formats given Pokémon Move to a readable String.
     *
     * @param move The Pokémon Move.
     * @return Pokémon Move String.
     */
    public static String formatMove(final PokemonMove move) {
        return WordUtils.capitalize(move.toString().toLowerCase().replaceAll("_fast", "").replaceAll(StringLiterals.UNDERSCORE, StringLiterals.SPACE));
    }

    /**
     * Formats given DPS (= Damage per second) to a readable String. With braces.
     *
     * @param dps The DPS.
     * @return DPS String.
     */
    public static String formatDps(final double dps) {
        return "(" + String.format("%.2f", dps) + " dps)";
    }

    /**
     * Formats given Item to a readable String.
     *
     * @param item The Item.
     * @return Item String.
     */
    public static String formatItem(final ItemId item) {
        return WordUtils.capitalize(item.toString().toLowerCase().replaceAll("item_", "").replaceAll(StringLiterals.UNDERSCORE, StringLiterals.SPACE));
    }

    /**
     * Checks if given Pokémon has STAB (=Same Type Attack Bonus), for primary or secondary move.
     *
     * @param p       The Pokémon.
     * @param primary If it should check the primary move, or not.
     * @return Weather or not the Pokémon has STAB.
     */
    public static boolean hasStab(final Pokemon p, final boolean primary) {
        return hasStab(p.getPokemonId(), primary ? p.getMove1() : p.getMove2());
    }

    /**
     * Checks if a Pokémon with given ID has STAB (=Same Type Attack Bonus) with given move.
     *
     * @param pokemonId The Pokémons ID.
     * @param move      The move.
     * @return Weather or not the Pokémon has STAB.
     */
    public static boolean hasStab(final PokemonId pokemonId, final PokemonMove move) {
        final PokemonSettings settings = PokemonMeta.getPokemonSettings(pokemonId);
        final MoveSettings moveSettings = PokemonMeta.getMoveSettings(move);
        return settings.getType().equals(moveSettings.getPokemonType()) || settings.getType2().equals(moveSettings.getPokemonType());
    }
}

