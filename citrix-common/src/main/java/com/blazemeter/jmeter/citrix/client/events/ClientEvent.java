package com.blazemeter.jmeter.citrix.client.events;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import java.io.Serializable;
import java.util.Date;

public class ClientEvent implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 2404479292064665761L;
  private final transient CitrixClient source;
  private final long timestamp;

  public ClientEvent(CitrixClient source) {
    this.source = source;
    this.timestamp = new Date().getTime();
  }

  public CitrixClient getSource() {
    return this.source;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
