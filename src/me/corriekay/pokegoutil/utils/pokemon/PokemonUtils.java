package me.corriekay.pokegoutil.utils.pokemon;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.pokegoapi.api.player.Team;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonType;
import com.pokegoapi.util.PokeDictionary;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;

/**
 * General Pokemon helper functions
 */
public final class PokemonUtils {

    /**
     * A list of all currently not existing Pokémon.
     */
    public static final List<PokemonId> NOT_EXISTING_POKEMON_LIST = Arrays.asList(
        PokemonId.DITTO,
        PokemonId.ARTICUNO,
        PokemonId.ZAPDOS,
        PokemonId.MOLTRES,
        PokemonId.MEWTWO,
        PokemonId.MEW,
        PokemonId.UNRECOGNIZED
    );

    /** Prevent initializing this class. */
    private PokemonUtils() {
    }

    /**
     * Returns the Name for the Pokémon with <c>id</c> in the current language.
     *
     * @param id The Pokémon ID
     * @return The translated Pokémon name
     */
    public static String getLocalPokeName(final int id) {
        // TODO: change call to getConfigItem to config class once implemented
        final String lang = ConfigNew.getConfig().getString(ConfigKey.LANGUAGE);

        Locale locale;
        final String[] langar = lang.split("_");
        if (langar.length == 1) {
            locale = new Locale(langar[0]);
        } else {
            locale = new Locale(langar[0], langar[1]);
        }

        return PokeDictionary.getDisplayName(id, locale);
    }

    /**
     * Returns the Name of the Pokémon <c>pokemon</c> in the current language.
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

    public static String formatType(PokemonType pokemonType) {
        return StringUtils.capitalize(pokemonType.toString().toLowerCase().replaceAll("none", ""));
    }

    public static String formatMove(PokemonMove move) {
        return WordUtils.capitalize(move.toString().toLowerCase().replaceAll("_fast", "").replaceAll("_", " "));
    }

    public static String formatItem(ItemId item) {
        return WordUtils.capitalize(item.toString().toLowerCase().replaceAll("item_", "").replaceAll("_", " "));
    }
}

