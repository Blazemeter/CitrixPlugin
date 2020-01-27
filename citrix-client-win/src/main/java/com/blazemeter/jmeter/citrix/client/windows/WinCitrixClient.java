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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.AbstractCitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
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

	private IICAClient icaClient;

	private IKeyboard keyboard;
	private IMouse mouse;

	private Integer desiredHRes = null;
	private Integer desiredVRes = null;
	private ICAColorDepth desiredColorDepth = null;
	private Long socketTimeoutInMs = 5000L;

	// POSSIBLE_IMPROVEMENT See onDisconnect handler in createICAClient
	@SuppressWarnings("unused")
	private EventCookie keyboardCookie;
	@SuppressWarnings("unused")
	private EventCookie mouseCookie;

	private boolean isKeepingScreenshots;
	private String screenshotDirectory;

    private CountDownLatch icaFileReadyLatch = new CountDownLatch(1);
    
    private AtomicBoolean icaFileReady  = new AtomicBoolean(false);

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
		WindowInfo info = fgKey != null ? windowInfos.get(fgKey) : null;
		return info != null ? new Rectangle(info.getArea()) : null;
	}

	@Override
	public Collection<WindowInfo> getWindowInfos() {
		return new ArrayList<>(windowInfos.values());
	}

	@Override
	protected void startSession(boolean replayMode, boolean visible) throws CitrixClientException {
	    String threadName = Thread.currentThread().getName();
		try {
			icaClient = createICAClient(replayMode, visible);
            boolean expired = waitForIcaFile();
            if(!expired && icaFileReady.get()) {
                LOGGER.debug("Thread {} is connecting using ICA file {}", threadName, getICAFilePath());
                icaClient.connect();
            } else {
                if (expired) {
                    throw new CitrixClientException(Thread.currentThread().getName()+" timed out waiting for ICAFile to load from:"+getICAFilePath());
                } else {
                    throw new CitrixClientException(Thread.currentThread().getName()+" failed to load ICAFile from:"+getICAFilePath());
                }
            }
		} catch (Exception e) {
			final String msg = "Thread " + threadName + " is unable to start ICA session using path:"+getICAFilePath();
			LOGGER.error(msg, e);
			throw new CitrixClientException(msg, e);
		}
	}

	private boolean waitForIcaFile() throws InterruptedException {
	    boolean expired = false;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} starts to wait for ICAFile event at {}", Thread.currentThread().getName(), 
                    System.currentTimeMillis());
        }
        if (!icaFileReady.get()) {
            expired = !icaFileReadyLatch.await(5000, TimeUnit.MILLISECONDS);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} stops to wait for ICAFile event at {}, timeout expired: {}", Thread.currentThread().getName(),
                System.currentTimeMillis(), expired);
        }
        return expired;
    }

    @Override
	protected void stopSession() throws CitrixClientException {
	    String threadName = Thread.currentThread().getName();
		LOGGER.debug("Logging off ICA client for thread:{}", threadName);
		try {
            icaClient.logoff();
        } catch (Exception e) {
            LOGGER.error("Unable to logoff session for thread:{}", threadName, e);
        }

		try {
		    LOGGER.debug("Disconnecting ICA client for thread:{}", threadName);
		    icaClient.disconnect();
		} catch (Exception e) {
            LOGGER.error("Unable to disconnect session for thread:{}", threadName, e);
        }

		try {
		    LOGGER.debug("Disposing ICA client for thread:{}", threadName);
		    icaClient.dispose();
		} catch (Exception e) {
            LOGGER.error("Unable to dispose icaClient for thread:{}", threadName);
		}
		icaClient = null;
	}

	private IScreenShot createScreenshot(Rectangle selection) throws CitrixClientException {
	    String threadName = Thread.currentThread().getName();
		if (icaClient == null) {
			throw new CitrixClientException("Thread "+ threadName +" is unable to create screenshot when ICA client is not running.");
		}
		ISession session = icaClient.session();
		if (session == null) {
			throw new CitrixClientException("Thread "+ threadName +" is unable to create screenshot whereas Citrix session is not available.");
		}
		try {
			return selection != null
					? session.createScreenShot(selection.x, selection.y, selection.width, selection.height)
					: session.createFullScreenShot();
		} catch (ComException e) {
			String msg = MessageFormat.format(
					"Thread {6} is unable to create screenshot whereas Citrix session state is running={0}, connected={1}, visible={2}, userLogged={3} and selection={4}: {5}",
					isRunning(), isConnected(), isVisible(), isUserLogged(), selection, e.getMessage(), threadName);
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
					"Thread {6} is unable to save screenshot to file {0} whereas Citrix session state is running={1}, connected={2}, visible={3}, userLogged={4}: {5}",
					file.getAbsolutePath(), isRunning(), isConnected(), isVisible(), isUserLogged(), e.getMessage(), Thread.currentThread().getName());
			LOGGER.warn(msg, e);
			throw new CitrixClientException(msg);
		}
	}

	@Override
	protected BufferedImage doScreenshot() throws CitrixClientException {
	    String screenshotDirectory = getScreenshotDirectory();
	    if(screenshotDirectory == null || screenshotDirectory.length()==0) {
	        screenshotDirectory = new File(System.getProperty("java.io.tmpdir"),"jm_citrix_screenshots").getAbsolutePath();
        }
	    if(!Paths.get(screenshotDirectory).toFile().exists()) {
            LOGGER.info("Creating screenshot dir {}", screenshotDirectory);
	        if (!Paths.get(screenshotDirectory).toFile().mkdirs()) {
	            LOGGER.error("Unable to create screenshot dir {}", screenshotDirectory);
	        }
        }
		final Path filePath = Paths.get(screenshotDirectory, UUID.randomUUID().toString());
		final File file = filePath.toFile();
		IScreenShot screenshot = createScreenshot(null);
		saveScreenshot(screenshot, file);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Screenshot saved in {}", file.getAbsolutePath());
		}
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			String msg = MessageFormat.format("Thread {2} is unable to read screenshot file {0}: {1}", file.getAbsolutePath(),
					e.getMessage(), Thread.currentThread().getName());
			LOGGER.warn(msg, e);
			throw new CitrixClientException(msg);
		} finally {
			if (!isKeepingScreenshots) {
				try {
					Files.delete(filePath);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Deletes screenshot {}", file.getAbsolutePath());
					}
				} catch (IOException e) {
					LOGGER.warn("Unable to delete screenshot {}: {}", file.getAbsolutePath(), e.getMessage(), e);
				}
			}
		}
	}

	/* Ensure keyboard interface is initialized */
	private IKeyboard ensureKeyboard() throws CitrixClientException {
		if (keyboard == null) {
			throw new CitrixClientException("Thread "+ Thread.currentThread().getName() +" cannot send key event whereas Citrix session is disconnected");
		}
		return keyboard;
	}

	/* Ensure mouse interface is initialized */
	private IMouse ensureMouse() throws CitrixClientException {
		if (mouse == null) {
			throw new CitrixClientException("Thread "+ Thread.currentThread().getName() +" cannot send mouse event whereas Citrix session is disconnected");
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
		final WindowInfo info = fgKey != null ? windowInfos.get(fgKey) : null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Switches foreground to window {} with caption '{}'", key,
					info != null ? info.getCaption() : null);
		}
		notifyHandlers(createWindowEvent(WindowState.FOREGROUND, info));
	}

	private IICAClient createICAClient(boolean replayMode, boolean visible) {
	    String threadName = Thread.currentThread().getName();
	    LOGGER.debug("Create a new ICA client with replayMode={} and visible={}", replayMode, visible);
		IICAClient client = ClassFactory.createICAClient();
		
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
					LOGGER.debug("ICA session in not available while client connects");
					notifyHandlers(new SessionEvent(WinCitrixClient.this, EventType.ERROR,
							KnownError.UNAVAILABLE_SESSION.getCode()));
				} else {
					// Prevent input when replay
					LOGGER.debug("Sets ICA client replay mode to {}", replayMode);
					session.replayMode(replayMode);

					session.advise(_ISessionEvents.class, new SessionAdapter() {
						private Integer delayedFgKey;

						@Override
						public void onWindowCreate(IWindow window) {
							super.onWindowCreate(window);

							final int windowID = window.windowID();
							final Integer key = Integer.valueOf(windowID);
							Rectangle newArea = new Rectangle(window.positionX(), window.positionY(), window.width(),
									window.height());
							String caption = window.caption();
							windowInfos.put(key, new WindowInfo(newArea, caption));
							LOGGER.debug("Sets window info:[caption={}, area={}] to window {}", caption, newArea,
									windowID);

							if (key.equals(delayedFgKey)) {
								LOGGER.debug("Confirm window {} as foreground.", delayedFgKey);
								switchForeground(key);
							}
							
							window.advise(_IWindowEvents.class, new WindowAdapter(windowID) {

								@Override
								public void onMove(int xPos, int yPos) {
									super.onMove(xPos, yPos);
									final WindowInfo winInfo = windowInfos.get(key);
									if (winInfo != null) {
										final Rectangle area = winInfo.getArea();
										area.setLocation(xPos, yPos);
										LOGGER.debug("Sets window position={} to window {}", area.getLocation(),
												windowID);
										notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
									} else {
										LOGGER.debug("Should not happen : Unable to move the disposed window {}",
												windowID);
									}
								}

								@Override
								public void onSize(int width, int height) {
									super.onSize(width, height);
									final WindowInfo winInfo = windowInfos.get(key);
									if (winInfo != null) {
										final Rectangle area = winInfo.getArea();
										area.setSize(width, height);
										LOGGER.debug("Sets window dimension={} to window {}", area.getSize(), windowID);
										notifyHandlers(createWindowEvent(WindowState.CHANGE_AREA, winInfo));
									} else {
										LOGGER.debug("Should not happen : Unable to resize the disposed window {}",
												windowID);
									}
								}

								@Override
								public void onDestroy() {
									super.onDestroy();
									if (key.equals(fgKey)) {
										switchForeground(null);
									}
									final WindowInfo winInfo = windowInfos.remove(key);
									LOGGER.debug("Removes window info of window {}", windowID);
									notifyHandlers(createWindowEvent(WindowState.CLOSED, winInfo));
								}

								@Override
								public void onCaptionChange(String caption) {
									super.onCaptionChange(caption);
									final WindowInfo winInfo = windowInfos.get(key);
									if (winInfo != null) {
										winInfo.setCaption(caption);
										LOGGER.debug("Sets window caption={} to window {}", caption, windowID);
										notifyHandlers(createWindowEvent(WindowState.CHANGE_CAPTION, winInfo));
									} else {
										LOGGER.debug(
												"Should not happen : Unable to change caption of the disposed window {}",
												windowID);
									}
								}

								@Override
								public void onActivate() {
									super.onActivate();
									final WindowInfo winInfo = windowInfos.get(key);
									if (winInfo != null) {
										LOGGER.debug("Activates window {}", windowID);
										notifyHandlers(createWindowEvent(WindowState.ACTIVATED, winInfo));
									} else {
										LOGGER.debug("Should not happen : Unable to activate the disposed window {}",
												windowID);
									}
								}

								@Override
								public void onDeactivate() {
									super.onDeactivate();
									final WindowInfo winInfo = windowInfos.get(key);
									if (winInfo != null) {
										LOGGER.debug("Deactivates window {}", windowID);
										notifyHandlers(createWindowEvent(WindowState.DEACTIVATED, winInfo));
									} else {
										LOGGER.debug("Should not happen : Unable to deactivate the disposed window {}",
												windowID);
									}
								}

							});

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
								Integer key = Integer.valueOf(windowID);
								if (windowInfos.containsKey(key)) {
									switchForeground(key);
								} else {
									LOGGER.debug("Potential foreground window: {}", key);
									delayedFgKey = key;
								}
							} else {
								LOGGER.debug("Cannot switch foreground when windowId=0");
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

            /* (non-Javadoc)
             * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFile()
             */
            @Override
            public void onICAFile() {
                super.onICAFile();
                icaFileReady.compareAndSet(false, true);
                icaFileReadyLatch.countDown();
            }

            /* (non-Javadoc)
             * @see com.blazemeter.jmeter.citrix.client.windows.events.ICAClientAdapter#onICAFileFailed()
             */
            @Override
            public void onICAFileFailed() {
                super.onICAFileFailed();
                LOGGER.error("ICAFileFailed");
                icaFileReadyLatch.countDown();
            }
		});

		final Path path = getICAFilePath();
        if (path != null && path.toFile().exists() && path.toFile().canRead()) {
            //LOGGER.debug("Sets ICA client ICA file path to {}", path);
            //client.icaFile(path.toString());
            LOGGER.debug("Thread {} is loading ICA file path from {}", threadName, path);
            client.loadIcaFile(path.toString());
        }
		return client;
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
}
