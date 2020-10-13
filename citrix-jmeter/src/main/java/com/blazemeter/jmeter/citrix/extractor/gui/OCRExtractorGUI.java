package com.blazemeter.jmeter.citrix.extractor.gui;

import com.blazemeter.jmeter.citrix.extractor.OCRExtractor;
import com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo;
import com.blazemeter.jmeter.citrix.gui.GuiHelper;
import com.blazemeter.jmeter.citrix.gui.SelectionPanel;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;

public class OCRExtractorGUI extends AbstractPostProcessorGui {

  private static final long serialVersionUID = 6943041278226836672L;

  private SelectionPanel pnlSelection;
  private JCheckBox chbRelative;
  private JTextField tfRefName;
  private JTextField tfDefaultValue;
  private JCheckBox chbEmpty;

  public OCRExtractorGUI() {
    init();
  }

  private void init() {
    BlazeMeterLabsLogo blazeMeterLabsLogo = new BlazeMeterLabsLogo();

    setLayout(new BorderLayout(0, 10));
    setBorder(makeBorder());

    Box box = Box.createVerticalBox();
    box.add(makeTitlePanel());
    box.add(Box.createVerticalStrut(10));
    box.add(createMainPanel());
    box.add(Box.createVerticalStrut(10));
    box.add(blazeMeterLabsLogo);
    add(box, BorderLayout.NORTH);
  }

  private JPanel createMainPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    Insets insets = new Insets(2, 0, 2, 0);

    JPanel pnlArea = new JPanel();
    pnlArea.setLayout(new GridBagLayout());

    chbRelative = new JCheckBox(CitrixUtils.getResString("ocr_extractor_relative", false));
    GridBagConstraints gbcRelative = new GridBagConstraints();
    gbcRelative.anchor = GridBagConstraints.LINE_START;
    gbcRelative.gridwidth = GridBagConstraints.REMAINDER;
    pnlArea.add(chbRelative, gbcRelative);

    pnlSelection = new SelectionPanel();
    GridBagConstraints gbcSelection = new GridBagConstraints();
    gbcSelection.anchor = GridBagConstraints.LINE_START;
    gbcSelection.fill = GridBagConstraints.HORIZONTAL;
    gbcSelection.gridwidth = GridBagConstraints.REMAINDER;
    gbcSelection.weightx = 1d;
    pnlArea.add(pnlSelection, gbcSelection);

    GuiHelper.addLabeledComponent(pnlArea, "ocr_extractor_selection", panel, insets);

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

    chbEmpty = new JCheckBox(CitrixUtils.getResString("ocr_extractor_empty_default_value", false));
    panel.add(chbEmpty, BorderLayout.CENTER);

    return panel;
  }

  @Override
  public String getStaticLabel() {
    return CitrixUtils.getResString(getLabelResource(), false);
  }

  @Override
  public String getLabelResource() {
    return "ocr_extractor_title";
  }

  @Override
  public TestElement createTestElement() {
    OCRExtractor el = new OCRExtractor();
    modifyTestElement(el);
    return el;
  }

  @Override
  public void modifyTestElement(TestElement element) {
    configureTestElement(element);
    OCRExtractor extractor = (OCRExtractor) element;
    pnlSelection.updateSelection();
    extractor.setRelative(chbRelative.isSelected());
    extractor.setSelection(pnlSelection.getSelection());
    extractor.setDefaultValue(tfDefaultValue.getText());
    extractor.setEmptyDefaultValue(chbEmpty.isSelected());
    extractor.setRefName(tfRefName.getText());
  }

  @Override
  public void configure(TestElement element) {
    super.configure(element);
    OCRExtractor extractor = (OCRExtractor) element;
    chbRelative.setSelected(extractor.isRelative());
    pnlSelection.setSelection(extractor.getSelection());
    tfDefaultValue.setText(extractor.getDefaultValue());
    chbEmpty.setSelected(extractor.isDefaultValueEmpty());
    tfRefName.setText(extractor.getRefName());
  }

  @Override
  public void clearGui() {
    super.clearGui();
    chbRelative.setSelected(false);
    pnlSelection.setSelection(null);
    tfDefaultValue.setText("");
    chbEmpty.setSelected(false);
    tfRefName.setText("");
  }
}
