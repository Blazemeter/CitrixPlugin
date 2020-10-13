package com.blazemeter.jmeter.citrix.client.windows.events;

import com.blazemeter.jmeter.citrix.client.windows.com4j.IWindow;
import com.blazemeter.jmeter.citrix.client.windows.com4j.events._ISessionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionAdapter extends _ISessionEvents {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionAdapter.class);

  @Override
  public void onPingAck(String pingInfo, int roundTripTime) {
    LOGGER.debug("onPingAck: pingInfo={}, roundTripTime={}", pingInfo, roundTripTime);
  }

  @Override
  public void onWindowCreate(IWindow window) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("onWindowCreate: window=[ID={}, caption={}, style=0x{}, extendedStyle=0x{}]",
          window.windowID(),
          window.caption(), Integer.toHexString(window.style()),
          Integer.toHexString(window.extendedStyle()));
    }
  }

  @Override
  public void onWindowDestroy(IWindow window) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("onWindowDestroy: window=[ID={}, caption={}, style=0x{}, extendedStyle=0x{}]",
          window.windowID(),
          window.caption(), Integer.toHexString(window.style()),
          Integer.toHexString(window.extendedStyle()));
    }
  }

  @Override
  public void onWindowForeground(int windowID) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("onWindowForeground: windowID={}", windowID);
    }
  }

}
