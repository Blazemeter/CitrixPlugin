package com.blazemeter.jmeter.citrix.installer;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinRegistry {

  public static final WinReg.HKEY HKEY_LOCAL_MACHINE = WinReg.HKEY_LOCAL_MACHINE;
  private static final int ERROR_CODE_ACCESS_DENIED = 5;

  private static final Logger LOGGER = LoggerFactory.getLogger(WinRegistry.class);

  /**
   * Reads value for the key from given path.
   *
   * @param hKey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
   * @param path Path to key
   * @param key  Key name
   * @return String value
   */
  public static String getValueForKey(WinReg.HKEY hKey, String path, String key) {
    Object value = Advapi32Util.registryGetValue(hKey, path, key);
    return value != null ? String.valueOf(value) : "";
  }

  public static boolean setIntValueForKey(WinReg.HKEY hKey, String path, String key, int value) {
    try {
      Advapi32Util.registrySetIntValue(hKey, path, key, value);
      return true;
    } catch (Win32Exception ex) {
      if (ex.getErrorCode() != ERROR_CODE_ACCESS_DENIED) {
        LOGGER.error("Unknown error setting Registry value:", ex);
      }
      return false;
    }
  }
}
