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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.control.gui.TreeNodeWrapper;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.UnsharedComponent;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.JMeterToolBar;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.protocol.http.control.RecordingController;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
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
import com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo;

/**
 * GUI for CitrixRecorder class.
 */
public class CitrixRecorderGUI extends AbstractControllerGui // NOSONAR Ignore inheritance warning
		implements ActionListener, CitrixRecorderHandler, UnsharedComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(CitrixRecorderGUI.class);

	private static final long serialVersionUID = 1L;

	private static final String ICONSIZE = JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE,
			JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE);
	private static final ImageIcon START_IMAGE = JMeterUtils.getImage("toolbar/" + ICONSIZE + "/arrow-right-3.png");
	private static final ImageIcon STOP_IMAGE = JMeterUtils.getImage("toolbar/" + ICONSIZE + "/process-stop-4.png");

	private static final String CMD_TOGGLE_RECORDING = "TOGGLE_RECORDING";
	private static final String CMD_TOGGLE_TEXT_CAPTURE = "TOGGLE_TEXT_CAPTURE";
	private static final String CMD_TOGGLE_MOUSE_CAPTURE = "TOGGLE_MOUSE_CAPTURE";
	public static final String CMD_CHECK_FULL_SCREENSHOT = "CHECK_FULL_SCREENSHOT";
	public static final String CMD_CHECK_SELECTION_SCREENSHOT = "CHECK_SELECTION_SCREENSHOT";
	public static final String CMD_CHECK_OCR_SCREENSHOT = "CHECK_OCR_SCREENSHOT";
	private static final String CMD_START_APPLICATION = "START_APPLICATION";

	private static final String RESOURCE_KEY_START_TEXT_CAPTURE = "recorder_record_text_input";
	private static final String RESOURCE_KEY_START_MOUSE_CAPTURE = "recorder_record_mouse_click";

	/* Represents an empty path when using TreeNodes array */
	private static final TreeNode[] EMPTY_PATH = new TreeNode[0];

	private static final TreeNodeWrapper USE_RECORDING_CONTROLLER_NODE = new TreeNodeWrapper(null,
			CitrixUtils.getResString("use_recording_controller", true));

	static {
		try {
			CitrixInstaller.main(new String[0]);
		} catch (IOException e) {
			LOGGER.error("Error running installer", e);
		}
	}
	private CitrixRecorder recorder;
	private boolean stoppingRecord = false;
	private boolean waitForDisconnect = false;
	private boolean configuring = false;
	private boolean clearing = false;

	// Recording actions buttons
	private JToggleButton btnToggleRecording;
	private JToggleButton btnCaptureText;
	private JToggleButton btnCaptureMouse;
	private JButton btnFullScreenshotCheck;
	private JButton btnPartialScreenshotCheck;
	private JButton btnOCRScreenshotCheck;
	private JButton btnAppStarted;

	private JPanel pnlMouseCaptureOptions;
	private JCheckBox chbIncludeMouseMoves;
	private JCheckBox chbRelative;

	private JTextField tfStepName;
	private JLabel lblFlowFirst;
	private JLabel lblFlowThen;
	
	/* components used for ICA file downloading */
	private JTree treeDownloadingControllers;
	private DefaultTreeModel downloadingControllersTreeModel;

	// components used for sampler insertion in test plan
	private JComboBox<TreeNodeWrapper> cbbSamplerParent; // List of available target controllers
	private DefaultComboBoxModel<TreeNodeWrapper> samplerParentModel; // Model of targetNodes

	private JSyntaxTextArea icaDownloadStatus;

	private JProgressBar progressBar;

	private InteractionType capturedInteractionType;

	public CitrixRecorderGUI() {
		super();
		init();
		setupTemplate();
	}

	private void setupTemplate() {
		TemplateUpdater templateUpdater = new TemplateUpdater(new File(JMeterUtils.getJMeterBinDir(), "templates"));
		try {
		    if(TemplateUpdater.hasParameterizedTemplate()) {
                templateUpdater.addTemplate("BlazeMeter Citrix Recording", "citrixRecordingTemplateWithParameters.jmx",
                        "bzmCitrixTemplateWithParameters.xml", "/com/blazemeter/jmeter/citrix/template");
		    } else {
                templateUpdater.addTemplate("BlazeMeter Citrix Recording", "citrixRecordingTemplate.jmx",
                        "bzmCitrixTemplate.xml", "/com/blazemeter/jmeter/citrix/template");
		    }
		} catch (IOException ex) {
			LOGGER.error("Error setting up templates using JMeter home: {}", JMeterUtils.getJMeterHome(), ex);
		}
	}

	private void init() {
	    BlazeMeterLabsLogo blazeMeterLabsLogo = new BlazeMeterLabsLogo();
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		JPanel pnlIcaDownloading = createDownloadPanel();

		JSplitPane spContext = new JSplitPane();
		spContext.setAlignmentX(Component.CENTER_ALIGNMENT);
		spContext.setResizeWeight(0.7d);
		spContext.setOneTouchExpandable(true);
		spContext.setLeftComponent(pnlIcaDownloading);
		spContext.setRightComponent(createDownloadStatusIndicatorPanel());

		Box box = new Box(BoxLayout.Y_AXIS) {
			private static final long serialVersionUID = 1509833938600152961L;
			private boolean painted;

			@Override
			public void paint(Graphics g) {
				super.paint(g);

				if (!painted) {
					painted = true;
					spContext.setDividerLocation(0.5d);
				}
			}
		};
		box.add(makeTitlePanel());
		box.add(spContext);
		box.add(createContentPanel());
		box.add(blazeMeterLabsLogo);

		add(box, BorderLayout.NORTH);
	}

	private Component createDownloadStatusIndicatorPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(CitrixUtils.getResString("recorder_status", false)));
		icaDownloadStatus = JSyntaxTextArea.getInstance(10, 30, true);
		icaDownloadStatus.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		icaDownloadStatus.setCodeFoldingEnabled(false);
		icaDownloadStatus.setAntiAliasingEnabled(false);
		icaDownloadStatus.setEditable(false);
		icaDownloadStatus.setLineWrap(true);
		icaDownloadStatus.setLanguage("text");
		icaDownloadStatus.setWrapStyleWord(true);
		progressBar = new JProgressBar(0, 2);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		panel.add(JTextScrollPane.getInstance(icaDownloadStatus), BorderLayout.CENTER);
		panel.add(progressBar, BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Creates the "Downloading Controller" pane and its components
	 * 
	 * @return the "Downloading Controller" pane
	 */
	private JPanel createDownloadPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory
				.createTitledBorder(CitrixUtils.getResString("recorder_ica_downloading_controller", false)));
		panel.setLayout(new BorderLayout());

		// Limit the selection to only valid controllers
		DownloadingControllerTreeSelectionModel treeSelectionModel = new DownloadingControllerTreeSelectionModel();
		treeSelectionModel.setSelectionMode(DownloadingControllerTreeSelectionModel.SINGLE_TREE_SELECTION);

		downloadingControllersTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		treeDownloadingControllers = new JTree(downloadingControllersTreeModel);
		treeDownloadingControllers.setCellRenderer(new DownloadingControllerCellRenderer());
		treeDownloadingControllers.setSelectionModel(treeSelectionModel);
		treeDownloadingControllers.setVisibleRowCount(5);
		treeDownloadingControllers.addTreeSelectionListener(e -> modifyDownloadingController());

		JScrollPane scrollPane = new JScrollPane(treeDownloadingControllers);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	public static void initConstraints(GridBagConstraints gbc) {
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
	}

	private static String getFlowFirstLabel(boolean recording) {
		return CitrixUtils.getResString(
				recording ? "recorder_init_recording_flow_first_enabled" : "recorder_init_recording_flow_first_disabled", false);
	}
	
	private static String getFlowThenLabel(boolean recording) {
		return CitrixUtils.getResString(
				recording ? "recorder_init_recording_flow_then_enabled" : "recorder_init_recording_flow_then_disabled", false);
	}
	
	private JPanel createInitRecordingPanel() {
		JPanel pnlInit = new JPanel();
		pnlInit.setBorder(
				BorderFactory.createTitledBorder(CitrixUtils.getResString("recorder_init_recording_title", false)));

		// Button used to start/stop recording
		btnToggleRecording = new JToggleButton(getStartRecordingText());
		btnToggleRecording.setIcon(START_IMAGE);
		btnToggleRecording.setActionCommand(CMD_TOGGLE_RECORDING);
		btnToggleRecording.addActionListener(this);

		// Button used to signal the remote application is started
		btnAppStarted = new JButton(CitrixUtils.getResString("recorder_application_started", false));
		btnAppStarted.setEnabled(false);
		btnAppStarted.setActionCommand(CMD_START_APPLICATION);
		btnAppStarted.addActionListener(this);

		lblFlowFirst = new JLabel(getFlowFirstLabel(false));
		lblFlowThen = new JLabel(getFlowThenLabel(false));

		// Layout
		GroupLayout pnlInitLayout = new GroupLayout(pnlInit);
		pnlInit.setLayout(pnlInitLayout);
		pnlInitLayout.setHorizontalGroup(pnlInitLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlInitLayout.createSequentialGroup().addContainerGap().addComponent(lblFlowFirst)
						.addGap(20, 20, 20).addComponent(btnToggleRecording).addGap(18, 18, 18).addComponent(lblFlowThen)
						.addGap(18, 18, 18).addComponent(btnAppStarted)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		pnlInitLayout.setVerticalGroup(pnlInitLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlInitLayout.createSequentialGroup().addContainerGap()
						.addGroup(pnlInitLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(btnToggleRecording).addComponent(btnAppStarted).addComponent(lblFlowFirst)
								.addComponent(lblFlowThen))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		return pnlInit;
	}

	private JPanel createActionRecordingPanel() {
		JPanel pnlAction = new JPanel();
		pnlAction.setBorder(BorderFactory.createTitledBorder(CitrixUtils.getResString("recorder_action_title", false)));

		JLabel lblSamplerParent = new JLabel(CitrixUtils.getResString("proxy_target", true));
		lblSamplerParent.setLabelFor(cbbSamplerParent);
		samplerParentModel = new DefaultComboBoxModel<>();
		cbbSamplerParent = new JComboBox<>(samplerParentModel);
		cbbSamplerParent.setPrototypeDisplayValue(USE_RECORDING_CONTROLLER_NODE);
		cbbSamplerParent.addItemListener(e -> modifySamplersParent());

		JLabel lblStepName = new JLabel(CitrixUtils.getResString("recorder_step_name", false));
		tfStepName = new JTextField();
		tfStepName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				recorder.setStepName(tfStepName.getText());
			}
		});

		JLabel lblStartCapture = new JLabel(CitrixUtils.getResString("recorder_action_start_capture", false));
		JLabel lblStopCapture = new JLabel(CitrixUtils.getResString("recorder_action_stop_capture", false));

		// Button used to stop capture and build Full Screenshot Hash assertion
		btnFullScreenshotCheck = new JButton(CitrixUtils.getResString("recorder_full_screenshot_check", false));
		btnFullScreenshotCheck.setEnabled(false);
		btnFullScreenshotCheck.setActionCommand(CMD_CHECK_FULL_SCREENSHOT);
		btnFullScreenshotCheck.addActionListener(this);

		// Button used to stop capture and build Partial Screenshot Hash assertion
		btnPartialScreenshotCheck = new JButton(CitrixUtils.getResString("recorder_selection_screenshot_check", false));
		btnPartialScreenshotCheck.setEnabled(false);
		btnPartialScreenshotCheck.setActionCommand(CMD_CHECK_SELECTION_SCREENSHOT);
		btnPartialScreenshotCheck.addActionListener(this);

		// Button used to stop capture and build Partial of Full Screenshot OCR
		// assertion
		btnOCRScreenshotCheck = new JButton(CitrixUtils.getResString("recorder_ocr_screenshot_check", false));
		btnOCRScreenshotCheck.setEnabled(false);
		btnOCRScreenshotCheck.setActionCommand(CMD_CHECK_OCR_SCREENSHOT);
		btnOCRScreenshotCheck.addActionListener(this);

		// Button used to capture keyboard interactions
		btnCaptureText = new JToggleButton(CitrixUtils.getResString(RESOURCE_KEY_START_TEXT_CAPTURE, false));
		btnCaptureText.setEnabled(false);
		btnCaptureText.setActionCommand(CMD_TOGGLE_TEXT_CAPTURE);
		btnCaptureText.addActionListener(this);

		// Button used to capture mouse interactions
		btnCaptureMouse = new JToggleButton(CitrixUtils.getResString(RESOURCE_KEY_START_MOUSE_CAPTURE, false));
		btnCaptureMouse.setEnabled(false);
		btnCaptureMouse.setActionCommand(CMD_TOGGLE_MOUSE_CAPTURE);
		btnCaptureMouse.addActionListener(this);

		JLabel jLabel6 = new JLabel(CitrixUtils.getResString("recorder_capture_or", false));

		pnlMouseCaptureOptions = new JPanel();
		pnlMouseCaptureOptions.setBorder(
				BorderFactory.createTitledBorder(CitrixUtils.getResString("recorder_mouse_capture_options", false)));
		pnlMouseCaptureOptions.setEnabled(false);

		// Checkbox used to include/exclude mouse moves during capture
		chbIncludeMouseMoves = new JCheckBox(
				CitrixUtils.getResString("recorder_mouse_capture_options_include_mouse_moves", false));
		chbIncludeMouseMoves.setEnabled(false);
		chbIncludeMouseMoves.addActionListener(e -> {
			if (chbIncludeMouseMoves.isSelected()) {
				recorder.getMouseCaptureOptions().add(MouseCaptureOption.INCLUDE_MOVES);
			} else {
				recorder.getMouseCaptureOptions().remove(MouseCaptureOption.INCLUDE_MOVES);
			}
		});

		// Checkbox used to include/exclude mouse moves during capture
		chbRelative = new JCheckBox(CitrixUtils.getResString("recorder_mouse_capture_options_relative", false));
		chbRelative.setEnabled(false);
		chbRelative.addActionListener(e -> {
			if (chbRelative.isSelected()) {
				recorder.getMouseCaptureOptions().add(MouseCaptureOption.RELATIVE_TO_FOREGROUND);
			} else {
				recorder.getMouseCaptureOptions().remove(MouseCaptureOption.RELATIVE_TO_FOREGROUND);
			}
		});

		JSeparator jSeparator1 = new JSeparator();
		JSeparator jSeparator3 = new JSeparator();

		GroupLayout pnlMouseCaptureOptionsLayout = new GroupLayout(pnlMouseCaptureOptions);
		pnlMouseCaptureOptions.setLayout(pnlMouseCaptureOptionsLayout);
		pnlMouseCaptureOptionsLayout.setHorizontalGroup(pnlMouseCaptureOptionsLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlMouseCaptureOptionsLayout.createSequentialGroup().addContainerGap()
						.addGroup(pnlMouseCaptureOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chbIncludeMouseMoves).addComponent(chbRelative))
						.addContainerGap(129, Short.MAX_VALUE)));
		pnlMouseCaptureOptionsLayout.setVerticalGroup(pnlMouseCaptureOptionsLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlMouseCaptureOptionsLayout.createSequentialGroup().addContainerGap()
						.addComponent(chbIncludeMouseMoves).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(chbRelative)));

		GroupLayout pnlActionLayout = new GroupLayout(pnlAction);
		pnlAction.setLayout(pnlActionLayout);

		pnlActionLayout.setHorizontalGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				pnlActionLayout.createSequentialGroup().addContainerGap().addGroup(pnlActionLayout
						.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jSeparator3)
						.addComponent(jSeparator1, GroupLayout.Alignment.LEADING)
						.addGroup(GroupLayout.Alignment.LEADING, pnlActionLayout.createSequentialGroup()
								.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(pnlActionLayout.createSequentialGroup().addComponent(lblStopCapture)
												.addGap(43, 43, 43).addComponent(btnFullScreenshotCheck)
												.addGap(18, 18, 18).addComponent(btnPartialScreenshotCheck)
												.addGap(18, 18, 18).addComponent(btnOCRScreenshotCheck))
										.addGroup(pnlActionLayout.createSequentialGroup().addComponent(lblStartCapture)
												.addGap(18, 18, 18)
												.addComponent(btnCaptureText, GroupLayout.PREFERRED_SIZE, 166,
														GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18).addComponent(jLabel6)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(pnlActionLayout
														.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(pnlMouseCaptureOptions,
																GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(btnCaptureMouse, GroupLayout.PREFERRED_SIZE, 188,
																GroupLayout.PREFERRED_SIZE))))
								.addGap(0, 52, Short.MAX_VALUE))
						.addGroup(GroupLayout.Alignment.LEADING,
								pnlActionLayout.createSequentialGroup().addComponent(lblSamplerParent)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(cbbSamplerParent, GroupLayout.PREFERRED_SIZE, 651,
												GroupLayout.PREFERRED_SIZE))
						.addGroup(GroupLayout.Alignment.LEADING,
								pnlActionLayout.createSequentialGroup().addComponent(lblStepName)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tfStepName, GroupLayout.PREFERRED_SIZE, 651,
												GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));

		pnlActionLayout.setVerticalGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlActionLayout.createSequentialGroup().addContainerGap()
						.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblStepName).addComponent(tfStepName, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblSamplerParent).addComponent(cbbSamplerParent,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lblStartCapture)
								.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(pnlActionLayout.createSequentialGroup().addComponent(btnCaptureText)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(pnlMouseCaptureOptions, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(GroupLayout.Alignment.CENTER,
												pnlActionLayout.createSequentialGroup()
														.addGroup(pnlActionLayout
																.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(btnCaptureMouse).addComponent(jLabel6))
														.addGap(92, 92, 92))))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(pnlActionLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(btnFullScreenshotCheck).addComponent(btnPartialScreenshotCheck)
								.addComponent(btnOCRScreenshotCheck).addComponent(lblStopCapture))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		return pnlAction;
	}

	private JPanel createContentPanel() {
		JPanel contentPanel = new JPanel();

		JPanel pnlInit = createInitRecordingPanel();
		JPanel pnlAction = createActionRecordingPanel();

		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(pnlInit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(pnlAction, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(pnlInit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(pnlAction,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		return contentPanel;
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

	@Override
	public void clearGui() {
		super.clearGui();
		clearing = true;
		tfStepName.setText("");
		// Clear the "Downloading Controller" part
		treeDownloadingControllers.clearSelection();
		// Reset Samplers parent selection
		cbbSamplerParent.setSelectedItem(USE_RECORDING_CONTROLLER_NODE);
		clearing = false;
	}

	/**
	 * Builds recursively a tree node using the specified model node
	 * 
	 * @param modelNode the model node
	 * @param parent    the current node used to attach new nodes
	 */
	private void buildICADownloadingTreeModel(JMeterTreeNode modelNode, int level, DefaultMutableTreeNode parent) {
		if (modelNode != null) {
			for (int i = 0; i < modelNode.getChildCount(); i++) {
				JMeterTreeNode child = (JMeterTreeNode) modelNode.getChildAt(i);
				TestElement testElement = child.getTestElement();
				if (TestPlanHelper.isAllowedDownloadingControllerElement(testElement, level)) {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(child);
					parent.add(newNode);
					buildICADownloadingTreeModel(child, level + 1, newNode);
				}
				// Skip first "step plan" node
				else if (testElement instanceof TestPlan) {
					DefaultMutableTreeNode modelRoot = (DefaultMutableTreeNode) downloadingControllersTreeModel
							.getRoot();
					modelRoot.setUserObject(child);
					buildICADownloadingTreeModel(child, level, modelRoot);
				}
			}
		}
	}

	/**
	 * Builds recursively a path of tree nodes matching the specified path from the
	 * test plan
	 * 
	 * @param parent       the node where the lookup is done
	 * @param testPlanPath the path based on test plan
	 * @param level        the current level of the path
	 * @return the path to descendant of "parent" node matching the path from the
	 *         test plan
	 */
	private TreeNode[] buildMatchingTestPlanPath(DefaultMutableTreeNode parent, TreeNode[] testPlanPath, int level) {
		TreeNode[] result = EMPTY_PATH;
		if (level < testPlanPath.length) {
			int childCount = parent.getChildCount();
			JMeterTreeNode searchedTreeNode = (JMeterTreeNode) testPlanPath[level];

			for (int i = 0; i < childCount; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
				JMeterTreeNode childUserObj = (JMeterTreeNode) child.getUserObject();

				if (childUserObj.equals(searchedTreeNode)) {
					if (level == (testPlanPath.length - 1)) {
						return child.getPath();
					} else {
						return buildMatchingTestPlanPath(child, testPlanPath, level + 1);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Initialises "Downloading Controller" components
	 */
	private void configureICADownloading() {
		((DefaultMutableTreeNode) downloadingControllersTreeModel.getRoot()).removeAllChildren();

		GuiPackage gp = GuiPackage.getInstance();
		JMeterTreeNode testPlanRoot;
		if (gp != null) {
			testPlanRoot = (JMeterTreeNode) gp.getTreeModel().getRoot();
			DefaultMutableTreeNode modelRoot = (DefaultMutableTreeNode) downloadingControllersTreeModel.getRoot();
			buildICADownloadingTreeModel(testPlanRoot, 0, modelRoot);
			downloadingControllersTreeModel.nodeStructureChanged(modelRoot);
		}

		// Auto focus node if it exists
		JMeterTreeNode dcNode = recorder.getDownloadingControllerNode();
		if (dcNode != null) {
			TreeNode[] selectedPath = dcNode.getPath();
			TreeNode[] filteredPath = new TreeNode[selectedPath.length - 1];

			System.arraycopy(selectedPath, 1, filteredPath, 0, selectedPath.length - 1);

			DefaultMutableTreeNode downloadingRoot = (DefaultMutableTreeNode) downloadingControllersTreeModel.getRoot();
			TreeNode[] nodes = buildMatchingTestPlanPath(downloadingRoot, filteredPath, 1);
			if (nodes.length > 0) {
				TreePath treePath = new TreePath(nodes);
				treeDownloadingControllers.setSelectionPath(treePath);
				treeDownloadingControllers.scrollPathToVisible(treePath);
			}
		}
	}

	/**
	 * Remove and re add the possibles target choices in the targetNodes comboBox
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

	@Override
	public void configure(TestElement el) {
		super.configure(el);
		configuring = true;
		recorder = (CitrixRecorder) el;
		tfStepName.setText(recorder.getStepName());

		configureSamplersParent();
		configureICADownloading();

		Set<MouseCaptureOption> options = recorder.getMouseCaptureOptions();
		chbRelative.setSelected(options.contains(MouseCaptureOption.RELATIVE_TO_FOREGROUND));
		chbIncludeMouseMoves.setSelected(options.contains(MouseCaptureOption.INCLUDE_MOVES));
		configuring = false;
	}

	private void modifyDownloadingController() {
		if (!configuring && !clearing) {
			// Update test plan using "Downloading controller" components
			JMeterTreeNode downloadingSelectedNode = null;
			DefaultMutableTreeNode lastSelected = (DefaultMutableTreeNode) treeDownloadingControllers
					.getLastSelectedPathComponent();
			if (lastSelected != null) {
				Object lastSelectedObject = lastSelected.getUserObject();
				if (lastSelectedObject instanceof JMeterTreeNode) {
					downloadingSelectedNode = (JMeterTreeNode) lastSelectedObject;
				}
			}
			if (downloadingSelectedNode == null
					|| TestPlanHelper.isValidDownloadingController(downloadingSelectedNode.getTestElement())) {
				recorder.setDownloadingControllerNode(downloadingSelectedNode);
			}
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
				recorder.setAltSamplersParentNode(TestPlanHelper.findFirstNodeOfType(RecordingController.class));
			}
			recorder.setSamplersParentNode(samplersParentNode);
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

	private String getStartRecordingText() {
		return CitrixUtils.getResString("recorder_start", false);
	}

	private String getStopRecordingText() {
		return CitrixUtils.getResString("recorder_stop", false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		// Handle the start button
		if (CMD_TOGGLE_RECORDING.equals(actionCommand)) {
			boolean recording = btnToggleRecording.isSelected();
			btnToggleRecording.setSelected(false);
			toggleRecording(recording, true);
		}
		// handle the start application button
		else if (CMD_START_APPLICATION.equals(actionCommand)) {
			btnAppStarted.setSelected(false);
			recorder.createStartApplicationSampler();
			toggleAppStartedUI(true);
		}
		// Handle the recording buttons
		else if (CMD_TOGGLE_TEXT_CAPTURE.equals(actionCommand)) {
			boolean selected = btnCaptureText.isSelected();
			btnCaptureText.setSelected(false);
			if (selected) {
				startCapture(InteractionType.KEY);
			} else {
				stopCapture(null);
			}
		} else if (CMD_TOGGLE_MOUSE_CAPTURE.equals(actionCommand)) {
			boolean selected = btnCaptureMouse.isSelected();
			btnCaptureMouse.setSelected(false);
			if (selected) {
				startCapture(InteractionType.MOUSE);
			} else {
				stopCapture(null);
			}
		} else if (CMD_CHECK_FULL_SCREENSHOT.equals(actionCommand)) {
			stopCapture(ClauseType.FULL);
		} else if (CMD_CHECK_SELECTION_SCREENSHOT.equals(actionCommand)) {
			stopCapture(ClauseType.FORCE_HASH);
		} else if (CMD_CHECK_OCR_SCREENSHOT.equals(actionCommand)) {
			stopCapture(ClauseType.FORCE_OCR);
		}
	}

	private void toggleAppStartedUI(boolean appStarted) {
		btnAppStarted.setEnabled(!appStarted);
		btnCaptureMouse.setEnabled(appStarted);
		btnCaptureText.setEnabled(appStarted);
		toggleMouseCaptureOtionsUI(appStarted);
	}

	private void toggleMouseCaptureOtionsUI(boolean active) {
		pnlMouseCaptureOptions.setEnabled(active);
		chbIncludeMouseMoves.setEnabled(active);
		chbRelative.setEnabled(active);
	}

	private void toggleCaptureUI(InteractionType type) {
		if (capturedInteractionType != type) {
			capturedInteractionType = type;

			boolean capturingMouse = capturedInteractionType == InteractionType.MOUSE;
			String mouseLabel = capturingMouse ? "recorder_mouse_selected" : RESOURCE_KEY_START_MOUSE_CAPTURE;
			btnCaptureMouse.setSelected(capturingMouse);
			btnCaptureMouse.setText(CitrixUtils.getResString(mouseLabel, false));

			boolean capturingKey = capturedInteractionType == InteractionType.KEY;
			String keyLabel = capturingKey ? "recorder_text_selected" : RESOURCE_KEY_START_TEXT_CAPTURE;
			btnCaptureText.setSelected(capturingKey);
			btnCaptureText.setText(CitrixUtils.getResString(keyLabel, false));

			boolean capturing = capturedInteractionType != null;
			btnFullScreenshotCheck.setEnabled(capturing);
			btnPartialScreenshotCheck.setEnabled(capturing);
			btnOCRScreenshotCheck.setEnabled(capturing);
			toggleMouseCaptureOtionsUI(!capturing);
		}
	}

	private void toggleRecordingUI(boolean recording) {
		if (recording) {
			btnToggleRecording.setText(getStopRecordingText());
			btnToggleRecording.setIcon(STOP_IMAGE);
		} else {
			btnToggleRecording.setText(getStartRecordingText());
			btnToggleRecording.setIcon(START_IMAGE);
		}
		btnToggleRecording.setSelected(recording);
		lblFlowFirst.setText(getFlowFirstLabel(recording));
		lblFlowThen.setText(getFlowThenLabel(recording));
		toggleAppStartedUI(false);
		btnAppStarted.setEnabled(recording);
	}

	private boolean checkRecordingPrerequisites() {
		boolean valid = false;
		if (recorder.getDownloadingControllerNode() == null) {
			DialogHelper.showError(CitrixUtils.getResString("recorder_no_downloading_controller", false));
		} else if (recorder.getSamplersParentNode() == null && recorder.getAltSamplersParentNode() == null) {
			DialogHelper.showError(CitrixUtils.getResString("recorder_no_samplers_parent", false));
		} else {
			valid = true;
		}
		return valid;
	}

	private void toggleRecording(boolean startRecording, boolean cancelable) {
		if (startRecording) {
			if (!recorder.isRecording() && checkRecordingPrerequisites()) {
				progressBar.setIndeterminate(true);
				downloadIcaAndRecord();
			}
		} else {
			if (recorder.isRecording() && !stoppingRecord) {
				if (recorder.hasCapturedItems()) {

					// Define available options according to cancelable switch
					Object[] options;
					if (cancelable) {
						options = new Object[] { CitrixUtils.getResString("yes", false),
								CitrixUtils.getResString("no", false), CitrixUtils.getResString("cancel", false) };
					} else {
						options = new Object[] { CitrixUtils.getResString("yes", false),
								CitrixUtils.getResString("no", false) };
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
						btnToggleRecording.setSelected(true);
						return;
					} // if no is selected, clear capture and finish the function
					else if (response == JOptionPane.NO_OPTION) {
						recorder.cancelCapture();
					}
					// if yes is selected, finish the function
				}
				stoppingRecord = true;
				try {
					icaDownloadStatus.append("Stopping recorder\n");
					recorder.stopRecord();
					waitForDisconnect = true;
				} catch (CitrixClientException ex) {
					LOGGER.error("Unable to stop recording: {}", ex.getMessage(), ex);
					icaDownloadStatus.append(CitrixUtils.getResString("client_exception", false) + "\n");
				} finally {
					stoppingRecord = false;
				}
			}
		}
	}

	private class StartRecording extends SwingWorker<Boolean, Object> {

		private Optional<Path> icaPath;

		public StartRecording(Optional<Path> path) {
			this.icaPath = path;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Boolean doInBackground() throws Exception {
			recorder.startRecord(icaPath);
			return Boolean.TRUE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		protected void done() {
			try {
				progressBar.setValue(2);
				Boolean result = get();
				if (Boolean.TRUE.equals(result)) {
					progressBar.setString("Recording started");
					icaDownloadStatus.append("Recording started\n");
				} else {
					progressBar.setValue(0);
					icaDownloadStatus.append(CitrixUtils.getResString("client_exception", false) + "\n");
				}
			} catch (InterruptedException | ExecutionException e) {
				progressBar.setValue(0);
				LOGGER.error("Error occured starting citrix application {}", e.getMessage(), e);
				icaDownloadStatus.append(CitrixUtils.getResString("client_exception", false) + "\n");
			}
		}
	}

	private class DownloadIca extends SwingWorker<Optional<Path>, Object> {

		@Override
		protected Optional<Path> doInBackground() throws Exception {
			return recorder.downloadIcaFile();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		protected void done() {
			progressBar.setIndeterminate(false);
			progressBar.setValue(1);
			Optional<Path> path;
			try {
				path = get();
				boolean canRecord = false;
				if (path.isPresent()) {
					icaDownloadStatus.append("Downloaded ICA File to " + path.get().toString() + "\n");
					icaDownloadStatus.append("Launching application and recording\n");
					progressBar.setString("ICA File downloaded");
					canRecord = true;
				} else if (CitrixRecorder.SKIP_ICA_FILE_DOWNLOADING) {
					canRecord = true;
				} else {
					progressBar.setValue(0);
					icaDownloadStatus.append(CitrixUtils.getResString("recorder_ica_downloading_failed", false) + "\n");
				}
				if (canRecord) {
					StartRecording startRecorder = new StartRecording(path);
					startRecorder.execute();
				}

			} catch (InterruptedException | ExecutionException e) {
				progressBar.setValue(0);
				LOGGER.error("ICA file downloading error : {}", e.getMessage(), e);
				icaDownloadStatus.append(CitrixUtils.getResString("recorder_ica_downloading_failed", false) + "\n");
			}
		}

	}

	/**
	 * Starts the ICA Download and Record background jobs
	 */
	private void downloadIcaAndRecord() {
		if (!CitrixRecorder.SKIP_ICA_FILE_DOWNLOADING) {
			icaDownloadStatus.setText("Downloading ICA File\n");
		}
		new DownloadIca().execute();
	}

	private void startCapture(InteractionType interactionType) {
		recorder.startCapture(interactionType);
	}

	private void stopCapture(ClauseType clauseType) {
		recorder.stopCapture(clauseType);
	}

	@Override
	public Collection<String> getMenuCategories() {
		return Arrays.asList(MenuFactory.NON_TEST_ELEMENTS);
	}

	@Override
	public void onSessionEvent(SessionEvent e) {
		switch (e.getEventType()) {
		case ERROR:
			icaDownloadStatus.append(CitrixUtils.getResString("recorder_alert_error", false) + "\n");
			toggleRecording(false, false);
			break;
		case DISCONNECT:
			if (!waitForDisconnect) {
				icaDownloadStatus.append(CitrixUtils.getResString("recorder_alert_disconnect", false) + "\n");
			}
			waitForDisconnect = false;
			toggleRecording(false, false);
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
		toggleCaptureUI(interactionType);
	}

	@Override
	public void onRecordingChanged(boolean active) {
		toggleRecordingUI(active);
		if (!active) {
			progressBar.setString("");
			progressBar.setValue(0);
			icaDownloadStatus.append("Recorder stopped\n");
		}
	}

}
