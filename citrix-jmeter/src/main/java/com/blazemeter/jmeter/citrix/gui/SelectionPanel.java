package com.blazemeter.jmeter.citrix.gui;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;

public class SelectionPanel extends JPanel implements SelectionChangedSubject {

	private static final long serialVersionUID = -4081910650471400803L;

	private final transient SelectionChangedManager selectionChangedManager = new SelectionChangedManager(listenerList);
	private boolean editable;
	private Rectangle selection;

	private PositionPanel pnlPosition;
	private DimensionPanel pnlDimension;

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		pnlDimension.setEnabled(enabled);
		pnlPosition.setEnabled(enabled);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;

		pnlPosition.setEditable(editable);
		pnlDimension.setEditable(editable);
	}

	public Rectangle getSelection() {
		return selection;
	}

	public void setSelection(Rectangle selection) {
		if (selection != null) {
			pnlPosition.setPosition(selection.getLocation());
			pnlDimension.setDimension(selection.getSize());
		} else {
			pnlPosition.setPosition(null);
			pnlDimension.setDimension(null);
		}
		updateSelection();
	}

	public SelectionPanel() {
		initialize();
	}

	private void updateSelection() {
		Point position = pnlPosition.getPosition();
		Dimension dimension = pnlDimension.getDimension();
		Rectangle newSelection;
		if (position != null && dimension != null) {
			newSelection = new Rectangle(position.x, position.y, dimension.width, dimension.height);
		} else {
			newSelection = null;
		}
		if (!Objects.equals(selection, newSelection)) {
			selection = newSelection;
			selectionChangedManager.trigger(selection);
		}
	}

	private void initialize() {
		setLayout(new GridBagLayout());

		pnlPosition = new PositionPanel();
		pnlPosition.addPositionChangedListener(e -> updateSelection());
		GridBagConstraints gbcPosition = new GridBagConstraints();
		gbcPosition.fill = GridBagConstraints.HORIZONTAL;
		gbcPosition.weightx = 1d;
		add(pnlPosition, gbcPosition);

		pnlDimension = new DimensionPanel();
		pnlDimension.addDimensionChangedListener(e -> updateSelection());
		GridBagConstraints gbcDimension = new GridBagConstraints();
		gbcDimension.fill = GridBagConstraints.HORIZONTAL;
		gbcDimension.weightx = 1d;
		add(pnlDimension, gbcDimension);
	}

	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		selectionChangedManager.addSelectionChangedListener(listener);
	}

	@Override
	public void removeSelectionChangedListener(SelectionChangedListener listener) {
		selectionChangedManager.removeSelectionChangedListener(listener);
	}

}
