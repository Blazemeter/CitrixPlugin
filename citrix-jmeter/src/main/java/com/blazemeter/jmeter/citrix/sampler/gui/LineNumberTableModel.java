package com.blazemeter.jmeter.citrix.sampler.gui;

import javax.swing.table.AbstractTableModel;

abstract class LineNumberTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8891118394163711665L;

	protected abstract int getDataColumnCount();

	protected abstract Class<?> getDataColumnClass(int columnIndex);

	protected abstract Object getDataValueAt(int row, int column);

	protected abstract void setDataValueAt(Object aValue, int rowIndex, int columnIndex);

	protected abstract String getDataColumnName(int column);

	protected abstract boolean isDataCellEditable(int rowIndex, int columnIndex);

	@Override
	public final int getColumnCount() {
		return getDataColumnCount() + 1;
	}

	@Override
	public final Class<?> getColumnClass(int columnIndex) {
		return (columnIndex == 0) ? Integer.class : getDataColumnClass(columnIndex - 1);
	}

	@Override
	public final String getColumnName(int column) {
		return (column == 0) ? "" : getDataColumnName(column - 1);
	}

	@Override
	public final Object getValueAt(int row, int column) {
		return (column == 0) ? row + 1 : getDataValueAt(row, column - 1);
	}

	@Override
	public final boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0 && isDataCellEditable(rowIndex, columnIndex - 1);
	}

	@Override
	public final void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex > 0) {
			setDataValueAt(aValue, rowIndex, columnIndex - 1);
		}
	}
}