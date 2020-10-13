package com.blazemeter.jmeter.citrix.client.windows;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.factory.AbstractCitrixClientFactory;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinCitrixClientFactory extends AbstractCitrixClientFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(WinCitrixClientFactory.class);

  private static final String SCREENSHOT_DIR_PROPERTY = "screenshots_dir";
  private static final String KEEP_SCREENSHOTS_PROPERTY = "keep_screenshots";
  private static final String HORIZONTAL_RESOLUTION_PROPERTY = "horizontal_resolution";
  private static final String VERTICAL_RESOLUTION_PROPERTY = "vertical_resolution";
  private static final String COLOR_DEPTH_PROPERTY = "color_depth";

  private static final String ICAFILE_TIMEOUT = "icafile_timeout_ms";
  private static final String CONNECT_TIMEOUT = "connect_timeout_ms";
  private static final String LOGON_TIMEOUT = "logon_timeout_ms";
  private static final String LOGOFF_TIMEOUT = "logoff_timeout_ms";
  private static final String DISCONNECT_TIMEOUT = "disconnect_timeout_ms";
  private static final String SOCKET_TIMEOUT = "socket_timeout_ms";

  @Override
  public CitrixClient createClient() {
    WinCitrixClient client = new WinCitrixClient();

    // Configure client using properties map

    client.setScreenshotDirectory(getClientProperty(SCREENSHOT_DIR_PROPERTY));
    client.setKeepingScreenshots(
        Boolean.parseBoolean(getClientProperty(KEEP_SCREENSHOTS_PROPERTY))
    );
    client.setICAFileTimeoutInMs(
        getClientPropertyAsLong(ICAFILE_TIMEOUT, client.getICAFileTimeoutInMs())
    );
    client.setConnectTimeoutInMs(
        getClientPropertyAsLong(CONNECT_TIMEOUT, client.getConnectTimeoutInMs())
    );
    client.setLogonTimeoutInMs(
        getClientPropertyAsLong(LOGON_TIMEOUT, client.getLogonTimeoutInMs())
    );
    client.setLogoffTimeoutInMs(
        getClientPropertyAsLong(LOGOFF_TIMEOUT, client.getLogoffTimeoutInMs())
    );
    client.setDisconnectTimeoutInMs(
        getClientPropertyAsLong(DISCONNECT_TIMEOUT, client.getDisconnectTimeoutInMs())
    );
    client.setSocketTimeoutInMs(
        getClientPropertyAsLong(SOCKET_TIMEOUT, client.getSocketTimeoutInMs())
    );

    // Force desired HRes or VRes only when is defined in property
    if (!getClientProperty(HORIZONTAL_RESOLUTION_PROPERTY).isEmpty()) {
      client.setDesiredHRes(getClientPropertyAsInteger(HORIZONTAL_RESOLUTION_PROPERTY));
    }
    if (!getClientProperty(VERTICAL_RESOLUTION_PROPERTY).isEmpty()) {
      client.setDesiredVRes(getClientPropertyAsInteger(VERTICAL_RESOLUTION_PROPERTY));
    }

    // Force color depth only when is defined in property
    if (!getClientProperty(COLOR_DEPTH_PROPERTY).isEmpty()) {
      client.setDesiredColorDepth(
          Enum.valueOf(ICAColorDepth.class, getClientProperty(COLOR_DEPTH_PROPERTY))
      );
    }

    return client;
  }
}

