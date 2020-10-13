package com.blazemeter.jmeter.citrix.recorder.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.util.JMeterUtils;

/**
 * Class that associate a JLabel, JTextField and a browser JButton.
 * Used on CitrixRecorderGUI.
 */
public class BrowseFile extends HorizontalPanel implements ActionListener {
  private static final long serialVersionUID = 280L;

  private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");

  private static final Font FONT_SMALL =
      new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));

  private static final String ACTION_BROWSE = "browse";

  private final JTextField filename = new JTextField();

  private final JLabel label;

  private final JButton browse = new JButton(JMeterUtils.getResString(ACTION_BROWSE));

  private final String[] filetypes;

  private final boolean onlyDirectories;

  public BrowseFile(String label) {
    this(label, null);
  }

  public BrowseFile(String label, boolean onlyDirectories, String... exts) {
    this(label, onlyDirectories, null, exts);
  }

  public BrowseFile(String label, ChangeListener listener, String... exts) {
    this(label, false, listener, exts);
  }

  public BrowseFile(String label, boolean onlyDirectories, ChangeListener listener,
                    String... exts) {
    this.label = new JLabel(label);
    if (listener != null) {
      List<ChangeListener> listeners = new LinkedList<>();
      listeners.add(listener);
    }
    if (exts != null &&
        !(exts.length == 1 &&
            exts[0] == null) // String null is converted to String[]{null} NOSONAR it's not code
    ) {
      this.filetypes = new String[exts.length];
      System.arraycopy(exts, 0, this.filetypes, 0, exts.length);
    } else {
      this.filetypes = null;
    }
    this.onlyDirectories = onlyDirectories;
    init();
  }

  private void init() { // WARNING: called from ctor so must not be overridden
    // (i.e. must be private or final)
    add(label);
    add(filename);
    filename.addActionListener(this);
    browse.setFont(FONT_SMALL);
    add(browse);
    browse.setActionCommand(ACTION_BROWSE);
    browse.addActionListener(this);
  }

  public List<JComponent> getComponent() {
    ArrayList<JComponent> components = new ArrayList<>();
    components.add(label);
    components.add(filename);
    components.add(browse);
    return components;
  }

  public void clearGui() {
    filename.setText("");
  }

  public String getFilename() {
    return filename.getText();
  }

  public void setFilename(String f) {
    filename.setText(f);
  }

  public void setBrowseButtonlabel(String label) {
    browse.setText(label);
  }

  public String getSelectedFile() {
    return filename.getText();
  }

  public void setSelectedFile(String file) {
    filename.setText(file);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(ACTION_BROWSE)) {
      JFileChooser chooser;
      if (filetypes == null || filetypes.length == 0) {
        chooser = FileDialoger.promptToOpenFile(filename.getText(), onlyDirectories);
      } else {
        chooser = FileDialoger.promptToOpenFile(filetypes, filename.getText(), onlyDirectories);
      }
      if (chooser != null && chooser.getSelectedFile() != null) {
        filename.setText(chooser.getSelectedFile().getPath());
      }
    }
  }
}
