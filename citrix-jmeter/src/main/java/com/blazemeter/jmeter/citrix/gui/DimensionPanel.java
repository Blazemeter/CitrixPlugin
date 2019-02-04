package com.blazemeter.jmeter.citrix.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;

public class DimensionPanel extends JPanel {

	private static final long serialVersionUID = -8148435033646222684L;

	private boolean editable = true;
	private Dimension dimension;

	private JTextField tfWidth;
	private JTextField tfHeight;

	private JLabel lblWidth;
	private JLabel lblHeight;
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		lblHeight.setEnabled(enabled);
		lblWidth.setEnabled(enabled);
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;

		tfWidth.setEditable(editable);
		tfWidth.setFocusable(editable);

		tfHeight.setEditable(editable);
		tfHeight.setFocusable(editable);
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		if (dimension != null) {
			tfWidth.setText(Integer.toString(dimension.width));
			tfHeight.setText(Integer.toString(dimension.height));
		} else {
			tfWidth.setText("");
			tfHeight.setText("");
		}
		updateDimension();
	}

	public DimensionPanel() {
		initialize();
	}

	private void triggerDimensionChanged() {
		DimensionChangedListener[] listeners = listenerList.getListeners(DimensionChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onDimensionChanged(new DimensionChangedEvent(this, dimension));
		}
	}

	private void updateDimension() {
		Dimension newDimension;
		try {
			newDimension = new Dimension(Integer.parseInt(tfWidth.getText()), Integer.parseInt(tfHeight.getText()));
		} catch (NumberFormatException ex) {
			newDimension = null;
		}
		if (!Objects.equals(dimension, newDimension)) {
			dimension = newDimension;
			triggerDimensionChanged();
		}
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		
		tfWidth = new JTextField(4);
		tfWidth.addFocusListener(new ChangeHandler());
		lblWidth = GuiHelper.addLabeledComponent(tfWidth, "dimension_panel_width", this);

		tfHeight = new JTextField(4);
		tfHeight.addFocusListener(new ChangeHandler());
		lblHeight = GuiHelper.addLabeledComponent(tfHeight, "dimension_panel_height", this);
	}

	public void addDimensionChangedListener(DimensionChangedListener listener) {
		listenerList.add(DimensionChangedListener.class, listener);
	}

	public void removeDimensionChangedListener(DimensionChangedListener listener) {
		listenerList.remove(DimensionChangedListener.class, listener);
	}

	public static interface DimensionChangedListener extends EventListener {
		void onDimensionChanged(DimensionChangedEvent event);
	}

	public static class DimensionChangedEvent extends EventObject {

		private static final long serialVersionUID = 5183476663277223198L;

		private final Dimension dimension;

		public DimensionChangedEvent(Object source, Dimension dimension) {
			super(source);
			this.dimension = dimension;
		}

		public Dimension getDimension() {
			return dimension;
		}
	}

	private class ChangeHandler implements FocusListener {

		private String oldValue;

		@Override
		public void focusLost(FocusEvent e) {
			if (editable && !oldValue.equals(((JTextComponent) e.getComponent()).getText())) {
				updateDimension();
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			oldValue = ((JTextComponent) e.getComponent()).getText();
		}
	}
}
