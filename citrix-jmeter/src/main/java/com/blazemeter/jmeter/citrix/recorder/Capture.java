package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.MouseAction;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capture {

  public static final Logger LOGGER = LoggerFactory.getLogger(Capture.class);

  private static final int CAPTURE_MAX_SIZE = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "capture_max_size", 500);

  private final InteractionType interactionType;
  private final List<CaptureItem> items = new ArrayList<>();
  private final Set<MouseCaptureOption> mouseCaptureOptions =
      EnumSet.noneOf(MouseCaptureOption.class);

  private Capture(InteractionType interactionType, Set<MouseCaptureOption> options) {
    if (interactionType == null) {
      throw new IllegalArgumentException("interactionType must not be null.");
    }
    this.interactionType = interactionType;
    if (options != null) {
      this.mouseCaptureOptions.addAll(options);
    }
  }

  public static Capture buildKeyCapture() {
    return new Capture(InteractionType.KEY, null);
  }

  public static Capture buildMouseCapture(Set<MouseCaptureOption> options) {
    return new Capture(InteractionType.MOUSE, options);
  }

  public final InteractionType getInteractionType() {
    return interactionType;
  }

  public final Iterable<CaptureItem> getItems() {
    return items;
  }

  public final boolean hasItems() {
    return !items.isEmpty();
  }

  public final Iterable<MouseCaptureOption> getMouseCaptureOptions() {
    return mouseCaptureOptions;
  }

  public final boolean isRelative() {
    return mouseCaptureOptions.contains(MouseCaptureOption.RELATIVE_TO_FOREGROUND);
  }

  private boolean isMatchingFilter(InteractionEvent event) {
    InteractionType eventType = event.getInteractionType();
    boolean isMatching = interactionType == eventType
        && (eventType != InteractionType.MOUSE || event.getMouseAction() != MouseAction.MOVE
        || mouseCaptureOptions.contains(MouseCaptureOption.INCLUDE_MOVES));
    if (LOGGER.isTraceEnabled()) {
      if (!isMatching) {
        LOGGER.trace(
            "Discarding interaction with " +
                "type={} and mouseAction={} while capture type={} and mouseCaptureOptions={}",
            eventType, event.getMouseAction(), interactionType, mouseCaptureOptions);
      } else {
        LOGGER.trace(
            "Keeping interaction with " +
                "type={} and mouseAction={} while capture type={} and mouseCaptureOptions={}",
            eventType, event.getMouseAction(), interactionType, mouseCaptureOptions);
      }
    }
    return isMatching;
  }

  private void addItem(CaptureItem item) throws CaptureLimitException {
    int count = items.size();
    if (count > CAPTURE_MAX_SIZE) {
      throw new CaptureLimitException(count,
          MessageFormat.format("Capture size exceeded: {0} interactions already captured.", count));
    }
    items.add(item);
  }

  public CaptureItem handleInteractionEvent(InteractionEvent event) throws CaptureLimitException {
    CaptureItem item = null;
    if (event != null && isMatchingFilter(event)) {
      if (interactionType == InteractionType.KEY) {
        item = new CaptureItem(event);
        addItem(item);
      } else { // InteractionType.MOUSE
        if (isRelative()) {
          Rectangle foreground = event.getForegroundWindowArea();
          int windowID = event.getWindowID();
          if (foreground == null) {
            String msg =
                "No active window detected while in relative mode. Using absolute coordinates.";
            LOGGER.debug(msg);

            // When relative is not possible, send it in absolute and fix it in post-process
            item = new CaptureItem(event);
            addItem(item);

          } else {

            final int x = event.getX();
            final int y = event.getY();
            if (foreground.contains(x, y)) {
              final int relX = x - foreground.x;
              final int relY = y - foreground.y;
              LOGGER.debug(
                  "Adapting interaction coordinates" +
                      " (X={}, Y={}) to foreground {} results in new coordinates (X={}, Y={})",
                  x, y, foreground, relX, relY);
              InteractionEvent mouseEvent =
                  new InteractionEvent(event.getSource(), windowID, foreground,
                      event.getMouseAction(),
                      relX, relY, event.getButtons(), event.getModifiers());
              item = new CaptureItem(mouseEvent);
              addItem(item);
            } else {
              LOGGER.debug(
                  "Mouse interaction not contained in foreground, " +
                      "migrating to absolute {} with X={}, Y={}",
                  foreground, x, y);

              InteractionEvent mouseEvent =
                  new InteractionEvent(event.getSource(), 0, null,
                      event.getMouseAction(),
                      event.getX(), event.getY(), event.getButtons(), event.getModifiers());
              item = new CaptureItem(mouseEvent);
              addItem(item);
            }
          }

        } else {
          item = new CaptureItem(event);
          addItem(item);
        }
      }
    }
    return item;
  }

  public enum MouseCaptureOption {
    INCLUDE_MOVES, RELATIVE_TO_FOREGROUND
  }
}
