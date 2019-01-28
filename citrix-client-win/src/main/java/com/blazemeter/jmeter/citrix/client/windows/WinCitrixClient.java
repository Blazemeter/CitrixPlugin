package com.blazemeter.jmeter.citrix.client.windows;

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

import com.blazemeter.jmeter.citrix.client.AbstractCitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.KnownError;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent.WindowState;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.EventHelper;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ClassFactory;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth;
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

public class WinCitrixClient extends AbstractCitrixClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(WinCitrixClient.class);

	private final ConcurrentHashMap<Integer, WindowInfo> windowInfos = new ConcurrentHashMap<>();
	private Integer fgKey;
	private Integer delayedFgKey;

	private IICAClient icaClient;

	private IKeyboard keyboard;
	private IMouse mouse;

	private Integer desiredHRes = null;
	private Integer desiredVRes = null;
	private ICAColorDepth desiredColorDepth = null;

	// POSSIBLE_IMPROVEMENT See onDisconnect handler in createICAClient
	@SuppressWarnings("unused")
	private EventCookie keyboardCookie;
	@SuppressWarnings("unused")
	private EventCookie mouseCookie;

	private boolean isKeepingScreenshots;
	private String screenshotDirectory;

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
	 * Indicates whether screenshots are kept on disk
	 * 
	 * @return true, if screenshots are kept on disk; false otherwise.
	 */
	public boolean isKeepingScreenshots() {
		return isKeepingScreenshots;
	}

	/**
	 * Defines whether screenshots are kept on disk
	 * 
	 * @param isKeepingScreenshots true to keep screenshots on disk; false
	 *                             otherwise.
	 */
	public void setKeepingScreenshots(boolean isKeepingScreenshots) {
		this.isKeepingScreenshots = isKeepingScreenshots;
	}

	/**
	 * Gets the desired horizontal resolution
	 * 
	 * @return the desired horizontal resolution
	 */
	public Integer getDesiredHRes() {
		return desiredHRes;
	}

	/**
	 * Defines the desired horizontal resolution
	 * 
	 * @param desiredHRes the desired horizontal resolution
	 */
	public void setDesiredHRes(Integer desiredHRes) {
		this.desiredHRes = desiredHRes;
	}

	/**
	 * Gets the desired vertical resolution
	 * 
	 * @return the desired vertical resolution
	 */
	public Integer getDesiredVRes() {
		return desiredVRes;
	}

	/**
	 * Defines the desired vertical resolution
	 * 
	 * @param desiredVRes the desired vertical resolution
	 */
	public void setDesiredVRes(Integer desiredVRes) {
		this.desiredVRes = desiredVRes;
	}

	/**
	 * Gets the desired color depth
	 * 
	 * @return the desired color depth
	 */
	public ICAColorDepth getDesiredColorDepth() {
		return desiredColorDepth;
	}

	/**
	 * Defines the desired color depth
	 * 
	 * @param colorDepth the desired color depth
	 */
	public void setDesiredColorDepth(ICAColorDepth colorDepth) {
		this.desiredColorDepth = colorDepth;
	}

	@Override
	public Rectangle getForegroundWindowArea() {
		return fgKey != null ? new Rectangle(windowInfos.get(fgKey).getArea()) : null;
	}

	@Override
	public Collection<WindowInfo> getWindowInfos() {
		return new ArrayList<>(windowInfos.values());
	}

	@Override
	protected void startSession(boolean replayMode, boolean visible) throws CitrixClientException {
		try {
			icaClient = createICAClient(replayMode, visible);
			icaClient.connect();
		} catch (Exception e) {
			final String msg = "Unable to start ICA session.";
			LOGGER.error(msg, e);
			throw new CitrixClientException(msg, e);
		}
	}

	@Override
	protected void stopSession() throws CitrixClientException {
		try {
			icaClient.logoff();
			icaClient.disconnect();
			icaClient.dispose();
			icaClient = null;
		} catch (Exception e) {
			final String msg = "Unable to stop ICA session.";
			LOGGER.error(msg, e);
			throw new CitrixClientException(msg, e);
		}
	}

	private IScreenShot createScreenshot(Rectangle selection) throws CitrixClientException {
		try {
			ISession session = icaClient.session();
			return selection != null
					? session.createScreenShot(selection.x, selection.y, selection.width, selection.height)
					: session.createFullScreenShot();
		} catch (ComException e) {
			String msg = MessageFormat.format(
					"Unable to create screenshot whereas Citrix session state is running={0}, connected={1}, visible={2}, userLogged={3} and selection={4}: {5}",
					isRunning(), isConnected(), isVisible(), isUserLogged(), selection, e.getMessage());
			LOGGER.warn(msg, e);
			throw new CitrixClientException(msg);
		}
	}

	private void saveScreenshot(IScreenShot screenshot, File file) throws CitrixClientException {
		try {
			screenshot.filename(file.getAbsolutePath());
			screenshot.save();
		} catch (ComException e) {
			String msg = MessageFormat.format(
					"Unable to save screenshot to file {0} whereas Citrix session state is running={1}, connected={2}, visible={3}, userLogged={4}: {5}",
					file.getAbsolutePath(), isRunning(), isConnected(), isVisible(), isUserLogged(), e.getMessage());
			LOGGER.warn(msg, e);
			throw new CitrixClientException(msg);
		}
	}

	@Override
	protected BufferedImage doScreenshot() throws CitrixClientException {
		final Path filePath = Paths.get(getScreenshotDirectory(), UUID.randomUUID().toString());
		final File file = filePath.toFile();
		IScreenShot screenshot = createScreenshot(null);
		saveScreenshot(screenshot, file);
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			String msg = MessageFormat.format("Unable to read screenshot file {0}: {1}", file.getAbsolutePath(),
					e.getMessage());
			LOGGER.warn(msg, e);
			throw new CitrixClientException(msg);
		} finally {
			if (!isKeepingScreenshots) {
				try {
					Files.delete(filePath);
				} catch (IOException e) {
					LOGGER.warn("Unable to delete screenshot {}: {}", file.getAbsolutePath(), e.getMessage(), e);
				}
			}
		}
	}

	/* Ensure keyboard interface is initialized */
	private IKeyboard ensureKeyboard() throws CitrixClientException {
		if (keyboard == null) {
			throw new CitrixClientException("Cannot send key event whereas Citrix session is disconnected");
		}
		return keyboard;
	}

	/* Ensure mouse interface is initialized */
	private IMouse ensureMouse() throws CitrixClientException {
		if (mouse == null) {
			throw new CitrixClientException("Cannot send mouse event whereas Citrix session is disconnected");
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
	protected void doMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers)
			throws CitrixClientException {
		if (buttonUp) {
			ensureMouse().sendMouseUp(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x, y);

		} else {
			ensureMouse().sendMouseDown(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x, y);
		}
	}

	@Override
	protected void doMouseMoveQuery(Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers)
			throws CitrixClientException {
		ensureMouse().sendMouseMove(EventHelper.fromButtons(buttons), EventHelper.fromModifiers(modifiers), x, y);
	}

	private WindowEvent createWindowEvent(WindowState state, WindowInfo info) {
		return new WindowEvent(this, state, new WindowInfo(info));
	}

	private void switchForeground(Integer key) {
		fgKey = key;
		notifyHandlers(createWindowEvent(WindowState.FOREGROUND, fgKey != null ? windowInfos.get(fgKey) : null));
	}

	private IICAClient createICAClient(boolean replayMode, boolean visible) {
		IICAClient client = ClassFactory.createICAClient();

		Path path = getICAFilePath();
		client.icaFile(path != null ? path.toString() : "");

		// Required to launch ActiveX client
		client.launch(true);

		Integer hRes = getDesiredHRes();
		if (hRes != null) {
			client.desiredHRes(hRes);
		}

		Integer vRes = getDesiredVRes();
		if (vRes != null) {
			client.desiredVRes(vRes);
		}

		ICAColorDepth colorDepth = getDesiredColorDepth();
		if (colorDepth != null) {
			client.desiredColor(colorDepth);
		}

		// Event intercept requires TWIMode is off
		client.twiMode(false);
		OutputMode mode = visible ? OutputMode.OutputModeNormal : OutputMode.OutputModeWindowless;
		LOGGER.debug("Starting client in mode {}", mode);
		client.outputMode(mode);

		// Set events handlers
		client.advise(_IICAClientEvents.class, new ICAClientAdapter(client) {

			@Override
			public void onConnect() {
				super.onConnect();

				LOGGER.info("Screen info: dimension={}x{}, depth={}bpp", client.getScreenWidth(),
						client.getScreenHeight(), client.getScreenColorDepth());

				final ISession session = client.session();
				if (session == null) {
					notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.ERROR,
							KnownError.UNAVAILABLE_SESSION.getCode()));
				} else {
					// Prevent input when replay
					session.replayMode(replayMode);

					session.advise(_ISessionEvents.class, new SessionAdapter() {
						@Override
						public void onWindowCreate(IWindow window) {
							super.onWindowCreate(window);

							final int windowID = window.windowID();
							final Integer key = Integer.valueOf(windowID);
							Rectangle newArea = new Rectangle(window.positionX(), window.positionY(), window.width(),
									window.height());
							String caption = window.caption();
							windowInfos.put(key, new WindowInfo(newArea, caption));

							window.advise(_IWindowEvents.class, new WindowAdapter(windowID) {

								@Override
								public void onMove(int xPos, int yPos) {
									super.onMove(xPos, yPos);
									final WindowInfo winInfo = windowInfos.get(key);
									final Rectangle area = winInfo.getArea();
									area.setLocation(xPos, yPos);
									notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
								}

								@Override
								public void onSize(int width, int height) {
									super.onSize(width, height);
									final WindowInfo winInfo = windowInfos.get(key);
									final Rectangle area = winInfo.getArea();
									area.setSize(width, height);
									notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
								}

								@Override
								public void onDestroy() {
									super.onDestroy();
									final WindowInfo winInfo = windowInfos.remove(key);
									notifyHandlers(createWindowEvent(WindowState.CLOSED, winInfo));
									if (key.equals(fgKey)) {
										switchForeground(null);
									}
								}

								@Override
								public void onCaptionChange(String caption) {
									super.onCaptionChange(caption);
									final WindowInfo winInfo = windowInfos.get(key);
									winInfo.setCaption(caption);
									notifyHandlers(createWindowEvent(WindowState.CLOSED, winInfo));
								}

								@Override
								public void onActivate() {
									super.onActivate();
									notifyHandlers(createWindowEvent(WindowState.ACTIVATED, windowInfos.get(key)));
								}

								@Override
								public void onDeactivate() {
									super.onDeactivate();
									notifyHandlers(createWindowEvent(WindowState.DEACTIVATED, windowInfos.get(key)));
								}

							});

							if (key.equals(delayedFgKey)) {
								LOGGER.debug("Confirm window {} as foreground.", delayedFgKey);
								switchForeground(key);
							}
						}

						@Override
						public void onWindowForeground(int windowID) {
							super.onWindowForeground(windowID);
							if (windowID != 0) {
								Integer key = Integer.valueOf(windowID);
								if (windowInfos.containsKey(key)) {
									switchForeground(key);
								} else {
									LOGGER.debug("Potential foreground window: {}", key);
									delayedFgKey = key;
								}
							}
						}

					});

					// Keep mouse and keyboard interface
					keyboard = session.keyboard();
					mouse = session.mouse();

					// Set keyboard handler
					keyboardCookie = keyboard.advise(_IKeyboardEvents.class, new KeyboardAdapter() {
						// session.keyboard().advise(_IKeyboardEvents.class, new _IKeyboardEvents() {
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

					// Set mouse handler
					mouseCookie = mouse.advise(_IMouseEvents.class, new MouseAdapter() {
						// session.mouse().advise(_IMouseEvents.class, new _IMouseEvents() {
						@Override
						public void onMouseDown(int buttonState, int modifierState, int xPos, int yPos) {
							super.onMouseDown(buttonState, modifierState, xPos, yPos);
							notifyHandlers(new InteractionEvent(WinCitrixClient.this, MouseAction.BUTTON_DOWN, xPos,
									yPos, EventHelper.toButtons(buttonState), EventHelper.toModifiers(modifierState)));
						}

						@Override
						public void onMouseUp(int buttonState, int modifierState, int xPos, int yPos) {
							super.onMouseUp(buttonState, modifierState, xPos, yPos);
							notifyHandlers(new InteractionEvent(WinCitrixClient.this, MouseAction.BUTTON_UP, xPos, yPos,
									EventHelper.toButtons(buttonState), EventHelper.toModifiers(modifierState)));
						}

						@Override
						public void onMove(int buttonState, int modifierState, int xPos, int yPos) {
							super.onMove(buttonState, modifierState, xPos, yPos);
							notifyHandlers(new InteractionEvent(WinCitrixClient.this, MouseAction.MOVE, xPos, yPos,
									EventHelper.toButtons(buttonState), EventHelper.toModifiers(modifierState)));
						}

					});
				}

				// Emit Connect event
				notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.CONNECT));
			}

			@Override
			public void onLogon() {
				super.onLogon();
				notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.LOGON));
			}

			@Override
			public void onWindowDisplayed(int wndType) {
				super.onWindowDisplayed(wndType);
				// client.hideWindow(ICAWindowType.WindowTypeClient);
				LOGGER.info("Session info: desired dimension={}x{}, desired depth={}", getDesiredHRes(),
						getDesiredVRes(), getDesiredColorDepth());
				LOGGER.info("Session info: dimension={}x{}, depth={}bpp", client.getSessionWidth(),
						client.getSessionHeight(), client.getSessionColorDepth());
				notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.SHOW));
			}

			@Override
			public void onWindowDestroyed(int wndType) {
				super.onWindowDestroyed(wndType);
				// Depends on wndType ?
				notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.HIDE));
			}

			@Override
			public void onDisconnect() {
				super.onDisconnect();

				// POSSIBLE_IMPROVEMENT Analyze why next lines throw TargetInvocationException
				// http://com4j.kohsuke.org/event.html
				// mouseCookie.close();
				// keyboardCookie.close();

				mouse = null;
				keyboard = null;

				// Emit Disconnect event
				notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.DISCONNECT));
			}

		});

		return client;
	}
}
