/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.blazemeter.jmeter.citrix.sampler.gui;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler;
import com.blazemeter.jmeter.citrix.sampler.InteractionSampler;
import com.blazemeter.jmeter.citrix.sampler.KeySequenceItem;
import com.blazemeter.jmeter.citrix.sampler.MouseSequenceItem;
import com.blazemeter.jmeter.citrix.sampler.SamplerType;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.EnumHelper;
import com.blazemeter.jmeter.citrix.utils.EnumHelper.EnumWrapper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;

/**
 * GUI for CitrixSampler class.
 */
public class InteractionSamplerGUI extends CitrixSamplerGUI { // NOSONAR

  private static final long serialVersionUID = -5280931051718626301L;
  private static final int NUMBER_OF_VISIBLE_ROWS = 7;
  private static final EnumWrapper<SamplerType>[] SAMPLER_TYPES =
      EnumHelper.getWrappersAsArray(SamplerType.class,
          false);
  private static final EnumWrapper<KeyState>[] KEY_STATES =
      EnumHelper.getWrappersAsArray(KeyState.class, false);
  private static final EnumWrapper<MouseAction>[] MOUSE_ACTIONS =
      EnumHelper.getWrappersAsArray(MouseAction.class,
          false);

  private JComboBox<EnumWrapper<SamplerType>> cbbSamplerType;
  private JCheckBox chbRelative;
  private JTabbedPane tbpDescription;

  private JPanel pnlTextTab;
  private JSyntaxTextArea taInputText;

  private JPanel pnlKeySequenceTab;
  private KeySequenceModel keySequenceModel;
  private JTable tblKeySequence;

  private JPanel pnlMouseClickTab;
  private JTextField tfMouseX;
  private JTextField tfMouseY;
  private JCheckBox chbDoubleClick;
  private MouseButtonsPanel pnlMouseButtons;
  private ModifiersPanel pnlModifiers;

  private JPanel pnlMouseSequenceTab;
  private MouseSequenceModel mouseSequenceModel;
  private JTable tblMouseSequence;

  public InteractionSamplerGUI() {
    super();
    updateActiveTab();
  }

  @Override
  public String getStaticLabel() {
    return CitrixUtils.getResString("interaction_sampler_title", false);
  }

  private SamplerType getCurrentSamplerType() {
    SamplerType result = null;
    @SuppressWarnings("unchecked")
    final EnumWrapper<SamplerType> wrapper =
        (EnumWrapper<SamplerType>) cbbSamplerType.getSelectedItem();
    if (wrapper != null) {
      result = wrapper.getEnumValue();
    }
    return result;
  }

  @Override
  protected CitrixBaseSampler createCitrixSampler() {
    return new InteractionSampler();
  }

  @Override
  protected void stopEditing() {
    GuiUtils.stopTableEditing(tblKeySequence);
    GuiUtils.stopTableEditing(tblMouseSequence);
  }

  @Override
  protected void updateSampler(CitrixBaseSampler sampler) {
    InteractionSampler interactionSampler = (InteractionSampler) sampler;
    interactionSampler.setSamplerType(getCurrentSamplerType());
    interactionSampler.setRelative(chbRelative.isSelected());

    // Text
    interactionSampler.setInputText(taInputText.getText());

    // Key sequence
    List<KeySequenceItem> keySequence = interactionSampler.getKeySequence();
    keySequence.clear();
    keySequence.addAll(keySequenceModel.getItems());

    // Click
    interactionSampler.setDoubleClick(chbDoubleClick.isSelected());
    interactionSampler.setMouseX(tfMouseX.getText());
    interactionSampler.setMouseY(tfMouseY.getText());

    Set<MouseButton> buttons = interactionSampler.getMouseButtons();
    buttons.clear();
    buttons.addAll(pnlMouseButtons.getButtons());

    Set<Modifier> modifiers = interactionSampler.getModifiers();
    modifiers.clear();
    modifiers.addAll(pnlModifiers.getModifiers());

    // Mouse sequence
    List<MouseSequenceItem> mouseSequence = interactionSampler.getMouseSequence();
    mouseSequence.clear();
    mouseSequence.addAll(mouseSequenceModel.getItems());
  }

  @Override
  protected void updateUI(CitrixBaseSampler sampler) {
    InteractionSampler interactionSampler = (InteractionSampler) sampler;
    cbbSamplerType
        .setSelectedItem(EnumHelper.lookup(SAMPLER_TYPES, interactionSampler.getSamplerType()));
    chbRelative.setSelected(interactionSampler.isRelative());

    // Text
    taInputText.setText(interactionSampler.getInputText());

    // Key sequence
    keySequenceModel.setItems(interactionSampler.getKeySequence());

    // Click
    chbDoubleClick.setSelected(interactionSampler.isDoubleClick());
    tfMouseX.setText(interactionSampler.getMouseXAsString());
    tfMouseY.setText(interactionSampler.getMouseYAsString());
    pnlMouseButtons.setButtons(interactionSampler.getMouseButtons());
    pnlModifiers.setModifiers(interactionSampler.getModifiers());

    // Mouse sequence
    mouseSequenceModel.setItems(interactionSampler.getMouseSequence());
  }

