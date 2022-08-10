package com.blazemeter.jmeter.citrix.utils;

import com.helger.commons.annotation.VisibleForTesting;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.jmeter.gui.GuiPackage;

public class DialogHelper {

  private static final int DEF_OPTION_VALE = -9; // The value to not collide with other options
  private static int defaultOptionDialogResponse = DEF_OPTION_VALE;

  private DialogHelper() {
  }

  @VisibleForTesting
  public static void setDefaultOptionDialogResponse(int value) {
    defaultOptionDialogResponse = value;
  }

  @VisibleForTesting
  public static void setDefaultOptionDialogResponse() {
    defaultOptionDialogResponse = DEF_OPTION_VALE;
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

  public static JFrame getJMeterFrameRoot() {
    GuiPackage jmInstance = GuiPackage.getInstance();
    if (jmInstance != null) {
      return ((JFrame) SwingUtilities.getRoot(jmInstance.getMainToolbar().getTopLevelAncestor()));
    } else {
      return null;
    }
  }

  public static void minimizeJMeter() {
    // Minimize the main window so the Citrix client becomes visible and focused.
    // Windows +10 do not allows to change focus between apps.
    JFrame jmRoot = getJMeterFrameRoot();
    if (jmRoot != null) {
      jmRoot.setState(Frame.ICONIFIED);
    }
  }

  public static void focusJMeter() {
    JFrame jmRoot = getJMeterFrameRoot();
    if (jmRoot != null) {
      jmRoot.setState(Frame.NORMAL);
    }
  }

  public static int showOptionDialog(Component parentComponent,
                                     Object message, String title, int optionType, int messageType,
                                     Object[] options, Object initialValue) {
    if (defaultOptionDialogResponse != DEF_OPTION_VALE) {
      return defaultOptionDialogResponse;
    } else {
      return JOptionPane.showOptionDialog(parentComponent,
          message,
          title,
          optionType,
          messageType, null, options, initialValue);
    }
  }

}
