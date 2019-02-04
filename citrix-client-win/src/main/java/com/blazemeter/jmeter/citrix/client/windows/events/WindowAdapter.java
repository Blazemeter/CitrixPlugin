package com.blazemeter.jmeter.citrix.client.windows.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IWindowEvents;

public class WindowAdapter extends _IWindowEvents {

	private static final Logger LOGGER = LoggerFactory.getLogger(WindowAdapter.class);

	private final int windowID;

	public final int getWindowID() {
		return windowID;
	}

	public WindowAdapter(int windowID) {
		this.windowID = windowID;
	}

	@Override
	public void onActivate() {
		LOGGER.debug("onActivate for window {}", windowID);
	}

	@Override
	public void onCaptionChange(String caption) {
		LOGGER.debug("onCaptionChange for window {}: caption={}", windowID, caption);
	}

	@Override
	public void onDeactivate() {
		LOGGER.debug("onDeactivate for window {}", windowID);
	}

	@Override
	public void onDestroy() {
		LOGGER.debug("onDestroy for window {}", windowID);
	}

	@Override
	public void onLargeIconChange(String largeIconHash) {
		LOGGER.debug("onLargeIconChange for window {}: largeIconHash={}", windowID, largeIconHash);
	}

	@Override
	public void onMinimize() {
		LOGGER.debug("onMinimize for window {}", windowID);
	}

	@Override
	public void onMove(int xPos, int yPos) {
		LOGGER.debug("onMove for window {}: xPos={}, yPos={}", windowID, xPos, yPos);
	}

	@Override
	public void onSize(int width, int height) {
		LOGGER.debug("onSize for window {}: width={}, height={}", windowID, width, height);
	}

	@Override
	public void onSmallIconChange(String smallIconHash) {
		LOGGER.debug("onSmallIconChange for window {}: smallIconHash={}", windowID, smallIconHash);
	}

	@Override
	public void onStyleChange(int style, int extendedStyle) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("onStyleChange for window {}: style=0x{}, extendedStyle=0x{}", windowID,
					Integer.toHexString(style), Integer.toHexString(extendedStyle));
		}
	}
}
