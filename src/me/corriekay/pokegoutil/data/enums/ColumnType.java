package me.corriekay.pokegoutil.data.enums;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.windows.renderer.AutoIncrementCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.DefaultCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.FutureCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.NumberCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.PercentageCellRenderer;

/**
 * Enum that defines all possible column types for table columns.
 */
public enum ColumnType {
    AUTO_INCREMENT(
        String.class,
        Comparators.STRING,
        CellRenderers.AUTO_INCREMENT
    ),
    DATE(
        String.class,
        Comparators.DATE_STRING
    ),
    INT(
        Integer.class,
        Comparators.INT,
        CellRenderers.NUMBER
    ),
    LONG(
        Long.class,
        Comparators.LONG,
        CellRenderers.NUMBER
    ),
    DOUBLE(
        Double.class,
        Comparators.DOUBLE,
        CellRenderers.NUMBER
    ),
    NULLABLE_INT(
        String.class,
        Comparators.NULLABLE_INT,
        CellRenderers.NUMBER
    ),
    PERCENTAGE(
        Double.class,
        Comparators.DOUBLE,
        CellRenderers.PERCENTAGE
    ),
    STRING(
        String.class,
        Comparators.STRING
    ),
    FUTURE_STRING(
        String.class,
        Comparators.FUTURE_STRING,
        CellRenderers.FUTURE
    );

    public final Class clazz;
    public final Comparator comparator;
    public final TableCellRenderer tableCellRenderer;

    /**
     * This private class is needed to create the comparators that are used in this enum.
     */
    private static final class Comparators {
        // The comparators.
        public static final Comparator<String> DATE_STRING = (date1, date2) -> DateHelper.fromString(date1)
            .compareTo(DateHelper.fromString(date2));
        public static final Comparator<Double> DOUBLE = Double::compareTo;
        public static final Comparator<Integer> INT = Integer::compareTo;
        public static final Comparator<Long> LONG = Long::compareTo;
        public static final Comparator<String> STRING = String::compareTo;
        public static final Comparator<CompletableFuture<String>> FUTURE_STRING = (left, right) -> left.getNow("").compareTo(right.getNow(""));
        public static final Comparator<String> NULLABLE_INT = (left, right) -> {
            if (StringLiterals.NO_VALUE_SIGN.equals(left)) {
                left = String.valueOf(0);
            }
            if (StringLiterals.NO_VALUE_SIGN.equals(right)) {
                right = String.valueOf(0);
            }
            return Integer.compare(Integer.parseInt(left), Integer.parseInt(right));
        };
    }

    /**
     * This private class is needed to create the cell renderers that are used in this enum.
     */
    private static final class CellRenderers {
        public static final DefaultCellRenderer DEFAULT = new DefaultCellRenderer();
        public static final NumberCellRenderer NUMBER = new NumberCellRenderer();
        public static final PercentageCellRenderer PERCENTAGE = new PercentageCellRenderer();
        public static final FutureCellRenderer FUTURE = new FutureCellRenderer();
        public static final AutoIncrementCellRenderer AUTO_INCREMENT = new AutoIncrementCellRenderer();
    }

    /**
     * Constructor to create a column type enum field.
     *
     * @param clazz      The class type of the column, what the data is.
     * @param comparator The comparator for that column.
     */
    ColumnType(final Class clazz, final Comparator comparator) {
        this(clazz, comparator, CellRenderers.DEFAULT);
    }

    /**
     * Constructor to create a column type enum field.
     *
     * @param clazz             The class type of the column, what the data is.
     * @param comparator        The comparator for that column.
     * @param tableCellRenderer The table cell renderer.
     */
    ColumnType(final Class clazz, final Comparator comparator, final TableCellRenderer tableCellRenderer) {
        this.clazz = clazz;
        this.comparator = comparator;
        this.tableCellRenderer = tableCellRenderer;
    }
}
