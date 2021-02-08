package com.blazemeter.jmeter.citrix.clause;

import com.blazemeter.jmeter.citrix.clause.strategy.check.PollingContext;
import com.blazemeter.jmeter.citrix.ocr.OcrManagerHolder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.github.kilianB.hashAlgorithms.AverageHash;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.Hash;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides helper class to facilitate clause computation.
 */
public class ClauseHelper {

  public static final String IMAGE_FORMAT = "png";
  public static final int LEGACY_BIT_RESOLUTION = 20;
  private static final Logger LOGGER = LoggerFactory.getLogger(ClauseHelper.class);
  private static final String CLAUSE_TIMEOUT_PROP =
      CitrixUtils.PROPERTIES_PFX + "clause_check_timeout";
  public static final long CLAUSE_TIMEOUT = JMeterUtils.getPropDefault(CLAUSE_TIMEOUT_PROP, 3000L);
  private static final String CLAUSE_INTERVAL_PROP =
      CitrixUtils.PROPERTIES_PFX + "clause_check_interval";
  public static final long CLAUSE_INTERVAL =
      JMeterUtils.getPropDefault(CLAUSE_INTERVAL_PROP, 1000L);
  private static final String CLAUSE_BIT_RESOLUTION_PROP =
      CitrixUtils.PROPERTIES_PFX + "clause_hash_bit_resolution";
  public static final int BIT_RESOLUTION =
      JMeterUtils.getPropDefault(CLAUSE_BIT_RESOLUTION_PROP, 128);
  private static final String CLAUSE_BIT_DISTANCE_PROP =
      CitrixUtils.PROPERTIES_PFX + "clause_hash_bit_distance";
  public static final int BIT_DISTANCE = JMeterUtils.getPropDefault(CLAUSE_BIT_DISTANCE_PROP, 3);

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
   * @param image   the image which will be hashed
   * @param area    portion of the image that will be hashed, null for the whole
   *                image
   * @param context the execution context
   * @return the hash value
   * @throws ClauseComputationException when hashing fails
   * @throws IllegalArgumentException   when image is null
   */
  public static String hash(final BufferedImage image, final Rectangle area, PollingContext context)
      throws ClauseComputationException {
    if (image == null) {
      throw new IllegalArgumentException("image must not be null.");
    }

    BufferedImage target = image;
    if (area != null) {
      Rectangle bounds = new Rectangle(image.getWidth(), image.getHeight());
      Rectangle intersection = bounds.intersection(area);
      target = image
          .getSubimage(intersection.x, intersection.y, intersection.width, intersection.height);
    }

    boolean useLegacy = (context != null && isHashLegacy(context.getClause().getExpectedValue()));

    try {
      BigInteger hashValue = useLegacy ? (
          new PerceptiveHash(LEGACY_BIT_RESOLUTION)
      ).hash(target).getHashValue() : (
          new AverageHash(BIT_RESOLUTION)
      ).hash(target).getHashValue();

      LOGGER.debug("Hash {} area {} useLegacy={}", hashValue.toString(), area, useLegacy);
      return hashValue.toString();
    } catch (Exception e) {
      String msg = MessageFormat
          .format("Unable to hash area {0}: {1} useLegacy={2}", area, e.getMessage(), useLegacy);
      throw new ClauseComputationException(msg, e);
    }

  }

  /**
   * Recognizes text in image.
   *
   * @param image   the image which will be analyzed
   * @param area    portion of the image that will be analyzed, null for the whole
   *                image
   * @param context the execution context
   * @return OCR result
   * @throws ClauseComputationException when text recognition fails
   * @throws IllegalArgumentException   when image is null
   */
  public static String recognize(final BufferedImage image, final Rectangle area,
                                 PollingContext context) throws ClauseComputationException {
    if (image == null) {
      throw new IllegalArgumentException("image must not be null.");
    }

    try {
      return OcrManagerHolder.getManager().recognize(image, area);
    } catch (Exception e) {
      String msg =
          MessageFormat.format("Unable to recognize text in area {0}: {1}", area, e.getMessage());
      throw new ClauseComputationException(msg, e);
    }
  }

