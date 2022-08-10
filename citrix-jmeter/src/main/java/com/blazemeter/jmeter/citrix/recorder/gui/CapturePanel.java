package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.recorder.Capture;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.commons.ThemedIcon;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CapturePanel extends BasePanel {
  public static final String CMD_START_TEXT_CAPTURE = "TOGGLE_TEXT_CAPTURE";
  public static final String CMD_START_MOUSE_CAPTURE = "TOGGLE_MOUSE_CAPTURE";

  public static final String CMD_CHECK_FULL_SCREENSHOT = "CHECK_FULL_SCREENSHOT";
  public static final String CMD_CHECK_SELECTION_SCREENSHOT = "CHECK_SELECTION_SCREENSHOT";
  public static final String CMD_CHECK_OCR_SCREENSHOT = "CHECK_OCR_SCREENSHOT";
  public static final String CMD_DISCARD_ACTION = "ADD_ACTION";
  public static final String CMD_ADD_ACTION = "DISCARD_ACTION";

  public static final String RESOURCE_KEY_START_TEXT_CAPTURE = "recorder_record_text_input";
  public static final String RESOURCE_KEY_START_MOUSE_CAPTURE = "recorder_record_mouse_click";

  private final JPanel actionCapturePanel;
  private final JPanel finalActionCapturePanel;

  private JButton btnAddAction;
  private JButton btnFullScreenshotCheck;
  private JButton btnPartialScreenshotCheck;
  private JButton btnOCRScreenshotCheck;

  private final ImageIcon addIcon;
  private final ImageIcon selectionIcon;
  private final ImageIcon fullScreenIcon;
  private final ImageIcon ocrTextIcon;
  private final ImageIcon discardIcon;

  private JCheckBox includeMouseMoves;
  private JCheckBox relativeForeground;

  public CapturePanel(CitrixRecorderGUI recorderGUI) {
    super(recorderGUI);

    addIcon = ThemedIcon.fromResourceName("add_action.png");
    selectionIcon = ThemedIcon.fromResourceName("selection_screenshot.png");
    fullScreenIcon = ThemedIcon.fromResourceName("full_screenshot.png");
    ocrTextIcon = ThemedIcon.fromResourceName("text_ocr.png");
    discardIcon = ThemedIcon.fromResourceName("discard.png");

    this.mainPanel = new JPanel(new BorderLayout());
    this.mainPanel.setVisible(false);

    actionCapturePanel = createStepCapturePanel();
    this.mainPanel.add(actionCapturePanel, BorderLayout.NORTH);

    finalActionCapturePanel = createFinalStepCapturePanel();
    finalActionCapturePanel.setVisible(false);
    this.mainPanel.add(finalActionCapturePanel, BorderLayout.CENTER);

  }

  private JPanel createStepCapturePanel() {

    JPanel panel = new JPanel();

    PageViewer layout = new PageViewer();
    panel.setLayout(layout);

    // Text used to capture keyboard interactions
    ActionItem actionCaptureText = new ActionItem(
        CMD_START_TEXT_CAPTURE,
        CitrixUtils.getText(RESOURCE_KEY_START_TEXT_CAPTURE));
    // Text used to capture mouse interactions
    ActionItem actionCaptureMouse = new ActionItem(
        CMD_START_MOUSE_CAPTURE,
        CitrixUtils.getText(RESOURCE_KEY_START_MOUSE_CAPTURE)
    );
    JPanel mouseCaptureOptionsPanel = createMouseCaptureOptions();

    panel.add(actionCaptureText.getCommand(), new JPanel());
    panel.add(actionCaptureMouse.getCommand(), mouseCaptureOptionsPanel);

    final DefaultComboBoxModel<ActionItem> panelActionName = new DefaultComboBoxModel<>();

    panelActionName.addElement(actionCaptureText);
    panelActionName.addElement(actionCaptureMouse);
    final JComboBox<ActionItem> actionList = new JComboBox<>(panelActionName);
    actionList.setName("action");
    actionList.setRenderer(new ActionItemRenderer());
    actionList.setSelectedIndex(0);

    // Activate the card panel based on the selection
    actionList.addItemListener(e -> {
      if (actionList.getSelectedIndex() != -1) {
        CardLayout cardLayout = (CardLayout) (panel.getLayout());
        cardLayout.show(panel,
            (actionList.getItemAt(actionList.getSelectedIndex())).getCommand());
        this.recorderGUI.getRecorderDialog().pack();
      }
    });

    JButton recordActionButton = new JButton("Record Action");
    recordActionButton.setName("record_action");

    recordActionButton.addActionListener(e -> {
      int uniqueId = (int) System.currentTimeMillis();
      String commandName =
          ((ActionItem) Objects.requireNonNull(actionList.getSelectedItem())).getCommand();
      ActionEvent event = new ActionEvent(this, uniqueId, commandName);
      this.recorderGUI.actionPerformed(event);
    });

    JPanel actionPanel = new JPanel(new GridLayout(1, 2));

    actionPanel.add(actionList);
    actionPanel.add(recordActionButton);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

    controlPanel.add(actionPanel, BorderLayout.NORTH);

    controlPanel.add(panel, BorderLayout.CENTER);

    return controlPanel;
  }

  public JPanel createFinalStepCapturePanel() {

    // Button used to stop capture and build Full Screenshot Hash assertion
    btnAddAction =
        baseButtonBuilder.withIcon(addIcon).withAction(CMD_ADD_ACTION)
            .withName(CitrixUtils.getText("recorder_add_action_id")).build();
    btnAddAction.setToolTipText(
        CitrixUtils.getText("recorder_add_action"));
    btnAddAction.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnAddAction.setHorizontalTextPosition(SwingConstants.CENTER);

    // Button used to stop capture and build Full Screenshot Hash assertion
    btnFullScreenshotCheck =
        baseButtonBuilder.withIcon(fullScreenIcon).withAction(CMD_CHECK_FULL_SCREENSHOT)
            .withName(CitrixUtils.getText("recorder_full_screenshot_check_id")).build();
    btnFullScreenshotCheck.setToolTipText(
        CitrixUtils.getText("recorder_full_screenshot_check"));
    btnFullScreenshotCheck.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnFullScreenshotCheck.setHorizontalTextPosition(SwingConstants.CENTER);

    // Button used to stop capture and build Partial Screenshot Hash assertion
    btnPartialScreenshotCheck =
        baseButtonBuilder.withIcon(selectionIcon).withAction(CMD_CHECK_SELECTION_SCREENSHOT)
            .withName(CitrixUtils.getText("recorder_selection_screenshot_check_id")).build();
    btnPartialScreenshotCheck.setToolTipText(
        CitrixUtils.getText("recorder_selection_screenshot_check"));
    btnPartialScreenshotCheck.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnPartialScreenshotCheck.setHorizontalTextPosition(SwingConstants.CENTER);

    // Button used to stop capture and build Partial of Full Screenshot OCR assertion
    btnOCRScreenshotCheck =
        baseButtonBuilder.withIcon(ocrTextIcon).withAction(CMD_CHECK_OCR_SCREENSHOT)
            .withName(CitrixUtils.getText("recorder_ocr_screenshot_check_id")).build();
    btnOCRScreenshotCheck.setToolTipText(
        CitrixUtils.getText("recorder_ocr_screenshot_check"));
    btnOCRScreenshotCheck.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnOCRScreenshotCheck.setHorizontalTextPosition(SwingConstants.CENTER);

    // Button used to stop capture and build Partial of Full Screenshot OCR assertion
    JButton btnDiscardAction =
        baseButtonBuilder.withIcon(discardIcon).withAction(CMD_DISCARD_ACTION)
            .withName(CitrixUtils.getText("recorder_discard_action_id")).build();
    btnDiscardAction.setToolTipText(
        CitrixUtils.getText("recorder_discard_action"));
    btnDiscardAction.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnDiscardAction.setHorizontalTextPosition(SwingConstants.CENTER);

    JPanel panel = new JPanel(new GridLayout(1, 5));
    panel.add(btnAddAction);
    panel.add(btnOCRScreenshotCheck);
    panel.add(btnPartialScreenshotCheck);
    panel.add(btnFullScreenshotCheck);
    panel.add(btnDiscardAction);
    panel.setName("final_action");
    return panel;
  }

  private JPanel createMouseCaptureOptions() {
    // Panel with the Mouse Capture options
    JPanel captureMouseOptions = new JPanel();
    captureMouseOptions
        .setBorder(BorderFactory.createTitledBorder(
            CitrixUtils.getText("recorder_mouse_capture_options")));

    // Checkbox used to include/exclude mouse moves during capture
    includeMouseMoves =
        new JCheckBox(CitrixUtils.getText("recorder_mouse_capture_options_include_mouse_moves"));
    includeMouseMoves.setName("mouse_include_mouse_moves");
    includeMouseMoves.addActionListener(e -> updateMouseCaptureOptions(includeMouseMoves,
        Capture.MouseCaptureOption.INCLUDE_MOVES));

    // Checkbox used to include/exclude mouse moves during capture
    relativeForeground = new JCheckBox(
        CitrixUtils.getText("recorder_mouse_capture_options_relative"));
    relativeForeground.setName("mouse_relative_foreground");
    relativeForeground.addActionListener(e -> updateMouseCaptureOptions(relativeForeground,
        Capture.MouseCaptureOption.RELATIVE_TO_FOREGROUND));

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

  private void updateMouseCaptureOptions(JCheckBox includeCheckbox,
                                         Capture.MouseCaptureOption option) {
    Set<Capture.MouseCaptureOption> options =
        this.recorderGUI.getRecorder().getMouseCaptureOptions();
    if (includeCheckbox.isSelected()) {
      options.add(option);
    } else {
      options.remove(option);
    }
  }

  public void enableActions(boolean enable) {
    btnAddAction.setEnabled(enable);
    btnFullScreenshotCheck.setEnabled(enable);
    btnPartialScreenshotCheck.setEnabled(enable);
    btnOCRScreenshotCheck.setEnabled(enable);
  }

  public void setRelativeForeground(boolean selected) {
    relativeForeground.setSelected(selected);
  }

  public void setIncludeMouseMoves(boolean selected) {
    includeMouseMoves.setSelected(selected);
  }

  public void startCapturing(boolean capturing) {
    actionCapturePanel.setVisible(!capturing);
    finalActionCapturePanel.setVisible(capturing);
  }
}
