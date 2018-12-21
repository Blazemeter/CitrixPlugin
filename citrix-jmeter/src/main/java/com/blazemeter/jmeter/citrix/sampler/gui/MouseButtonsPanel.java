package com.blazemeter.jmeter.citrix.sampler.gui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

import java.awt.FlowLayout;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

public class MouseButtonsPanel extends JPanel {

	private static final long serialVersionUID = 5459475634154464148L;

	private boolean editable;
	private Set<MouseButton> buttons = EnumSet.noneOf(MouseButton.class);

	private JCheckBox chbLeft;
	private JCheckBox chbRight;
	private JCheckBox chbMiddle;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;

		chbLeft.setEnabled(editable);
		chbLeft.setFocusable(editable);

		chbRight.setEnabled(editable);
		chbRight.setFocusable(editable);

		chbMiddle.setEnabled(editable);
		chbMiddle.setFocusable(editable);
	}

	public Set<MouseButton> getButtons() {
		return buttons;
	}

	public void setButtons(Set<MouseButton> buttons) {
		if (buttons != null) {
			chbLeft.setSelected(buttons.contains(MouseButton.LEFT));
			chbRight.setSelected(buttons.contains(MouseButton.RIGHT));
			chbMiddle.setSelected(buttons.contains(MouseButton.MIDDLE));
		} else {
			chbLeft.setSelected(false);
			chbRight.setSelected(false);
			chbMiddle.setSelected(false);
		}
		updateButtons();
	}

	public MouseButtonsPanel() {
		initialize();
	}

	private void triggerButtonsChanged() {
		ButtonsChangedListener[] listeners = listenerList.getListeners(ButtonsChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onButtonsChanged(new ButtonsChangedEvent(this, buttons));
		}
	}

	private void updateButtons() {
		Set<MouseButton> newButtons = EnumSet.noneOf(MouseButton.class);
		if (chbLeft.isSelected()) {
			newButtons.add(MouseButton.LEFT);
		}
		if (chbRight.isSelected()) {
			newButtons.add(MouseButton.RIGHT);
		}
		if (chbMiddle.isSelected()) {
			newButtons.add(MouseButton.MIDDLE);
		}
		if (!buttons.equals(newButtons)) {
			buttons = newButtons;
			triggerButtonsChanged();
		}
	}

	private void initialize() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		
		chbLeft = new JCheckBox(CitrixUtils.getResString("mouse_buttons_panel_left", false));
		chbLeft.addActionListener(e -> updateButtons());
		add(chbLeft);

		chbRight = new JCheckBox(CitrixUtils.getResString("mouse_buttons_panel_right", false));
		chbRight.addActionListener(e -> updateButtons());
		add(chbRight);

		chbMiddle = new JCheckBox(CitrixUtils.getResString("mouse_buttons_panel_middle", false));
		chbMiddle.addActionListener(e -> updateButtons());
		add(chbMiddle);
	}

	public void addButtonsChangedListener(ButtonsChangedListener listener) {
		listenerList.add(ButtonsChangedListener.class, listener);
	}

	public void removeButtonsChangedListener(ButtonsChangedListener listener) {
		listenerList.remove(ButtonsChangedListener.class, listener);
	}

	public static interface ButtonsChangedListener extends EventListener {
		void onButtonsChanged(ButtonsChangedEvent event);
	}

	public static class ButtonsChangedEvent extends EventObject {

		private static final long serialVersionUID = 2816518640221724621L;

		private final Set<MouseButton> buttons = EnumSet.noneOf(MouseButton.class);

		public ButtonsChangedEvent(Object source, Set<MouseButton> buttons) {
			super(source);
			if (buttons != null) {
				this.buttons.addAll(buttons);
			}
		}

		public Set<MouseButton> getButtons() {
			return buttons;
		}
	}
}
