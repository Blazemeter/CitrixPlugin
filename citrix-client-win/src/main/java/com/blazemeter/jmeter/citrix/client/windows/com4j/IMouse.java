package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IMouse Interface
 */
@IID("{02093274-7B69-4FEB-B7FD-3A010561A5F3}")
public interface IMouse extends Com4jObject {
  // Methods:
  /**
   * <p>
   * method SendMouseDown
   * </p>
   * @param buttonId Mandatory int parameter.
   * @param modifiers Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(7)
  void sendMouseDown(
    int buttonId,
    int modifiers,
    int xPos,
    int yPos);


  /**
   * <p>
   * method SendMouseUp
   * </p>
   * @param buttonId Mandatory int parameter.
   * @param modifiers Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(8)
  void sendMouseUp(
    int buttonId,
    int modifiers,
    int xPos,
    int yPos);


  /**
   * <p>
   * method SendMouseMove
   * </p>
   * @param buttonId Mandatory int parameter.
   * @param modifiers Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(9)
  void sendMouseMove(
    int buttonId,
    int modifiers,
    int xPos,
    int yPos);


  // Properties:
}
