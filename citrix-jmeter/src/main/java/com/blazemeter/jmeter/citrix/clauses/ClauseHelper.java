package com.blazemeter.jmeter.citrix.clauses;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.clauses.Clause.CheckResult;
import com.blazemeter.jmeter.citrix.clauses.Clause.CheckType;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.ocr.OcrManagerHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.Hash;

public class ClauseHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClauseHelper.class);

	// public static final String HASH_ALGORITHM = "MD5";
	public static final String IMAGE_FORMAT = "png";

	private static final String CLAUSE_TIMEOUT_PROP = CitrixUtils.PROPERTIES_PFX + "clause_check_timeout";
	private static final long CLAUSE_TIMEOUT_DEFAULT = 3000L;
	public static final long CLAUSE_TIMEOUT = JMeterUtils.getPropDefault(CLAUSE_TIMEOUT_PROP, CLAUSE_TIMEOUT_DEFAULT);

	private static final String CLAUSE_INTERVAL_PROP = CitrixUtils.PROPERTIES_PFX + "clause_check_interval";
	private static final long CLAUSE_INTERVAL_DEFAULT = 100L;
	private static final long CLAUSE_INTERVAL = JMeterUtils.getPropDefault(CLAUSE_INTERVAL_PROP,
			CLAUSE_INTERVAL_DEFAULT);

	private static final int BIT_RESOLUTION = 20;

	private ClauseHelper() {
	}

	public static String computeValue(CheckType checkType, BufferedImage screenshot, Rectangle selection)
			throws ClauseComputationException {
		if (checkType == null) {
			throw new IllegalArgumentException("checkType cannot be null.");
		}

		if (!Clause.SNAPSHOT_CHECKTYPES.contains(checkType)) {
			throw new IllegalArgumentException(MessageFormat.format("Cannot compute value for check {0}", checkType));
		}

		return checkType == CheckType.HASH ? ClauseHelper.hash(screenshot, selection)
				: ClauseHelper.recognize(screenshot, selection);
	}

	public static BufferedImage convertByteArrayToImage(byte[] array) throws IOException {
		try (ByteArrayInputStream input = new ByteArrayInputStream(array)) {
			return ImageIO.read(input);
		}
	}

	public static byte[] convertImageToByteArray(BufferedImage image) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageIO.write(image, "png", output);
			output.flush();
			return output.toByteArray();
		}
	}

	/**
	 * Hashes an image.
	 * 
	 * @param image the image which will be hashed
	 * @param area  portion of the image that will be hashed, null for the whole
	 *              image
	 * @return the hash value
	 * @throws ClauseComputationException when hashing fails
	 * @throws IllegalArgumentException   when image is null
	 */
	public static String hash(final BufferedImage image, final Rectangle area) throws ClauseComputationException {
		if (image == null) {
			throw new IllegalArgumentException("image must not be null.");
		}

		BufferedImage target = image;
		if (area != null) {
			Rectangle bounds = new Rectangle(image.getWidth(), image.getHeight());
			if (!bounds.contains(area)) {
				String msg = MessageFormat.format("Unable to hash area {0}: Exceeding image area {1}", area, bounds);
				throw new ClauseComputationException(msg);
			}
			target = image.getSubimage(area.x, area.y, area.width, area.height);
		}

		try {
//          MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
//			byte[] digest = md.digest(convertImageToByteArray(target));
//			return new String(Hex.encodeHex(digest)).toUpperCase();

			PerceptiveHash perceptiveHash = new PerceptiveHash(BIT_RESOLUTION);
			Hash hash = perceptiveHash.hash(target);
			return hash.getHashValue().toString();
		} catch (Exception e) {
			String msg = MessageFormat.format("Unable to hash area {0}: {1}", area, e.getMessage());
			throw new ClauseComputationException(msg, e);
		}
	}

	/**
	 * Recognizes text in image.
	 * 
	 * @param image the image which will be analyzed
	 * @param area  portion of the image that will be analyzed, null for the whole
	 *              image
	 * @return OCR result
	 * @throws ClauseComputationException when text recognition fails
	 * @throws IllegalArgumentException   when image is null
	 */
	public static String recognize(final BufferedImage image, final Rectangle area) throws ClauseComputationException {
		if (image == null)
			throw new IllegalArgumentException("image must not be null.");

		try {
			return OcrManagerHolder.getManager().recognize(image, area);
		} catch (Exception e) {
			String msg = MessageFormat.format("Unable to recognize text in area {0}: {1}", area, e.getMessage());
			throw new ClauseComputationException(msg, e);
		}
	}

	/**
	 * Wait until the specified clause is honored or the timeout is reached
	 * 
	 * @param clause  the clause to check
	 * @param client  the Citrix client where the clause must be checked
	 * @param start   start time to take into account for the timeout
	 * @param onCheck callback function called each time a check occurs
	 * @return true if the clause is honored; false otherwise
	 * @throws InterruptedException     if current thread is interrupted
	 * @throws IllegalArgumentException if clause is null
	 */
	public static boolean waitForClause(Clause clause, CitrixClient client, long start,
			BiConsumer<Optional<CheckResult>, Integer> onCheck) throws InterruptedException {
		if (clause == null) {
			throw new IllegalArgumentException("clause must not be null.");
		}

		boolean success = false;
		final long maxTime = start + clause.getTimeout();
		int count = 1;
		while (!success && System.currentTimeMillis() <= maxTime) {
			
			// Check clause
			CheckResult result = null;
			try {
				result = clause.check(client);
				success = result.isSuccessful();
			} catch (CitrixClientException | ClauseComputationException e) {
				LOGGER.debug("Unable to compute clause value at test #{}: {}", count, e.getMessage(), e);
			}

			// Callback function call
			if (onCheck != null) {
				onCheck.accept(Optional.ofNullable(result), Integer.valueOf(count));
			}

			// Wait until the next check
			if (!success) {
				TimeUnit.MILLISECONDS.sleep(CLAUSE_INTERVAL);
			}
			count++;
		}
		return success;
	}

	/**
	 * Get selection area with absolute position according to specified parameters
	 * 
	 * @param selection    the selection area
	 * @param relative     indicates whether the selection is relative to the
	 *                     foreground area
	 * @param fgWindowArea the foreground window area
	 * @return if relative, the selection area with absolute position; otherwise the
	 *         initial selection area.
	 */
	public static Rectangle getAbsoluteSelection(Rectangle selection, boolean relative, Rectangle fgWindowArea) {
		Rectangle selectedArea = selection != null ? new Rectangle(selection) : null;
		if (relative) {
			if (fgWindowArea == null) {
				throw new IllegalStateException(
						"Foreground window area must be defined while obtaining the absolute position of a relative selection.");
			}

			if (selectedArea != null) {
				// Take foreground area into account
				selectedArea.translate(fgWindowArea.x, fgWindowArea.y);
			} else {
				// Absolute area is foreground window area
				selectedArea = new Rectangle(fgWindowArea);
			}

			LOGGER.debug("Get absolute selection area {} from initial selection {} and foreground area {} ",
					selectedArea, selection, fgWindowArea);
		}
		return selectedArea;
	}

}
