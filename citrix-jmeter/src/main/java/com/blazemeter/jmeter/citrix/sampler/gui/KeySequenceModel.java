package com.blazemeter.jmeter.citrix.sampler.gui;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.sampler.KeySequenceItem;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.util.ArrayList;
import java.util.List;

public class KeySequenceModel extends LineNumberTableModel {
  private static final long serialVersionUID = 7840705826475773097L;

  private final transient List<KeySequenceItem> sequence = new ArrayList<>();

  public List<KeySequenceItem> getItems() {
    return sequence;
  }

  public void setItems(List<KeySequenceItem> items) {
    sequence.clear();
    if (items != null) {
      sequence.addAll(items);
    }
    fireTableDataChanged();
  }

  @Override
  public int getDataColumnCount() {
    return 3;
  }

  @Override
  protected Class<?> getDataColumnClass(int columnIndex) {
    Class<?> clazz;
    switch (columnIndex) {
      case 0:
        clazz = KeyState.class;
        break;
      case 1:
        clazz = Integer.class;
        break;
      case 2:
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
        name = CitrixUtils.getResString("key_sequence_model_header_state", false);
        break;
      case 1:
        name = CitrixUtils.getResString("key_sequence_model_header_code", false);
        break;
      case 2:
        name = CitrixUtils.getResString("key_sequence_model_header_delay", false);
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
    final KeySequenceItem item = sequence.get(row);
    Object value;
    switch (col) {
      case 0:
        value = item.getKeyState();
        break;
      case 1:
        value = item.getKeyCode();
        break;
      case 2:
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

  @Override
  public void setDataValueAt(Object aValue, int rowIndex, int columnIndex) {
    KeySequenceItem item = sequence.get(rowIndex);
    if (item != null) {
      switch (columnIndex) {
        case 0:
          item.setKeyState((KeyState) aValue);
          break;
        case 1:
          item.setKeyCode((int) aValue);
          break;
        case 2:
          item.setDelay((long) aValue);
          break;
        default:
          // NOOP
      }
    }
  }

  public void addItem(KeySequenceItem item) {
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
