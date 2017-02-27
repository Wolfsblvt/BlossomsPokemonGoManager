package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import me.corriekay.pokegoutil.utils.helpers.EvolveHelper;

/**
 * Provide custom formatting for the moveset ranking columns while allowing sorting on original values.
 */
@SuppressWarnings("serial")
class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

    private CheckBoxCellRenderer vr = new CheckBoxCellRenderer();
    private EvolveHelper evolve;

    public CheckBoxCellEditor() {
        vr.getCheckBox().addItemListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return vr.getCheckBox().isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
        if (value != null) {
            CheckBoxCellRenderer chcr = (CheckBoxCellRenderer) vr.getTableCellRendererComponent(table, value, isSelected, true, row, col);
            if(chcr.getCheckBox().isEnabled()) {
                evolve = (EvolveHelper) value;
                return chcr;
            }
        }
        return null;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (evolve != null) {
            evolve.setEvolveWithItem(e.getStateChange() == ItemEvent.SELECTED);
            evolve = null;
        }
        this.fireEditingStopped();
    }
}
