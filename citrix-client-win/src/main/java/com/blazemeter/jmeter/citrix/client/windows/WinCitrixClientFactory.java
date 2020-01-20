package com.blazemeter.jmeter.citrix.client.windows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.factory.AbstractCitrixClientFactory;
import com.blazemeter.jmeter.citrix.client.windows.com4j.ICAColorDepth;

public class WinCitrixClientFactory extends AbstractCitrixClientFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(WinCitrixClientFactory.class);

	private static final String SCREENSHOT_DIR_PROPERTY = "screenshots_dir";
	private static final String KEEP_SCREENSHOTS_PROPERTY = "keep_screenshots";
	private static final String HORIZONTAL_RESOLUTION_PROPERTY = "horizontal_resolution";
	private static final String VERTICAL_RESOLUTION_PROPERTY = "vertical_resolution";
	private static final String COLOR_DEPTH_PROPERTY = "color_depth";
	private static final String SOCKET_TIMEOUT = "socket_timeout_ms";
	@Override
	public CitrixClient createClient() {
		WinCitrixClient client = new WinCitrixClient();

		// Configure client using properties map

		// Screenshot config
		client.setScreenshotDirectory(getClientProperty(SCREENSHOT_DIR_PROPERTY));
		client.setKeepingScreenshots(Boolean.parseBoolean(getClientProperty(KEEP_SCREENSHOTS_PROPERTY)));

		String socketTimeoutInString = getClientProperty(SOCKET_TIMEOUT);
        if (socketTimeoutInString.length() > 0) {
            try {
                client.setSocketTimeoutInMs(Long.valueOf(socketTimeoutInString));
            } catch (NumberFormatException ex) {
                LOGGER.warn("Invalid value for property {}, socket timeout will default to: {}",
                        SOCKET_TIMEOUT, client.getSocketTimeoutInMs(), ex);
            }
        }
        
		// Horizontal resolution
		String hResProperty = getClientProperty(HORIZONTAL_RESOLUTION_PROPERTY);
		if (hResProperty.length() > 0) {
			try {
				client.setDesiredHRes(Integer.valueOf(hResProperty));
			} catch (NumberFormatException ex) {
				LOGGER.warn("Invalid value for property {}, horizontal resolution will not be forced: {}",
						HORIZONTAL_RESOLUTION_PROPERTY, ex.getMessage());
			}
		}

		// Vertical resolution
		String vResProperty = getClientProperty(VERTICAL_RESOLUTION_PROPERTY);
		if (vResProperty.length() > 0) {
			try {
				client.setDesiredVRes(Integer.valueOf(vResProperty));
			} catch (NumberFormatException ex) {
				LOGGER.warn("Invalid value for property {}, vertical resolution will not be forced: {}",
						VERTICAL_RESOLUTION_PROPERTY, ex.getMessage());
			}
		}

		// Color depth
		String colorDepth = getClientProperty(COLOR_DEPTH_PROPERTY);
		if (colorDepth.length() > 0) {
			try {
				client.setDesiredColorDepth(Enum.valueOf(ICAColorDepth.class, colorDepth));
			} catch (IllegalArgumentException ex) {
				LOGGER.warn("Invalid value for property {}, color depth will not be forced: {}", COLOR_DEPTH_PROPERTY,
						ex.getMessage());
			}
		}

		return client;
	}

}
