package com.blazemeter.jmeter.citrix.ocr;

/**
 * Holds the OCR manager.
 */
public class OcrManagerHolder {

  protected static final ThreadLocal<OcrManager> LOCAL_OCR =
      ThreadLocal.withInitial(OcrManager::new);

  private OcrManagerHolder() {
    super();
  }

  /**
   * Gets the held {@link OcrManager}.
   *
   * @return held {@link OcrManager}
   */
  public static OcrManager getManager() {
    return LOCAL_OCR.get();
  }

}
