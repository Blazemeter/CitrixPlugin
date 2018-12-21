package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * IICAClient Interface
 */
@IID("{238F6F81-B8B4-11CF-8771-00A024541EE3}")
public interface IICAClient extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Setter method for the COM property "TabStop"
   * </p>
   * @param pbool Mandatory boolean parameter.
   */

  @DISPID(-516) //= 0xfffffdfc. The runtime will prefer the VTID if present
  @VTID(7)
  void tabStop(
    boolean pbool);


  /**
   * <p>
   * Getter method for the COM property "TabStop"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(-516) //= 0xfffffdfc. The runtime will prefer the VTID if present
  @VTID(8)
  boolean tabStop();


  /**
   * <p>
   * method Show AboutBox
   * </p>
   */

  @DISPID(-552) //= 0xfffffdd8. The runtime will prefer the VTID if present
  @VTID(9)
  void aboutBox();


  /**
   * <p>
   * method Clear all properties
   * </p>
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(10)
  void clearProps();


  /**
   * <p>
   * method Get property count
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(11)
  int getPropCount();


  /**
   * <p>
   * method Delete propoperty
   * </p>
   * @param name Mandatory java.lang.String parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  void deleteProp(
    java.lang.String name);


  /**
   * <p>
   * method Delete propoperty by index
   * </p>
   * @param index Mandatory int parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  void deletePropByIndex(
    int index);


  /**
   * <p>
   * method Get propoperty name by index
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(14)
  java.lang.String getPropNameByIndex(
    int index);


  /**
   * <p>
   * method Reset all propoperties
   * </p>
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(15)
  void resetProps();


  /**
   * <p>
   * method Set propoperty
   * </p>
   * @param name Mandatory java.lang.String parameter.
   * @param value Mandatory java.lang.String parameter.
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(16)
  void setProp(
    java.lang.String name,
    java.lang.String value);


  /**
   * <p>
   * method Get propoperty value
   * </p>
   * @param name Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String getPropValue(
    java.lang.String name);


  /**
   * <p>
   * method Get propoperty value by index
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(18)
  java.lang.String getPropValueByIndex(
    int index);


  /**
   * <p>
   * method Connect to server
   * </p>
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(19)
  void connect();


  /**
   * <p>
   * method Disconnect from server
   * </p>
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(20)
  void disconnect();


  /**
   * <p>
   * method Logoff from server
   * </p>
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(21)
  void logoff();


  /**
   * <p>
   * method Load ICA file
   * </p>
   * @param file Mandatory java.lang.String parameter.
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(22)
  void loadIcaFile(
    java.lang.String file);


  /**
   * <p>
   * method Run published application
   * </p>
   * @param appName Mandatory java.lang.String parameter.
   * @param arguments Mandatory java.lang.String parameter.
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(23)
  void runPublishedApplication(
    java.lang.String appName,
    java.lang.String arguments);


  /**
   * <p>
   * method Set session end action
   * </p>
   * @param action Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction parameter.
   */

  @DISPID(15) //= 0xf. The runtime will prefer the VTID if present
  @VTID(24)
  void setSessionEndAction(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction action);


  /**
   * <p>
   * method True if currently connected to server
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(25)
  boolean isConnected();


  /**
   * <p>
   * method Get ICA Client interface version
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(17) //= 0x11. The runtime will prefer the VTID if present
  @VTID(26)
  java.lang.String getInterfaceVersion();


  /**
   * <p>
   * method Get ICA Client identification
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(27)
  java.lang.String getClientIdentification();


  /**
   * <p>
   * method Get session string
   * </p>
   * @param index Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionString parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(19) //= 0x13. The runtime will prefer the VTID if present
  @VTID(28)
  java.lang.String getSessionString(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionString index);


  /**
   * <p>
   * method Get session counter
   * </p>
   * @param index Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionCounter parameter.
   * @return  Returns a value of type int
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(29)
  int getSessionCounter(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionCounter index);


  /**
   * <p>
   * method Get last notification reason
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICAEvent
   */

  @DISPID(21) //= 0x15. The runtime will prefer the VTID if present
  @VTID(30)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICAEvent getNotificationReason();


  /**
   * <p>
   * method Startup
   * </p>
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(31)
  void startup();


  /**
   * <p>
   * method Get Last Error
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(23) //= 0x17. The runtime will prefer the VTID if present
  @VTID(32)
  int getLastError();


  /**
   * <p>
   * method Get Last Client Error
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(33)
  int getLastClientError();


  /**
   * <p>
   * method Enable scaling
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(25) //= 0x19. The runtime will prefer the VTID if present
  @VTID(34)
  int scaleEnable();


  /**
   * <p>
   * method Disable scaling
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(35)
  int scaleDisable();


  /**
   * <p>
   * method Scale up to a larger size
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(27) //= 0x1b. The runtime will prefer the VTID if present
  @VTID(36)
  int scaleUp();


  /**
   * <p>
   * method Scale down to a smaller size
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(37)
  int scaleDown();


  /**
   * <p>
   * method Scale to a size
   * </p>
   * @param width Mandatory int parameter.
   * @param height Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(29) //= 0x1d. The runtime will prefer the VTID if present
  @VTID(38)
  int scaleSize(
    int width,
    int height);


  /**
   * <p>
   * method Scale to a percent
   * </p>
   * @param percent Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(39)
  int scalePercent(
    int percent);


  /**
   * <p>
   * method Scale to fit size of ICA Client Object window
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(31) //= 0x1f. The runtime will prefer the VTID if present
  @VTID(40)
  int scaleToFit();


  /**
   * <p>
   * method Popup the scaling dialog box
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(41)
  int scaleDialog();


  /**
   * <p>
   * method CreateChannels
   * </p>
   * @param channelNames Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(33) //= 0x21. The runtime will prefer the VTID if present
  @VTID(42)
  int createChannels(
    java.lang.String channelNames);


  /**
   * <p>
   * method SendChannelData
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @param data Mandatory java.lang.String parameter.
   * @param dataSize Mandatory int parameter.
   * @param dataType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType parameter.
   * @return  Returns a value of type int
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(43)
  int sendChannelData(
    java.lang.String channelName,
    java.lang.String data,
    int dataSize,
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType dataType);


  /**
   * <p>
   * method GetChannelCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(35) //= 0x23. The runtime will prefer the VTID if present
  @VTID(44)
  int getChannelCount();


  /**
   * <p>
   * method GetChannelName
   * </p>
   * @param channelIndex Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(45)
  java.lang.String getChannelName(
    int channelIndex);


  /**
   * <p>
   * method GetChannelNumber
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(37) //= 0x25. The runtime will prefer the VTID if present
  @VTID(46)
  int getChannelNumber(
    java.lang.String channelName);


  /**
   * <p>
   * method GetGlobalChannelCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(47)
  int getGlobalChannelCount();


  /**
   * <p>
   * method GetGlobalChannelName
   * </p>
   * @param channelIndex Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(39) //= 0x27. The runtime will prefer the VTID if present
  @VTID(48)
  java.lang.String getGlobalChannelName(
    int channelIndex);


  /**
   * <p>
   * method GetGlobalChannelNumber
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(49)
  int getGlobalChannelNumber(
    java.lang.String channelName);


  /**
   * <p>
   * method GetMaxChannelCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(41) //= 0x29. The runtime will prefer the VTID if present
  @VTID(50)
  int getMaxChannelCount();


  /**
   * <p>
   * method GetMaxChannelWrite
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(51)
  int getMaxChannelWrite();


  /**
   * <p>
   * method GetMaxChannelRead
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(43) //= 0x2b. The runtime will prefer the VTID if present
  @VTID(52)
  int getMaxChannelRead();


  /**
   * <p>
   * method SetChannelFlags
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @param flags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(53)
  int setChannelFlags(
    java.lang.String channelName,
    int flags);


  /**
   * <p>
   * method GetChannelFlags
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(45) //= 0x2d. The runtime will prefer the VTID if present
  @VTID(54)
  int getChannelFlags(
    java.lang.String channelName);


  /**
   * <p>
   * method GetChannelDataSize
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(55)
  int getChannelDataSize(
    java.lang.String channelName);


  /**
   * <p>
   * method GetChannelDataType
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType
   */

  @DISPID(47) //= 0x2f. The runtime will prefer the VTID if present
  @VTID(56)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType getChannelDataType(
    java.lang.String channelName);


  /**
   * <p>
   * method GetChannelData
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @param dataType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(57)
  java.lang.String getChannelData(
    java.lang.String channelName,
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAVCDataType dataType);


  /**
   * <p>
   * method EnumerateServers
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(49) //= 0x31. The runtime will prefer the VTID if present
  @VTID(58)
  int enumerateServers();


  /**
   * <p>
   * method EnumerateApplications
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(59)
  int enumerateApplications();


  /**
   * <p>
   * method EnumerateFarms
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(51) //= 0x33. The runtime will prefer the VTID if present
  @VTID(60)
  int enumerateFarms();


  /**
   * <p>
   * method GetEnumNameCount
   * </p>
   * @param hndEnum Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(52) //= 0x34. The runtime will prefer the VTID if present
  @VTID(61)
  int getEnumNameCount(
    int hndEnum);


  /**
   * <p>
   * method GetEnumNameByIndex
   * </p>
   * @param hndEnum Mandatory int parameter.
   * @param hndIndex Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(53) //= 0x35. The runtime will prefer the VTID if present
  @VTID(62)
  java.lang.String getEnumNameByIndex(
    int hndEnum,
    int hndIndex);


  /**
   * <p>
   * method CloseEnumHandle
   * </p>
   * @param hndEnum Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(54) //= 0x36. The runtime will prefer the VTID if present
  @VTID(63)
  int closeEnumHandle(
    int hndEnum);


  /**
   * <p>
   * method GetWindowWidth
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(56) //= 0x38. The runtime will prefer the VTID if present
  @VTID(64)
  int getWindowWidth(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int wndFlags);


  /**
   * <p>
   * method GetWindowHeight
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(57) //= 0x39. The runtime will prefer the VTID if present
  @VTID(65)
  int getWindowHeight(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int wndFlags);


  /**
   * <p>
   * method SetWindowSize
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param width Mandatory int parameter.
   * @param height Mandatory int parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(58) //= 0x3a. The runtime will prefer the VTID if present
  @VTID(66)
  int setWindowSize(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int width,
    int height,
    int wndFlags);


  /**
   * <p>
   * method GetWindowXPosition
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(59) //= 0x3b. The runtime will prefer the VTID if present
  @VTID(67)
  int getWindowXPosition(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int wndFlags);


  /**
   * <p>
   * method GetWindowYPosition
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(60) //= 0x3c. The runtime will prefer the VTID if present
  @VTID(68)
  int getWindowYPosition(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int wndFlags);


  /**
   * <p>
   * method SetWindowPosition
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   * @param wndFlags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(61) //= 0x3d. The runtime will prefer the VTID if present
  @VTID(69)
  int setWindowPosition(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType,
    int xPos,
    int yPos,
    int wndFlags);


  /**
   * <p>
   * method DisplayWindow
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @return  Returns a value of type int
   */

  @DISPID(62) //= 0x3e. The runtime will prefer the VTID if present
  @VTID(70)
  int displayWindow(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType);


  /**
   * <p>
   * method HideWindow
   * </p>
   * @param wndType Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType parameter.
   * @return  Returns a value of type int
   */

  @DISPID(63) //= 0x3f. The runtime will prefer the VTID if present
  @VTID(71)
  int hideWindow(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType wndType);


  /**
   * <p>
   * method UndockWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(64) //= 0x40. The runtime will prefer the VTID if present
  @VTID(72)
  int undockWindow();


  /**
   * <p>
   * method DockWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(65) //= 0x41. The runtime will prefer the VTID if present
  @VTID(73)
  int dockWindow();


  /**
   * <p>
   * method PlaceWindowOnTop
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(66) //= 0x42. The runtime will prefer the VTID if present
  @VTID(74)
  int placeWindowOnTop();


  /**
   * <p>
   * method PlaceWindowOnBottom
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(67) //= 0x43. The runtime will prefer the VTID if present
  @VTID(75)
  int placeWindowOnBottom();


  /**
   * <p>
   * method MinimizeWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(68) //= 0x44. The runtime will prefer the VTID if present
  @VTID(76)
  int minimizeWindow();


  /**
   * <p>
   * method MaximizeWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(69) //= 0x45. The runtime will prefer the VTID if present
  @VTID(77)
  int maximizeWindow();


  /**
   * <p>
   * method RestoreWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(70) //= 0x46. The runtime will prefer the VTID if present
  @VTID(78)
  int restoreWindow();


  /**
   * <p>
   * method ShowTitleBar
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(71) //= 0x47. The runtime will prefer the VTID if present
  @VTID(79)
  int showTitleBar();


  /**
   * <p>
   * method HideTitleBar
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(72) //= 0x48. The runtime will prefer the VTID if present
  @VTID(80)
  int hideTitleBar();


  /**
   * <p>
   * method EnableSizingBorder
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(73) //= 0x49. The runtime will prefer the VTID if present
  @VTID(81)
  int enableSizingBorder();


  /**
   * <p>
   * method DisableSizingBorder
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(74) //= 0x4a. The runtime will prefer the VTID if present
  @VTID(82)
  int disableSizingBorder();


  /**
   * <p>
   * method FullScreenWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(75) //= 0x4b. The runtime will prefer the VTID if present
  @VTID(83)
  int fullScreenWindow();


  /**
   * <p>
   * method FocusWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(76) //= 0x4c. The runtime will prefer the VTID if present
  @VTID(84)
  int focusWindow();


  /**
   * <p>
   * method IsWindowDocked
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(77) //= 0x4d. The runtime will prefer the VTID if present
  @VTID(85)
  boolean isWindowDocked();


  /**
   * <p>
   * method GetSessionWidth
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(78) //= 0x4e. The runtime will prefer the VTID if present
  @VTID(86)
  int getSessionWidth();


  /**
   * <p>
   * method GetSessionHeight
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(79) //= 0x4f. The runtime will prefer the VTID if present
  @VTID(87)
  int getSessionHeight();


  /**
   * <p>
   * method GetSessionColorDepth
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(80) //= 0x50. The runtime will prefer the VTID if present
  @VTID(88)
  int getSessionColorDepth();


  /**
   * <p>
   * method GetScreenWidth
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(81) //= 0x51. The runtime will prefer the VTID if present
  @VTID(89)
  int getScreenWidth();


  /**
   * <p>
   * method GetScreenHeight
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(82) //= 0x52. The runtime will prefer the VTID if present
  @VTID(90)
  int getScreenHeight();


  /**
   * <p>
   * method GetScreenColorDepth
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(83) //= 0x53. The runtime will prefer the VTID if present
  @VTID(91)
  int getScreenColorDepth();


  /**
   * <p>
   * method NewWindow
   * </p>
   * @param xPos Mandatory int parameter.
   * @param yPos Mandatory int parameter.
   * @param width Mandatory int parameter.
   * @param height Mandatory int parameter.
   * @param flags Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(84) //= 0x54. The runtime will prefer the VTID if present
  @VTID(92)
  int newWindow(
    int xPos,
    int yPos,
    int width,
    int height,
    int flags);


  /**
   * <p>
   * method DeleteWindow
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(85) //= 0x55. The runtime will prefer the VTID if present
  @VTID(93)
  int deleteWindow();


  /**
   * <p>
   * method GetErrorMessage
   * </p>
   * @param errCode Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(86) //= 0x56. The runtime will prefer the VTID if present
  @VTID(94)
  java.lang.String getErrorMessage(
    int errCode);


  /**
   * <p>
   * method GetClientErrorMessage
   * </p>
   * @param errCode Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(87) //= 0x57. The runtime will prefer the VTID if present
  @VTID(95)
  java.lang.String getClientErrorMessage(
    int errCode);


  /**
   * <p>
   * method EnableKeyboardInput
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(88) //= 0x58. The runtime will prefer the VTID if present
  @VTID(96)
  int enableKeyboardInput();


  /**
   * <p>
   * method DisableKeyboardInput
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(89) //= 0x59. The runtime will prefer the VTID if present
  @VTID(97)
  int disableKeyboardInput();


  /**
   * <p>
   * method IsKeyboardInputEnabled
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(90) //= 0x5a. The runtime will prefer the VTID if present
  @VTID(98)
  boolean isKeyboardInputEnabled();


  /**
   * <p>
   * method EnableMouseInput
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(91) //= 0x5b. The runtime will prefer the VTID if present
  @VTID(99)
  int enableMouseInput();


  /**
   * <p>
   * method DisableMouseInput
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(92) //= 0x5c. The runtime will prefer the VTID if present
  @VTID(100)
  int disableMouseInput();


  /**
   * <p>
   * method IsMouseInputEnabled
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(93) //= 0x5d. The runtime will prefer the VTID if present
  @VTID(101)
  boolean isMouseInputEnabled();


  /**
   * <p>
   * method GetClientNetworkName
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(94) //= 0x5e. The runtime will prefer the VTID if present
  @VTID(102)
  java.lang.String getClientNetworkName();


  /**
   * <p>
   * method GetClientAddressCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(95) //= 0x5f. The runtime will prefer the VTID if present
  @VTID(103)
  int getClientAddressCount();


  /**
   * <p>
   * method GetClientAddress
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(96) //= 0x60. The runtime will prefer the VTID if present
  @VTID(104)
  java.lang.String getClientAddress(
    int index);


  /**
   * <p>
   * method AttachSession
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(97) //= 0x61. The runtime will prefer the VTID if present
  @VTID(105)
  int attachSession(
    java.lang.String pSessionId);


  /**
   * <p>
   * method DetachSession
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(98) //= 0x62. The runtime will prefer the VTID if present
  @VTID(106)
  int detachSession(
    java.lang.String pSessionId);


  /**
   * <p>
   * method GetCachedSessionCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(99) //= 0x63. The runtime will prefer the VTID if present
  @VTID(107)
  int getCachedSessionCount();


  /**
   * <p>
   * method IsSessionAttached
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(108)
  boolean isSessionAttached(
    java.lang.String pSessionId);


  /**
   * <p>
   * method IsSessionDetached
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(101) //= 0x65. The runtime will prefer the VTID if present
  @VTID(109)
  boolean isSessionDetached(
    java.lang.String pSessionId);


  /**
   * <p>
   * method IsSessionRunning
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(110)
  boolean isSessionRunning(
    java.lang.String pSessionId);


  /**
   * <p>
   * method SetSessionId
   * </p>
   * @param pSessionId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(103) //= 0x67. The runtime will prefer the VTID if present
  @VTID(111)
  int setSessionId(
    java.lang.String pSessionId);


  /**
   * <p>
   * Getter method for the COM property "ReadyState"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(-525) //= 0xfffffdf3. The runtime will prefer the VTID if present
  @VTID(112)
  int readyState();


  /**
   * <p>
   * Setter method for the COM property "ReadyState"
   * </p>
   * @param state Mandatory int parameter.
   */

  @DISPID(-525) //= 0xfffffdf3. The runtime will prefer the VTID if present
  @VTID(113)
  void readyState(
    int state);


  /**
   * <p>
   * property Address
   * </p>
   * <p>
   * Getter method for the COM property "Address"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1024) //= 0x400. The runtime will prefer the VTID if present
  @VTID(114)
  java.lang.String address();


  /**
   * <p>
   * property Address
   * </p>
   * <p>
   * Setter method for the COM property "Address"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1024) //= 0x400. The runtime will prefer the VTID if present
  @VTID(115)
  void address(
    java.lang.String pVal);


  /**
   * <p>
   * property Application
   * </p>
   * <p>
   * Getter method for the COM property "Application"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1025) //= 0x401. The runtime will prefer the VTID if present
  @VTID(116)
  java.lang.String application();


  /**
   * <p>
   * property Application
   * </p>
   * <p>
   * Setter method for the COM property "Application"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1025) //= 0x401. The runtime will prefer the VTID if present
  @VTID(117)
  void application(
    java.lang.String pVal);


  /**
   * <p>
   * property AudioBandwidthLimit
   * </p>
   * <p>
   * Getter method for the COM property "AudioBandwidthLimit"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICASoundQuality
   */

  @DISPID(1026) //= 0x402. The runtime will prefer the VTID if present
  @VTID(118)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICASoundQuality audioBandwidthLimit();


  /**
   * <p>
   * property AudioBandwidthLimit
   * </p>
   * <p>
   * Setter method for the COM property "AudioBandwidthLimit"
   * </p>
   * @param pVal Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICASoundQuality parameter.
   */

  @DISPID(1026) //= 0x402. The runtime will prefer the VTID if present
  @VTID(119)
  void audioBandwidthLimit(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICASoundQuality pVal);


  /**
   * <p>
   * property Border
   * </p>
   * <p>
   * Getter method for the COM property "Border"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1027) //= 0x403. The runtime will prefer the VTID if present
  @VTID(120)
  int border();


  /**
   * <p>
   * property Border
   * </p>
   * <p>
   * Setter method for the COM property "Border"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1027) //= 0x403. The runtime will prefer the VTID if present
  @VTID(121)
  void border(
    int pVal);


  /**
   * <p>
   * property CDMAllowed
   * </p>
   * <p>
   * Getter method for the COM property "CDMAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1028) //= 0x404. The runtime will prefer the VTID if present
  @VTID(122)
  boolean cdmAllowed();


  /**
   * <p>
   * property CDMAllowed
   * </p>
   * <p>
   * Setter method for the COM property "CDMAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1028) //= 0x404. The runtime will prefer the VTID if present
  @VTID(123)
  void cdmAllowed(
    boolean pVal);


  /**
   * <p>
   * property ClientAudio
   * </p>
   * <p>
   * Getter method for the COM property "ClientAudio"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1029) //= 0x405. The runtime will prefer the VTID if present
  @VTID(124)
  boolean clientAudio();


  /**
   * <p>
   * property ClientAudio
   * </p>
   * <p>
   * Setter method for the COM property "ClientAudio"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1029) //= 0x405. The runtime will prefer the VTID if present
  @VTID(125)
  void clientAudio(
    boolean pVal);


  /**
   * <p>
   * property ClientName
   * </p>
   * <p>
   * Getter method for the COM property "ClientName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1030) //= 0x406. The runtime will prefer the VTID if present
  @VTID(126)
  java.lang.String clientName();


  /**
   * <p>
   * property ClientName
   * </p>
   * <p>
   * Setter method for the COM property "ClientName"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1030) //= 0x406. The runtime will prefer the VTID if present
  @VTID(127)
  void clientName(
    java.lang.String pVal);


  /**
   * <p>
   * property COMAllowed
   * </p>
   * <p>
   * Getter method for the COM property "COMAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1031) //= 0x407. The runtime will prefer the VTID if present
  @VTID(128)
  boolean comAllowed();


  /**
   * <p>
   * property COMAllowed
   * </p>
   * <p>
   * Setter method for the COM property "COMAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1031) //= 0x407. The runtime will prefer the VTID if present
  @VTID(129)
  void comAllowed(
    boolean pVal);


  /**
   * <p>
   * property Compress
   * </p>
   * <p>
   * Getter method for the COM property "Compress"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1032) //= 0x408. The runtime will prefer the VTID if present
  @VTID(130)
  boolean compress();


  /**
   * <p>
   * property Compress
   * </p>
   * <p>
   * Setter method for the COM property "Compress"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1032) //= 0x408. The runtime will prefer the VTID if present
  @VTID(131)
  void compress(
    boolean pVal);


  /**
   * <p>
   * property Connected
   * </p>
   * <p>
   * Getter method for the COM property "Connected"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1033) //= 0x409. The runtime will prefer the VTID if present
  @VTID(132)
  boolean connected();


  /**
   * <p>
   * property ConnectionEntry
   * </p>
   * <p>
   * Getter method for the COM property "ConnectionEntry"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1034) //= 0x40a. The runtime will prefer the VTID if present
  @VTID(133)
  java.lang.String connectionEntry();


  /**
   * <p>
   * property ConnectionEntry
   * </p>
   * <p>
   * Setter method for the COM property "ConnectionEntry"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1034) //= 0x40a. The runtime will prefer the VTID if present
  @VTID(134)
  void connectionEntry(
    java.lang.String pVal);


  /**
   * <p>
   * property CPMAllowed
   * </p>
   * <p>
   * Getter method for the COM property "CPMAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1035) //= 0x40b. The runtime will prefer the VTID if present
  @VTID(135)
  boolean cpmAllowed();


  /**
   * <p>
   * property CPMAllowed
   * </p>
   * <p>
   * Setter method for the COM property "CPMAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1035) //= 0x40b. The runtime will prefer the VTID if present
  @VTID(136)
  void cpmAllowed(
    boolean pVal);


  /**
   * <p>
   * property CustomMessage
   * </p>
   * <p>
   * Getter method for the COM property "CustomMessage"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1036) //= 0x40c. The runtime will prefer the VTID if present
  @VTID(137)
  java.lang.String customMessage();


  /**
   * <p>
   * property CustomMessage
   * </p>
   * <p>
   * Setter method for the COM property "CustomMessage"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1036) //= 0x40c. The runtime will prefer the VTID if present
  @VTID(138)
  void customMessage(
    java.lang.String pVal);


  /**
   * <p>
   * property Description
   * </p>
   * <p>
   * Getter method for the COM property "Description"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1037) //= 0x40d. The runtime will prefer the VTID if present
  @VTID(139)
  java.lang.String description();


  /**
   * <p>
   * property Description
   * </p>
   * <p>
   * Setter method for the COM property "Description"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1037) //= 0x40d. The runtime will prefer the VTID if present
  @VTID(140)
  void description(
    java.lang.String pVal);


  /**
   * <p>
   * property DesiredColor
   * </p>
   * <p>
   * Getter method for the COM property "DesiredColor"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth
   */

  @DISPID(1038) //= 0x40e. The runtime will prefer the VTID if present
  @VTID(141)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth desiredColor();


  /**
   * <p>
   * property DesiredColor
   * </p>
   * <p>
   * Setter method for the COM property "DesiredColor"
   * </p>
   * @param pVal Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth parameter.
   */

  @DISPID(1038) //= 0x40e. The runtime will prefer the VTID if present
  @VTID(142)
  void desiredColor(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth pVal);


  /**
   * <p>
   * property DesiredHRes
   * </p>
   * <p>
   * Getter method for the COM property "DesiredHRes"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1039) //= 0x40f. The runtime will prefer the VTID if present
  @VTID(143)
  int desiredHRes();


  /**
   * <p>
   * property DesiredHRes
   * </p>
   * <p>
   * Setter method for the COM property "DesiredHRes"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1039) //= 0x40f. The runtime will prefer the VTID if present
  @VTID(144)
  void desiredHRes(
    int pVal);


  /**
   * <p>
   * property DesiredVRes
   * </p>
   * <p>
   * Getter method for the COM property "DesiredVRes"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1040) //= 0x410. The runtime will prefer the VTID if present
  @VTID(145)
  int desiredVRes();


  /**
   * <p>
   * property DesiredVRes
   * </p>
   * <p>
   * Setter method for the COM property "DesiredVRes"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1040) //= 0x410. The runtime will prefer the VTID if present
  @VTID(146)
  void desiredVRes(
    int pVal);


  /**
   * <p>
   * property Domain
   * </p>
   * <p>
   * Getter method for the COM property "Domain"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1041) //= 0x411. The runtime will prefer the VTID if present
  @VTID(147)
  java.lang.String domain();


  /**
   * <p>
   * property Domain
   * </p>
   * <p>
   * Setter method for the COM property "Domain"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1041) //= 0x411. The runtime will prefer the VTID if present
  @VTID(148)
  void domain(
    java.lang.String pVal);


  /**
   * <p>
   * property Encrypt
   * </p>
   * <p>
   * Getter method for the COM property "Encrypt"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1042) //= 0x412. The runtime will prefer the VTID if present
  @VTID(149)
  boolean encrypt();


  /**
   * <p>
   * property Encrypt
   * </p>
   * <p>
   * Setter method for the COM property "Encrypt"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1042) //= 0x412. The runtime will prefer the VTID if present
  @VTID(150)
  void encrypt(
    boolean pVal);


  /**
   * <p>
   * property Height
   * </p>
   * <p>
   * Getter method for the COM property "Height"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1043) //= 0x413. The runtime will prefer the VTID if present
  @VTID(151)
  int height();


  /**
   * <p>
   * property ICAFile
   * </p>
   * <p>
   * Getter method for the COM property "ICAFile"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1044) //= 0x414. The runtime will prefer the VTID if present
  @VTID(152)
  java.lang.String icaFile();


  /**
   * <p>
   * property ICAFile
   * </p>
   * <p>
   * Setter method for the COM property "ICAFile"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1044) //= 0x414. The runtime will prefer the VTID if present
  @VTID(153)
  void icaFile(
    java.lang.String pVal);


  /**
   * <p>
   * property IconIndex
   * </p>
   * <p>
   * Getter method for the COM property "IconIndex"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1045) //= 0x415. The runtime will prefer the VTID if present
  @VTID(154)
  int iconIndex();


  /**
   * <p>
   * property IconIndex
   * </p>
   * <p>
   * Setter method for the COM property "IconIndex"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1045) //= 0x415. The runtime will prefer the VTID if present
  @VTID(155)
  void iconIndex(
    int pVal);


  /**
   * <p>
   * property IconPath
   * </p>
   * <p>
   * Getter method for the COM property "IconPath"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1046) //= 0x416. The runtime will prefer the VTID if present
  @VTID(156)
  java.lang.String iconPath();


  /**
   * <p>
   * property IconPath
   * </p>
   * <p>
   * Setter method for the COM property "IconPath"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1046) //= 0x416. The runtime will prefer the VTID if present
  @VTID(157)
  void iconPath(
    java.lang.String pVal);


  /**
   * <p>
   * property InitialProgram
   * </p>
   * <p>
   * Getter method for the COM property "InitialProgram"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1047) //= 0x417. The runtime will prefer the VTID if present
  @VTID(158)
  java.lang.String initialProgram();


  /**
   * <p>
   * property InitialProgram
   * </p>
   * <p>
   * Setter method for the COM property "InitialProgram"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1047) //= 0x417. The runtime will prefer the VTID if present
  @VTID(159)
  void initialProgram(
    java.lang.String pVal);


  /**
   * <p>
   * property IPXBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "IPXBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1048) //= 0x418. The runtime will prefer the VTID if present
  @VTID(160)
  java.lang.String ipxBrowserAddress();


  /**
   * <p>
   * property IPXBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "IPXBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1048) //= 0x418. The runtime will prefer the VTID if present
  @VTID(161)
  void ipxBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property NetbiosBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "NetbiosBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1049) //= 0x419. The runtime will prefer the VTID if present
  @VTID(162)
  java.lang.String netbiosBrowserAddress();


  /**
   * <p>
   * property NetbiosBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "NetbiosBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1049) //= 0x419. The runtime will prefer the VTID if present
  @VTID(163)
  void netbiosBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property NotificationReason
   * </p>
   * <p>
   * Getter method for the COM property "NotificationReason"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICAEvent
   */

  @DISPID(1050) //= 0x41a. The runtime will prefer the VTID if present
  @VTID(164)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICAEvent notificationReason();


  /**
   * <p>
   * property PersistentCacheEnabled
   * </p>
   * <p>
   * Getter method for the COM property "PersistentCacheEnabled"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1051) //= 0x41b. The runtime will prefer the VTID if present
  @VTID(165)
  boolean persistentCacheEnabled();


  /**
   * <p>
   * property PersistentCacheEnabled
   * </p>
   * <p>
   * Setter method for the COM property "PersistentCacheEnabled"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1051) //= 0x41b. The runtime will prefer the VTID if present
  @VTID(166)
  void persistentCacheEnabled(
    boolean pVal);


  /**
   * <p>
   * property ProtocolSupport
   * </p>
   * <p>
   * Getter method for the COM property "ProtocolSupport"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1052) //= 0x41c. The runtime will prefer the VTID if present
  @VTID(167)
  java.lang.String protocolSupport();


  /**
   * <p>
   * property ProtocolSupport
   * </p>
   * <p>
   * Setter method for the COM property "ProtocolSupport"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1052) //= 0x41c. The runtime will prefer the VTID if present
  @VTID(168)
  void protocolSupport(
    java.lang.String pVal);


  /**
   * <p>
   * property Reliable
   * </p>
   * <p>
   * Getter method for the COM property "Reliable"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1053) //= 0x41d. The runtime will prefer the VTID if present
  @VTID(169)
  boolean reliable();


  /**
   * <p>
   * property Reliable
   * </p>
   * <p>
   * Setter method for the COM property "Reliable"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1053) //= 0x41d. The runtime will prefer the VTID if present
  @VTID(170)
  void reliable(
    boolean pVal);


  /**
   * <p>
   * property SessionEndAction
   * </p>
   * <p>
   * Getter method for the COM property "SessionEndAction"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction
   */

  @DISPID(1054) //= 0x41e. The runtime will prefer the VTID if present
  @VTID(171)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction sessionEndAction();


  /**
   * <p>
   * property SessionEndAction
   * </p>
   * <p>
   * Setter method for the COM property "SessionEndAction"
   * </p>
   * @param pVal Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction parameter.
   */

  @DISPID(1054) //= 0x41e. The runtime will prefer the VTID if present
  @VTID(172)
  void sessionEndAction(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICASessionEndAction pVal);


  /**
   * <p>
   * property Start
   * </p>
   * <p>
   * Getter method for the COM property "Start"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1055) //= 0x41f. The runtime will prefer the VTID if present
  @VTID(173)
  boolean start();


  /**
   * <p>
   * property Start
   * </p>
   * <p>
   * Setter method for the COM property "Start"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1055) //= 0x41f. The runtime will prefer the VTID if present
  @VTID(174)
  void start(
    boolean pVal);


  /**
   * <p>
   * property TCPBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "TCPBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1056) //= 0x420. The runtime will prefer the VTID if present
  @VTID(175)
  java.lang.String tcpBrowserAddress();


  /**
   * <p>
   * property TCPBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "TCPBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1056) //= 0x420. The runtime will prefer the VTID if present
  @VTID(176)
  void tcpBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property TransportDriver
   * </p>
   * <p>
   * Getter method for the COM property "TransportDriver"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1057) //= 0x421. The runtime will prefer the VTID if present
  @VTID(177)
  java.lang.String transportDriver();


  /**
   * <p>
   * property TransportDriver
   * </p>
   * <p>
   * Setter method for the COM property "TransportDriver"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1057) //= 0x421. The runtime will prefer the VTID if present
  @VTID(178)
  void transportDriver(
    java.lang.String pVal);


  /**
   * <p>
   * property UIActive
   * </p>
   * <p>
   * Getter method for the COM property "UIActive"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1058) //= 0x422. The runtime will prefer the VTID if present
  @VTID(179)
  boolean uiActive();


  /**
   * <p>
   * property UIActive
   * </p>
   * <p>
   * Setter method for the COM property "UIActive"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1058) //= 0x422. The runtime will prefer the VTID if present
  @VTID(180)
  void uiActive(
    boolean pVal);


  /**
   * <p>
   * property UpdatesAllowed
   * </p>
   * <p>
   * Getter method for the COM property "UpdatesAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1059) //= 0x423. The runtime will prefer the VTID if present
  @VTID(181)
  boolean updatesAllowed();


  /**
   * <p>
   * property UpdatesAllowed
   * </p>
   * <p>
   * Setter method for the COM property "UpdatesAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1059) //= 0x423. The runtime will prefer the VTID if present
  @VTID(182)
  void updatesAllowed(
    boolean pVal);


  /**
   * <p>
   * property Username
   * </p>
   * <p>
   * Getter method for the COM property "Username"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1060) //= 0x424. The runtime will prefer the VTID if present
  @VTID(183)
  java.lang.String username();


  /**
   * <p>
   * property Username
   * </p>
   * <p>
   * Setter method for the COM property "Username"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1060) //= 0x424. The runtime will prefer the VTID if present
  @VTID(184)
  void username(
    java.lang.String pVal);


  /**
   * <p>
   * property Version
   * </p>
   * <p>
   * Getter method for the COM property "Version"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1061) //= 0x425. The runtime will prefer the VTID if present
  @VTID(185)
  java.lang.String version();


  /**
   * <p>
   * property VSLAllowed
   * </p>
   * <p>
   * Getter method for the COM property "VSLAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1062) //= 0x426. The runtime will prefer the VTID if present
  @VTID(186)
  boolean vslAllowed();


  /**
   * <p>
   * property VSLAllowed
   * </p>
   * <p>
   * Setter method for the COM property "VSLAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1062) //= 0x426. The runtime will prefer the VTID if present
  @VTID(187)
  void vslAllowed(
    boolean pVal);


  /**
   * <p>
   * property Width
   * </p>
   * <p>
   * Getter method for the COM property "Width"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1063) //= 0x427. The runtime will prefer the VTID if present
  @VTID(188)
  int width();


  /**
   * <p>
   * property WinstationDriver
   * </p>
   * <p>
   * Getter method for the COM property "WinstationDriver"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1064) //= 0x428. The runtime will prefer the VTID if present
  @VTID(189)
  java.lang.String winstationDriver();


  /**
   * <p>
   * property WinstationDriver
   * </p>
   * <p>
   * Setter method for the COM property "WinstationDriver"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1064) //= 0x428. The runtime will prefer the VTID if present
  @VTID(190)
  void winstationDriver(
    java.lang.String pVal);


  /**
   * <p>
   * property WorkDirectory
   * </p>
   * <p>
   * Getter method for the COM property "WorkDirectory"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1065) //= 0x429. The runtime will prefer the VTID if present
  @VTID(191)
  java.lang.String workDirectory();


  /**
   * <p>
   * property WorkDirectory
   * </p>
   * <p>
   * Setter method for the COM property "WorkDirectory"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1065) //= 0x429. The runtime will prefer the VTID if present
  @VTID(192)
  void workDirectory(
    java.lang.String pVal);


  /**
   * <p>
   * property AppsrvIni
   * </p>
   * <p>
   * Getter method for the COM property "AppsrvIni"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1068) //= 0x42c. The runtime will prefer the VTID if present
  @VTID(193)
  java.lang.String appsrvIni();


  /**
   * <p>
   * property AppsrvIni
   * </p>
   * <p>
   * Setter method for the COM property "AppsrvIni"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1068) //= 0x42c. The runtime will prefer the VTID if present
  @VTID(194)
  void appsrvIni(
    java.lang.String pVal);


  /**
   * <p>
   * property ModuleIni
   * </p>
   * <p>
   * Getter method for the COM property "ModuleIni"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1069) //= 0x42d. The runtime will prefer the VTID if present
  @VTID(195)
  java.lang.String moduleIni();


  /**
   * <p>
   * property ModuleIni
   * </p>
   * <p>
   * Setter method for the COM property "ModuleIni"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1069) //= 0x42d. The runtime will prefer the VTID if present
  @VTID(196)
  void moduleIni(
    java.lang.String pVal);


  /**
   * <p>
   * property WfclientIni
   * </p>
   * <p>
   * Getter method for the COM property "WfclientIni"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1070) //= 0x42e. The runtime will prefer the VTID if present
  @VTID(197)
  java.lang.String wfclientIni();


  /**
   * <p>
   * property WfclientIni
   * </p>
   * <p>
   * Setter method for the COM property "WfclientIni"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1070) //= 0x42e. The runtime will prefer the VTID if present
  @VTID(198)
  void wfclientIni(
    java.lang.String pVal);


  /**
   * <p>
   * property ClientPath
   * </p>
   * <p>
   * Getter method for the COM property "ClientPath"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1071) //= 0x42f. The runtime will prefer the VTID if present
  @VTID(199)
  java.lang.String clientPath();


  /**
   * <p>
   * property ClientVersion
   * </p>
   * <p>
   * Getter method for the COM property "ClientVersion"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1072) //= 0x430. The runtime will prefer the VTID if present
  @VTID(200)
  java.lang.String clientVersion();


  /**
   * <p>
   * property LogAppend
   * </p>
   * <p>
   * Getter method for the COM property "LogAppend"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1075) //= 0x433. The runtime will prefer the VTID if present
  @VTID(201)
  boolean logAppend();


  /**
   * <p>
   * property LogAppend
   * </p>
   * <p>
   * Setter method for the COM property "LogAppend"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1075) //= 0x433. The runtime will prefer the VTID if present
  @VTID(202)
  void logAppend(
    boolean pVal);


  /**
   * <p>
   * property LogConnect
   * </p>
   * <p>
   * Getter method for the COM property "LogConnect"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1076) //= 0x434. The runtime will prefer the VTID if present
  @VTID(203)
  boolean logConnect();


  /**
   * <p>
   * property LogConnect
   * </p>
   * <p>
   * Setter method for the COM property "LogConnect"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1076) //= 0x434. The runtime will prefer the VTID if present
  @VTID(204)
  void logConnect(
    boolean pVal);


  /**
   * <p>
   * property LogErrors
   * </p>
   * <p>
   * Getter method for the COM property "LogErrors"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1077) //= 0x435. The runtime will prefer the VTID if present
  @VTID(205)
  boolean logErrors();


  /**
   * <p>
   * property LogErrors
   * </p>
   * <p>
   * Setter method for the COM property "LogErrors"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1077) //= 0x435. The runtime will prefer the VTID if present
  @VTID(206)
  void logErrors(
    boolean pVal);


  /**
   * <p>
   * property LogFile
   * </p>
   * <p>
   * Getter method for the COM property "LogFile"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1078) //= 0x436. The runtime will prefer the VTID if present
  @VTID(207)
  java.lang.String logFile();


  /**
   * <p>
   * property LogFile
   * </p>
   * <p>
   * Setter method for the COM property "LogFile"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1078) //= 0x436. The runtime will prefer the VTID if present
  @VTID(208)
  void logFile(
    java.lang.String pVal);


  /**
   * <p>
   * property LogFlush
   * </p>
   * <p>
   * Getter method for the COM property "LogFlush"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1079) //= 0x437. The runtime will prefer the VTID if present
  @VTID(209)
  boolean logFlush();


  /**
   * <p>
   * property LogFlush
   * </p>
   * <p>
   * Setter method for the COM property "LogFlush"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1079) //= 0x437. The runtime will prefer the VTID if present
  @VTID(210)
  void logFlush(
    boolean pVal);


  /**
   * <p>
   * property LogKeyboard
   * </p>
   * <p>
   * Getter method for the COM property "LogKeyboard"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1080) //= 0x438. The runtime will prefer the VTID if present
  @VTID(211)
  boolean logKeyboard();


  /**
   * <p>
   * property LogKeyboard
   * </p>
   * <p>
   * Setter method for the COM property "LogKeyboard"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1080) //= 0x438. The runtime will prefer the VTID if present
  @VTID(212)
  void logKeyboard(
    boolean pVal);


  /**
   * <p>
   * property LogReceive
   * </p>
   * <p>
   * Getter method for the COM property "LogReceive"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1081) //= 0x439. The runtime will prefer the VTID if present
  @VTID(213)
  boolean logReceive();


  /**
   * <p>
   * property LogReceive
   * </p>
   * <p>
   * Setter method for the COM property "LogReceive"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1081) //= 0x439. The runtime will prefer the VTID if present
  @VTID(214)
  void logReceive(
    boolean pVal);


  /**
   * <p>
   * property LogTransmit
   * </p>
   * <p>
   * Getter method for the COM property "LogTransmit"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1082) //= 0x43a. The runtime will prefer the VTID if present
  @VTID(215)
  boolean logTransmit();


  /**
   * <p>
   * property LogTransmit
   * </p>
   * <p>
   * Setter method for the COM property "LogTransmit"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1082) //= 0x43a. The runtime will prefer the VTID if present
  @VTID(216)
  void logTransmit(
    boolean pVal);


  /**
   * <p>
   * property Title
   * </p>
   * <p>
   * Getter method for the COM property "Title"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1073) //= 0x431. The runtime will prefer the VTID if present
  @VTID(217)
  java.lang.String title();


  /**
   * <p>
   * property Title
   * </p>
   * <p>
   * Setter method for the COM property "Title"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1073) //= 0x431. The runtime will prefer the VTID if present
  @VTID(218)
  void title(
    java.lang.String pVal);


  /**
   * <p>
   * property Launch
   * </p>
   * <p>
   * Getter method for the COM property "Launch"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1085) //= 0x43d. The runtime will prefer the VTID if present
  @VTID(219)
  boolean launch();


  /**
   * <p>
   * property Launch
   * </p>
   * <p>
   * Setter method for the COM property "Launch"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1085) //= 0x43d. The runtime will prefer the VTID if present
  @VTID(220)
  void launch(
    boolean pVal);


  /**
   * <p>
   * property BackgroundColor
   * </p>
   * <p>
   * Getter method for the COM property "BackgroundColor"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1087) //= 0x43f. The runtime will prefer the VTID if present
  @VTID(221)
  int backgroundColor();


  /**
   * <p>
   * property BackgroundColor
   * </p>
   * <p>
   * Setter method for the COM property "BackgroundColor"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1087) //= 0x43f. The runtime will prefer the VTID if present
  @VTID(222)
  void backgroundColor(
    int pVal);


  /**
   * <p>
   * property BorderColor
   * </p>
   * <p>
   * Getter method for the COM property "BorderColor"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1088) //= 0x440. The runtime will prefer the VTID if present
  @VTID(223)
  int borderColor();


  /**
   * <p>
   * property BorderColor
   * </p>
   * <p>
   * Setter method for the COM property "BorderColor"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1088) //= 0x440. The runtime will prefer the VTID if present
  @VTID(224)
  void borderColor(
    int pVal);


  /**
   * <p>
   * property TextColor
   * </p>
   * <p>
   * Getter method for the COM property "TextColor"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1089) //= 0x441. The runtime will prefer the VTID if present
  @VTID(225)
  int textColor();


  /**
   * <p>
   * property TextColor
   * </p>
   * <p>
   * Setter method for the COM property "TextColor"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1089) //= 0x441. The runtime will prefer the VTID if present
  @VTID(226)
  void textColor(
    int pVal);


  /**
   * <p>
   * property EncryptionLevelSession
   * </p>
   * <p>
   * Getter method for the COM property "EncryptionLevelSession"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1090) //= 0x442. The runtime will prefer the VTID if present
  @VTID(227)
  java.lang.String encryptionLevelSession();


  /**
   * <p>
   * property EncryptionLevelSession
   * </p>
   * <p>
   * Setter method for the COM property "EncryptionLevelSession"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1090) //= 0x442. The runtime will prefer the VTID if present
  @VTID(228)
  void encryptionLevelSession(
    java.lang.String pVal);


  /**
   * <p>
   * property HttpBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "HttpBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1091) //= 0x443. The runtime will prefer the VTID if present
  @VTID(229)
  java.lang.String httpBrowserAddress();


  /**
   * <p>
   * property HttpBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "HttpBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1091) //= 0x443. The runtime will prefer the VTID if present
  @VTID(230)
  void httpBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property BrowserProtocol
   * </p>
   * <p>
   * Getter method for the COM property "BrowserProtocol"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1102) //= 0x44e. The runtime will prefer the VTID if present
  @VTID(231)
  java.lang.String browserProtocol();


  /**
   * <p>
   * property BrowserProtocol
   * </p>
   * <p>
   * Setter method for the COM property "BrowserProtocol"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1102) //= 0x44e. The runtime will prefer the VTID if present
  @VTID(232)
  void browserProtocol(
    java.lang.String pVal);


  /**
   * <p>
   * property LocHTTPBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "LocHTTPBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1103) //= 0x44f. The runtime will prefer the VTID if present
  @VTID(233)
  java.lang.String locHTTPBrowserAddress();


  /**
   * <p>
   * property LocHTTPBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "LocHTTPBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1103) //= 0x44f. The runtime will prefer the VTID if present
  @VTID(234)
  void locHTTPBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property LocIPXBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "LocIPXBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1104) //= 0x450. The runtime will prefer the VTID if present
  @VTID(235)
  java.lang.String locIPXBrowserAddress();


  /**
   * <p>
   * property LocIPXBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "LocIPXBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1104) //= 0x450. The runtime will prefer the VTID if present
  @VTID(236)
  void locIPXBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property LocNETBIOSBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "LocNETBIOSBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1105) //= 0x451. The runtime will prefer the VTID if present
  @VTID(237)
  java.lang.String locNETBIOSBrowserAddress();


  /**
   * <p>
   * property LocNETBIOSBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "LocNETBIOSBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1105) //= 0x451. The runtime will prefer the VTID if present
  @VTID(238)
  void locNETBIOSBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property LocTCPBrowserAddress
   * </p>
   * <p>
   * Getter method for the COM property "LocTCPBrowserAddress"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1106) //= 0x452. The runtime will prefer the VTID if present
  @VTID(239)
  java.lang.String locTCPBrowserAddress();


  /**
   * <p>
   * property LocTCPBrowserAddress
   * </p>
   * <p>
   * Setter method for the COM property "LocTCPBrowserAddress"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1106) //= 0x452. The runtime will prefer the VTID if present
  @VTID(240)
  void locTCPBrowserAddress(
    java.lang.String pVal);


  /**
   * <p>
   * property DoNotUseDefaultCSL
   * </p>
   * <p>
   * Getter method for the COM property "DoNotUseDefaultCSL"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1107) //= 0x453. The runtime will prefer the VTID if present
  @VTID(241)
  boolean doNotUseDefaultCSL();


  /**
   * <p>
   * property DoNotUseDefaultCSL
   * </p>
   * <p>
   * Setter method for the COM property "DoNotUseDefaultCSL"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1107) //= 0x453. The runtime will prefer the VTID if present
  @VTID(242)
  void doNotUseDefaultCSL(
    boolean pVal);


  /**
   * <p>
   * property ICAPortNumber
   * </p>
   * <p>
   * Getter method for the COM property "ICAPortNumber"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1108) //= 0x454. The runtime will prefer the VTID if present
  @VTID(243)
  int icaPortNumber();


  /**
   * <p>
   * property ICAPortNumber
   * </p>
   * <p>
   * Setter method for the COM property "ICAPortNumber"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1108) //= 0x454. The runtime will prefer the VTID if present
  @VTID(244)
  void icaPortNumber(
    int pVal);


  /**
   * <p>
   * property KeyboardTimer
   * </p>
   * <p>
   * Getter method for the COM property "KeyboardTimer"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1109) //= 0x455. The runtime will prefer the VTID if present
  @VTID(245)
  int keyboardTimer();


  /**
   * <p>
   * property KeyboardTimer
   * </p>
   * <p>
   * Setter method for the COM property "KeyboardTimer"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1109) //= 0x455. The runtime will prefer the VTID if present
  @VTID(246)
  void keyboardTimer(
    int pVal);


  /**
   * <p>
   * property MouseTimer
   * </p>
   * <p>
   * Getter method for the COM property "MouseTimer"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1110) //= 0x456. The runtime will prefer the VTID if present
  @VTID(247)
  int mouseTimer();


  /**
   * <p>
   * property MouseTimer
   * </p>
   * <p>
   * Setter method for the COM property "MouseTimer"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1110) //= 0x456. The runtime will prefer the VTID if present
  @VTID(248)
  void mouseTimer(
    int pVal);


  /**
   * <p>
   * property Scrollbars
   * </p>
   * <p>
   * Getter method for the COM property "Scrollbars"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1111) //= 0x457. The runtime will prefer the VTID if present
  @VTID(249)
  boolean scrollbars();


  /**
   * <p>
   * property Scrollbars
   * </p>
   * <p>
   * Setter method for the COM property "Scrollbars"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1111) //= 0x457. The runtime will prefer the VTID if present
  @VTID(250)
  void scrollbars(
    boolean pVal);


  /**
   * <p>
   * property ScalingHeight
   * </p>
   * <p>
   * Getter method for the COM property "ScalingHeight"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1112) //= 0x458. The runtime will prefer the VTID if present
  @VTID(251)
  int scalingHeight();


  /**
   * <p>
   * property ScalingHeight
   * </p>
   * <p>
   * Setter method for the COM property "ScalingHeight"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1112) //= 0x458. The runtime will prefer the VTID if present
  @VTID(252)
  void scalingHeight(
    int pVal);


  /**
   * <p>
   * property ScalingMode
   * </p>
   * <p>
   * Getter method for the COM property "ScalingMode"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ICAScalingMode
   */

  @DISPID(1113) //= 0x459. The runtime will prefer the VTID if present
  @VTID(253)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ICAScalingMode scalingMode();


  /**
   * <p>
   * property ScalingMode
   * </p>
   * <p>
   * Setter method for the COM property "ScalingMode"
   * </p>
   * @param pVal Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.ICAScalingMode parameter.
   */

  @DISPID(1113) //= 0x459. The runtime will prefer the VTID if present
  @VTID(254)
  void scalingMode(
    com.blazemeter.jmeter.citrix.client.windows.com4j.ICAScalingMode pVal);


  /**
   * <p>
   * property ScalingPercent
   * </p>
   * <p>
   * Getter method for the COM property "ScalingPercent"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1114) //= 0x45a. The runtime will prefer the VTID if present
  @VTID(255)
  int scalingPercent();


  /**
   * <p>
   * property ScalingPercent
   * </p>
   * <p>
   * Setter method for the COM property "ScalingPercent"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1114) //= 0x45a. The runtime will prefer the VTID if present
  @VTID(256)
  void scalingPercent(
    int pVal);


  /**
   * <p>
   * property ScalingWidth
   * </p>
   * <p>
   * Getter method for the COM property "ScalingWidth"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1115) //= 0x45b. The runtime will prefer the VTID if present
  @VTID(257)
  int scalingWidth();


  /**
   * <p>
   * property ScalingWidth
   * </p>
   * <p>
   * Setter method for the COM property "ScalingWidth"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1115) //= 0x45b. The runtime will prefer the VTID if present
  @VTID(258)
  void scalingWidth(
    int pVal);


  /**
   * <p>
   * property VirtualChannels
   * </p>
   * <p>
   * Getter method for the COM property "VirtualChannels"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1116) //= 0x45c. The runtime will prefer the VTID if present
  @VTID(259)
  java.lang.String virtualChannels();


  /**
   * <p>
   * property VirtualChannels
   * </p>
   * <p>
   * Setter method for the COM property "VirtualChannels"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1116) //= 0x45c. The runtime will prefer the VTID if present
  @VTID(260)
  void virtualChannels(
    java.lang.String pVal);


  /**
   * <p>
   * property UseAlternateAddress
   * </p>
   * <p>
   * Getter method for the COM property "UseAlternateAddress"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1117) //= 0x45d. The runtime will prefer the VTID if present
  @VTID(261)
  int useAlternateAddress();


  /**
   * <p>
   * property UseAlternateAddress
   * </p>
   * <p>
   * Setter method for the COM property "UseAlternateAddress"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1117) //= 0x45d. The runtime will prefer the VTID if present
  @VTID(262)
  void useAlternateAddress(
    int pVal);


  /**
   * <p>
   * property BrowserRetry
   * </p>
   * <p>
   * Getter method for the COM property "BrowserRetry"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1118) //= 0x45e. The runtime will prefer the VTID if present
  @VTID(263)
  int browserRetry();


  /**
   * <p>
   * property BrowserRetry
   * </p>
   * <p>
   * Setter method for the COM property "BrowserRetry"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1118) //= 0x45e. The runtime will prefer the VTID if present
  @VTID(264)
  void browserRetry(
    int pVal);


  /**
   * <p>
   * property BrowserTimeout
   * </p>
   * <p>
   * Getter method for the COM property "BrowserTimeout"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1119) //= 0x45f. The runtime will prefer the VTID if present
  @VTID(265)
  int browserTimeout();


  /**
   * <p>
   * property BrowserTimeout
   * </p>
   * <p>
   * Setter method for the COM property "BrowserTimeout"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1119) //= 0x45f. The runtime will prefer the VTID if present
  @VTID(266)
  void browserTimeout(
    int pVal);


  /**
   * <p>
   * property LanaNumber
   * </p>
   * <p>
   * Getter method for the COM property "LanaNumber"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1120) //= 0x460. The runtime will prefer the VTID if present
  @VTID(267)
  int lanaNumber();


  /**
   * <p>
   * property LanaNumber
   * </p>
   * <p>
   * Setter method for the COM property "LanaNumber"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1120) //= 0x460. The runtime will prefer the VTID if present
  @VTID(268)
  void lanaNumber(
    int pVal);


  /**
   * <p>
   * property ICASOCKSProtocolVersion
   * </p>
   * <p>
   * Getter method for the COM property "ICASOCKSProtocolVersion"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1121) //= 0x461. The runtime will prefer the VTID if present
  @VTID(269)
  int icasocksProtocolVersion();


  /**
   * <p>
   * property ICASOCKSProtocolVersion
   * </p>
   * <p>
   * Setter method for the COM property "ICASOCKSProtocolVersion"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1121) //= 0x461. The runtime will prefer the VTID if present
  @VTID(270)
  void icasocksProtocolVersion(
    int pVal);


  /**
   * <p>
   * property ICASOCKSProxyHost
   * </p>
   * <p>
   * Getter method for the COM property "ICASOCKSProxyHost"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1122) //= 0x462. The runtime will prefer the VTID if present
  @VTID(271)
  java.lang.String icasocksProxyHost();


  /**
   * <p>
   * property ICASOCKSProxyHost
   * </p>
   * <p>
   * Setter method for the COM property "ICASOCKSProxyHost"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1122) //= 0x462. The runtime will prefer the VTID if present
  @VTID(272)
  void icasocksProxyHost(
    java.lang.String pVal);


  /**
   * <p>
   * property ICASOCKSProxyPortNumber
   * </p>
   * <p>
   * Getter method for the COM property "ICASOCKSProxyPortNumber"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1123) //= 0x463. The runtime will prefer the VTID if present
  @VTID(273)
  int icasocksProxyPortNumber();


  /**
   * <p>
   * property ICASOCKSProxyPortNumber
   * </p>
   * <p>
   * Setter method for the COM property "ICASOCKSProxyPortNumber"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1123) //= 0x463. The runtime will prefer the VTID if present
  @VTID(274)
  void icasocksProxyPortNumber(
    int pVal);


  /**
   * <p>
   * property ICASOCKSRFC1929Username
   * </p>
   * <p>
   * Getter method for the COM property "ICASOCKSRFC1929Username"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1124) //= 0x464. The runtime will prefer the VTID if present
  @VTID(275)
  java.lang.String icasocksrfC1929Username();


  /**
   * <p>
   * property ICASOCKSRFC1929Username
   * </p>
   * <p>
   * Setter method for the COM property "ICASOCKSRFC1929Username"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1124) //= 0x464. The runtime will prefer the VTID if present
  @VTID(276)
  void icasocksrfC1929Username(
    java.lang.String pVal);


  /**
   * <p>
   * property ICASOCKSTimeout
   * </p>
   * <p>
   * Getter method for the COM property "ICASOCKSTimeout"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1126) //= 0x466. The runtime will prefer the VTID if present
  @VTID(277)
  int icasocksTimeout();


  /**
   * <p>
   * property ICASOCKSTimeout
   * </p>
   * <p>
   * Setter method for the COM property "ICASOCKSTimeout"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1126) //= 0x466. The runtime will prefer the VTID if present
  @VTID(278)
  void icasocksTimeout(
    int pVal);


  /**
   * <p>
   * property SSLEnable
   * </p>
   * <p>
   * Getter method for the COM property "SSLEnable"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1127) //= 0x467. The runtime will prefer the VTID if present
  @VTID(279)
  boolean sslEnable();


  /**
   * <p>
   * property SSLEnable
   * </p>
   * <p>
   * Setter method for the COM property "SSLEnable"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1127) //= 0x467. The runtime will prefer the VTID if present
  @VTID(280)
  void sslEnable(
    boolean pVal);


  /**
   * <p>
   * property SSLProxyHost
   * </p>
   * <p>
   * Getter method for the COM property "SSLProxyHost"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1128) //= 0x468. The runtime will prefer the VTID if present
  @VTID(281)
  java.lang.String sslProxyHost();


  /**
   * <p>
   * property SSLProxyHost
   * </p>
   * <p>
   * Setter method for the COM property "SSLProxyHost"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1128) //= 0x468. The runtime will prefer the VTID if present
  @VTID(282)
  void sslProxyHost(
    java.lang.String pVal);


  /**
   * <p>
   * property SSLCiphers
   * </p>
   * <p>
   * Getter method for the COM property "SSLCiphers"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1129) //= 0x469. The runtime will prefer the VTID if present
  @VTID(283)
  java.lang.String sslCiphers();


  /**
   * <p>
   * property SSLCiphers
   * </p>
   * <p>
   * Setter method for the COM property "SSLCiphers"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1129) //= 0x469. The runtime will prefer the VTID if present
  @VTID(284)
  void sslCiphers(
    java.lang.String pVal);


  /**
   * <p>
   * property SSLNoCACerts
   * </p>
   * <p>
   * Getter method for the COM property "SSLNoCACerts"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1130) //= 0x46a. The runtime will prefer the VTID if present
  @VTID(285)
  int sslNoCACerts();


  /**
   * <p>
   * property SSLNoCACerts
   * </p>
   * <p>
   * Setter method for the COM property "SSLNoCACerts"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1130) //= 0x46a. The runtime will prefer the VTID if present
  @VTID(286)
  void sslNoCACerts(
    int pVal);


  /**
   * <p>
   * property SSLCommonName
   * </p>
   * <p>
   * Getter method for the COM property "SSLCommonName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1131) //= 0x46b. The runtime will prefer the VTID if present
  @VTID(287)
  java.lang.String sslCommonName();


  /**
   * <p>
   * property SSLCommonName
   * </p>
   * <p>
   * Setter method for the COM property "SSLCommonName"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1131) //= 0x46b. The runtime will prefer the VTID if present
  @VTID(288)
  void sslCommonName(
    java.lang.String pVal);


  /**
   * <p>
   * property AUTHUsername
   * </p>
   * <p>
   * Getter method for the COM property "AUTHUsername"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1132) //= 0x46c. The runtime will prefer the VTID if present
  @VTID(289)
  java.lang.String authUsername();


  /**
   * <p>
   * property AUTHUsername
   * </p>
   * <p>
   * Setter method for the COM property "AUTHUsername"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1132) //= 0x46c. The runtime will prefer the VTID if present
  @VTID(290)
  void authUsername(
    java.lang.String pVal);


  /**
   * <p>
   * property XmlAddressResolutionType
   * </p>
   * <p>
   * Getter method for the COM property "XmlAddressResolutionType"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1134) //= 0x46e. The runtime will prefer the VTID if present
  @VTID(291)
  java.lang.String xmlAddressResolutionType();


  /**
   * <p>
   * property XmlAddressResolutionType
   * </p>
   * <p>
   * Setter method for the COM property "XmlAddressResolutionType"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1134) //= 0x46e. The runtime will prefer the VTID if present
  @VTID(292)
  void xmlAddressResolutionType(
    java.lang.String pVal);


  /**
   * <p>
   * property AutoScale
   * </p>
   * <p>
   * Getter method for the COM property "AutoScale"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1135) //= 0x46f. The runtime will prefer the VTID if present
  @VTID(293)
  boolean autoScale();


  /**
   * <p>
   * property AutoScale
   * </p>
   * <p>
   * Setter method for the COM property "AutoScale"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1135) //= 0x46f. The runtime will prefer the VTID if present
  @VTID(294)
  void autoScale(
    boolean pVal);


  /**
   * <p>
   * property AutoAppResize
   * </p>
   * <p>
   * Getter method for the COM property "AutoAppResize"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1136) //= 0x470. The runtime will prefer the VTID if present
  @VTID(295)
  boolean autoAppResize();


  /**
   * <p>
   * property AutoAppResize
   * </p>
   * <p>
   * Setter method for the COM property "AutoAppResize"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1136) //= 0x470. The runtime will prefer the VTID if present
  @VTID(296)
  void autoAppResize(
    boolean pVal);


  /**
   * <p>
   * property Hotkey1Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey1Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1139) //= 0x473. The runtime will prefer the VTID if present
  @VTID(297)
  java.lang.String hotkey1Char();


  /**
   * <p>
   * property Hotkey1Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey1Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1139) //= 0x473. The runtime will prefer the VTID if present
  @VTID(298)
  void hotkey1Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey1Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey1Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1140) //= 0x474. The runtime will prefer the VTID if present
  @VTID(299)
  java.lang.String hotkey1Shift();


  /**
   * <p>
   * property Hotkey1Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey1Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1140) //= 0x474. The runtime will prefer the VTID if present
  @VTID(300)
  void hotkey1Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey2Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey2Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1141) //= 0x475. The runtime will prefer the VTID if present
  @VTID(301)
  java.lang.String hotkey2Char();


  /**
   * <p>
   * property Hotkey2Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey2Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1141) //= 0x475. The runtime will prefer the VTID if present
  @VTID(302)
  void hotkey2Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey2Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey2Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1142) //= 0x476. The runtime will prefer the VTID if present
  @VTID(303)
  java.lang.String hotkey2Shift();


  /**
   * <p>
   * property Hotkey2Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey2Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1142) //= 0x476. The runtime will prefer the VTID if present
  @VTID(304)
  void hotkey2Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey3Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey3Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1143) //= 0x477. The runtime will prefer the VTID if present
  @VTID(305)
  java.lang.String hotkey3Char();


  /**
   * <p>
   * property Hotkey3Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey3Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1143) //= 0x477. The runtime will prefer the VTID if present
  @VTID(306)
  void hotkey3Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey3Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey3Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1144) //= 0x478. The runtime will prefer the VTID if present
  @VTID(307)
  java.lang.String hotkey3Shift();


  /**
   * <p>
   * property Hotkey3Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey3Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1144) //= 0x478. The runtime will prefer the VTID if present
  @VTID(308)
  void hotkey3Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey4Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey4Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1145) //= 0x479. The runtime will prefer the VTID if present
  @VTID(309)
  java.lang.String hotkey4Char();


  /**
   * <p>
   * property Hotkey4Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey4Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1145) //= 0x479. The runtime will prefer the VTID if present
  @VTID(310)
  void hotkey4Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey4Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey4Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1146) //= 0x47a. The runtime will prefer the VTID if present
  @VTID(311)
  java.lang.String hotkey4Shift();


  /**
   * <p>
   * property Hotkey4Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey4Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1146) //= 0x47a. The runtime will prefer the VTID if present
  @VTID(312)
  void hotkey4Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey5Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey5Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1147) //= 0x47b. The runtime will prefer the VTID if present
  @VTID(313)
  java.lang.String hotkey5Char();


  /**
   * <p>
   * property Hotkey5Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey5Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1147) //= 0x47b. The runtime will prefer the VTID if present
  @VTID(314)
  void hotkey5Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey5Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey5Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1148) //= 0x47c. The runtime will prefer the VTID if present
  @VTID(315)
  java.lang.String hotkey5Shift();


  /**
   * <p>
   * property Hotkey5Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey5Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1148) //= 0x47c. The runtime will prefer the VTID if present
  @VTID(316)
  void hotkey5Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey6Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey6Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1149) //= 0x47d. The runtime will prefer the VTID if present
  @VTID(317)
  java.lang.String hotkey6Char();


  /**
   * <p>
   * property Hotkey6Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey6Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1149) //= 0x47d. The runtime will prefer the VTID if present
  @VTID(318)
  void hotkey6Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey6Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey6Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1150) //= 0x47e. The runtime will prefer the VTID if present
  @VTID(319)
  java.lang.String hotkey6Shift();


  /**
   * <p>
   * property Hotkey6Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey6Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1150) //= 0x47e. The runtime will prefer the VTID if present
  @VTID(320)
  void hotkey6Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey7Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey7Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1196) //= 0x4ac. The runtime will prefer the VTID if present
  @VTID(321)
  java.lang.String hotkey7Char();


  /**
   * <p>
   * property Hotkey7Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey7Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1196) //= 0x4ac. The runtime will prefer the VTID if present
  @VTID(322)
  void hotkey7Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey7Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey7Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1152) //= 0x480. The runtime will prefer the VTID if present
  @VTID(323)
  java.lang.String hotkey7Shift();


  /**
   * <p>
   * property Hotkey7Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey7Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1152) //= 0x480. The runtime will prefer the VTID if present
  @VTID(324)
  void hotkey7Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey8Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey8Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1153) //= 0x481. The runtime will prefer the VTID if present
  @VTID(325)
  java.lang.String hotkey8Char();


  /**
   * <p>
   * property Hotkey8Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey8Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1153) //= 0x481. The runtime will prefer the VTID if present
  @VTID(326)
  void hotkey8Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey8Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey8Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1154) //= 0x482. The runtime will prefer the VTID if present
  @VTID(327)
  java.lang.String hotkey8Shift();


  /**
   * <p>
   * property Hotkey8Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey8Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1154) //= 0x482. The runtime will prefer the VTID if present
  @VTID(328)
  void hotkey8Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey9Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey9Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1155) //= 0x483. The runtime will prefer the VTID if present
  @VTID(329)
  java.lang.String hotkey9Char();


  /**
   * <p>
   * property Hotkey9Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey9Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1155) //= 0x483. The runtime will prefer the VTID if present
  @VTID(330)
  void hotkey9Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey9Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey9Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1156) //= 0x484. The runtime will prefer the VTID if present
  @VTID(331)
  java.lang.String hotkey9Shift();


  /**
   * <p>
   * property Hotkey9Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey9Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1156) //= 0x484. The runtime will prefer the VTID if present
  @VTID(332)
  void hotkey9Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey10Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey10Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1157) //= 0x485. The runtime will prefer the VTID if present
  @VTID(333)
  java.lang.String hotkey10Char();


  /**
   * <p>
   * property Hotkey10Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey10Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1157) //= 0x485. The runtime will prefer the VTID if present
  @VTID(334)
  void hotkey10Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey10Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey10Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1158) //= 0x486. The runtime will prefer the VTID if present
  @VTID(335)
  java.lang.String hotkey10Shift();


  /**
   * <p>
   * property Hotkey10Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey10Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1158) //= 0x486. The runtime will prefer the VTID if present
  @VTID(336)
  void hotkey10Shift(
    java.lang.String pVal);


  /**
   * <p>
   * property ControlWindowText
   * </p>
   * <p>
   * Getter method for the COM property "ControlWindowText"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1159) //= 0x487. The runtime will prefer the VTID if present
  @VTID(337)
  java.lang.String controlWindowText();


  /**
   * <p>
   * property ControlWindowText
   * </p>
   * <p>
   * Setter method for the COM property "ControlWindowText"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1159) //= 0x487. The runtime will prefer the VTID if present
  @VTID(338)
  void controlWindowText(
    java.lang.String pVal);


  /**
   * <p>
   * property CacheICAFile
   * </p>
   * <p>
   * Getter method for the COM property "CacheICAFile"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1160) //= 0x488. The runtime will prefer the VTID if present
  @VTID(339)
  boolean cacheICAFile();


  /**
   * <p>
   * property CacheICAFile
   * </p>
   * <p>
   * Setter method for the COM property "CacheICAFile"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1160) //= 0x488. The runtime will prefer the VTID if present
  @VTID(340)
  void cacheICAFile(
    boolean pVal);


  /**
   * <p>
   * property ScreenPercent
   * </p>
   * <p>
   * Getter method for the COM property "ScreenPercent"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1161) //= 0x489. The runtime will prefer the VTID if present
  @VTID(341)
  int screenPercent();


  /**
   * <p>
   * property ScreenPercent
   * </p>
   * <p>
   * Setter method for the COM property "ScreenPercent"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1161) //= 0x489. The runtime will prefer the VTID if present
  @VTID(342)
  void screenPercent(
    int pVal);


  /**
   * <p>
   * property TWIMode
   * </p>
   * <p>
   * Getter method for the COM property "TWIMode"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1163) //= 0x48b. The runtime will prefer the VTID if present
  @VTID(343)
  boolean twiMode();


  /**
   * <p>
   * property TWIMode
   * </p>
   * <p>
   * Setter method for the COM property "TWIMode"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1163) //= 0x48b. The runtime will prefer the VTID if present
  @VTID(344)
  void twiMode(
    boolean pVal);


  /**
   * <p>
   * property TransportReconnectEnabled
   * </p>
   * <p>
   * Getter method for the COM property "TransportReconnectEnabled"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1164) //= 0x48c. The runtime will prefer the VTID if present
  @VTID(345)
  boolean transportReconnectEnabled();


  /**
   * <p>
   * property TransportReconnectEnabled
   * </p>
   * <p>
   * Setter method for the COM property "TransportReconnectEnabled"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1164) //= 0x48c. The runtime will prefer the VTID if present
  @VTID(346)
  void transportReconnectEnabled(
    boolean pVal);


  /**
   * <p>
   * property TransportReconnectDelay
   * </p>
   * <p>
   * Getter method for the COM property "TransportReconnectDelay"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1165) //= 0x48d. The runtime will prefer the VTID if present
  @VTID(347)
  int transportReconnectDelay();


  /**
   * <p>
   * property TransportReconnectDelay
   * </p>
   * <p>
   * Setter method for the COM property "TransportReconnectDelay"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1165) //= 0x48d. The runtime will prefer the VTID if present
  @VTID(348)
  void transportReconnectDelay(
    int pVal);


  /**
   * <p>
   * property TransportReconnectRetries
   * </p>
   * <p>
   * Getter method for the COM property "TransportReconnectRetries"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1166) //= 0x48e. The runtime will prefer the VTID if present
  @VTID(349)
  int transportReconnectRetries();


  /**
   * <p>
   * property TransportReconnectRetries
   * </p>
   * <p>
   * Setter method for the COM property "TransportReconnectRetries"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1166) //= 0x48e. The runtime will prefer the VTID if present
  @VTID(350)
  void transportReconnectRetries(
    int pVal);


  /**
   * <p>
   * property AutoLogonAllowed
   * </p>
   * <p>
   * Getter method for the COM property "AutoLogonAllowed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1167) //= 0x48f. The runtime will prefer the VTID if present
  @VTID(351)
  boolean autoLogonAllowed();


  /**
   * <p>
   * property AutoLogonAllowed
   * </p>
   * <p>
   * Setter method for the COM property "AutoLogonAllowed"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1167) //= 0x48f. The runtime will prefer the VTID if present
  @VTID(352)
  void autoLogonAllowed(
    boolean pVal);


  /**
   * <p>
   * property EnableSessionSharingClient
   * </p>
   * <p>
   * Getter method for the COM property "EnableSessionSharingClient"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1168) //= 0x490. The runtime will prefer the VTID if present
  @VTID(353)
  boolean enableSessionSharingClient();


  /**
   * <p>
   * property EnableSessionSharingClient
   * </p>
   * <p>
   * Setter method for the COM property "EnableSessionSharingClient"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1168) //= 0x490. The runtime will prefer the VTID if present
  @VTID(354)
  void enableSessionSharingClient(
    boolean pVal);


  /**
   * <p>
   * property SessionSharingName
   * </p>
   * <p>
   * Getter method for the COM property "SessionSharingName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1169) //= 0x491. The runtime will prefer the VTID if present
  @VTID(355)
  java.lang.String sessionSharingName();


  /**
   * <p>
   * property SessionSharingName
   * </p>
   * <p>
   * Setter method for the COM property "SessionSharingName"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1169) //= 0x491. The runtime will prefer the VTID if present
  @VTID(356)
  void sessionSharingName(
    java.lang.String pVal);


  /**
   * <p>
   * property SessionSharingLaunchOnly
   * </p>
   * <p>
   * Getter method for the COM property "SessionSharingLaunchOnly"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1170) //= 0x492. The runtime will prefer the VTID if present
  @VTID(357)
  boolean sessionSharingLaunchOnly();


  /**
   * <p>
   * property SessionSharingLaunchOnly
   * </p>
   * <p>
   * Setter method for the COM property "SessionSharingLaunchOnly"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1170) //= 0x492. The runtime will prefer the VTID if present
  @VTID(358)
  void sessionSharingLaunchOnly(
    boolean pVal);


  /**
   * <p>
   * property DisableCtrlAltDel
   * </p>
   * <p>
   * Getter method for the COM property "DisableCtrlAltDel"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1171) //= 0x493. The runtime will prefer the VTID if present
  @VTID(359)
  boolean disableCtrlAltDel();


  /**
   * <p>
   * property DisableCtrlAltDel
   * </p>
   * <p>
   * Setter method for the COM property "DisableCtrlAltDel"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1171) //= 0x493. The runtime will prefer the VTID if present
  @VTID(360)
  void disableCtrlAltDel(
    boolean pVal);


  /**
   * <p>
   * property SessionCacheEnable
   * </p>
   * <p>
   * Getter method for the COM property "SessionCacheEnable"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1172) //= 0x494. The runtime will prefer the VTID if present
  @VTID(361)
  boolean sessionCacheEnable();


  /**
   * <p>
   * property SessionCacheEnable
   * </p>
   * <p>
   * Setter method for the COM property "SessionCacheEnable"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1172) //= 0x494. The runtime will prefer the VTID if present
  @VTID(362)
  void sessionCacheEnable(
    boolean pVal);


  /**
   * <p>
   * property SessionCacheTimeout
   * </p>
   * <p>
   * Getter method for the COM property "SessionCacheTimeout"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1173) //= 0x495. The runtime will prefer the VTID if present
  @VTID(363)
  int sessionCacheTimeout();


  /**
   * <p>
   * property SessionCacheTimeout
   * </p>
   * <p>
   * Setter method for the COM property "SessionCacheTimeout"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1173) //= 0x495. The runtime will prefer the VTID if present
  @VTID(364)
  void sessionCacheTimeout(
    int pVal);


  /**
   * <p>
   * property session
   * </p>
   * <p>
   * Getter method for the COM property "Session"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.ISession
   */

  @DISPID(1175) //= 0x497. The runtime will prefer the VTID if present
  @VTID(365)
  com.blazemeter.jmeter.citrix.client.windows.com4j.ISession session();


  /**
   * <p>
   * property OutputMode
   * </p>
   * <p>
   * Getter method for the COM property "OutputMode"
   * </p>
   * @return  Returns a value of type com.blazemeter.jmeter.citrix.client.windows.com4j.OutputMode
   */

  @DISPID(1176) //= 0x498. The runtime will prefer the VTID if present
  @VTID(366)
  com.blazemeter.jmeter.citrix.client.windows.com4j.OutputMode outputMode();


  /**
   * <p>
   * property OutputMode
   * </p>
   * <p>
   * Setter method for the COM property "OutputMode"
   * </p>
   * @param pVal Mandatory com.blazemeter.jmeter.citrix.client.windows.com4j.OutputMode parameter.
   */

  @DISPID(1176) //= 0x498. The runtime will prefer the VTID if present
  @VTID(367)
  void outputMode(
    com.blazemeter.jmeter.citrix.client.windows.com4j.OutputMode pVal);


  /**
   * <p>
   * property SessionExitTimeout
   * </p>
   * <p>
   * Getter method for the COM property "SessionExitTimeout"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1178) //= 0x49a. The runtime will prefer the VTID if present
  @VTID(368)
  int sessionExitTimeout();


  /**
   * <p>
   * property SessionExitTimeout
   * </p>
   * <p>
   * Setter method for the COM property "SessionExitTimeout"
   * </p>
   * @param pVal Mandatory int parameter.
   */

  @DISPID(1178) //= 0x49a. The runtime will prefer the VTID if present
  @VTID(369)
  void sessionExitTimeout(
    int pVal);


  /**
   * <p>
   * property EnableSessionSharingHost
   * </p>
   * <p>
   * Getter method for the COM property "EnableSessionSharingHost"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1179) //= 0x49b. The runtime will prefer the VTID if present
  @VTID(370)
  boolean enableSessionSharingHost();


  /**
   * <p>
   * property EnableSessionSharingHost
   * </p>
   * <p>
   * Setter method for the COM property "EnableSessionSharingHost"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1179) //= 0x49b. The runtime will prefer the VTID if present
  @VTID(371)
  void enableSessionSharingHost(
    boolean pVal);


  /**
   * <p>
   * property LongCommandLine
   * </p>
   * <p>
   * Getter method for the COM property "LongCommandLine"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1180) //= 0x49c. The runtime will prefer the VTID if present
  @VTID(372)
  java.lang.String longCommandLine();


  /**
   * <p>
   * property LongCommandLine
   * </p>
   * <p>
   * Setter method for the COM property "LongCommandLine"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1180) //= 0x49c. The runtime will prefer the VTID if present
  @VTID(373)
  void longCommandLine(
    java.lang.String pVal);


  /**
   * <p>
   * property TWIDisableSessionSharing
   * </p>
   * <p>
   * Getter method for the COM property "TWIDisableSessionSharing"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1181) //= 0x49d. The runtime will prefer the VTID if present
  @VTID(374)
  boolean twiDisableSessionSharing();


  /**
   * <p>
   * property TWIDisableSessionSharing
   * </p>
   * <p>
   * Setter method for the COM property "TWIDisableSessionSharing"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1181) //= 0x49d. The runtime will prefer the VTID if present
  @VTID(375)
  void twiDisableSessionSharing(
    boolean pVal);


  /**
   * <p>
   * property SessionSharingKey
   * </p>
   * <p>
   * Getter method for the COM property "SessionSharingKey"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1182) //= 0x49e. The runtime will prefer the VTID if present
  @VTID(376)
  java.lang.String sessionSharingKey();


  /**
   * <p>
   * property SessionSharingKey
   * </p>
   * <p>
   * Setter method for the COM property "SessionSharingKey"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1182) //= 0x49e. The runtime will prefer the VTID if present
  @VTID(377)
  void sessionSharingKey(
    java.lang.String pVal);


  /**
   * <p>
   * method DisconnectSessions
   * </p>
   * @param pGroupId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(104) //= 0x68. The runtime will prefer the VTID if present
  @VTID(378)
  int disconnectSessions(
    java.lang.String pGroupId);


  /**
   * <p>
   * method LogoffSessions
   * </p>
   * @param pGroupId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(105) //= 0x69. The runtime will prefer the VTID if present
  @VTID(379)
  int logoffSessions(
    java.lang.String pGroupId);


  /**
   * <p>
   * method SetSessionGroupId
   * </p>
   * @param pGroupId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(106) //= 0x6a. The runtime will prefer the VTID if present
  @VTID(380)
  int setSessionGroupId(
    java.lang.String pGroupId);


  /**
   * <p>
   * method GetSessionHandle
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(107) //= 0x6b. The runtime will prefer the VTID if present
  @VTID(381)
  int getSessionHandle();


  /**
   * <p>
   * method SwitchSession
   * </p>
   * @param hSession Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(108) //= 0x6c. The runtime will prefer the VTID if present
  @VTID(382)
  int switchSession(
    int hSession);


  /**
   * <p>
   * method GetSessionCount
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(109) //= 0x6d. The runtime will prefer the VTID if present
  @VTID(383)
  int getSessionCount();


  /**
   * <p>
   * method GetSessionHandleByIndex
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(110) //= 0x6e. The runtime will prefer the VTID if present
  @VTID(384)
  int getSessionHandleByIndex(
    int index);


  /**
   * <p>
   * method GetSessionGroupCount
   * </p>
   * @param pGroupId Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(111) //= 0x6f. The runtime will prefer the VTID if present
  @VTID(385)
  int getSessionGroupCount(
    java.lang.String pGroupId);


  /**
   * <p>
   * property IPCLaunch
   * </p>
   * <p>
   * Getter method for the COM property "IPCLaunch"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1191) //= 0x4a7. The runtime will prefer the VTID if present
  @VTID(386)
  boolean ipcLaunch();


  /**
   * <p>
   * property IPCLaunch
   * </p>
   * <p>
   * Setter method for the COM property "IPCLaunch"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1191) //= 0x4a7. The runtime will prefer the VTID if present
  @VTID(387)
  void ipcLaunch(
    boolean pVal);


  /**
   * <p>
   * property AudioDuringDetach
   * </p>
   * <p>
   * Getter method for the COM property "AudioDuringDetach"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1192) //= 0x4a8. The runtime will prefer the VTID if present
  @VTID(388)
  boolean audioDuringDetach();


  /**
   * <p>
   * property AudioDuringDetach
   * </p>
   * <p>
   * Setter method for the COM property "AudioDuringDetach"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1192) //= 0x4a8. The runtime will prefer the VTID if present
  @VTID(389)
  void audioDuringDetach(
    boolean pVal);


  /**
   * <p>
   * property Hotkey11Char
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey11Char"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1193) //= 0x4a9. The runtime will prefer the VTID if present
  @VTID(390)
  java.lang.String hotkey11Char();


  /**
   * <p>
   * property Hotkey11Char
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey11Char"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1193) //= 0x4a9. The runtime will prefer the VTID if present
  @VTID(391)
  void hotkey11Char(
    java.lang.String pVal);


  /**
   * <p>
   * property Hotkey11Shift
   * </p>
   * <p>
   * Getter method for the COM property "Hotkey11Shift"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1194) //= 0x4aa. The runtime will prefer the VTID if present
  @VTID(392)
  java.lang.String hotkey11Shift();


  /**
   * <p>
   * property Hotkey11Shift
   * </p>
   * <p>
   * Setter method for the COM property "Hotkey11Shift"
   * </p>
   * @param pVal Mandatory java.lang.String parameter.
   */

  @DISPID(1194) //= 0x4aa. The runtime will prefer the VTID if present
  @VTID(393)
  void hotkey11Shift(
    java.lang.String pVal);


  /**
   * <p>
   * method IsPassThrough
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(112) //= 0x70. The runtime will prefer the VTID if present
  @VTID(394)
  boolean isPassThrough();


  /**
   * <p>
   * property VirtualCOMPortEmulation
   * </p>
   * <p>
   * Getter method for the COM property "VirtualCOMPortEmulation"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1195) //= 0x4ab. The runtime will prefer the VTID if present
  @VTID(395)
  boolean virtualCOMPortEmulation();


  /**
   * <p>
   * property VirtualCOMPortEmulation
   * </p>
   * <p>
   * Setter method for the COM property "VirtualCOMPortEmulation"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1195) //= 0x4ab. The runtime will prefer the VTID if present
  @VTID(396)
  void virtualCOMPortEmulation(
    boolean pVal);


  /**
   * <p>
   * method SetSessionSize
   * </p>
   * @param depth Mandatory int parameter.
   * @param hDesiredHres Mandatory int parameter.
   * @param hDesiredVres Mandatory int parameter.
   * @param bSingleMonitor Mandatory int parameter.
   * @return  Returns a value of type int
   */

  @DISPID(113) //= 0x71. The runtime will prefer the VTID if present
  @VTID(397)
  int setSessionSize(
    int depth,
    int hDesiredHres,
    int hDesiredVres,
    int bSingleMonitor);


  /**
   * <p>
   * method GetEngineWndHandle
   * </p>
   * @return  Returns a value of type long
   */

  @DISPID(114) //= 0x72. The runtime will prefer the VTID if present
  @VTID(398)
  long getEngineWndHandle();


  /**
   * <p>
   * method CreateChannelComms
   * </p>
   * @param channelName Mandatory java.lang.String parameter.
   * @param pipeName Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(116) //= 0x74. The runtime will prefer the VTID if present
  @VTID(399)
  boolean createChannelComms(
    java.lang.String channelName,
    java.lang.String pipeName);


  /**
   * <p>
   * method EnumerateCCMSessions
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(117) //= 0x75. The runtime will prefer the VTID if present
  @VTID(400)
  int enumerateCCMSessions();


  /**
   * <p>
   * method StartMonitoringCCMSession
   * </p>
   * @param ccmSessionID Mandatory java.lang.String parameter.
   * @param bReserved Mandatory boolean parameter.
   */

  @DISPID(118) //= 0x76. The runtime will prefer the VTID if present
  @VTID(401)
  void startMonitoringCCMSession(
    java.lang.String ccmSessionID,
    boolean bReserved);


  /**
   * <p>
   * method StopMonitoringCCMSession
   * </p>
   * @param ccmSessionID Mandatory java.lang.String parameter.
   */

  @DISPID(119) //= 0x77. The runtime will prefer the VTID if present
  @VTID(402)
  void stopMonitoringCCMSession(
    java.lang.String ccmSessionID);


  /**
   * <p>
   * method GetCDMSecuritySettings
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(120) //= 0x78. The runtime will prefer the VTID if present
  @VTID(403)
  int getCDMSecuritySettings();


  /**
   * <p>
   * method SetCDMSecuritySettings
   * </p>
   * @param secSetting Mandatory int parameter.
   */

  @DISPID(121) //= 0x79. The runtime will prefer the VTID if present
  @VTID(404)
  void setCDMSecuritySettings(
    int secSetting);


  /**
   * <p>
   * method GetAudioInSecuritySettings
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(122) //= 0x7a. The runtime will prefer the VTID if present
  @VTID(405)
  int getAudioInSecuritySettings();


  /**
   * <p>
   * method SetAudioInSecuritySettings
   * </p>
   * @param secSetting Mandatory int parameter.
   */

  @DISPID(123) //= 0x7b. The runtime will prefer the VTID if present
  @VTID(406)
  void setAudioInSecuritySettings(
    int secSetting);


  /**
   * <p>
   * method GetFlashSecuritySettings
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(124) //= 0x7c. The runtime will prefer the VTID if present
  @VTID(407)
  int getFlashSecuritySettings();


  /**
   * <p>
   * method SetFlashSecuritySettings
   * </p>
   * @param secSetting Mandatory int parameter.
   */

  @DISPID(125) //= 0x7d. The runtime will prefer the VTID if present
  @VTID(408)
  void setFlashSecuritySettings(
    int secSetting);


  /**
   * <p>
   * property InXenDesktopViewer
   * </p>
   * <p>
   * Getter method for the COM property "InXenDesktopViewer"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1197) //= 0x4ad. The runtime will prefer the VTID if present
  @VTID(409)
  boolean inXenDesktopViewer();


  /**
   * <p>
   * property InXenDesktopViewer
   * </p>
   * <p>
   * Setter method for the COM property "InXenDesktopViewer"
   * </p>
   * @param pVal Mandatory boolean parameter.
   */

  @DISPID(1197) //= 0x4ad. The runtime will prefer the VTID if present
  @VTID(410)
  void inXenDesktopViewer(
    boolean pVal);


  /**
   * <p>
   * method GetTWAINSecuritySettings
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(126) //= 0x7e. The runtime will prefer the VTID if present
  @VTID(411)
  int getTWAINSecuritySettings();


  /**
   * <p>
   * method SetTWAINSecuritySettings
   * </p>
   * @param secSetting Mandatory int parameter.
   */

  @DISPID(127) //= 0x7f. The runtime will prefer the VTID if present
  @VTID(412)
  void setTWAINSecuritySettings(
    int secSetting);


  // Properties:
}
