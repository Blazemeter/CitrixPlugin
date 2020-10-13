package com.blazemeter.jmeter.citrix.client.events;

/**
 * Enumerates the buttons of a mouse.
 */
public enum MouseButton {
  /**
   * Left button.
   */
  LEFT(1),
  /**
   * Right button.
   */
  RIGHT(2),
  /**
   * Middle button.
   */
  MIDDLE(4);

  private final int value;

  MouseButton(int value) {
    this.value = value;
  }

  /**
   * Gets the integer value of the button.
   *
   * @return the integer value of the button
   */
  public int getValue() {
    return value;
  }
}
