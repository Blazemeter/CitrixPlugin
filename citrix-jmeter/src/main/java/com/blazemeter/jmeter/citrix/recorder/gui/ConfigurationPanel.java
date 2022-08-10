package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.TestPlanHelper;
import com.blazemeter.jmeter.commons.FieldValidations;
import com.blazemeter.jmeter.commons.FormValidation;
import com.blazemeter.jmeter.commons.PlaceHolderPassword;
import com.blazemeter.jmeter.commons.PlaceHolderTextField;
import com.blazemeter.jmeter.commons.ThemedIcon;
import com.blazemeter.jmeter.commons.Validation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.apache.jmeter.control.TestFragmentController;
import org.apache.jmeter.control.gui.TreeNodeWrapper;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationPanel extends BasePanel {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPanel.class);

  private static final String VISIBLE_CREDENTIALS_ICON = "visible-credentials.png";
  private static final String NOT_VISIBLE_CREDENTIAL_ICON = "not-visible-credentials.png";

  private static final TreeNodeWrapper SELECT_CUSTOM_FRAGMENT = new TreeNodeWrapper(null,
      "Select custom fragment");

  private static final String CITRIX_APP_NAME = "citrix_app_name";
  private static final String CITRIX_PORTAL_SCHEME = "citrix_portal_scheme";
  private static final String CITRIX_PORTAL_HOST = "citrix_portal_host";
  private static final String CITRIX_PORTAL_PORT = "citrix_portal_port";
  private static final String CITRIX_PORTAL_CONTEXT_PATH = "citrix_portal_context_path";
  private static final String CITRIX_USE_HTTPS = "citrix_use_https";
  private static final String CITRIX_LOGIN = "citrix_login";
  private static final String CITRIX_PASSWORD = "citrix_password";
  private static final String CITRIX_DOMAIN = "citrix_domain";

  private static final Validation NOT_EMPTY = new Validation(s -> !s.isEmpty(),
      "This field can't be empty");

  private static final Pattern URL_PATTERN = Pattern.compile(
      "^((((https?|ftps?)://)|(mailto:|news:))" +
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

  private PlaceHolderTextField username;
  private PlaceHolderPassword password;
  private PlaceHolderTextField domain;
  private PlaceHolderTextField application;

  private final JLabel userError = new JLabel();
  private final JLabel passError = new JLabel();
  private final JLabel storefrontUrlError = new JLabel();
  private final JLabel domainError = new JLabel();
  private final JLabel applicationError = new JLabel();

  private PlaceHolderTextField storeFrontURL;

  private DefaultComboBoxModel<TreeNodeWrapper> testFragmentSelectorModel;

  private final ImageIcon eyeIcon;
  private final ImageIcon eyeSlashIcon;

  private FormValidation formValidation;

  public ConfigurationPanel(CitrixRecorderGUI recorderGUI) {
    super(recorderGUI);

    eyeIcon = ThemedIcon.fromResourceName(VISIBLE_CREDENTIALS_ICON);
    eyeSlashIcon = ThemedIcon.fromResourceName(NOT_VISIBLE_CREDENTIAL_ICON);

    this.mainPanel = createIcaConfigurationPanel();

    addValidations();
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
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(domainLabel)
            .addComponent(userLabel)
            .addComponent(passLabel)
            .addComponent(applicationLabel))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(userLabel)
            .addComponent(username))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(userError)))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(passLabel)
            .addComponent(password)
            .addComponent(display))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(passError)))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(domainLabel)
            .addComponent(domain))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(domainError)))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(applicationLabel)
            .addComponent(application))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(applicationError)))
    );

    layout.linkSize(SwingConstants.HORIZONTAL, username, password, domain, application);
    layout.linkSize(SwingConstants.VERTICAL, username, password, domain, application, display);

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
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(storeFrontURL)
            .addComponent(storefrontUrlError)
        )
    );

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(urlLabel)
            .addComponent(storeFrontURL)
        )
        .addComponent(storefrontUrlError)
    );

    return panel;
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

  private JPanel createCustomFragmentSelector() {
    testFragmentSelectorModel = new DefaultComboBoxModel<>();
    /* component used to select the Test Fragment to download the ICA File*/
    JComboBox<TreeNodeWrapper> testFragmentSelector = new JComboBox<>(testFragmentSelectorModel);
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

  public void modifyDownloadingController() {
    if (!this.recorderGUI.isConfiguring() && !this.recorderGUI.isClearing()) {
      // Update test plan using "Downloading controller" components
      JMeterTreeNode selectedNode = null;
      TreeNodeWrapper selectedWrapper = (TreeNodeWrapper) testFragmentSelectorModel
          .getSelectedItem();
      if (selectedWrapper != null) {
        selectedNode = selectedWrapper.getTreeNode();
      }

      //If no TestFragment is selected, define the first appearance as a temporal alternative
      CitrixRecorder recorder = this.getRecorder();
      if (selectedNode == null) {
        recorder.setAltSamplersParentNode(
            TestPlanHelper.findFirstNodeOfType(TestFragmentController.class));
      }
      recorder.setDownloadingControllerNode(selectedNode);
    }
  }

  public void configureICADownloadingTestFragment() {
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

    JMeterTreeNode spNode = this.getRecorder().getDownloadingControllerNode();
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

  private PlaceHolderTextField buildPlaceHolder(String name, String placeholder) {
    PlaceHolderTextField textField = new PlaceHolderTextField();
    textField.setPlaceHolder(placeholder);
    textField.setName(name);
    return textField;
  }

  private void setupValidationLabel(JLabel label, String name) {
    label.setText("N/A");
    label.setName(name);
    label.setForeground(Color.RED);
    label.setVisible(false);
  }

  public void setFormValidation(boolean enable) {
    if (!this.getRecorder().isRecording()) {
      this.recorderGUI.setStartEnabled(enable);
    }
  }

  public void validate() {
    formValidation.validate();
  }

  public void hideConfigPanel() {
    storeFrontURL.getParent().setVisible(false);
    username.getParent().setVisible(false);
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

  public void loadConfigFromRecorder() {
    CitrixRecorder recorder = this.getRecorder();
    storeFrontURL.setText(recorder.getStorefrontURL());
    username.setText(recorder.getUsername());
    password.setText(recorder.getPassword());
    domain.setText(recorder.getDomain());
    application.setText(recorder.getApplicationName());
  }

  public void setConfigFromGUI() {
    CitrixRecorder recorder = this.getRecorder();
    recorder.setUsername(username.getText());
    recorder.setPassword(String.valueOf(password.getPassword()));
    recorder.setStorefrontURL(storeFrontURL.getText());
    recorder.setDomain(domain.getText());
    recorder.setApplicationName(application.getText());
  }

  public void setConfigFromUV() {
    Map<String, String> propSet = getUserDefinedValues();
    String portalPort = propSet.getOrDefault(CITRIX_PORTAL_PORT, "");
    String url = propSet.getOrDefault(CITRIX_PORTAL_SCHEME, "") + "://" +
        propSet.getOrDefault(CITRIX_PORTAL_HOST, "") +
        ("".equals(portalPort) || "-1".equals(portalPort) ? "" : (":" + portalPort)) +
        propSet.getOrDefault(CITRIX_PORTAL_CONTEXT_PATH, "");
    CitrixRecorder recorder = this.getRecorder();
    if (!"://".equals(url)) {
      recorder.setStorefrontURL(url);
    }
    recorder.setUsername(propSet.getOrDefault(CITRIX_LOGIN, ""));
    recorder.setPassword(propSet.getOrDefault(CITRIX_PASSWORD, ""));
    recorder.setDomain(propSet.getOrDefault(CITRIX_DOMAIN, ""));
    recorder.setApplicationName(propSet.getOrDefault(CITRIX_APP_NAME, ""));
  }

  public void setFormValidationActive(boolean enable) {
    formValidation.setActive(enable);
  }

  public void updateUserDefinedValues() {
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
      this.recorderGUI.stopRecording();
      return;
    }
    GuiPackage gp = GuiPackage.getInstance();
    if (gp != null) {
      TestPlanHelper
          .updateArguments((JMeterTreeNode) gp.getTreeModel().getRoot(),
              argumentsSet);
    }
  }

  public Map<String, String> getUserDefinedValues() {

    List<String> propList = Arrays.asList(CITRIX_APP_NAME,
        CITRIX_PORTAL_SCHEME, CITRIX_PORTAL_HOST, CITRIX_PORTAL_PORT,
        CITRIX_PORTAL_CONTEXT_PATH, CITRIX_USE_HTTPS, CITRIX_LOGIN, CITRIX_PASSWORD,
        CITRIX_DOMAIN);

    return TestPlanHelper
        .getArguments((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot(),
            propList);

  }

}
