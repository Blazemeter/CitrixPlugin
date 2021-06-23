package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.client.SessionErrorLogger;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.recorder.Capture.MouseCaptureOption;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maintains a {@link Capture} object and relays all
 * {@link InteractionEvent} to it while recording.
 */
public class RecordingHandler extends SessionErrorLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecordingHandler.class);

  private final CaptureManager captureManager;
  private final Consumer<SessionEvent> onSessionEvent;
  private final Set<MouseCaptureOption> mouseCaptureOptions =
      EnumSet.of(MouseCaptureOption.RELATIVE_TO_FOREGROUND);

  private Capture currentCapture;

  /**
   * Instantiates a new {@link RecordingHandler}.
   *
   * @param captureManager Manager
   * @param onSessionEvent callback used when session event happens
   */
  public RecordingHandler(CaptureManager captureManager, Consumer<SessionEvent> onSessionEvent) {
    if (captureManager == null) {
      throw new IllegalArgumentException("captureManager must not be null");
    }
    if (onSessionEvent == null) {
      throw new IllegalArgumentException("onSessionEvent must not be null.");
    }
    this.captureManager = captureManager;
    this.onSessionEvent = onSessionEvent;
  }

  public boolean isCapturing() {
    return currentCapture != null;
  }

  public boolean hasCapturedItems() {
    return currentCapture != null && currentCapture.hasItems();
  }

  public Set<MouseCaptureOption> getMouseCaptureOptions() {
    return mouseCaptureOptions;
  }

  @Override
  public void handleInteractionEvent(InteractionEvent event) {
    if (currentCapture != null) {
      try {
        CaptureItem item = currentCapture.handleInteractionEvent(event);
        if (item != null) {
          captureManager.onCaptureItemAdded(item);
          LOGGER.debug("Has captured event {} with label {}", event.getInteractionType(),
              item.getLabel());
        }
      } catch (CaptureLimitException e) {
        LOGGER.debug("Detects maximum capture size {} is reached", e.getSize());
        captureManager.onCaptureSizeExceeded();
      } catch (Exception ex) {
        LOGGER.error("Exception handling interaction event", ex);
      }
    } else {
      if (isCapturing()) {
        LOGGER.debug("No capture handler for interaction event");
      }
    }
  }

  @Override
  public void handleSessionEvent(SessionEvent sessionEvent) {
    super.handleSessionEvent(sessionEvent);
    onSessionEvent.accept(sessionEvent);
  }

  /**
   * <p>
   * Starts a new capture of the specified {@link InteractionType}
   * </p>
   *
   * <p>
   * A new capture is started only if none is running. Call
   * {@link RecordingHandler#cancelCapture()} or
   * {@link RecordingHandler#stopCapture()} if required
   * </p>
   *
   * @param interactionType the type of interaction to capture
   * @return true, if a new capture is started; false if a previous capture is
   * already running
   */
  public boolean startCapture(InteractionType interactionType) {
    if (interactionType == null) {
      throw new IllegalArgumentException("interactionType must not be null.");
    }

    boolean started = false;
    if (currentCapture == null) {
      switch (interactionType) {
        case KEY:
          currentCapture = Capture.buildKeyCapture();
          break;
        case MOUSE:
          currentCapture = Capture.buildMouseCapture(mouseCaptureOptions);
          break;
        default:
          break;
      }
      started = true;
      LOGGER.debug("Starts {} capture", interactionType);
    } else {
      LOGGER
          .debug("Does not start a new {} capture because a {} is already running", interactionType,
              currentCapture.getInteractionType());
    }

    return started;
  }

  /**
   * Stops the current capture if it exists.
   *
   * @return null if no capture was running; the capture otherwise
   */
  public Capture stopCapture() {
    Capture result = null;
    if (currentCapture != null) {
      LOGGER.debug("Stops {} capture", currentCapture.getInteractionType());
      result = currentCapture;
      currentCapture = null;
    } else {
      LOGGER.debug("No capture to stop");
    }
    return result;
  }

  /**
   * Cancels the current capture is it exists.
   *
   * @return true if a capture was running; false otherwise
   */
  public boolean cancelCapture() {
    boolean canceled = false;
    if (currentCapture != null) {
      LOGGER.debug("Cancels {} capture", currentCapture.getInteractionType());
      currentCapture = null;
      canceled = true;
    } else {
      LOGGER.debug("No capture to cancel");
    }
    return canceled;
  }

  public interface CaptureManager {
    void onCaptureItemAdded(CaptureItem item);

    void onCaptureSizeExceeded();
  }
}
