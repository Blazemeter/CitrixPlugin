package com.blazemeter.jmeter.citrix.recorder.gui;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import com.blazemeter.jmeter.citrix.recorder.CitrixRecorder;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;
import java.io.IOException;
import javax.swing.Icon;
import junit.framework.TestCase;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.testelement.TestElement;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.fixture.JToggleButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(SwingTestRunner.class)
public class CitrixRecorderGUITest extends TestCase { //NOSONAR

  private static final String STORE_FRONT_URL = "https://test.com:1234/Citrix/StoreWeb";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String DOMAIN = "domain";
  private static final String APP = "Notepad";
  @Rule
  public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
  private FrameFixture frame;
  @Mock
  private CitrixRecorder recorder;

  private JUnitJMeter jmeter;
  private CitrixRecorderGUI gui = null;

  @Before
  public void setUp() throws IOException {

    jmeter = new JUnitJMeter();
    jmeter.start();
    jmeter.setVisible(false);

    Mockito.when(recorder.skipIcaFileDownloading()).thenReturn(true);
    Mockito.when(recorder.isFromTemplate()).thenReturn(true);
    Mockito.when(recorder.isRecording()).thenReturn(false);
    JMeterTreeNode value = new JMeterTreeNode();
    Mockito.when(recorder.getSamplersParentNode()).thenReturn(value);
    Mockito.when(recorder.getDownloadingControllerNode()).thenReturn(value);

    gui = new CitrixRecorderGUI();
    gui.setRecorder(recorder);

    frame = showInFrame(gui);
    frame.target().pack();
  }

  @After
  public void tearDown() {
    frame.cleanUp();
    jmeter.close();
  }

  @Test
  public void shouldJMeterIsMinimizedWhenSendToMinimize() { // NOSONAR
    // This test only cover the no error exception error on this methods
    jmeter.setVisible(true);
    DialogHelper.focusJMeter();
    DialogHelper.minimizeJMeter();
  }

  @Test
  public void shouldCrossedOutEyeBeVisibleAndInvisibleWhenDisplayEyeIsSelected() {
    JToggleButtonFixture display = getPasswordDisplay();
    Icon displayIcon = display.target().getIcon();
    // Change the icon to crossed out eye
    display.click();
    assertNotEquals(display.target().getIcon(), displayIcon);
    // Reverse action, return to not crossed out eye
    display.click();
    assertEquals(display.target().getIcon(), displayIcon);
  }

  @Test
  public void shouldStartButtonBeDisabledWhenSomeRequiredFieldsAreMissing() {
    getStorefrontURL().setText(STORE_FRONT_URL);
    forceLooseFocus();
    assertThat(getStartButton().isEnabled()).isFalse();
  }

  private JToggleButtonFixture getPasswordDisplay() {
    return frame.toggleButton("display");
  }

  private JTextComponentFixture getStorefrontURL() {
    return frame.textBox("storefrontURL");
  }

  private JButtonFixture getStartButton() {
    return frame.button("startButton");
  }

  @Test
  public void shouldStartButtonBeEnabledWhenRequiredFieldsProvided() {
    getStorefrontURL().setText(STORE_FRONT_URL);
    getUsername().setText(USERNAME);
    getDomain().setText(DOMAIN);
    getPassword().setText(PASSWORD);
    getApplication().setText(APP);
    forceLooseFocus();

    assertThat(getStartButton().isEnabled()).isTrue();
  }

  @Test
  public void shouldDomainValidationHaveErrorOnInvalidDomain() {
    getDomain().setText("domain_invalid");
    forceLooseFocus();
    assertThat(getDomainError().text()).isNotEmpty();
    getDomain().setText("domain,invalid");
    forceLooseFocus();
    assertThat(getDomainError().text()).isNotEmpty();
    getDomain().setText("domain invalid");
    forceLooseFocus();
    assertThat(getDomainError().text()).isNotEmpty();
  }

  @Test
  public void shouldDomainValidationHaveOKWhenNoDomainProvided() {
    getDomain().setText("");
    forceLooseFocus();
    Exception exc = assertThrows(ComponentLookupException.class, this::getDomainError);
    assertThat(exc.getMessage()).contains("Unable to find component using matcher");
  }

  private JTextComponentFixture getPassword() {
    return frame.textBox("password");
  }

  private JTextComponentFixture getUsername() {
    return frame.textBox("username");
  }

  private JTextComponentFixture getApplication() {
    return frame.textBox("application");
  }

  private JTextComponentFixture getDomain() {
    return frame.textBox("domain");
  }

  private JLabelFixture getDomainError() {
    return frame.label("domainError");
  }

  private void forceLooseFocus() {
    frame.target().requestFocusInWindow();
  }

  @Test
  public void shouldDisableStartWhenStartRecording() {
    getStorefrontURL().setText(STORE_FRONT_URL);
    getUsername().setText(USERNAME);
    getDomain().setText(DOMAIN);
    getPassword().setText(PASSWORD);
    getApplication().setText(APP);
    forceLooseFocus();
    getStartButton().click();
    assertThat(getStartButton().isEnabled()).isFalse();
  }

  @Test
  public void shouldEnableStopWhenStartRecording() {
    getUsername().setText("username1");
    getDomain().setText("domain1");
    getPassword().setText("password");
    getApplication().setText("application1");
    getStorefrontURL().setText(STORE_FRONT_URL);
    forceLooseFocus();
    getStartButton().click();
    assertThat(getStopButton().isEnabled()).isTrue();
  }


  private JButtonFixture getStopButton() {
    return frame.button("stopButton");
  }

  @Test
  public void shouldConfiguringRunWithoutError() { //NOSONAR
    // First with the mocked version of recorder
    gui.configure(recorder);

    // With the recorder created in the gui class, casted to TestElement
    TestElement recTE = gui.createTestElement();
    gui.configure(recTE);

  }

}