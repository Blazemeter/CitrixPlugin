package com.blazemeter.jmeter.citrix.clauses.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import com.blazemeter.jmeter.citrix.clauses.Clause;
import com.blazemeter.jmeter.citrix.clauses.Clause.CheckType;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.gui.SelectionPanel;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.EnumHelper;
import com.blazemeter.jmeter.citrix.utils.EnumHelper.EnumWrapper;

public class ClausePanel extends JPanel {

	private static final long serialVersionUID = -1950474156401033057L;

	private final transient EnumWrapper<CheckType>[] check_types;

	private boolean editable;
	private Clause clause;

	private JComboBox<EnumWrapper<CheckType>> cbbCheckType;
	private JSyntaxTextArea taExpectedValue;
	private SelectionPanel pnlSelection;
	private JCheckBox chbRelative;
	private JTextField tfTimeout;

	private JLabel lblSelection;
	private JLabel lblExpectedValue;
	private JLabel lblTimeout;

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
			cbbCheckType.setSelectedItem(EnumHelper.lookup(check_types, clause.getCheckType()));
			taExpectedValue.setText(clause.getExpectedValue());
			pnlSelection.setSelection(clause.getSelection());
			chbRelative.setSelected(clause.isRelative());
			tfTimeout.setText(Long.toString(clause.getTimeout()));
		} else {
			cbbCheckType.setSelectedItem(check_types[0]);
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

	public ClausePanel(Set<CheckType> acceptedChecks, boolean isNoneAccepted) {
		check_types = EnumHelper.getWrappersAsArray(CheckType.class, acceptedChecks, isNoneAccepted);
		initialize();
		setEditable(true);
	}

	public ClausePanel(boolean isNoneAccepted) {
		this(null, isNoneAccepted);
	}

	private void triggerClauseChanged() {
		ClauseChangedListener[] listeners = listenerList.getListeners(ClauseChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onClauseChanged(new EventObject(this));
		}
	}

	/**
	 * Update clause from GUI
	 */
	public void updateClause() {
		@SuppressWarnings("unchecked")
		final CheckType checkType = ((EnumWrapper<CheckType>) cbbCheckType.getSelectedItem()).getEnumValue();
		if (checkType != null) {
			try {
				clause = new Clause(checkType, taExpectedValue.getText(), pnlSelection.getSelection());
				clause.setRelative(chbRelative.isSelected());
				clause.setTimeout(Long.parseLong(tfTimeout.getText()));
			} catch (NumberFormatException ex) {
				clause = null;
			}
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
			final boolean canEditSnapshot = canEdit && Clause.SNAPSHOT_CHECKTYPES.contains(checkType);

			lblExpectedValue.setEnabled(canEditSnapshot);
			taExpectedValue.setEditable(canEditSnapshot);
			taExpectedValue.setFocusable(canEditSnapshot);

			lblSelection.setEnabled(canEditSnapshot);
			pnlSelection.setEnabled(canEditSnapshot);
			pnlSelection.setEditable(canEditSnapshot);
			pnlSelection.setFocusable(canEditSnapshot);

			chbRelative.setEnabled(canEditSnapshot);
			chbRelative.setFocusable(canEditSnapshot);

			lblTimeout.setEnabled(canEdit);
			tfTimeout.setEditable(canEdit);
			tfTimeout.setFocusable(canEdit);

		}
	}

	private void initialize() {
		setLayout(new GridBagLayout());

		cbbCheckType = new JComboBox<>(check_types);
		cbbCheckType.addItemListener(e -> {
			updateEditable();
			updateClause();
		});
		GuiHelper.addLabeledComponent(cbbCheckType, "clause_panel_type", this);

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

		lblSelection = GuiHelper.addLabeledComponent(pnlArea, "clause_panel_selection", this);

		taExpectedValue = JSyntaxTextArea.getInstance(5, 80);
		taExpectedValue.addFocusListener(new ChangeHandler());
		lblExpectedValue = GuiHelper.addLabeledComponent(JTextScrollPane.getInstance(taExpectedValue),
				"clause_panel_expected_value", this);

		tfTimeout = new JTextField();
		tfTimeout.addFocusListener(new ChangeHandler());
		lblTimeout = GuiHelper.addLabeledComponent(tfTimeout, "clause_panel_timeout", this);

	}

	public void addClauseChangedListener(ClauseChangedListener listener) {
		listenerList.add(ClauseChangedListener.class, listener);
	}

	public void removeClauseChangedListener(ClauseChangedListener listener) {
		listenerList.remove(ClauseChangedListener.class, listener);
	}

	public static interface ClauseChangedListener extends EventListener {
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