package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.utils.TestPlanHelper;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import org.apache.jmeter.gui.tree.JMeterTreeNode;

/**
 * Provides a selection model for Downloading Controller tree component.
 */
public class DownloadingControllerTreeSelectionModel extends DefaultTreeSelectionModel {

  private static final long serialVersionUID = -5011434911282903840L;

  /**
   * <p>
   * Checks whether the specified node if selectable.
   * </p>
   *
   * @param node the node to test
   * @return true if the node is selectable; false otherwise
   */
  private boolean isValidDownloadingControllerNode(DefaultMutableTreeNode node) {
    JMeterTreeNode modelNode = null;
    if (node != null && node.getUserObject() instanceof JMeterTreeNode) {
      modelNode = (JMeterTreeNode) node.getUserObject();
    }
    return modelNode != null &&
        TestPlanHelper.isValidDownloadingController(modelNode.getTestElement());
  }

  /**
   * <p>
   * Apply the action when the specified node is selectable
   * </p>
   * <p>
   * WARNING : The {@link Runnable} interface is used just for its signature, not
   * for thread handling.
   * </p>
   *
   * @param node   the node to test
   * @param action the action to execute when node is selectable
   */
  private void applyWhenValid(DefaultMutableTreeNode node, Runnable action) {
    if (isValidDownloadingControllerNode(node)) {
      action.run();
    }
  }

  @Override
  public void setSelectionPath(TreePath path) {
    // Change the selection only when the matching node is valid
    applyWhenValid((DefaultMutableTreeNode) path.getLastPathComponent(),
        () -> super.setSelectionPath(path));
  }

  @Override
  public void setSelectionPaths(TreePath[] paths) {
    // Change the selection only when the matching node is valid
    applyWhenValid((DefaultMutableTreeNode) paths[paths.length - 1].getLastPathComponent(),
        () -> super.setSelectionPaths(paths));
  }

  @Override
  public void addSelectionPath(TreePath path) {
    // Add to the current selection only when the matching node is valid
    applyWhenValid((DefaultMutableTreeNode) path.getLastPathComponent(),
        () -> super.addSelectionPath(path));
  }

  @Override
  public void addSelectionPaths(TreePath[] paths) {
    // Add to the current selection only when the matching node is valid
    applyWhenValid((DefaultMutableTreeNode) paths[paths.length - 1].getLastPathComponent(),
        () -> super.addSelectionPaths(paths));
  }
}
