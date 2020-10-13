package com.blazemeter.jmeter.citrix.client.windows.com4j;

import com4j.COM4J;
import com4j.Com4jObject;

/**
 * Defines methods to create COM objects.
 */
public abstract class ClassFactory {
  private ClassFactory() {
  } // instanciation is not allowed


  /**
   * Citrix ICA Client.
   */
  public static com.blazemeter.jmeter.citrix.client.windows.com4j.IICAClient createICAClient() {
    return COM4J.createInstance(com.blazemeter.jmeter.citrix.client.windows.com4j.IICAClient.class,
        "{238F6F83-B8B4-11CF-8771-00A024541EE3}");
  }

  /**
   * ICA Client Properties.
   */
  public static Com4jObject createICAClientProp() {
    return COM4J.createInstance(Com4jObject.class, "{238F6F85-B8B4-11CF-8771-00A024541EE3}");
  }
}
