package me.corriekay.pokegoutil.utils.pokemon;

import com.pokegoapi.api.pokemon.Pokemon;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeNick {

    public static final int MAX_NICKNAME_LENGTH = 12;

    /**
     * Helper variable to get the rename Regex-Pattern so we don't have to rebuild
     * it every time we process a pokemon. This should save resources.
     */
    private static Pattern regex = Pattern.compile("(%([a-zA-Z0-9_]+)%)");

    // Constructor parameters
    public String pattern;
    public Pokemon pokemon;

    // Calculated variables
    public String fullNickname;
    public String usableNickname;

    public PokeNick(String pattern, Pokemon pokemon) {
        this.pattern = pattern;
        this.pokemon = pokemon;
        fullNickname = pattern;
        Matcher m = regex.matcher(pattern);
        while (m.find()) {
            String fullExpr = m.group(1);
            String exprName = m.group(2);

            try {
                // Get ReplacePattern Object and use its get method to get the replacement string.
                // Replace in nickname.
                PokeHandler.ReplacePattern rep = PokeHandler.ReplacePattern.valueOf(exprName.toUpperCase());
                String repStr = rep.get(pokemon);
                fullNickname = fullNickname.replace(fullExpr, repStr);
            } catch (IllegalArgumentException iae) {
                // Do nothing, nothing to replace
            }
        }

        // Set the stuff we need
        usableNickname = StringUtils.substring(fullNickname, 0, MAX_NICKNAME_LENGTH);
    }

    @Override
    public String toString() {
        return usableNickname;
    }

    public boolean isTooLong() {
        return fullNickname.length() > MAX_NICKNAME_LENGTH;
    }

    public boolean equals(String otherNick) {
        return this.toString().equals(otherNick);
    }
}
