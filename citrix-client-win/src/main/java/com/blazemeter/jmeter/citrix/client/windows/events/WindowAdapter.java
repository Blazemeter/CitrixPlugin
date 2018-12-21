package com.blazemeter.jmeter.citrix.client.windows.events;

import java.util.Arrays;

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

	private void log(final String format, final Object... arguments) {
		int size = (arguments != null ? arguments.length : 0) + 1;
		Object[] args = Arrays.copyOf(arguments, size);
		args[size - 1] = Integer.valueOf(windowID);
		LOGGER.debug(format + " for window {}", args);
	}

	@Override
	public void onActivate() {
		log("onActivate");
	}

	@Override
	public void onCaptionChange(String caption) {
		log("onCaptionChange: caption={}", caption);
	}

	@Override
	public void onDeactivate() {
		log("onDeactivate");
	}

	@Override
	public void onDestroy() {
		log("onDestroy");
	}

	@Override
	public void onLargeIconChange(String largeIconHash) {
		log("onLargeIconChange: largeIconHash={}", largeIconHash);
	}

	@Override
	public void onMinimize() {
		log("onMinimize");
	}

	@Override
	public void onMove(int xPos, int yPos) {
		log("onMove: xPos={}, yPos={}", xPos, yPos);
	}

	@Override
	public void onSize(int width, int height) {
		log("onSize: width={}, height={}", width, height);
	}

	@Override
	public void onSmallIconChange(String smallIconHash) {
		log("onSmallIconChange: smallIconHash={}", smallIconHash);
	}

	@Override
	public void onStyleChange(int style, int extendedStyle) {
		log("onStyleChange: style={}, extendedStyle={}", style, extendedStyle);
	}
}
