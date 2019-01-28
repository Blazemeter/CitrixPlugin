package com.blazemeter.jmeter.citrix.clause.strategy.check;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;

/**
 * Represents a function to call when computing clause value from a single
 * screenshot
 */
public interface ScreenshotAssessor {

	/**
	 * Does assessment on the specified image
	 * 
	 * @param screenshot the screenshot used as source
	 * @param selection  the sub area where to do computation; null for entire image
	 * @return the computed value of the clause
	 * @throws ClauseComputationException when computation fails
	 */
	String assess(BufferedImage screenshot, Rectangle selection) throws ClauseComputationException;
}