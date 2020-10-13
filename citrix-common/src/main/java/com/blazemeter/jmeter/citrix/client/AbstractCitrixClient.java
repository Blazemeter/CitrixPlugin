package com.blazemeter.jmeter.citrix.client;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.handler.CitrixClientHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class provides an abstract base class for {@link CitrixClient}
 * implementations.
 * </p>
 *
 * <p>
 * It is based on high level Citrix events conversion into platform specific
 * event.
 * </p>
 */
public abstract class AbstractCitrixClient implements CitrixClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCitrixClient.class);
  private final AtomicBoolean icafile = new AtomicBoolean(false);
  private final CountDownLatch icafileLatch = new CountDownLatch(1);
  private final AtomicBoolean connected = new AtomicBoolean(false);
  private final CountDownLatch connectedLatch = new CountDownLatch(1);
  private final CountDownLatch disconnectLatch = new CountDownLatch(1);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final AtomicBoolean userLogged = new AtomicBoolean(false);
  private final CountDownLatch userLoggedLatch = new CountDownLatch(1);
  private final CountDownLatch userLogoffLatch = new CountDownLatch(1);
  private final AtomicBoolean visible = new AtomicBoolean(false);
  // Contains the list of the handler of this client.
  private final List<CitrixClientHandler> handlers = new ArrayList<>();
  private final int threadGroupNumber;
  private final String threadGroupName;
  private Path icaFilePath;
  // Timeouts
  private Long icaFileTimeoutInMs = 10000L;
  private Long connectTimeoutInMs = 15000L;
  private Long logonTimeoutInMs = 60000L;
  private Long activeAppTimeoutInMs = 60000L;
  private Long logoffTimeoutInMs = 65000L;
  private Long disconnectTimeoutInMs = 65000L;
  private Long socketTimeoutInMs = 5000L;

  private int checkActiveAppPeriod = 1000;

  public AbstractCitrixClient() {
    this.threadGroupName = Thread.currentThread().getName();
    this.threadGroupNumber = this.parseThreadGroupNumber();
  }

  private int parseThreadGroupNumber() {
    int setThreadGroupNumber = 1;
    if (this.threadGroupName.lastIndexOf('-') != -1) {
      String stringNumber =
          this.threadGroupName.substring(this.threadGroupName.lastIndexOf('-') + 1);
      if (stringNumber.matches("\\d+")) {
        try {
          setThreadGroupNumber = Integer.parseInt(stringNumber);
        } catch (Exception e) {
          LOGGER.error("Problem parsing threadGroupNumber from {}", threadGroupName, e);
        }
      }
    }
    return setThreadGroupNumber;
  }

  public void onVisible() {

  }

  public String getThreadGroupName() {
    return this.threadGroupName;
  }

  public int getThreadGroupNumber() {
    return this.threadGroupNumber;
  }

  @Override
  public boolean isICAFileConnected() {
    return icafile.get();
  }

  @Override
  public boolean isConnected() {
    return connected.get();
  }

  @Override
  public final boolean isRunning() {
    return running.get();
  }

  @Override
  public boolean isUserLogged() {
    return userLogged.get();
  }

  @Override
  public boolean isVisible() {
    return visible.get();
  }

  @Override
  public final Path getICAFilePath() {
    return icaFilePath;
  }

  @Override
  public final void setICAFilePath(Path icaFilePath) {
    this.icaFilePath = icaFilePath;
  }

  /**
   * Sends a keyboard update to the Citrix server.
   *
   * @param keyUp   indicates whether the key is pressed (false) or released
   *                (true).
   * @param keyCode the code of the key
   * @throws CitrixClientException when sending fails
   */
  protected abstract void doKeyQuery(boolean keyUp, int keyCode) throws CitrixClientException;

  /**
   * Sends a mouse buttons update to the Citrix server.
   *
   * @param buttonUp  indicates whether the buttons are pressed (false) or
   *                  released (true).
   * @param buttons   mouse buttons to update
   * @param x         horizontal position of the mouse cursor
   * @param y         vertical position of the mouse cursor
   * @param modifiers modifier keys pressed during update
   * @throws CitrixClientException when sending fails
   */
  protected abstract void doMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x,
                                             int y,
                                             Set<Modifier> modifiers) throws CitrixClientException;

  /**
   * Does a screenshot of the Citrix session screen.
   *
   * @return the screenshot
   * @throws CitrixClientException when screenshot fails
   */
  protected abstract BufferedImage doScreenshot() throws CitrixClientException;

  /**
   * Sends a mouse position update to the Citrix server.
   *
   * @param buttons   mouse buttons pressed during update
   * @param x         horizontal position of the mouse cursor
   * @param y         vertical position of the mouse cursor
   * @param modifiers modifier keys pressed during update
   * @throws CitrixClientException when sending fails
   */
  protected abstract void doMouseMoveQuery(Set<MouseButton> buttons, int x, int y,
                                           Set<Modifier> modifiers)
      throws CitrixClientException;

  /**
   * Notify all subscribers when a user interaction happens.
   *
   * @param event the interaction event to emit
   */
  protected final void notifyHandlers(InteractionEvent event) {
    if (event != null) {
      synchronized (handlers) {
        for (CitrixClientHandler handler : handlers) {
          handler.handleInteractionEvent(event);
        }
      }
    }
  }

  /**
   * Notify all subscribers when a Citrix session event happens.
   *
   * @param event the session event to emit
   */
  protected final void notifyHandlers(SessionEvent event) {
    if (event != null) {
      switch (event.getEventType()) {
        case CONNECT:
          LOGGER.debug("Set connected status to true.");
          connected.set(true);
          connectedLatch.countDown();
          break;
        case CONNECT_FAIL:
          LOGGER.debug("Connect fail, set connected status to false.");
          connected.set(false);
          connectedLatch.countDown();
          break;
        case DISCONNECT:
          LOGGER.debug("Set connected status to false.");
          // ICAFile status to false
          icafile.set(false);
          icafileLatch.countDown();
          // Session status to false
          userLogged.set(false);
          userLoggedLatch.countDown();
          // Connection status to false
          connected.set(false);
          disconnectLatch.countDown();
          break;
        case DISCONNECT_FAIL:
          LOGGER.debug("Disconnect fail.");
          disconnectLatch.countDown();
          break;
        case LOGON:
          LOGGER.debug("Set user logged status to true.");
          userLogged.set(true);
          userLoggedLatch.countDown();
          break;
        case LOGON_FAIL:
          LOGGER.debug("Logon Fail, set user logged status to false.");
          userLogged.set(false);
          userLoggedLatch.countDown();
          break;
        case LOGOFF:
          LOGGER.debug("Set user logged status to false.");
          userLogged.set(false);
          userLogoffLatch.countDown();
          break;
        case LOGOFF_FAIL:
          LOGGER.debug("Logoff failed");
          userLogoffLatch.countDown();
          break;
        case SHOW:
          LOGGER.debug("Set visible status to true.");
          visible.set(true);
          onVisible();
          break;
        case HIDE:
          LOGGER.debug("Set visible status to false.");
          visible.set(false);
          break;
        case ICAFILE:
          LOGGER.debug("Set ICA File status to true.");
          icafile.set(true);
          icafileLatch.countDown();
          break;
        case ICAFILE_FAIL:
          LOGGER.debug("Set ICA File status to false.");
          icafile.set(false);
          icafileLatch.countDown();
          break;
        default:
          // NOOP
      }

      LOGGER
          .debug("Relays {} session event to {} listeners", event.getEventType(), handlers.size());
      synchronized (handlers) {
        for (CitrixClientHandler handler : handlers) {
          handler.handleSessionEvent(event);
        }
      }
    }

  }

  /**
   * Notify all subscribers when a window change happens.
   *
   * @param event the window change to emit
   */
  protected final void notifyHandlers(WindowEvent event) {
    if (event != null) {
      LOGGER
          .debug("Relays {} window event to {} listeners", event.getWindowState(), handlers.size());
      synchronized (handlers) {
        for (CitrixClientHandler handler : handlers) {
          handler.handleWindowEvent(event);
        }
      }
    }
  }

  /**
   * <p>
   * Starts a Citrix session.
   * </p>
   * <p>
   * Sub classes have to implements this method to manage the initialization and
   * connection of the Citrix session.
   * </p>
   *
   * @param replayMode defines is the client is in replay mode
   * @param visible    defines is the client is visible during replay.
   * @throws CitrixClientException when initializing or connecting fails.
   */
  protected abstract void startSession(boolean replayMode, boolean visible)
      throws CitrixClientException;

  /**
   * <p>
   * Stops the current Citrix session.
   * </p>
   * <p>
   * Sub classes have to implements this method to terminate the current session
   * and deallocate any resource linked to it.
   * </p>
   *
   * @throws CitrixClientException when ending the session fails.
   */
  protected abstract void stopSession() throws CitrixClientException;

  @Override
  public final void addHandler(CitrixClientHandler clientHandler) {
    synchronized (handlers) {
      handlers.add(clientHandler);
      LOGGER.debug("Adds a listener, Total: {}", handlers.size());
    }
  }

  @Override
  public final void removeHandler(CitrixClientHandler clientHandler) {
    synchronized (handlers) {
      handlers.remove(clientHandler);
      LOGGER.debug("Removes a listener, Total: {}", handlers.size());
    }
  }

  @Override
  public final void start(boolean replayMode, boolean visible) throws CitrixClientException {
    synchronized (running) {
      if (!running.get()) {
        LOGGER.debug("Starts a new Citrix session with replayMode={}, visible={}", replayMode,
            visible);
        startSession(replayMode, visible);
        running.set(true);
      } else {
        LOGGER.debug("Does not start a new Citrix session : A session is already in progress");
      }
    }
  }

  @Override
  public final void stop() throws CitrixClientException {
    synchronized (running) {

      LOGGER.debug("Stops the Citrix session");
      stopSession();
      running.set(false);

    }
  }

  @Override
  public final void sendKeyQuery(int keyCode, boolean keyUp) throws CitrixClientException {
    doKeyQuery(keyUp, keyCode);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Relays key query with keyCode=0x{} and keyUp={} to {} listeners",
          Integer.toHexString(keyCode), keyUp, handlers.size());
    }
    synchronized (handlers) {
      handlers.forEach(h -> h.handleKeyQuery(this, keyUp, keyCode));
    }
  }

  private Point getAbsolutePosition(Point relativePosition) {
    Rectangle area = getForegroundWindowArea();
    if (area == null) {
      String msg =
          "Unable to define absolute coordinates whereas no foreground window area is set.";
      LOGGER.error(msg);
      throw new IllegalStateException(msg);
    }
    final Point position = new Point(area.x + relativePosition.x, area.y + relativePosition.y);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(
          "Get absolute position {} from relative coordinates {} with foreground window area {}",
          position, relativePosition, area);
    }
    return position;
  }

  /*
   * Gets absolute coordinates when relative is true before send mouse query
   */
  private void sendPositionalQuery(boolean relative, int x, int y, PositionalQueryAction action)
      throws CitrixClientException {
    Point origPosition;
    Point position;
    if (relative) {
      origPosition = new Point(x, y);
      position = getAbsolutePosition(origPosition);
    } else {
      origPosition = null;
      position = new Point(x, y);
    }
    action.accept(position, origPosition);
  }

  @Override
  public final void sendMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x, int y,
                                         Set<Modifier> modifiers, boolean relative)
      throws CitrixClientException {
    sendPositionalQuery(relative, x, y, (pos, origPos) -> {
      doMouseButtonQuery(buttonUp, buttons, pos.x, pos.y, modifiers);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Relays mouse button query with " +
                "bUp={}, position={}, origPosition={}, btns={} and modifiers={} to {} listeners",
            buttonUp, pos, origPos, buttons, modifiers, handlers.size());
      }
      synchronized (handlers) {
        handlers.forEach(
            h -> h.handleMouseButtonQuery(this, buttonUp, buttons, pos, modifiers, origPos));
      }
    });
  }

  @Override
  public final void sendMouseMoveQuery(Set<MouseButton> buttons, int x, int y,
                                       Set<Modifier> modifiers,
                                       boolean relative) throws CitrixClientException {
    sendPositionalQuery(relative, x, y, (pos, origPos) -> {
      doMouseMoveQuery(buttons, pos.x, pos.y, modifiers);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Relays mouse move query with " +
                "position={}, origPosition={}, buttons={} and modifiers={} to {} listeners",
            pos, origPos, buttons, modifiers, handlers.size());
      }
      synchronized (handlers) {
        handlers.forEach(h -> h.handleMouseMoveQuery(this, buttons, pos, modifiers, origPos));
      }
    });
  }

  @Override
  public final BufferedImage takeScreenshot() throws CitrixClientException {
    return doScreenshot();
  }

  @Override
  public final Snapshot takeSnapshot() throws CitrixClientException {
    return new Snapshot(takeScreenshot(), getForegroundWindowArea());
  }

  protected boolean waitICAFileConnected(long timeout) {
    return waitLatch(icafileLatch, "ICA File Connected", timeout);
  }

  // Waiting routines

  protected boolean waitUserLogged(long timeout) {
    return waitLatch(userLoggedLatch, "User Logged", timeout);
  }

  protected boolean waitUserLogoff(long timeout) {
    return waitLatch(userLogoffLatch, "User Logoff", timeout);
  }

  protected boolean waitConnected(long timeout) {
    return waitLatch(connectedLatch, "Connected", timeout);
  }

  protected boolean waitDisconnect(long timeout) {
    return waitLatch(disconnectLatch, "Disconnect", timeout);
  }

  protected abstract boolean waitUserActiveApp(long timeout) throws InterruptedException;

  protected boolean waitLatch(CountDownLatch varLatch, String name, long timeout) {
    boolean expired = false;
    LOGGER.debug("Starts to wait for {}", name);
    try {
      expired = !varLatch.await(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      LOGGER.debug("Wait for {} interrupted", name, e);
    }
    LOGGER.debug("Stops to wait for {}, expired: {}", name, expired);
    return expired;
  }

  /**
   * @return the timeout in Millis
   */
  public Long getSocketTimeoutInMs() {
    return socketTimeoutInMs;
  }

  /**
   * @param socketTimeoutInMs the timeout in millis to set
   */
  public void setSocketTimeoutInMs(Long socketTimeoutInMs) {
    this.socketTimeoutInMs = socketTimeoutInMs;
  }

  public Long getICAFileTimeoutInMs() {
    return icaFileTimeoutInMs;
  }

  public void setICAFileTimeoutInMs(Long timeout) {
    icaFileTimeoutInMs = timeout;
  }

  public Long getConnectTimeoutInMs() {
    return connectTimeoutInMs;
  }

  public void setConnectTimeoutInMs(Long timeout) {
    connectTimeoutInMs = timeout;
  }

  public Long getLogonTimeoutInMs() {
    return logonTimeoutInMs;
  }

  public void setLogonTimeoutInMs(Long timeout) {
    logonTimeoutInMs = timeout;
  }

  public Long getActiveAppTimeoutInMs() {
    return activeAppTimeoutInMs;
  }

  public void setActiveAppTimeoutInMs(Long timeout) {
    activeAppTimeoutInMs = timeout;
  }

  public Long getLogoffTimeoutInMs() {
    return logoffTimeoutInMs;
  }

  public void setLogoffTimeoutInMs(Long timeout) {
    logoffTimeoutInMs = timeout;
  }

  public Long getDisconnectTimeoutInMs() {
    return disconnectTimeoutInMs;
  }

  public void setDisconnectTimeoutInMs(Long timeout) {
    disconnectTimeoutInMs = timeout;
  }

  public int getCheckActiveAppPeriod() {
    return this.checkActiveAppPeriod;
  }

  public void setCheckActiveAppPeriod(int period) {
    this.checkActiveAppPeriod = period;
  }

  private interface PositionalQueryAction {
    void accept(Point position, Point originalPosition) throws CitrixClientException;
  }

}