  /**
   * Get selection area with absolute position according to specified parameters.
   *
   * @param selection    the selection area
   * @param relative     indicates whether the selection is relative to the
   *                     foreground area
   * @param fgWindowArea the foreground window area
   * @return if relative, the selection area with absolute position; otherwise the
   * initial selection area.
   * @throws IllegalStateException when relative is true and fgWindowArea is null
   */
  public static Rectangle getAbsoluteSelection(Rectangle selection, boolean relative,
                                               Rectangle fgWindowArea) {
    Rectangle selectedArea = selection != null ? new Rectangle(selection) : null;
    if (relative) {
      if (fgWindowArea == null) {
        throw new IllegalStateException(
            "Foreground window area must be defined " +
                "while obtaining the absolute position of a relative selection.");
      }

      if (selectedArea != null) {
        // Take foreground area into account
        selectedArea.translate(fgWindowArea.x, fgWindowArea.y);
      } else {
        // Absolute area is foreground window area
        // Area reported by Window exceeds one pixel and
        // generates Hash errors, extra pixel is removed.
        selectedArea = new Rectangle(fgWindowArea.x + 1, fgWindowArea.y + 1, fgWindowArea.width - 2,
            fgWindowArea.height - 2);
      }

      LOGGER
          .debug("Get absolute selection area {} from initial selection {} and foreground area {} ",
              selectedArea, selection, fgWindowArea);
    }
    return selectedArea;
  }

  private static Predicate<String> getRegexPredicate(Clause clause) {
    Pattern pattern = JMeterUtils.getPatternCache().getPattern(
        clause.getExpectedValueParametrized(),
        Perl5Compiler.READ_ONLY_MASK);
    return v -> JMeterUtils.getMatcher().matches(v, pattern);
  }

  public static boolean isHashLegacy(String hash) {
    return hash.length() <= 6; // Evaluate Legacy Hash algorithm (20 bit = 6 chars)
  }

  private static Predicate<String> getHashHammingDistancePredicate(Clause clause) {
    String expectedValue = clause.getExpectedValueParametrized();
    int hashBitResolution = (isHashLegacy(expectedValue) ? LEGACY_BIT_RESOLUTION : BIT_RESOLUTION);
    return v -> (
        new Hash(
            new BigInteger(expectedValue), hashBitResolution, 0
        ).hammingDistance(
            new Hash(new BigInteger(v), hashBitResolution, 0)
        ) <= ClauseHelper.BIT_DISTANCE);

  }

  private static Predicate<String> getEqualPredicate(Clause clause) {
    return v -> Objects.equals(v, clause.getExpectedValueParametrized());
  }

  public static boolean isHashClause(Clause clause) {
    return Arrays.asList(CheckType.HASH, CheckType.HASH_CHANGED).contains(clause.getCheckType());
  }

  /**
   * Builds a predicate to use on clause check result to decide if the clause is
   * honored.
   * <p>
   * If clause uses regular expression on expected value, the predicate will
   * return true if its argument matches {@link Clause#getExpectedValue()};
   * othserwise the predicate will return true if its argument is equals to
   * {@link Clause#getExpectedValue()}.
   *
   * @param clause the clause to use
   * @return a predicate to use on clause check result to decide if the clause is
   * honored.
   */
  public static Predicate<String> buildValuePredicate(Clause clause) {
    LOGGER.debug("Builds predicate for clause {} with expected value='{}'", clause.getCheckType(),
        clause.getExpectedValueParametrized());
    if (clause.isUsingRegex()) {
      return getRegexPredicate(clause);
    }
    if (isHashClause(clause)) {
      return getHashHammingDistancePredicate(clause);
    }
    return getEqualPredicate(clause);
  }

}
