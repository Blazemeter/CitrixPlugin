package com.blazemeter.jmeter.citrix.client;

import static com.sun.jna.platform.win32.WinUser.MAPVK_VK_TO_VSC;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import java.awt.event.KeyEvent;

public class Win32Utils {

  public static int getVirtualKey(int keyCode) {
    // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-vkkeyscanexw
    WinDef.HKL keyLayoutID = User32.INSTANCE.GetKeyboardLayout(0);
    return User32.INSTANCE.VkKeyScanExW((char) keyCode, keyLayoutID);
  }

  public static char getVKCharacter(int vkCode, boolean vkShift, boolean vkAlt, boolean vkCtrl) {

    //https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getkeyboardlayout
    int currentThread = 0;
    WinDef.HKL keyLayoutID = User32.INSTANCE.GetKeyboardLayout(currentThread);

    // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-mapvirtualkeyexa
    int scanCode = User32.INSTANCE.MapVirtualKeyEx(vkCode, MAPVK_VK_TO_VSC, keyLayoutID);

    // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-tounicodeex
    byte[] keyStates = new byte[256];
    byte keyDownState = (byte) 128;
    keyStates[KeyEvent.VK_SHIFT] = vkShift ? keyDownState : 0;
    keyStates[KeyEvent.VK_ALT] = vkAlt ? keyDownState : 0;
    keyStates[KeyEvent.VK_CONTROL] = vkCtrl ? keyDownState : 0;

    char[] buff = new char[1];
    int buffCharSize = 1;
    int menuActiveFlag = 0;
    int ret = User32.INSTANCE.ToUnicodeEx(
        vkCode, scanCode, keyStates, buff, buffCharSize, menuActiveFlag, keyLayoutID
    );

    switch (ret) {
      case -1: //Error
        return (char) -1;

      case 0:  //No Translation
        return (char) 0;

      default: //Returning key...
        return buff[0];
    }
  }

}
