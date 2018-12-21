package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IScreenShot Interface
 */
@IID("{8F2D9E63-D224-47E4-8111-32DBB016A4C6}")
public interface IScreenShot extends Com4jObject {
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
   * property PositionX
   * </p>
   * <p>
   * Setter method for the COM property "PositionX"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  void positionX(
    int pVal);


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
  @VTID(9)
  int positionY();


  /**
   * <p>
   * property PositionY
   * </p>
   * <p>
   * Setter method for the COM property "PositionY"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void positionY(
    int pVal);


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
  @VTID(11)
  int width();


  /**
   * <p>
   * property Width
   * </p>
   * <p>
   * Setter method for the COM property "Width"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  void width(
    int pVal);


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
  @VTID(13)
  int height();


  /**
   * <p>
   * property Height
   * </p>
   * <p>
   * Setter method for the COM property "Height"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(14)
  void height(
    int pVal);


  /**
   * <p>
   * property Filename
   * </p>
   * <p>
   * Getter method for the COM property "Filename"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String filename();


  /**
   * <p>
   * property Filename
   * </p>
   * <p>
   * Setter method for the COM property "Filename"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(16)
  void filename(
    java.lang.String pVal);


  /**
   * <p>
   * property BitmapHash
   * </p>
   * <p>
   * Getter method for the COM property "BitmapHash"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String bitmapHash();


  /**
   * <p>
   * method Save
   * </p>
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(18)
  void save();


  // Properties:
}
