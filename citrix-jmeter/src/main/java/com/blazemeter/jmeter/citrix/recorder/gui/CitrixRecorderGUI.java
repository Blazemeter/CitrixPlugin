/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.CitrixClientFactoryException;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.installer.CitrixInstaller;
import com.blazemeter.jmeter.citrix.recorder.Capture.MouseCaptureOption;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder.CitrixRecorderHandler;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder.ClauseType;
import com.blazemeter.jmeter.citrix.recorder.gui.workers.DownloadIcaWorker;
import com.blazemeter.jmeter.citrix.recorder.gui.workers.MonitorRecordingWorker;
import com.blazemeter.jmeter.citrix.recorder.gui.workers.StartRecordingWorker;
import com.blazemeter.jmeter.citrix.recorder.gui.workers.StopRecordingWorker;
import com.blazemeter.jmeter.citrix.recorder.gui.workers.WaitCaptureWorker;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import com.blazemeter.jmeter.citrix.utils.TestPlanHelper;
import com.blazemeter.jmeter.commons.BlazemeterLabsLogo;
import com.blazemeter.jmeter.commons.SwingUtils;
import com.blazemeter.jmeter.commons.SwingUtils.ButtonBuilder;
import com.helger.commons.annotation.VisibleForTesting;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuListener;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.control.gui.TreeNodeWrapper;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.UnsharedComponent;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JMeterToolBar;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.control.RecordingController;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI for CitrixRecorder class.
 */
