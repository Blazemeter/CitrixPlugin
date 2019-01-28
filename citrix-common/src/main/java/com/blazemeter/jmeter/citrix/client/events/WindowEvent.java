package com.blazemeter.jmeter.citrix.client.events;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.WindowInfo;

/**
 * Provides events about Citrix in-session windows.
 */
public class WindowEvent extends ClientEvent {

	private static final long serialVersionUID = -1758399947925884485L;

	/**
	 * Enumerates the types of window events.
	 */
	public enum WindowState {
		/**
		 * The window has just been created
		 */
		CREATED,

		/**
		 * The window has just been closed
		 */
		CLOSED,

		/**
		 * The windows has just been activated
		 */
		ACTIVATED,

		/**
		 * THe window has just been deactivated
		 */
		DEACTIVATED,

		/**
		 * The window area has changed
		 */
		CHANGE_AREA,

		/**
		 * The window caption has changed
		 */
		CHANGE_CAPTION,

		/**
		 * The window gets the foregound
		 */
		FOREGROUND;
	}

	private final WindowState winState;
	private final WindowInfo winInfo;

	/**
	 * Instantiates a WindowEvent
	 * 
	 * @param source   the source of the event
	 * @param winState the type of foreground window change
	 * @param info     details about the window involved in the event
	 */
	public WindowEvent(CitrixClient source, WindowState winState, WindowInfo info) {
		super(source);
		if (winState == null) {
			throw new IllegalArgumentException("winState must not be null.");
		}
		this.winState = winState;
		this.winInfo = info;
	}

	/**
	 * Gets the type of window change
	 * 
	 * @return the type of window change
	 */
	public WindowState getWindowState() {
		return winState;
	}

	/**
	 * Gets details about the window involved in the event
	 * 
	 * @return details about the window involved in the event
	 */
	public WindowInfo getWindowInfo() {
		return winInfo;
	}

}
