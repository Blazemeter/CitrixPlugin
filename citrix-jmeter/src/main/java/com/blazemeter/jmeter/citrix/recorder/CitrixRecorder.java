package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.CitrixClient.Snapshot;
import com.blazemeter.jmeter.citrix.client.CitrixClientException;
import com.blazemeter.jmeter.citrix.client.CitrixClientFactoryException;
import com.blazemeter.jmeter.citrix.client.CitrixClientFactoryHelper;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.InteractionType;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent;
import com.blazemeter.jmeter.citrix.client.events.SessionEvent.EventType;
import com.blazemeter.jmeter.citrix.client.factory.CitrixClientFactory;
import com.blazemeter.jmeter.citrix.listener.CitrixIcaFileSaver;
import com.blazemeter.jmeter.citrix.recorder.Capture.MouseCaptureOption;
import com.blazemeter.jmeter.citrix.recorder.RecordingHandler.CaptureManager;
import com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler;
import com.blazemeter.jmeter.citrix.sampler.InteractionSampler;
import com.blazemeter.jmeter.citrix.sampler.SamplerHelper;
import com.blazemeter.jmeter.citrix.sampler.SamplerResultHelper;
import com.blazemeter.jmeter.citrix.sampler.StartApplicationSampler;
import com.blazemeter.jmeter.citrix.sampler.gui.StartApplicationSamplerGUI;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import com.blazemeter.jmeter.citrix.utils.TestPlanHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.tree.TreeNode;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.ModuleController;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.TransactionControllerGui;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.NonTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which handle saved events from the client. Possess a RecordingHandler
 * which handles Mouse and Keys events.
 */
public class CitrixRecorder extends GenericController implements NonTestElement, CaptureManager {

