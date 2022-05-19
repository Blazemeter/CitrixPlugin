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
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.installer.CitrixInstaller;
import com.blazemeter.jmeter.citrix.recorder.Capture.MouseCaptureOption;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder.CitrixRecorderHandler;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder.ClauseType;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import com.blazemeter.jmeter.citrix.utils.TestPlanHelper;
import com.blazemeter.jmeter.commons.BlazemeterLabsLogo;
import com.blazemeter.jmeter.commons.FieldValidations;
import com.blazemeter.jmeter.commons.FormValidation;
import com.blazemeter.jmeter.commons.PlaceHolderPassword;
import com.blazemeter.jmeter.commons.PlaceHolderTextField;
import com.blazemeter.jmeter.commons.SwingUtils;
import com.blazemeter.jmeter.commons.SwingUtils.ButtonBuilder;
import com.blazemeter.jmeter.commons.ThemedIcon;
import com.blazemeter.jmeter.commons.Validation;
import com.helger.commons.annotation.VisibleForTesting;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.TestFragmentController;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.control.gui.TreeNodeWrapper;
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
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI for CitrixRecorder class.
 */
public class CitrixRecorderGUI extends AbstractControllerGui // NOSONAR Ignore inheritance warning
    implements ActionListener, CitrixRecorderHandler, UnsharedComponent {

  public static final String CMD_CHECK_FULL_SCREENSHOT = "CHECK_FULL_SCREENSHOT";
  public static final String CMD_CHECK_SELECTION_SCREENSHOT = "CHECK_SELECTION_SCREENSHOT";
  public static final String CMD_CHECK_OCR_SCREENSHOT = "CHECK_OCR_SCREENSHOT";
  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixRecorderGUI.class);
  private static final long serialVersionUID = 1L;
  private static final String ICON_SIZE =
      JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE,
          JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE);
  private static final String ICON_PREFIX = "toolbar/" + ICON_SIZE;

  private static final String CMD_TOGGLE_TEXT_CAPTURE = "TOGGLE_TEXT_CAPTURE";
  private static final String CMD_TOGGLE_MOUSE_CAPTURE = "TOGGLE_MOUSE_CAPTURE";
  private static final String CMD_START_APPLICATION = "START_APPLICATION";
  private static final String START = "START";
  private static final String STOP = "STOP";
  private static final String RESOURCE_KEY_START_TEXT_CAPTURE = "recorder_record_text_input";
  private static final String RESOURCE_KEY_START_MOUSE_CAPTURE = "recorder_record_mouse_click";
  private static final TreeNodeWrapper USE_RECORDING_CONTROLLER_NODE = new TreeNodeWrapper(null,
      CitrixUtils.getResString("use_recording_controller", true));
  private static final TreeNodeWrapper SELECT_CUSTOM_FRAGMENT = new TreeNodeWrapper(null,
      "Select custom fragment");
  private static final Validation NOT_EMPTY = new Validation(s -> !s.isEmpty(),
      "This field can't be empty");

  private static final String VISIBLE_CREDENTIALS_ICON = "visible-credentials.png";
  private static final String NOT_VISIBLE_CREDENTIAL_ICON = "not-visible-credentials.png";

  private static final String CITRIX_PORTAL_PORT = "citrix_portal_port";
  private static final String CITRIX_PORTAL_HOST = "citrix_portal_host";
  private static final String CITRIX_PORTAL_SCHEME = "citrix_portal_scheme";
  private static final String CITRIX_APP_NAME = "citrix_app_name";
  private static final String CITRIX_PORTAL_CONTEXT_PATH = "citrix_portal_context_path";
  private static final String CITRIX_USE_HTTPS = "citrix_use_https";
  private static final String CITRIX_LOGIN = "citrix_login";
  private static final String CITRIX_PASSWORD = "citrix_password";
  private static final String CITRIX_DOMAIN = "citrix_domain";

  private static final String CLIENT_EXCEPTION = "client_exception";

  private static final Pattern URL_PATTERN = Pattern.compile(
      "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
          "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
          "([).!';/?:,][[:blank:]])?$");
  private static final Pattern DOMAIN_PATTERN = Pattern.compile(
      "^((?!-)[A-Za-z0-9-]{1,63}(?<!-))+|^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}");
  private static final Validation URL_INVALID = new Validation(s -> {
    if (s.isEmpty()) {
      return false;
    }
    return URL_PATTERN.matcher(s).matches();
  }, "This field only accepts valid URL addresses");
  private static final Validation DOMAIN_INVALID = new Validation(s -> {
    if (s.isEmpty()) {
      return true; // Allow blank value in domain
    }
    return DOMAIN_PATTERN.matcher(s).matches();
  }, "This field only accepts valid domain name");

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
  private boolean expectedDisconnect = false;
  private boolean configuring = false;
  private boolean clearing = false;

  private JToggleButton captureText;
  private JToggleButton captureMouse;
  private JButton btnFullScreenshotCheck;
  private JButton btnPartialScreenshotCheck;
  private JButton btnOCRScreenshotCheck;
  private JButton btnAppStarted;
  private JButton btnStop;
  private JButton btnStopDialog;
  private JButton btnStart;
  private JButton btnRestart;

  private JPanel captureMouseOptions;
  private JCheckBox includeMouseMoves;
  private JCheckBox relativeForeground;

  private PlaceHolderTextField storeFrontURL;
  private PlaceHolderTextField username;
  private PlaceHolderPassword password;
  private PlaceHolderTextField domain;
  private PlaceHolderTextField application;
  private JTextField stepName;
  private JLabel userError = new JLabel();
  private JLabel passError = new JLabel();
  private JLabel storefrontUrlError = new JLabel();
  private JLabel domainError = new JLabel();
  private JLabel applicationError = new JLabel();

  private JPanel configurationPanel;

  /* component used to select the Test Fragment to download the ICA File*/
  private JComboBox<TreeNodeWrapper> testFragmentSelector;
  private DefaultComboBoxModel<TreeNodeWrapper> testFragmentSelectorModel;

  // components used for sampler insertion in test plan
  private JComboBox<TreeNodeWrapper> cbbSamplerParent; // List of available target controllers
  private DefaultComboBoxModel<TreeNodeWrapper> samplerParentModel; // Model of targetNodes

  private JSyntaxTextArea logTextArea;
  private JProgressBar recorderStatus;
  private InteractionType capturedInteractionType;

  private transient FormValidation formValidation;

  private transient Icon eyeIcon;
  private transient Icon eyeSlashIcon;

  private transient RecorderDialog recorderDialog;

  private JPanel appStartPanel;
  private JPanel recordingStepPanel;
  private JPanel capturePanel;
  private JPanel logPanel;
  private JTabbedPane tabMain;

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

  @VisibleForTesting
  public void setRecorder(CitrixRecorder recorder) {
    this.recorder = recorder;
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

    eyeIcon = ThemedIcon.fromResourceName(VISIBLE_CREDENTIALS_ICON);
    eyeSlashIcon = ThemedIcon.fromResourceName(NOT_VISIBLE_CREDENTIAL_ICON);

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

    configurationPanel = createIcaConfigurationPanel();
    JPanel icaContainer = new JPanel(new BorderLayout());
    icaContainer.add(configurationPanel, BorderLayout.NORTH);
    boxStatus.add(icaContainer);

    appStartPanel = createAppStartedPanel();
    recordingStepPanel = createRecordingStepPanel();
    recordingStepPanel.setVisible(false);

    capturePanel = new VerticalPanel();
    JPanel stepCapturePanel = createStepCapturePanel();
    JPanel stepFinalPanel = createFinalStepCapturePanel();
    capturePanel.add(stepCapturePanel, BorderLayout.NORTH);
    capturePanel.add(new JSeparator());
    capturePanel.add(stepFinalPanel, BorderLayout.CENTER);
    capturePanel.setVisible(false);

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

    VerticalPanel bottomRecDialog = new VerticalPanel();
    bottomRecDialog.add(recorderStatus);
    bottomRecDialog.add(capturePanel);

    recorderDialog.getContentPane().add(bottomRecDialog, BorderLayout.SOUTH);

    recorderDialog.pack();

    addValidations();

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

  private JPanel createIcaConfigurationPanel() {
    JPanel customFragmentSelector = createCustomFragmentSelector();

    JPanel panel = new JPanel();
    GroupLayout layout = buildGroupLayout(panel);
    panel.setBorder(createTitledBorder(""));

    JPanel storefrontPanel = createStoreFrontPanel();
    JPanel credentialsPanel = createConfigurationPanel();

    layout.setHorizontalGroup(layout.createParallelGroup()
        .addComponent(customFragmentSelector)
        .addComponent(storefrontPanel)
        .addGroup(layout.createSequentialGroup()
            .addComponent(credentialsPanel))
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addComponent(customFragmentSelector)
        .addComponent(storefrontPanel)
        .addGroup(layout.createParallelGroup()
            .addComponent(credentialsPanel))
    );
    return panel;
  }

  private GroupLayout buildGroupLayout(JPanel container) {
    GroupLayout layout = new GroupLayout(container);
    container.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    return layout;
  }

  private JPanel createStoreFrontPanel() {
    storeFrontURL = new PlaceHolderTextField();
    storeFrontURL.setPlaceHolder("<schema>://<host>:<port>/<context_path>");
    storeFrontURL.setName("storefrontURL");
    setupValidationLabel(storefrontUrlError, "storefrontUrlError");

    JLabel urlLabel = new JLabel("Storefront URL");

    JPanel panel = new JPanel();
    panel.setBorder(createTitledBorder("Connection"));
    GroupLayout layout = buildGroupLayout(panel);

    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(urlLabel)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(storeFrontURL)
            .addComponent(storefrontUrlError)
        )
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(urlLabel)
            .addComponent(storeFrontURL)
        )
        .addComponent(storefrontUrlError)
    );

    return panel;
  }

  private void setupValidationLabel(JLabel label, String name) {
    label.setText("N/A");
    label.setName(name);
    label.setForeground(Color.RED);
    label.setVisible(false);
  }

  private JPanel createCustomFragmentSelector() {
    testFragmentSelectorModel = new DefaultComboBoxModel<>();
    testFragmentSelector = new JComboBox<>(testFragmentSelectorModel);
    testFragmentSelector.setPrototypeDisplayValue(
        SELECT_CUSTOM_FRAGMENT); // $NON-NLS-1$ // Bug 56303 fixed the width of combo list
    testFragmentSelector.addItemListener(e -> modifyDownloadingController());

    JLabel label = new JLabel(CitrixUtils.getText("select_custom_fragment")); // $NON-NLS-1$
    label.setLabelFor(testFragmentSelector);

    HorizontalPanel panel = new HorizontalPanel();
    panel.add(label);
    panel.add(testFragmentSelector);

    return panel;
  }

  private void configureICADownloadingTestFragment() {
    testFragmentSelectorModel.removeAllElements();
    testFragmentSelectorModel.addElement(new TreeNodeWrapper(null, "Select custom fragment"));
    GuiPackage gp = GuiPackage.getInstance();
    if (gp != null) {
      JMeterTreeNode testPlanRoot = (JMeterTreeNode) gp.getTreeModel().getRoot();
      DefaultListModel<TreeNodeWrapper> listModel =
          TestPlanHelper.getTestFragmentNodes(testPlanRoot, "", 0);
      for (int index = 0; index < listModel.getSize(); index++) {
        testFragmentSelectorModel.addElement(listModel.getElementAt(index));
      }
    }

    JMeterTreeNode spNode = recorder.getDownloadingControllerNode();
    if (spNode != null) {
      final int size = testFragmentSelectorModel.getSize();
      TreeNodeWrapper selected = null;
      for (int index = 0; index < size; index++) {
        TreeNodeWrapper tnw = testFragmentSelectorModel.getElementAt(index);
        if (tnw.getTreeNode() == spNode) {
          selected = tnw;
          break;
        }
      }
      testFragmentSelectorModel.setSelectedItem(selected);
    } else {
      if (testFragmentSelectorModel.getSize() > 1) {
        testFragmentSelectorModel.setSelectedItem(testFragmentSelectorModel.getElementAt(1));
      }
    }
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

  private TitledBorder createTitledBorder(String title) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
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

  private PlaceHolderTextField buildPlaceHolder(String name, String placeholder) {
    PlaceHolderTextField textField = new PlaceHolderTextField();
    textField.setPlaceHolder(placeholder);
    textField.setName(name);
    return textField;
  }

  private JPanel createConfigurationPanel() {
    Dimension preferredPlaceHolderDimension = new Dimension(200, 30);

    username = buildPlaceHolder("username", "User's name");
    username.setPreferredSize(preferredPlaceHolderDimension);
    setupValidationLabel(userError, "userError");

    domain = buildPlaceHolder("domain", "Domain's name");
    setupValidationLabel(domainError, "domainError");

    application = buildPlaceHolder("application", "Application's name");
    setupValidationLabel(applicationError, "applicationError");

    password = new PlaceHolderPassword();
    password.setName("password");
    password.setPlaceHolder("User's Password");
    setupValidationLabel(passError, "passError");

    JLabel domainLabel = new JLabel("Domain");
    JLabel passLabel = new JLabel("Password");
    JLabel userLabel = new JLabel("Username");
    JLabel applicationLabel = new JLabel("Application");

    JToggleButton display = new JToggleButton();
    display.setBorderPainted(false);
    display.setContentAreaFilled(false);
    display.setIcon(eyeIcon);
    display.setRequestFocusEnabled(false);
    display.setMargin(new Insets(display.getMargin().top, 0, display.getMargin().bottom, 0));

    display.setName("display");
    display.addItemListener(e -> {
      if (display.isSelected()) {
        password.setEchoChar((char) 0);
        display.setIcon(eyeSlashIcon);
      } else {
        password.setEchoChar('*');
        display.setIcon(eyeIcon);
      }
    });

    JPanel panel = new JPanel();
    GroupLayout layout = buildGroupLayout(panel);
    panel.setBorder(createTitledBorder("Login's Info"));
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(domainLabel)
            .addComponent(userLabel)
            .addComponent(passLabel)
            .addComponent(applicationLabel))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(domain)
            .addComponent(domainError)
            .addComponent(username)
            .addComponent(userError)
            .addComponent(password)
            .addComponent(passError)
            .addComponent(application)
            .addComponent(applicationError))
        .addComponent(display)
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(userLabel)
            .addComponent(username))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(userError)))
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(passLabel)
            .addComponent(password)
            .addComponent(display))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(passError)))
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(domainLabel)
            .addComponent(domain))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(domainError)))
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(applicationLabel)
            .addComponent(application))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(applicationError)))
    );

    layout.linkSize(SwingConstants.HORIZONTAL, username, password, domain, application);
    layout.linkSize(SwingConstants.VERTICAL, username, password, domain, application, display);

    return panel;
  }

  private Component createDownloadStatusIndicatorPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    logTextArea = JSyntaxTextArea.getInstance(20, 120, true);
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

  private JPanel createStepCapturePanel() {
    // Button used to capture keyboard interactions
    captureText = buildToggleButton(RESOURCE_KEY_START_TEXT_CAPTURE, CMD_TOGGLE_TEXT_CAPTURE);
    // Button used to capture mouse interactions
    captureMouse = buildToggleButton(RESOURCE_KEY_START_MOUSE_CAPTURE, CMD_TOGGLE_MOUSE_CAPTURE);
    JPanel mouseCaptureOptionsPanel = createMouseCaptureOptions();

    JLabel stepCaptureLabel = new JLabel("Pick an action to record");
    JLabel orLabel = new JLabel("OR");

    JPanel panel = new JPanel();
    GroupLayout layout = buildGroupLayout(panel);

    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(stepCaptureLabel)
        .addComponent(captureText)
        .addComponent(orLabel)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(captureMouse)
            .addComponent(mouseCaptureOptionsPanel))
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
            .addComponent(stepCaptureLabel)
            .addComponent(captureText)
            .addComponent(orLabel)
            .addComponent(captureMouse))
        .addComponent(mouseCaptureOptionsPanel)
    );

    return panel;
  }

  private JToggleButton buildToggleButton(String textName, String command) {
    JToggleButton button = new JToggleButton(CitrixUtils.getText(textName));
    button.setActionCommand(command);
    button.addActionListener(this);
    button.setEnabled(false);
    return button;
  }

  private JPanel createMouseCaptureOptions() {
    // Panel with the Mouse Capture options
    captureMouseOptions = new JPanel();
    captureMouseOptions
        .setBorder(BorderFactory.createTitledBorder(
            CitrixUtils.getText("recorder_mouse_capture_options")));
    captureMouseOptions.setEnabled(false);

    // Checkbox used to include/exclude mouse moves during capture
    includeMouseMoves =
        new JCheckBox(CitrixUtils.getText("recorder_mouse_capture_options_include_mouse_moves"));
    includeMouseMoves.setEnabled(false);
    includeMouseMoves.addActionListener(e -> {
      if (includeMouseMoves.isSelected()) {
        recorder.getMouseCaptureOptions().add(MouseCaptureOption.INCLUDE_MOVES);
      } else {
        recorder.getMouseCaptureOptions().remove(MouseCaptureOption.INCLUDE_MOVES);
      }
    });

    // Checkbox used to include/exclude mouse moves during capture
    relativeForeground = new JCheckBox(
        CitrixUtils.getText("recorder_mouse_capture_options_relative"));
    relativeForeground.setEnabled(false);
    relativeForeground.addActionListener(e -> {
      if (relativeForeground.isSelected()) {
        recorder.getMouseCaptureOptions().add(MouseCaptureOption.RELATIVE_TO_FOREGROUND);
      } else {
        recorder.getMouseCaptureOptions().remove(MouseCaptureOption.RELATIVE_TO_FOREGROUND);
      }
    });

    JPanel panel = new JPanel();
    panel.setBorder(createTitledBorder(CitrixUtils.getText("recorder_mouse_capture_options")));
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    layout.setHorizontalGroup(layout.createParallelGroup()
        .addComponent(includeMouseMoves)
        .addComponent(relativeForeground)
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addComponent(includeMouseMoves)
        .addComponent(relativeForeground)
    );

    return panel;
  }

  private JPanel createFinalStepCapturePanel() {

    // Button used to stop capture and build Full Screenshot Hash assertion
    btnFullScreenshotCheck = baseButtonBuilder.withAction(CMD_CHECK_FULL_SCREENSHOT)
        .withName(CitrixUtils.getText("recorder_full_screenshot_check"))
        .build();

    // Button used to stop capture and build Partial Screenshot Hash assertion
    btnPartialScreenshotCheck = baseButtonBuilder.withAction(CMD_CHECK_SELECTION_SCREENSHOT)
        .withName(CitrixUtils.getText("recorder_selection_screenshot_check"))
        .build();

    // Button used to stop capture and build Partial of Full Screenshot OCR assertion
    btnOCRScreenshotCheck = baseButtonBuilder.withAction(CMD_CHECK_OCR_SCREENSHOT)
        .withName(CitrixUtils.getText("recorder_ocr_screenshot_check"))
        .build();

    JLabel finalStepCaptureLabel = new JLabel("Pick final action check");

    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addComponent(finalStepCaptureLabel)
        .addComponent(btnFullScreenshotCheck)
        .addComponent(btnPartialScreenshotCheck)
        .addComponent(btnOCRScreenshotCheck)
    );

    layout.setVerticalGroup(layout.createParallelGroup(Alignment.BASELINE)
        .addComponent(finalStepCaptureLabel)
        .addComponent(btnFullScreenshotCheck)
        .addComponent(btnPartialScreenshotCheck)
        .addComponent(btnOCRScreenshotCheck)
    );

    return panel;
  }

  private void addValidations() {
    FieldValidations storeFrontValidations =
        new FieldValidations(storeFrontURL, storefrontUrlError);
    FieldValidations userValidations = new FieldValidations(username, userError);
    FieldValidations passwordValidations = new FieldValidations(password, passError);
    FieldValidations appValidation = new FieldValidations(application, applicationError);
    FieldValidations domainValidation = new FieldValidations(domain, domainError);

    storeFrontValidations.addValidations(URL_INVALID);
    userValidations.addValidations(NOT_EMPTY);
    passwordValidations.addValidations(NOT_EMPTY);
    appValidation.addValidations(NOT_EMPTY);
    domainValidation.addValidations(DOMAIN_INVALID);

    formValidation = new FormValidation(Arrays
        .asList(storeFrontValidations, userValidations, passwordValidations, domainValidation,
            appValidation));

    formValidation.onSuccess(() -> setFormValidation(true));
    formValidation.onFailure(() -> setFormValidation(false));

    storeFrontURL.addFocusListener(validateForms(formValidation));
    username.addFocusListener(validateForms(formValidation));
    password.addFocusListener(validateForms(formValidation));
    domain.addFocusListener(validateForms(formValidation));
    application.addFocusListener(validateForms(formValidation));
  }

  private FocusAdapter validateForms(FormValidation formValidation) {
    return new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        super.focusLost(e);
        //Only displays the error in the Fields that triggers the event
        formValidation.validate((JTextField) e.getSource());
      }
    };
  }

  private void setFormValidation(boolean enable) {
    if (!recorder.isRecording()) {
      btnStart.setEnabled(enable);
      btnStart.setToolTipText(enable ? "Start Recording" : "Some fields are invalid.");
    }
  }

  /**
   * Build the the nodeModel. Used to know the possibles targets controller
   *
   * @param node   Node to insert to model
   * @param prefix prefix used for node labeling
   */
  private void buildSamplersParentModel(JMeterTreeNode node, String prefix) {
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
          samplerParentModel.addElement(tnw);
          name.append(separator);
          buildSamplersParentModel(cur, name.toString());
        } else if (te instanceof TestPlan) {
          name.append(cur.getName());
          name.append(separator);
          buildSamplersParentModel(cur, name.toString());
        }
      }
    }
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
      buildSamplersParentModel(testPlanRoot, "");
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

        HashMap<String, String> propSet = getUserDefinedValues();

        String portalPort = propSet.getOrDefault(CITRIX_PORTAL_PORT, "");
        String url = propSet.getOrDefault(CITRIX_PORTAL_SCHEME, "") + "://" +
            propSet.getOrDefault(CITRIX_PORTAL_HOST, "") +
            ("".equals(portalPort) || "-1".equals(portalPort) ? "" : (":" + portalPort)) +
            propSet.getOrDefault(CITRIX_PORTAL_CONTEXT_PATH, "");
        if (!"://".equals(url)) {
          recorder.setStorefrontURL(url);
        }
        recorder.setUsername(propSet.getOrDefault(CITRIX_LOGIN, ""));
        recorder.setPassword(propSet.getOrDefault(CITRIX_PASSWORD, ""));
        recorder.setDomain(propSet.getOrDefault(CITRIX_DOMAIN, ""));
        recorder.setApplicationName(propSet.getOrDefault(CITRIX_APP_NAME, ""));

      }

      storeFrontURL.setText(recorder.getStorefrontURL());

      username.setText(recorder.getUsername());
      password.setText(recorder.getPassword());
      domain.setText(recorder.getDomain());
      application.setText(recorder.getApplicationName());

    } else {
      // Hide the containers from Portal and Credentials
      storeFrontURL.getParent().setVisible(false);
      username.getParent().setVisible(false);

      // Disable the validation and enable the Start Button
      formValidation.setActive(false);
      setFormValidation(true);
    }

    stepName.setText(recorder.getStepName());
    configureSamplersParent();
    configureICADownloadingTestFragment();
    Set<MouseCaptureOption> options = recorder.getMouseCaptureOptions();
    relativeForeground.setSelected(options.contains(MouseCaptureOption.RELATIVE_TO_FOREGROUND));
    includeMouseMoves.setSelected(options.contains(MouseCaptureOption.INCLUDE_MOVES));

    formValidation.validate();

    configuring = false;
  }

  private void modifyDownloadingController() {
    if (!configuring && !clearing) {
      // Update test plan using "Downloading controller" components
      JMeterTreeNode selectedNode = null;
      TreeNodeWrapper selectedWrapper = (TreeNodeWrapper) testFragmentSelectorModel
          .getSelectedItem();
      if (selectedWrapper != null) {
        selectedNode = selectedWrapper.getTreeNode();
      }

      //If no TestFragment is selected, define the first appearance as a temporal alternative
      if (selectedNode == null) {
        recorder.setAltSamplersParentNode(
            TestPlanHelper.findFirstNodeOfType(TestFragmentController.class));
      }
      recorder.setDownloadingControllerNode(selectedNode);
    }
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
    modifyDownloadingController();
    modifySamplersParent();
    recorder.setHandler(this);
    recorder.setUsername(username.getText());
    recorder.setPassword(String.valueOf(password.getPassword()));
    recorder.setStorefrontURL(storeFrontURL.getText());
    recorder.setDomain(domain.getText());
    recorder.setApplicationName(application.getText());
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
        recorderDialog.setVisible(false);
        btnAppStarted.setSelected(false);
        if (recorder.createStartApplicationSampler()) {
          toggleAppStartedUI(true);
          appStartPanel.setVisible(false);
          configurationPanel.setVisible(false);
          recorderStatus.setVisible(false);
          recordingStepPanel.setVisible(true);
          capturePanel.setVisible(true);
          recorderDialog.pack();
          recorderDialog.setVisible(true);
          stepName.grabFocus();
          recorderDialog.setTopRightLocation();
        } else {
          recorderDialog.setVisible(true);
        }

        break;

      // Handle the recording buttons
      case CMD_TOGGLE_TEXT_CAPTURE:
        toggleButton(captureText, InteractionType.KEY);
        break;

      case CMD_TOGGLE_MOUSE_CAPTURE:
        toggleButton(captureMouse, InteractionType.MOUSE);
        break;

      case CMD_CHECK_FULL_SCREENSHOT:
        stopCapture(ClauseType.FULL);
        break;

      case CMD_CHECK_SELECTION_SCREENSHOT:
        stopCapture(ClauseType.FORCE_HASH);
        break;

      case CMD_CHECK_OCR_SCREENSHOT:
        stopCapture(ClauseType.FORCE_OCR);
        break;
      default:
        LOGGER.error("Unknown actionCommand: {}", actionCommand);
    }
  }

  private void stopRecording() {
    expectedDisconnect = false;
    toggleRecording(false, true);
  }

  private void startRecording() {
    expectedDisconnect = true;
    setEnabledRecursively(configurationPanel, false);
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

  private void toggleButton(JToggleButton button, InteractionType key) {
    boolean selected = button.isSelected();
    button.setSelected(false);
    if (selected) {
      startCapture(key);
    } else {
      stopCapture(null);
    }
  }

  private void toggleAppStartedUI(boolean appStarted) {
    this.btnAppStarted.setEnabled(!appStarted);
    captureMouse.setEnabled(appStarted);
    captureText.setEnabled(appStarted);
    toggleMouseCaptureOptionsUI(appStarted);
  }

  private void toggleMouseCaptureOptionsUI(boolean active) {
    captureMouseOptions.setEnabled(active);
    includeMouseMoves.setEnabled(active);
    relativeForeground.setEnabled(active);
  }

  private void toggleCaptureUI(InteractionType type) {
    if (capturedInteractionType != type) {
      capturedInteractionType = type;

      boolean capturingMouse = capturedInteractionType == InteractionType.MOUSE;
      String mouseLabel =
          capturingMouse ? "recorder_mouse_selected" : RESOURCE_KEY_START_MOUSE_CAPTURE;
      captureMouse.setSelected(capturingMouse);
      captureMouse.setText(CitrixUtils.getResString(mouseLabel, false));

      boolean capturingKey = capturedInteractionType == InteractionType.KEY;
      String keyLabel = capturingKey ? "recorder_text_selected" : RESOURCE_KEY_START_TEXT_CAPTURE;
      captureText.setSelected(capturingKey);
      captureText.setText(CitrixUtils.getResString(keyLabel, false));

      boolean capturing = capturedInteractionType != null;
      btnFullScreenshotCheck.setEnabled(capturing);
      btnPartialScreenshotCheck.setEnabled(capturing);
      btnOCRScreenshotCheck.setEnabled(capturing);
      toggleMouseCaptureOptionsUI(!capturing);
    }
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
        configurationPanel.setVisible(false);
        recordingStepPanel.setVisible(false);
        capturePanel.setVisible(false);
        appStartPanel.setVisible(true);
        recorderStatus.setVisible(true);
        recorderDialog.pack();
        recorderDialog.setVisible(true);
        recorderDialog.setTopRightLocation();
      }
    } else {
      LOGGER.debug("isRecording: {} stoppingRecord: {}", recorder.isRecording(), stoppingRecord);
      if (recorder.isRecording() && !stoppingRecord) {
        if (recorder.hasCapturedItems()) {

          // Define available options according to cancelable switch
          Object[] options;
          if (cancelable) {
            options = new Object[] {CitrixUtils.getResString("yes", false),
                CitrixUtils.getResString("no", false), CitrixUtils.getResString("cancel", false)};
          } else {
            options = new Object[] {CitrixUtils.getResString("yes", false),
                CitrixUtils.getResString("no", false)};
          }
          int response = JOptionPane.showOptionDialog(GuiPackage.getInstance().getMainFrame(),
              CitrixUtils.getResString("save_events_popup_question", false),
              CitrixUtils.getResString("save_events_popup_title", false),
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.WARNING_MESSAGE, null, options, options[0]);

          // Interpret Close response according to cancelable
          if (response == JOptionPane.CLOSED_OPTION) {
            response = cancelable ? JOptionPane.CANCEL_OPTION : JOptionPane.OK_OPTION;
          }

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
        appendStatus("Stopping recorder");
        new StopRecording().execute();

        stoppingRecord = false;
      } else {
        if (!recorder.isRecording() && !stoppingRecord) {
          btnStart.setEnabled(true);
          btnStop.setEnabled(false);
          btnStopDialog.setEnabled(false);
          configurationPanel.setVisible(true);
          setEnabledRecursively(configurationPanel, true);
          recordingStepPanel.setVisible(false);
          recorderDialog.setVisible(false);
        }
      }
    }
  }

  /**
   * Starts the ICA Download and Record background jobs.
   */
  private void downloadIcaAndRecord() {
    if (!CitrixRecorder.SKIP_ICA_FILE_DOWNLOADING) {
      clearStatus();
      appendStatus("Downloading ICA File");
    }
    if (recorder.isFromTemplate()) {
      updateUserDefinedValues();
    }
    new DownloadIca().execute();
  }

  private void updateUserDefinedValues() {
    HashMap<String, String> argumentsSet = new HashMap<>();

    try {
      URL url = new URI(this.storeFrontURL.getText()).toURL();

      argumentsSet.put(CITRIX_APP_NAME, application.getText());
      argumentsSet.put(CITRIX_PORTAL_SCHEME, url.getProtocol());
      argumentsSet.put(CITRIX_PORTAL_HOST, url.getHost());
      argumentsSet.put(CITRIX_PORTAL_PORT, String.valueOf(url.getPort()));
      argumentsSet.put(CITRIX_PORTAL_CONTEXT_PATH, url.getPath());
      argumentsSet.put(CITRIX_USE_HTTPS,
          url.getProtocol().equalsIgnoreCase("https") ? "Yes" : "No");

      argumentsSet.put(CITRIX_LOGIN, username.getText());
      argumentsSet.put(CITRIX_PASSWORD, String.valueOf(password.getPassword()));
      argumentsSet.put(CITRIX_DOMAIN, domain.getText());

    } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
      LOGGER.error("There was an error trying to parse the Storefront URL", e);
      stopRecording();
      return;
    }
    updateArguments((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot(),
        argumentsSet);
  }

  private void updateArguments(JMeterTreeNode root, HashMap<String, String> argumentsSet) {

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

  private HashMap<String, String> getUserDefinedValues() {

    List<String> propList = Arrays.asList(CITRIX_APP_NAME,
        CITRIX_PORTAL_SCHEME, CITRIX_PORTAL_HOST, CITRIX_PORTAL_PORT,
        CITRIX_PORTAL_CONTEXT_PATH, CITRIX_USE_HTTPS, CITRIX_LOGIN, CITRIX_PASSWORD,
        CITRIX_DOMAIN);

    return getArguments((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot(),
        propList);

  }

  private HashMap<String, String> getArguments(JMeterTreeNode root, List<String> propList) {
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

  private void startCapture(InteractionType interactionType) {
    recorder.startCapture(interactionType);
  }

  private void stopCapture(ClauseType clauseType) {
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
        DialogHelper.minimizeJMeter(this);
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

  private class StartRecording extends SwingWorker<Boolean, Object> {

    private final Optional<Path> icaPath;

    StartRecording(Optional<Path> path) {
      this.icaPath = path;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Boolean doInBackground() throws Exception {
      setWait(true);
      LOGGER.info("Started recording");
      // SwingWorker doesn't allow to define the Thread Name
      // WA, set the thread name inside the running thread
      // Required for set a correct session title and for session instance identification.
      // TODO: In the future this will be resolved when the concept of mode is implemented
      //  in the client.
      Thread.currentThread().setName("Citrix Recorder 1-1");

      recorder.startRecord(icaPath);
      new MonitorRecording().execute();

      return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
      setWait(false);
      try {
        Boolean result = get();
        if (Boolean.TRUE.equals(result)) {
          appendStatus("Recording started");
        } else {
          appendStatus(CitrixUtils.getResString(CLIENT_EXCEPTION, false));
          expectedDisconnect = true;
          toggleRecording(false, false);
        }
      } catch (InterruptedException | ExecutionException e) {
        Throwable ex = e.getCause();
        if (ex instanceof CitrixClientException) {
          appendStatus(
              "Recorder Error: " +
                  ((CitrixClientException) ex).code() + " " + ex.getMessage());
        } else {
          LOGGER.error("Error occurred starting citrix application {}", e.getMessage(), e);
          appendStatus(CitrixUtils.getResString(CLIENT_EXCEPTION, false));
        }
        expectedDisconnect = true;
        toggleRecording(false, false);
      }
    }
  }

  private void clearStatus() {
    logTextArea.setText("");
    recorderStatus.setString("");
  }

  private void appendStatus(String text) {
    logTextArea.append(text + "\n");
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    recorderStatus.setString(text);
    LOGGER.info(text);
  }

  private class MonitorRecording extends SwingWorker<Boolean, Object> {

    @Override
    protected Boolean doInBackground() throws Exception {
      long chunkWaitTime = 1000;
      // Polling over client waiting the finish state
      while (recorder.clientIsRunning() && !isCancelled()) {
        Thread.sleep(chunkWaitTime);
        LOGGER.info("[MonitorRecording] Monitoring...");
      }
      return true;
    }

    @Override
    protected void done() {
      try {
        get();
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("Error occurred recording citrix application {}", e.getMessage(), e);
        appendStatus(CitrixUtils.getResString(CLIENT_EXCEPTION, false));
      }
      if (recorder.isRecording()) {
        toggleRecording(false, false);
      }
    }
  }

  private class StopRecording extends SwingWorker<Boolean, Object> {

    @Override
    protected Boolean doInBackground() throws Exception {
      setWait(true);
      recorder.stopRecord();
      return true;
    }

    @Override
    protected void done() {
      setWait(false);
      try {
        get();
        toggleRecording(false, false);
      } catch (InterruptedException | ExecutionException e) {
        Throwable ex = e.getCause();
        if (ex instanceof CitrixClientException) {
          appendStatus(
              "Unable to stop Recorder: " +
                  ((CitrixClientException) ex).code() + " " + ex.getMessage());
        } else {
          LOGGER.error("Error occurred stopping citrix recorder {}", e.getMessage(), e);
          appendStatus(CitrixUtils.getResString(CLIENT_EXCEPTION, false));
          toggleRecording(false, false);
        }
      }
      DialogHelper.focusJMeter(logTextArea.getTopLevelAncestor());
    }
  }

  private class DownloadIca extends SwingWorker<Optional<Path>, Object> {

    @Override
    protected Optional<Path> doInBackground() throws Exception {
      setWait(true);
      return recorder.downloadIcaFile();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
      setWait(false);
      Optional<Path> path;
      try {
        path = get();
        boolean canRecord = false;
        if (path.isPresent()) {
          appendStatus("Downloaded ICA File to " + path.get().toString());
          appendStatus("ICA File downloaded");
          appendStatus("Launching application and recording");
          canRecord = true;
        } else if (CitrixRecorder.SKIP_ICA_FILE_DOWNLOADING) {
          canRecord = true;
        } else {
          appendStatus(CitrixUtils.getResString("recorder_ica_downloading_failed", false));
        }
        if (canRecord) {
          StartRecording startRecorder = new StartRecording(path);
          startRecorder.execute();
        } else {
          toggleRecording(false, false);
        }
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("ICA file downloading error : {}", e.getMessage(), e);
        appendStatus(CitrixUtils.getResString("recorder_ica_downloading_failed", false));
        toggleRecording(false, false);
      }
    }
  }
}
