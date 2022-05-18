package com.blazemeter.jmeter.citrix.client.windows.com4j.events;

import com4j.DISPID;
import com4j.IID;

/**
 * _IICAClientEvents Interface.
 */
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:AbbreviationAsWordInName"})
@IID("{238F6F82-B8B4-11CF-8771-00A024541EE3}")
public abstract class _IICAClientEvents { //NOSONAR
  // Methods:

  /**
   * @param lReadyState Mandatory int parameter.
   */

  @DISPID(-609)
  public void onReadyStateChange(
      int lReadyState) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnClick.
   * </p>
   *
   * @param mouseButton Mandatory int parameter.
   * @param posX        Mandatory int parameter.
   * @param posY        Mandatory int parameter.
   * @param keyMask     Mandatory int parameter.
   */

  @DISPID(2)
  public void onClick(
      int mouseButton,
      int posX,
      int posY,
      int keyMask) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnConnect.
   * </p>
   */

  @DISPID(3)
  public void onConnect() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnConnectFailed.
   * </p>
   */

  @DISPID(4)
  public void onConnectFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnLogon.
   * </p>
   */

  @DISPID(5)
  public void onLogon() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnLogonFailed.
   * </p>
   */

  @DISPID(6)
  public void onLogonFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnDisconnect.
   * </p>
   */

  @DISPID(7)
  public void onDisconnect() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnPublishedApp.
   * </p>
   */

  @DISPID(8)
  public void onPublishedApp() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnPublishedAppFailed.
   * </p>
   */

  @DISPID(9)
  public void onPublishedAppFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnICAFile.
   * </p>
   */

  @DISPID(10)
  public void onICAFile() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnICAFileFailed.
   * </p>
   */

  @DISPID(11)
  public void onICAFileFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnInitializing.
   * </p>
   */

  @DISPID(12)
  public void onInitializing() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnConnecting.
   * </p>
   */

  @DISPID(13)
  public void onConnecting() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnInitialProp.
   * </p>
   */

  @DISPID(14)
  public void onInitialProp() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnDisconnectFailed.
   * </p>
   */

  @DISPID(15)
  public void onDisconnectFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnLogoffFailed.
   * </p>
   */

  @DISPID(16)
  public void onLogoffFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnChannelDataReceived.
   * </p>
   *
   * @param channelName Mandatory java.lang.String parameter.
   */

  @DISPID(17)
  public void onChannelDataReceived(
      java.lang.String channelName) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowSized.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   * @param width   Mandatory int parameter.
   * @param height  Mandatory int parameter.
   */

  @DISPID(18)
  public void onWindowSized(
      int wndType,
      int width,
      int height) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowMoved.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   * @param xPos    Mandatory int parameter.
   * @param yPos    Mandatory int parameter.
   */

  @DISPID(19)
  public void onWindowMoved(
      int wndType,
      int xPos,
      int yPos) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowCreated.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   * @param xPos    Mandatory int parameter.
   * @param yPos    Mandatory int parameter.
   * @param width   Mandatory int parameter.
   * @param height  Mandatory int parameter.
   */

  @DISPID(20)
  public void onWindowCreated(
      int wndType,
      int xPos,
      int yPos,
      int width,
      int height) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowDestroyed.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   */

  @DISPID(21)
  public void onWindowDestroyed(
      int wndType) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowDocked.
   * </p>
   */

  @DISPID(22)
  public void onWindowDocked() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowUndocked.
   * </p>
   */

  @DISPID(23)
  public void onWindowUndocked() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowMinimized.
   * </p>
   */

  @DISPID(24)
  public void onWindowMinimized() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowMaximized.
   * </p>
   */

  @DISPID(25)
  public void onWindowMaximized() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowRestored.
   * </p>
   */

  @DISPID(26)
  public void onWindowRestored() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowFullscreened.
   * </p>
   */

  @DISPID(27)
  public void onWindowFullscreened() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowHidden.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   */

  @DISPID(28)
  public void onWindowHidden(
      int wndType) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowDisplayed.
   * </p>
   *
   * @param wndType Mandatory int parameter.
   */

  @DISPID(29)
  public void onWindowDisplayed(
      int wndType) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnWindowCloseRequest.
   * </p>
   */

  @DISPID(30)
  public void onWindowCloseRequest() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnDisconnectSessions.
   * </p>
   *
   * @param hCommand Mandatory int parameter.
   */

  @DISPID(31)
  public void onDisconnectSessions(
      int hCommand) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnDisconnectSessionsFailed.
   * </p>
   *
   * @param hCommand Mandatory int parameter.
   */

  @DISPID(32)
  public void onDisconnectSessionsFailed(
      int hCommand) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnLogoffSessions.
   * </p>
   *
   * @param hCommand Mandatory int parameter.
   */

  @DISPID(33)
  public void onLogoffSessions(
      int hCommand) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnLogoffSessionsFailed.
   * </p>
   *
   * @param hCommand Mandatory int parameter.
   */

  @DISPID(34)
  public void onLogoffSessionsFailed(
      int hCommand) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnSessionSwitch.
   * </p>
   *
   * @param hOldSession Mandatory int parameter.
   * @param hNewSession Mandatory int parameter.
   */

  @DISPID(35)
  public void onSessionSwitch(
      int hOldSession,
      int hNewSession) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnSessionEventPending.
   * </p>
   *
   * @param hSession Mandatory int parameter.
   * @param eventNum Mandatory int parameter.
   */

  @DISPID(36)
  public void onSessionEventPending(
      int hSession,
      int eventNum) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnSessionAttach.
   * </p>
   *
   * @param hSession Mandatory int parameter.
   */

  @DISPID(37)
  public void onSessionAttach(
      int hSession) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnSessionDetach.
   * </p>
   *
   * @param hSession Mandatory int parameter.
   */

  @DISPID(38)
  public void onSessionDetach(
      int hSession) {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnCGPWarn.
   * </p>
   */

  @DISPID(39)
  public void onCGPWarn() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnCGPUnwarn.
   * </p>
   */

  @DISPID(40)
  public void onCGPUnwarn() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnCGPDisconnect.
   * </p>
   */

  @DISPID(41)
  public void onCGPDisconnect() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnCGPReconnect.
   * </p>
   */

  @DISPID(42)
  public void onCGPReconnect() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnACRReconnected.
   * </p>
   */

  @DISPID(43)
  public void onACRReconnected() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnACRReconnectFailed.
   * </p>
   */

  @DISPID(44)
  public void onACRReconnectFailed() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnNewDesktopInfo.
   * </p>
   */

  @DISPID(45)
  public void onNewDesktopInfo() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * event OnACRReconnecting.
   * </p>
   */

  @DISPID(46)
  public void onACRReconnecting() {
    throw new UnsupportedOperationException();
  }


  // Properties:
}
