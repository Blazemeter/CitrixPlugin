package com.blazemeter.jmeter.citrix.client.factory;

import java.util.Properties;

import com.blazemeter.jmeter.citrix.client.CitrixClient;

/**
 * Represents a factory use to instantiate {@link CitrixClient} objects.
 *
 */
public interface CitrixClientFactory {

	/**
	 * Create a new {@link CitrixClient}. <br>
	 * The Citrix client type depends on this factory implementation.
	 * 
	 * @return A Citrix client
	 */
	public CitrixClient createClient();

	/**
	 * Gets the properties used to initialize client
	 * 
	 * @return the properties used to initialize client
	 */
	public Properties getClientProperties();

	/**
	 * Sets the properties used to initialize client
	 * 
	 * @param properties the properties used to initialize client
	 */
	public void setClientProperties(Properties properties);

}
