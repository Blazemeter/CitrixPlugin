package com.blazemeter.jmeter.citrix.sampler.gui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

import java.awt.FlowLayout;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

public class ModifiersPanel extends JPanel {

	private static final long serialVersionUID = 319333827306397707L;
	
	private boolean editable;
	private Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

	private JCheckBox chbShift;
	private JCheckBox chbControl;
	private JCheckBox chbAlt;
	private JCheckBox chbExtended;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;

		chbShift.setEnabled(editable);
		chbShift.setFocusable(editable);

		chbControl.setEnabled(editable);
		chbControl.setFocusable(editable);

		chbAlt.setEnabled(editable);
		chbAlt.setFocusable(editable);

		chbExtended.setEnabled(editable);
		chbExtended.setFocusable(editable);
	}

	public Set<Modifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(Set<Modifier> modifiers) {
		if (modifiers != null) {
			chbShift.setSelected(modifiers.contains(Modifier.SHIFT));
			chbControl.setSelected(modifiers.contains(Modifier.CONTROL));
			chbAlt.setSelected(modifiers.contains(Modifier.ALT));
			chbExtended.setSelected(modifiers.contains(Modifier.EXTENDED));
		} else {
			chbShift.setSelected(false);
			chbControl.setSelected(false);
			chbAlt.setSelected(false);
			chbExtended.setSelected(false);
		}
		updateModifiers();
	}

	public ModifiersPanel() {
		initialize();
	}

	private void triggerModifiersChanged() {
		ModifiersChangedListener[] listeners = listenerList.getListeners(ModifiersChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onModifiersChanged(new ModifiersChangedEvent(this, modifiers));
		}
	}

	private void updateModifiers() {
		Set<Modifier> newButtons = EnumSet.noneOf(Modifier.class);
		if (chbShift.isSelected()) {
			newButtons.add(Modifier.SHIFT);
		}
		if (chbControl.isSelected()) {
			newButtons.add(Modifier.CONTROL);
		}
		if (chbAlt.isSelected()) {
			newButtons.add(Modifier.ALT);
		}
		if (chbExtended.isSelected()) {
			newButtons.add(Modifier.EXTENDED);
		}
		if (!modifiers.equals(newButtons)) {
			modifiers = newButtons;
			triggerModifiersChanged();
		}
	}

	private void initialize() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		
		chbShift = new JCheckBox(CitrixUtils.getResString("modifiers_panel_shift", false));
		chbShift.addActionListener(e -> updateModifiers());
		add(chbShift);

		chbControl = new JCheckBox(CitrixUtils.getResString("modifiers_panel_control", false));
		chbControl.addActionListener(e -> updateModifiers());
		add(chbControl);

		chbAlt = new JCheckBox(CitrixUtils.getResString("modifiers_panel_alt", false));
		chbAlt.addActionListener(e -> updateModifiers());
		add(chbAlt);
		
		chbExtended = new JCheckBox(CitrixUtils.getResString("modifiers_panel_extended", false));
		chbExtended.addActionListener(e -> updateModifiers());
		add(chbExtended);
	}

	public void addModifiersChangedListener(ModifiersChangedListener listener) {
		listenerList.add(ModifiersChangedListener.class, listener);
	}

	public void removeModifiersChangedListener(ModifiersChangedListener listener) {
		listenerList.remove(ModifiersChangedListener.class, listener);
	}

	public static interface ModifiersChangedListener extends EventListener {
		void onModifiersChanged(ModifiersChangedEvent event);
	}

	public static class ModifiersChangedEvent extends EventObject {

		private static final long serialVersionUID = -8343786223902050611L;
		
		private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

		public ModifiersChangedEvent(Object source, Set<Modifier> modifiers) {
			super(source);
			if (modifiers != null) {
				this.modifiers.addAll(modifiers);
			}
		}

		public Set<Modifier> getModifiers() {
			return modifiers;
		}
	}
}
