package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * Window style masks.
 * </p>
 */
public enum WindowStyle implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowStyleOVERLAPPED(0), //NOSONAR
  /**
   * <p>
   * The value of this constant is -2147483648.
   * </p>
   */
  WindowStylePOPUP(-2147483648), //NOSONAR
  /**
   * <p>
   * The value of this constant is 1073741824.
   * </p>
   */
  WindowStyleCHILD(1073741824), //NOSONAR
  /**
   * <p>
   * The value of this constant is 536870912.
   * </p>
   */
  WindowStyleMINIMIZE(536870912), //NOSONAR
  /**
   * <p>
   * The value of this constant is 268435456.
   * </p>
   */
  WindowStyleVISIBLE(268435456), //NOSONAR
  /**
   * <p>
   * The value of this constant is 134217728.
   * </p>
   */
  WindowStyleDISABLED(134217728), //NOSONAR
  /**
   * <p>
   * The value of this constant is 67108864.
   * </p>
   */
  WindowStyleCLIPSIBLINGS(67108864), //NOSONAR
  /**
   * <p>
   * The value of this constant is 33554432.
   * </p>
   */
  WindowStyleCLIPCHILDREN(33554432), //NOSONAR
  /**
   * <p>
   * The value of this constant is 16777216.
   * </p>
   */
  WindowStyleMAXIMIZE(16777216), //NOSONAR
  /**
   * <p>
   * The value of this constant is 12582912.
   * </p>
   */
  WindowStyleCAPTION(12582912), //NOSONAR
  /**
   * <p>
   * The value of this constant is 8388608.
   * </p>
   */
  WindowStyleBORDER(8388608), //NOSONAR
  /**
   * <p>
   * The value of this constant is 4194304.
   * </p>
   */
  WindowStyleDLGFRAME(4194304), //NOSONAR
  /**
   * <p>
   * The value of this constant is 2097152.
   * </p>
   */
  WindowStyleVSCROLL(2097152), //NOSONAR
  /**
   * <p>
   * The value of this constant is 1048576.
   * </p>
   */
  WindowStyleHSCROLL(1048576), //NOSONAR
  /**
   * <p>
   * The value of this constant is 524288.
   * </p>
   */
  WindowStyleSYSMENU(524288), //NOSONAR
  /**
   * <p>
   * The value of this constant is 262144.
   * </p>
   */
  WindowStyleTHICKFRAME(262144), //NOSONAR
  /**
   * <p>
   * The value of this constant is 131072.
   * </p>
   */
  WindowStyleGROUP(131072), //NOSONAR
  /**
   * <p>
   * The value of this constant is 65536.
   * </p>
   */
  WindowStyleTABSTOP(65536), //NOSONAR
  /**
   * <p>
   * The value of this constant is 131072.
   * </p>
   */
  WindowStyleMINIMIZEBOX(131072), //NOSONAR
  /**
   * <p>
   * The value of this constant is 65536.
   * </p>
   */
  WindowStyleMAXIMIZEBOX(65536); //NOSONAR

  private final int value;

  WindowStyle(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
