package com.blazemeter.jmeter.citrix.client.windows.com4j.events;

import com4j.*;

/**
 * _ISessionEvents Interface
 */
@IID("{24FD31DB-3560-4C78-8950-30F03352D830}")
public abstract class _ISessionEvents {
  // Methods:
  /**
   * <p>
   * method OnWindowCreate
   * </p>
   * @param window Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow parameter.
   */

  @DISPID(1)
  public void onWindowCreate(
    com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow window) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnWindowDestroy
   * </p>
   * @param window Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow parameter.
   */

  @DISPID(2)
  public void onWindowDestroy(
    com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow window) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnPingAck
   * </p>
   * @param pingInfo Mandatory java.lang.String parameter.
   * @param roundTripTime Mandatory int parameter.
   */

  @DISPID(4)
  public void onPingAck(
    java.lang.String pingInfo,
    int roundTripTime) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnWindowForeground
   * </p>
   * @param windowID Mandatory int parameter.
   */

  @DISPID(5)
  public void onWindowForeground(
    int windowID) {
        throw new UnsupportedOperationException();
  }


  // Properties:
}
