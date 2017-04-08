package me.corriekay.pokegoutil.utils.windows;

import javax.swing.RowFilter;

import com.pokegoapi.api.pokemon.Pokemon;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

/**
 * Helper class to filter the contents of the table based on the fields of the Pokemon class.
 */
public class PokemonRowFilter extends RowFilter<PokemonTableModel, Integer> {
    final String filterText;

    /**
     * Default constructor to store filterText variable that will be used in the override "include" method.
     * @param filterText the text that will be used as filter
     */
    PokemonRowFilter(final String filterText) {
        this.filterText = filterText;
    }

    @Override
    public boolean include(final javax.swing.RowFilter.Entry<? extends PokemonTableModel, ? extends Integer> entry) {
        final Pokemon poke = entry.getModel().getPokemonByIndexNotConverting(entry.getIdentifier());
        if (poke != null) {
            final boolean useFamilyName = ConfigNew.getConfig().getBool(ConfigKey.INCLUDE_FAMILY);
            String familyName = "";
            if (useFamilyName) {
                // Try translating family name
                try {
                    final PokemonId familyPokemonId = PokemonId.valueOf(poke.getPokemonFamily().toString().replaceAll(StringLiterals.FAMILY_PREFIX, ""));
                    familyName = PokemonUtils.getLocalPokeName(familyPokemonId.getNumber());
                } catch (final IllegalArgumentException e) {
                    familyName = poke.getPokemonFamily().toString();
                }
            }

            String searchme = Utilities.concatString(',',
                    PokemonUtils.getLocalPokeName(poke),
                    useFamilyName ? familyName : "",
                            poke.getNickname(),
                            poke.getSettings().getType().toString(),
                            poke.getSettings().getType2().toString(),
                            poke.getMove1().toString(),
                            poke.getMove2().toString(),
                            poke.getPokeball().toString());
            searchme = searchme.replaceAll("_FAST", "").replaceAll(StringLiterals.FAMILY_PREFIX, "").replaceAll("NONE", "")
                    .replaceAll("ITEM_", "").replaceAll(StringLiterals.POKEMON_TYPE_PREFIX, "").replaceAll(StringLiterals.UNDERSCORE, "")
                    .replaceAll(StringLiterals.SPACE, "").toLowerCase();

            final String[] terms = filterText.split(";");
            for (final String s : terms) {
                if (searchme.contains(s)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
