package huan.luu.gov.gui.support;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import huan.luu.gov.common.WebLink;

public class URLTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<WebLink> linkModel;
	public URLTableModel(Object[][] data, Object[] tableHeader) {
		super(data, tableHeader);
	}
	
//	public URLTableModel(List<WebLink> model){
//		super (data, Constants.TABLE_HEADER);
//	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return super.getValueAt(row, column);
	}

}
