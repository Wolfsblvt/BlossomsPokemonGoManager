package me.corriekay.pokegoutil.utils.windows.renderer;

/**
 * A class that contains several cell renderer helper stuff.
 */
public class CellRendererHelper {
    public static final DefaultCellRenderer DEFAULT = new DefaultCellRenderer();
    public static final NumberCellRenderer NUMBER = new NumberCellRenderer();
    public static final PercentageCellRenderer PERCENTAGE = new PercentageCellRenderer();
    public static final FutureCellRenderer FUTURE = new FutureCellRenderer();
    public static final AutoIncrementCellRenderer AUTO_INCREMENT = new AutoIncrementCellRenderer();

    /** Prevent initializing this class. */
    private CellRendererHelper() {
    }

    /**
     * Creates a new number cell renderer with a rating limit.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The cell renderer.
     */
    public static NumberCellRenderer numberCellRendererWith(final double min, final double max) {
        return new NumberCellRenderer().withRatingColors(min, max);
    }
}
