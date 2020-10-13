package com.blazemeter.jmeter.citrix.sampler.gui;

import com.blazemeter.jmeter.citrix.clause.gui.ClausePanel;
import com.blazemeter.jmeter.citrix.gui.BlazeMeterLabsLogo;
import com.blazemeter.jmeter.citrix.sampler.CitrixBaseSampler;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

/**
 * Base GUI for Citrix samplers.
 */
public abstract class CitrixSamplerGUI extends AbstractSamplerGui { // NOSONAR Ignore parent warning

  private static final long serialVersionUID = 8757492879770709417L;

  private ClausePanel pnlEndClause;

  public CitrixSamplerGUI() {
    super();
    initialize();
  }

  protected abstract JPanel createMainPanel();

  protected abstract CitrixBaseSampler createCitrixSampler();

  protected abstract void updateSampler(CitrixBaseSampler sampler);

  protected abstract void updateUI(CitrixBaseSampler sampler);

  protected abstract void clearUI();

  private JPanel createClausePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(
        new TitledBorder(null, CitrixUtils.getResString("sampler_end_clause_title", false),
            TitledBorder.LEADING, TitledBorder.TOP, null, null));

    pnlEndClause = new ClausePanel(true);
    panel.add(pnlEndClause, BorderLayout.NORTH);

    return panel;
  }

  private void initialize() {
    BlazeMeterLabsLogo blazeMeterLabsLogo = new BlazeMeterLabsLogo();
    setLayout(new BorderLayout());
    setBorder(makeBorder());

    Box box = Box.createVerticalBox();
    box.add(makeTitlePanel());
    box.add(Box.createVerticalStrut(10));
    box.add(createMainPanel());
    box.add(Box.createVerticalStrut(10));
    box.add(createClausePanel());
    box.add(Box.createVerticalStrut(10));
    box.add(blazeMeterLabsLogo);
    add(box, BorderLayout.NORTH);
  }

  @Override
  public final TestElement createTestElement() {
    CitrixBaseSampler el = createCitrixSampler();
    modifyTestElement(el);
    return el;
  }

  protected void stopEditing() {
    // NOOP
  }

  @Override
  public final void modifyTestElement(TestElement element) {
    stopEditing();
    configureTestElement(element);
    CitrixBaseSampler sampler = (CitrixBaseSampler) element;
    updateSampler(sampler);
    pnlEndClause.updateClause();
    sampler.setEndClause(pnlEndClause.getClause());
  }

  @Override
  public final void configure(TestElement el) {
    super.configure(el);
    CitrixBaseSampler sampler = (CitrixBaseSampler) el;
    updateUI(sampler);
    pnlEndClause.setClause(sampler.getEndClause());
  }

  // should never be used as we override getStaticLabel
  @Override
  public final String getLabelResource() {
    return "interface_title"; //$NON-NLS-1$
  }

  @Override
  public final void clearGui() {
    super.clearGui();
    stopEditing();
    clearUI();
    pnlEndClause.setClause(null);
  }

}
