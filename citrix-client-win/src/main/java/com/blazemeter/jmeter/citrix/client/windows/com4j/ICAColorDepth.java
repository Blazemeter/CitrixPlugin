package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * List of ICA color depths.
 * </p>
 */
public enum ICAColorDepth implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1.
   * </p>
   */
  Color16(1),
  /**
   * <p>
   * The value of this constant is 2.
   * </p>
   */
  Color256(2),
  /**
   * <p>
   * The value of this constant is 4.
   * </p>
   */
  Color16Bit(4),
  /**
   * <p>
   * The value of this constant is 8.
   * </p>
   */
  Color24Bit(8);

  private final int value;

  ICAColorDepth(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
