package com.blazemeter.jmeter.citrix.client.windows;

import com.blazemeter.jmeter.citrix.client.AbstractCitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.CitrixClientException.ErrorCode;
import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.EventHelper;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.KnownError;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent.WindowState;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ClassFactory;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAEvent;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAScalingMode;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAWindowType;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IICAClient;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IKeyboard;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IMouse;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IScreenShot;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ISession;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow;
import com.blazemeter.jmeter.citrix.client.windows.com4j.OutputMode;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IICAClientEvents;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IKeyboardEvents;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IMouseEvents;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._ISessionEvents;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IWindowEvents;
import com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter;
import com.blazemeter.jmeter.citrix.client.windows.events.KeyboardAdapter;
import com.blazemeter.jmeter.citrix.client.windows.events.MouseAdapter;
import com.blazemeter.jmeter.citrix.client.windows.events.SessionAdapter;
import com.blazemeter.jmeter.citrix.client.windows.events.WindowAdapter;
import com4j.ComException;
import com4j.EventCookie;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinCitrixClient extends AbstractCitrixClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(WinCitrixClient.class);

  private static final Object LOCK_ACTION = new Object();

  private final ConcurrentHashMap<Integer, WindowInfo> windowInfos = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, EventCookie> windowCookies = new ConcurrentHashMap<>();
  private Integer fgKey;

  private IICAClient icaClient;
  private ISession session;

  private EventCookie eventsCookie;
  private EventCookie sessionCookie;

  private IKeyboard keyboard;
  private IMouse mouse;

  private Integer desiredHRes = null;
  private Integer desiredVRes = null;
  private ICAColorDepth desiredColorDepth = null;

  private EventCookie keyboardCookie;
  private EventCookie mouseCookie;

  private boolean isKeepingScreenshots;
  private String screenshotDirectory;

  private boolean replayMode;
  private boolean visibleWindow;
  private boolean scalingEnabled = false;

  @Override
  public void onVisible() {
    super.onVisible();

    ICAWindowType winType = ICAWindowType.WindowTypeClient;

    if (!visibleWindow) {
      icaClient.hideWindow(winType);
    } else {
      // Translate X,Y based on number of session instance and offset
      int sessionWindowOffset = 20;
      int winPosXY = getThreadGroupNumber() * sessionWindowOffset;
      int winAreaRelativeToScreenFlag = 3;
      icaClient.setWindowPosition(winType, winPosXY, winPosXY, winAreaRelativeToScreenFlag);
    }

    if (!isScalingEnabled()) {
      icaClient.scalingMode(ICAScalingMode.ScalingModeDisabled);
      icaClient.scaleDisable();
    }

    LOGGER.info(
        "Sess: des_dim={}x{}, des_depth={}bpp, dim={}x{}, depth={}bpp, Scr: dim={}x{}, depth={}bpp",
        getDesiredHRes(), getDesiredVRes(), getDesiredColorDepth(), icaClient.getSessionWidth(),
        icaClient.getSessionHeight(),
        icaClient.getSessionColorDepth(), icaClient.getScreenWidth(), icaClient.getScreenHeight(),
        icaClient.getScreenColorDepth());
  }

  protected boolean isAppActive() {
    if (isSessionSet()) {
      IWindow window = session.foregroundWindow();
      if (window != null) {
        LOGGER.debug("Active Window: c={} id={} w={} h={}", window.caption(), window.windowID(),
            window.width(), window.height());
        // Non-application windows are excluded, like "ICA Seamless Host Agent"
        // Citrix or System window exclusions will be incorporated as they are identified.
        return !"ICA Seamless Host Agent".contains(window.caption());
      }
    }
    return false;
  }

  private void logLastError() {
    try {
      ICAEvent reason = icaClient.notificationReason();
      LOGGER.error("Reason: {}", reason.comEnumValue());
    } catch (Exception e) {
      LOGGER.error("Fail parsing Reason", e);
    }
    int lastError = icaClient.getLastError();
    String lastErrorMsg = icaClient.getErrorMessage(lastError);
    int lastClientError = icaClient.getLastClientError();
    String lastClientErrorMsg = icaClient.getClientErrorMessage(lastClientError);

    LOGGER.debug("LGE={} MSG={}; LCE={} MSG={}; CAS={}",
        lastError, lastErrorMsg, lastClientError, lastClientErrorMsg, icaClient.getSessionCount());
  }

  /**
   * Gets the location where temporary screenshots are stored.
   *
   * @return the location where temporary screenshots are stored
   */
  public String getScreenshotDirectory() {
    return screenshotDirectory != null ? screenshotDirectory : "";
  }

  /**
   * <p>
   * Sets the location where temporary screenshots are stored.
   * </p>
   *
   * <p>
   * If empty, current working directory is used.
   * </p>
   *
   * @param screenshotDirectory the location used to store temporary screenshots
   */
  public void setScreenshotDirectory(String screenshotDirectory) {
    this.screenshotDirectory = screenshotDirectory;
  }

  /**
   * Indicates whether screenshots are kept on disk.
   *
   * @return true, if screenshots are kept on disk; false otherwise.
   */
  public boolean isKeepingScreenshots() {
    return isKeepingScreenshots;
  }

  /**
   * Defines whether screenshots are kept on disk.
   *
   * @param isKeepingScreenshots true to keep screenshots on disk; false
   *                             otherwise.
   */
  public void setKeepingScreenshots(boolean isKeepingScreenshots) {
    this.isKeepingScreenshots = isKeepingScreenshots;
  }

  /**
   * Gets the desired horizontal resolution.
   *
   * @return the desired horizontal resolution
   */
  public Integer getDesiredHRes() {
    return desiredHRes;
  }

  /**
   * Defines the desired horizontal resolution.
   *
   * @param desiredHRes the desired horizontal resolution
   */
  public void setDesiredHRes(Integer desiredHRes) {
    this.desiredHRes = desiredHRes;
  }

  /**
   * Gets the desired vertical resolution.
   *
   * @return the desired vertical resolution
   */
  public Integer getDesiredVRes() {
    return desiredVRes;
  }

  /**
   * Defines the desired vertical resolution.
   *
   * @param desiredVRes the desired vertical resolution
   */
  public void setDesiredVRes(Integer desiredVRes) {
    this.desiredVRes = desiredVRes;
  }

  /**
   * Gets the desired color depth.
   *
   * @return the desired color depth
   */
  public ICAColorDepth getDesiredColorDepth() {
    return desiredColorDepth;
  }

  /**
   * Defines the desired color depth.
   *
   * @param colorDepth the desired color depth
   */
  public void setDesiredColorDepth(ICAColorDepth colorDepth) {
    this.desiredColorDepth = colorDepth;
  }

  public boolean isScalingEnabled() {
    return this.scalingEnabled;
  }

  public void setScalingEnabled(boolean scalingEnabled) {
    this.scalingEnabled = scalingEnabled;
  }

  @Override
  public Rectangle getForegroundWindowArea() {
    if (!isSessionSet()) {
      return null;
    }
    IWindow window = session.foregroundWindow();
    return window != null ? new Rectangle(
        window.positionX(), window.positionY(), window.width(), window.height()
    ) : null;
  }

  @Override
  public int getForegroundWindowID() {
    if (!isSessionSet()) {
      return 0;
    }
    IWindow window = session.foregroundWindow();
    return window != null ? window.windowID() : 0;
  }

  @Override
  public Collection<WindowInfo> getWindowInfos() {
    return new ArrayList<>(windowInfos.values());
  }

  @Override
  protected void startSession(boolean replayMode, boolean visible) throws CitrixClientException {
    try {
      this.replayMode = replayMode;
      this.visibleWindow = visible;

      icaClient = createICAClient();
      attachEvents();
      loadIcaFile();
      if (waitICAFileConnected(getICAFileTimeoutInMs())) {
        throw new CitrixClientException(ErrorCode.ICAFILE_TIMEOUT,
            "Time out waiting for ICAFile");
      }
      if (!isICAFileConnected()) {
        throw new CitrixClientException(ErrorCode.ICAFILE_ERROR, "Failed to load ICAFile");
      }

      LOGGER.debug("Connecting using ICA file {}", getICAFilePath());
      synchronized (LOCK_ACTION) {
        icaClient.connect();
        if (waitConnected(getConnectTimeoutInMs()) || !isConnected()) {
          logLastError();
          throw new CitrixClientException(ErrorCode.CONNECT_TIMEOUT,
              "Timed out waiting for OnConnect");
        }
      }
      if (waitUserActiveApp(getActiveAppTimeoutInMs()) || !isAppActive()) {
        if (!isSessionAvailable()) {
          throw new CitrixClientException(ErrorCode.SESSION_ERROR,
              "The user session was not in the expected state");
        } else {
          throw new CitrixClientException(ErrorCode.ACTIVEAPP_TIMEOUT,
              "Timed out waiting for Active App");
        }
      }
    } catch (CitrixClientException e) {
      if (!icaClient.connected() && wasInterrupted()) {
        throw new CitrixClientException(ErrorCode.SESSION_INTERRUPTED,
            "The user session was unexpectedly interrupted");
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw new CitrixClientException(
          ErrorCode.START_SESSION_ERROR,
          "Unable to start session",
          e);
    }
  }

  protected boolean waitUserActiveApp(long timeout) throws InterruptedException {
    long totalWaitActiveApp = 0;
    long chunckWaitActiveApp = getCheckActiveAppPeriod();
    // Polling over isAppActive because it does not fail at high load
    while (totalWaitActiveApp < timeout && !isAppActive()) {
      Thread.sleep(chunckWaitActiveApp);
      totalWaitActiveApp += chunckWaitActiveApp;
    }
    return totalWaitActiveApp >= timeout;
  }

  public boolean waitWindowActive() throws InterruptedException {
    return !(waitUserActiveApp(getActiveAppTimeoutInMs()) || !isAppActive());
  }

  @Override
  protected void stopSession() {
    LOGGER.debug("Logging off ICA client");

    keyboardCookie = null;
    mouseCookie = null;
    windowCookies.clear();

    mouse = null;
    keyboard = null;

    Long logoffTimeout = getLogoffTimeoutInMs();
    long totalLogoffTime = 0L;
    long chunkLogoffTime = 1000L;
    doAsyncLogoff();
    while (isSessionAvailable() && totalLogoffTime < logoffTimeout) {
      // Force again Logoff, ensure message to the server under heavy load
      doAsyncLogoff();
      if (!waitUserLogoff(chunkLogoffTime)) {
        break;
      }
      totalLogoffTime += chunkLogoffTime;
    }
    if (totalLogoffTime >= logoffTimeout) {
      LOGGER.warn("Timed out waiting for Logoff");
      // Logoff generate a disconnection, only when logoff fail force disconnect
      doAsyncDisconnect();
    }
    LOGGER.debug("Logoff time:{}", totalLogoffTime);

    sessionCookie = null;
    eventsCookie = null;
    session = null;

    try {
      LOGGER.debug("Disposing ICA client");
      if (icaClient != null) {
        icaClient.dispose();
      }
    } catch (Exception e) {
      LOGGER.error("Unable to dispose icaClient", e);
    }
    icaClient = null;

    LOGGER.debug("ICA client Disposed");
  }

  private boolean isSessionAvailable() {
    // Only when foregroundWindow is null when session unavailable
    try {
      return (icaClient.session() != null && icaClient.session().foregroundWindow() != null);
    } catch (Exception e) {
      // Any type of exception is an indication of a session loss.
      // Whether the instance does not exist or in its nested methods.
      // Exception is not logged because it is an expected condition
      return false;
    }
  }

  private void doAsyncLogoff() {
    // Internally logoff() is asynchronous.
    // For some reason the method does not return control quickly.
    // It would go to asynchronous execution since no waiting is required.
    CompletableFuture.runAsync(() -> icaClient.logoff());
  }

  private void doAsyncDisconnect() {
    // Internally disconnect() is asynchronous.
    // For some reason the method does not return control quickly.
    // It would go to asynchronous execution since no waiting is required.
    CompletableFuture.runAsync(() -> icaClient.disconnect());
  }

  private IScreenShot createScreenshot(Rectangle selection) throws CitrixClientException {
    if (icaClient == null) {
      throw new CitrixClientException(
          ErrorCode.SCREENSHOT_ERROR,
          "Unable to create screenshot when ICA client is not running."
      );
    }
    if (!isSessionSet()) {
      throw new CitrixClientException(ErrorCode.SCREENSHOT_ERROR,
          "Unable to create screenshot whereas Citrix session is not available.");
    }
    try {
      return selection != null
          ? session.createScreenShot(selection.x, selection.y, selection.width, selection.height)
          : session.createFullScreenShot();
    } catch (ComException e) {
      String msg = MessageFormat.format(
          "Unable to create screenshot whereas Citrix session state " +
              "is running={0}, connected={1}, visible={2}, userLogged={3} and selection={4}: {5}",
          isRunning(), isConnected(), isVisible(), isUserLogged(), selection, e.getMessage());
      throw new CitrixClientException(ErrorCode.SCREENSHOT_ERROR, msg, e);
    }
  }

  private void saveScreenshot(IScreenShot screenshot, File file) throws CitrixClientException {
    int saveAttemptsLimit = 3;
    screenshot.filename(file.getAbsolutePath());
    // Save may fail for unknown reasons generating a COM error.
    // When it is retried it works.
    // In case it doesn't work, the last exception is raised
    Exception lastException = null;
    for (int i = 0; i < saveAttemptsLimit; ++i) {
      try {
        screenshot.save();
        break;
      } catch (Exception e) {
        lastException = e;
      }
    }
    if (lastException != null) {
      String msg = MessageFormat.format(
          "Unable to save screenshot {0}", lastException.getMessage());
      throw new CitrixClientException(ErrorCode.SCREENSHOT_ERROR, msg, lastException);
    }
  }

  @Override
  protected BufferedImage doScreenshot() throws CitrixClientException {
    String screenshotDirectory = getScreenshotDirectory();
    if (screenshotDirectory.isEmpty()) {
      screenshotDirectory =
          new File(System.getProperty("java.io.tmpdir"), "jm_citrix_screenshots").getAbsolutePath();
    }
    if (!Paths.get(screenshotDirectory).toFile().exists()) {
      LOGGER.info("Creating screenshot dir {}", screenshotDirectory);
      if (!Paths.get(screenshotDirectory).toFile().mkdirs()) {
        LOGGER.error("Unable to create screenshot dir {}", screenshotDirectory);
      }
    }
    final Path filePath = Paths.get(screenshotDirectory, UUID.randomUUID().toString());
    final File file = filePath.toFile();
    IScreenShot screenshot = createScreenshot(null);
    saveScreenshot(screenshot, file);
    LOGGER.debug("Screenshot saved in {}", file.getAbsolutePath());

    try {
      return ImageIO.read(file);
    } catch (IOException e) {
      throw new CitrixClientException(
          ErrorCode.SCREENSHOT_ERROR, "Unable to read screenshot file " + file.getAbsolutePath(),
          e);
    } finally {
      if (!isKeepingScreenshots) {
        try {
          Files.delete(filePath);
          LOGGER.debug("Deletes screenshot {}", file.getAbsolutePath());
        } catch (IOException e) {
          LOGGER.warn("Unable to delete screenshot {}: {}", file.getAbsolutePath(), e.getMessage(),
              e);
        }
      }
    }
  }

  /* Ensure keyboard interface is initialized */
  private IKeyboard ensureKeyboard() throws CitrixClientException {
    if (keyboard == null) {
      throw new CitrixClientException(ErrorCode.KEYBOARD_ERROR,
          "Cannot send key event whereas Citrix session is disconnected");
    }
    return keyboard;
  }

  /* Ensure mouse interface is initialized */
  private IMouse ensureMouse() throws CitrixClientException {
    if (mouse == null) {
      throw new CitrixClientException(
          ErrorCode.MOUSE_ERROR,
          "Cannot send mouse event whereas Citrix session is disconnected"
      );
    }
    return mouse;
  }

  @Override
  protected void doKeyQuery(boolean keyUp, int keyCode) throws CitrixClientException {
    if (keyUp) {
      ensureKeyboard().sendKeyUp(keyCode);
    } else {
      ensureKeyboard().sendKeyDown(keyCode);
    }
  }

  @Override
  protected void doMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x, int y,
                                    Set<Modifier> modifiers)
      throws CitrixClientException {
    if (buttonUp) {
      ensureMouse()
          .sendMouseUp(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x,
              y);

    } else {
      ensureMouse()
          .sendMouseDown(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x,
              y);
    }
  }

  @Override
  protected void doMouseMoveQuery(Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers)
      throws CitrixClientException {
    ensureMouse()
        .sendMouseMove(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x,
            y);
  }

  private WindowEvent createWindowEvent(WindowState state, WindowInfo info) {
    return new WindowEvent(this, state, new WindowInfo(info));
  }

  private void switchForeground(Integer key) {
    fgKey = key;
    final WindowInfo info = fgKey != null ? windowInfos.get(fgKey) : null;
    LOGGER.debug(
        "Switches foreground to window {} with caption '{}'", key,
        info != null ? info.getCaption() : null
    );
    notifyHandlers(createWindowEvent(WindowState.FOREGROUND, info));
  }

  private void notifyKeyEvent(KeyState state, int keyCode,
                              int modifiers) {
    notifyHandlers(new InteractionEvent(WinCitrixClient.this,
        WinCitrixClient.this.getForegroundWindowID(),
        WinCitrixClient.this.getForegroundWindowArea(),
        state, keyCode, EventHelper.toModifiers(modifiers)));

  }

  private void notifyMouseEvent(MouseAction mouseAction, int x, int y,
                                int buttons,
                                int modifiers) {
    notifyHandlers(new InteractionEvent(WinCitrixClient.this,
        WinCitrixClient.this.getForegroundWindowID(),
        WinCitrixClient.this.getForegroundWindowArea(),
        mouseAction, x, y, EventHelper.toButtons(buttons),
        EventHelper.toModifiers(modifiers)
    ));

  }

  private void notifySessionEvent(EventType eventType) {
    notifyHandlers(new SessionEvent(WinCitrixClient.this, eventType));
  }

  private void notifySessionEvent(EventType eventType, int errorCode) {
    notifyHandlers(new SessionEvent(WinCitrixClient.this, eventType, errorCode));
  }

  private void notifyWindowEvent(WindowEvent.WindowState state,
                                 WindowInfo info) {
    notifyHandlers(createWindowEvent(state, info));
  }

  private void attachEvents() {
    // Set events handlers
    eventsCookie = icaClient.advise(_IICAClientEvents.class, new ICAClientAdapter(icaClient) {

      @Override
      public void onSessionSwitch(int hOldSession, int hNewSession) {
        super.onSessionSwitch(hOldSession, hNewSession);
        LOGGER.debug("On Session Switch! Old:{}, New:{}", hOldSession, hNewSession);
      }

      @Override
      public void onSessionEventPending(int hSession, int eventNum) {
        super.onSessionEventPending(hSession, eventNum);
        LOGGER.debug("On Session Event Pending! Sess:{}, Event:{}", hSession, eventNum);
      }

      @Override
      public void onSessionAttach(int hSession) {
        super.onSessionAttach(hSession);
        LOGGER.debug("On Session Attach! Sess:{}", hSession);
      }

      @Override
      public void onSessionDetach(int hSession) {
        super.onSessionDetach(hSession);
        LOGGER.debug("On Session Detach! Sess:{}", hSession);
      }

      @Override
      public void onReadyStateChange(int lReadyState) {
        super.onReadyStateChange(lReadyState);
        LOGGER.debug("On Ready State change! {}", lReadyState);
      }

      @Override
      public void onInitializing() {
        super.onInitializing();
        LOGGER.debug("On Initializing");
      }

      @Override
      public void onConnecting() {
        super.onConnecting();
        LOGGER.debug("On Connecting");
      }

      @Override
      public void onConnectFailed() {
        super.onConnectFailed();

        LOGGER.error("On Connect Failed!");
        logLastError();

        notifySessionEvent(EventType.CONNECT_FAIL);
      }

      @Override
      public void onConnect() {
        super.onConnect();

        LOGGER.debug("On Connect");

        setSession();
        if (!isSessionSet()) {
          LOGGER.debug("ICA session in not available while client connects");
          notifySessionEvent(EventType.ERROR, KnownError.UNAVAILABLE_SESSION.getCode());
        } else {

          sessionCookie = session.advise(_ISessionEvents.class, new SessionAdapter() {
            private Integer delayedFgKey;

            @Override
            public void onWindowCreate(IWindow window) {
              super.onWindowCreate(window);

              String caption = window.caption();

              // Exclude Language Bar for WindowsInfo / Active Window
              if ("TF_FloatingLangBar_WndTitle".equals(caption)) {
                return;
              }
              final int windowID = window.windowID();

              Rectangle newArea =
                  new Rectangle(window.positionX(), window.positionY(), window.width(),
                      window.height());

              EventCookie windowCookie = attachWindow(window);

              windowInfos.put(windowID, new WindowInfo(newArea, caption));
              windowCookies.put(windowID, windowCookie);
              LOGGER.debug("Sets window info:[caption={}, area={}] to window {}", caption, newArea,
                  windowID);

              if (windowID == delayedFgKey) {
                LOGGER.debug("Confirm window {} as foreground.", delayedFgKey);
                switchForeground(windowID);
              }

            }

            /*
             * Due to asynchronicity, a window can get foreground before its creation. In
             * this case we store its ID and we wait its creation to confirm the foreground
             * switch
             */
            @Override
            public void onWindowForeground(int windowID) {
              super.onWindowForeground(windowID);
              // POSSIBLE_IMPROVEMENT Handle windowID=0, occurs when a window is minimized
              // (but not only)
              if (windowID != 0) {
                if (windowInfos.containsKey(windowID)) {
                  switchForeground(windowID);
                } else {
                  LOGGER.debug("Potential foreground window: {}", windowID);
                  delayedFgKey = windowID;
                }
              } else {
                LOGGER.debug("Cannot switch foreground when windowId=0");
              }
            }

          });

          // Prevent input when replay
          LOGGER.debug("Sets ICA client replay mode to {}", replayMode);
          session.replayMode(replayMode);

          // Keep mouse and keyboard interface
          keyboard = session.keyboard();
          mouse = session.mouse();

          // Set keyboard handler
          keyboardCookie = keyboard.advise(_IKeyboardEvents.class, new KeyboardAdapter() {
            @Override
            public void onKeyDown(int keyId, int modifierState) {
              super.onKeyDown(keyId, modifierState);
              notifyKeyEvent(KeyState.KEY_DOWN, keyId, modifierState);
            }

            @Override
            public void onKeyUp(int keyId, int modifierState) {
              super.onKeyUp(keyId, modifierState);
              notifyKeyEvent(KeyState.KEY_UP, keyId, modifierState);
            }
          });

          mouseCookie = mouse.advise(_IMouseEvents.class, new MouseAdapter() {
            @Override
            public void onMouseDown(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMouseDown(buttonState, modifierState, xPos, yPos);
              notifyMouseEvent(MouseAction.BUTTON_DOWN, xPos, yPos,
                  buttonState, modifierState);
            }

            @Override
            public void onMouseUp(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMouseUp(buttonState, modifierState, xPos, yPos);
              notifyMouseEvent(MouseAction.BUTTON_UP, xPos, yPos, buttonState,
                  modifierState);
            }

            @Override
            public void onMove(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMove(buttonState, modifierState, xPos, yPos);
              notifyMouseEvent(MouseAction.MOVE, xPos, yPos, buttonState,
                  modifierState);
            }

          });
        }
        notifySessionEvent(EventType.CONNECT);
      }

      @Override
      public void onLogonFailed() {
        super.onLogonFailed();
        notifySessionEvent(EventType.LOGON_FAIL);
      }

      @Override
      public void onLogon() {
        super.onLogon();
        notifySessionEvent(EventType.LOGON);
      }

      @Override
      public void onLogoffFailed() {
        super.onLogoffFailed();
        notifySessionEvent(EventType.LOGOFF_FAIL);
      }

      @Override
      public void onWindowDisplayed(int wndType) {
        super.onWindowDisplayed(wndType);
        notifySessionEvent(EventType.SHOW);
      }

      @Override
      public void onWindowDestroyed(int wndType) {
        super.onWindowDestroyed(wndType);
        // Depends on wndType ?
        notifySessionEvent(EventType.HIDE);
      }

      @Override
      public void onDisconnectFailed() {
        super.onDisconnectFailed();
        LOGGER.debug("On Disconnect Failed!");
        if (isUserLogged()) {
          notifySessionEvent(EventType.LOGOFF_FAIL);
        }
        notifySessionEvent(EventType.DISCONNECT_FAIL);
      }

      @Override
      public void onDisconnect() {
        super.onDisconnect();
        LOGGER.debug("On Disconnect Interrupted:{}", wasInterrupted());
        if (isUserLogged()) {
          notifySessionEvent(EventType.LOGOFF);
        }
        notifySessionEvent(EventType.DISCONNECT);
      }

      /* (non-Javadoc)
       * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFile()
       */
      @Override
      public void onICAFile() {
        super.onICAFile();
        notifySessionEvent(EventType.ICAFILE);
      }

      /* (non-Javadoc)
       * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFileFailed()
       */
      @Override
      public void onICAFileFailed() {
        super.onICAFileFailed();
        LOGGER.error("ICAFileFailed");
        notifySessionEvent(EventType.ICAFILE_FAIL);
      }
    });
  }

  private void setSession() {
    session = icaClient.session();
  }

  private boolean isSessionSet() {
    return session != null;
  }

  private EventCookie attachWindow(IWindow window) {

    return window.advise(_IWindowEvents.class, new WindowAdapter(window.windowID()) {

      @Override
      public void onMove(int xPos, int yPos) {
        super.onMove(xPos, yPos);
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          final Rectangle area = winInfo.getArea();
          area.setLocation(xPos, yPos);
          LOGGER.debug("Sets window position={} to window {}", area.getLocation(),
              this.getWindowID());
          notifyWindowEvent(WindowState.CHANGE_AREA, winInfo);
        } else {
          LOGGER.debug("Should not happen : Unable to move the disposed window {}",
              this.getWindowID());
        }
      }

      @Override
      public void onSize(int width, int height) {
        super.onSize(width, height);
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          final Rectangle area = winInfo.getArea();
          area.setSize(width, height);
          LOGGER.debug("Sets window dimension={} to window {}", area.getSize(), this.getWindowID());
          notifyWindowEvent(WindowState.CHANGE_AREA, winInfo);
        } else {
          LOGGER.debug("Should not happen : Unable to resize the disposed window {}",
              this.getWindowID());
        }
      }

      @Override
      public void onDestroy() {
        super.onDestroy();
        if (this.getWindowID() == fgKey) {
          switchForeground(null);
        }
        final WindowInfo winInfo = windowInfos.remove(this.getWindowID());
        windowCookies.get(this.getWindowID()).close();
        windowCookies.remove(this.getWindowID());
        LOGGER.debug("Removes window info of window {}", this.getWindowID());
        notifyWindowEvent(WindowState.CLOSED, winInfo);
      }

      @Override
      public void onCaptionChange(String caption) {
        super.onCaptionChange(caption);
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          winInfo.setCaption(caption);
          LOGGER.debug("Sets window caption={} to window {}", caption, this.getWindowID());
          notifyWindowEvent(WindowState.CHANGE_CAPTION, winInfo);
        } else {
          LOGGER.debug(
              "Should not happen : Unable to change caption of the disposed window {}",
              this.getWindowID());
        }
      }

      @Override
      public void onActivate() {
        super.onActivate();
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          LOGGER.debug("Activates window {}", this.getWindowID());
          notifyWindowEvent(WindowState.ACTIVATED, winInfo);
        } else {
          LOGGER.debug("Should not happen : Unable to activate the disposed window {}",
              this.getWindowID());
        }
      }

      @Override
      public void onDeactivate() {
        super.onDeactivate();
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          LOGGER.debug("Deactivates window {}", this.getWindowID());
          notifyWindowEvent(WindowState.DEACTIVATED, winInfo);
        } else {
          LOGGER.debug("Should not happen : Unable to deactivate the disposed window {}",
              this.getWindowID());
        }
      }

    });
  }

  private void loadIcaFile() {
    final Path path = getICAFilePath();
    if (path != null && path.toFile().exists() && path.toFile().canRead()) {
      LOGGER.debug("Loading ICA file path from {}", path);
      synchronized (LOCK_ACTION) {
        icaClient.loadIcaFile(path.toString());
      }
    }
  }

  private IICAClient createICAClient() {
    LOGGER.debug("Create a new ICA client with replayMode={} and visible={}", replayMode,
        visibleWindow);
    IICAClient client = ClassFactory.createICAClient();

    client.title(this.getThreadGroupName());

    client.cacheICAFile(false);
    client.persistentCacheEnabled(false);
    // Required to launch ActiveX client
    client.launch(true);

    final Integer hRes = getDesiredHRes();
    if (hRes != null) {
      LOGGER.debug("Sets ICA client desired HRes to {}", hRes);
      client.desiredHRes(hRes);
    }

    final Integer vRes = getDesiredVRes();
    if (vRes != null) {
      LOGGER.debug("Sets ICA client desired VRes to {}", vRes);
      client.desiredVRes(vRes);
    }

    ICAColorDepth colorDepth = getDesiredColorDepth();
    if (colorDepth != null) {
      LOGGER.debug("Sets ICA client desired colorDepth to {}", colorDepth);
      client.desiredColor(colorDepth);
    }

    // Event intercept requires TWIMode is off
    client.twiMode(false);
    client.outputMode(OutputMode.OutputModeNormal);

    return client;
  }
}
