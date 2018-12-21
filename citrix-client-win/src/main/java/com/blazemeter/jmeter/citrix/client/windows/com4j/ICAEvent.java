package com.blazemeter.jmeter.citrix.client.windows.com4j  ;

import com4j.*;

/**
 * <p>
 * List of ICA events
 * </p>
 */
public enum ICAEvent implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0
   * </p>
   */
  EventNone(0),
  /**
   * <p>
   * The value of this constant is 1
   * </p>
   */
  EventConnect(1),
  /**
   * <p>
   * The value of this constant is 2
   * </p>
   */
  EventConnectFail(2),
  /**
   * <p>
   * The value of this constant is 3
   * </p>
   */
  EventLogin(3),
  /**
   * <p>
   * The value of this constant is 4
   * </p>
   */
  EventLoginFail(4),
  /**
   * <p>
   * The value of this constant is 5
   * </p>
   */
  EventDisconnect(5),
  /**
   * <p>
   * The value of this constant is 6
   * </p>
   */
  EventRunPubishedApp(6),
  /**
   * <p>
   * The value of this constant is 7
   * </p>
   */
  EventRunPubishedAppFail(7),
  /**
   * <p>
   * The value of this constant is 8
   * </p>
   */
  EventIcaFilePresent(8),
  /**
   * <p>
   * The value of this constant is 9
   * </p>
   */
  EventLoadIcaFileFailed(9),
  /**
   * <p>
   * The value of this constant is 10
   * </p>
   */
  EventInitializing(10),
  /**
   * <p>
   * The value of this constant is 11
   * </p>
   */
  EventConnecting(11),
  /**
   * <p>
   * The value of this constant is 12
   * </p>
   */
  EventInitialProp(12),
  /**
   * <p>
   * The value of this constant is 13
   * </p>
   */
  EventDisconnectFailed(13),
  /**
   * <p>
   * The value of this constant is 14
   * </p>
   */
  EventLogoffFailed(14),
  /**
   * <p>
   * The value of this constant is 15
   * </p>
   */
  EventChannelDataReceived(15),
  /**
   * <p>
   * The value of this constant is 16
   * </p>
   */
  EventWindowSized(16),
  /**
   * <p>
   * The value of this constant is 17
   * </p>
   */
  EventWindowMoved(17),
  /**
   * <p>
   * The value of this constant is 18
   * </p>
   */
  EventWindowCreated(18),
  /**
   * <p>
   * The value of this constant is 19
   * </p>
   */
  EventWindowDestroyed(19),
  /**
   * <p>
   * The value of this constant is 20
   * </p>
   */
  EventWindowDocked(20),
  /**
   * <p>
   * The value of this constant is 21
   * </p>
   */
  EventWindowUndocked(21),
  /**
   * <p>
   * The value of this constant is 22
   * </p>
   */
  EventWindowMinimized(22),
  /**
   * <p>
   * The value of this constant is 23
   * </p>
   */
  EventWindowMaximized(23),
  /**
   * <p>
   * The value of this constant is 24
   * </p>
   */
  EventWindowRestored(24),
  /**
   * <p>
   * The value of this constant is 25
   * </p>
   */
  EventWindowFullscreened(25),
  /**
   * <p>
   * The value of this constant is 26
   * </p>
   */
  EventWindowHidden(26),
  /**
   * <p>
   * The value of this constant is 27
   * </p>
   */
  EventWindowDisplayed(27),
  /**
   * <p>
   * The value of this constant is 37
   * </p>
   */
  EventCGPWarn(37),
  /**
   * <p>
   * The value of this constant is 38
   * </p>
   */
  EventCGPUnwarn(38),
  /**
   * <p>
   * The value of this constant is 39
   * </p>
   */
  EventCGPDisconnect(39),
  /**
   * <p>
   * The value of this constant is 40
   * </p>
   */
  EventCGPReconnect(40),
  /**
   * <p>
   * The value of this constant is 41
   * </p>
   */
  EventACRReconnected(41),
  /**
   * <p>
   * The value of this constant is 42
   * </p>
   */
  EventACRReconnectFailed(42),
  /**
   * <p>
   * The value of this constant is 43
   * </p>
   */
  EventNewDesktopInfo(43),
  /**
   * <p>
   * The value of this constant is 44
   * </p>
   */
  EventACRReconnecting(44),
  ;

  private final int value;
  ICAEvent(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
