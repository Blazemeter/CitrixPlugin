package com.blazemeter.jmeter.citrix.installer;

import com.blazemeter.jmeter.citrix.client.windows.com4j.ClassFactory;
import com.blazemeter.jmeter.citrix.client.windows.com4j.IICAClient;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.jmeter.gui.GuiPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks JMeter, JVM and machine setup for Citrix Plugin:
 * - JVM32 bits.
 * - Citrix Client Receiver installed.
 * - Registry keys with correct values.
 * - saveservice.properties updated.
 * - Register wfica.ocx.
 *
 * @since 1.1
 */
public class CitrixInstaller {
  private static final Logger LOGGER = LoggerFactory.getLogger(CitrixInstaller.class);
  private static final String WINDOWS_32_PREFIX = "Software\\Citrix\\ICA Client";
  private static final String WINDOWS_64_PREFIX = "SOFTWARE\\Wow6432Node\\Citrix\\ICA Client";
  private static final String ALLOW_SIMULATION_API_32_PATH = WINDOWS_32_PREFIX + "\\CCM";
  private static final String ALLOW_SIMULATION_API_64_PATH = WINDOWS_64_PREFIX + "\\CCM";
  private static final String ALLOW_SIMULATION_API_KEY = "AllowSimulationAPI";
  private static final String VD_LOAD_UNLOAD_TIMEOUT_KEY = "VdLoadUnLoadTimeOut";
  private static final String ADD_DWORD = "REG ADD \"%s\" /v \"%s\" /t REG_DWORD /d \"%s\"";
  private static final String REGISTER_OCX =
      "regsvr32 /s \"C:\\Program Files (x86)\\Citrix\\ICA Client\\wfica.ocx\"";
  private static final String SAVESERVICE_EXCERPT =
      "/com/blazemeter/jmeter/citrix/installer/saveservice-excerpt.properties";

  public static void main(String[] args) throws IOException {
    updateSaveService();
    if (SystemUtils.IS_OS_WINDOWS) {
      List<String> errorMsgs = new ArrayList<>();
      List<String> infoMsgs = new ArrayList<>();
      String jvmArchitecture = System.getProperty("os.arch");
      boolean is32BitsJVM = "x86".equals(jvmArchitecture);
      if (!is32BitsJVM) {
        errorMsgs.add(
            "Citrix Plugin must run with a JVM 32 bits, " +
                "install a 32 bits JVM and restart JMeter with it");
      }
      Architecture model = Architecture.valueOf("BIT_" + (is64BitWindows() ? "64" : "32"));
      boolean citrixReceiverInstalled = checkCitrixReceiverVersion(model, infoMsgs, errorMsgs);
      if (citrixReceiverInstalled) {
        checkWficaRegistered(is32BitsJVM, infoMsgs, errorMsgs);
        if (model == Architecture.BIT_32) {
          checkRegKey(ALLOW_SIMULATION_API_32_PATH, ALLOW_SIMULATION_API_KEY, "1", infoMsgs,
              errorMsgs);
          checkRegKey(WINDOWS_32_PREFIX, VD_LOAD_UNLOAD_TIMEOUT_KEY, "1e", infoMsgs, errorMsgs);
        } else {
          checkRegKey(ALLOW_SIMULATION_API_64_PATH, ALLOW_SIMULATION_API_KEY, "1", infoMsgs,
              errorMsgs);
          checkRegKey(WINDOWS_64_PREFIX, VD_LOAD_UNLOAD_TIMEOUT_KEY, "1e", infoMsgs, errorMsgs);
        }
      }
      if (!errorMsgs.isEmpty()) {
        reportErrorToUser(String.join("\r\n", errorMsgs),
            "Citrix Plugin Installation errors");
      }
      if (!infoMsgs.isEmpty()) {
        reportInfoToUser(String.join("\r\n", infoMsgs),
            "Citrix Plugin Installation informations");
      }
    }
  }

