package com.blazemeter.jmeter.citrix.clause.gui;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.gui.SelectionPanel;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.EnumHelper;
import com.blazemeter.jmeter.citrix.utils.EnumHelper.EnumWrapper;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;

public class ClausePanel extends JPanel {

  private static final long serialVersionUID = -1950474156401033057L;

  private final transient EnumWrapper<CheckType>[] checkTypeWrappers;

  private boolean editable;
  private Clause clause;

  private JComboBox<EnumWrapper<CheckType>> cbbCheckType;
  private JSyntaxTextArea taExpectedValue;
  private JCheckBox chbUseRegex;
  private SelectionPanel pnlSelection;
  private JCheckBox chbRelative;
  private JTextField tfTimeout;

  private JLabel lblSelection;
  private JLabel lblExpectedValue;
  private JLabel lblTimeout;

  public ClausePanel(Set<CheckType> acceptedChecks, boolean isNoneAccepted) {
    checkTypeWrappers =
        EnumHelper.getWrappersAsArray(CheckType.class, acceptedChecks, isNoneAccepted);
    initialize();
    setEditable(true);
  }

  public ClausePanel(boolean isNoneAccepted) {
    this(null, isNoneAccepted);
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
    updateEditable();
  }

  public Clause getClause() {
    return clause;
  }

  public void setClause(Clause clause) {
    if (clause != null) {
      cbbCheckType.setSelectedItem(EnumHelper.lookup(checkTypeWrappers, clause.getCheckType()));
      chbUseRegex.setSelected(clause.isUsingRegex());
      taExpectedValue.setText(clause.getExpectedValue());
      pnlSelection.setSelection(clause.getSelection());
      chbRelative.setSelected(clause.isRelative());
      tfTimeout.setText(Long.toString(clause.getTimeout()));
    } else {
      cbbCheckType.setSelectedItem(checkTypeWrappers[0]);
      chbUseRegex.setSelected(false);
      taExpectedValue.setText("");
      pnlSelection.setSelection(null);
      chbRelative.setSelected(false);
      tfTimeout.setText("");
    }
    updateClause();
  }

  public boolean isTimeoutVisible() {
    return tfTimeout.isVisible();
  }

  public void setTimeoutVisible(boolean visible) {
    lblTimeout.setVisible(visible);
    tfTimeout.setVisible(visible);
  }

  private void triggerClauseChanged() {
    ClauseChangedListener[] listeners = listenerList.getListeners(ClauseChangedListener.class);
    for (ClauseChangedListener listener : listeners) {
      listener.onClauseChanged(new EventObject(this));
    }
  }

  /**
   * Update clause from GUI.
   */
  public void updateClause() {
    @SuppressWarnings("unchecked")
    final CheckType checkType =
        ((EnumWrapper<CheckType>) cbbCheckType.getSelectedItem()).getEnumValue();
    if (checkType != null) {
      pnlSelection.updateSelection();
      long timeout;
      try {
        timeout = Long.parseLong(tfTimeout.getText());
      } catch (NumberFormatException ex) {
        timeout = ClauseHelper.CLAUSE_TIMEOUT;
      }
      clause = new Clause(checkType, taExpectedValue.getText(), chbUseRegex.isSelected(),
          pnlSelection.getSelection());
      clause.setRelative(chbRelative.isSelected());
      clause.setTimeout(timeout);
    } else {
      clause = null;
    }
    triggerClauseChanged();
  }

  @SuppressWarnings("unchecked") // for the first line after if
  private void updateEditable() {
    Object item = cbbCheckType.getSelectedItem();
    if (item != null) {
      EnumWrapper<CheckType> type = (EnumWrapper<CheckType>) item;
      final CheckType checkType = type.getEnumValue();
      final boolean canEdit = editable && checkType != null;

      lblTimeout.setEnabled(canEdit);
      tfTimeout.setEditable(canEdit);
      tfTimeout.setFocusable(canEdit);

      final boolean canEditExpectedValue = canEdit && checkType.isUsingExpectedValue();
      chbUseRegex.setEnabled(canEditExpectedValue);
      chbUseRegex.setFocusable(canEditExpectedValue);
      lblExpectedValue.setEnabled(canEditExpectedValue);
      taExpectedValue.setEditable(canEditExpectedValue);
      taExpectedValue.setFocusable(canEditExpectedValue);

      final boolean canEditSelection = canEdit && checkType.isSupportingSelection();
      lblSelection.setEnabled(canEditSelection);
      pnlSelection.setEnabled(canEditSelection);
      pnlSelection.setEditable(canEditSelection);
      pnlSelection.setFocusable(canEditSelection);

      chbRelative.setEnabled(canEditSelection);
      chbRelative.setFocusable(canEditSelection);

    }
  }

