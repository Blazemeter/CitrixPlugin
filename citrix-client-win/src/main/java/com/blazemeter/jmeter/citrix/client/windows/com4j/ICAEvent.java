package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.ComEnum;

/**
 * <p>
 * List of ICA events.
 * </p>
 */
public enum ICAEvent implements ComEnum {
  /**
   * <p>
   * The value of this constant is 0.
   * </p>
   */
  EventNone(0), //NOSONAR
  /**
   * <p>
   * The value of this constant is 1.
   * </p>
   */
  EventConnect(1), //NOSONAR
  /**
   * <p>
   * The value of this constant is 2.
   * </p>
   */
  EventConnectFail(2), //NOSONAR
  /**
   * <p>
   * The value of this constant is 3.
   * </p>
   */
  EventLogin(3), //NOSONAR
  /**
   * <p>
   * The value of this constant is 4.
   * </p>
   */
  EventLoginFail(4), //NOSONAR
  /**
   * <p>
   * The value of this constant is 5.
   * </p>
   */
  EventDisconnect(5), //NOSONAR
  /**
   * <p>
   * The value of this constant is 6.
   * </p>
   */
  EventRunPubishedApp(6), //NOSONAR
  /**
   * <p>
   * The value of this constant is 7.
   * </p>
   */
  EventRunPubishedAppFail(7), //NOSONAR
  /**
   * <p>
   * The value of this constant is 8.
   * </p>
   */
  EventIcaFilePresent(8), //NOSONAR
  /**
   * <p>
   * The value of this constant is 9.
   * </p>
   */
  EventLoadIcaFileFailed(9), //NOSONAR
  /**
   * <p>
   * The value of this constant is 10.
   * </p>
   */
  EventInitializing(10), //NOSONAR
  /**
   * <p>
   * The value of this constant is 11.
   * </p>
   */
  EventConnecting(11), //NOSONAR
  /**
   * <p>
   * The value of this constant is 12.
   * </p>
   */
  EventInitialProp(12), //NOSONAR
  /**
   * <p>
   * The value of this constant is 13.
   * </p>
   */
  EventDisconnectFailed(13), //NOSONAR
  /**
   * <p>
   * The value of this constant is 14.
   * </p>
   */
  EventLogoffFailed(14), //NOSONAR
  /**
   * <p>
   * The value of this constant is 15.
   * </p>
   */
  EventChannelDataReceived(15), //NOSONAR
  /**
   * <p>
   * The value of this constant is 16.
   * </p>
   */
  EventWindowSized(16), //NOSONAR
  /**
   * <p>
   * The value of this constant is 17.
   * </p>
   */
  EventWindowMoved(17), //NOSONAR
  /**
   * <p>
   * The value of this constant is 18.
   * </p>
   */
  EventWindowCreated(18), //NOSONAR
  /**
   * <p>
   * The value of this constant is 19.
   * </p>
   */
  EventWindowDestroyed(19), //NOSONAR
  /**
   * <p>
   * The value of this constant is 20.
   * </p>
   */
  EventWindowDocked(20), //NOSONAR
  /**
   * <p>
   * The value of this constant is 21.
   * </p>
   */
  EventWindowUndocked(21), //NOSONAR
  /**
   * <p>
   * The value of this constant is 22.
   * </p>
   */
  EventWindowMinimized(22), //NOSONAR
  /**
   * <p>
   * The value of this constant is 23.
   * </p>
   */
  EventWindowMaximized(23), //NOSONAR
  /**
   * <p>
   * The value of this constant is 24.
   * </p>
   */
  EventWindowRestored(24), //NOSONAR
  /**
   * <p>
   * The value of this constant is 25.
   * </p>
   */
  EventWindowFullscreened(25), //NOSONAR
  /**
   * <p>
   * The value of this constant is 26.
   * </p>
   */
  EventWindowHidden(26), //NOSONAR
  /**
   * <p>
   * The value of this constant is 27.
   * </p>
   */
  EventWindowDisplayed(27), //NOSONAR
  /**
   * <p>
   * The value of this constant is 37.
   * </p>
   */
  EventCGPWarn(37), //NOSONAR
  /**
   * <p>
   * The value of this constant is 38.
   * </p>
   */
  EventCGPUnwarn(38), //NOSONAR
  /**
   * <p>
   * The value of this constant is 39.
   * </p>
   */
  EventCGPDisconnect(39), //NOSONAR
  /**
   * <p>
   * The value of this constant is 40.
   * </p>
   */
  EventCGPReconnect(40), //NOSONAR
  /**
   * <p>
   * The value of this constant is 41.
   * </p>
   */
  EventACRReconnected(41), //NOSONAR
  /**
   * <p>
   * The value of this constant is 42.
   * </p>
   */
  EventACRReconnectFailed(42), //NOSONAR
  /**
   * <p>
   * The value of this constant is 43.
   * </p>
   */
  EventNewDesktopInfo(43), //NOSONAR
  /**
   * <p>
   * The value of this constant is 44.
   * </p>
   */
  EventACRReconnecting(44); //NOSONAR

  private final int value;

  ICAEvent(int value) {
    this.value = value;
  }

  public int comEnumValue() {
    return value;
  }
}
