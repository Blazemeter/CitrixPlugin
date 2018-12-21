package com.blazemeter.jmeter.citrix.client.factory;

import java.util.Properties;

/**
 * Provides a abstract base class for {@link CitrixClientFactory}
 *
 */
public abstract class AbstractCitrixClientFactory implements CitrixClientFactory {

	private Properties clientProperties;

	@Override
	public Properties getClientProperties() {
		return clientProperties;
	}

	@Override
	public void setClientProperties(Properties properties) {
		this.clientProperties = properties;
	}

	/**
	 * <p>
	 * Gets the value of the specified property using client properties
	 * </p>
	 * 
	 * @param propertyName the name of the property
	 * @return the value of the property or an empty string if the property is not
	 *         defined
	 */
	protected final String getClientProperty(String propertyName) {
		return clientProperties != null ? clientProperties.getProperty(propertyName, "") : "";
	}
}
