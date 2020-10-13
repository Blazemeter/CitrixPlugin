package com.blazemeter.jmeter.citrix.utils;

import javax.swing.JOptionPane;

public class DialogHelper {

  private DialogHelper() {
  }

  public static void showAlert(String message) {
    JOptionPane.showMessageDialog(null, message,
        CitrixUtils.getResString("dialog_helper_title_warning", false),
        JOptionPane.WARNING_MESSAGE);
  }

  public static void showError(String message) {
    JOptionPane.showMessageDialog(null, message,
        CitrixUtils.getResString("dialog_helper_title_error", false),
        JOptionPane.ERROR_MESSAGE);
  }
}
