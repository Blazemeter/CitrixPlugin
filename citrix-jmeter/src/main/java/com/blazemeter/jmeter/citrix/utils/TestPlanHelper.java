package com.blazemeter.jmeter.citrix.utils;

import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.ModuleController;
import org.apache.jmeter.control.TestFragmentController;
import org.apache.jmeter.control.gui.TreeNodeWrapper;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPlanHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestPlanHelper.class);

  private TestPlanHelper() {
  }

  /**
   * <p>
   * Checks whether the specified element of the test plan can be used as
   * controller to download ICA file.
   * </p>
   *
   * @param element the element of the test plan to check
   * @return true, if the element can be used to download ICA file; false
   * otherwise
   */
  public static boolean isValidDownloadingController(TestElement element) {
    return (element instanceof Controller) && !(element instanceof AbstractThreadGroup);
  }

  /**
   * Checks whether the specified element of the test plan can be diplayed in the
   * "Downloading Controller" tree component.
   *
   * @param element the element of the test plan to check
   * @param level   level of the element in test plan
   * @return true, if the element can be displayed in the tree component; false
   * otherwise
   */
  public static boolean isAllowedDownloadingControllerElement(TestElement element, int level) {
    return element instanceof TestFragmentController || element instanceof AbstractThreadGroup
        || (element instanceof Controller && !(element instanceof ModuleController) && level > 0);
  }

  public static JMeterTreeNode findFirstNodeOfType(Class<?> type) {
    JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
    List<JMeterTreeNode> nodes = treeModel.getNodesOfType(type);
    for (JMeterTreeNode node : nodes) {
      if (node.isEnabled()) {
        return node;
      }
    }
    return null;
  }

  public static JMeterTreeNode addToPlan(JMeterTreeNode parent, TestElement element) {
    final JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
    try {
      return treeModel.addComponent(element, parent);
    } catch (IllegalUserActionException e) {
      LOGGER.error("Unable to insert {} in {}", element.getName(), parent.getName(), e);
      JMeterUtils.reportErrorToUser(e.getMessage());
      return null;
    }
  }

  /**
   * Retrieves recursively the node matching the path from the specified node.
   *
   * @param node  the node from which the search starts
   * @param path  the path stored in test plan properties
   * @param level the current level of the recursion
   * @return the node matching the path stored in test plan properties
   */
  public static JMeterTreeNode findNodeByPath(JMeterTreeNode node, List<JMeterProperty> path,
                                              int level) {
    JMeterTreeNode result = null;
    int size = path.size();
    if (node != null && level < size) {
      final JMeterProperty property = path.get(level);
      final String propertyValue = property.getStringValue();
      if (propertyValue != null && propertyValue.equals(node.getName())) {
        if (level == size - 1) {
          result = node;
        } else {
          int index = 0;
          int count = node.getChildCount();
          while (index < count && result == null) {
            result = findNodeByPath((JMeterTreeNode) node.getChildAt(index), path, level + 1);
            index++;
          }
        }
      }
    }
    return result;
  }

  /**
   * <p>
   * Retrieves the node matching the path from the root of test plan.
   * </p>
   *
   * <p>
   * This implementation requires GUI mode.
   * </p>
   *
   * @param path the path stored in test plan properties
   * @return the node matching the path stored in test plan properties
   */
  public static JMeterTreeNode findNodeByPath(List<JMeterProperty> path) {
    JMeterTreeNode result = null;
    if (path != null && !path.isEmpty()) {
      GuiPackage gp = GuiPackage.getInstance();
      if (gp != null) {
        JMeterTreeNode root = (JMeterTreeNode) gp.getTreeModel().getRoot();
        result = TestPlanHelper.findNodeByPath(root, path, 0);
      }
    }
    return result;
  }

  public static <T> void listModelAddTo(ListModel<T> from, DefaultListModel<T> to) {
    for (int index = 0; index < from.getSize(); index++) {
      to.addElement(from.getElementAt(index));
    }
  }

  public static DefaultListModel<TreeNodeWrapper> getTestFragmentNodes(JMeterTreeNode node,
                                                                       String parentName,
                                                                       int level) {
    DefaultListModel<TreeNodeWrapper> listModel = new DefaultListModel<>();
    String separator = " > ";
    if (node != null) {
      for (int i = 0; i < node.getChildCount(); i++) {
        StringBuilder name = new StringBuilder();
        JMeterTreeNode cur = (JMeterTreeNode) node.getChildAt(i);
        TestElement te = cur.getTestElement();
        if (te instanceof TestFragmentController) {
          name.append(parentName);
          name.append(cur.getName());
          TreeNodeWrapper tnw = new TreeNodeWrapper(cur, name.toString());
          listModel.addElement(tnw);
          name.append(separator);
          DefaultListModel<TreeNodeWrapper> listModelToAppend =
              getTestFragmentNodes(cur, name.toString(),
                  level + 1);
          listModelAddTo(listModelToAppend, listModel);
        } else if (te instanceof TestPlan) {
          name.append(cur.getName());
          name.append(separator);
          DefaultListModel<TreeNodeWrapper> listModelToAppend =
              getTestFragmentNodes(cur, name.toString(), 0);
          listModelAddTo(listModelToAppend, listModel);
        }
      }
    }
    return listModel;
  }

  public static Map<String, String> getArguments(JMeterTreeNode root, List<String> propList) {
    HashMap<String, String> argumentsSet = new HashMap<>();

    for (int i = 0; i < root.getChildCount(); i++) {
      JMeterTreeNode node = (JMeterTreeNode) root.getChildAt(i);
      TestElement te = node.getTestElement();
      if (te instanceof Arguments) {
        Arguments arguments = (Arguments) te;
        CollectionProperty variables = arguments.getArguments();
        for (JMeterProperty variable : variables) {
          Argument arg = (Argument) variable.getObjectValue();
          String name = arg.getName();
          if (propList.contains(name)) {
            argumentsSet.put(name, arg.getValue());
          }
        }
      }
      argumentsSet.putAll(getArguments(node, propList));
    }
    return argumentsSet;
  }

  public static void updateArguments(JMeterTreeNode root, Map<String, String> argumentsSet) {

    for (int i = 0; i < root.getChildCount(); i++) {
      JMeterTreeNode node = (JMeterTreeNode) root.getChildAt(i);
      TestElement te = node.getTestElement();
      if (te instanceof Arguments) {
        Arguments arguments = (Arguments) te;
        CollectionProperty variables = arguments.getArguments();
        for (JMeterProperty variable : variables) {
          Argument arg = (Argument) variable.getObjectValue();
          String name = arg.getName();
          if (argumentsSet.containsKey(name)) {
            arg.setValue(argumentsSet.get(name));
          }
        }
      }
      updateArguments(node, argumentsSet);
    }
  }


  /**
   * Build the the nodeModel. Used to know the possibles targets controller
   *
   * @param node   Node to insert to model
   * @param prefix prefix used for node labeling
   */
  public static DefaultListModel<TreeNodeWrapper> buildSamplersParentModel(JMeterTreeNode node,
                                                                           String prefix) {
    DefaultListModel<TreeNodeWrapper> listModel = new DefaultListModel<>();
    String separator = " > ";
    if (node != null) {
      for (int i = 0; i < node.getChildCount(); i++) {
        StringBuilder name = new StringBuilder();
        JMeterTreeNode cur = (JMeterTreeNode) node.getChildAt(i);
        TestElement te = cur.getTestElement();
        if (te instanceof Controller && !(te instanceof CitrixRecorder)) {
          name.append(prefix);
          name.append(cur.getName());
          TreeNodeWrapper tnw = new TreeNodeWrapper(cur, name.toString());
          listModel.addElement(tnw);
          name.append(separator);
          DefaultListModel<TreeNodeWrapper> listModelToAppend =
              buildSamplersParentModel(cur, name.toString());
          listModelAddTo(listModelToAppend, listModel);
        } else if (te instanceof TestPlan) {
          name.append(cur.getName());
          name.append(separator);
          DefaultListModel<TreeNodeWrapper> listModelToAppend =
              buildSamplersParentModel(cur, name.toString());
          listModelAddTo(listModelToAppend, listModel);
        }
      }
    }
    return listModel;
  }
}