public class CitrixRecorderGUI extends AbstractControllerGui // NOSONAR Ignore inheritance warning
    implements ActionListener, CitrixRecorderHandler, UnsharedComponent {

  public static final String CLIENT_EXCEPTION = "client_exception";

  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixRecorderGUI.class);
  private static final long serialVersionUID = 1L;
  private static final String ICON_SIZE =
      JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE,
          JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE);
  private static final String ICON_PREFIX = "toolbar/" + ICON_SIZE;

  private static final String CMD_START_APPLICATION = "START_APPLICATION";
  private static final String START = "START";
  private static final String STOP = "STOP";
  private static final TreeNodeWrapper USE_RECORDING_CONTROLLER_NODE = new TreeNodeWrapper(null,
      CitrixUtils.getResString("use_recording_controller", true));

  static {
    try {
      if (GuiPackage.getInstance() != null) {
        CitrixInstaller.main(new String[0]);
      }
    } catch (IOException e) {
      LOGGER.error("Error running installer", e);
    }
  }

  private CitrixRecorder recorder;
  private boolean stoppingRecord = false;
  private boolean configuring = false;
  private boolean clearing = false;
  private boolean expectedDisconnect = false;

  private JButton btnAppStarted;
  private JButton btnStop;
  private JButton btnStopDialog;
  private JButton btnStart;
  private JButton btnRestart;

  private JTextField stepName;

  private transient ConfigurationPanel configurationPanel;

  // components used for sampler insertion in test plan
  private JComboBox<TreeNodeWrapper> cbbSamplerParent; // List of available target controllers
  private DefaultComboBoxModel<TreeNodeWrapper> samplerParentModel; // Model of targetNodes

  private JSyntaxTextArea logTextArea;
  private JProgressBar recorderStatus;
  private InteractionType capturedInteractionType;

  private transient RecorderDialog recorderDialog;

  private JPanel appStartPanel;
  private JPanel recordingStepPanel;
  private JPanel logPanel;
  private JTabbedPane tabMain;

  private transient CapturePanel capturePanel;

  private transient ButtonBuilder baseButtonBuilder =
      new SwingUtils.ButtonBuilder().isEnabled(false).withActionListener(this);

  public CitrixRecorderGUI() {
    super();

    try {
      this.recorderDialog = new RecorderDialog();
    } catch (HeadlessException ex) { // NOSONAR Needed for Headless tests
      // Ignore as due to Headless tests
    }

    init();
    setupTemplate();

  }

  public void setExpectedDisconnect(boolean expected) {
    expectedDisconnect = expected;
  }

  public RecorderDialog getRecorderDialog() {
    return this.recorderDialog;
  }

  @VisibleForTesting
  public void setRecorder(CitrixRecorder recorder) {
    this.recorder = recorder;
  }

  @VisibleForTesting
  public void setRecorderDialog(RecorderDialog dialog) {
    this.recorderDialog = dialog;
  }

  @VisibleForTesting
  public CapturePanel getCapturePanel() {
    return capturePanel;
  }

  public CitrixRecorder getRecorder() {
    return this.recorder;
  }

  private void setupTemplate() {
    TemplateUpdater templateUpdater =
        new TemplateUpdater(new File(JMeterUtils.getJMeterBinDir(), "templates"));
    try {
      templateUpdater.addTemplate("BlazeMeter Citrix Recording", "citrixRecordingTemplate.jmx",
          "bzmCitrixTemplate.xml", "/com/blazemeter/jmeter/citrix/template");
    } catch (IOException ex) {
      LOGGER.error("Error setting up templates using JMeter home: {}", JMeterUtils.getJMeterHome(),
          ex);
    }
  }

  private void init() {

    int mainLayoutVerticalGap = 5;
    int mainLayoutHorizontalGap = 0;
    setLayout(new BorderLayout(mainLayoutHorizontalGap, mainLayoutVerticalGap));
    setBorder(makeBorder());

    Box boxMainTop = new Box(BoxLayout.Y_AXIS);
    boxMainTop.add(makeTitlePanel());
    boxMainTop.add(createRecordingPanel());

    add(boxMainTop, BorderLayout.NORTH);

    Box boxMain = new Box(BoxLayout.Y_AXIS);
    tabMain = new JTabbedPane(SwingConstants.TOP);

    Box boxStatus = new Box(BoxLayout.Y_AXIS);

    configurationPanel = new ConfigurationPanel(this);
    JPanel icaContainer = new JPanel(new BorderLayout());
    icaContainer.add(configurationPanel.getPanel(), BorderLayout.NORTH);
    boxStatus.add(icaContainer);

    appStartPanel = createAppStartedPanel();
    recordingStepPanel = createRecordingStepPanel();
    recordingStepPanel.setVisible(false);

    tabMain.addTab("Configuration", boxStatus);

    logPanel = new JPanel();
    logPanel.setName("recordingLog");
    logPanel.add(createDownloadStatusIndicatorPanel());

    tabMain.addTab("Log", logPanel);

    boxMain.add(tabMain);

    add(boxMain, BorderLayout.CENTER);
    add(new BlazemeterLabsLogo("https://github.com/Blazemeter/CitrixPlugin"),
        BorderLayout.SOUTH);

    int recorderHorizontalGap = 10;
    int recorderVerticalGap = 10;
    recorderDialog.setLayout(new BorderLayout(recorderHorizontalGap, recorderVerticalGap));

    Box headerRecDialog = new Box(BoxLayout.Y_AXIS);

    headerRecDialog.add(appStartPanel);
    headerRecDialog.add(recordingStepPanel);

    recorderDialog.getContentPane().add(headerRecDialog, BorderLayout.CENTER);

    btnStopDialog = baseButtonBuilder.withName("stop").withAction(STOP)
        .withIcon(JMeterUtils.getImage(ICON_PREFIX + "/process-stop-4.png"))
        .build();

    GridLayout gridLayout = new GridLayout(1, 1);
    JPanel panelStop = new JPanel(gridLayout);
    panelStop.add(btnStopDialog);

    recorderDialog.getContentPane().add(panelStop, BorderLayout.WEST);

    recorderStatus = new JProgressBar(0, 2);
    recorderStatus.setStringPainted(true);

    capturePanel = new CapturePanel(this);

    VerticalPanel bottomRecDialog = new VerticalPanel();
    bottomRecDialog.add(recorderStatus);
    bottomRecDialog.add(capturePanel.getPanel());

    recorderDialog.getContentPane().add(bottomRecDialog, BorderLayout.SOUTH);

    recorderDialog.pack();
  }

  public void enableCaptureActions(boolean enable) {
    capturePanel.enableActions(enable);
  }

  private JPanel createRecordingPanel() {
    btnRestart = baseButtonBuilder.withName("restart").withAction("RESTART")
        .withIcon(JMeterUtils.getImage(ICON_PREFIX + "/edit-redo-7.png"))
        .build();
    btnStop = baseButtonBuilder.withName("stop").withAction(STOP)
        .withIcon(JMeterUtils.getImage(ICON_PREFIX + "/process-stop-4.png"))
        .build();
    btnStart = baseButtonBuilder.withName("start").withAction(START)
        .withIcon(JMeterUtils.getImage(ICON_PREFIX + "/arrow-right-3.png"))
        .build();

    JPanel recPanel = new JPanel();
    recPanel.setName("recordingControls");
    recPanel.add(btnStart);
    recPanel.add(Box.createHorizontalStrut(10));
    recPanel.add(btnStop);
    recPanel.add(Box.createHorizontalStrut(10));
    recPanel.add(btnRestart);

    return recPanel;
  }

  private JPanel createFieldPanel(String labelText, JComponent field) {
    HorizontalPanel panel = new HorizontalPanel();
    int fieldPanelEmptyBorder = 2;
    panel.setBorder(BorderFactory
        .createEmptyBorder(fieldPanelEmptyBorder, fieldPanelEmptyBorder, fieldPanelEmptyBorder,
            fieldPanelEmptyBorder));
    panel.add(createLabeledTextField(labelText, field));
    return panel;
  }

  private JPanel createLabeledTextField(String labelText, JComponent field) {
    JLabel label = new JLabel(CitrixUtils.getText(labelText)); // $NON-NLS-1$
    label.setLabelFor(field);
    int panelBorderHorizontalGap = 10;
    int panelBorderVerticalGap = 5;
    JPanel panel = new JPanel(new BorderLayout(panelBorderHorizontalGap, panelBorderVerticalGap));
    panel.add(label, BorderLayout.WEST);
    panel.add(field, BorderLayout.CENTER);
    return panel;
  }

  private Component createDownloadStatusIndicatorPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    logTextArea = JSyntaxTextArea.getInstance(20, 120, true);
    logTextArea.setText("");
    logTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
    logTextArea.setCodeFoldingEnabled(false);
    logTextArea.setAntiAliasingEnabled(false);
    logTextArea.setEditable(false);
    logTextArea.setLineWrap(true);
    logTextArea.setLanguage("text");
    logTextArea.setWrapStyleWord(true);
    panel.add(JTextScrollPane.getInstance(logTextArea), BorderLayout.NORTH);
    return panel;
  }

  private JPanel createAppStartedPanel() {
    // Button used to signal the remote application is started
    btnAppStarted = new JButton(CitrixUtils.getText("recorder_application_started"));
    btnAppStarted.setEnabled(false);
    btnAppStarted.setActionCommand(CMD_START_APPLICATION);
    btnAppStarted.addActionListener(this);

    return createFieldPanel(
        "When the application starts, press the button", btnAppStarted);
  }

  private JPanel createRecordingStepPanel() {
    samplerParentModel = new DefaultComboBoxModel<>();
    cbbSamplerParent = new JComboBox<>(samplerParentModel);
    cbbSamplerParent.setPrototypeDisplayValue(USE_RECORDING_CONTROLLER_NODE);
    cbbSamplerParent.addItemListener(e -> modifySamplersParent());

    // Force to refresh the list of JMeter elements always when focus is gained
    cbbSamplerParent.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        configureSamplersParent();
      }
    });

    PopupMenuListener listener = new BoundsPopupMenuListener(true, false);
    cbbSamplerParent.addPopupMenuListener(listener);

    stepName = new JTextField();
    stepName.setName("stepName");
    stepName.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        recorder.setStepName(stepName.getText());
      }
    });

    JLabel stepLabel = new JLabel("Step Name");
    JLabel targetLabel = new JLabel("Target Controller");

    JPanel recordingPanel = new JPanel();
    GroupLayout layout = buildGroupLayout(recordingPanel);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(stepLabel)
            .addComponent(targetLabel))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(stepName)
            .addComponent(cbbSamplerParent))
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(stepLabel)
            .addComponent(stepName))
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(targetLabel)
            .addComponent(cbbSamplerParent))
    );

    VerticalPanel panel = new VerticalPanel();
    panel.setBorder(null);
    panel.add(recordingPanel);

    return panel;
  }

  protected GroupLayout buildGroupLayout(JPanel container) {
    GroupLayout layout = new GroupLayout(container);
    container.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    return layout;
  }

  /**
   * Remove and re add the possibles target choices in the targetNodes comboBox.
   */
  private void configureSamplersParent() {
    samplerParentModel.removeAllElements();
    samplerParentModel.addElement(USE_RECORDING_CONTROLLER_NODE);
    GuiPackage gp = GuiPackage.getInstance();
    if (gp != null) {
      JMeterTreeNode testPlanRoot = (JMeterTreeNode) gp.getTreeModel().getRoot();
      DefaultListModel<TreeNodeWrapper> listModel =
          TestPlanHelper.buildSamplersParentModel(testPlanRoot, "");
      for (int index = 0; index < listModel.getSize(); index++) {
        samplerParentModel.addElement(listModel.getElementAt(index));
      }
    }

    JMeterTreeNode spNode = recorder.getSamplersParentNode();
    if (spNode != null) {
      final int size = samplerParentModel.getSize();
      TreeNodeWrapper selected = null;
      for (int index = 0; index < size; index++) {
        TreeNodeWrapper tnw = samplerParentModel.getElementAt(index);
        if (tnw.getTreeNode() == spNode) {
          selected = tnw;
          break;
        }
      }
      samplerParentModel.setSelectedItem(selected);
    } else {
      samplerParentModel.setSelectedItem(USE_RECORDING_CONTROLLER_NODE);
    }
  }

  // Allows to add listeners to recorder
  @Override
  public JPopupMenu createPopupMenu() {
    JPopupMenu pop = new JPopupMenu();
    JMenu addMenu = new JMenu(JMeterUtils.getResString("add")); // $NON-NLS-1$
    addMenu.add(MenuFactory.makeMenu(MenuFactory.LISTENERS, ActionNames.ADD));

    pop.add(addMenu);

    MenuFactory.addEditMenu(pop, true);
    MenuFactory.addFileMenu(pop);
    return pop;
  }

  private boolean isNewRecorder(CitrixRecorder recorder) {
    return "".equals(recorder.getPropertyAsString("storefrontURL", ""));
  }

  @Override
  public void configure(TestElement el) {
    super.configure(el);

    configuring = true;
    recorder = (CitrixRecorder) el;

    // When the recorder not use the default template, don't load data and hide configuration
    if (recorder.isFromTemplate()) {

      // When Recorder is new and UDV is with values, takes the values as default set
      if (isNewRecorder(recorder)) {
        configurationPanel.setConfigFromUV();
      }

      configurationPanel.loadConfigFromRecorder();

    } else {
      // Hide the containers from Portal and Credentials
      configurationPanel.hideConfigPanel();

      // Disable the validation and enable the Start Button
      configurationPanel.setFormValidationActive(false);
      configurationPanel.setFormValidation(true);
    }

    stepName.setText(recorder.getStepName());
    configureSamplersParent();
    configurationPanel.configureICADownloadingTestFragment();
    Set<MouseCaptureOption> options = recorder.getMouseCaptureOptions();

    capturePanel.setRelativeForeground(options.contains(MouseCaptureOption.RELATIVE_TO_FOREGROUND));
    capturePanel.setIncludeMouseMoves(options.contains(MouseCaptureOption.INCLUDE_MOVES));

    configurationPanel.validate();

    configuring = false;
  }

  public boolean isConfiguring() {
    return configuring;
  }

  public boolean isClearing() {
    return clearing;
  }

  private void modifySamplersParent() {
    if (!configuring && !clearing) {
      // Update test plan using "Samplers Parent" components
      JMeterTreeNode samplersParentNode = null;
      TreeNodeWrapper selectedWrapper = (TreeNodeWrapper) cbbSamplerParent.getSelectedItem();
      if (selectedWrapper != null) {
        samplersParentNode = selectedWrapper.getTreeNode();
      }

      // If no test plan element is selected, define any Recording controller as
      // temporary alternative
      if (samplersParentNode == null) {
        recorder.setAltSamplersParentNode(
            TestPlanHelper.findFirstNodeOfType(RecordingController.class));
      } else {
        recorder.setSamplersParentNode(samplersParentNode);
      }
    }
  }

  @Override
  public void modifyTestElement(TestElement el) {
    configureTestElement(el);
    if (recorder != null) {
      recorder.setHandler(null);
    }
    recorder = (CitrixRecorder) el;
    configurationPanel.modifyDownloadingController();
    modifySamplersParent();
    recorder.setHandler(this);
    configurationPanel.setConfigFromGUI();
  }

  // should never be used as we override getStaticLabel
  @Override
  public String getLabelResource() {
    return "";
  }

  @Override
  public String getStaticLabel() {
    return CitrixUtils.getResString("recorder_title", false);
  }

  @Override
  public TestElement createTestElement() {
    CitrixRecorder newTestElement = new CitrixRecorder();
    modifyTestElement(newTestElement);
    return newTestElement;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    LOGGER.debug("actionPerformed: {}", actionCommand);
    switch (actionCommand) {
      case START:
        startRecording();
        break;
      case STOP:
        stopRecording();
        break;
      // handle the start application button
      case CMD_START_APPLICATION:
        startApplication();
        break;

      // Handle the recording buttons
      case CapturePanel.CMD_ADD_ACTION:
        stopCapture(null);
        break;

      case CapturePanel.CMD_START_TEXT_CAPTURE:
        startCapture(InteractionType.KEY);
        break;

      case CapturePanel.CMD_START_MOUSE_CAPTURE:
        startCapture(InteractionType.MOUSE);
        break;

      case CapturePanel.CMD_CHECK_FULL_SCREENSHOT:
        stopCapture(ClauseType.FULL);
        break;

      case CapturePanel.CMD_CHECK_SELECTION_SCREENSHOT:
        stopCapture(ClauseType.FORCE_HASH);
        break;

      case CapturePanel.CMD_CHECK_OCR_SCREENSHOT:
        stopCapture(ClauseType.FORCE_OCR);
        break;
      case CapturePanel.CMD_DISCARD_ACTION:
        if (recorder.hasCapturedItems()) {
          int response = saveEventsPopup(true);
          // if cancel, we don't do anything
          if (response == JOptionPane.CANCEL_OPTION) {
            return;
          } else if (response == JOptionPane.NO_OPTION) {
            // if no is selected, clear capture and finish the function
            recorder.cancelCapture();
          }
          // if yes is selected, finish the function
        }
        stopCapture(null);
        break;
      default:
        LOGGER.error("Unknown actionCommand: {}", actionCommand);
    }
  }

  private void showRecorderDialog(boolean show) {
    recorderDialog.setVisible(show);
  }

  private void startApplication() {
    showRecorderDialog(false);
    btnAppStarted.setSelected(false);
    if (recorder.createStartApplicationSampler()) {
      toggleAppStartedUI(true);
      appStartPanel.setVisible(false);
      configurationPanel.getPanel().setVisible(false);
      recorderStatus.setVisible(false);
      recordingStepPanel.setVisible(true);
      capturePanel.getPanel().setVisible(true);
      recorderDialog.pack();
      showRecorderDialog(true);
      stepName.grabFocus();
      recorderDialog.setTopRightLocation();
    } else {
      showRecorderDialog(true);
    }
  }

  public void setStartEnabled(boolean enable) {
    btnStart.setEnabled(enable);
    btnStart.setToolTipText(enable ? "Start Recording" : "Some fields are invalid.");
  }

  public void stopRecording() {
    expectedDisconnect = false;
    toggleRecording(false, true);
  }

  public void stopRecordingNoCancel() {
    toggleRecording(false, false);
  }

  private void startRecording() {
    expectedDisconnect = true;
    setEnabledRecursively(configurationPanel.getPanel(), false);
    toggleRecording(true, true);
  }

  public static void setEnabledRecursively(Component component, boolean enabled) {
    component.setEnabled(enabled);
    if (component instanceof Container) {
      for (Component child : ((Container) component).getComponents()) {
        setEnabledRecursively(child, enabled);
      }
    }
  }

  private void toggleAppStartedUI(boolean appStarted) {
    this.btnAppStarted.setEnabled(!appStarted);
  }

  private void toggleCaptureUI(InteractionType type) {
    if (capturedInteractionType != type) {
      capturedInteractionType = type;

      boolean capturing = capturedInteractionType != null;

      capturePanel.startCapturing(capturing);

      recorderDialog.pack();

      if (isRecording() && !recorderDialog.isVisible()) {
        showRecorderDialog(true);
      }

    }
  }

  private int saveEventsPopup(boolean cancelable) {
    // Define available options according to cancelable switch
    Object[] options;
    if (cancelable) {
      options = new Object[] {CitrixUtils.getResString("yes", false),
          CitrixUtils.getResString("no", false), CitrixUtils.getResString("cancel", false)};
    } else {
      options = new Object[] {CitrixUtils.getResString("yes", false),
          CitrixUtils.getResString("no", false)};
    }
    int response = DialogHelper.showOptionDialog(GuiPackage.getInstance().getMainFrame(),
        CitrixUtils.getResString("save_events_popup_question", false),
        CitrixUtils.getResString("save_events_popup_title", false),
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.WARNING_MESSAGE, options, options[0]);

    // Interpret Close response according to cancelable
    if (response == JOptionPane.CLOSED_OPTION) {
      response = cancelable ? JOptionPane.CANCEL_OPTION : JOptionPane.OK_OPTION;
    }
    return response;
  }

  private void toggleRecordingUI(boolean recording) {
    toggleAppStartedUI(false);
    btnAppStarted.setEnabled(recording);
  }

  private boolean checkRecordingPrerequisites() {
    if (recorder.getDownloadingControllerNode() == null) {
      DialogHelper.showError(CitrixUtils.getResString("recorder_no_downloading_controller", false));
      return false;
    }

    if (recorder.getSamplersParentNode() == null && recorder.getAltSamplersParentNode() == null) {
      DialogHelper.showError(CitrixUtils.getResString("recorder_no_samplers_parent", false));
      return false;
    }
    return true;
  }

  private void toggleRecording(boolean startRecording, boolean cancelable) {
    LOGGER.debug("toggleRecording: {} {}", startRecording, cancelable);
    if (startRecording) {
      if (!recorder.isRecording() && checkRecordingPrerequisites()) {
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        btnStopDialog.setEnabled(true);
        tabMain.setSelectedComponent(logPanel);
        downloadIcaAndRecord();
        configurationPanel.getPanel().setVisible(false);
        recordingStepPanel.setVisible(false);
        capturePanel.getPanel().setVisible(false);
        appStartPanel.setVisible(true);
        recorderStatus.setVisible(true);
        recorderDialog.pack();
        showRecorderDialog(true);
        recorderDialog.setTopRightLocation();
      }
    } else {
      LOGGER.debug("isRecording: {} stoppingRecord: {}", recorder.isRecording(), stoppingRecord);
      if (recorder.isRecording() && !stoppingRecord) {
        if (recorder.hasCapturedItems()) {

          int response = saveEventsPopup(cancelable);
          // if cancel, we don't do anything
          if (response == JOptionPane.CANCEL_OPTION) {
            return;
          } else if (response == JOptionPane.NO_OPTION) {
            // if no is selected, clear capture and finish the function
            recorder.cancelCapture();
          }
          // if yes is selected, finish the function
        }
        stoppingRecord = true;
        showRecorderDialog(false);
        appendStatus("Stopping recorder");
        new StopRecordingWorker(this).execute();

        stoppingRecord = false;
      } else {
        if (!recorder.isRecording() && !stoppingRecord) {
          btnStart.setEnabled(true);
          btnStop.setEnabled(false);
          btnStopDialog.setEnabled(false);
          configurationPanel.getPanel().setVisible(true);
          setEnabledRecursively(configurationPanel.getPanel(), true);
          recordingStepPanel.setVisible(false);
          showRecorderDialog(false);
        }
      }
    }
  }

  /**
   * Starts the ICA Download and Record background jobs.
   */
  private void downloadIcaAndRecord() {
    if (!recorder.skipIcaFileDownloading()) {
      clearStatus();
      appendStatus("Downloading ICA File");
    }
    if (recorder.isFromTemplate()) {
      configurationPanel.updateUserDefinedValues();
    }
    // Only execute the download on real JMeter instance
    if (!recorder.skipIcaFileDownloading() && GuiPackage.getInstance() != null) {
      new DownloadIcaWorker(this).execute();
    }
  }

  private void startCapture(InteractionType interactionType) {
    if (!recorder.isCapturing()) {
      recorder.startCapture(interactionType);
      enableCaptureActions(false);
      new WaitCaptureWorker(this).execute();
    }
  }

  private void stopCapture(ClauseType clauseType) {
    if (clauseType != null) {
      showRecorderDialog(false);
    }
    recorder.stopCapture(clauseType);
  }

  @Override
  public Collection<String> getMenuCategories() {
    return Collections.singletonList(MenuFactory.NON_TEST_ELEMENTS);
  }

  @Override
  public void onSessionEvent(SessionEvent e) {
    switch (e.getEventType()) {
      case CONNECT:
        appendStatus("Citrix Client connected");
        break;
      case CONNECT_FAIL:
        appendStatus("Citrix Client failed to connected");
        break;
      case SHOW:
        appendStatus("Display Citrix Client");
        DialogHelper.minimizeJMeter();
        break;
      case HIDE:
        appendStatus("Hide Citrix Client");
        break;
      case LOGON:
        appendStatus("User logged in");
        break;
      case LOGON_FAIL:
        appendStatus("User failed to logged in");
        break;
      case ERROR:
        appendStatus(CitrixUtils.getResString("recorder_alert_error", false));
        break;
      case LOGOFF:
        if (!expectedDisconnect) {
          appendStatus("User logged off");
        }
        break;
      case DISCONNECT:
        if (!expectedDisconnect) {
          appendStatus(CitrixUtils.getResString("recorder_alert_disconnect", false));
        }
        expectedDisconnect = false;
        break;
      default:
        // NOOP for other events
        break;
    }

  }

  @Override
  public Clause onBuildClause(Snapshot snapshot, Set<CheckType> checkTypes) {
    return GuiHelper.buildClause(snapshot, checkTypes);
  }

  @Override
  public void onCaptureChanged(InteractionType interactionType) {
    LOGGER.debug("onCaptureChanged: {}", interactionType);
    toggleCaptureUI(interactionType);
  }

  @Override
  public void onRecordingChanged(boolean active) {
    toggleRecordingUI(active);
    if (!active) {
      appendStatus("Recorder stopped");
    }
  }

  public void setWait(boolean state) {
    recorderStatus.setIndeterminate(state);
  }

  public Boolean stopRecordingUI() throws CitrixClientException {
    setWait(true);
    recorder.stopRecord();
    return true;
  }

  public boolean startRecord(Optional<Path> icaPath)
      throws CitrixClientException, CitrixClientFactoryException {
    recorder.startRecord(icaPath);
    new MonitorRecordingWorker(this).execute();
    return true;
  }

  private void clearStatus() {
    logTextArea.setText("");
    recorderStatus.setString("");
  }

  public void appendStatus(String text) {
    logTextArea.append(text + "\n");
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    recorderStatus.setString(text);
    LOGGER.info(text);
  }

  public Boolean waitCapture() throws InterruptedException {
    boolean eventCaptured = false;
    while (recorder.isCapturing()) {
      if (recorder.hasCapturedItems()) {
        eventCaptured = true;
        break;
      }
      Thread.sleep(1000); // Free CPU
    }
    return eventCaptured;
  }

  public void downloadIcaFileDone(Optional<Path> path) {
    boolean canRecord = false;
    if (path.isPresent()) {
      appendStatus("Downloaded ICA File to " + path.get().toString());
      appendStatus("ICA File downloaded");
      appendStatus("Launching application and recording");
      canRecord = true;
    } else if (recorder.skipIcaFileDownloading()) {
      canRecord = true;
    } else {
      appendStatus(CitrixUtils.getResString("recorder_ica_downloading_failed", false));
    }
    if (canRecord) {
      StartRecordingWorker startRecorder = new StartRecordingWorker(this, path);
      startRecorder.execute();
    } else {
      stopRecordingNoCancel();
    }
  }

  public Optional<Path> downloadIcaFile() throws JMeterEngineException {
    setWait(true);
    return recorder.downloadIcaFile();
  }

  public boolean isRecording() {
    return recorder.isRecording();
  }

  public boolean clientIsRunning() {
    return recorder.clientIsRunning();
  }
}
