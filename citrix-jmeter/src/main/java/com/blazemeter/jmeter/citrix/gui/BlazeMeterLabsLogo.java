package com.blazemeter.jmeter.citrix.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlazeMeterLabsLogo extends JPanel {

  private static final long serialVersionUID = -2926310622121776666L;
  private static final Logger LOG = LoggerFactory
      .getLogger(com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo.class);
  private static final ImageIcon DEFAULT_LOGO = new ImageIcon(
      BlazeMeterLabsLogo.class.getResource("/com/blazemeter/labs-logo/blazemeter-labs-logo.png"));
  private static final ImageIcon DARK_LOGO = new ImageIcon(
      BlazeMeterLabsLogo.class
          .getResource("/com/blazemeter/labs-logo/blazemeter-labs-light-logo.png"));
  private static final String URL_TO_BROWSE = "https://github.com/Blazemeter/CitrixPlugin";

  private final JLabel blazeMeterLabsLogo;

  public BlazeMeterLabsLogo() {
    blazeMeterLabsLogo = new JLabel(DEFAULT_LOGO);
    setBrowseOnClick(URL_TO_BROWSE);
    GroupLayout blazeMeterLabsLogoLayout = new GroupLayout(this);
    setLayout(blazeMeterLabsLogoLayout);
    blazeMeterLabsLogoLayout.setHorizontalGroup(blazeMeterLabsLogoLayout
        .createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(blazeMeterLabsLogo));
    blazeMeterLabsLogoLayout.setVerticalGroup(blazeMeterLabsLogoLayout
        .createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(blazeMeterLabsLogo));
  }

  @Override
  public void paint(Graphics g) {
    blazeMeterLabsLogo
        .setIcon("Darcula".equals(UIManager.getLookAndFeel().getID()) ? DARK_LOGO : DEFAULT_LOGO);
    super.paint(g);
  }

  private void setBrowseOnClick(String url) {
    blazeMeterLabsLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    blazeMeterLabsLogo.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(new URI(url));
          } catch (IOException | URISyntaxException exception) {
            LOG.error("Problem when accessing repository", exception);
          }
        }
      }
    });
  }
}
