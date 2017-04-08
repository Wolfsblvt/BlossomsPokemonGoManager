package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import me.corriekay.pokegoutil.utils.helpers.EvolveHelper;

/**
 * Provide a checkbox editor for clicking in the checkBox renderer and editing the content.
 */
class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

    private static final long serialVersionUID = 7138280995796928980L;
    private CheckBoxCellRenderer checkBoxCellRenderer = new CheckBoxCellRenderer();
    private EvolveHelper evolve;

    /**
     * Default constructor to add a listener in the checkBox renderer.
     */
    CheckBoxCellEditor() {
        checkBoxCellRenderer.getCheckBox().addItemListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return checkBoxCellRenderer.getCheckBox().isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int col) {
        if (value != null) {
            final CheckBoxCellRenderer chcr = (CheckBoxCellRenderer) checkBoxCellRenderer.getTableCellRendererComponent(table, value, isSelected, true, row, col);
            if (chcr.getCheckBox().isEnabled()) {
                evolve = (EvolveHelper) value;
                return chcr;
            }
        }
        return null;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if (evolve != null) {
            evolve.setEvolveWithItem(e.getStateChange() == ItemEvent.SELECTED);
            evolve = null;
        }
        this.fireEditingStopped();
    }
}
