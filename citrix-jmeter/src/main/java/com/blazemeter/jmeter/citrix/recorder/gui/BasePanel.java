package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.commons.SwingUtils;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class BasePanel {

  protected JPanel mainPanel;

  protected CitrixRecorderGUI recorderGUI;

  protected SwingUtils.ButtonBuilder baseButtonBuilder;

  public BasePanel(CitrixRecorderGUI recorderGUI) {
    super();
    this.recorderGUI = recorderGUI;

    // Set the event receiver to te recorder GUI
    baseButtonBuilder =
        new SwingUtils.ButtonBuilder().withActionListener(recorderGUI);

  }

  public JPanel getPanel() {
    return mainPanel;
  }

  protected GroupLayout buildGroupLayout(JPanel container) {
    GroupLayout layout = new GroupLayout(container);
    container.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    return layout;
  }

  protected TitledBorder createTitledBorder(String title) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
  }

  protected CitrixRecorder getRecorder() {
    return this.recorderGUI.getRecorder();
  }

}
