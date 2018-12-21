package com.blazemeter.jmeter.citrix.recorder;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

import com.blazemeter.jmeter.citrix.client.SessionErrorLogger;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.recorder.Capture.MouseCaptureOption;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;

/**
 * Class which save received Events into a List. This List is then used by the
 * CitrixRecorder to know with which events it must create its samplers.
 */
class RecordingHandler extends SessionErrorLogger {

	private final CaptureManager captureManager;
	private final Consumer<SessionEvent> onSessionEvent;
	private final Set<MouseCaptureOption> mouseCaptureOptions = EnumSet.of(MouseCaptureOption.RELATIVE_TO_FOREGROUND);

	private Capture currentCapture;

	public boolean isCapturing() {
		return currentCapture != null;
	}

	public boolean hasCapturedItems() {
		return currentCapture != null && currentCapture.hasItems();
	}

	public Set<MouseCaptureOption> getMouseCaptureOptions() {
		return mouseCaptureOptions;
	}

	/**
	 * Instantiates a new {@link RecordingHandler}.
	 * 
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

	@Override
	public void handleInteractionEvent(InteractionEvent event) {
		if (currentCapture != null) {
			try {
				CaptureItem item = currentCapture.handleInteractionEvent(event);
				if (item != null) {
					captureManager.onCaptureItemAdded(item);
				}
			} catch (CaptureLimitException e) {
				captureManager.onCaptureSizeExceeded();
			}
		}
	}

	@Override
	public void handleSessionEvent(SessionEvent sessionEvent) {
		onSessionEvent.accept(sessionEvent);
	}

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
			}
			started = true;

		}
		return started;
	}

	public Capture stopCapture() {
		Capture result = null;
		if (currentCapture != null) {
			result = currentCapture;
			currentCapture = null;
		}
		return result;
	}

	public boolean cancelCapture() {
		boolean canceled = false;
		if (currentCapture != null) {
			currentCapture = null;
			canceled = true;
		}
		return canceled;
	}

	public interface CaptureManager {
		void onCaptureItemAdded(CaptureItem item);

		void onCaptureSizeExceeded();
	}

}