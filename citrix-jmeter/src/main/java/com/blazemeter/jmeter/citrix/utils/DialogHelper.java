package com.blazemeter.jmeter.citrix.utils;

import java.awt.Component;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

  public static void minimizeJMeter(Component root) {
    // Minimize the main window so the Citrix client becomes visible and focused.
    // Windows +10 do not allows to change focus between apps.
    ((JFrame) SwingUtilities.getRoot(root)).setState(Frame.ICONIFIED);
  }

  public static void focusJMeter(Component root) {
    ((JFrame) SwingUtilities.getRoot(root)).setState(Frame.NORMAL);
  }

}
