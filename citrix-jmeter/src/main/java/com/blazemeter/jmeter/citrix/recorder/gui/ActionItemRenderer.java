package com.blazemeter.jmeter.citrix.recorder.gui;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

class ActionItemRenderer extends BasicComboBoxRenderer {

  @Override
  public Component getListCellRendererComponent(
      JList list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus);

    if (value != null || index == -1) {
      ActionItem item = (ActionItem) value;
      setText((item != null) ? item.getDescription() : "NAN");
    }
    return this;
  }
}

