package com.blazemeter.jmeter.citrix.recorder.gui;

import static org.assertj.swing.fixture.Containers.showInFrame;

import com.blazemeter.jmeter.citrix.recorder.Capture;
import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import java.io.IOException;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import junit.framework.TestCase;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JCheckBoxFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(SwingTestRunner.class)
public class CapturePanelTest extends TestCase { //NOSONAR

  @Rule
  public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
  private JUnitJMeter jmeter;
  private FrameFixture frame;
  private CitrixRecorderGUI gui;
  private CitrixRecorder recorder;

  // Custom Recorder Dialog, force never visible on test
  private static class CustomRecorderDialog extends RecorderDialog {

    @Override
    public void setVisible(boolean b) {
      super.setVisible(false);
    }

  }

  // Custom Recorder class to simulate default states for the test
  private static class CustomRecorder extends CitrixRecorder {

    @Override
    public boolean isRecording() {
      return true;
    }

  }

  private static class CustomRecorderWithCapture extends CustomRecorder {

    @Override
    public boolean hasCapturedItems() {
      return true;
    }

  }

  @Before
  public void setUp() throws IOException {

    jmeter = new JUnitJMeter();
    jmeter.start();
    jmeter.setVisible(false);

    gui = new CitrixRecorderGUI();

    recorder = new CustomRecorder();
    recorder.setHandler(gui);
    gui.setRecorder(recorder);
    gui.setRecorderDialog(new CustomRecorderDialog());

    JPanel mainPanel = gui.getCapturePanel().mainPanel;
    mainPanel.setVisible(true);

    frame = showInFrame(mainPanel);
    frame.target().pack();

  }

  @After
  public void tearDown() {
    frame.cleanUp();
    jmeter.close();
  }

  private JComboBoxFixture getActionList() {
    return frame.comboBox("action");
  }

  private JButtonFixture getRecordAction() {
    return frame.button("record_action");
  }

  private JPanelFixture getFinalActionPanel() {
    return frame.panel("final_action");
  }

  private JCheckBoxFixture getMouseOpsMouseMove() {
    return frame.checkBox("mouse_include_mouse_moves");
  }

  private JCheckBoxFixture getMouseOpsRelativeForeground() {
    return frame.checkBox("mouse_relative_foreground");
  }

  private JButtonFixture getAddAction() {
    return frame.button("AddButton");
  }

  private JButtonFixture getDiscardAction() {
    return frame.button("DiscardButton");
  }

  private void selectMouseCapture() {
    JComboBoxFixture actionList = getActionList();
    actionList.click(); // Open the list

    // Select the recording text input option
    actionList.selectItem(CitrixUtils.getText(CapturePanel.RESOURCE_KEY_START_MOUSE_CAPTURE));
  }

  private void selectTextCapture() {
    JComboBoxFixture actionList = getActionList();
    actionList.click(); // Open the list

    // Select the recording text input option
    actionList.selectItem(CitrixUtils.getText(CapturePanel.RESOURCE_KEY_START_TEXT_CAPTURE));
  }

