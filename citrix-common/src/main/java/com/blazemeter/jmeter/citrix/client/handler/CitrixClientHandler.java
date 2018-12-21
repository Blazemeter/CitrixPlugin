package com.blazemeter.jmeter.citrix.client.handler;

import java.awt.Point;
import java.util.Set;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.ForegroundEvent;
import com.blazemeter.jmeter.citrix.client.events.Modifier;

/**
 * Represents a handler for {@link CitrixClient}
 * 
 */
public interface CitrixClientHandler {

	/**
	 * Handles the user interaction
	 * 
	 * @param interactionEvent the user interaction
	 */
	public void handleInteractionEvent(InteractionEvent interactionEvent);

	/**
	 * Handles the session event
	 * 
	 * @param sessionEvent the session event
	 */
	public void handleSessionEvent(SessionEvent sessionEvent);

	/**
	 * Handles the foreground window event
	 * 
	 * @param fgEvent the foreground windows event
	 */
	public void handleForegroundEvent(ForegroundEvent fgEvent);

	/**
	 * Handles a key query
	 * 
	 * @param source  the client where the query occurred
	 * @param up      true, if the key is up; false otherwise
	 * @param keyCode the code of the key
	 */
	public void handleKeyQuery(CitrixClient source, boolean up, int keyCode);

	/**
	 * Handles a mouse buttons change query
	 * 
	 * @param source           the client where the query occurred
	 * @param up               true, if the mouse buttons are up; false otherwise
	 * @param buttons          the set of involved mouse buttons
	 * @param position         the absolute position of mouse cursor
	 * @param modifiers        the set of modifier keys pressed when the query
	 *                         occurred
	 * @param originalPosition the relative to foreground window position of the
	 *                         mouse cursor; null if query is not relative
	 */
	public void handleMouseButtonQuery(CitrixClient source, boolean up, Set<MouseButton> buttons, Point position,
			Set<Modifier> modifiers, Point originalPosition);

	/**
	 * Handles a mouse move query
	 * 
	 * @param source           the client where the query occurred
	 * @param buttons          the set of pressed mouse buttons
	 * @param position         the absolute position of mouse cursor
	 * @param modifiers        the set of modifier keys pressed when the query
	 *                         occurred
	 * @param originalPosition the relative to foreground window position of the
	 *                         mouse cursor; null if query is not relative
	 */
	public void handleMouseMoveQuery(CitrixClient source, Set<MouseButton> buttons, Point position,
			Set<Modifier> modifiers, Point originalPosition);
}
