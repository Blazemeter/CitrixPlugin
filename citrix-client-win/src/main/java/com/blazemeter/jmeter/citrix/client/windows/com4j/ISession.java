package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * ISession Interface
 */
@IID("{4A502C16-CFAE-4BB0-B1F9-93ACADDA57BB}")
public interface ISession extends Com4jObject {
  // Methods:
  /**
   * <p>
   * property TopLevelWindows
   * </p>
   * <p>
   * Getter method for the COM property "TopLevelWindows"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IWindows
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IWindows topLevelWindows();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={com.blazemeter.jmeter.citrix.client.windows.com4j.IWindows.class})
  com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow topLevelWindows(
    int n);

  /**
   * <p>
   * property Mouse
   * </p>
   * <p>
   * Getter method for the COM property "Mouse"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IMouse
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IMouse mouse();


  /**
   * <p>
   * property Keyboard
   * </p>
   * <p>
   * Getter method for the COM property "Keyboard"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IKeyboard
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IKeyboard keyboard();


  /**
   * <p>
   * property ForegroundWindow
   * </p>
   * <p>
   * Getter method for the COM property "ForegroundWindow"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow foregroundWindow();


  /**
   * <p>
   * property ReplayMode
   * </p>
   * <p>
   * Getter method for the COM property "ReplayMode"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  boolean replayMode();


  /**
   * <p>
   * property ReplayMode
   * </p>
   * <p>
   * Setter method for the COM property "ReplayMode"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(12)
  void replayMode(
    boolean pVal);


  /**
   * <p>
   * method CreateFullScreenShot
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IScreenShot
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(13)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IScreenShot createFullScreenShot();


  /**
   * <p>
   * method CreateScreenShot
   * </p>
   * @param x Mandatory int parameter.
   * @param y Mandatory int parameter.
   * @param width Mandatory int parameter.
   * @param height Mandatory int parameter.
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IScreenShot
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(14)
  com.blazemeter.jmeter.citrix.client.windows.com4j.IScreenShot createScreenShot(
    int x,
    int y,
    int width,
    int height);


  /**
   * <p>
   * method SendPingRequest
   * </p>
   * @param pingInfo Mandatory java.lang.String parameter.
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(15)
  void sendPingRequest(
    java.lang.String pingInfo);


  // Properties:
}
