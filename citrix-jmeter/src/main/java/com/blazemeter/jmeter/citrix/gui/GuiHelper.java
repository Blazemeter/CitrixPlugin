package com.blazemeter.jmeter.citrix.gui;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.gui.ClauseBuilderPanel;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GuiHelper {

  private GuiHelper() {
  }

  public static JLabel addLabeledComponent(JComponent component, String resLabel,
                                           JComponent parent) {
    return addLabeledComponent(component, resLabel, parent, false);
  }

  public static JLabel addLabeledComponent(JComponent component, String resLabel, JComponent parent,
                                           Insets insets) {
    return addLabeledComponent(component, resLabel, parent, false, insets);
  }

  public static JLabel addLabeledComponent(JComponent component, String resLabel, JComponent parent,
                                           boolean useJMeterRes) {
    return addLabeledComponent(component, resLabel, parent, useJMeterRes, new Insets(0, 0, 0, 0));
  }

  public static JLabel addLabeledComponent(JComponent component, String resLabel, JComponent parent,
                                           boolean useJMeterRes, Insets insets) {
    JLabel label = new JLabel(CitrixUtils.getResString(resLabel, useJMeterRes));
    label.setLabelFor(component);
    GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.BASELINE_LEADING;
    gbcLabel.fill = GridBagConstraints.HORIZONTAL;
    gbcLabel.insets = insets;
    parent.add(label, gbcLabel);

    GridBagConstraints gbcComponent = new GridBagConstraints();
    gbcComponent.anchor = GridBagConstraints.BASELINE;
    gbcComponent.fill = GridBagConstraints.HORIZONTAL;
    gbcComponent.gridwidth = GridBagConstraints.REMAINDER;
    gbcComponent.weightx = 1d;
    gbcComponent.insets = insets;
    parent.add(component, gbcComponent);

    return label;
  }

  public static Clause buildClause(Snapshot snapshot, Set<CheckType> checkTypes) {

    if (snapshot == null) {
      throw new IllegalArgumentException("snapshot must not be null");
    }
    ClauseBuilderPanel panel = new ClauseBuilderPanel();
    panel.setDragAreaVisible(false);
    panel.setImage(snapshot.getScreenshot());
    panel.setFgWindowArea(snapshot.getForegroundWindowArea());
    if (checkTypes != null) {
      panel.setCheckTypes(checkTypes);
    }
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    panel.addHierarchyListener(e -> {
      Window window = SwingUtilities.getWindowAncestor(panel);
      if (window instanceof Dialog) {
        Dialog dialog = (Dialog) window;
        Insets bounds =
            Toolkit.getDefaultToolkit().getScreenInsets(dialog.getGraphicsConfiguration());
        if (!dialog.isResizable()) {
          dialog.setResizable(true);
          panel.setPreferredSize(
              new Dimension(
                  (int) ((screenSize.getWidth() - bounds.left - bounds.right) * 0.8),
                  (int) ((screenSize.getHeight() - bounds.top - bounds.bottom) * 0.8)
              )
          );
        }
      }
    });

    int response = JOptionPane.showOptionDialog(null, panel,
        CitrixUtils.getResString("clause_builder_popup_title", false), JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE, null,
        new String[] {CitrixUtils.getResString("clause_builder_popup_validate", false)}, null);
    return 0 == response ? panel.getClause() : null;
  }
}
