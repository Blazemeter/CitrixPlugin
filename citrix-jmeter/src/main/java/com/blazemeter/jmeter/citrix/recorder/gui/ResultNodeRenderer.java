package com.blazemeter.jmeter.citrix.recorder.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.util.JMeterUtils;

class ResultNodeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 4159626601097711565L;
	
	private static final String ICON_SIZE = JMeterUtils.getPropDefault(JMeter.TREE_ICON_SIZE, JMeter.DEFAULT_TREE_ICON_SIZE);
	private static final ImageIcon STEPIMAGE = JMeterUtils.getImage(
            JMeterUtils.getPropDefault("viewResultsTree.success","vrt/" + ICON_SIZE + "/security-high-2.png"));

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean focus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
		this.setIcon(STEPIMAGE);
		return this;
	}
}