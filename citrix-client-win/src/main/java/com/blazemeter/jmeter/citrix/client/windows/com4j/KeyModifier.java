package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * Key modifier buttons.
 * </p>
 */
public enum KeyModifier implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1.
   * </p>
   */
  KeyModifierShift(1),
  /**
   * <p>
   * The value of this constant is 2.
   * </p>
   */
  KeyModifierControl(2),
  /**
   * <p>
   * The value of this constant is 4.
   * </p>
   */
  KeyModifierAlt(4),
  /**
   * <p>
   * The value of this constant is 8.
   * </p>
   */
  KeyModifierExtended(8);

  private final int value;

  KeyModifier(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
