package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * <p>
 * Mouse buttons
 * </p>
 */
public enum MouseButton implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  MouseButtonLeft(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  MouseButtonRight(2),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  MouseButtonMiddle(4),
  ;

  private final int value;
  MouseButton(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