  public static final boolean SKIP_ICA_FILE_DOWNLOADING = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "skip_ica_file_downloading", false);
  private static final long serialVersionUID = 1194210414410846275L;
  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixRecorder.class);

  // Name of the property used to save the path to the controller used to download
  // ICA file
  private static final String DOWNLOADING_CONTROLLER_PATH =
      "CitrixRecorder.ica_downloading_controller_path";

  // Name of the property used to save the path to the controller used to insert
  // samplers in test plan
  private static final String SAMPLERS_PARENT_CONTROLLER_PATH =
      "CitrixRecorder.samplers_parent_controller_path";

  // JMeterProperty that defines the name of the variable used to store ICA file
  // path when StartApplicationSampler is created
  private static final String ICA_PATH_VAR = CitrixIcaFileSaver.DEFAULT_ICA_FILE_PATH_VAR;
  // $NON-NLS-1$

  // + JMX file attributes
  private static final String STEP_NAME = "CitrixRecorder.stepName";
  private final transient RecordingHandler recordingHandler;

  // - JMX file attributes
  private transient CitrixClient client;
  private transient CitrixRecorderHandler handler;
  private long logOnStart;
  private int samplerId = 0;
  private JMeterTreeNode downloadingControllerNode;
  private JMeterTreeNode samplersParentNode;
  private JMeterTreeNode altSamplersParentNode;

  /**
   * Instantiates a new {@link CitrixRecorder}.
   */
  public CitrixRecorder() {

    // Initialize recordingHandler
    recordingHandler = new RecordingHandler(this,
        // time-stamp on log on and relay any session events to handler
        e -> {
          if (e.getEventType() == EventType.LOGON) {
            logOnStart = System.currentTimeMillis();
          }
          if (handler != null) {
            handler.onSessionEvent(e);
          }
        });
  }

  public boolean isCapturing() {
    return recordingHandler.isCapturing();
  }

  @Override
  public boolean canRemove() {
    return !isRecording();
  }

  /**
   * Indicates whether current recorder is running.
   *
   * @return true if recording is started;false otherwise.
   */
  public boolean isRecording() {
    return client != null;
  }

  public boolean hasCapturedItems() {
    return recordingHandler.hasCapturedItems();
  }

  public Set<MouseCaptureOption> getMouseCaptureOptions() {
    return recordingHandler.getMouseCaptureOptions();
  }

  /**
   * Download the ICA file.
   *
   * @return {@link Paths} path to ICA file
   * @throws JMeterEngineException when the ICA file downloading fails
   */
  public Optional<Path> downloadIcaFile() throws JMeterEngineException {
    if (!isRecording()) {
      LOGGER.info(CitrixUtils.getResString("recorder_begin_recording", false));
      return tryICAFileDownloading();

    } else {
      throw new IllegalStateException("Recorder is already recording");
    }
  }

  /**
   * Create a new Client that send events and start the record.
   *
   * @param icaFilePath {@link Path} to ICA file
   * @throws CitrixClientFactoryException when Citrix client factory is missing
   * @throws CitrixClientException        when Citrix client is unable to start
   */
  public void startRecord(Optional<Path> icaFilePath)
      throws CitrixClientFactoryException, CitrixClientException {
    if (!isRecording()) {
      CitrixClientFactory factory = CitrixClientFactoryHelper.getInstance();
      client = factory.createClient();
      client.addHandler(recordingHandler);
      icaFilePath.ifPresent(path -> client.setICAFilePath(path));
      long recordingStart = System.currentTimeMillis();
      client.start(false, GuiPackage.getInstance() != null);
      if (handler != null) {
        handler.onRecordingChanged(true);
      }
      notifyTestListenersOfStart();
    } else {
      throw new IllegalStateException("Recorder is already recording");
    }
  }

  /**
   * Stop the current Citrix client.
   *
   * @throws CitrixClientException when CitrixClient is unable to stop
   */
  public void stopRecord() throws CitrixClientException {
    if (isRecording()) {
      try {
        stopCapture(null);
        notifyTestListenersOfEnd();
      } finally {
        stopClient();
        if (handler != null) {
          handler.onRecordingChanged(false);
        }
      }
    }
  }

  public boolean clientIsRunning() {
    return (client != null && client.isConnected());
  }
  
  private void stopClient() {
    if (client != null) {
      try {
        client.stop();
        client.removeHandler(recordingHandler);
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
      client = null;
    }
  }

  /**
   * <p>
   * Starts capture of user interactions.
   * </p>
   *
   * <p>
   * Does nothing if a capture is already running
   * </p>
   *
   * @param interactionType type of captured interactions
   */
  public void startCapture(InteractionType interactionType) {
    boolean started = recordingHandler.startCapture(interactionType);
    if (started && handler != null) {
      handler.onCaptureChanged(interactionType);
    }
  }

  private Clause buildClause(Snapshot snapshot) {
    return buildClause(snapshot, null);
  }

  private Clause buildClause(Snapshot snapshot, Set<CheckType> checkTypes) {
    return handler != null ? handler.onBuildClause(snapshot, checkTypes) : null;
  }

  /**
   * <p>
   * Stops running capture and associates clause of the specified type to the last
   * generated sampler.
   * </p>
   *
   * @param clauseType the kind of clause to build
   */
  public void stopCapture(ClauseType clauseType) {
    final Capture capture = recordingHandler.stopCapture();

    if (capture != null) {
      final List<InteractionSampler> samplers = buildSamplersFromCapture(capture);

      // Build clause if required and sampler exists
      int count = samplers.size();
      if (count > 0) {
        InteractionSampler lastSampler = samplers.get(count - 1);
        Snapshot snapshot = null;
        try {
          snapshot = client.takeSnapshot();
          if (clauseType != null) {
            switch (clauseType) {
              case FULL:
                // POSSIBLE_IMPROVEMENT Handle relative to foreground window
                lastSampler
                    .setEndClause(new Clause(CheckType.HASH, snapshot.getScreenshot(), null, null));
                break;
              case FORCE_HASH:
                lastSampler.setEndClause(buildClause(snapshot, EnumSet.of(CheckType.HASH)));
                break;
              case FORCE_OCR:
                lastSampler.setEndClause(buildClause(snapshot, EnumSet.of(CheckType.OCR)));
                break;
              case QUERY_USER:
                lastSampler.setEndClause(buildClause(snapshot));
                break;
              default:
                break;
            }
          }
        } catch (CitrixClientException ex) {
          LOGGER.error("Unable to get snapshot at the end of the capture.", ex);
        } catch (ClauseComputationException ex) {
          LOGGER.error("Unable to set {} clause when capture finished.", clauseType, ex);
        } finally {
          final Snapshot shot = snapshot;
          samplers.forEach(
              s -> notifySampleListeners(buildOkEvent(s, s == lastSampler ? shot : null)));
          insertSamplersToPlan(samplers);
        }
      }

      if (handler != null) {
        handler.onCaptureChanged(null);
      }
    }
  }

  public void cancelCapture() {
    boolean canceled = recordingHandler.cancelCapture();
    if (canceled && handler != null) {
      handler.onCaptureChanged(null);
    }
  }

  /* Use Start validation logic */
  private Optional<Path> tryICAFileDownloading() throws JMeterEngineException {
    Optional<Path> icaFile = Optional.empty();
    if (!SKIP_ICA_FILE_DOWNLOADING) {

      // Get the current test plan and normalize it
      GuiPackage gui = GuiPackage.getInstance();
      HashTree testTree = gui.getTreeModel().getTestPlan();
      // Ensure Module and Include controllers are setup
      JMeter.convertSubTree(testTree);

      // Clone the test to ensure we have all configuration and disable Thread Groups
      TreeClonerForICADownloading disableThreadGroupsCloner = new TreeClonerForICADownloading();
      testTree.traverse(disableThreadGroupsCloner);
      ListedHashTree clonedTree = disableThreadGroupsCloner.getClonedTree();

      createThreadGroupForDownload(clonedTree, clonedTree.getArray()[0],
          getDownloadingControllerNode());
      // Ensure ModuleController is replaced
      JMeter.convertSubTree(clonedTree);

      // Clone test to avoid using referenced node
      TreeCloner cloner = new TreeCloner(false);
      clonedTree.traverse(cloner);
      clonedTree = cloner.getClonedTree();

      RecordingCitrixIcaFileSaver citrixIcaFileSaver = new RecordingCitrixIcaFileSaver();
      citrixIcaFileSaver.setVariableName(CitrixIcaFileSaver.DEFAULT_ICA_FILE_PATH_VAR);
      citrixIcaFileSaver.setSaveFolder(CitrixIcaFileSaver.DEFAULT_ICA_FOLDER_NAME);
      clonedTree.add(clonedTree.getArray()[0], citrixIcaFileSaver);

      // Execute the downloading using JMeter engine
      StandardJMeterEngine engine = new StandardJMeterEngine();
      engine.configure(clonedTree);
      engine.runTest();
      try {
        // Wait download is done
        while (engine.isActive()) {
          Thread.sleep(1000L);
        }

        String icaFilename = citrixIcaFileSaver.getRecordedIcaFile();
        icaFile = Optional.ofNullable(icaFilename != null ? new File(icaFilename).toPath() : null);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    if (icaFile.isPresent()) {
      final Path file = icaFile.get();
      LOGGER.info("ICA file successfully downloaded in : {}", file);
      if (LOGGER.isDebugEnabled()) {
        try {
          // POSSIBLE_IMPROVEMENT Handle charset
          LOGGER.debug("ICA file content: \n{}\n\n", new String(Files.readAllBytes(file)));
        } catch (IOException e) {
          LOGGER.warn("Unable to get ICA file content: {}", e.getMessage(), e);
        }
      }
    }

    return icaFile;
  }

  /**
   * Creates a ThreadGroup for ICA Download using a {@link ModuleController}
   * pointing to downloadingControllerNode.
   *
   * @param testplanHT                TestPlan {@link HashTree}
   * @param parentNode                Parent node to which {@link ThreadGroup} is
   *                                  added
   * @param downloadingControllerNode {@link JMeterTreeNode} Reference to the node
   *                                  that contains the ICA Download scenario
   */
  private void createThreadGroupForDownload(HashTree testplanHT, Object parentNode,
                                            JMeterTreeNode downloadingControllerNode) {
    ModuleController mc = new ModuleController();
    mc.setSelectedNode(downloadingControllerNode);

    LoopController lc = new LoopController();
    lc.setLoops(1);
    lc.setFirst(true);

    ThreadGroup tg = new ThreadGroup();
    tg.setName("ICA-Download-By-Citrix-Recorder");
    tg.setNumThreads(1);
    tg.setProperty(AbstractThreadGroup.ON_SAMPLE_ERROR,
        AbstractThreadGroup.ON_SAMPLE_ERROR_STOPTEST_NOW);
    tg.setScheduler(false);
    tg.setProperty(ThreadGroup.DELAY, 0);
    tg.setSamplerController(lc);

    HashTree threadGroupHT = testplanHT.add(parentNode, tg);
    HashTree loopControllerHT = threadGroupHT.add(lc);
    loopControllerHT.add(mc);
  }

  private void insertSamplersToPlan(List<? extends Sampler> samplers) {
    // Get the target node where samplers are added
    JMeterTreeNode target = getSamplersParentNode();
    if (target == null) {
      target = getAltSamplersParentNode();
    }

    // Error when no node selected
    if (target == null) {
      String msg = "Unable to insert samplers to test plan : No target node is selected";
      LOGGER.error(msg);
      throw new IllegalStateException(msg);
    }

    // Create transaction controller when several samplers have to be inserted
    if (samplers.size() > 1) {
      TransactionController txController = new TransactionController();
      txController.setIncludeTimers(false);
      txController.setProperty(TestElement.GUI_CLASS, TransactionControllerGui.class.getName());
      txController.setName(samplers.get(0).getName());
      target = TestPlanHelper.addToPlan(target, txController);

      // Error when controller insertion fails
      if (target == null) {
        String msg =
            "Unable to insert samplers to test plan : Cannot insert transaction controller.";
        LOGGER.error(msg);
        throw new IllegalStateException(msg);
      }
    }

    // Add samplers to plan
    final JMeterTreeNode parent = target;
    samplers.forEach(s -> TestPlanHelper.addToPlan(parent, s));
  }

  private SampleEvent buildOkEvent(CitrixBaseSampler sampler, Snapshot snapshot) {
    return new SampleEvent(SamplerResultHelper.buildOkResult(sampler, snapshot), "Citrix Recorder");
  }

  public void createStartApplicationSampler() {
    if (isRecording()) {
      final String name = CitrixUtils.getResString(StartApplicationSamplerGUI.RSC_TITLE, false);
      StartApplicationSampler sampler = new StartApplicationSampler();
      sampler.setProperty(TestElement.GUI_CLASS, StartApplicationSamplerGUI.class.getName());
      sampler.setName(name);
      sampler.setICAPathVar(ICA_PATH_VAR);
      long now = System.currentTimeMillis();
      long clauseTimeout = now - logOnStart;

      Snapshot snapshot = null;
      try {
        snapshot = client.takeSnapshot();
        Clause clause = buildClause(snapshot);
        if (clause != null) {
          clause.setTimeout(clauseTimeout);
          sampler.setEndClause(clause);
        }
      } catch (CitrixClientException e) {
        LOGGER.error("Unable to get snapshot at application start", e);
      } finally {
        notifySampleListeners(buildOkEvent(sampler, snapshot));
        insertSamplersToPlan(Collections.singletonList(sampler));
      }
    }
  }

  public String getStepName() {
    return getPropertyAsString(STEP_NAME);
  }

  /* ============= object properties ================ */

  public void setStepName(String stepName) {
    setProperty(STEP_NAME, stepName);
  }

  /**
   * Gets the node of test plan that matches the controller used to download ICA
   * file.
   *
   * @return the node of test plan that matches the controller use to download ICA
   */
  public JMeterTreeNode getDownloadingControllerNode() {
    // Retrieve the node of the test plan from the test plan property
    // ica_downloading_controller_path
    if (downloadingControllerNode == null) {
      downloadingControllerNode = TestPlanHelper.findNodeByPath(getDownloadingControllerPath());
    }
    return downloadingControllerNode;
  }

  /**
   * Updates the controller node used to download ICA file.
   *
   * @param downloadingControllerNode the controller node to use to download ICA
   *                                  file
   */
  public void setDownloadingControllerNode(JMeterTreeNode downloadingControllerNode) {
    this.downloadingControllerNode = downloadingControllerNode;
    // Updapte the property of the test plan
    List<String> path = new ArrayList<>();
    if (downloadingControllerNode != null) {
      for (TreeNode node : downloadingControllerNode.getPath()) {
        path.add(((JMeterTreeNode) node).getName());
      }
    }
    setProperty(new CollectionProperty(DOWNLOADING_CONTROLLER_PATH, path));
  }

  /**
   * Gets the value of the test plan property used to store the path to "ICA
   * Downloading" controller.
   *
   * @return the path element to "ICA Downloading controller"
   */
  @SuppressWarnings("unchecked")
  public List<JMeterProperty> getDownloadingControllerPath() {
    List<JMeterProperty> path = null;
    JMeterProperty prop = getProperty(DOWNLOADING_CONTROLLER_PATH);
    if (prop instanceof CollectionProperty) {
      path = (List<JMeterProperty>) prop.getObjectValue();
    }
    return path;
  }

  /**
   * Gets the node of test plan that matches the controller used to insert
   * samplers.
   *
   * @return the node of test plan that matches the controller used to insert
   * samplers
   */
  public JMeterTreeNode getSamplersParentNode() {
    // Retrieve the node of the test plan from the test plan property
    // "samplers_parent_path"
    if (samplersParentNode == null) {
      samplersParentNode = TestPlanHelper.findNodeByPath(getSamplersParentPath());
    }
    return samplersParentNode;
  }

  /**
   * Updates the controller node used to insert samplers.
   *
   * @param samplersParentNode the controller node to use to insert samplers
   */
  public void setSamplersParentNode(JMeterTreeNode samplersParentNode) {
    this.samplersParentNode = samplersParentNode;
    // Updapte the property of the test plan
    List<String> path = new ArrayList<>();
    if (samplersParentNode != null) {
      for (TreeNode node : downloadingControllerNode.getPath()) {
        path.add(((JMeterTreeNode) node).getName());
      }
    }
    setProperty(new CollectionProperty(SAMPLERS_PARENT_CONTROLLER_PATH, path));
  }

  /**
   * <p>
   * Gets the alternative test plan element used to insert created samplers.
   * </p>
   *
   * <p>
   * This element is used only if SamplersParentNode is null.
   * </p>
   * <p>
   * The value of this property is not saved in test plan.
   * </p>
   *
   * @return the alternative test plan element used to insert created samplers.
   */
  public JMeterTreeNode getAltSamplersParentNode() {
    return altSamplersParentNode;
  }

  /**
   * <p>
   * Defines the alternative test plan element used to insert created samplers.
   * </p>
   *
   * <p>
   * This element is used only if SamplersParentNode is null.
   * </p>
   * <p>
   * The value of this property is not saved in test plan.
   * </p>
   *
   * @param altSamplersParentNode the test plan element to use
   */
  public void setAltSamplersParentNode(JMeterTreeNode altSamplersParentNode) {
    this.altSamplersParentNode = altSamplersParentNode;
  }

  /**
   * Gets the value of the test plan property used to store the path to "ICA
   * Downloading" controller.
   *
   * @return the path element to "ICA Downloading controller"
   */
  @SuppressWarnings("unchecked")
  public List<JMeterProperty> getSamplersParentPath() {
    List<JMeterProperty> path = null;
    JMeterProperty prop = getProperty(SAMPLERS_PARENT_CONTROLLER_PATH);
    if (prop instanceof CollectionProperty) {
      path = (List<JMeterProperty>) prop.getObjectValue();
    }
    return path;
  }

  private List<InteractionSampler> buildSamplersFromCapture(Capture capture) {
    List<InteractionSampler> samplers = new ArrayList<>();
    if (capture != null) {
      Consumer<InteractionSampler> action = s -> s.setName(++samplerId + " " + s.getName());
      switch (capture.getInteractionType()) { // NOSONAR We do what we want SONAR ok ?
        case KEY:
          samplers.addAll(SamplerHelper.buildKeySamplers(capture, action));
          break;
        case MOUSE:
          samplers.addAll(SamplerHelper.buildMouseSamplers(capture, action));
          break;
        default:
          break;
      }
    }
    return samplers;
  }

  public final void setHandler(CitrixRecorderHandler handler) {
    this.handler = handler;
  }

  @Override
  public void onCaptureItemAdded(CaptureItem item) {
    // set current step name on capture
    item.setLabel(getStepName());

  }

  @Override
  public void onCaptureSizeExceeded() {
    DialogHelper.showAlert(CitrixUtils.getResString("recorder_alert_capture_size_exceeded", false));
    stopCapture(ClauseType.QUERY_USER);
  }

  /**
   * This will notify sample listeners directly within the CitrixRecorder of the
   * sampling that just occurred -- so that we have a means to record the
   * server's responses as we go.
   *
   * @param event sampling event to be delivered
   */
  private void notifySampleListeners(SampleEvent event) {
    JMeterTreeModel treeModel = getJMeterTreeModel();
    JMeterTreeNode myNode = treeModel.getNodeOf(this);
    if (myNode != null) {
      Enumeration<?> kids = myNode.children();
      while (kids.hasMoreElements()) {
        JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
        if (subNode.isEnabled()) {
          TestElement testElement = subNode.getTestElement();
          if (testElement instanceof SampleListener) {
            ((SampleListener) testElement).sampleOccurred(event);
          }
        }
      }
    }
  }

  /**
   * This will notify test listeners directly within the CitrixRecorder that the 'test'
   * (here meaning the recording) has started.
   */
  private void notifyTestListenersOfStart() {
    JMeterTreeModel treeModel = getJMeterTreeModel();
    JMeterTreeNode myNode = treeModel.getNodeOf(this);
    if (myNode != null) {
      Enumeration<?> kids = myNode.children();
      while (kids.hasMoreElements()) {
        JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
        if (subNode.isEnabled()) {
          TestElement testElement = subNode.getTestElement();
          if (testElement instanceof TestStateListener) {
            ((TestStateListener) testElement).testStarted();
          }
        }
      }
    }
  }

  /**
   * This will notify test listeners directly within the CitrixRecorder that the 'test'
   * (here meaning the recording) has ended.
   */
  private void notifyTestListenersOfEnd() {
    JMeterTreeModel treeModel = getJMeterTreeModel();
    JMeterTreeNode myNode = treeModel.getNodeOf(this);
    if (myNode != null) {
      Enumeration<?> kids = myNode.children();
      while (kids.hasMoreElements()) {
        JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
        if (subNode.isEnabled()) {
          TestElement testElement = subNode.getTestElement();
          if (testElement instanceof TestStateListener) { // TL - TE
            ((TestStateListener) testElement).testEnded();
          }
        }
      }
    }
  }

  private JMeterTreeModel getJMeterTreeModel() {
    return GuiPackage.getInstance().getTreeModel();
  }

  public enum ClauseType {
    FULL, FORCE_HASH, FORCE_OCR, QUERY_USER
  }

  public interface CitrixRecorderHandler {

    void onCaptureChanged(InteractionType interactionType);

    void onRecordingChanged(boolean active);

    Clause onBuildClause(Snapshot snapshot, Set<CheckType> checkTypes);

    void onSessionEvent(SessionEvent e);
  }

}
