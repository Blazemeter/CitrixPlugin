package com.blazemeter.jmeter.citrix.recorder.gui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.jmeter.gui.tree.JMeterTreeNode;

/**
 * This class provides a renderer for printing "ICA Downloading Controller" tree.
 */
public class DownloadingControllerCellRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = -2787040480968015550L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
    JMeterTreeNode node = (JMeterTreeNode) ((DefaultMutableTreeNode) value).getUserObject();
    if (node != null) {
      super.getTreeCellRendererComponent(tree, node.getName(), selected, expanded, leaf, row,
          hasFocus);
      // print same icon as in test plan tree
      boolean enabled = node.isEnabled();
      ImageIcon icon = node.getIcon(enabled);
      if (icon != null) {
        if (enabled) {
          setIcon(icon);
        } else {
          setDisabledIcon(icon);
        }
      } else if (!enabled) { // i.e. no disabled icon found
        // Must therefore set the enabled icon so there is at least some icon
        icon = node.getIcon();
        if (icon != null) {
          setIcon(icon);
        }
      }
    }
    return this;
  }
}
