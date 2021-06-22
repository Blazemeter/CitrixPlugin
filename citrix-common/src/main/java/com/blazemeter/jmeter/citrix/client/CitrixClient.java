package com.blazemeter.jmeter.citrix.client;

import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.handler.CitrixClientHandler;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public interface CitrixClient {

  /**
   * Indicates whether the client ICA file is connected.
   *
   * @return true is the client ICA File is connected; otherwise false.
   */
  boolean isICAFileConnected();

  /**
   * Indicates whether the client is connected.
   *
   * @return true is the client is connected; otherwise false.
   */
  boolean isConnected();

  /**
   * Indicates whether this client is running.
   *
   * @return true if this client is running; false otherwise.
   */
  boolean isRunning();

  /**
   * Indicates whether the user of the Citrix session is logged on remote server.
   *
   * @return true if the user of the Citrix session is logged on remote server;
   * otherwise false.
   */
  boolean isUserLogged();

  /**
   * Indicates whether the Citrix session is displayed.
   *
   * @return true if he Citrix session is displayed; otherwise false.
   */
  boolean isVisible();

  boolean wasInterrupted();

  /**
   * Gets the path of the ICA file used to initialize the Citrix session.
   *
   * @return the path of the ICA file used to initialize the Citrix session
   */
  Path getICAFilePath();

  /**
   * Defines the path of the ICA file used to initialize the Citrix session.
   *
   * @param icaFilePath the ICA file used to initialize the Citrix session
   */
  void setICAFilePath(Path icaFilePath);

  /**
   * <p>
   * Gets the area containing the foreground window.
   * </p>
   *
   * <p>
   * Positions of the area are relative to the Citrix session screen.
   * </p>
   *
   * @return the area containing the foreground window
   */
  Rectangle getForegroundWindowArea();

  /**
   * Gets details on current windows in the Citrix session.
   *
   * @return details on current windows in the Citrix session
   */
  Collection<WindowInfo> getWindowInfos();

  /**
   * Add a @{link CitrixClientHandler} to the client.
   *
   * @param clientHandler to add
   */
  void addHandler(CitrixClientHandler clientHandler);

  /**
   * Removes a {@link CitrixClientHandler} from the client.
   *
   * @param clientHandler to remove
   */
  void removeHandler(CitrixClientHandler clientHandler);

  /**
   * Starts the Citrix session.
   *
   * @param replayMode defines is the client is in replay mode
   * @param visible    defines is the client is visible during replay
   * @throws CitrixClientException when starting session fails
   */
  void start(boolean replayMode, boolean visible) throws CitrixClientException;

  /**
   * Stops the Citrix session.
   *
   * @throws CitrixClientException when soptting session fails
   */
  void stop() throws CitrixClientException;

  /**
   * Takes a screenshot of the whole screen session.
   *
   * @return the screenshot of the whole screen session
   * @throws CitrixClientException when screenshot fails
   */
  BufferedImage takeScreenshot() throws CitrixClientException;

  /**
   * Atomically, takes a screenshot and gets foreground window area.
   *
   * @return the synchronized screenshot and foreground window area
   * @throws CitrixClientException when screenshot fails
   */
  Snapshot takeSnapshot() throws CitrixClientException;

  /**
   * Sends a keyboard update to the Citrix server.
   *
   * @param keyCode the code of the key
   * @param keyUp   indicates whether the key is pressed (false) or released
   *                (true).
   * @throws CitrixClientException when sending fails
   */
  void sendKeyQuery(int keyCode, boolean keyUp) throws CitrixClientException;

  /**
   * Sends a mouse buttons update to the Citrix server.
   *
   * @param buttonUp  indicates whether the buttons are pressed (false) or
   *                  released (true).
   * @param buttons   mouse buttons to update
   * @param x         horizontal position of the mouse cursor
   * @param y         vertical position of the mouse cursor
   * @param modifiers modifier keys pressed during update
   * @param relative  true if specified coordinates are relative to foreground
   *                  windows; false otherwise
   * @throws CitrixClientException when sending fails
   */
  void sendMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x, int y,
                            Set<Modifier> modifiers,
                            boolean relative) throws CitrixClientException;

  /**
   * Sends a mouse position update to the Citrix server.
   *
   * @param buttons   mouse buttons pressed during update
   * @param x         horizontal position of the mouse cursor
   * @param y         vertical position of the mouse cursor
   * @param modifiers modifier keys pressed during update
   * @param relative  true if specified coordinates are relative to foreground
   *                  windows; false otherwise
   * @throws CitrixClientException when sending fails
   */
  void sendMouseMoveQuery(Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers,
                          boolean relative)
      throws CitrixClientException;

  Long getSocketTimeoutInMs();

  void setSocketTimeoutInMs(Long socketTimeoutInMs);

  Long getICAFileTimeoutInMs();

  void setICAFileTimeoutInMs(Long timeout);

  Long getConnectTimeoutInMs();

  void setConnectTimeoutInMs(Long timeout);

  Long getLogonTimeoutInMs();

  void setLogonTimeoutInMs(Long timeout);

  Long getLogoffTimeoutInMs();

  void setLogoffTimeoutInMs(Long timeout);

  Long getDisconnectTimeoutInMs();

  void setDisconnectTimeoutInMs(Long timeout);

  int getCheckActiveAppPeriod();

  void setCheckActiveAppPeriod(int period);

  int getForegroundWindowID();

  class Snapshot {
    private final Rectangle fgWindowArea;
    private final BufferedImage screenshot;

    public Snapshot(BufferedImage screenshot, Rectangle fgWindowArea) {
      this.screenshot = screenshot;
      this.fgWindowArea = fgWindowArea;
    }

    public Rectangle getForegroundWindowArea() {
      return fgWindowArea;
    }

    public BufferedImage getScreenshot() {
      return screenshot;
    }

  }

}
