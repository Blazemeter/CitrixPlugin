package com.blazemeter.jmeter.citrix.sampler;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.MouseButton;
import com.blazemeter.jmeter.citrix.recorder.Capture;
import com.blazemeter.jmeter.citrix.recorder.CaptureItem;
import com.blazemeter.jmeter.citrix.sampler.gui.InteractionSamplerGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps to build samplers.
 */
public class SamplerHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(SamplerHelper.class);

  private static final Set<MouseButton> DOUBLE_CLICK_BUTTONS = EnumSet.of(MouseButton.LEFT);

  private static final String MAX_KEPT_CHECKS_PROP =
      CitrixUtils.PROPERTIES_PFX + "clause_check_max_results";
  public static final long MAX_KEPT_CHECKS = JMeterUtils.getPropDefault(MAX_KEPT_CHECKS_PROP, 20);

  // JMeter property that allows to deactivate interpretation of key interactions
  // sequence as text
  private static final boolean DEACTIVATE_INTERPRETATION = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "deactivate_interpretation", false);

  private SamplerHelper() {
  }

  // Create and initialize a new interaction sampler using the specified capture
  // item informations
  private static InteractionSampler buildSamplerFromCaptureItem(
      CaptureItem item,
      boolean relative,
      Consumer<InteractionSampler> onSamplerCreated
  ) {
    InteractionSampler sampler = new InteractionSampler();
    sampler.setProperty(TestElement.GUI_CLASS, InteractionSamplerGUI.class.getName());
    sampler.setName(item.getLabel());
    sampler.setRelative(relative);
    sampler.setTimestamp(item.getEvent().getTimestamp());
    // the consumer allows to override/complete properties of the new sampler
    onSamplerCreated.accept(sampler);
    return sampler;
  }

  private static boolean isInterpretable(final InteractionEvent e) {
    final int keyCode = e.getKeyCode();
    final boolean result = !DEACTIVATE_INTERPRETATION && (e.getTargetKeyCode() > 0);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("{} {} (0x{}, {}) is interpretable : {}", e.getKeyState(), keyCode,
          Integer.toHexString(keyCode), (char) keyCode, result);
    }
    return result;
  }

  // Groups key capture items depending on they are interpretable as text
  // DO NOT ALLOW EMPTY GROUP
  private static List<KeyCaptureGroup> groupKeyCaptures(Iterable<CaptureItem> items) {
    final List<KeyCaptureGroup> groups = new ArrayList<>();
    KeySamplerType lastType = null;
    KeyCaptureGroup group = null;
    for (CaptureItem capture : items) {
      LOGGER.debug("Item KC:{} KS:{} TGC:{} MOD:{}", capture.getEvent().getKeyCode(),
          capture.getEvent().getKeyState(), capture.getEvent().getTargetKeyCode(),
          capture.getEvent().getModifiersText());

      if (capture.getEvent().getKeyCode() != KeyEvent.VK_SHIFT) {
        final KeySamplerType currentType = isInterpretable(capture.getEvent()) ? KeySamplerType.TEXT
            : KeySamplerType.SEQUENCE;
        if (currentType != lastType) {
          LOGGER.debug("Create {} group while capture item grouping.", currentType);
          group = new KeyCaptureGroup(currentType);
          groups.add(group);
          lastType = currentType;
        }
        group.items.add(capture);
      }
    }
    return groups;
  }

  /**
   * Builds keyboard samplers from specified capture.
   *
   * @param capture          the capture used to build samplers
   * @param onSamplerCreated callback function used to customize each created
   *                         sampler
   * @return keyboard samplers matching the capture
   */
  public static List<InteractionSampler> buildKeySamplers(
      Capture capture,
      Consumer<InteractionSampler> onSamplerCreated
  ) {
    final List<InteractionSampler> samplers = new ArrayList<>();
    if (capture != null) {
      final List<KeyCaptureGroup> groups = groupKeyCaptures(capture.getItems());

      groups.stream().map(group -> {
        // Get last capture item of the group
        final int count = group.items.size();
        try {
          final CaptureItem lastItem = group.items.get(count - 1);

          // Build a new sampler from the last capture
          return buildSamplerFromCaptureItem(lastItem, capture.isRelative(), sampler -> {

            LOGGER.debug(
                "Initializing sampler from last capture " +
                    "(keyState={}, keyCode={}) of group {} wich contains {} captures.",
                lastItem.getEvent().getKeyState(), lastItem.getEvent().getKeyCode(), group.type,
                group.items.size());

            // Initialize sampler depending on group type
            switch (group.type) {
              case TEXT:
                sampler.setSamplerType(SamplerType.TEXT);

                // Converts captures to text
                String inputText = getKeyCaptureGroupText(group);

                LOGGER.debug("Interprets captures as text: {}", inputText);
                sampler.setInputText(inputText);
                break;

              case SEQUENCE:
                sampler.setSamplerType(SamplerType.KEY_SEQUENCE);

                if (!group.items.isEmpty()) {
                  // Converts captures to key sequence
                  long previousTimestamp = group.items.get(0).getEvent().getTimestamp();
                  List<KeySequenceItem> sequence = new ArrayList<>();
                  for (CaptureItem captureItem : group.items) {
                    InteractionEvent event = captureItem.getEvent();
                    long currentTimestamp = event.getTimestamp();
                    KeySequenceItem item =
                        new KeySequenceItem(event.getKeyState(), event.getKeyCode(),
                            currentTimestamp - previousTimestamp);
                    sequence.add(item);
                    previousTimestamp = currentTimestamp;
                  }
                  sampler.getKeySequence().addAll(sequence);
                }
                break;
              default:
                break;
            }

            // Allow caller to override new sampler properties
            if (onSamplerCreated != null) {
              onSamplerCreated.accept(sampler);
            }
          });
        } catch (IndexOutOfBoundsException ex) {
          throw new UnsupportedOperationException("KeyCaptureGroup must not be empty", ex);
        }
      }).forEach(samplers::add);
    }
    return samplers;
  }

  protected static String getKeyCaptureGroupText(KeyCaptureGroup group) {
    StringBuilder builder = new StringBuilder();
    group.items.stream().map(CaptureItem::getEvent)
        .filter(e -> e.getKeyState() == KeyState.KEY_DOWN)
        .forEach(e -> builder.append(e.getTargetKeyCodeString()));
    String inputText = builder.toString();

    // Citrix use CR (\r), migrate to LF (\n) used by JMeter
    inputText = inputText.replace('\r', '\n');

    // Clean invalid unicode character for UTF and XML spec
    // 0xFFFE: BOM
    // 0xFFFF: Unknown Unicode character
    // https://www.w3.org/TR/2008/REC-xml-20081126/#charsets
    // http://www.unicode.org/faq/private_use.html#sentinel6
    inputText = inputText.replaceAll("[\\uFFFE-\\uFFFF]", "");
    return inputText;
  }

  private static boolean areClickComponents(CaptureItem down, CaptureItem up) {
    final InteractionEvent downEvent = down.getEvent();
    final InteractionEvent upEvent = up.getEvent();
    // POSSIBLE_IMPROVEMENT Add tolerance threshold
    return downEvent.getButtons().equals(upEvent.getButtons()) && downEvent.getX() == upEvent.getX()
        && downEvent.getY() == upEvent.getY();
  }

  private static boolean areDoubleClickComponents(MouseCaptureGroup lastClick, CaptureItem up) {
    final InteractionEvent lastClickEvent = lastClick.captures.get(0).getEvent();
    final InteractionEvent upEvent = up.getEvent();
    final Set<MouseButton> lastClickButtons = lastClickEvent.getButtons();
    // POSSIBLE_IMPROVEMENT Add tolerance threshold
    return DOUBLE_CLICK_BUTTONS.equals(lastClickButtons) &&
        Objects.equals(lastClickButtons, upEvent.getButtons())
        && lastClickEvent.getX() == upEvent.getX() && lastClickEvent.getY() == upEvent.getY();
  }

  // Groups mouse capture items depending on they are interpretable as click or
  // double
  // click
  // DO NOT ALLOW EMPTY GROUP
  private static List<MouseCaptureGroup> groupMouseCaptures(Iterable<CaptureItem> items) {
    final List<MouseCaptureGroup> groups = new ArrayList<>();
    MouseCaptureGroup currentGroup = new MouseCaptureGroup(MouseSamplerType.SEQUENCE);
    CaptureItem lastBtnDown = null;
    MouseCaptureGroup lastMouseClick = null;
    for (CaptureItem capture : items) {
      final InteractionEvent event = capture.getEvent();
      switch (event.getMouseAction()) {
        case BUTTON_DOWN:
          lastBtnDown = capture;
          currentGroup.captures.add(capture);
          break;
        case BUTTON_UP:
          if (lastBtnDown != null && areClickComponents(lastBtnDown, capture)) {
            currentGroup.captures.remove(lastBtnDown);
            if (!currentGroup.captures.isEmpty()) {
              groups.add(currentGroup);
            }

            if (lastMouseClick != null && areDoubleClickComponents(lastMouseClick, capture)) {
              LOGGER.debug("Double click detected");
              lastMouseClick.doubleClick = true;
              lastMouseClick.captures.add(lastBtnDown);
              lastMouseClick.captures.add(capture);
              lastMouseClick = null;
            } else {
              LOGGER.debug("Click detected");
              lastMouseClick = new MouseCaptureGroup(MouseSamplerType.CLICK);
              lastMouseClick.captures.add(lastBtnDown);
              lastMouseClick.captures.add(capture);
              groups.add(lastMouseClick);
            }

            currentGroup = new MouseCaptureGroup(MouseSamplerType.SEQUENCE);
            lastBtnDown = null;
          } else {
            currentGroup.captures.add(capture);
          }
          break;
        case MOVE:
          currentGroup.captures.add(capture);
          break;
        default:
          break;
      }
    }
    if (!currentGroup.captures.isEmpty()) {
      groups.add(currentGroup);
    }
    return groups;
  }

  /**
   * Builds mouse samplers from specified capture.
   *
   * @param capture          the capture used to build samplers
   * @param onSamplerCreated callback function used to customize each created
   *                         sampler
   * @return mouse samplers matching the capture
   */
  public static List<InteractionSampler> buildMouseSamplers(
      Capture capture,
      Consumer<InteractionSampler> onSamplerCreated
  ) {
    final List<InteractionSampler> samplers = new ArrayList<>();
    if (capture != null) {
      final List<MouseCaptureGroup> groups = groupMouseCaptures(capture.getItems());

      groups.stream().map(group -> {
        // Get last capture of the group
        final int count = group.captures.size();
        try {
          final CaptureItem lastItem = group.captures.get(count - 1);
          // Build a new sampler from the last capture
          return buildSamplerFromCaptureItem(lastItem, capture.isRelative(), sampler -> {

            LOGGER.debug(
                "Initializing sampler from last capture" +
                    " (mouseAction={}) of group {} wich contains {} captures.",
                lastItem.getEvent().getMouseAction(), group.type, group.captures.size());

            // Initialize sampler depending on group type
            switch (group.type) {
              case CLICK:
                InteractionEvent event = group.captures.get(0).getEvent();
                sampler.setSamplerType(SamplerType.MOUSE_CLICK);
                sampler.setDoubleClick(group.doubleClick);
                sampler.setMouseX(Integer.toString(event.getX()));
                sampler.setMouseY(Integer.toString(event.getY()));
                sampler.getMouseButtons().addAll(event.getButtons());
                sampler.getModifiers().addAll(event.getModifiers());
                break;
              case SEQUENCE:
                sampler.setSamplerType(SamplerType.MOUSE_SEQUENCE);

                if (!group.captures.isEmpty()) {
                  // Converts captures to mouse sequence
                  long previousTimestamp = group.captures.get(0).getEvent().getTimestamp();
                  List<MouseSequenceItem> sequence = new ArrayList<>();
                  for (CaptureItem captureItem : group.captures) {
                    InteractionEvent e = captureItem.getEvent();
                    long currentTimestamp = e.getTimestamp();
                    MouseSequenceItem item = new MouseSequenceItem(e.getMouseAction(), e.getX(),
                        e.getY(), e.getButtons(), e.getModifiers(),
                        currentTimestamp - previousTimestamp);
                    sequence.add(item);
                    previousTimestamp = currentTimestamp;
                  }
                  sampler.getMouseSequence().addAll(sequence);
                }
                break;
              default:
                break;
            }

            // Allow caller to override new sampler properties
            if (onSamplerCreated != null) {
              onSamplerCreated.accept(sampler);
            }
          });
        } catch (IndexOutOfBoundsException ex) {
          throw new UnsupportedOperationException("MouseCaptureGroup must not be empty", ex);
        }
      }).forEach(samplers::add);
    }
    return samplers;
  }

  public static String formatResponseMessage(Queue<String> details, boolean overflow) {
    final String newLine = System.lineSeparator();
    final StringBuilder builder = new StringBuilder(
        CitrixUtils.getResString("sampler_helper_response_message_header", false)).append(newLine);
    if (details.isEmpty()) {
      builder.append(CitrixUtils.getResString("sampler_helper_response_message_no_check", false));
    } else {
      if (overflow) {
        builder.append(MessageFormat.format(
            CitrixUtils.getResString("sampler_helper_response_message_overflow", false),
            MAX_KEPT_CHECKS,
            MAX_KEPT_CHECKS_PROP)).append(newLine);
      }
      while (!details.isEmpty()) {
        builder.append(details.remove()).append(newLine);
      }
    }
    return builder.toString();
  }

  // Enumerates kinds of key samplers
  protected enum KeySamplerType {
    // Sampler contains a text to transform to interactions while sampling
    TEXT,
    // Sampler contains directly a list of interaction to sample
    SEQUENCE
  }

  // Enumerates kinds of mouse samplers
  private enum MouseSamplerType {
    CLICK, SEQUENCE
  }

  // Allows to group captures depending on KeySamplerType logic
  protected static class KeyCaptureGroup {
    protected final List<CaptureItem> items = new ArrayList<>();
    private final KeySamplerType type;

    KeyCaptureGroup(KeySamplerType type) {
      this.type = type;
    }
  }

  // Allows to group captures depending on KeySamplerType logic
  private static class MouseCaptureGroup {
    private final MouseSamplerType type;
    private final List<CaptureItem> captures = new ArrayList<>();

    private boolean doubleClick = false;

    MouseCaptureGroup(MouseSamplerType type) {
      this.type = type;
    }
  }
}
