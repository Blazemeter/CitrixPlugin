package com.blazemeter.jmeter.citrix.sampler;

/**
 * Lists the types of interactions that the sampler can handle.
 */
public enum SamplerType {
  /**
   * The sampler simulates the entry of a text.
   */
  TEXT,

  /**
   * The sampler simulates a sequence of keystokes.
   */
  KEY_SEQUENCE,

  /**
   * The sampler simulates a click or double click with the mouse.
   */
  MOUSE_CLICK,

  /**
   * The sampler simulates a sequence of clicks and movements with the mouse.
   */
  MOUSE_SEQUENCE
}