  @Override
  protected void clearUI() {
    cbbSamplerType.setSelectedItem(0);
    chbRelative.setSelected(false);
    taInputText.setText("");
    keySequenceModel.setItems(null);
    chbDoubleClick.setSelected(false);
    tfMouseX.setText("");
    tfMouseY.setText("");
    pnlMouseButtons.setButtons(null);
    mouseSequenceModel.setItems(null);
  }

  private void updateActiveTab() {
    JPanel selectedTab = null;
    SamplerType type = getCurrentSamplerType();
    boolean relativeVisible = false;
    if (type != null) {
      switch (type) {
        case TEXT:
          selectedTab = pnlTextTab;
          break;
        case KEY_SEQUENCE:
          selectedTab = pnlKeySequenceTab;
          break;
        case MOUSE_CLICK:
          selectedTab = pnlMouseClickTab;
          relativeVisible = true;
          break;
        case MOUSE_SEQUENCE:
          selectedTab = pnlMouseSequenceTab;
          relativeVisible = true;
          break;
        default:
          // NOOP
      }
    }
    if (selectedTab != null) {
      tbpDescription.setSelectedComponent(selectedTab);
    }
    int count = tbpDescription.getTabCount();
    for (int index = 0; index < count; index++) {
      tbpDescription.setEnabledAt(index, tbpDescription.getComponentAt(index) == selectedTab);
    }

    chbRelative.setVisible(relativeVisible);
  }

