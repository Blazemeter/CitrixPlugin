package com.blazemeter.jmeter.citrix.client.events;

/**
 * Defines the modifiers keys that impact the events state
 */
public enum Modifier {
	/**
	 * Shift key is pressed
	 */
	SHIFT(1),
	/**
	 * Control key is pressed
	 */
	CONTROL(2),
	/**
	 * Alt key is pressed
	 */
	ALT(4),
	/**
	 * Used to build extended modifier state
	 */
	EXTENDED(8);

	private final int value;

	Modifier(int value) {
		this.value = value;
	}
	
	/**
	 * Get the integer value of the modifier
	 * @return the integer value of the modifier
	 */
	public int getValue() {
		return value;
	}
}