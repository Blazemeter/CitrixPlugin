package com.blazemeter.jmeter.citrix.sampler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.client.events.Modifier;

/**
 * Sampler that contains informations of an interaction saved by a CitrixRecord.
 */
public class InteractionSampler extends CitrixBaseSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InteractionSampler.class);

	private static final long serialVersionUID = 6076968592699324616L;

	public static final long KEYSTROKE_DELAY = JMeterUtils
			.getPropDefault(CitrixUtils.PROPERTIES_PFX + "keystroke_delay", 100);

	public static final long KEYSTROKE_DELAY_VARIATION = JMeterUtils
			.getPropDefault(CitrixUtils.PROPERTIES_PFX + "keystroke_delay_variation", 10);

	private static final RandomDataGenerator VARIATION_GENERATOR = new RandomDataGenerator();

	public enum SamplerType {
		TEXT, KEY_SEQUENCE, MOUSE_CLICK, MOUSE_SEQUENCE
	}

	// + JMX file attributes
	private static final String SAMPLER_TYPE_PROP = "InteractionSampler.samplerType"; // $NON-NLS-1$
	private static final String INPUT_TEXT_PROP = "InteractionSampler.inputText"; // $NON-NLS-1$
	private static final String KEY_SEQUENCE_PROP = "InteractionSampler.keySequence"; // $NON-NLS-1$
	private static final String DOUBLE_CLICK_PROP = "InteractionSampler.doubleClick"; // $NON-NLS-1$
	private static final String MOUSE_X_PROP = "InteractionSampler.mouseX"; // $NON-NLS-1$
	private static final String MOUSE_Y_PROP = "InteractionSampler.mouseY"; // $NON-NLS-1$
	private static final String MOUSE_BUTTONS = "InteractionSampler.mouseButtons"; // $NON-NLS-1$
	private static final String MODIFIERS = "InteractionSampler.modifiers"; // $NON-NLS-1$
	private static final String MOUSE_SEQUENCE_PROP = "InteractionSampler.mouseSequence"; // $NON-NLS-1$
	private static final String RELATIVE_PROP = "InteractionSampler.relative"; // $NON-NLS-1$
	private static final String TIMESTAMP = "InteractionSampler.timestamp"; // $NON-NLS-1$

	private static long getNextVariation() {
		return VARIATION_GENERATOR.nextLong(-KEYSTROKE_DELAY_VARIATION, KEYSTROKE_DELAY_VARIATION);
	}

	public SamplerType getSamplerType() {
		return Enum.valueOf(SamplerType.class, getPropertyAsString(SAMPLER_TYPE_PROP));
	}

	public void setSamplerType(SamplerType samplerType) {
		if (samplerType == null) {
			throw new IllegalArgumentException("samplerType cannot be null.");
		}
		setProperty(SAMPLER_TYPE_PROP, samplerType.name());
	}

	public String getInputText() {
		return getPropertyAsString(INPUT_TEXT_PROP);
	}

	public void setInputText(String text) {
		setProperty(INPUT_TEXT_PROP, text);
	}

	@SuppressWarnings("unchecked")
	public List<KeySequenceItem> getKeySequence() {
		List<KeySequenceItem> sequence = (List<KeySequenceItem>) getProperty(KEY_SEQUENCE_PROP).getObjectValue();
		if (sequence == null) {
			sequence = new ArrayList<>();
			setProperty(new ObjectProperty(KEY_SEQUENCE_PROP, sequence));
		}
		return sequence;
	}

	@SuppressWarnings("unchecked")
	public List<MouseSequenceItem> getMouseSequence() {
		List<MouseSequenceItem> sequence = (List<MouseSequenceItem>) getProperty(MOUSE_SEQUENCE_PROP).getObjectValue();
		if (sequence == null) {
			sequence = new ArrayList<>();
			setProperty(new ObjectProperty(MOUSE_SEQUENCE_PROP, sequence));
		}
		return sequence;
	}

	@SuppressWarnings("unchecked")
	public Set<Modifier> getModifiers() {
		Set<Modifier> modifiers = (Set<Modifier>) getProperty(MODIFIERS).getObjectValue();
		if (modifiers == null) {
			modifiers = EnumSet.noneOf(Modifier.class);
			setProperty(new ObjectProperty(MODIFIERS, modifiers));
		}
		return modifiers;
	}

	@SuppressWarnings("unchecked")
	public Set<MouseButton> getMouseButtons() {
		Set<MouseButton> buttons = (Set<MouseButton>) getProperty(MOUSE_BUTTONS).getObjectValue();
		if (buttons == null) {
			buttons = EnumSet.noneOf(MouseButton.class);
			setProperty(new ObjectProperty(MOUSE_BUTTONS, buttons));
		}
		return buttons;
	}

	public boolean isDoubleClick() {
		return getPropertyAsBoolean(DOUBLE_CLICK_PROP);
	}

	public void setDoubleClick(boolean doubleClick) {
		setProperty(DOUBLE_CLICK_PROP, doubleClick);
	}

	public int getMouseX() {
		return getPropertyAsInt(MOUSE_X_PROP);
	}

	public String getMouseXAsString() {
		return getPropertyAsString(MOUSE_X_PROP);
	}

	public void setMouseX(String x) {
		setProperty(MOUSE_X_PROP, x);
	}

	public int getMouseY() {
		return getPropertyAsInt(MOUSE_Y_PROP);
	}

	public String getMouseYAsString() {
		return getPropertyAsString(MOUSE_Y_PROP);
	}

	public void setMouseY(String y) {
		setProperty(MOUSE_Y_PROP, y);
	}

	public boolean isRelative() {
		return getPropertyAsBoolean(RELATIVE_PROP);
	}

	public void setRelative(boolean relative) {
		setProperty(RELATIVE_PROP, relative);
	}

	public long getTimestamp() {
		return getPropertyAsLong(TIMESTAMP);
	}

	public void setTimestamp(long timestamp) {
		setProperty(TIMESTAMP, timestamp);
	}

	public InteractionSampler() {
		super(RunningClientPolicy.REQUIRED);
	}

	@Override
	protected SamplingHandler createHandler() {
		return new InteractionHandler();
	}

	@Override
	protected Long doClientAction(CitrixClient client) throws CitrixClientException, InterruptedException {
		switch (getSamplerType()) {
		case TEXT:
			sampleText(client);
			break;
		case KEY_SEQUENCE:
			sampleKeySequence(client);
			break;
		case MOUSE_CLICK:
			sampleClick(client);
			if (isDoubleClick()) {
				sampleClick(client);
			}
			break;
		case MOUSE_SEQUENCE:
			sampleMouseSequence(client);
			break;
		}

		return null;
	}

	private void sampleClick(CitrixClient client) throws CitrixClientException {
		final int x = getMouseX();
		final int y = getMouseY();
		final Set<MouseButton> buttons = getMouseButtons();
		final Set<Modifier> modifiers = getModifiers();
		final boolean relative = isRelative();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} samples click at position X={}, Y={} (relative={}) with buttons={} and modifiers={}",
					getThreadName(), x, y, relative, buttons, modifiers);
		}
		client.sendMouseButtonQuery(false, buttons, x, y, modifiers, relative);
		client.sendMouseButtonQuery(true, buttons, x, y, modifiers, relative);
	}

	private void sendKeystroke(CitrixClient client, int keyCode, boolean keyUp, boolean withDelay)
			throws CitrixClientException, InterruptedException {
		LOGGER.trace("{} sends key stroke keyCode=0x{}({}), keyState={}", getThreadName(), Long.toHexString(keyCode),
				keyCode, keyUp ? KeyState.KEY_UP : KeyState.KEY_DOWN);
		client.sendKeyQuery(keyCode, keyUp);
		if (withDelay) {
			long delay = KEYSTROKE_DELAY + getNextVariation();
			LOGGER.trace("{} waits {}ms before next key stroke", getThreadName(), delay);
			TimeUnit.MILLISECONDS.sleep(delay);
		}
	}

	private void sampleText(CitrixClient client) throws CitrixClientException, InterruptedException {
		final String inputText = getInputText();
		LOGGER.debug("{} samples text {}", getThreadName(), inputText);

		final char[] chars = inputText.toCharArray();
		for (int index = 0; index < chars.length; index++) {
			final char c = chars[index];
			sendKeystroke(client, c, false, true);
			sendKeystroke(client, c, true, index < chars.length - 1);
		}
	}

	private void sampleKeySequence(CitrixClient client) throws CitrixClientException, InterruptedException {
		final List<KeySequenceItem> keySequence = getKeySequence();
		if (keySequence != null) {
			LOGGER.debug("{} samples key sequence with {} items", getThreadName(), keySequence.size());
			for (KeySequenceItem item : keySequence) {
				final int keyCode = item.getKeyCode();
				final KeyState keyState = item.getKeyState();
				TimeUnit.MILLISECONDS.sleep(item.getDelay());
				LOGGER.trace("{} sends key: keyCode={}, keyState={}", getThreadName(), keyCode, keyState);
				client.sendKeyQuery(keyCode, keyState == KeyState.KEY_UP);
			}
		}
	}

	private void sampleMouseSequence(CitrixClient client) throws CitrixClientException, InterruptedException {
		final List<MouseSequenceItem> mouseSequence = getMouseSequence();
		if (mouseSequence != null) {
			LOGGER.debug("{} samples mouse sequence with {} items", getThreadName(), mouseSequence.size());
			for (MouseSequenceItem item : mouseSequence) {
				TimeUnit.MILLISECONDS.sleep(item.getDelay());
				final int x = item.getX();
				final int y = item.getY();
				final Set<MouseButton> buttons = item.getButtons();
				final Set<Modifier> modifiers = item.getModifiers();
				final boolean relative = isRelative();
				if (item.getAction() == MouseAction.MOVE) {
					LOGGER.trace("{} sends mouse move: X={}, Y={}, relative={}, buttons={}, modifiers={}",
							getThreadName(), x, y, relative, buttons, modifiers);
					client.sendMouseMoveQuery(buttons, x, y, modifiers, relative);
				} else {
					LOGGER.trace("{} sends mouse buttons change {}: X={}, Y={}, relative={}, buttons={}, modifiers={}",
							getThreadName(), item.getAction(), x, y, relative, buttons, modifiers);
					client.sendMouseButtonQuery(item.getAction() == MouseAction.BUTTON_UP, buttons, x, y, modifiers,
							relative);
				}
			}
		}
	}

	private static class InteractionHandler extends SamplingHandler {

		private final StringBuilder builder = new StringBuilder();
		private int count = 0;

		private String prefix(String msg) {
			return "Interaction #" + (++count) + ": " + msg + System.lineSeparator();
		}

		@Override
		public String getSamplerData() {
			return builder.toString();
		}

		@Override
		public void handleKeyQuery(CitrixClient source, boolean keyUp, int keyCode) {
			final String state = (keyUp ? KeyState.KEY_UP : KeyState.KEY_DOWN).name();
			builder.append(prefix(state + " Code=0x" + Integer.toHexString(keyCode) + ", Symbol=" + (char) keyCode));
		}

		@Override
		public void handleMouseButtonQuery(CitrixClient source, boolean buttonUp, Set<MouseButton> buttons, Point pos,
				Set<Modifier> modifiers, Point origPos) {
			final String state = (buttonUp ? MouseAction.BUTTON_UP : MouseAction.BUTTON_DOWN).name();
			String msg = origPos != null
					? state + " Position=" + pos + "(" + origPos + "), Buttons=" + buttons + ", Modifiers=" + modifiers
					: state + " Position=" + pos + ", Buttons=" + buttons + ", Modifiers=" + modifiers;
			builder.append(prefix(msg));
		}

		@Override
		public void handleMouseMoveQuery(CitrixClient source, Set<MouseButton> buttons, Point pos,
				Set<Modifier> modifiers, Point origPos) {
			String msg = MouseAction.MOVE + (origPos != null
					? " Position=" + pos + "(" + origPos + "), Buttons=" + buttons + ", Modifiers=" + modifiers
					: " Position=" + pos + ", Buttons=" + buttons + ", Modifiers=" + modifiers);
			builder.append(prefix(msg));
		}
	}
}
