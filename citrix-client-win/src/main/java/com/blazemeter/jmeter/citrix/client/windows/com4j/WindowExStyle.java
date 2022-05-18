package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * Window extended style masks.
 * </p>
 */
public enum WindowExStyle implements ComEnum {
  /**
   * <p>
   * The value of this constant is 1.
   * </p>
   */
  WindowsExStyleDLGMODALFRAME(1), //NOSONAR
  /**
   * <p>
   * The value of this constant is 4.
   * </p>
   */
  WindowsExStyleNOPARENTNOTIFY(4), //NOSONAR
  /**
   * <p>
   * The value of this constant is 8.
   * </p>
   */
  WindowsExStyleTOPMOST(8), //NOSONAR
  /**
   * <p>
   * The value of this constant is 16.
   * </p>
   */
  WindowsExStyleACCEPTFILES(16), //NOSONAR
  /**
   * <p>
   * The value of this constant is 32.
   * </p>
   */
  WindowsExStyleTRANSPARENT(32), //NOSONAR
  /**
   * <p>
   * The value of this constant is 64.
   * </p>
   */
  WindowsExStyleMDICHILD(64), //NOSONAR
  /**
   * <p>
   * The value of this constant is 128.
   * </p>
   */
  WindowsExStyleTOOLWINDOW(128), //NOSONAR
  /**
   * <p>
   * The value of this constant is 256.
   * </p>
   */
  WindowsExStyleWINDOWEDGE(256), //NOSONAR
  /**
   * <p>
   * The value of this constant is 512.
   * </p>
   */
  WindowsExStyleCLIENTEDGE(512), //NOSONAR
  /**
   * <p>
   * The value of this constant is 1024.
   * </p>
   */
  WindowsExStyleCONTEXTHELP(1024), //NOSONAR
  /**
   * <p>
   * The value of this constant is 4096.
   * </p>
   */
  WindowsExStyleRIGHT(4096), //NOSONAR
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleLEFT(0), //NOSONAR
  /**
   * <p>
   * The value of this constant is 8192.
   * </p>
   */
  WindowsExStyleRTLREADING(8192), //NOSONAR
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleLTRREADING(0), //NOSONAR
  /**
   * <p>
   * The value of this constant is 16384.
   * </p>
   */
  WindowsExStyleLEFTSCROLLBAR(16384), //NOSONAR
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleRIGHTSCROLLBAR(0), //NOSONAR
  /**
   * <p>
   * The value of this constant is 65536.
   * </p>
   */
  WindowsExStyleCONTROLPARENT(65536), //NOSONAR
  /**
   * <p>
   * The value of this constant is 131072.
   * </p>
   */
  WindowsExStyleSTATICEDGE(131072), //NOSONAR
  /**
   * <p>
   * The value of this constant is 262144.
   * </p>
   */
  WindowsExStyleAPPWINDOW(262144), //NOSONAR
  /**
   * <p>
   * The value of this constant is 768.
   * </p>
   */
  WindowsExStyleOVERLAPPEDWINDOW(768), //NOSONAR
  /**
   * <p>
   * The value of this constant is 392.
   * </p>
   */
  WindowsExStylePALETTEWINDOW(392); //NOSONAR

  private final int value;

  WindowExStyle(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
