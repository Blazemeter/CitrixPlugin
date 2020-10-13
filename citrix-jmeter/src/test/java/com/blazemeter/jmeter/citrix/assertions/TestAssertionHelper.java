package com.blazemeter.jmeter.citrix.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.clause.strategy.check.PollingContext;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the HashHelper class
 */
public class TestAssertionHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClauseHelper.class);
  BufferedImage image_same_01;
  BufferedImage image_same_02;
  BufferedImage image_not_same_01;
  BufferedImage image_not_same_02;
  final Class<TestAssertionHelper> helper = TestAssertionHelper.class;

  private BufferedImage loadImageResource(String imagePath) {
    try {
      return ImageIO.read(new File(helper.getResource(imagePath).getPath()));
    } catch (IOException e) {
      LOGGER.error("Error Loading Resource", e);
      return null;
    }
  }

  @Before
  public void init() {
    image_same_01 = loadImageResource("/hash_same_01.png");
    image_same_02 = loadImageResource("/hash_same_02.png");
    image_not_same_01 = loadImageResource("/hash_not_same_01.png");
    image_not_same_02 = loadImageResource("/hash_not_same_02.png");
  }

  @Test
  public void testHashRepeatability() throws ClauseComputationException {
    // The same image generate the same hash any time
    String hash1 = ClauseHelper.hash(image_same_01, null, null);
    String hash2 = ClauseHelper.hash(image_same_01, null, null);
    LOGGER.debug("Test same image if generate same hash: {},{}", hash1, hash2);
    assertEquals(hash1, hash2);
  }

  @Test
  public void testHashDiffPerceptualEqual() throws ClauseComputationException {
    // The image are the same at perceptual point of view but not a bit level
    String hash1 = ClauseHelper.hash(image_same_01, null, null);
    String hash2 = ClauseHelper.hash(image_same_02, null, null);
    LOGGER.debug("Test same perceptual image with diff hash: {},{}", hash1, hash2);
    assertNotEquals(hash1, hash2);
  }

  @Test
  public void testHashPerceptualHammingDistance() throws ClauseComputationException {
    String hash1 = ClauseHelper.hash(image_same_01, null, null);
    String hash2 = ClauseHelper.hash(image_same_02, null, null);

    // With different hash, test if the image generate perceptual equality
    Clause hash1Clause = new Clause(CheckType.HASH, hash1);
    LOGGER.debug("Test same perceptual image with hamming distance: {},{}", hash1, hash2);
    assertTrue(ClauseHelper.buildValuePredicate(hash1Clause).test(hash2));
  }

  @Test
  public void testHashColorVariance() throws ClauseComputationException {
    // Using the old Perceptual hash the hash was the same.
    // Image are different in the color perspective, the Average hash generate diff hash
    String hash1 = ClauseHelper.hash(image_not_same_01, null, null);
    String hash2 = ClauseHelper.hash(image_not_same_02, null, null);
    LOGGER.debug(
        "Test diff images with diff hashes (but same image with perceptual algorithm): {},{}",
        hash1, hash2);
    assertNotEquals(hash1, hash2);
  }

  @Test
  public void testHashColorVarianceHammingDistance() throws ClauseComputationException {
    // Two different hash with a variance of color perception not are the same image
    String hash1 = ClauseHelper.hash(image_not_same_01, null, null);
    String hash2 = ClauseHelper.hash(image_not_same_02, null, null);
    Clause hash1Clause = new Clause(CheckType.HASH, hash1);
    LOGGER.debug("Test diff images with with the clause using average algorithm: {},{}", hash1,
        hash2);
    assertFalse(ClauseHelper.buildValuePredicate(hash1Clause).test(hash2));
  }

  @Test
  public void testHashLegacyHammingDistance() throws ClauseComputationException {
    // Test the legacy precision using the new HammingDistance
    String hash1 = "123840";
    Clause hash1Clause = new Clause(CheckType.HASH, hash1);
    PollingContext context = new PollingContext(ClauseHelper.buildValuePredicate(hash1Clause),
        r -> ClauseHelper
            .getAbsoluteSelection(hash1Clause.getSelection(), hash1Clause.isRelative(), r),
        hash1Clause);
    String hash2 = ClauseHelper.hash(image_same_01, null, context);
    LOGGER.debug(hash2);

    LOGGER.debug("Test same images using clause and legacy algorithm: {},{}", hash1, hash2);
    assertTrue(ClauseHelper.buildValuePredicate(hash1Clause).test(hash2));
  }

  @Test
  public void testHashArea() throws ClauseComputationException {
    // test if the same screenshot with the same rectangle give the same result
    Rectangle rectangle = new Rectangle(new Point(25, 25), new Dimension(50, 50));
    String hash3 = ClauseHelper.hash(image_same_01, rectangle, null);
    String hash4 = ClauseHelper.hash(image_same_02, rectangle, null);
    assertEquals(hash3, hash4);
  }

  @Test
  public void testThrownExceptionsOfHash() throws ClauseComputationException {
    Rectangle rectangle2 = new Rectangle(new Point(1, 1), new Dimension(5000, 6000));
    ClauseHelper.hash(image_same_01, rectangle2, null);

  }
}

