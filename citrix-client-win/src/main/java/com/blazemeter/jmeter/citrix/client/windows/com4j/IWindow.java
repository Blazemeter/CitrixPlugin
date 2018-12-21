package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IWindow Interface
 */
@IID("{4D5D2139-29E2-4CDC-8020-429B35999BE6}")
public interface IWindow extends Com4jObject {
  // Methods:
  /**
   * <p>
   * property PositionX
   * </p>
   * <p>
   * Getter method for the COM property "PositionX"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  int positionX();


  /**
   * <p>
   * property PositionY
   * </p>
   * <p>
   * Getter method for the COM property "PositionY"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  int positionY();


  /**
   * <p>
   * property Width
   * </p>
   * <p>
   * Getter method for the COM property "Width"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  int width();


  /**
   * <p>
   * property Height
   * </p>
   * <p>
   * Getter method for the COM property "Height"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  int height();


  /**
   * <p>
   * property Style
   * </p>
   * <p>
   * Getter method for the COM property "Style"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int style();


  /**
   * <p>
   * property ExtendedStyle
   * </p>
   * <p>
   * Getter method for the COM property "ExtendedStyle"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int extendedStyle();


  /**
   * <p>
   * property Caption
   * </p>
   * <p>
   * Getter method for the COM property "Caption"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  java.lang.String caption();


  /**
   * <p>
   * property SmallIconHash
   * </p>
   * <p>
   * Getter method for the COM property "SmallIconHash"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  java.lang.String smallIconHash();


  /**
   * <p>
   * property LargeIconHash
   * </p>
   * <p>
   * Getter method for the COM property "LargeIconHash"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String largeIconHash();


  /**
   * <p>
   * property Disposed
   * </p>
   * <p>
   * Getter method for the COM property "Disposed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  boolean disposed();


  /**
   * <p>
   * property WindowFlags
   * </p>
   * <p>
   * Getter method for the COM property "WindowFlags"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  int windowFlags();


  /**
   * <p>
   * property WindowID
   * </p>
   * <p>
   * Getter method for the COM property "WindowID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  int windowID();


  /**
   * <p>
   * property ParentID
   * </p>
   * <p>
   * Getter method for the COM property "ParentID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  int parentID();


  /**
   * <p>
   * method BringToTop
   * </p>
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(20)
  void bringToTop();


  /**
   * <p>
   * method Move
   * </p>
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(21)
  void move(
    int xPos,
    int yPos);


  /**
   * <p>
   * method Resize
   * </p>
   * @param width Mandatory int parameter.
   * @param height Mandatory int parameter.
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(22)
  void resize(
    int width,
    int height);


  /**
   * <p>
   * method Restore
   * </p>
   */

  @DISPID(103) //= 0x67. The runtime will prefer the VTID if present
  @VTID(23)
  void restore();


  // Properties:
}
