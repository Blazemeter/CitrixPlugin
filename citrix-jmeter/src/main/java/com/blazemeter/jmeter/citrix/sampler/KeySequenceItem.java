package com.blazemeter.jmeter.citrix.sampler;

import java.io.Serializable;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;

public class KeySequenceItem implements Serializable {
	
	private static final long serialVersionUID = 2935074902697920184L;
    
	private KeyState keyState;
	private int keyCode;
	private long delay;

	public KeySequenceItem() {
	}

	public KeySequenceItem(KeyState keyState, int keyCode, long delay) {
		this.setKeyState(keyState);
		this.setKeyCode(keyCode);
		this.delay = delay;
	}

	/**
	 * @return the keyState
	 */
	public KeyState getKeyState() {
		return keyState;
	}

	/**
	 * @param keyState the keyState to set
	 */
	public void setKeyState(KeyState keyState) {
		if (keyState == null) {
			throw new IllegalArgumentException("keyState cannot be null");
		}
		this.keyState = keyState;
	}

	/**
	 * @return the keyCode
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * @param keyCode the keyCode to set
	 */
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
}