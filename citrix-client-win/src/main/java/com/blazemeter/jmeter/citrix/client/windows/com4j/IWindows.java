package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IWindows Interface
 */
@IID("{9B371833-5E4F-4B72-A8F6-CB8E762999F4}")
public interface IWindows extends Com4jObject,Iterable<Com4jObject> {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Item"
   * </p>
   * @param n Mandatory int parameter.
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow item(
    int n);


  /**
   * <p>
   * Getter method for the COM property "_NewEnum"
   * </p>
   */

  @DISPID(-4) //= 0xfffffffc. The runtime will prefer the VTID if present
  @VTID(8)
  java.util.Iterator<Com4jObject> iterator();

  /**
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(9)
  int count();


  // Properties:
}
