package com.blazemeter.jmeter.citrix.recorder.gui;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

public class RecorderDialog extends JDialog implements ItemListener, KeyListener, ActionListener {

  public RecorderDialog() {
    super((JFrame) null, CitrixUtils.getResString("recorder_toolbar_title", false),
        false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setResizable(false);
    init();
  }

  @Override
  protected JRootPane createRootPane() {
    return new JRootPane();
  }

  private void init() {
    this.getContentPane().setLayout(new BorderLayout(10, 10));
    ((JPanel) this.getContentPane())
        .setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    this.pack();
    this.setLocation(5, 10);
    this.getContentPane().requestFocusInWindow();
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    setAlwaysOnTop(b);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    // NOOP
  }

  public void setTopRightLocation() {
    Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
        .getDefaultConfiguration().getBounds();
    int padding = 10; // for cognitive aid, not aligning the window with the background window
    this.setLocation((int) ((bounds.getWidth() - this.getWidth()) - padding), padding);
    this.validate();
    this.repaint();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyPressed(KeyEvent e) {
    // NOOP
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyTyped(KeyEvent e) {
    // NOOP
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyReleased(KeyEvent e) {
    // NOOP
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    // NOOP
  }
}
