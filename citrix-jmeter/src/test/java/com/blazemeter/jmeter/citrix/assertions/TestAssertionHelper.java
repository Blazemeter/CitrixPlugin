package com.blazemeter.jmeter.citrix.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.blazemeter.jmeter.citrix.clauses.ClauseHelper;
import com.blazemeter.jmeter.citrix.clauses.ClauseComputationException;

/**
 * Test the HashHelper class
 */
public class TestAssertionHelper {
	BufferedImage image1;
	BufferedImage image2;

	@Before
	public void init() {
		String image1FileName = "/4328_1536568867580.png";
		String image1Path = TestAssertionHelper.class.getResource(image1FileName).getPath();

		String image2FileName = "/4328_1536568879467.png";
		String image2Path = TestAssertionHelper.class.getResource(image2FileName).getPath();

		try {
			image1 = ImageIO.read(new File(image1Path));
			image2 = ImageIO.read(new File(image2Path));
		} catch (IOException e) {
			// NOOP
		}
	}

	@Test
	public void testHash() throws ClauseComputationException {
		// test if the same screenshot with a null rectangle give the same result.
		String hash1 = ClauseHelper.hash(image1, null);
		String hash2 = ClauseHelper.hash(image1, null);
		assertEquals(hash1, hash2);

		// test if the same screenshot with the same rectangle give the same result
		Rectangle rectangle = new Rectangle(new Point(25, 25), new Dimension(50, 50));
		String hash3 = ClauseHelper.hash(image1, rectangle);
		String hash4 = ClauseHelper.hash(image2, rectangle);
		assertEquals(hash3, hash4);
		// assertNotEquals(hash1, hash3);

		// test if a different rectangle on the same screenshot give a different result
		Rectangle rectangle2 = new Rectangle(new Point(150, 150), new Dimension(70, 210));
		String hash5 = ClauseHelper.hash(image1, rectangle2);
		assertNotEquals(hash4, hash5);

		// test if a different screenshot with the same rectangle give a different
		// result
		String hash6 = ClauseHelper.hash(image2, rectangle2);
		assertNotEquals(hash5, hash6);
	}

	@Test(expected = ClauseComputationException.class)
	public void testThrownExceptionsOfHash() throws ClauseComputationException {

		Rectangle rectangle2 = new Rectangle(new Point(1, 1), new Dimension(5000, 6000));
		ClauseHelper.hash(image1, rectangle2);

//		try {
//			byte[] testBuffer = ImageHelper.convertImageToByteArray(image1);
//			Screenshot screenshot = new Screenshot(testBuffer, image1.getWidth(), image1.getHeight());
//			Rectangle rectangle = new Rectangle(new Point(1, -1), new Dimension(-3, 3));
//
//			// Check the exception thrown when the rectangle has negatives values
//			try {
//				HashHelper.hash(screenshot, rectangle);
//				fail("Hash using rectangle with negatives values has to throw Exception");
//			} catch (Exception e) {
//				// NOOP
//			}
//
//			// Check the exception thrown if the rectangle would be out of bonds of the
//			// screenshot
//			try {
//				Rectangle rectangle2 = new Rectangle(new Point(1, 1), new Dimension(5000, 6000));
//				HashHelper.hash(screenshot, rectangle2);
//				fail("Hash using rectangle with out of bound dimensions has to throw Exception");
//			} catch (Exception e) {
//				// NOOP
//			}
//		} catch (IOException e) {
//			fail("Wrong Exception in testThrownExceptionsOfHash");
//		}
	}

//	@Test
//	public void testHashMD5Type() throws ClauseComputationException {
//		// test if the hash of an entire screenshot is MD5 type
//		boolean isMd5 = false;
//		String hash1 = ClauseHelper.hash(image1, null);
//		Pattern pattern = Pattern.compile("^[A-f0-9]{32}$");
//		Matcher matcher = pattern.matcher(hash1);
//		while (matcher.find()) {
//			isMd5 = true;
//		}
//		assertEquals(true, isMd5);
//
//		// test if the hash of a part of a screenshot is MD5 type
//		isMd5 = false;
//		Rectangle rectangle = new Rectangle(new Point(1, 1), new Dimension(3, 3));
//		String hash2 = ClauseHelper.hash(image1, rectangle);
//		Matcher matcher2 = pattern.matcher(hash2);
//		while (matcher2.find()) {
//			isMd5 = true;
//		}
//		assertEquals(true, isMd5);
//	}

}
