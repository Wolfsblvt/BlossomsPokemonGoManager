package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import me.corriekay.pokegoutil.utils.helpers.EvolveHelper;
import me.corriekay.pokegoutil.utils.windows.PokemonTable;
import me.corriekay.pokegoutil.windows.PokemonTab;

/**
 * Provide custom formatting for the moveset ranking columns while allowing sorting on original values.
 */
public class CheckBoxCellRenderer extends JPanel implements TableCellRenderer {

    // Padding left and right can be overwritten by custom renderer.
    protected static final int PADDING_LEFT = 3;
    protected static final int PADDING_RIGHT = 3;
    private static final long serialVersionUID = 1340158676852621702L;

    private JCheckBox checkBox;

    /**
     * Default constructor for creating the component properly.
     */
    public CheckBoxCellRenderer() {
        super(new FlowLayout(FlowLayout.LEADING, 0, 0));
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setVerticalAlignment(SwingUtilities.TOP);
        add(checkBox);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        if (value != null) {
            final EvolveHelper evolve = (EvolveHelper) value;
            checkBox.setVisible(true);
            checkBox.setSelected(evolve.isEvolveWithItem());
            final int itemCount = PokemonTab.getPlayerItems().getItem(evolve.getItemToEvolveId()).getCount();
            checkBox.setText(evolve.toString() + " [" + itemCount + "]");
            checkBox.setEnabled(true);
            if (itemCount <= 0) {
                checkBox.setEnabled(false);
                checkBox.setOpaque(false);
            }
        } else {
            checkBox.setEnabled(true);
            checkBox.setText("");
            checkBox.setVisible(false);
        }
        setNativeLookAndFeel(table, value, isSelected);
        
        return this;
    }

    /**
     * Sets the native look and feel for the TableCellRenderer.
     * This method should be called first in getTableCellRendererComponent() when extending this CellRenderer,
     * or should be overwritten and called then.
     *
     * @param table      The table.
     * @param value      The value.
     * @param isSelected If the cell is selected.
     */
    protected void setNativeLookAndFeel(final JTable table, final Object value, final boolean isSelected) {
        setOpaque(true);
        setDefaultSelectionColors(table, isSelected);
        setFont(table.getFont());
        setBorder(BorderFactory.createEmptyBorder(0, PADDING_LEFT, PokemonTable.ROW_HEIGHT_PADDING, PADDING_RIGHT));
    }

    /**
     * Set the selection colors for the cell.
     *
     * @param table      The table.
     * @param isSelected If the cell is selected.
     */
    private void setDefaultSelectionColors(final JTable table, final boolean isSelected) {
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
        } else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }
    }
    
    /**
     * Return the checkBox component.
     * @return checkBox component 
     */
    public JCheckBox getCheckBox() {
        return checkBox;
    }
}
