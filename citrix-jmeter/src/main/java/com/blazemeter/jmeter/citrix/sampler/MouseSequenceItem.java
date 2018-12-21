package com.blazemeter.jmeter.citrix.sampler;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;

public class MouseSequenceItem implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 6163197927231021148L;
    private MouseAction action;
	private int x;
	private int y;
	private final Set<MouseButton> buttons = EnumSet.noneOf(MouseButton.class);
	private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
	private long delay;

	public MouseSequenceItem() {
	}

	public MouseSequenceItem(MouseAction action, int x, int y, Set<MouseButton> buttons, Set<Modifier> modifiers,
			long delay) {
		this.setAction(action);
		this.setX(x);
		this.setY(y);
		if (buttons != null) {
			this.buttons.addAll(buttons);
		}
		if (modifiers != null) {
			this.modifiers.addAll(modifiers);
		}
		this.delay = delay;
	}

	public MouseAction getAction() {
		return action;
	}

	public void setAction(MouseAction action) {
		if (action == null) {
			throw new IllegalArgumentException("action cannot be null.");
		}
		this.action = action;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Set<MouseButton> getButtons() {
		return buttons;
	}

	public Set<Modifier> getModifiers() {
		return modifiers;
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