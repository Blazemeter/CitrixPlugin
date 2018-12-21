package com.blazemeter.jmeter.citrix.client.windows.com4j.events;

import com4j.*;

/**
 * _IMouseEvents Interface
 */
@IID("{24013613-FF51-4B50-9832-37FA561594ED}")
public abstract class _IMouseEvents {
  // Methods:
  /**
   * <p>
   * method OnMove
   * </p>
   * @param buttonState Mandatory int parameter.
   * @param modifierState Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(1)
  public void onMove(
    int buttonState,
    int modifierState,
    int xPos,
    int yPos) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnMouseDown
   * </p>
   * @param buttonState Mandatory int parameter.
   * @param modifierState Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(3)
  public void onMouseDown(
    int buttonState,
    int modifierState,
    int xPos,
    int yPos) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnMouseUp
   * </p>
   * @param buttonState Mandatory int parameter.
   * @param modifierState Mandatory int parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(2)
  public void onMouseUp(
    int buttonState,
    int modifierState,
    int xPos,
    int yPos) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnDoubleClick
   * </p>
   */

  @DISPID(4)
  public void onDoubleClick() {
        throw new UnsupportedOperationException();
  }


  // Properties:
}
