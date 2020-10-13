package com.blazemeter.jmeter.citrix.client.windows;

/**
 * Provides Windows specific key event.
 */
public class KeyEvent {
  private final int keyId;

  /**
   * Instantiates a new {@link KeyEvent}.
   *
   * @param keyId the key identifier
   */
  public KeyEvent(int keyId) {
    this.keyId = keyId;
  }

  /**
   * Gets the key identifier of this event.
   *
   * @return the key identifier
   */
  public final int getKeyId() {
    return keyId;
  }
}
