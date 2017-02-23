package me.corriekay.pokegoutil.data.enums;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.windows.renderer.CellRendererHelper;

/**
 * Enum that defines all possible column types for table columns.
 */
public enum ColumnType {
    AUTO_INCREMENT(
        String.class,
        Comparators.STRING,
        CellRendererHelper.AUTO_INCREMENT
    ),
    DATE(
        String.class,
        Comparators.DATE_STRING
    ),
    INT(
        Integer.class,
        Comparators.INT,
        CellRendererHelper.NUMBER
    ),
    LONG(
        Long.class,
        Comparators.LONG,
        CellRendererHelper.NUMBER
    ),
    DOUBLE(
        Double.class,
        Comparators.DOUBLE,
        CellRendererHelper.NUMBER
    ),
    NULLABLE_INT(
        String.class,
        Comparators.NULLABLE_INT,
        CellRendererHelper.NUMBER
    ),
    PERCENTAGE(
        Double.class,
        Comparators.DOUBLE,
        CellRendererHelper.PERCENTAGE
    ),
    DPS1VALUE(
        Double.class,
        Comparators.DOUBLE,
        CellRendererHelper.DPS1VALUE
    ),
    DPS2VALUE(
        Double.class,
        Comparators.DOUBLE,
        CellRendererHelper.DPS2VALUE
    ),
    STRING(
        String.class,
        Comparators.STRING
    ),
    FUTURE_STRING(
        String.class,
        Comparators.FUTURE_STRING,
        CellRendererHelper.FUTURE
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
     * Constructor to create a column type enum field.
     *
     * @param clazz      The class type of the column, what the data is.
     * @param comparator The comparator for that column.
     */
    ColumnType(final Class clazz, final Comparator comparator) {
        this(clazz, comparator, CellRendererHelper.DEFAULT);
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
