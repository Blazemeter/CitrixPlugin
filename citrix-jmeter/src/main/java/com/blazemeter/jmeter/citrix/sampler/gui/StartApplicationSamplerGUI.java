package com.blazemeter.jmeter.citrix.sampler.gui;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler;
import com.blazemeter.jmeter.citrix.sampler.StartApplicationSampler;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * GUI for ApplicationStartedSampler class.
 */
public class StartApplicationSamplerGUI extends CitrixSamplerGUI { // NOSONAR Ignore parent warning

	private static final long serialVersionUID = -7106863979451551913L;

	public static final String RSC_TITLE = "start_application_sampler_gui_title";

	private JTextField tfICAPathVar;
	private JTextField tfLogonTimeout;

	protected final JPanel createMainPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		tfICAPathVar = new JTextField(25);
		GuiHelper.addLabeledComponent(tfICAPathVar, "start_application_sampler_gui_ica_path_var", panel);
		
		tfLogonTimeout = new JTextField(6);
		GuiHelper.addLabeledComponent(tfLogonTimeout, "start_application_sampler_gui_logon_timeout", panel);

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
		startSampler.setLogOnTimeout(tfLogonTimeout.getText());
	}

	@Override
	protected void updateUI(CitrixBaseSampler sampler) {
		StartApplicationSampler startSampler = (StartApplicationSampler) sampler;
		tfICAPathVar.setText(startSampler.getICAPathVar());
		tfLogonTimeout.setText(startSampler.getLogOnTimeoutAsString());
	}

	@Override
	protected void clearUI() {
		tfICAPathVar.setText("");
		tfLogonTimeout.setText("");
	}

}
