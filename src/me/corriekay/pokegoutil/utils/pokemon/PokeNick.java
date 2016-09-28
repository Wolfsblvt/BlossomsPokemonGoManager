package me.corriekay.pokegoutil.utils.pokemon;

import com.pokegoapi.api.pokemon.Pokemon;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.corriekay.pokegoutil.utils.pokemon.PokeHandler.ReplacePattern;

/**
 * Calls to manage a nickname of a Pokémon.
 * The constructor replaces the patterns in the design with its values.
 * The Nick will also be cutted to the maximum nickname length.
 */
public class PokeNick {

    public static final int MAX_NICKNAME_LENGTH = 12;

    /**
     * Helper variable to get the rename Regex-Pattern so we don't have to rebuild
     * it every time we process a pokemon. This should save resources.
     */
    private static Pattern regex = Pattern.compile("(%([a-zA-Z0-9_]+)%)");

    // Constructor parameters
    public final String pattern;
    public final Pokemon pokemon;

    // Calculated variables
    public final String fullNickname;
    public final String usableNickname;

    /**
     * Creates a Pokémon nickname from given pattern and the Pokémon.
     *
     * @param pattern The pattern (with placeholders allowed).
     * @param pokemon The Pokémon
     */
    public PokeNick(final String pattern, final Pokemon pokemon) {
        this.pattern = pattern;
        this.pokemon = pokemon;
        final Matcher m = regex.matcher(pattern);
        String replacedNickname = pattern;
        while (m.find()) {
            final String fullExpr = m.group(1);
            final String exprName = m.group(2);

            try {
                // Get ReplacePattern Object and use its get method to get the replacement string.
                // Replace in nickname.
                final ReplacePattern rep = ReplacePattern.valueOf(exprName.toUpperCase());
                final String repStr = rep.get(pokemon);
                replacedNickname = replacedNickname.replace(fullExpr, repStr);
            } catch (IllegalArgumentException iae) {
                // Do nothing, nothing to replace
            }
        }

        // Set the stuff we need
        fullNickname = replacedNickname;
        usableNickname = StringUtils.substring(fullNickname, 0, MAX_NICKNAME_LENGTH);
    }

    @Override
    public String toString() {
        return usableNickname;
    }

    /**
     * Checks if the original planned nickname was too long.
     *
     * @return Weather or not the nickname was too long.
     */
    public boolean isTooLong() {
        return fullNickname.length() > MAX_NICKNAME_LENGTH;
    }
}
