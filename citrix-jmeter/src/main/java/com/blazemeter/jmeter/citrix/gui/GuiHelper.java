package com.blazemeter.jmeter.citrix.gui;

import java.awt.GridBagConstraints;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.blazemeter.jmeter.citrix.clauses.Clause;
import com.blazemeter.jmeter.citrix.clauses.Clause.CheckType;
import com.blazemeter.jmeter.citrix.clauses.gui.ClauseBuilderPanel;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

public class GuiHelper {

	private GuiHelper() {
	}

	public static JLabel addLabeledComponent(JComponent component, String resLabel, JComponent parent) {
		JLabel label = new JLabel(CitrixUtils.getResString(resLabel, false));
		label.setLabelFor(component);
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.anchor = GridBagConstraints.BASELINE_LEADING;
		gbcLabel.fill = GridBagConstraints.HORIZONTAL;
		parent.add(label, gbcLabel);
		GridBagConstraints gbcComponent = new GridBagConstraints();
		gbcComponent.anchor = GridBagConstraints.BASELINE;
		gbcComponent.fill = GridBagConstraints.HORIZONTAL;
		gbcComponent.gridwidth = GridBagConstraints.REMAINDER;
		gbcComponent.weightx = 1d;
		parent.add(component, gbcComponent);
		return label;
	}

	public static Clause buildClause(Snapshot snapshot, Set<CheckType> checkTypes) {
		if (snapshot == null) {
			throw new IllegalArgumentException("snapshot must not be null");
		}
		ClauseBuilderPanel panel = new ClauseBuilderPanel();
		panel.setDragAreaVisible(false);
		panel.setImage(snapshot.getScreenshot());
		panel.setFgWindowArea(snapshot.getForegroundWindowArea());
		if (checkTypes != null) {
			panel.setCheckTypes(checkTypes);
		}
		int reponse = JOptionPane.showOptionDialog(null, panel,
				CitrixUtils.getResString("clause_builder_popup_title", false), JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null,
				new String[] { CitrixUtils.getResString("clause_builder_popup_validate", false) }, null);
		return 0 == reponse ? panel.getClause() : null;
	}
}
