package com.blazemeter.jmeter.citrix.client.factory;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a abstract base class for {@link CitrixClientFactory}.
 */
public abstract class AbstractCitrixClientFactory implements CitrixClientFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCitrixClientFactory.class);

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
   * Gets the value of the specified property using client properties.
   * </p>
   *
   * @param propertyName the name of the property
   * @return the value of the property or an empty string if the property is not
   * defined
   */
  public final String getClientProperty(String propertyName) {
    return getClientProperty(propertyName, "");
  }

  /**
   * <p>
   * Gets the value of the specified property using client properties.
   * </p>
   *
   * @param propertyName the name of the property
   * @param defaultValue the default value of the property
   * @return the value of the property or an empty string if the property is not
   * defined
   */
  public final String getClientProperty(String propertyName, String defaultValue) {
    return
        clientProperties != null ? clientProperties.getProperty(propertyName, defaultValue)
            : defaultValue;
  }

  /**
   * <p>
   * Gets the value as Long of the specified property using client properties.
   * </p>
   *
   * @param propertyName the name of the property
   * @param defaultValue the default value of the property as Long
   * @return the value as Long of the property or an empty string if the property is not
   * defined
   */
  public final Long getClientPropertyAsLong(String propertyName, Long defaultValue) {
    String prop = getClientProperty(propertyName, null);
    try {
      return prop != null ? Long.parseLong(prop) : defaultValue;
    } catch (NumberFormatException ex) {
      LOGGER.warn("Invalid value for property {}, using default value {}", propertyName,
          defaultValue);
      return defaultValue;
    }
  }

  /**
   * <p>
   * Gets the value as Long of the specified property using client properties.
   * </p>
   *
   * @param propertyName the name of the property
   * @return the value as Integer of the property or an empty string if the property is not
   * defined
   */
  public final Integer getClientPropertyAsInteger(String propertyName) {
    String prop = clientProperties.getProperty(propertyName, null);
    try {
      return prop != null ? Integer.parseInt(prop) : null;
    } catch (NumberFormatException ex) {
      LOGGER.warn("Invalid value for property {}", propertyName);
      return null;
    }
  }

  /**
   * <p>
   * Gets the value as Long of the specified property using client properties.
   * </p>
   *
   * @param propertyName the name of the property
   * @param defaultValue the default value of the property
   * @return the value as Integer of the property or an empty string if the property is not
   * defined
   */
  public final Integer getClientPropertyAsInteger(String propertyName, Integer defaultValue) {
    String prop = clientProperties.getProperty(propertyName, null);
    try {
      return prop != null ? Integer.parseInt(prop) : defaultValue;
    } catch (NumberFormatException ex) {
      LOGGER.warn("Invalid value for property {}, using default value {}", propertyName,
          defaultValue);
      return defaultValue;
    }
  }
}

