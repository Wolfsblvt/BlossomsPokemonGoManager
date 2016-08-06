package me.corriekay.pokegoutil.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.PokeNames;

import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;

public class PokeHandler {
	private ArrayList<Pokemon> mons;

	public PokeHandler(Pokemon pokemon) {
		this(new Pokemon[] { pokemon });
	}

	public PokeHandler(Pokemon[] pokemon) {
		this(Arrays.asList(pokemon));
	}

	public PokeHandler(List<Pokemon> pokemon) {
		mons = new ArrayList<Pokemon>(pokemon);
	}

	// region Static helper methods to handle Pokémon

	/***
	 * Rename a single Pokemon based on a pattern
	 * 
	 * @param pattern
	 *            The pattern to use for renaming
	 * @param p
	 *            The Pokemon to rename
	 * @return The result of type <c>NicknamePokemonResponse.Result</c>
	 */
	public static NicknamePokemonResponse.Result renameWithPattern(String pattern, Pokemon pokemon) {
		return renWPattern(pattern, pokemon, getRenamePattern());
	}

	/***
	 * Helper method to get the rename Regex-Pattern so we don't have to rebuild
	 * it every time we process a pokemon. This should save resources.
	 */
	private static Pattern getRenamePattern() {
		return Pattern.compile("(%([a-zA-Z]+)%)");
	}

	/***
	 * Renames a pokemon according to a regex pattern.
	 * 
	 * @param pattern
	 * @param pokemon
	 * @param regex
	 */
	private static NicknamePokemonResponse.Result renWPattern(String pattern, Pokemon pokemon, Pattern regex) {
		Pokemon p = pokemon;
		String pokeNick = pattern;
		Matcher m = regex.matcher(pattern);
		while (m.find()) {
			String fullExpr = m.group(1);
			String exprName = m.group(2);

			try {
				// Get ReplacePattern Object and use its get method
				// to get the replacement string.
				// Replace in nickname.
				ReplacePattern rep = ReplacePattern.valueOf(exprName.toUpperCase());
				String repStr = rep.get(pokemon);
				pokeNick = pokeNick.replace(fullExpr, repStr);
			} catch (IllegalArgumentException iae) {
				// Do nothing, nothing to replace
			}
		}

		// Actually renaming the Pokémon with the calculated nickname
		try {
			NicknamePokemonResponse.Result result = p.renamePokemon(pokeNick);
			return result;
		} catch (LoginFailedException | RemoteServerException e) {
			System.out.println("API Error while renaming " + getLocalPokeName(p) + "(" + p.getNickname() + ")!");
			System.err.println(e.getStackTrace());
			return NicknamePokemonResponse.Result.UNRECOGNIZED;
		}
	}

	/***
	 * Returns the Name for the Pokémon with <c>id</c> in the current language.
	 * 
	 * @param id
	 *            The Pokémon ID
	 * @return The translated Pokémon name
	 */
	public static String getLocalPokeName(int id) {
		// TODO: change call to getConfigItem to config class once implemented
		String lang = Config.getConfig().getString("options.lang", "en");

		Locale locale;
		String[] langar = lang.split("_");
		if (langar.length == 1) {
			locale = new Locale(langar[0]);
		} else {
			locale = new Locale(langar[0], langar[1]);
		}

		String name = null;
		try {
			name = new String(PokeNames.getDisplayName(id, locale).getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		name = StringUtils.capitalize(name.toLowerCase());
		return name;
	}

	/***
	 * Returns the Name of the Pokémon <c>pokemon</c> in the current language.
	 * 
	 * @param pokemon
	 *            The Pokémon
	 * @return The translated Pokémon name
	 */
	public static String getLocalPokeName(Pokemon pokemon) {
		return getLocalPokeName(pokemon.getPokemonId().getNumber());
	}

	// endregion

	/***
	 * Rename a bunch of Pokemon based on a pattern
	 * 
	 * @param pattern
	 *            The pattern to use for renaming
	 * @param perPokeCallback
	 *            Will be called for each Pokémon that has been (tried) to
	 *            rename.
	 * @return A <c>LinkedHashMap</c> with each Pokémon as key and the result as
	 *         value.
	 */
	public LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> bulkRenameWithPattern(String pattern,
			BiConsumer<NicknamePokemonResponse.Result, Pokemon> perPokeCallback) {

		LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> results = new LinkedHashMap<>();

		Pattern regex = getRenamePattern();
		mons.forEach(p -> {
			NicknamePokemonResponse.Result result = renWPattern(pattern, p, regex);
			if (perPokeCallback != null) {
				perPokeCallback.accept(result, p);
			}
			results.put(p, result);
		});

		return results;
	}

	public void bulkRenameWithPattern(String pattern) {
		bulkRenameWithPattern(pattern, null);
	}

	/**
	 * This enum represents the definition of placeholder strings for Pokémon
	 * renaming. To add a new placeholder rule, add a new enum constant, pass a
	 * friendly Name as constructor parameter and override the <c>get</c> method
	 * to return what should be the replacement for the enum. The enum constant
	 * serves as placeholder.
	 * 
	 * Example: String "%cp%_%name%" for a 200cp Magikarp will result in a
	 * renaming of that Magikarp to "200_Magicarp".
	 * 
	 */
	public enum ReplacePattern {
		NICK("Nickname") {
			@Override
			public String get(Pokemon p) {
				return p.getNickname();
			}
		},
		NAME("Pokemon Name") {
			@Override
			public String get(Pokemon p) {
				return getLocalPokeName(p);
			}
		},
		CP("Combat Points") {
			@Override
			public String get(Pokemon p) {
				return String.valueOf(p.getCp());
			}
		},
		IVRATING("IV Rating") {
			@Override
			public String get(Pokemon p) {
				return String.valueOf((int) p.getIvRatio() * 100);
			}
		};

		private final String friendlyName;

		ReplacePattern(String friendlyName) {
			this.friendlyName = friendlyName;
		}

		/***
		 * Returns the friendly name for the replacement pattern. Can be used to
		 * generate explanations for possible placeholders automatically.
		 */
		public String toString() {
			return friendlyName;
		}

		/***
		 * This method must be overwritten and should return what should be the
		 * replacement.
		 * 
		 * @param p
		 *            The Pokémon that receives the new nick name.
		 * @return The value that the placeholder should be replaced with
		 */
		public abstract String get(Pokemon p);
	}
}
