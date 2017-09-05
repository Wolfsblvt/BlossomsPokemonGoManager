package me.corriekay.pokegoutil.utils.windows.renderer;

import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

/**
 * A class that contains several cell renderer helper stuff.
 */
public final class CellRendererHelper {
    // Default renderer
    public static final DefaultCellRenderer DEFAULT = new DefaultCellRenderer();
    public static final NumberCellRenderer NUMBER = new NumberCellRenderer();
    public static final NumberStringCellRenderer NUMBER_STRING = new NumberStringCellRenderer();
    public static final PercentageCellRenderer PERCENTAGE = new PercentageCellRenderer();
    public static final DpsCellRenderer DPS1VALUE = new DpsCellRenderer().withRatingColors(0, 17);
    public static final DpsCellRenderer DPS2VALUE = new DpsCellRenderer().withRatingColors(0, 45);
    public static final FutureCellRenderer FUTURE = new FutureCellRenderer();
    public static final AutoIncrementCellRenderer AUTO_INCREMENT = new AutoIncrementCellRenderer();
    public static final CheckBoxCellRenderer CHECK_BOX = new CheckBoxCellRenderer();
    public static final CheckBoxCellEditor CHECK_BOX_EDITOR = new CheckBoxCellEditor();

    // Special renderer
    public static final NumberCellRenderer IV = new NumberCellRenderer().withRatingColors(0, PokemonUtils.MAX_IV);

    /** Prevent initializing this class. */
    private CellRendererHelper() {
    }
}
