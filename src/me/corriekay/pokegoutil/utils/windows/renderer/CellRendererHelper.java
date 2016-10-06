package me.corriekay.pokegoutil.utils.windows.renderer;

import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

/**
 * A class that contains several cell renderer helper stuff.
 */
public final class CellRendererHelper {
    // Default renderer
    public static final DefaultCellRenderer DEFAULT = new DefaultCellRenderer();
    public static final NumberCellRenderer NUMBER = new NumberCellRenderer();
    public static final PercentageCellRenderer PERCENTAGE = new PercentageCellRenderer();
    public static final FutureCellRenderer FUTURE = new FutureCellRenderer();
    public static final AutoIncrementCellRenderer AUTO_INCREMENT = new AutoIncrementCellRenderer();

    // Special renderer
    public static final NumberCellRenderer IV = new NumberCellRenderer().withRatingColors(0, PokemonUtils.MAX_IV);

    /** Prevent initializing this class. */
    private CellRendererHelper() {
    }
}
