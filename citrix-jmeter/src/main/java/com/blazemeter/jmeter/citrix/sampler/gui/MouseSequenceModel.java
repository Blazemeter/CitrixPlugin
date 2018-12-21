package com.blazemeter.jmeter.citrix.sampler.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.sampler.MouseSequenceItem;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

class MouseSequenceModel extends LineNumberTableModel {

	private static final long serialVersionUID = -6323977390826023412L;

	private final transient List<MouseSequenceItem> sequence = new ArrayList<>();

	public List<MouseSequenceItem> getItems() {
		return sequence;
	}

	public void setItems(List<MouseSequenceItem> items) {
		sequence.clear();
		if (items != null) {
			sequence.addAll(items);
		}
		fireTableDataChanged();
	}

	@Override
	public int getDataColumnCount() {
		return 6;
	}

	@Override
	protected Class<?> getDataColumnClass(int columnIndex) {
		Class<?> clazz;
		switch (columnIndex) {
		case 0:
			clazz = MouseAction.class;
			break;
		case 1:
			clazz = Integer.class;
			break;
		case 2:
			clazz = Integer.class;
			break;
		case 3:
			clazz = Set.class;
			break;
		case 4:
			clazz = Set.class;
			break;
		case 5:
			clazz = Long.class;
			break;
		default:
			clazz = null;
		}
		return clazz;
	}

	@Override
	public String getDataColumnName(int column) {
		String name;
		switch (column) {
		case 0:
			name = CitrixUtils.getResString("mouse_sequence_model_header_action", false);
			break;
		case 1:
			name = CitrixUtils.getResString("mouse_sequence_model_header_x", false);
			break;
		case 2:
			name = CitrixUtils.getResString("mouse_sequence_model_header_y", false);
			break;
		case 3:
			name = CitrixUtils.getResString("mouse_sequence_model_header_buttons", false);
			break;
		case 4:
			name = CitrixUtils.getResString("mouse_sequence_model_header_modifiers", false);
			break;
		case 5:
			name = CitrixUtils.getResString("mouse_sequence_model_header_delay", false);
			break;
		default:
			name = null;
		}
		return name;
	}

	@Override
	public int getRowCount() {
		return sequence.size();
	}

	@Override
	public Object getDataValueAt(int row, int col) {
		final MouseSequenceItem item = sequence.get(row);
		Object value;
		switch (col) {
		case 0:
			value = item.getAction();
			break;
		case 1:
			value = item.getX();
			break;
		case 2:
			value = item.getY();
			break;
		case 3:
			value = item.getButtons();
			break;
		case 4:
			value = item.getModifiers();
			break;
		case 5:
			value = item.getDelay();
			break;
		default:
			value = null;
			break;
		}
		return value;
	}

	@Override
	protected boolean isDataCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDataValueAt(Object aValue, int rowIndex, int columnIndex) {
		MouseSequenceItem item = sequence.get(rowIndex);
		if (item != null) {
			switch (columnIndex) {
			case 0:
				item.setAction((MouseAction) aValue);
				break;
			case 1:
				item.setX((int) aValue);
				break;
			case 2:
				item.setY((int) aValue);
				break;
			case 3:
				final Set<MouseButton> buttons = item.getButtons();
				buttons.clear();
				buttons.addAll((Set<MouseButton>) aValue);
				break;
			case 4:
				final Set<Modifier> modifiers = item.getModifiers();
				modifiers.clear();
				modifiers.addAll((Set<Modifier>) aValue);
				break;
			case 5:
				item.setDelay((long) aValue);
				break;
			default:
				// NOOP
			}
		}
	}

	public void addItem(MouseSequenceItem item) {
		final int index = sequence.size();
		if (sequence.add(item)) {
			fireTableRowsInserted(index, index);
		}
	}

	public void removeItem(int index) {
		sequence.remove(index);
		fireTableRowsDeleted(index, index);
	}
}