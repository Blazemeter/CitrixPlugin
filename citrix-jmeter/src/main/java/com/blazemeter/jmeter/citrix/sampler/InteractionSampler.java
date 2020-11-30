package com.blazemeter.jmeter.citrix.sampler;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.Win32Utils;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a sampler that can replay Citrix user interactions.
 */
public class InteractionSampler extends CitrixBaseSampler {

  public static final long KEYSTROKE_DELAY = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "keystroke_delay", 100);
  public static final long KEYSTROKE_DELAY_VARIATION = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "keystroke_delay_variation", 10);
  private static final Logger LOGGER = LoggerFactory.getLogger(InteractionSampler.class);
  private static final long serialVersionUID = 6076968592699324616L;
  private static final RandomDataGenerator VARIATION_GENERATOR = new RandomDataGenerator();
  private static final String INPUT_TEXT_VERSION = "2.0";

  // + JMX file attributes
  private static final String SAMPLER_TYPE_PROP = "InteractionSampler.samplerType"; // $NON-NLS-1$
  private static final String INPUT_TEXT_PROP = "InteractionSampler.inputText"; // $NON-NLS-1$
  private static final String INPUT_TEXT_VER_PROP = "InteractionSampler.inputTextVersion";
  private static final String KEY_SEQUENCE_PROP = "InteractionSampler.keySequence"; // $NON-NLS-1$
  private static final String DOUBLE_CLICK_PROP = "InteractionSampler.doubleClick"; // $NON-NLS-1$
  private static final String MOUSE_X_PROP = "InteractionSampler.mouseX"; // $NON-NLS-1$
  private static final String MOUSE_Y_PROP = "InteractionSampler.mouseY"; // $NON-NLS-1$
  private static final String MOUSE_BUTTONS = "InteractionSampler.mouseButtons"; // $NON-NLS-1$
  private static final String MODIFIERS = "InteractionSampler.modifiers"; // $NON-NLS-1$
  private static final String MOUSE_SEQUENCE_PROP = "InteractionSampler.mouseSequence";
  // $NON-NLS-1$
  private static final String RELATIVE_PROP = "InteractionSampler.relative"; // $NON-NLS-1$
  private static final String TIMESTAMP = "InteractionSampler.timestamp"; // $NON-NLS-1$

  public InteractionSampler() {
    super(RunningClientPolicy.REQUIRED);
  }

  // Gets the next duration variation for waiting times between keystrokes
  private static long getNextVariation() {
    return VARIATION_GENERATOR.nextLong(-KEYSTROKE_DELAY_VARIATION, KEYSTROKE_DELAY_VARIATION);
  }

  private boolean isUnVersionedTestElement() {
    return getInputTextVersion().isEmpty(); // It is versioned from version 0.7.0
  }

  /**
   * Gets the type of interaction to sample.
   *
   * @return the type of interaction to sample
   */
  public SamplerType getSamplerType() {
    return Enum.valueOf(SamplerType.class, getPropertyAsString(SAMPLER_TYPE_PROP));
  }

  /**
   * Defines the type of interaction to sample.
   *
   * @param samplerType the type of interaction to sample
   */
  public void setSamplerType(SamplerType samplerType) {
    if (samplerType == null) {
      throw new IllegalArgumentException("samplerType cannot be null.");
    }
    setProperty(SAMPLER_TYPE_PROP, samplerType.name());
  }

  /**
   * Gets the text whose keystrokes to simulate when the sample type is
   * {@link SamplerType#TEXT}.
   *
   * @return the text to simulate
   */
  public String getInputText() {
    if (isUnVersionedTestElement()) {
      // Prior to version 0.7.0, the only text recorded was A-Z in Uppercase.
      // It is migrated to lowercase for compatibility with the new InputText.
      setInputText(getPropertyAsString(INPUT_TEXT_PROP).toLowerCase());
    }
    return getPropertyAsString(INPUT_TEXT_PROP);
  }

  /**
   * Defines the text whose keystrokes to simulate when the sample type is
   * {@link SamplerType#TEXT}.
   *
   * @param text the text to simulate
   */
  public void setInputText(String text) {
    if (isUnVersionedTestElement()) {
      setInputTextVersion(INPUT_TEXT_VERSION);
    }
    setProperty(INPUT_TEXT_PROP, text);
  }

  public String getInputTextVersion() {
    return getPropertyAsString(INPUT_TEXT_VER_PROP);
  }

  public void setInputTextVersion(String text) {
    setProperty(INPUT_TEXT_VER_PROP, text);
  }


  /**
   * Gets the lists of keystrokes to simulate when sample type is
   * {@link SamplerType#KEY_SEQUENCE}.
   *
   * @return the lists of keystrokes to simulate
   */
  @SuppressWarnings("unchecked")
  public List<KeySequenceItem> getKeySequence() {
    List<KeySequenceItem> sequence =
        (List<KeySequenceItem>) getProperty(KEY_SEQUENCE_PROP).getObjectValue();
    if (sequence == null) {
      sequence = new ArrayList<>();
      setProperty(new ObjectProperty(KEY_SEQUENCE_PROP, sequence));
    }
    return sequence;
  }

  /**
   * Gets the lists of mouse events to simulate when sample type is
   * {@link SamplerType#MOUSE_SEQUENCE}.
   *
   * @return the lists of mouse events to simulate
   */
  @SuppressWarnings("unchecked")
  public List<MouseSequenceItem> getMouseSequence() {
    List<MouseSequenceItem> sequence =
        (List<MouseSequenceItem>) getProperty(MOUSE_SEQUENCE_PROP).getObjectValue();
    if (sequence == null) {
      sequence = new ArrayList<>();
      setProperty(new ObjectProperty(MOUSE_SEQUENCE_PROP, sequence));
    }
    return sequence;
  }

  /**
   * Gets the modifier keys to simulate when sample type is
   * {@link SamplerType#MOUSE_CLICK}.
   *
   * @return the modifier keys to simulate
   */
  @SuppressWarnings("unchecked")
  public Set<Modifier> getModifiers() {
    Set<Modifier> modifiers = (Set<Modifier>) getProperty(MODIFIERS).getObjectValue();
    if (modifiers == null) {
      modifiers = EnumSet.noneOf(Modifier.class);
      setProperty(new ObjectProperty(MODIFIERS, modifiers));
    }
    return modifiers;
  }

  /**
   * Gets the mouse buttons to simulate when sample type is
   * {@link SamplerType#MOUSE_CLICK}.
   *
   * @return the mouse buttons to simulate
   */
  @SuppressWarnings("unchecked")
  public Set<MouseButton> getMouseButtons() {
    Set<MouseButton> buttons = (Set<MouseButton>) getProperty(MOUSE_BUTTONS).getObjectValue();
    if (buttons == null) {
      buttons = EnumSet.noneOf(MouseButton.class);
      setProperty(new ObjectProperty(MOUSE_BUTTONS, buttons));
    }
    return buttons;
  }

  /**
   * Indicates whether the mouse interaction is a double-click when sampler type
   * is {@link SamplerType#MOUSE_CLICK}.
   *
   * @return true if the mouse interaction is a double-click; false otherwise
   */
  public boolean isDoubleClick() {
    return getPropertyAsBoolean(DOUBLE_CLICK_PROP);
  }

  /**
   * Defines whether the mouse interaction is a double-click when sampler type is
   * {@link SamplerType#MOUSE_CLICK}.
   *
   * @param doubleClick true to simulate a double click
   */
  public void setDoubleClick(boolean doubleClick) {
    setProperty(DOUBLE_CLICK_PROP, doubleClick);
  }

  public int getMouseX() {
    return getPropertyAsInt(MOUSE_X_PROP);
  }

  public void setMouseX(String x) {
    setProperty(MOUSE_X_PROP, x);
  }

  public String getMouseXAsString() {
    return getPropertyAsString(MOUSE_X_PROP);
  }

  public int getMouseY() {
    return getPropertyAsInt(MOUSE_Y_PROP);
  }

  public void setMouseY(String y) {
    setProperty(MOUSE_Y_PROP, y);
  }

  public String getMouseYAsString() {
    return getPropertyAsString(MOUSE_Y_PROP);
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

  @Override
  protected SamplingHandler createHandler() {
    return new InteractionHandler(getName());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler#doClientAction(com.
   * blazemeter.jmeter.citrix.client.CitrixClient)
   *
   * Simulates Citrix user interactions with the specified client
   */
  @Override
  protected void doClientAction(CitrixClient client)
      throws CitrixClientException, InterruptedException {
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
      default:
        break;
    }
  }

  private void sampleClick(CitrixClient client) throws CitrixClientException {
    final int x = getMouseX();
    final int y = getMouseY();
    final Set<MouseButton> buttons = getMouseButtons();
    final Set<Modifier> modifiers = getModifiers();
    final boolean relative = isRelative();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "{} on sampler {} samples click at pos X={}, Y={} (rel={}) with butt={} and modifiers={}",
          getThreadName(), getName(), x, y, relative, buttons, modifiers);
    }
    client.sendMouseButtonQuery(false, buttons, x, y, modifiers, relative);
    client.sendMouseButtonQuery(true, buttons, x, y, modifiers, relative);
  }

  private void sendKeystroke(CitrixClient client, int keyCode, boolean keyUp, boolean withDelay)
      throws CitrixClientException, InterruptedException {
    if (LOGGER.isTraceEnabled()) {
      LOGGER
          .trace("{} on sampler {} sends key stroke keyCode=0x{}({}), keyState={}", getThreadName(),
              getName(),
              Long.toHexString(keyCode), keyCode, keyUp ? KeyState.KEY_UP : KeyState.KEY_DOWN);
    }
    client.sendKeyQuery(keyCode, keyUp);
    if (withDelay) {
      long delay = KEYSTROKE_DELAY + getNextVariation();
      LOGGER.trace("{} waits {}ms before next key stroke", getThreadName(), delay);
      TimeUnit.MILLISECONDS.sleep(delay);
    }
  }

  private void sampleText(CitrixClient client) throws CitrixClientException, InterruptedException {
    String inputText = getInputText();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{} on sampler {} samples text {}", getThreadName(), getName(), inputText);
    }
    List<Keystroke> keystrokes = sampleTextKeystrokes(inputText);

    for (Keystroke keystroke : keystrokes) {
      sendKeystroke(client, keystroke.keyCode, keystroke.keyUp, keystroke.withDelay);
    }

  }

  public static List<Keystroke> sampleTextKeystrokes(String inputText) {

    List<Keystroke> keystrokes = new ArrayList<>();

    // JMeter property lost CR (\r), replace LF to CR for Citrix playback.
    inputText = inputText.replace('\n', '\r');

    final char[] chars = inputText.toCharArray();
    for (char aChar : chars) {
      int vkExtendedCode = Win32Utils.getVirtualKey(aChar);
      vkExtendedCode = vkExtendedCode == -1 ? Win32Utils.getVirtualKey('?') : vkExtendedCode;

      char vkCode = (char) (vkExtendedCode & 0xFF);
      int mask = (vkExtendedCode >>> 8) & 0xFF;

      List<Integer> pressKeys = new ArrayList<>();
      if ((mask & 4) == 4) {
        pressKeys.add(KeyEvent.VK_ALT);
      }
      if ((mask & 2) == 2) {
        pressKeys.add(KeyEvent.VK_CONTROL);
      }
      if ((mask & 1) == 1) {
        pressKeys.add(KeyEvent.VK_SHIFT);
      }

      LOGGER.debug("Char:{} OVK:{} OM:{} VK:{} KP:{} ", aChar, vkExtendedCode, mask,
          (int) vkCode, pressKeys);

      for (int keyDown : pressKeys) {
        keystrokes.add(new Keystroke(keyDown, false, false));
      }

      keystrokes.add(new Keystroke(vkCode, false, true));
      keystrokes.add(new Keystroke(vkCode, true, false));

      Collections.reverse(pressKeys); // Reverse the order to release the key pressed
      for (int keyUp : pressKeys) {
        keystrokes.add(new Keystroke(keyUp, true, false));
      }
    }
    // Last keystroke always force a delay
    Keystroke lastKeystroke = keystrokes.get(keystrokes.size() - 1);
    keystrokes.set(
        keystrokes.size() - 1,
        new Keystroke(lastKeystroke.keyCode, lastKeystroke.keyUp, true)
    );
    return keystrokes;
  }

  private void sampleKeySequence(CitrixClient client)
      throws CitrixClientException, InterruptedException {
    final List<KeySequenceItem> keySequence = getKeySequence();
    if (keySequence != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("{} on sampler {} samples key sequence with {} items", getThreadName(),
            getName(),
            keySequence.size());
      }
      for (KeySequenceItem item : keySequence) {
        final int keyCode = item.getKeyCode();
        final KeyState keyState = item.getKeyState();
        TimeUnit.MILLISECONDS.sleep(item.getDelay());
        LOGGER.trace("{} sends key: keyCode={}, keyState={}", getThreadName(), keyCode, keyState);
        client.sendKeyQuery(keyCode, keyState == KeyState.KEY_UP);
      }
    }
  }

  private void sampleMouseSequence(CitrixClient client)
      throws CitrixClientException, InterruptedException {
    final List<MouseSequenceItem> mouseSequence = getMouseSequence();
    if (mouseSequence != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("{} on sampler {} samples mouse sequence with {} items", getThreadName(),
            getName(),
            mouseSequence.size());
      }
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
          LOGGER.trace(
              "{} sends mouse buttons change {}: X={}, Y={}, relative={}, buttons={}, modifiers={}",
              getThreadName(), item.getAction(), x, y, relative, buttons, modifiers);
          client.sendMouseButtonQuery(item.getAction() == MouseAction.BUTTON_UP, buttons, x, y,
              modifiers,
              relative);
        }
      }
    }
  }

  private static class InteractionHandler extends SamplingHandler {

    private final StringBuilder builder = new StringBuilder();
    private int count = 0;

    protected InteractionHandler(String elementName) {
      super(elementName);
    }

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
      builder.append(
          prefix(state + " Code=0x" + Integer.toHexString(keyCode) + ", Symbol=" + (char) keyCode));
    }

    @Override
    public void handleMouseButtonQuery(CitrixClient source, boolean buttonUp,
                                       Set<MouseButton> buttons, Point pos,
                                       Set<Modifier> modifiers, Point origPos) {
      final String state = (buttonUp ? MouseAction.BUTTON_UP : MouseAction.BUTTON_DOWN).name();
      String msg = origPos != null
          ? state + " Position=" + pos + "(" + origPos + "), Buttons=" + buttons + ", Modifiers=" +
          modifiers
          : state + " Position=" + pos + ", Buttons=" + buttons + ", Modifiers=" + modifiers;
      builder.append(prefix(msg));
    }

    @Override
    public void handleMouseMoveQuery(CitrixClient source, Set<MouseButton> buttons, Point pos,
                                     Set<Modifier> modifiers, Point origPos) {
      String msg = MouseAction.MOVE + (origPos != null
          ?
          " Position=" + pos + "(" + origPos + "), Buttons=" + buttons + ", Modifiers=" + modifiers
          : " Position=" + pos + ", Buttons=" + buttons + ", Modifiers=" + modifiers);
      builder.append(prefix(msg));
    }
  }

  public static class Keystroke {

    private int keyCode;
    private boolean keyUp;
    private boolean withDelay;

    Keystroke(int keyCode, boolean keyUp, boolean withDelay) {
      this.keyCode = keyCode;
      this.keyUp = keyUp;
      this.withDelay = withDelay;
    }

    public String toString() {
      return "KC:" + keyCode + " KU:" + keyUp + " D:" + withDelay;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Keystroke keystroke = (Keystroke) o;
      return keyCode == keystroke.keyCode &&
          keyUp == keystroke.keyUp &&
          withDelay == keystroke.withDelay;
    }

    @Override
    public int hashCode() {
      return Objects.hash(keyCode, keyUp, withDelay);
    }
  }

}
