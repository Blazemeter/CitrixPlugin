package com.blazemeter.jmeter.citrix.listener.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.FilePanel;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
import org.apache.jorphan.gui.JLabeledTextField;

import com.blazemeter.jmeter.citrix.listener.CitrixIcaFileSaver;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * Create a GUI for {@link CitrixIcaFileSaver} test element
 *
 */
public class CitrixIcaFileSaverGui extends AbstractListenerGui implements Clearable { // NOSONAR Ignore inheritance rule

    private static final long serialVersionUID = 241L;

    private FilePanel filePanel;

    private JLabeledTextField variableName;
    
    
    

    public CitrixIcaFileSaverGui() {
        super();
        init();
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
     */
    @Override
    public String getLabelResource() {
        return "citrix_file_saver_title"; // $NON-NLS-1$
    }

    /**
     * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#getStaticLabel()
     */
    @Override
    public String getStaticLabel() {
        return CitrixUtils.getResString(getLabelResource(), false);
    }
    
    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement)
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        CitrixIcaFileSaver icaFileSaver = (CitrixIcaFileSaver) el;
        filePanel.setFilename(icaFileSaver.getSaveFolder());
        variableName.setText(icaFileSaver.getVariableName());
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    @Override
    public TestElement createTestElement() {
        CitrixIcaFileSaver icaFileSaver = new CitrixIcaFileSaver();
        modifyTestElement(icaFileSaver);
        return icaFileSaver;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement te) {
        super.configureTestElement(te);
        CitrixIcaFileSaver icaFileSaver = (CitrixIcaFileSaver) te;
        icaFileSaver.setSaveFolder(filePanel.getFilename());
        icaFileSaver.setVariableName(variableName.getText());
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
        super.clearGui();
        variableName.setText(CitrixIcaFileSaver.DEFAULT_ICA_FILE_PATH_VAR); //$NON-NLS-1$
        filePanel.setFilename(CitrixIcaFileSaver.DEFAULT_ICA_FOLDER_NAME); //$NON-NLS-1$

    }

    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(createSavePanel());
        add(box, BorderLayout.NORTH);
    }

    private Component createSavePanel() {
        filePanel = new FilePanel(CitrixUtils.getResString("citrix_file_saver_save_folder", false), true); // $NON-NLS-1$

        variableName = new JLabeledTextField(CitrixUtils.getResString("citrix_file_saver_variable", false));
        variableName.setName(CitrixIcaFileSaver.VARIABLE_NAME);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(CitrixUtils.getResString("citrix_file_saver_format", false))); //$NON-NLS-1$
        GridBagConstraints gbc = new GridBagConstraints();
        initConstraints(gbc);

        addField(panel, variableName, gbc);
        resetContraints(gbc);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        panel.add(filePanel, gbc.clone());
        resetContraints(gbc);

        return panel;
    }
    
    private void addField(JPanel panel, JLabeledTextField field, GridBagConstraints gbc) {
        List<JComponent> item = field.getComponentList();
        panel.add(item.get(0), gbc.clone());
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(item.get(1), gbc.clone());
    }

    // Next line
    private void resetContraints(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill=GridBagConstraints.NONE;
    }

    private void initConstraints(GridBagConstraints gbc) {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
    }

    @Override
    public void clearData() {
        // NOOP
    }
}