  /**
   * Update saveservice.properties if needed.
   */
  private static void updateSaveService() throws IOException {
    File self = new File(
        CitrixInstaller.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    String home = self.getParentFile().getParentFile().getParent();
    File jmeterBinDir = new File(home, "bin");

    String excerpt;
    LOGGER.info("Reading {}", SAVESERVICE_EXCERPT);
    try (
        InputStream inputStream = CitrixInstaller.class.getResourceAsStream(SAVESERVICE_EXCERPT)) {
      excerpt = IOUtils.toString(inputStream, StandardCharsets.ISO_8859_1);
      excerpt = excerpt.replaceAll("\n", "\r\n");
      LOGGER.info("{} contains {}", SAVESERVICE_EXCERPT, excerpt);
    }
    File saveServiceFile = new File(jmeterBinDir, "saveservice.properties");
    String saveServiceContent;
    try (FileReader fr = new FileReader(saveServiceFile)) {
      LOGGER.info("Reading {}", saveServiceFile);
      saveServiceContent = IOUtils.toString(fr);
      LOGGER.info("Read from {} content:{}", saveServiceFile, saveServiceContent);
    }
    if (!saveServiceContent
        .contains("_com.blazemeter.jmeter.citrix.sampler.CitrixSampleResultConverter=collection")) {
      LOGGER.info("Will add to {} content:{}", saveServiceFile, excerpt);
      FileUtils.write(saveServiceFile, "\r\n" + excerpt, StandardCharsets.ISO_8859_1.name(), true);
    } else {
      LOGGER.info("File {} already contains {}", saveServiceFile, excerpt);
    }
  }

  private static boolean is64BitWindows() {
    File file = new File("C:\\Windows\\SysWOW64");
    return file.exists();
  }

  private static JScrollPane formatMessage(String errorMsg) {
    JTextArea ta = new JTextArea(10, 50);
    ta.setText(errorMsg);
    ta.setWrapStyleWord(true);
    ta.setLineWrap(true);
    ta.setCaretPosition(0);
    ta.setEditable(false);
    return new JScrollPane(ta);
  }

  /**
   * Report an error through a dialog box in GUI mode
   * or in logs and stdout in Non GUI mode.
   *
   * @param errorMsg - the error message.
   * @param titleMsg - title string
   */
  public static void reportErrorToUser(String errorMsg, String titleMsg) {
    if (errorMsg == null) {
      errorMsg = "Unknown error - see log file";
      LOGGER.warn("Unknown error", new Throwable("errorMsg == null"));
    }
    GuiPackage instance = GuiPackage.getInstance();
    if (instance == null) {
      LOGGER.error(errorMsg);
      System.out.println(errorMsg); // NOSONAR intentional
      return; // Done
    }
    try {
      JOptionPane.showMessageDialog(instance.getMainFrame(),
          formatMessage(errorMsg),
          titleMsg,
          JOptionPane.ERROR_MESSAGE);
    } catch (HeadlessException e) {
      LOGGER.warn("reportErrorToUser(\"{}\") caused", errorMsg, e);
    }
  }

  /**
   * Report an information through a dialog box in GUI mode
   * or in logs and stdout in Non GUI mode.
   *
   * @param infoMsg  - the error message.
   * @param titleMsg - title string
   */
  public static void reportInfoToUser(String infoMsg, String titleMsg) {
    GuiPackage instance = GuiPackage.getInstance();
    if (instance == null) {
      LOGGER.info(infoMsg);
      System.out.println(infoMsg); // NOSONAR intentional
      return; // Done
    }
    try {
      JOptionPane.showMessageDialog(instance.getMainFrame(),
          formatMessage(infoMsg),
          titleMsg,
          JOptionPane.INFORMATION_MESSAGE);
    } catch (HeadlessException e) {
      LOGGER.warn("reportInfoToUser(\"{}\") caused", infoMsg, e);
    }
  }

  private static void checkWficaRegistered(boolean is32BitsJVM, List<String> infoMsgs,
                                           List<String> errors) {
    IICAClient client = null;
    try {
      if (is32BitsJVM) {
        LOGGER.info("Trying ICA client creation");
        client = ClassFactory.createICAClient();
        LOGGER.info("Successfully created ICA client");
      }
    } catch (Exception | UnsatisfiedLinkError exception) {
      LOGGER.warn("ICA Client creation failed with message:{}, will try to register ocx",
          exception.getMessage(), exception);
      try {
        if (runCommand(REGISTER_OCX, infoMsgs, errors)) {
          LOGGER.info("Registered successfully wfica.ocx using:{}", REGISTER_OCX);
          infoMsgs.add("Registered successfully wfica.ocx using:" + REGISTER_OCX);
        } else {
          LOGGER.error(
              "Failed registering wfica.ocx using:{}, ensure you run it as Local Administrator",
              REGISTER_OCX);
          errors.add("Failed registering wfica.ocx using:" + REGISTER_OCX +
              ", ensure you run it as Local Administrator");
        }
      } catch (IOException | InterruptedException e) {
        LOGGER.error(
            "Failed registering wfica.ocx using:{}, ensure you run it as Local Administrator",
            REGISTER_OCX, e);
        errors.add("Failed registering wfica.ocx using:" + REGISTER_OCX +
            ", ensure you run it as Local Administrator, error:" + e.getMessage());
      }
    } finally {
      if (client != null) {
        try {
          client.dispose();
        } catch (Exception e) {
          // NOOP
        }
      }
    }
  }

  private static boolean checkCitrixReceiverVersion(Architecture model, List<String> infoMsgs,
                                                    List<String> errors) {
    try {
      String key32 = "Software\\Citrix\\ICA Client\\Dazzle";
      String key64 = "SOFTWARE\\Wow6432Node\\Citrix\\Dazzle";
      String versionPath = model == Architecture.BIT_32 ? key32 : key64;
      LOGGER.info("Checking registry key in HKLM path:{}, key:{}", versionPath, "Version");
      String value =
          WinRegistry.valueForKey(WinRegistry.HKEY_LOCAL_MACHINE, versionPath, "Version");
      if (StringUtils.isEmpty(value)) {
        errors.add(
            "No Citrix Client Receiver is installed, install it to be able to use this plugin");
        return false;
      } else {
        LOGGER.info("Found Citrix Client Receiver in version :{}", value);
        infoMsgs.add("Found Citrix Client Receiver in version :" + value);
        return true;
      }
    } catch (Exception ex) {
      errors.add(
          "No Citrix Client Receiver is installed, " +
              "install it to be able to use this plugin, message:" +
              ex.getMessage());
      return false;
    }
  }

  private static boolean checkRegKey(String path, String key, String expectedValueAsHex,
                                     List<String> infoMsgs, List<String> errors) {
    try {
      LOGGER.info("Checking registry key in HKLM path:{}, key:{}", path, key);
      String value = WinRegistry.valueForKey(WinRegistry.HKEY_LOCAL_MACHINE, path, key);
      LOGGER.info("Got registry key in HKLM path:{}, key:{}, value:{}", path, key, value);
      if (StringUtils.isEmpty(value)) {
        return createRegKey(path, key, Integer.toString(Integer.parseInt(expectedValueAsHex, 16)),
            infoMsgs, errors);
      } else if (!("0x" + expectedValueAsHex).equals(value)) {
        LOGGER.warn(
            "Found registry key in HKLM path:{}, key:{}, value:{}, expected value should be:{}",
            path, key, value, expectedValueAsHex);
        errors.add("Found registry key:" + key + " in path:" + path + " with value:" + value +
            ", but value is not correct, change it to:" + expectedValueAsHex);
        return false;
      } else {
        LOGGER.info("Found registry key in HKLM path:{}, key:{}, value:{} exv:{}", path, key, value,
            expectedValueAsHex);
        infoMsgs.add("Found registry key:" + key + " in path:" + path + " with value:" + value);
        return ("0x" + expectedValueAsHex).equals(value);
      }
    } catch (Exception ex) {
      LOGGER.error("Error accessing registry key in HKLM path:{}, key:{}", path, key, ex);
      errors.add("Error reading registry key:" + key + " in path:" + path + " trying to set to:" +
          expectedValueAsHex + ", error:" + ex.getMessage());
      return createRegKey(path, key, Integer.toString(Integer.parseInt(expectedValueAsHex, 16)),
          infoMsgs, errors);
    }
  }

  private static boolean createRegKey(String path, String key, String expectedValue,
                                      List<String> infoMsgs, List<String> errors) {
    try {
      LOGGER.info("Adding registry key in HKLM path:{}, key:{} to value:{}", path, key,
          expectedValue);
      // REG ADD \"%s\" /v \"%s\" /t REG_DWORD /d \"%s\"
      if (runCommand(String.format(ADD_DWORD, "HKEY_LOCAL_MACHINE\\" + path, key, expectedValue),
          infoMsgs, errors)) {
        infoMsgs
            .add("Updated registry key:" + key + " in path:" + path + " to value:" + expectedValue);
      } else {
        infoMsgs.add("Could not update registry key:" + key + " in path:" + path + " to value:" +
            expectedValue + ", check you are running as local Administrator");
      }
      return true;
    } catch (Exception ex) {
      errors.add(
          "Error creating/updating registry key:" + key + " in path:" + path + " with value:" +
              expectedValue
              + ",check you are running as local Administrator, error:" + ex.getMessage());
      LOGGER.error("Error modifying registry key in HKLM path:{}, key:{}", path, key, ex);
      return false;
    }
  }

  private static boolean runCommand(String cmd, List<String> infoMsgs, List<String> errors)
      throws IOException, InterruptedException {
    LOGGER.info("Running commande line:" + cmd);
    Process process = Runtime.getRuntime().exec(cmd);
    // any output?
    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());

    // kick them off
    errorGobbler.start();
    outputGobbler.start();

    // any error???
    int rc = process.waitFor();

    if (!StringUtils.isEmpty(errorGobbler.getResult())) {
      errors.add("Running " + cmd + " returned:" + errorGobbler.getResult());
    }
    if (!StringUtils.isEmpty(outputGobbler.getResult())) {
      infoMsgs.add("Running " + cmd + " returned:" + outputGobbler.getResult());
    }
    return rc == 0 && StringUtils.isEmpty(errorGobbler.getResult());
  }

  private enum Architecture {
    BIT_32,
    BIT_64
  }

}
