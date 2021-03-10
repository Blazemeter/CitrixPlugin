package com.blazemeter.jmeter.citrix.installer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assume.assumeThat;

import org.junit.Test;

public class WinRegistryTest {

  @Test
  public void accessDeniedWinRegTest() {
    assumeThat(WinRegistry.isAdmin(), is(false));

    String regPath = "SOFTWARE\\Citrix\\ICA Client";
    String regKey = "DummyKey";
    assertFalse(
        WinRegistry.setIntValueForKey(WinRegistry.HKEY_LOCAL_MACHINE, regPath, regKey, 0));

  }

  @Test
  public void getWinVersionTest() {
    String regPath = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
    String regKey = "ProductName";

    assertNotSame("", WinRegistry.getValueForKey(WinRegistry.HKEY_LOCAL_MACHINE, regPath, regKey));
  }

}
