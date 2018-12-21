package com.blazemeter.jmeter.citrix.client.events;

import com.blazemeter.jmeter.citrix.client.CitrixClient;

/**
 * Represents details about events occured during a Citrix session.
 */
public interface EventInfo {

	/**
	 * Gets the source of this event
	 * 
	 * @return the source of this event
	 */
	CitrixClient getSource();

	/**
	 * Gets the timestamp of this event
	 * 
	 * @return the timestamp of this event
	 */
	long getTimestamp();
}
