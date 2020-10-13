package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * List of ICA sound qualities.
 * </p>
 */
public enum ICASoundQuality implements ComEnum {
  /**
   * <p>
   * The value of this constant is -1.
   * </p>
   */
  SoundQualityNone(-1),
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  SoundQualityHigh(0),
  /**
   * <p>
   * The value of this constant is 1.
   * </p>
   */
  SoundQualityMedium(1),
  /**
   * <p>
   * The value of this constant is 2.
   * </p>
   */
  SoundQualityLow(2);

  private final int value;

  ICASoundQuality(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
