package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IKeyboard Interface
 */
@IID("{17BFCA0A-C42E-4AC9-A693-29473FF9BA6C}")
public interface IKeyboard extends Com4jObject {
  // Methods:
  /**
   * <p>
   * method SendKeyDown
   * </p>
   * @param keyId Mandatory int parameter.
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(7)
  void sendKeyDown(
    int keyId);


  /**
   * <p>
   * method SendKeyUp
   * </p>
   * @param keyId Mandatory int parameter.
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(8)
  void sendKeyUp(
    int keyId);


  // Properties:
}
