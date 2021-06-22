package com.blazemeter.jmeter.citrix.client.events;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.Win32Utils;
import java.awt.Rectangle;
import java.util.EnumSet;
import java.util.Set;

/**
 * Provides an event built when a user interacts with a Citrix session.
 */
public class InteractionEvent extends ClientEvent {

  private static final long serialVersionUID = 4255838475686322061L;
  private final int windowID;
  private final Rectangle foregroundWindowArea;
  private final InteractionType interactionType;
  private final Set<Modifier> modifiers;
  // Key specific
  private final KeyState keyState;
  private final int keyCode;
  // Mouse specific
  private final Set<MouseButton> buttons;
  private final MouseAction mouseAction;
  private final int x;
  private final int y;

  /**
   * Products a CitrixInteractionEvent for key interaction.
   *
   * @param source    the source of the event
   * @param state     the state of the key
   * @param keyCode   the code of pressed key
   * @param modifiers the set of pressed modifier keys
   */
  public InteractionEvent(CitrixClient source, int windowID, Rectangle foregroundWindowArea,
                          KeyState state, int keyCode,
                          Set<Modifier> modifiers) {
    super(source);
    this.windowID = windowID;
    this.foregroundWindowArea = foregroundWindowArea;
    interactionType = InteractionType.KEY;
    this.modifiers = modifiers != null ? EnumSet.copyOf(modifiers) : EnumSet.noneOf(Modifier.class);

    this.keyCode = keyCode;
    this.keyState = state;

    this.buttons = EnumSet.noneOf(MouseButton.class);
    this.mouseAction = null;
    this.x = -1;
    this.y = -1;
  }

  /**
   * Products a CitrixInteractionEvent for mouse interaction.
   *
   * @param source      the source of this event
   * @param mouseAction the action on the mouse
   * @param x           position of the mouse
   * @param y           position of the mouse
   * @param buttons     the set of pressed mouse buttons
   * @param modifiers   the set of pressed modifier keys
   */
  public InteractionEvent(CitrixClient source, int windowID, Rectangle foregroundWindowArea,
                          MouseAction mouseAction, int x, int y,
                          Set<MouseButton> buttons,
                          Set<Modifier> modifiers) {
    super(source);
    this.windowID = windowID;
    this.foregroundWindowArea = foregroundWindowArea;
    interactionType = InteractionType.MOUSE;
    this.modifiers = modifiers != null ? EnumSet.copyOf(modifiers) : EnumSet.noneOf(Modifier.class);

    this.keyCode = -1;
    this.keyState = null;

    this.buttons = buttons != null ? EnumSet.copyOf(buttons) : EnumSet.noneOf(MouseButton.class);
    this.mouseAction = mouseAction;
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the type of this interaction.
   *
   * @return the type of interaction
   */
  public InteractionType getInteractionType() {
    return interactionType;
  }

  /**
   * Gets the code of the key.
   *
   * @return the code of the key
   */
  public int getKeyCode() {
    return keyCode;
  }

  public int getTargetKeyCode() {
    boolean withShift = modifiers.contains(Modifier.SHIFT);
    boolean withAlt = modifiers.contains(Modifier.ALT);
    boolean withCtrl = modifiers.contains(Modifier.CONTROL);
    return Win32Utils.getVKCharacter(keyCode, withShift, withAlt, withCtrl);
  }

  public String getTargetKeyCodeString() {
    return String.valueOf((char) getTargetKeyCode());
  }

  /**
   * Gets the set of key modifiers pressed during this event.
   *
   * @return the set of key modifiers pressed during this event
   */
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  /**
   * Gets the state of the key.
   *
   * @return the state of the key
   */
  public KeyState getKeyState() {
    return keyState;
  }

  /**
   * Gets the action on the mouse.
   *
   * @return the action on the mouse
   */
  public MouseAction getMouseAction() {
    return mouseAction;
  }

  /**
   * Gets the horizontal coordinate of the mouse pointer.
   *
   * @return the horizontal coordinate of the mouse pointer
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the vertical coordinate of the mouse pointer.
   *
   * @return the vertical coordinate of the mouse pointer
   */
  public int getY() {
    return y;
  }

  /**
   * Gets the set of pressed buttons during the event.
   *
   * @return the set of pressed buttons during the event
   */
  public Set<MouseButton> getButtons() {
    return buttons;
  }

  public String getModifiersText() {
    return EventHelper.getModifiersText(getModifiers());
  }

  public int getWindowID() {
    return this.windowID;
  }

  public Rectangle getForegroundWindowArea() {
    return this.foregroundWindowArea;
  }

  /**
   * Enumerates the types of user interactions on Citrix session.
   */
  public enum InteractionType {
    /**
     * Keyboard interaction.
     */
    KEY,
    /**
     * Mouse interaction.
     */
    MOUSE
  }

  /**
   * Enumerates keyboard interactions.
   */
  public enum KeyState {
    /**
     * A key is released.
     */
    KEY_UP,
    /**
     * A key is pressed.
     */
    KEY_DOWN
  }

  /**
   * Defines the types of mouse interactions.
   */
  public enum MouseAction {
    /**
     * The mouse is moved.
     */
    MOVE,
    /**
     * A mouse button is released.
     */
    BUTTON_UP,
    /**
     * A mouse button is pressed.
     */
    BUTTON_DOWN
  }

}
