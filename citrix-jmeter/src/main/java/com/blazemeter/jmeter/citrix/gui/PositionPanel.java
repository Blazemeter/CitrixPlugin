package com.blazemeter.jmeter.citrix.gui;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class PositionPanel extends JPanel {

  private static final long serialVersionUID = -4081910650471400803L;

  private boolean editable = true;
  private Point position;

  private JTextField tfXPosition;
  private JTextField tfYPosition;

  private JLabel lblXPosition;
  private JLabel lblYPosition;

  public PositionPanel() {
    initialize();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    lblXPosition.setEnabled(enabled);
    lblYPosition.setEnabled(enabled);
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;

    tfXPosition.setEditable(editable);
    tfXPosition.setFocusable(editable);

    tfYPosition.setEditable(editable);
    tfYPosition.setFocusable(editable);
  }

  public Point getPosition() {
    return position;
  }

  public void setPosition(Point position) {
    if (position != null) {
      tfXPosition.setText(Integer.toString(position.x));
      tfYPosition.setText(Integer.toString(position.y));
    } else {
      tfXPosition.setText("");
      tfYPosition.setText("");
    }
    updatePosition();
  }

  private void triggerPositionChanged() {
    PositionChangedListener[] listeners = listenerList.getListeners(PositionChangedListener.class);
    for (PositionChangedListener listener : listeners) {
      listener.onPositionChanged(new PositionChangedEvent(this, position));
    }
  }

  private void updatePosition() {
    Point newPosition;
    try {
      newPosition = new Point(Integer.parseInt(tfXPosition.getText()),
          Integer.parseInt(tfYPosition.getText()));
    } catch (NumberFormatException ex) {
      newPosition = null;
    }
    if (!Objects.equals(position, newPosition)) {
      position = newPosition;
      triggerPositionChanged();
    }
  }

  private void initialize() {
    setLayout(new GridBagLayout());

    tfXPosition = new JTextField(4);
    tfXPosition.addFocusListener(new ChangeHandler());
    lblXPosition = GuiHelper.addLabeledComponent(tfXPosition, "position_panel_x", this);

    tfYPosition = new JTextField(4);
    tfYPosition.addFocusListener(new ChangeHandler());
    lblYPosition = GuiHelper.addLabeledComponent(tfYPosition, "position_panel_y", this);
  }

  public void addPositionChangedListener(PositionChangedListener listener) {
    listenerList.add(PositionChangedListener.class, listener);
  }

  public void removePositionChangedListener(PositionChangedListener listener) {
    listenerList.remove(PositionChangedListener.class, listener);
  }

  public interface PositionChangedListener extends EventListener {
    void onPositionChanged(PositionChangedEvent event);
  }

  public static class PositionChangedEvent extends EventObject {

    private static final long serialVersionUID = 4487738515005537846L;

    private final Point position;

    public PositionChangedEvent(Object source, Point position) {
      super(source);
      this.position = position;
    }

    public Point getPosition() {
      return position;
    }
  }

  private class ChangeHandler implements FocusListener {

    private String oldValue;

    @Override
    public void focusLost(FocusEvent e) {
      if (editable && !oldValue.equals(((JTextComponent) e.getComponent()).getText())) {
        updatePosition();
      }
    }

    @Override
    public void focusGained(FocusEvent e) {
      oldValue = ((JTextComponent) e.getComponent()).getText();
    }
  }
}
