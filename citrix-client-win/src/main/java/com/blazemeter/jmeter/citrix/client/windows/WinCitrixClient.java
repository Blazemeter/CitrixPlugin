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

    LOGGER.info(
        "Sess: des_dim={}x{}, des_depth={}bpp, dim={}x{}, depth={}bpp, Scr: dim={}x{}, depth={}bpp",
        getDesiredHRes(), getDesiredVRes(), getDesiredColorDepth(), icaClient.getSessionWidth(),
        icaClient.getSessionHeight(),
        icaClient.getSessionColorDepth(), icaClient.getScreenWidth(), icaClient.getScreenHeight(),
        icaClient.getScreenColorDepth());
  }

  protected boolean isAppActive() {
    if (session != null) {
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

  @Override
  public Rectangle getForegroundWindowArea() {
    WindowInfo info = fgKey != null ? windowInfos.get(fgKey) : null;
    return info != null ? new Rectangle(info.getArea()) : null;
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
      if (waitUserLogged(getLogonTimeoutInMs()) || !isUserLogged()) {
        throw new CitrixClientException(ErrorCode.LOGON_TIMEOUT, "Timed out waiting for Logon"
        );
      } else if (waitUserActiveApp(getActiveAppTimeoutInMs()) || !isAppActive()) {
        throw new CitrixClientException(ErrorCode.ACTIVEAPP_TIMEOUT,
            "Timed out waiting for Active App");
      }
    } catch (CitrixClientException e) {
      throw e; // Elevate
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

  @Override
  protected void stopSession() {
    LOGGER.debug("Logging off ICA client");

    keyboardCookie = null;
    mouseCookie = null;
    windowCookies.clear();

    mouse = null;
    keyboard = null;

    boolean expired;

    if (icaClient.session() != null) {
      try {
        icaClient.logoff();
      } catch (Exception e) {
        LOGGER.error("Unable to logoff session", e);
      }

      if (waitUserLogoff(getLogoffTimeoutInMs())) {
        LOGGER.warn("Timed out waiting for Logoff");
        sessionCookie = null;
      }
    }

    try {
      if (icaClient.session() != null) {
        LOGGER.debug("Disconnecting ICA client");
        icaClient.disconnect();
        if (waitDisconnect(getDisconnectTimeoutInMs())) {
          LOGGER.warn("Timed out waiting for Disconnect");
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to disconnect session", e);
    }

    eventsCookie = null;
    session = null;

    try {
      LOGGER.debug("Disposing ICA client");
      icaClient.dispose();
    } catch (Exception e) {
      LOGGER.error("Unable to dispose icaClient", e);
    }
    icaClient = null;
  }

  private IScreenShot createScreenshot(Rectangle selection) throws CitrixClientException {
    if (icaClient == null) {
      throw new CitrixClientException(
          ErrorCode.SCREENSHOT_ERROR,
          "Unable to create screenshot when ICA client is not running."
      );
    }
    ISession session = icaClient.session();
    if (session == null) {
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
    try {
      screenshot.filename(file.getAbsolutePath());
      screenshot.save();
    } catch (ComException e) {
      String msg = MessageFormat.format(
          "Unable to save screenshot " +
              "{0} session state is running={1}, connected={2}, visible={3}, userLogged={4}: {5}",
          file.getAbsolutePath(), isRunning(), isConnected(), isVisible(), isUserLogged(),
          e.getMessage());
      throw new CitrixClientException(ErrorCode.SCREENSHOT_ERROR, msg, e);
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

        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.CONNECT_FAIL));
      }

      @Override
      public void onConnect() {
        super.onConnect();

        LOGGER.debug("On Connect");

        session = icaClient.session();
        if (session == null) {
          LOGGER.debug("ICA session in not available while client connects");
          notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.ERROR,
              KnownError.UNAVAILABLE_SESSION.getCode()));
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
              notifyHandlers(new InteractionEvent(WinCitrixClient.this, KeyState.KEY_DOWN, keyId,
                  EventHelper.toModifiers(modifierState)));
            }

            @Override
            public void onKeyUp(int keyId, int modifierState) {
              super.onKeyUp(keyId, modifierState);
              notifyHandlers(new InteractionEvent(WinCitrixClient.this, KeyState.KEY_UP, keyId,
                  EventHelper.toModifiers(modifierState)));
            }
          });

          mouseCookie = mouse.advise(_IMouseEvents.class, new MouseAdapter() {
            @Override
            public void onMouseDown(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMouseDown(buttonState, modifierState, xPos, yPos);
              notifyHandlers(
                  new InteractionEvent(WinCitrixClient.this, MouseAction.BUTTON_DOWN, xPos,
                      yPos, EventHelper.toButtons(buttonState),
                      EventHelper.toModifiers(modifierState)));
            }

            @Override
            public void onMouseUp(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMouseUp(buttonState, modifierState, xPos, yPos);
              notifyHandlers(
                  new InteractionEvent(WinCitrixClient.this, MouseAction.BUTTON_UP, xPos, yPos,
                      EventHelper.toButtons(buttonState), EventHelper.toModifiers(modifierState)));
            }

            @Override
            public void onMove(int buttonState, int modifierState, int xPos, int yPos) {
              super.onMove(buttonState, modifierState, xPos, yPos);
              notifyHandlers(
                  new InteractionEvent(WinCitrixClient.this, MouseAction.MOVE, xPos, yPos,
                      EventHelper.toButtons(buttonState), EventHelper.toModifiers(modifierState)));
            }

          });
        }
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.CONNECT));
      }

      @Override
      public void onLogonFailed() {
        super.onLogonFailed();
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGON_FAIL));
      }

      @Override
      public void onLogon() {
        super.onLogon();
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGON));
      }

      @Override
      public void onLogoffFailed() {
        super.onLogoffFailed();
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGOFF_FAIL));
      }

      @Override
      public void onWindowDisplayed(int wndType) {
        super.onWindowDisplayed(wndType);
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.SHOW));
      }

      @Override
      public void onWindowDestroyed(int wndType) {
        super.onWindowDestroyed(wndType);
        // Depends on wndType ?
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.HIDE));
      }

      @Override
      public void onDisconnectFailed() {
        super.onDisconnectFailed();
        LOGGER.debug("On Disconnect Failed!");
        if (isUserLogged()) {
          notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGOFF_FAIL));
        }
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.DISCONNECT_FAIL));
      }

      @Override
      public void onDisconnect() {
        super.onDisconnect();
        LOGGER.debug("On Disconnect");
        if (isUserLogged()) {
          notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGOFF));
        }
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.DISCONNECT));
      }

      /* (non-Javadoc)
       * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFile()
       */
      @Override
      public void onICAFile() {
        super.onICAFile();
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.ICAFILE));
      }

      /* (non-Javadoc)
       * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFileFailed()
       */
      @Override
      public void onICAFileFailed() {
        super.onICAFileFailed();
        LOGGER.error("ICAFileFailed");
        notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.ICAFILE_FAIL));
      }
    });
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
          notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
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
          notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
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
        notifyHandlers(createWindowEvent(WindowState.CLOSED, winInfo));
      }

      @Override
      public void onCaptionChange(String caption) {
        super.onCaptionChange(caption);
        final WindowInfo winInfo = windowInfos.get(this.getWindowID());
        if (winInfo != null) {
          winInfo.setCaption(caption);
          LOGGER.debug("Sets window caption={} to window {}", caption, this.getWindowID());
          notifyHandlers(createWindowEvent(WindowState.CHANGE_CAPTION, winInfo));
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
          notifyHandlers(createWindowEvent(WindowState.ACTIVATED, winInfo));
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
          notifyHandlers(createWindowEvent(WindowState.DEACTIVATED, winInfo));
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
