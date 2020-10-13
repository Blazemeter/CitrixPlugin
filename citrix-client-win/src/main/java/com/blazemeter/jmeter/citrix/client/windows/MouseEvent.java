package com.blazemeter.jmeter.citrix.client.windows;

/**
 * Provides Windows specific mouse events.
 */
public class MouseEvent {

  private final int buttonId;
  private final int modifiers;
  private final int xPos;
  private final int yPos;

  /**
   * Instantiates a new {@link MouseEvent}.
   *
   * @param buttonId  the mouse button identifier
   * @param modifiers the modifiers flags
   * @param xPos      the mouse cursor horizontal position
   * @param yPos      the mouse cursor vertical position
   */
  public MouseEvent(int buttonId, int modifiers, int xPos, int yPos) {
    this.buttonId = buttonId;
    this.modifiers = modifiers;
    this.xPos = xPos;
    this.yPos = yPos;
  }

  /**
   * Gets the mouse button identifier.
   *
   * @return the mouse button identifier
   */
  public int getButtonId() {
    return buttonId;
  }

  /**
   * Gets the modifiers flags.
   *
   * @return the modifiers flags
   */
  public int getModifiers() {
    return modifiers;
  }

  /**
   * Gets the mouse cursor horizontal position.
   *
   * @return the mouse cursor horizontal position
   */
  public int getXPos() {
    return xPos;
  }

  /**
   * Gets the mouse cursor vertical position.
   *
   * @return the mouse cursor vertical position
   */
  public int getYPos() {
    return yPos;
  }
}
