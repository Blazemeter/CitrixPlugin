package com.blazemeter.jmeter.citrix.client.handler;

import java.awt.Point;
import java.util.Set;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.events.Modifier;

/**
 * Provides an adapter for Citrix client event handling.
 */
public class CitrixClientAdapter implements CitrixClientHandler {

	@Override
	public void handleInteractionEvent(InteractionEvent interactionEvent) {
		// NOOP
	}

	@Override
	public void handleSessionEvent(SessionEvent sessionEvent) {
		// NOOP
	}

	@Override
	public void handleWindowEvent(WindowEvent windowEvent) {
		// NOOP
	}

	@Override
	public void handleKeyQuery(CitrixClient source, boolean keyUp, int keyCode) {
		// NOOP
	}

	@Override
	public void handleMouseButtonQuery(CitrixClient source, boolean up, Set<MouseButton> buttons, Point position,
			Set<Modifier> modifiers, Point originalPosition) {
		// NOOP
	}

	@Override
	public void handleMouseMoveQuery(CitrixClient source, Set<MouseButton> buttons, Point position,
			Set<Modifier> modifiers, Point originalPosition) {
		// NOOP
	}

}
