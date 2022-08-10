package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.assertions.TestAssertionHelper;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.MainFrame;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.ActionRouter;
import org.apache.jmeter.gui.tree.JMeterTreeListener;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.util.FocusRequester;
import org.apache.jmeter.plugin.PluginManager;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class JUnitJMeter extends JMeter implements AutoCloseable {

  private MainFrame main;
  private final Class<TestAssertionHelper> helper = TestAssertionHelper.class;

  private static final String BIN_DIR = "bin";
  private static final String JMETER_PROPS_FILE_NAME = "jmeter.properties";
  private static final String LIB_DIR = "lib";
  private static final String LIB_EXT_DIR = "lib/ext";
  private static final String JUNITLIB_DIR = "lib/junit";

  private Path homeDir;

  public JUnitJMeter() throws IOException {
    homeDir = Files.createTempDirectory("junit-jmeter");

    homeDir.toFile().deleteOnExit();
    System.setProperty("jmeter.home", homeDir.toString());

    File binDir = new File(homeDir.toFile(), BIN_DIR);
    // Create the default config files
    installConfig(binDir);

    // Create the dummy lib, lib/ext and lib/junit folder
    createDummyLibFolder(homeDir, LIB_DIR);
    createDummyLibFolder(homeDir, LIB_EXT_DIR);
    createDummyLibFolder(homeDir, JUNITLIB_DIR);

    String jmeterProperties = homeDir + "/bin/jmeter.properties";

    JMeterUtils.loadJMeterProperties(jmeterProperties);
    loadJMeterClassesFromRef();

  }

  @Override
  public void close() {
    // Mark all the file to be cleaned on exit
    Path configBinDir = homeDir;
    try {
      for (Path p : (Iterable<Path>) Files.walk(configBinDir)::iterator) {
        p.toFile().deleteOnExit();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createDummyLibFolder(Path home, String path) throws IOException {
    Path libDir = Paths.get(home.toString(), path);
    Files.createDirectories(libDir);
  }

  private void installConfig(File binDir) throws IOException {
    // Extract all the default settings and templates from JMeter config jar
    try (FileSystem fs = FileSystems
        .newFileSystem(getClass().getResource("/" + BIN_DIR + "/" + JMETER_PROPS_FILE_NAME).toURI(),
            Collections.emptyMap())) {
      Path configBinDir = fs.getPath("/" + BIN_DIR);
      for (Path p : (Iterable<Path>) Files.walk(configBinDir)::iterator) {
        Path targetPath = binDir.toPath().resolve(configBinDir.relativize(p).toString());
        Files.copy(p, targetPath);
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadJMeterClassesFromRef() {
    ArrayList<String> jarPaths = new ArrayList<>();
    jarPaths.add(getClassJarPath(org.apache.jmeter.gui.util.MenuFactory.class));
    jarPaths
        .add(getClassJarPath(org.apache.jmeter.gui.action.ExportTransactionAndSamplerNames.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.functions.EvalFunction.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.http.sampler.HTTPSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.ftp.sampler.FTPSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.java.sampler.JavaSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.bolt.sampler.BoltSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.jms.sampler.JMSSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.java.sampler.JUnitSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.ldap.sampler.LDAPSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.mail.sampler.MailReaderSampler.class));
    jarPaths
        .add(getClassJarPath(org.apache.jmeter.protocol.mongodb.sampler.MongoScriptSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.system.SystemSampler.class));
    jarPaths.add(getClassJarPath(org.apache.jmeter.protocol.tcp.sampler.TCPSampler.class));

    String search_path =
        String.join(";", jarPaths) + ";" + JMeterUtils.getProperty("search_paths");
    JMeterUtils.setProperty("search_paths",
        search_path);
  }

  private String getClassJarPath(Class<?> theClass) {
    try {
      return new File(
          theClass.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public void start() {
    String[] args = {};
    this.start(args);
  }

  @Override
  public void start(String[] args) {

    JMeterUtils.initLocale();
    JMeterUtils.setJMeterHome(System.getProperty("jmeter.home"));
    Configurator.setRootLevel(Level.getLevel("DEBUG"));

    Thread.setDefaultUncaughtExceptionHandler(
        (Thread t, Throwable e) -> {
          if (!(e instanceof ThreadDeath)) {
            System.err.println("Uncaught Exception " + e + " in thread " + t +
                ". See log file for details.");//NOSONAR
          }
        });

    try {
      Method m =
          getClass().getSuperclass().getDeclaredMethod("updateClassLoader"); // , new Class<?>[] {}
      m.setAccessible(true);
      m.invoke(this, (Object[]) null);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

    // Set some (hopefully!) useful properties
    Instant now = Instant.now();
    JMeterUtils.setProperty("START.MS", Long.toString(now.toEpochMilli()));// $NON-NLS-1$
    JMeterUtils
        .setProperty("START.YMD", getFormatter("yyyyMMdd").format(now));// $NON-NLS-1$ $NON-NLS-2$
    JMeterUtils
        .setProperty("START.HMS", getFormatter("HHmmss").format(now));// $NON-NLS-1$ $NON-NLS-2$

    PluginManager.install(this, true);
    JMeterTreeModel treeModel = new JMeterTreeModel();
    JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
    final ActionRouter instance = ActionRouter.getInstance();
    instance.populateCommandMap();
    treeLis.setActionHandler(instance);
    GuiPackage.initInstance(treeLis, treeModel);
    main = new MainFrame(treeModel, treeLis);
    ComponentUtil.centerComponentInWindow(main, 80);

    instance.actionPerformed(new ActionEvent(main, 1, ActionNames.ADD_ALL));

    JTree jTree = GuiPackage.getInstance().getMainFrame().getTree();
    TreePath path = jTree.getPathForRow(0);
    jTree.setSelectionPath(path);
    FocusRequester.requestFocus(jTree);

  }

  private DateTimeFormatter getFormatter(String pattern) {
    return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
  }

  public void setVisible(boolean visible) {
    main.setVisible(visible);
  }

  public void toFront() {
    main.toFront();
  }

}
