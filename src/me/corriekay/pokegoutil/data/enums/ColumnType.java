package me.corriekay.pokegoutil.data.enums;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.helpers.DateHelper;
import me.corriekay.pokegoutil.utils.windows.DefaultCellRenderer;
import me.corriekay.pokegoutil.utils.windows.PercentageCellRenderer;


public enum ColumnType {
    DATE(
        String.class,
        ObjectHolder.dateStringComparator
    ),
    INT(
        Integer.class,
        ObjectHolder.intComparator
    ),
    DOUBLE(
        Double.class,
        ObjectHolder.doubleComparator
    ),
    NULLABLE_INT(
        String.class,
        ObjectHolder.nullableIntComparator
    ),
    PERCENTAGE(
        Double.class,
        ObjectHolder.doubleComparator,
        ObjectHolder.percentageCellRenderer
    ),
    STRING(
        String.class,
        ObjectHolder.stringComparator
    );

    /**
     * This private class is needed to create the objects that are used in this enum.
     */
    private static final class ObjectHolder {
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

        // The cell renderers.
        public static final DefaultCellRenderer defaultCellRenderer = new DefaultCellRenderer();
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
        this(clazz, comparator, ObjectHolder.defaultCellRenderer);
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