  protected JPanel createMainPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        CitrixUtils.getResString("interaction_sampler_informations", false)));

    cbbSamplerType = new JComboBox<>(SAMPLER_TYPES);
    cbbSamplerType.addItemListener(e -> updateActiveTab());
    GuiHelper.addLabeledComponent(cbbSamplerType, "interaction_sampler_type", panel);

    chbRelative = new JCheckBox(CitrixUtils.getResString("interaction_sampler_relative", false));
    GridBagConstraints gbcRelative = new GridBagConstraints();
    gbcRelative.fill = GridBagConstraints.HORIZONTAL;
    gbcRelative.gridwidth = GridBagConstraints.REMAINDER;
    gbcRelative.weightx = 1d;
    panel.add(chbRelative, gbcRelative);

    pnlTextTab = createInputTextTab();
    pnlKeySequenceTab = createKeySequenceTab();
    pnlMouseClickTab = createMouseClickTab();
    pnlMouseSequenceTab = createMouseSequenceTab();

    tbpDescription = new JTabbedPane(JTabbedPane.TOP);
    tbpDescription
        .addTab(EnumHelper.lookup(SAMPLER_TYPES, SamplerType.TEXT).getLabel(), pnlTextTab);
    tbpDescription.addTab(EnumHelper.lookup(SAMPLER_TYPES, SamplerType.KEY_SEQUENCE).getLabel(),
        pnlKeySequenceTab);
    tbpDescription.addTab(EnumHelper.lookup(SAMPLER_TYPES, SamplerType.MOUSE_CLICK).getLabel(),
        pnlMouseClickTab);
    tbpDescription.addTab(EnumHelper.lookup(SAMPLER_TYPES, SamplerType.MOUSE_SEQUENCE).getLabel(),
        pnlMouseSequenceTab);
    makeScrollPane(tbpDescription);
    GridBagConstraints gbcDescription = new GridBagConstraints();
    gbcDescription.fill = GridBagConstraints.HORIZONTAL;
    gbcDescription.gridwidth = GridBagConstraints.REMAINDER;
    gbcDescription.weightx = 1d;
    panel.add(tbpDescription, gbcDescription);

    return panel;
  }

  private JPanel createInputTextTab() {
    JPanel panel = new JPanel(new BorderLayout());
    taInputText = JSyntaxTextArea.getInstance(13, 120);
    taInputText.setLanguage("text");
    panel.add(JTextScrollPane.getInstance(taInputText, true), BorderLayout.NORTH);
    return panel;
  }

  private JPanel createKeySequenceTab() {
    JPanel panel = new JPanel(new BorderLayout());
    keySequenceModel = new KeySequenceModel();
    tblKeySequence = new JTable(keySequenceModel);
    tblKeySequence.setRowSelectionAllowed(true);
    tblKeySequence.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tblKeySequence.setDefaultRenderer(KeyState.class, new KeyStateCellRenderer());
    tblKeySequence.setDefaultEditor(KeyState.class, new KeyStateCellEditor());
    tblKeySequence.getColumnModel().getColumn(0).setMaxWidth(50);
    JMeterUtils.applyHiDPI(tblKeySequence);
    tblKeySequence.setPreferredScrollableViewportSize(new Dimension(
        tblKeySequence.getPreferredSize().width,
        tblKeySequence.getRowHeight() * NUMBER_OF_VISIBLE_ROWS));
    panel.add(new JScrollPane(tblKeySequence), BorderLayout.CENTER);
    JPanel pnlButtons = new JPanel();

    JButton btAddItem =
        new JButton(CitrixUtils.getResString("interaction_sampler_add_key_sequence_item", false));
    btAddItem.addActionListener(e -> {
      GuiUtils.stopTableEditing(tblKeySequence);
      keySequenceModel.addItem(new KeySequenceItem(KeyState.KEY_DOWN, 0, 0));
    });
    pnlButtons.add(btAddItem);

    JButton btRemoveSelectedItems = new JButton(
        CitrixUtils.getResString("interaction_sampler_remove_selected_key_sequence_items", false));
    btRemoveSelectedItems.setEnabled(false);
    btRemoveSelectedItems.addActionListener(e -> {
      GuiUtils.stopTableEditing(tblKeySequence);
      int count = tblKeySequence.getSelectedRowCount();
      int[] indexes = tblKeySequence.getSelectedRows();
      for (int i = count - 1; i >= 0; i--) {
        keySequenceModel.removeItem(indexes[i]);
      }
    });
    pnlButtons.add(btRemoveSelectedItems);

    tblKeySequence.getSelectionModel().addListSelectionListener(
        e -> btRemoveSelectedItems.setEnabled(tblKeySequence.getSelectedRowCount() > 0));

    panel.add(pnlButtons, BorderLayout.NORTH);
    return panel;
  }

  private JPanel createMouseClickTab() {
    JPanel panel = new JPanel(new GridBagLayout());

    chbDoubleClick = new JCheckBox();
    chbDoubleClick.addActionListener(e -> {
      if (chbDoubleClick.isSelected()) {
        pnlMouseButtons.setButtons(EnumSet.of(MouseButton.LEFT));
      }
    });
    GuiHelper.addLabeledComponent(chbDoubleClick, "interaction_sampler_double_click", panel);

    tfMouseX = new JTextField(4);
    GuiHelper.addLabeledComponent(tfMouseX, "interaction_sampler_x", panel);

    tfMouseY = new JTextField(4);
    GuiHelper.addLabeledComponent(tfMouseY, "interaction_sampler_y", panel);

    pnlMouseButtons = new MouseButtonsPanel();
    GuiHelper.addLabeledComponent(pnlMouseButtons, "interaction_sampler_mouse_buttons", panel);

    pnlModifiers = new ModifiersPanel();
    GuiHelper.addLabeledComponent(pnlModifiers, "interaction_sampler_modifiers", panel);

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(panel, BorderLayout.NORTH);
    return wrapper;
  }

  private JPanel createMouseSequenceTab() {
    JPanel panel = new JPanel(new BorderLayout());
    mouseSequenceModel = new MouseSequenceModel();
    tblMouseSequence = new JTable(mouseSequenceModel);
    tblMouseSequence.setRowSelectionAllowed(true);
    tblMouseSequence.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tblMouseSequence.setDefaultRenderer(MouseAction.class, new MouseActionCellRenderer());
    tblMouseSequence.setDefaultEditor(MouseAction.class, new MouseActionCellEditor());
    tblMouseSequence.getColumnModel().getColumn(0).setMaxWidth(50);
    tblMouseSequence.setRowHeight(25);
    JMeterUtils.applyHiDPI(tblMouseSequence);
    tblMouseSequence.setPreferredScrollableViewportSize(new Dimension(
        tblMouseSequence.getPreferredSize().width,
        tblMouseSequence.getRowHeight() * NUMBER_OF_VISIBLE_ROWS));

    TableColumn buttonsColumn = tblMouseSequence.getColumnModel().getColumn(4);
    buttonsColumn.setCellRenderer(new MouseButtonsCellRenderer());
    buttonsColumn.setCellEditor(new MouseButtonsCellEditor());
    buttonsColumn.setMinWidth(220);

    TableColumn modifiersColumn = tblMouseSequence.getColumnModel().getColumn(5);
    modifiersColumn.setCellRenderer(new ModifiersCellRenderer());
    modifiersColumn.setCellEditor(new ModifiersCellEditor());
    modifiersColumn.setMinWidth(300);

    panel.add(new JScrollPane(tblMouseSequence), BorderLayout.CENTER);

    JPanel pnlButtons = new JPanel();

    JButton btAddItem =
        new JButton(CitrixUtils.getResString("interaction_sampler_add_mouse_sequence_item", false));
    btAddItem.addActionListener(e -> {
      GuiUtils.stopTableEditing(tblMouseSequence);
      mouseSequenceModel.addItem(new MouseSequenceItem(MouseAction.MOVE, 0, 0, null, null, 0));
    });
    pnlButtons.add(btAddItem);

    JButton btRemoveSelectedItems = new JButton(
        CitrixUtils
            .getResString("interaction_sampler_remove_selected_mouse_sequence_items", false));
    btRemoveSelectedItems.setEnabled(false);
    btRemoveSelectedItems.addActionListener(e -> {
      GuiUtils.stopTableEditing(tblMouseSequence);
      int count = tblMouseSequence.getSelectedRowCount();
      int[] indexes = tblMouseSequence.getSelectedRows();
      for (int i = count - 1; i >= 0; i--) {
        mouseSequenceModel.removeItem(indexes[i]);
      }
    });
    pnlButtons.add(btRemoveSelectedItems);

    tblMouseSequence.getSelectionModel().addListSelectionListener(
        e -> btRemoveSelectedItems.setEnabled(tblMouseSequence.getSelectedRowCount() > 0));

    panel.add(pnlButtons, BorderLayout.NORTH);
    return panel;
  }

  private static class KeyStateCellRenderer extends DefaultTableCellRenderer { // NOSONAR
    private static final long serialVersionUID = -8490520756137639371L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
      KeyState state = (KeyState) value;
      if (state != null) {
        setText(EnumHelper.lookup(KEY_STATES, state).getLabel());
      } else {
        setText("");
      }
      return this;
    }
  }

  private static class MouseActionCellRenderer extends DefaultTableCellRenderer { // NOSONAR

    private static final long serialVersionUID = 2045654735310318198L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
      MouseAction action = (MouseAction) value;
      if (action != null) {
        setText(EnumHelper.lookup(MOUSE_ACTIONS, action).getLabel());
      } else {
        setText("");
      }
      return this;
    }
  }

  private static class MouseButtonsCellRenderer extends MouseButtonsPanel
      implements TableCellRenderer { // NOSONAR

    private static final long serialVersionUID = -317680945782070176L;

    MouseButtonsCellRenderer() {
      setEditable(false);
    }

    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus,
                                                   int rowIndex, int vColIndex) {
      setButtons((Set<MouseButton>) value);
      return this;
    }
  }

  private static class ModifiersCellRenderer extends ModifiersPanel
      implements TableCellRenderer { // NOSONAR

    private static final long serialVersionUID = 8778528293533882315L;

    ModifiersCellRenderer() {
      setEditable(false);
    }

    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus,
                                                   int rowIndex, int vColIndex) {
      setModifiers((Set<Modifier>) value);
      return this;
    }
  }

  private static class KeyStateCellEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 1843308368035061308L;

    KeyStateCellEditor() {
      super(new JComboBox<>(KEY_STATES));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getCellEditorValue() {
      return ((EnumWrapper<KeyState>) super.getCellEditorValue()).getEnumValue();
    }
  }

  private static class MouseActionCellEditor extends DefaultCellEditor {

    private static final long serialVersionUID = 4166405053211083639L;

    MouseActionCellEditor() {
      super(new JComboBox<>(MOUSE_ACTIONS));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getCellEditorValue() {
      return ((EnumWrapper<MouseAction>) super.getCellEditorValue()).getEnumValue();
    }
  }

  private static class MouseButtonsCellEditor extends AbstractCellEditor
      implements TableCellEditor {

    private static final long serialVersionUID = -2637438367880728341L;

    final MouseButtonsPanel pnlButtons = new MouseButtonsPanel();

    @SuppressWarnings("unchecked")
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int rowIndex,
                                                 int vColIndex) {
      pnlButtons.setButtons((Set<MouseButton>) value);
      return pnlButtons;
    }

    public Object getCellEditorValue() {
      return pnlButtons.getButtons();
    }
  }

  private static class ModifiersCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 2540989152204383675L;

    final ModifiersPanel pnlModifiers = new ModifiersPanel();

    @SuppressWarnings("unchecked")
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int rowIndex,
                                                 int vColIndex) {
      pnlModifiers.setModifiers((Set<Modifier>) value);
      return pnlModifiers;
    }

    public Object getCellEditorValue() {
      return pnlModifiers.getModifiers();
    }
  }
}
