package com.blazemeter.jmeter.citrix.clause;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.ocr.OcrManagerHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.Hash;

/**
 * Provides helper class to facilitate clause computation
 */
public class ClauseHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClauseHelper.class);

	public static final String IMAGE_FORMAT = "png";

	private static final String CLAUSE_TIMEOUT_PROP = CitrixUtils.PROPERTIES_PFX + "clause_check_timeout";
	private static final long CLAUSE_TIMEOUT_DEFAULT = 3000L;
	public static final long CLAUSE_TIMEOUT = JMeterUtils.getPropDefault(CLAUSE_TIMEOUT_PROP, CLAUSE_TIMEOUT_DEFAULT);

	private static final String CLAUSE_INTERVAL_PROP = CitrixUtils.PROPERTIES_PFX + "clause_check_interval";
	private static final long CLAUSE_INTERVAL_DEFAULT = 100L;
	public static final long CLAUSE_INTERVAL = JMeterUtils.getPropDefault(CLAUSE_INTERVAL_PROP,
			CLAUSE_INTERVAL_DEFAULT);

	private static final int BIT_RESOLUTION = 20;

	private ClauseHelper() {
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

	/**
	 * Gets a predicate to use on clause check result to decide if the clause is
	 * honored.
	 * 
	 * If clause uses regular expression on expected value, the predicate will
	 * return true if its argument matches {@link Clause#getExpectedValue()};
	 * othserwise the predicate will return true if its argument is equals to
	 * {@link Clause#getExpectedValue()}.
	 * 
	 * @param clause the clause to use
	 * @return a predicate to use on clause check result to decide if the clause is
	 *         honored.
	 */
	public static Predicate<String> getValuePredicate(Clause clause) {
		Predicate<String> predicate = null;
		if (clause.isUsingRegex()) {
			// Build regex matching predicate
			PatternMatcher matcher = JMeterUtils.getMatcher();
			Pattern pattern = JMeterUtils.getPatternCache().getPattern(clause.getExpectedValue(),
					Perl5Compiler.READ_ONLY_MASK);
			predicate = v -> matcher.matches(v, pattern);
		} else {
			// Build equality predicate
			predicate = v -> Objects.equals(v, clause.getExpectedValue());
		}
		return predicate;
	}

}
