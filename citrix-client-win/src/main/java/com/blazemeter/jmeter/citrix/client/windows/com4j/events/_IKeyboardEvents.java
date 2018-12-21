package com.blazemeter.jmeter.citrix.client.windows.com4j.events;

import com4j.*;

/**
 * _IKeyboardEvents Interface
 */
@IID("{8A5961DF-314E-4B7C-B57F-AAF35EA33079}")
public abstract class _IKeyboardEvents {
  // Methods:
  /**
   * <p>
   * method OnKeyUp
   * </p>
   * @param keyId Mandatory int parameter.
   * @param modifierState Mandatory int parameter.
   */

  @DISPID(1)
  public void onKeyUp(
    int keyId,
    int modifierState) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnKeyDown
   * </p>
   * @param keyId Mandatory int parameter.
   * @param modifierState Mandatory int parameter.
   */

  @DISPID(2)
  public void onKeyDown(
    int keyId,
    int modifierState) {
        throw new UnsupportedOperationException();
  }


  // Properties:
}
