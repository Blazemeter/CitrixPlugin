package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.WindowInfo;
import com.blazemeter.jmeter.citrix.client.events.WindowEvent;
import com.blazemeter.jmeter.citrix.client.handler.CitrixClientAdapter;

/**
 * Provides a clause chek strategy based on listening to Citrix client events
 */
public class ListeningStrategy implements CheckStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(ListeningStrategy.class);

	private final Predicate<WindowEvent> windowEventPredicate;

	@Override
	public boolean isSupportingSnapshot() {
		return false;
	}

	@Override
	public boolean isUsingExpectedValue() {
		return true;
	}

	@Override
	public boolean isSupportingImageAssessment() {
		return false;
	}

	/**
	 * Instantiates a new {@link ListeningStrategy}.
	 * 
	 * @param windowEventPredicate the predicate used to identify the expected
	 *                             window event
	 * @throws IllegalArgumentException when windowEventPredicate is null
	 */
	public ListeningStrategy(Predicate<WindowEvent> windowEventPredicate) {
		if (windowEventPredicate == null) {
			throw new IllegalArgumentException("windowEventPredicate must not be null.");
		}
		this.windowEventPredicate = windowEventPredicate;
	}

	@Override
	public boolean wait(Clause clause, CitrixClient client, CheckResultCallback onCheck) throws InterruptedException {
		if (clause == null) {
			throw new IllegalArgumentException("clause must not be null.");
		}

		if (client == null) {
			throw new IllegalArgumentException("client must not be null.");
		}

		final long timeout = clause.getTimeout();
		boolean expired = false;

		// Add a dedicated window event listener
		WindowEventListener listener = new WindowEventListener(clause, onCheck);
		client.addHandler(listener);
		LOGGER.debug("Starts listening Citrix windows events at {} with timeout={}", System.currentTimeMillis(),
				timeout);

		// Wait for the expected event occurs
		try {
		    if (!listener.happened.get()) {
		        expired = !listener.countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
		    }
		} finally {
			client.removeHandler(listener);
			LOGGER.debug("Stops listening Citrix windows events at {} with expired={}", System.currentTimeMillis(),
					expired);
		}

		if (listener.happened.get()) {
			// Workaround : Wait for the session rendering to reflect the expected event  
			// See Citrix API simulation (sim_api_specification_programmers_guide.pdf)
			// Screenshot OnUpdate
			LOGGER.debug("Waits for Citrix rendering synchronization after receiving the expected event");
			Thread.sleep(1000L);
		}
		
		return listener.happened.get();
	}

	@Override
	public CheckResult checkSnapshot(Clause clause, Snapshot snapshot) throws ClauseComputationException {
		throw new UnsupportedOperationException("Cannot check clause from snapshot");
	}

	@Override
	public String assess(BufferedImage image, Rectangle selection) throws ClauseComputationException {
		throw new UnsupportedOperationException("Cannot do image assessment.");
	}

	private class WindowEventListener extends CitrixClientAdapter {
	    private final CountDownLatch countDownLatch = new CountDownLatch(1);
		private final CheckResultCallback onCheck;
		private final Predicate<WindowEvent> predicate;

		private AtomicBoolean happened = new AtomicBoolean(false);
		private int index = 1;
		private CheckResult previous = null;

		public WindowEventListener(Clause clause, CheckResultCallback onCheck) {
			this.onCheck = onCheck;
			final Predicate<String> clauseValuePredicate = ClauseHelper.buildValuePredicate(clause);
			this.predicate = windowEventPredicate.and(e -> {
				final WindowInfo info = e.getWindowInfo();
				return info != null && clauseValuePredicate.test(info.getCaption());
			});
		}

		@Override
		public void handleWindowEvent(WindowEvent windowEvent) {
			// Check if the event is the expected one using the property predicate and
			// ensuring the window caption matches the expected value of the clause
			boolean success = predicate.test(windowEvent);

			// POSSIBLE_IMPROVEMENT Adds snapshot to result
			CheckResult result = new CheckResult(null, windowEvent, success);

			if (LOGGER.isTraceEnabled()) {
				final WindowInfo info = windowEvent.getWindowInfo();
				LOGGER.trace("Event {} occurred on window '{}' is expected: {}", windowEvent.getWindowState(),
						(info != null ? info.getCaption() : null), success);
			}

			// Callback on each check
			onCheck.apply(result, previous, index++);
			previous = result;

			if (success) {
				// Signal the event occurred
			    happened.compareAndSet(false, true);
			    countDownLatch.countDown();
			}
		}
	};
}
