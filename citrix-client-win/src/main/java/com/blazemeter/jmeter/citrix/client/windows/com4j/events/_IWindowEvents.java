package com.blazemeter.jmeter.citrix.client.windows.com4j.events;

import com4j.DISPID;
import com4j.IID;

/**
 * _IWindowEvents Interface.
 */

@SuppressWarnings("checkstyle:TypeName")
@IID("{49813E6D-17FF-41A1-9A7B-95C3D5B44185}")
public abstract class _IWindowEvents {
  // Methods:

  /**
   * <p>
   * method OnMove.
   * </p>
   *
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(1)
  public void onMove(
      int xPos,
      int yPos) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnSize.
   * </p>
   *
   * @param width  Mandatory int parameter.
   * @param height Mandatory int parameter.
   */

  @DISPID(2)
  public void onSize(
      int width,
      int height) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnActivate.
   * </p>
   */

  @DISPID(3)
  public void onActivate() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnDeactivate.
   * </p>
   */

  @DISPID(4)
  public void onDeactivate() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnMinimize.
   * </p>
   */

  @DISPID(5)
  public void onMinimize() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnCaptionChange.
   * </p>
   *
   * @param caption Mandatory java.lang.String parameter.
   */

  @DISPID(6)
  public void onCaptionChange(
      java.lang.String caption) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnStyleChange.
   * </p>
   *
   * @param style         Mandatory int parameter.
   * @param extendedStyle Mandatory int parameter.
   */

  @DISPID(7)
  public void onStyleChange(
      int style,
      int extendedStyle) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnSmallIconChange.
   * </p>
   *
   * @param smallIconHash Mandatory java.lang.String parameter.
   */

  @DISPID(8)
  public void onSmallIconChange(
      java.lang.String smallIconHash) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnLargeIconChange.
   * </p>
   *
   * @param largeIconHash Mandatory java.lang.String parameter.
   */

  @DISPID(9)
  public void onLargeIconChange(
      java.lang.String largeIconHash) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * method OnDestroy.
   * </p>
   */

  @DISPID(10)
  public void onDestroy() {
    throw new UnsupportedOperationException();
  }


  // Properties:
}
