package me.corriekay.pokegoutil.data.enums;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.windows.renderer.DefaultCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.NumberCellRenderer;
import me.corriekay.pokegoutil.utils.windows.renderer.PercentageCellRenderer;


public enum ColumnType {
    DATE(
        String.class,
        Comparators.dateStringComparator
    ),
    INT(
        Integer.class,
        Comparators.intComparator,
        CellRenderers.numberCellRenderer
    ),
    DOUBLE(
        Double.class,
        Comparators.doubleComparator,
        CellRenderers.numberCellRenderer
    ),
    NULLABLE_INT(
        String.class,
        Comparators.nullableIntComparator,
        CellRenderers.numberCellRenderer
    ),
    PERCENTAGE(
        Double.class,
        Comparators.doubleComparator,
        CellRenderers.percentageCellRenderer
    ),
    STRING(
        String.class,
        Comparators.stringComparator
    );

    /**
     * This private class is needed to create the comparators that are used in this enum.
     */
    private static final class Comparators {
        // The comparators.
        public static final Comparator<String> dateStringComparator = (date1, date2) -> DateHelper.fromString(date1)
            .compareTo(DateHelper.fromString(date2));
        public static final Comparator<Double> doubleComparator = Double::compareTo;
        public static final Comparator<Integer> intComparator = Integer::compareTo;
        public static final Comparator<String> stringComparator = String::compareTo;
        public static final Comparator<String> nullableIntComparator = (s1, s2) -> {
            if ("-".equals(s1)) {
                s1 = "0";
            }
            if ("-".equals(s2)) {
                s2 = "0";
            }
            return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
        };
    }

    /**
     * This private class is needed to create the cell renderers that are used in this enum.
     */
    private static final class CellRenderers {
        public static final DefaultCellRenderer defaultCellRenderer = new DefaultCellRenderer();
        public static final NumberCellRenderer numberCellRenderer = new NumberCellRenderer();
        public static final PercentageCellRenderer percentageCellRenderer = new PercentageCellRenderer();
    }


    public final Class clazz;
    public final Comparator comparator;
    public final TableCellRenderer tableCellRenderer;

    /**
     * Constructor to create a column type enum field.
     *
     * @param clazz      The class type of the column, what the data is.
     * @param comparator The comparator for that column.
     */
    ColumnType(final Class clazz, final Comparator comparator) {
        this(clazz, comparator, CellRenderers.defaultCellRenderer);
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
