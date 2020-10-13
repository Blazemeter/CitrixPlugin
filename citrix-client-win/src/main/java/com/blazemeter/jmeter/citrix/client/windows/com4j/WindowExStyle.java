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
  WindowsExStyleDLGMODALFRAME(1),
  /**
   * <p>
   * The value of this constant is 4.
   * </p>
   */
  WindowsExStyleNOPARENTNOTIFY(4),
  /**
   * <p>
   * The value of this constant is 8.
   * </p>
   */
  WindowsExStyleTOPMOST(8),
  /**
   * <p>
   * The value of this constant is 16.
   * </p>
   */
  WindowsExStyleACCEPTFILES(16),
  /**
   * <p>
   * The value of this constant is 32.
   * </p>
   */
  WindowsExStyleTRANSPARENT(32),
  /**
   * <p>
   * The value of this constant is 64.
   * </p>
   */
  WindowsExStyleMDICHILD(64),
  /**
   * <p>
   * The value of this constant is 128.
   * </p>
   */
  WindowsExStyleTOOLWINDOW(128),
  /**
   * <p>
   * The value of this constant is 256.
   * </p>
   */
  WindowsExStyleWINDOWEDGE(256),
  /**
   * <p>
   * The value of this constant is 512.
   * </p>
   */
  WindowsExStyleCLIENTEDGE(512),
  /**
   * <p>
   * The value of this constant is 1024.
   * </p>
   */
  WindowsExStyleCONTEXTHELP(1024),
  /**
   * <p>
   * The value of this constant is 4096.
   * </p>
   */
  WindowsExStyleRIGHT(4096),
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleLEFT(0),
  /**
   * <p>
   * The value of this constant is 8192.
   * </p>
   */
  WindowsExStyleRTLREADING(8192),
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleLTRREADING(0),
  /**
   * <p>
   * The value of this constant is 16384.
   * </p>
   */
  WindowsExStyleLEFTSCROLLBAR(16384),
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  WindowsExStyleRIGHTSCROLLBAR(0),
  /**
   * <p>
   * The value of this constant is 65536.
   * </p>
   */
  WindowsExStyleCONTROLPARENT(65536),
  /**
   * <p>
   * The value of this constant is 131072.
   * </p>
   */
  WindowsExStyleSTATICEDGE(131072),
  /**
   * <p>
   * The value of this constant is 262144.
   * </p>
   */
  WindowsExStyleAPPWINDOW(262144),
  /**
   * <p>
   * The value of this constant is 768.
   * </p>
   */
  WindowsExStyleOVERLAPPEDWINDOW(768),
  /**
   * <p>
   * The value of this constant is 392.
   * </p>
   */
  WindowsExStylePALETTEWINDOW(392);

  private final int value;

  WindowExStyle(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
