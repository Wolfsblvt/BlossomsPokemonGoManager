package me.corriekay.pokegoutil.utils.windows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.data.enums.ColumnType;
import me.corriekay.pokegoutil.utils.helpers.CollectionHelper;

/**
 * A class that holds data relevant for each column
 */
public enum PokemonTableColumn {
    ID(0, "Id", ColumnType.INT),
    NICKNAME(1, "Nickname", ColumnType.STRING),
    SPECIES(2, "Species", ColumnType.STRING),
    IV_RATING(3, "IV %", ColumnType.PERCENTAGE),
    LEVEL(4, "Lvl", ColumnType.DOUBLE),
    IV_ATTACK(5, "Atk", ColumnType.INT),
    IV_DEFENSE(6, "Def", ColumnType.INT),
    IV_STAMINA(7, "Stam", ColumnType.INT),
    TYPE_1(8, "Type 1", ColumnType.STRING),
    TYPE_2(9, "Type 2", ColumnType.STRING),
    MOVE_1(10, "Move 1", ColumnType.STRING),
    MOVE_2(11, "Move 2", ColumnType.STRING),
    CP(12, "CP", ColumnType.INT),
    HP(13, "HP", ColumnType.INT),
    MAX_CP_CUR(14, "Max CP (Cur)", ColumnType.INT),
    MAX_CP_40(15, "Max CP (40)", ColumnType.INT),
    MAX_CP_EVOLVED_CUR(16, "Max CP Evolved (Cur)", ColumnType.INT),
    MAX_CP_EVOLVED_40(17, "Max CP Evolved (40)", ColumnType.INT),
    CANDIES(18, "Candies", ColumnType.STRING),
    CANDIES_TO_EVOLVE(19, "To Evolve", ColumnType.NULLABLE_INT),
    STARDUST_TO_POWERUP(20, "Stardust", ColumnType.INT),
    CAUGHT_WITH(21, "Caught With", ColumnType.STRING),
    CAUGHT_TIME(22, "Caught Time", ColumnType.DATE),
    FAVORITE(23, "Favorite", ColumnType.STRING),
    DUEL_ABILITY(24, "Duel Ability", ColumnType.PERCENTAGE),
    GYM_OFFENSE(25, "Gym Offense", ColumnType.PERCENTAGE),
    GYM_DEFENSE(26, "Gym Defense", ColumnType.PERCENTAGE),
    CP_EVOLVED(27, "CP Evolved", ColumnType.NULLABLE_INT),
    EVOLVABLE_COUNT(28, "Evolvable", ColumnType.NULLABLE_INT),
    DUEL_ABILITY_RATING(29, "Duel Ability Rating", ColumnType.PERCENTAGE),
    GYM_OFFENSE_RATING(30, "Gym Offense Rating", ColumnType.PERCENTAGE),
    GYM_DEFENSE_RATING(31, "Gym Defense Rating", ColumnType.PERCENTAGE);


    public final int id;
    public final String name;
    public final ColumnType columnType;
    public final ArrayList data;

    /**
     * Constructor to create the enum entries.
     *
     * @param id         The id of the column.
     * @param name       The name of the column.
     * @param columnType The type of the column.
     */
    PokemonTableColumn(int id, String name, ColumnType columnType) {
        this.id = id;
        this.name = name;
        this.columnType = columnType;
        this.data = CollectionHelper.provideArrayList(columnType.clazz);
    }

    /**
     * Returns the comparator for the given column, based on the column type.
     *
     * @return The comparator.
     */
    public Comparator getComparator() {
        return columnType.comparator;
    }

    /**
     * Returns the table cell renderer for the given column, based on the column type.
     *
     * @return The cell renderer.
     */
    public TableCellRenderer getCellRenderer() {
        return columnType.tableCellRenderer;
    }

    public static PokemonTableColumn getForId(int id) {
        for (final PokemonTableColumn column : PokemonTableColumn.values()) {
            if (column.id == id) {
                return column;
            }
        }
        // If not found, we throw an exception
        throw new NoSuchElementException("There is no column with id " + id);
    }
}
