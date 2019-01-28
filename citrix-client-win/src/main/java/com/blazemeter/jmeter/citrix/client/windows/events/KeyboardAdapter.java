package com.blazemeter.jmeter.citrix.client.windows.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IKeyboardEvents;

public class KeyboardAdapter extends _IKeyboardEvents {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardAdapter.class);

	@Override
	public void onKeyDown(int keyId, int modifierState) {
		LOGGER.debug("onKeyDown: keyId={} (0x{}, {}), modifierState=0b{}", keyId, Integer.toHexString(keyId),
				(char) keyId, Integer.toBinaryString(modifierState));
	}

	@Override
	public void onKeyUp(int keyId, int modifierState) {
		LOGGER.debug("onKeyUp: keyId={} (0x{}, {}), modifierState=0b{}", keyId, Integer.toHexString(keyId),
				modifierState, (char) keyId, Integer.toBinaryString(modifierState));
	}
}
