package com.blazemeter.jmeter.citrix.ocr;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages OCR Hides underlying implementation.
 */
public final class OcrManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(OcrManager.class);

  // JMeter property used to select language for OCR analysis
  private static final String OCR_LANGUAGE =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "ocr_language",
          "eng");

  // JMeter property used to localized OCR configuration and language packs
  private static final String OCR_DATA_PATH =
      JMeterUtils.getProperty(CitrixUtils.PROPERTIES_PFX + "ocr_data_path");

  // JMeter property used to localized OCR configuration and language packs
  private static final String OCR_DEFAULT_DPI =
      JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "ocr_default_dpi", "70");

  private static final Object EXTRACT_LOCKER = new Object();
  private static String extractedDataPath;

  private final Tesseract1 tessInstance;

  /**
   * Instantiates a new {@link OcrManager}.
   */
  public OcrManager() {
    // Initialize and set up Tesseract instance
    String dataPath = "";
    if (!StringUtils.isBlank(OCR_DATA_PATH)) {
      Path tessDataPath = Paths.get(OCR_DATA_PATH);
      Path tessDataDictionary = Paths.get(tessDataPath.toString(), OCR_LANGUAGE + ".traineddata");
      if (!Files.isDirectory(tessDataPath)) {
        LOGGER.error("OCR_DATA_PATH not exists: {}. Value is ignored and default value is used.",
            OCR_DATA_PATH);
      } else if (Files.notExists(tessDataDictionary)) {
        LOGGER.error("OCR_DATA_PATH appears not to be a Tesseract data directory: {} not found. " +
                "Value is ignored and default value is used.",
            tessDataDictionary.toAbsolutePath().toString());
      } else {
        dataPath = Paths.get(OCR_DATA_PATH).toAbsolutePath().toString();
      }
    }
    if (StringUtils.isBlank(dataPath)) {
      dataPath = getExtractedDataPath();
    }

    tessInstance = new Tesseract1();

    LOGGER.debug("OCR manager {} uses language '{}'", this, OCR_LANGUAGE);
    tessInstance.setLanguage(OCR_LANGUAGE);

    LOGGER.debug("OCR manager {} uses data in {}", this, dataPath);
    tessInstance.setDatapath(dataPath);

    LOGGER.debug("OCR manager {} setting default dpi to {}", this, OCR_DEFAULT_DPI);
    tessInstance.setVariable("user_defined_dpi", OCR_DEFAULT_DPI);
  }

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
   * Recognizes text in image.
   *
   * @param imageToScan the image to analyse
   * @param area        the image portion to analyse, null for the whole image
   * @return the text from OCR analysis
   * @throws OcrException when OCR analysis fails or when specified area is
   *                      invalid
   */
  // POSSIBLE_IMPROVEMENT : To avoid multiple image conversions
  // Tesseract1.doOCR(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp) should be used.
  public String recognize(BufferedImage imageToScan, Rectangle area) throws OcrException {
    if (imageToScan == null) {
      throw new IllegalArgumentException("imageToScan must not be null.");
    }

    final Rectangle bounds = new Rectangle(imageToScan.getWidth(), imageToScan.getHeight());

    // Avoid miscalculation of the bpp by Tesseract by converting the image into grayscale
    imageToScan = ImageHelper.convertImageToGrayscale(imageToScan);
    // Calculate intersection with bounds from selection area
    Rectangle intersection = bounds.intersection(area);
    // Do OCR analysis
    try {
      String text = tessInstance.doOCR(imageToScan, intersection);
      return text != null ? text.trim() : null;
    } catch (TesseractException | Error e) {
      throw new OcrException(
          MessageFormat
              .format("Exception occurred while scanning area {0} of image {1}", area, imageToScan),
          e);
    } // NOSONAR : We need to avoid Invalid memory access making threads exit

  }

}
