package com.blazemeter.jmeter.citrix.client;

import com.blazemeter.jmeter.citrix.client.factory.CitrixClientFactory;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Properties;
import org.apache.commons.lang3.SystemUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client factory that returns the right client according to the specified one
 * in the jmeter.properties file.
 */
public class CitrixClientFactoryHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixClientFactoryHelper.class);

  private static final String FACTORY_PROPERTY = CitrixUtils.PROPERTIES_PFX + "client_factory";
  private static final String CLIENT_PROPERTIES_PFX = FACTORY_PROPERTY + ".client_property.";
  private static final int CLIENT_PROPERTIES_PFX_LENGTH = CLIENT_PROPERTIES_PFX.length();

  private static final String FACTORY_DEFAULT_WINDOWS =
      "com.blazemeter.jmeter.citrix.client.windows.WinCitrixClientFactory";
  private static final String FACTORY_DEFAULT_OTHER =
      "com.blazemeter.jmeter.citrix.client.mock.MockCitrixClientFactory";

  private static CitrixClientFactory instance;

  private CitrixClientFactoryHelper() {
  }

  /**
   * <p>
   * Gets the singleton instance of the factory.
   * </p>
   *
   * @return a factory defined on jmeter.properties
   * @throws CitrixClientFactoryException when the property is wrong
   */
  public static synchronized CitrixClientFactory getInstance() throws CitrixClientFactoryException {
    if (instance == null) {
      Properties properties = JMeterUtils.getJMeterProperties();
      String defaultValue =
          SystemUtils.IS_OS_WINDOWS ? FACTORY_DEFAULT_WINDOWS : FACTORY_DEFAULT_OTHER;
      String selectedClass = properties.getProperty(FACTORY_PROPERTY, defaultValue);
      try {
        Class<?> clazz = Class.forName(selectedClass);
        Constructor<?> constructor = clazz.getConstructor();
        instance = (CitrixClientFactory) constructor.newInstance();
        Properties clientProperties = new Properties();
        for (String key : properties.stringPropertyNames()) {
          if (key.startsWith(CLIENT_PROPERTIES_PFX)) {
            clientProperties
                .put(key.substring(CLIENT_PROPERTIES_PFX_LENGTH), properties.getProperty(key));
          }
        }
        instance.setClientProperties(clientProperties);
      } catch (Exception e) {
        String msg = MessageFormat
            .format("Unable to create client factory {0} from JMeter property {1}: {2}",
                selectedClass, FACTORY_PROPERTY, e.getMessage());
        LOGGER.error(msg);
        throw new CitrixClientFactoryException(msg, e);
      }
    }
    return instance;
  }
}
