package com.blazemeter.jmeter.citrix.ocr;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;

/**
 * Manages OCR Hides underlying implementation.
 */
public final class OcrManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(OcrManager.class);

	// JMeter property used to select language for OCR analysis
	private static final String OCR_LANGUAGE = JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "ocr_language",
			"eng");

	// JMeter property used to localized OCR configuration and language packs
	private static final String OCR_DATA_PATH = JMeterUtils.getProperty(CitrixUtils.PROPERTIES_PFX + "ocr_data_path");

	private static final Object EXTRACT_LOCKER = new Object();
	private static String extractedDataPath;

	private Tesseract1 tessInstance;

	// Get OCR default resources
	private static String getExtractedDataPath() {
		// Extract only once
		synchronized (EXTRACT_LOCKER) {
			if (extractedDataPath == null) {
				File dir = LoadLibs.extractTessResources("tessdata");
				extractedDataPath = dir.getAbsolutePath();
				LOGGER.debug("Tesseract resources extracted in {}", extractedDataPath);
			}
			return extractedDataPath;
		}
	}

	/**
	 * Instantiates a new {@link OcrManager}
	 */
	public OcrManager() {

		// Initialize and set up Tesseract instance
		String dataPath = StringUtils.isBlank(OCR_DATA_PATH) ? getExtractedDataPath() : OCR_DATA_PATH;
		tessInstance = new Tesseract1();

		LOGGER.debug("OCR manager {} uses language '{}'", this, OCR_LANGUAGE);
		tessInstance.setLanguage(OCR_LANGUAGE);

		LOGGER.debug("OCR manager {} uses data in {}", this, dataPath);
		tessInstance.setDatapath(dataPath);
	}

	/**
	 * Recognizes text in image.
	 * 
	 * @param imageToScan the image to analyse
	 * @param area        the image portion to analyse, null for the whole image
	 * @return the text from OCR analysis
	 * @throws OcrException when OCR analysis fails or when specified area is
	 *                      invalid
	 */
	// POSSIBLE_IMPROVEMENT : To avoid multiple image conversions Tesseract1.doOCR(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp) should be used.
	public String recognize(BufferedImage imageToScan, Rectangle area) throws OcrException {
		if (imageToScan == null) {
			throw new IllegalArgumentException("imageToScan must not be null.");
		}

		final Rectangle bounds = new Rectangle(imageToScan.getWidth(), imageToScan.getHeight());

		// Tesseract does not support wrong area (java.lang.Error: Invalid memory
		// access)
		if (area != null && !bounds.contains(area)) {
			String msg = MessageFormat.format("Unable to recognize text in area {0}: Exceeding image area {1}.", area,
					bounds);
			throw new OcrException(msg);
		}

		// Avoid miscalculation of the bpp by Tesseract by converting the image into grayscale
		imageToScan = ImageHelper.convertImageToGrayscale(imageToScan);
		
		// Do OCR analysis
		try {
			String text = tessInstance.doOCR(imageToScan, area);
			return text != null ? text.trim() : null;
		} catch (TesseractException e) {
			throw new OcrException(
					MessageFormat.format("Exception occurred while scanning area {0} of image {1}", area, imageToScan),
					e);
		}
	}

}