  private void initialize() {
    setLayout(new GridBagLayout());
    Insets insets = new Insets(2, 0, 2, 0);

    cbbCheckType = new JComboBox<>(checkTypeWrappers);
    cbbCheckType.addItemListener(e -> {
      updateEditable();
      updateClause();
    });
    JLabel lblCheckType =
        GuiHelper.addLabeledComponent(cbbCheckType, "clause_panel_type", this, insets);
    if (checkTypeWrappers.length <= 1) {
      cbbCheckType.setVisible(false);
      lblCheckType.setVisible(false);
    }
    JPanel pnlArea = new JPanel();
    pnlArea.setLayout(new GridBagLayout());

    chbRelative = new JCheckBox(CitrixUtils.getResString("clause_panel_relative", false));
    chbRelative.addActionListener(e -> updateClause());
    GridBagConstraints gbcRelative = new GridBagConstraints();
    gbcRelative.anchor = GridBagConstraints.LINE_START;
    gbcRelative.gridwidth = GridBagConstraints.REMAINDER;
    pnlArea.add(chbRelative, gbcRelative);

    pnlSelection = new SelectionPanel();
    pnlSelection.addSelectionChangedListener(e -> updateClause());
    GridBagConstraints gbcSelection = new GridBagConstraints();
    gbcSelection.anchor = GridBagConstraints.LINE_START;
    gbcSelection.fill = GridBagConstraints.HORIZONTAL;
    gbcSelection.gridwidth = GridBagConstraints.REMAINDER;
    gbcSelection.weightx = 1d;
    pnlArea.add(pnlSelection, gbcSelection);

    lblSelection = GuiHelper.addLabeledComponent(pnlArea, "clause_panel_selection", this, insets);

    JPanel pnlExpectedValue = new JPanel();
    pnlExpectedValue.setLayout(new GridBagLayout());

    chbUseRegex = new JCheckBox(CitrixUtils.getResString("clause_panel_use_regex", false));
    chbUseRegex.addActionListener(e -> updateClause());
    GridBagConstraints gbcUseRegex = new GridBagConstraints();
    gbcUseRegex.anchor = GridBagConstraints.LINE_START;
    gbcUseRegex.gridwidth = GridBagConstraints.REMAINDER;
    pnlExpectedValue.add(chbUseRegex, gbcRelative);

    taExpectedValue = JSyntaxTextArea.getInstance(5, 80);
    taExpectedValue.setLanguage("text");
    taExpectedValue.addFocusListener(new ChangeHandler());
    GridBagConstraints gbcExpectedValue = new GridBagConstraints();
    gbcExpectedValue.anchor = GridBagConstraints.LINE_START;
    gbcExpectedValue.fill = GridBagConstraints.HORIZONTAL;
    gbcExpectedValue.gridwidth = GridBagConstraints.REMAINDER;
    gbcExpectedValue.weightx = 1d;
    pnlExpectedValue.add(JTextScrollPane.getInstance(taExpectedValue), gbcExpectedValue);

    lblExpectedValue = GuiHelper
        .addLabeledComponent(pnlExpectedValue, "clause_panel_expected_value", this, insets);

    tfTimeout = new JTextField();
    tfTimeout.addFocusListener(new ChangeHandler());
    lblTimeout = GuiHelper.addLabeledComponent(tfTimeout, "clause_panel_timeout", this, insets);

  }

  public void addClauseChangedListener(ClauseChangedListener listener) {
    listenerList.add(ClauseChangedListener.class, listener);
  }

  public void removeClauseChangedListener(ClauseChangedListener listener) {
    listenerList.remove(ClauseChangedListener.class, listener);
  }

  public interface ClauseChangedListener extends EventListener {
    void onClauseChanged(EventObject event);
  }

  private class ChangeHandler implements FocusListener {

    private String oldValue;

    @Override
    public void focusLost(FocusEvent e) {
      if (editable && !oldValue.equals(((JTextComponent) e.getComponent()).getText())) {
        updateClause();
      }
    }

    @Override
    public void focusGained(FocusEvent e) {
      oldValue = ((JTextComponent) e.getComponent()).getText();
    }
  }
}
