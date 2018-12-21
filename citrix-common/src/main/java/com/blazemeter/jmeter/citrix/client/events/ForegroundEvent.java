package com.blazemeter.jmeter.citrix.client.events;

import java.awt.Rectangle;
import com.blazemeter.jmeter.citrix.client.CitrixClient;

/**
 * Provides events about Citrix session foreground window.
 *
 */
public class ForegroundEvent extends ClientEvent {

	private static final long serialVersionUID = -1758399947925884485L;
	
	/**
	 * Enumerates the types of window events.
	 */
	public enum ForegroundState {
	/**
	 * Foreground area has changed
	 */
	CHANGE_AREA,

	/**
	 * A window is placed in the foreground
	 */
	NEW
	}

	private final ForegroundState fgState;
	private final Rectangle area;

	/**
	 * Instantiates a ForegroundEvent
	 * 
	 * @param source  the source of the event
	 * @param fgState the type of foreground window change
	 * @param area    the area of the window involved in the event
	 */
	public ForegroundEvent(CitrixClient source, ForegroundState fgState, Rectangle area) {
		super(source);
		if (fgState == null) {
			throw new IllegalArgumentException("fgState must not be null.");
		}
		this.fgState = fgState;
		this.area = area;
	}

	/**
	 * Gets the type of window change
	 * 
	 * @return the type of window change
	 */
	public ForegroundState getForegroundState() {
		return fgState;
	}

	/**
	 * Gets the area of the window involved in the event
	 * 
	 * @return the area of the window involved in the event
	 */
	public Rectangle getWindowArea() {
		return area;
	}

}
