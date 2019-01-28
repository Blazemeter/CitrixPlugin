package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blazemeter.jmeter.citrix.clause.CheckResult;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;

/**
 * Represents a strategy used to check a clause
 *
 */
public interface CheckStrategy {

	/**
	 * Indicates whether the current strategy ensures clause value is equal to
	 * expected value
	 * 
	 * @return true if the current check ensures clause value is equal to expected
	 *         value; false otherwise
	 */
	boolean isUsingExpectedValue();

	/**
	 * Indicates whether the current strategy can compute clause value from a single
	 * image.
	 * 
     * If true, implementers must define {@link CheckStrategy#assess(BufferedImage, Rectangle)}
	 * 
	 * @return true if the current check can compute clause value from a single
	 *         image; false otherwise
	 */
	boolean isSupportingImageAssessment();

	/**
	 * Indicates whether the current strategy can compute clause value from a
	 * snapshot
	 * 
	 * If true, implementers must define {@link CheckStrategy#checkSnapshot(Clause, Snapshot)}
	 * 
	 * @return true if the current strategy can compute clause value from a
	 *         snapshot; false otherwise
	 */
	boolean isSupportingSnapshot();

	/**
	 * Waits a clause is honored using the specified Citrix client.
	 * 
	 * @param clause  the clause to be expected
	 * @param client  the Citrix client used to compute clause result
	 * @param onCheck the callback method used when a check occurs during waiting
	 * @return true, if the clause is honored before the timeout specified by clause
	 * @throws InterruptedException when waiting is interrupted
	 */
	boolean wait(Clause clause, CitrixClient client, CheckResultCallback onCheck) throws InterruptedException;

	/**
	 * Checks a clause is honored using the specified snapshot.
	 * 
	 * This method does not wait the clause is honored. Instead it computes directly
	 * a clause result value from the snapshot and provides this result.
	 * 
	 * @param clause   the clause to test
	 * @param snapshot the snapshot to test
	 * @return the check result
	 * @throws ClauseComputationException when clause value computation fails
	 */
	CheckResult checkSnapshot(Clause clause, Snapshot snapshot) throws ClauseComputationException;

	/**
	 * Gets assessment from the specified image.
	 * 
	 * @param image     the image to assess
	 * @param selection the sub area of the image used to get assessment; whole
	 *                  image if null
	 * @return the assessment
	 * @throws ClauseComputationException when assessment computation fails
	 */
	String assess(BufferedImage image, Rectangle selection) throws ClauseComputationException;

	/**
	 * Represents a callback method used when a check occurs during waiting of
	 * {@link CheckStrategy#wait(Clause, CitrixClient, CheckResultCallback)}
	 */
	public static interface CheckResultCallback {
		/**
		 * Do the callback
		 * 
		 * @param result   the current check result
		 * @param previous the previous check result
		 * @param index    the index of the check
		 */
		void apply(CheckResult result, CheckResult previous, int index);
	}
}
