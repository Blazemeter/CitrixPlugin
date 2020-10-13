package com.blazemeter.jmeter.citrix.assertion.gui;

import com.blazemeter.jmeter.citrix.assertion.CitrixAssertion;
import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.gui.ClausePanel;
import com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.BorderLayout;
import javax.swing.Box;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.testelement.TestElement;

/**
 * GUI for {@link CitrixAssertion}.
 */
public class CitrixAssertionGUI extends AbstractAssertionGui { // NOSONAR Ignore Inheritance warning
  private static final long serialVersionUID = 1686121565577093423L;

  private ClausePanel pnlClause;

  public CitrixAssertionGUI() {
    init();
  }

  private void init() { // WARNING: called from ctor so must not be overridden
    // (i.e. must be private or final)
    BlazeMeterLabsLogo blazeMeterLabsLogo = new BlazeMeterLabsLogo();

    setLayout(new BorderLayout(0, 10));
    setBorder(makeBorder());

    Box box = Box.createVerticalBox();
    box.add(makeTitlePanel());
    box.add(Box.createVerticalStrut(10));
    pnlClause = new ClausePanel(CheckType.ASSERTION_CHECKS, false);
    pnlClause.setTimeoutVisible(false);
    box.add(pnlClause);
    box.add(Box.createVerticalStrut(10));
    box.add(blazeMeterLabsLogo);
    add(box, BorderLayout.NORTH);
  }

  @Override
  public String getStaticLabel() {
    return CitrixUtils.getResString(getLabelResource(), false); // $NON-NLS-1$
  }

  @Override
  public String getLabelResource() {
    return "citrix_assertion_title"; //$NON-NLS-1$
  }

  @Override
  public TestElement createTestElement() {
    CitrixAssertion el = new CitrixAssertion();
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
    CitrixAssertion assertion = (CitrixAssertion) el;
    pnlClause.updateClause();
    assertion.setClause(pnlClause.getClause());
  }

  /**
   * A newly created component can be initialized with the contents of a Test
   * Element object by calling this method. The component is responsible for
   * querying the Test Element object for the relevant information to display in
   * its GUI.
   *
   * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement)
   */
  @Override
  public void configure(TestElement el) {
    super.configure(el);
    CitrixAssertion assertion = (CitrixAssertion) el;
    pnlClause.setClause(assertion.getClause());
  }

  /**
   * Implements JMeterGUIComponent.clearGui.
   */
  @Override
  public void clearGui() {
    super.clearGui();
    pnlClause.setClause(CitrixAssertion.createDefaultClause());
  }

}
