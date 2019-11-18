package com.blazemeter.jmeter.citrix.extractor.gui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;

import com.blazemeter.jmeter.citrix.extractor.AssessmentExtractor;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

public class AssessmentExtractorGUI extends AbstractPostProcessorGui {

	private static final long serialVersionUID = 2475156768635414523L;
	
	private JTextField tfRefName;
	private JTextField tfDefaultValue;
	private JCheckBox chbEmpty;

	public AssessmentExtractorGUI() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout(0, 10));
		setBorder(makeBorder());

		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		box.add(Box.createVerticalStrut(10));
		box.add(createMainPanel());
		add(box, BorderLayout.NORTH);
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(2, 0, 2, 0);
		
		tfRefName = new JTextField();
		GuiHelper.addLabeledComponent(tfRefName, "ref_name_field", panel, true, insets);

		JPanel pnlDefaultVar = createDefaultValuePanel();
		GuiHelper.addLabeledComponent(pnlDefaultVar, "default_value_field", panel, true, insets);

		return panel;
	}

	private JPanel createDefaultValuePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		tfDefaultValue = new JTextField(20);
		panel.add(tfDefaultValue, BorderLayout.WEST);

		chbEmpty = new JCheckBox(CitrixUtils.getResString("assessment_extractor_empty_default_value", false));
		panel.add(chbEmpty, BorderLayout.CENTER);

		return panel;
	}

	@Override
	public String getStaticLabel() {
		return CitrixUtils.getResString(getLabelResource(), false);
	}

	@Override
	public String getLabelResource() {
		return "assessment_extractor_title";
	}

	@Override
	public TestElement createTestElement() {
		AssessmentExtractor el = new AssessmentExtractor();
		modifyTestElement(el);
		return el;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		configureTestElement(element);
		AssessmentExtractor extractor = (AssessmentExtractor) element;
		extractor.setDefaultValue(tfDefaultValue.getText());
		extractor.setEmptyDefaultValue(chbEmpty.isSelected());
		extractor.setRefName(tfRefName.getText());
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		AssessmentExtractor extractor = (AssessmentExtractor) element;
		tfDefaultValue.setText(extractor.getDefaultValue());
		chbEmpty.setSelected(extractor.isDefaultValueEmpty());
		tfRefName.setText(extractor.getRefName());
	}

	@Override
	public void clearGui() {
		super.clearGui();
		tfDefaultValue.setText("");
		chbEmpty.setSelected(false);
		tfRefName.setText("");
	}
}
