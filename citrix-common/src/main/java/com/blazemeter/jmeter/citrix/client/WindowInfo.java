package com.blazemeter.jmeter.citrix.client;

import java.awt.Rectangle;

/**
 * Provides details about existing windows in citrix session.
 */
public class WindowInfo {
  private Rectangle area;
  private String caption;

  /**
   * Instantiates a new {@link WindowInfo}.
   *
   * @param area    the area of the window
   * @param caption the caption of the window
   */
  public WindowInfo(Rectangle area, String caption) {
    this.area = area;
    this.caption = caption;
  }

  public WindowInfo(WindowInfo info) {
    this.area = new Rectangle(info.getArea());
    this.caption = info.getCaption();
  }

  /**
   * Gets the area of the window.
   *
   * @return the area of the window
   */
  public Rectangle getArea() {
    return area;
  }

  /**
   * Sets the area of the window.
   *
   * @param area the area of the window
   */
  public void setArea(Rectangle area) {
    this.area = area;
  }

  /**
   * Gets the caption of the window.
   *
   * @return the caption of the window
   */
  public String getCaption() {
    return caption;
  }

  /**
   * Sets the caption of the window.
   *
   * @param caption the caption of the window
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }
}
