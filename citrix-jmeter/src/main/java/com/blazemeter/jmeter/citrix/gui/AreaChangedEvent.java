package com.blazemeter.jmeter.citrix.gui;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * Provides event triggered when a property of type Rectangle is changed.
 */
public class AreaChangedEvent extends EventObject {

  private static final long serialVersionUID = -6345258791738580747L;

  private final Rectangle area;

  /**
   * Instantiates a new {@link AreaChangedEvent}.
   *
   * @param source the source of the event
   * @param area   the new value of the Rectangle property
   */
  public AreaChangedEvent(Object source, Rectangle area) {
    super(source);
    this.area = area;
  }

  /**
   * Gets the new value of the Rectangle property.
   *
   * @return the new value of the Rectangle property
   */
  public Rectangle getArea() {
    return area;
  }
}
