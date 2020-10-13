package com.blazemeter.jmeter.citrix.sampler.gui;

import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler;
import com.blazemeter.jmeter.citrix.sampler.StartApplicationSampler;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * GUI for ApplicationStartedSampler class.
 */
public class StartApplicationSamplerGUI extends CitrixSamplerGUI { // NOSONAR Ignore parent warning

  public static final String RSC_TITLE = "start_application_sampler_gui_title";
  private static final long serialVersionUID = -7106863979451551913L;
  private JTextField tfICAPathVar;

  protected final JPanel createMainPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());

    tfICAPathVar = new JTextField(25);
    GuiHelper
        .addLabeledComponent(tfICAPathVar, "start_application_sampler_gui_ica_path_var", panel);

    return panel;
  }

  @Override
  public String getStaticLabel() {
    return CitrixUtils.getResString(RSC_TITLE, false);
  }

  @Override
  protected CitrixBaseSampler createCitrixSampler() {
    return new StartApplicationSampler();
  }

  @Override
  protected void updateSampler(CitrixBaseSampler sampler) {
    StartApplicationSampler startSampler = (StartApplicationSampler) sampler;
    startSampler.setICAPathVar(tfICAPathVar.getText());
  }

  @Override
  protected void updateUI(CitrixBaseSampler sampler) {
    StartApplicationSampler startSampler = (StartApplicationSampler) sampler;
    tfICAPathVar.setText(startSampler.getICAPathVar());
  }

  @Override
  protected void clearUI() {
    tfICAPathVar.setText("");
  }

}
