package huan.luu.gov.gui;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import huan.luu.gov.common.Constants;

/**
 * @author ashraf_sarhan
 * 
 */
public class CellChangeColorRenderer implements TableCellRenderer {

	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		if(isSelected){
			c.setBackground(Color.decode("#CCFFCC"));
		}
		// Apply zebra style on table rows
		else if (row % 2 == 0) {
			c.setBackground(Constants.EVEN_ROW_COLOR);
		} else {
			c.setBackground(Constants.ODD_ROW_COLOR);
		}

		if (column == Constants.STATUS_IDX) {
			Object cellObject = table.getModel().getValueAt(row, Constants.STATUS_IDX);
			Color color = Constants.DEFAULT_FOREGROUND_COLOR;
			if (cellObject != null && cellObject.toString().equalsIgnoreCase("ERROR")){
				color = Color.RED;
			}
			c.setForeground(color);
		} else {
			c.setForeground(Constants.DEFAULT_FOREGROUND_COLOR);
		}

		return c;
	}

}
