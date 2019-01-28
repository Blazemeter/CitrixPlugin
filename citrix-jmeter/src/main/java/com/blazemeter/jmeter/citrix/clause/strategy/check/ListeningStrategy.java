package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

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
		if (client == null) {
			throw new IllegalArgumentException("client must not be null.");
		}

		boolean expired = false;

		// Add a dedicated window event listener
		WindowEventListener listener = new WindowEventListener(clause, onCheck);
		client.addHandler(listener);

		// Wait for the expected event occurs
		listener.locker.lock();
		try {
			while (!listener.happened && !expired) {
				expired = !listener.eventHappened.await(clause.getTimeout(), TimeUnit.MILLISECONDS);
			}
		} finally {
			listener.locker.unlock();
			client.removeHandler(listener);
		}

		return listener.happened;
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
		private final Lock locker = new ReentrantLock();
		private final Condition eventHappened = locker.newCondition();
		private final CheckResultCallback onCheck;
		private final Clause clause;

		private boolean happened = false;
		private int index = 1;
		private CheckResult previous = null;

		public WindowEventListener(Clause clause, CheckResultCallback onCheck) {
			this.clause = clause;
			this.onCheck = onCheck;
		}

		@Override
		public void handleWindowEvent(WindowEvent windowEvent) {
			// Check if the event is the expected one using the property predicate and
			// ensuring the window caption matches the expected value of the clause
			boolean success = windowEventPredicate.and(e -> {
				final WindowInfo info = e.getWindowInfo();
				return info != null && ClauseHelper.getValuePredicate(clause).test(info.getCaption());
			}).test(windowEvent);

			
			// POSSIBLE_IMPROVEMENT Adds snapshot to result
			CheckResult result = new CheckResult(null, windowEvent, success);
			
			// Callback on each check
			onCheck.apply(result, previous, index++);
			previous = result;
			
			if (success) {
				// Signal the event occurred
				locker.lock();
				try {
					happened = true;
					eventHappened.signal();
				} finally {
					locker.unlock();
				}
			}
		}
	};
}
