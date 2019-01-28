package com.blazemeter.jmeter.citrix.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.handler.CitrixClientHandler;

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
 *
 */
public abstract class AbstractCitrixClient implements CitrixClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCitrixClient.class);

	private Path icaFilePath;
	private final AtomicBoolean connected = new AtomicBoolean(false);
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicBoolean userLogged = new AtomicBoolean(false);
	private final AtomicBoolean visible = new AtomicBoolean(false);

	// Contains the list of the handler of this client.
	private final List<CitrixClientHandler> handlers = new ArrayList<>();

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
	protected abstract void doMouseButtonQuery(boolean buttonUp, Set<MouseButton> buttons, int x, int y,
			Set<Modifier> modifiers) throws CitrixClientException;

	/**
	 * Does a screenshot of the Citrix session screen
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
	protected abstract void doMouseMoveQuery(Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers)
			throws CitrixClientException;

	/**
	 * Notify all subscribers when a user interaction happens
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
	 * Notify all subscribers when a Citrix session event happens
	 * 
	 * @param event the session event to emit
	 */
	protected final void notifyHandlers(SessionEvent event) {
		if (event != null) {
			switch (event.getEventType()) {
			case CONNECT:
				LOGGER.debug("Set connected status to true.");
				connected.compareAndSet(false, true);
				break;
			case DISCONNECT:
				LOGGER.debug("Set connected status to false.");
				connected.compareAndSet(true, false);
				break;
			case LOGON:
				LOGGER.debug("Set user logged status to true.");
				userLogged.compareAndSet(false, true);
				break;
			case LOGOFF:
				LOGGER.debug("Set user logged status to false.");
				userLogged.compareAndSet(true, false);
				break;
			case SHOW:
				LOGGER.debug("Set visible status to true.");
				visible.compareAndSet(false, true);
				break;
			case HIDE:
				LOGGER.debug("Set visible status to false.");
				visible.compareAndSet(true, false);
				break;
			default:
				// NOOP
			}
			synchronized (handlers) {
				for (CitrixClientHandler handler : handlers) {
					handler.handleSessionEvent(event);
				}
			}
		}

	}

	/**
	 * Notify all subscribers when a window change happens
	 * 
	 * @param event the window change to emit
	 */
	protected final void notifyHandlers(WindowEvent event) {
		if (event != null) {
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
	protected abstract void startSession(boolean replayMode, boolean visible) throws CitrixClientException;

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
		}
	}

	@Override
	public final void removeHandler(CitrixClientHandler clientHandler) {
		synchronized (handlers) {
			handlers.remove(clientHandler);
		}
	}

	@Override
	public final void start(boolean replayMode, boolean visible) throws CitrixClientException {
		synchronized (running) {
			if (!running.get()) {
				startSession(replayMode, visible);
				running.set(true);
			}
		}
	}

	@Override
	public final void stop() throws CitrixClientException {
		synchronized (running) {
			if (running.get()) {
				stopSession();
				running.set(false);
			}
		}
	}

	@Override
	public final void sendKeyQuery(int keyCode, boolean keyUp) throws CitrixClientException {
		doKeyQuery(keyUp, keyCode);
		synchronized (handlers) {
			handlers.forEach(h -> h.handleKeyQuery(this, keyUp, keyCode));
		}
	}

	private Point getAbsolutePosition(Point relativePosition) {
		Rectangle area = getForegroundWindowArea();
		if (area == null) {
			String msg = "Unable to define absolute coordinates whereas no foreground window area is set.";
			LOGGER.error(msg);
			throw new IllegalStateException(msg);
		}
		final Point position = new Point(area.x + relativePosition.x, area.y + relativePosition.y);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Get absolute position {} from relative coordinates {} with foreground window area {}",
					position, relativePosition, area);
		}
		return position;
	}

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
			Set<Modifier> modifiers, boolean relative) throws CitrixClientException {
		sendPositionalQuery(relative, x, y, (pos, origPos) -> {
			doMouseButtonQuery(buttonUp, buttons, pos.x, pos.y, modifiers);
			synchronized (handlers) {
				handlers.forEach(h -> h.handleMouseButtonQuery(this, buttonUp, buttons, pos, modifiers, origPos));
			}
		});
	}

	@Override
	public final void sendMouseMoveQuery(Set<MouseButton> buttons, int x, int y, Set<Modifier> modifiers,
			boolean relative) throws CitrixClientException {
		sendPositionalQuery(relative, x, y, (pos, origPos) -> {
			doMouseMoveQuery(buttons, pos.x, pos.y, modifiers);
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
	public final synchronized Snapshot takeSnapshot() throws CitrixClientException {
		return new Snapshot(takeScreenshot(), getForegroundWindowArea());
	}

	private static interface PositionalQueryAction {
		void accept(Point position, Point originalPosition) throws CitrixClientException;
	}

}
