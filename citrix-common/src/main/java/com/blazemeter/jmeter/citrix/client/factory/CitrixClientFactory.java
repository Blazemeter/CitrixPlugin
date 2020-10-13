package com.blazemeter.jmeter.citrix.client.factory;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import java.util.Properties;

/**
 * Represents a factory use to instantiate {@link CitrixClient} objects.
 */
public interface CitrixClientFactory {

  /**
   * Create a new {@link CitrixClient}. <br>
   * The Citrix client type depends on this factory implementation.
   *
   * @return A Citrix client
   */
  CitrixClient createClient();

  /**
   * Gets the properties used to initialize client.
   *
   * @return the properties used to initialize client
   */
  Properties getClientProperties();

  /**
   * Sets the properties used to initialize client.
   *
   * @param properties the properties used to initialize client
   */
  void setClientProperties(Properties properties);

}