  @Test
  public void shouldMouseOptionsSettedWhenClicked() {
    recorder = new CustomRecorder();
    recorder.setHandler(gui);
    gui.setRecorder(recorder);

    selectMouseCapture();

    // Start Capture Action

    Set<Capture.MouseCaptureOption> mOpts = recorder.getMouseCaptureOptions();
    boolean includeMoves = mOpts.contains(Capture.MouseCaptureOption.INCLUDE_MOVES);
    boolean includeRelativeForeground =
        mOpts.contains(Capture.MouseCaptureOption.RELATIVE_TO_FOREGROUND);

    frame.target().pack();

    getMouseOpsMouseMove().requireSelected(includeMoves);

    getMouseOpsMouseMove().click();

    getMouseOpsMouseMove().requireSelected(!includeMoves);

    getMouseOpsRelativeForeground().requireSelected(!includeRelativeForeground);

    getMouseOpsRelativeForeground().click();

    getMouseOpsRelativeForeground().requireSelected(includeRelativeForeground);

    Set<Capture.MouseCaptureOption> trgtOpts = recorder.getMouseCaptureOptions();
    boolean includeMovesInOpts = trgtOpts.contains(Capture.MouseCaptureOption.INCLUDE_MOVES);

    assertNotSame("Mouse Capture Option - Include Mouse Move not Sync",
        includeMoves, includeMovesInOpts);

    // NOTE: By default the relative to foreground is selected, the test check the unselected option
    boolean notIncludeRelativeForegroundInOpts =
        trgtOpts.contains(Capture.MouseCaptureOption.RELATIVE_TO_FOREGROUND);

    assertSame("Mouse Capture Option - Include Relative Foreground not Sync",
        includeRelativeForeground, notIncludeRelativeForegroundInOpts);
  }

  @Test
  public void shouldActionButtonsBeVisibledWhenStartCaptureAction() { //NOSONAR
    recorder = new CustomRecorder();
    recorder.setHandler(gui);
    gui.setRecorder(recorder);

    selectMouseCapture();

    // Start Capture Action
    getRecordAction().click();

    frame.target().pack();

    getFinalActionPanel().requireVisible();

  }

  private void initRecorderForStartCapture() {
    recorder = new CustomRecorderWithCapture();
    recorder.setHandler(gui);
    gui.setRecorder(recorder);
  }

  private void goToStartRecordTextInput() {
    initRecorderForStartCapture();

    selectTextCapture();

    // Start Capture Action
    getRecordAction().click();

    frame.target().pack();

    getFinalActionPanel().requireVisible();

  }

  @Test
  public void shouldActionButtonsNotBeVisibledWhenAddActionClicked() { //NOSONAR

    goToStartRecordTextInput();

    getAddAction().requireEnabled().click();

    frame.target().pack();

    // Check if the record action button is visible again
    getRecordAction().requireVisible();

  }

  @Test
  public void shouldDiscardActionSaveOkCaptured() { //NOSONAR

    DialogHelper.setDefaultOptionDialogResponse(JOptionPane.OK_OPTION); // Set Default to OK

    goToStartRecordTextInput();

    getDiscardAction().click();

    frame.target().pack();

    DialogHelper.setDefaultOptionDialogResponse(); // Return to default

    // Check if the record action button is visible again
    getRecordAction().requireVisible();

  }

  @Test
  public void shouldDiscardActionSaveCancelCaptured() { //NOSONAR

    DialogHelper.setDefaultOptionDialogResponse(JOptionPane.CANCEL_OPTION); // Set Default to CANCEL

    goToStartRecordTextInput();

    getDiscardAction().click();

    frame.target().pack();

    DialogHelper.setDefaultOptionDialogResponse(); // Return to default

    // Check if the discard action button is visible, the cancel cancel the discard action
    getDiscardAction().requireVisible();

  }

  @Test
  public void shouldDiscardActionSaveNoCaptured() { //NOSONAR

    DialogHelper.setDefaultOptionDialogResponse(JOptionPane.NO_OPTION); // Set Default to NO

    goToStartRecordTextInput();

    getDiscardAction().click();

    frame.target().pack();

    DialogHelper.setDefaultOptionDialogResponse(); // Return to default

    // Check if the record action button is visible again
    getRecordAction().requireVisible();

  }

  @Test
  public void shouldDiscardActionSaveCloseCaptured() { //NOSONAR

    DialogHelper.setDefaultOptionDialogResponse(JOptionPane.CLOSED_OPTION); // Set Default to CLOSED

    goToStartRecordTextInput();

    getDiscardAction().click();

    frame.target().pack();

    DialogHelper.setDefaultOptionDialogResponse(); // Return to default

    // Check if the record action button is visible again
    getDiscardAction().requireVisible();

  }

}
