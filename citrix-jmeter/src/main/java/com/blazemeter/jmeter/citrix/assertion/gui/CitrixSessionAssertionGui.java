package com.blazemeter.jmeter.citrix.assertion.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.testelement.TestElement;

import com.blazemeter.jmeter.citrix.assertion.CitrixSessionAssertion;
import com.blazemeter.jmeter.citrix.clause.SessionState;
import com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * GUI for {@link CitrixSessionAssertion}
 */
public class CitrixSessionAssertionGui extends AbstractAssertionGui { // NOSONAR Ignore Inheritance warning
    
    /**
     * 
     */
    private static final long serialVersionUID = 8094778924930222378L;
    private JRadioButton openState;
    private JRadioButton closedState;

    public CitrixSessionAssertionGui() {
        init();
    }

    @Override
    public String getStaticLabel() {
        return CitrixUtils.getResString(getLabelResource(), false); // $NON-NLS-1$
    }
    
    @Override
    public String getLabelResource() {
        return "citrix_assertion_session_title"; //$NON-NLS-1$
    }


    @Override
    public TestElement createTestElement() {
        CitrixSessionAssertion el = new CitrixSessionAssertion();
        modifyTestElement(el);
        return el;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement el) {
        configureTestElement(el);
        if (el instanceof CitrixSessionAssertion) {
            CitrixSessionAssertion assertion = (CitrixSessionAssertion) el;
            assertion.setExpectedState(openState.isSelected() ? SessionState.OPEN : SessionState.CLOSED);
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
        super.clearGui();

        openState.setSelected(true);
        closedState.setSelected(false);
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof CitrixSessionAssertion){
            CitrixSessionAssertion csa = (CitrixSessionAssertion) el;
            if(csa.getExpectedState() == SessionState.OPEN) {
                openState.setSelected(true);
                closedState.setSelected(false);
            } else {
                openState.setSelected(false);
                closedState.setSelected(true);
            }
        }
    }

    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
        BlazeMeterLabsLogo blazeMeterLabsLogo = new BlazeMeterLabsLogo();

        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());
        
        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(Box.createVerticalStrut(10));
        box.add(createStatePanel());
        box.add(Box.createVerticalStrut(10));
        box.add(blazeMeterLabsLogo);
        add(box, BorderLayout.NORTH);
    }

    private Component createStatePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBorder(BorderFactory.createTitledBorder(CitrixUtils.getResString("citrix_assertion_session_state_title", false))); //$NON-NLS-1$
        ButtonGroup group = new ButtonGroup();
        openState = new JRadioButton(CitrixUtils.getResString("citrix_assertion_session_open", false)); //$NON-NLS-1$
        closedState = new JRadioButton(CitrixUtils.getResString("citrix_assertion_session_closed", false)); //$NON-NLS-1$
        group.add(openState);
        group.add(closedState);
        
        JPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(openState);
        buttonPanel.add(closedState);
        
        panel.add(buttonPanel);
        return panel;
    }
}
