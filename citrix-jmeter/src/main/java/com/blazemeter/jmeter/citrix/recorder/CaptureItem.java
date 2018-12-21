package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;

/**
 * Capture items are labeled Citrix interaction events
 */
public class CaptureItem {
	private String label;
	private final InteractionEvent event;

	/**
	 * Gets the label of this capture
	 * 
	 * @return the label of this capture
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Defines the label of this capture
	 * 
	 * @param label the lable to use
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the inner Citrix interaction event
	 * 
	 * @return the inner Citrix interaction event
	 */
	public InteractionEvent getEvent() {
		return event;
	}

	/**
	 * Instantiates a new {@link CaptureItem} for the specified event
	 * 
	 * @param event the Citrix interaction event to encapsulate
	 */
	public CaptureItem(InteractionEvent event) {
		if (event == null)
			throw new IllegalArgumentException("event cannot be null.");
		this.event = event;
	}
}