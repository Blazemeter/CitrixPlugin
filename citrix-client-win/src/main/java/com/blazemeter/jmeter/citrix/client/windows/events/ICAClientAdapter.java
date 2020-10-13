package com.blazemeter.jmeter.citrix.client.windows.events;

import com.blazemeter.jmeter.citrix.client.windows.com4j.IICAClient;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ISession;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IICAClientEvents;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ICAClientAdapter extends _IICAClientEvents {

  private static final Logger LOGGER = LoggerFactory.getLogger(ICAClientAdapter.class);

  private final IICAClient icaClient;

  public ICAClientAdapter(IICAClient icaClient) {
    if (icaClient == null) {
      throw new IllegalArgumentException("icaClient cannot be null.");
    }
    this.icaClient = icaClient;
  }

  private void log(final String format, final Object... arguments) {
    log(false, format, arguments);
  }

  private void log(boolean isError, final String format, final Object... arguments) {
    if (!isError && LOGGER.isDebugEnabled()) {
      ISession session = icaClient.session();
      int size = (arguments != null ? arguments.length : 0) + 1;
      Object[] args = Arrays.copyOf(arguments, size);
      args[size - 1] = session != null ? "session is available" : "session is unavailable";
      LOGGER.debug("{} - arguments {}", format, args);
    }
    if (isError) {
      int lastError = icaClient.getLastError();
      String lastErrorMsg = icaClient.getErrorMessage(lastError);
      int lastClientError = icaClient.getLastClientError();
      String lastClientErrorMsg = icaClient.getClientErrorMessage(lastClientError);

      ISession session = icaClient.session();
      int size = (arguments != null ? arguments.length : 0) + 3;
      Object[] args = Arrays.copyOf(arguments, size);
      args[size - 1] = session != null ? "session is available" : "session is unavailable";
      args[size - 2] = "Last error:" + lastError + "=>" + lastErrorMsg;
      args[size - 3] = "Last client error:" + lastClientError + "=>" + lastClientErrorMsg;
      LOGGER.error("{} - arguments {}", format, args);
    }
  }

  @Override
  public void onACRReconnected() {
    log("ACRReconnected");
  }

  @Override
  public void onACRReconnectFailed() {
    log("ACRReconnectFailed");
  }

  @Override
  public void onACRReconnecting() {
    log("ACRReconnecting");
  }

  @Override
  public void onCGPDisconnect() {
    log("CGPDisconnect");
  }

  @Override
  public void onCGPReconnect() {
    log("CGPReconnect");
  }

  @Override
  public void onCGPUnwarn() {
    log("CGPUnwarn");
  }

  @Override
  public void onCGPWarn() {
    log("CGPWarn");
  }

  @Override
  public void onChannelDataReceived(String channelName) {
    log("ChannelDataReceived: channelName={}", channelName);
  }

  @Override
  public void onClick(int mouseButton, int posX, int posY, int keyMask) {
    log("Click: mouseButton={}, posX={}, posY={}, keyMask={}", mouseButton, posX, posY, keyMask);
  }

  @Override
  public void onConnect() {
    log("Connect");
  }

  @Override
  public void onConnectFailed() {
    log(true, "ConnectFailed");
  }

  @Override
  public void onConnecting() {
    log("Connecting");
  }

  @Override
  public void onDisconnect() {
    log("Disconnect");
  }

  @Override
  public void onDisconnectFailed() {
    log(true, "DisconnectFailed");
  }

  @Override
  public void onDisconnectSessions(int hCommand) {
    log("DisconnectSessions: hCommand=0x{}", Integer.toHexString(hCommand));
  }

  @Override
  public void onDisconnectSessionsFailed(int hCommand) {
    log(true, "DisconnectSessionsFailed: hCommand=0x{}", Integer.toHexString(hCommand));
  }

  @Override
  public void onICAFile() {
    log("ICAFile");
  }

  @Override
  public void onICAFileFailed() {
    log(true, "ICAFileFailed");
  }

  @Override
  public void onInitializing() {
    log("Initializing");
  }

  @Override
  public void onInitialProp() {
    log("InitialProp");
  }

  @Override
  public void onLogoffFailed() {
    log(true, "LogoffFailed");
  }

  @Override
  public void onLogoffSessions(int hCommand) {
    log("LogoffSessions: hCommand=0x{}", Integer.toHexString(hCommand));
  }

  @Override
  public void onLogoffSessionsFailed(int hCommand) {
    log(true, "LogoffSessionsFailed: hCommand=0x{}", Integer.toHexString(hCommand));
  }

  @Override
  public void onLogon() {
    log("Logon");
  }

  @Override
  public void onLogonFailed() {
    log(true, "LogonFailed");
  }

  @Override
  public void onNewDesktopInfo() {
    log("NewDesktopInfo");
  }

  @Override
  public void onPublishedApp() {
    log("PublishedApp");
  }

  @Override
  public void onPublishedAppFailed() {
    log("PublishedAppFailed");
  }

  @Override
  public void onReadyStateChange(int lReadyState) {
    log("ReadyStateChange: lReadyState=0x{}", Integer.toHexString(lReadyState));
  }

  @Override
  public void onSessionAttach(int hSession) {
    log("SessionAttach: hSession={}(0x{})", hSession, Integer.toHexString(hSession));
  }

  @Override
  public void onSessionDetach(int hSession) {
    log("SessionDetach: hSession={}(0x{})", hSession, Integer.toHexString(hSession));
  }

  @Override
  public void onSessionEventPending(int hSession, int eventNum) {
    log("SessionEventPending: hSession={}, eventNum={}", hSession, eventNum);
  }

  @Override
  public void onSessionSwitch(int hOldSession, int hNewSession) {
    log("SessionSwitch: hOldSession={}, hNewSession={}", hOldSession, hNewSession);
  }

  @Override
  public void onWindowCloseRequest() {
    log("WindowCloseRequest");
  }

  @Override
  public void onWindowCreated(int wndType, int xPos, int yPos, int width, int height) {
    log("WindowCreated: wndType={}, xPos={}, yPos={}, width={}, height={}", wndType, xPos, yPos,
        width, height);
  }

  @Override
  public void onWindowDestroyed(int wndType) {
    log("WindowDestroyed: wndType={}", wndType);
  }

  @Override
  public void onWindowDisplayed(int wndType) {
    log("WindowDisplayed: wndType={}", wndType);
  }

  @Override
  public void onWindowDocked() {
    log("WindowDocked");
  }

  @Override
  public void onWindowFullscreened() {
    log("WindowFullscreened");
  }

  @Override
  public void onWindowHidden(int wndType) {
    log("WindowHidden: wndType={}", wndType);
  }

  @Override
  public void onWindowMaximized() {
    log("WindowMaximized");
  }

  @Override
  public void onWindowMinimized() {
    log("WindowMinimized");
  }

  @Override
  public void onWindowMoved(int wndType, int xPos, int yPos) {
    log("WindowMoved: wndType={}, xPos={}, yPos={}", wndType, xPos, yPos);
  }

  @Override
  public void onWindowRestored() {
    log("WindowRestored");
  }

  @Override
  public void onWindowSized(int wndType, int width, int height) {
    log("WindowSized: wndType={}, width={}, height={}", wndType, width, height);
  }

  @Override
  public void onWindowUndocked() {
    log("WindowUndocked");
  }

}
