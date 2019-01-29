package com.blazemeter.jmeter.citrix.clause.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.assertion.CitrixAssertion;
import com.blazemeter.jmeter.citrix.assertion.gui.CitrixAssertionGUI;
import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.blazemeter.jmeter.citrix.clause.ClauseComputationException;
import com.blazemeter.jmeter.citrix.clause.ClauseHelper;
import com.blazemeter.jmeter.citrix.gui.ImagePanel;
import com.blazemeter.jmeter.citrix.gui.PositionPanel;
import com.blazemeter.jmeter.citrix.gui.SelectionPanel;
import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import com.blazemeter.jmeter.citrix.utils.DialogHelper;

public class ClauseBuilderPanel extends JPanel implements ActionListener, DragGestureListener {

	private static final long serialVersionUID = 6084133449821079931L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClauseBuilderPanel.class);

	private static final String DATAFLAVOR_TYPE = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
			+ JMeterTreeNode[].class.getName() + "\"";

	private static final DataFlavor[] DATAFLAVORS;

	private static final Cursor DND_CURSOR;
	private static final ImageIcon DND_ICON;

	private Clause clause;

	private ImagePanel pnlImage;
	private PositionPanel pnlPosition;
	private SelectionPanel pnlSelection;
	private JTextArea taClauseValue;
	private JLabel lblDrag;
	private ButtonGroup btngCheckTypes;
	private JCheckBox chbRelative;

	private Point lastPosition;

	private final Set<CheckType> checkTypes = EnumSet.copyOf(CheckType.ASSERTION_CHECKS);

	static {
		// Load custom cursor for drag n drop
		Cursor cursor = null;
		ImageIcon icon = null;
		try (InputStream stream = ClauseBuilderPanel.class
				.getResourceAsStream("/com/blazemeter/jmeter/citrix/cursors/dnd_clause.png")) {
			icon = new ImageIcon(ImageIO.read(stream));
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), new Point(), "dnd_clause");
		} catch (IOException ex) {
			LOGGER.error("Unable to load custom drag n drop cursor: {}", ex.getMessage(), ex);
		}
		DND_ICON = icon;
		DND_CURSOR = cursor;

		DataFlavor dataFlavor = null;
		try {
			dataFlavor = new DataFlavor(DATAFLAVOR_TYPE);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Assertion dragging unavailable.", e);
		}
		if (dataFlavor != null) {
			DATAFLAVORS = new DataFlavor[] { dataFlavor };
		} else {
			DATAFLAVORS = new DataFlavor[0];
		}
	}

	public Clause getClause() {
		return clause;
	}

	public BufferedImage getImage() {
		return pnlImage.getImage();
	}

	/**
	 * <p>
	 * Defines the image to display.
	 * </p>
	 * 
	 * @param image the image to display
	 */
	public void setImage(BufferedImage image) {
		pnlImage.setImage(image);
	}

	public boolean isDragAreaVisible() {
		return lblDrag.isVisible();
	}

	public void setDragAreaVisible(boolean visible) {
		lblDrag.setVisible(visible);
	}

	public Set<CheckType> getCheckTypes() {
		return checkTypes;
	}

	public void setCheckTypes(Set<CheckType> checkTypes) {
		if (checkTypes == null) {
			throw new IllegalArgumentException("checkTypes cannot be null.");
		}
		checkTypes.retainAll(CheckType.ASSERTION_CHECKS);

		this.checkTypes.clear();
		this.checkTypes.addAll(checkTypes);

		Enumeration<AbstractButton> buttonEnum = btngCheckTypes.getElements();
		while (buttonEnum.hasMoreElements()) {
			AbstractButton currentButton = buttonEnum.nextElement();
			currentButton.setVisible(
					this.checkTypes.contains(Enum.valueOf(CheckType.class, currentButton.getActionCommand())));
		}

	}

	public Rectangle getSelection() {
		return pnlImage.getSelection();
	}

	public void setSelection(Rectangle selection, boolean relative) {
		Rectangle absoluteSelection = ClauseHelper.getAbsoluteSelection(selection, relative, pnlImage.getHighlight());
		pnlImage.setHighlightVisible(relative);
		pnlImage.setSelection(absoluteSelection);

	}

	public Rectangle getFgWindowArea() {
		return pnlImage.getHighlight();
	}

	public void setFgWindowArea(Rectangle area) {
		pnlImage.setHightlight(area);
	}

	public ClauseBuilderPanel() {
		this.setLayout(new BorderLayout());
		initialize();
	}

	private void triggerClauseChanged() {
		ClauseChangedListener[] listeners = listenerList.getListeners(ClauseChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onClauseChanged(new ClauseChangedEvent(this, clause));
		}
	}

	private void updateClause() {
		ButtonModel model = btngCheckTypes.getSelection();
		if (model != null) {
			CheckType checkType = Enum.valueOf(CheckType.class, model.getActionCommand());
			String expectedValue = taClauseValue.getText();
			Rectangle selection = pnlSelection.getSelection();
			clause = new Clause(checkType, expectedValue, false, selection);
			clause.setRelative(chbRelative.isSelected());
		} else {
			clause = null;
		}
		lblDrag.setEnabled(clause != null);
		triggerClauseChanged();
	}

	private JPanel createToolbar() {
		JPanel pnlToolbar = new JPanel(new GridBagLayout());

		chbRelative = new JCheckBox(CitrixUtils.getResString("clause_builder_relative", false));
		chbRelative.addActionListener(e -> {
			pnlImage.setHighlightVisible(chbRelative.isSelected());
			if (pnlImage.hasVisibleHighlight()) {
				pnlImage.scrollRectToVisible(pnlImage.getHighlight());
			}
			updatePositionInToolbar(lastPosition);
			updateSelectionInToolbar(pnlImage.getSelection());
		});
		chbRelative.setVisible(pnlImage.getHighlight() != null);
		chbRelative.setSelected(pnlImage.isHighlightVisible());
		GridBagConstraints gbcRelative = new GridBagConstraints();
		gbcRelative.fill = GridBagConstraints.HORIZONTAL;
		gbcRelative.anchor = GridBagConstraints.LINE_START;
		gbcRelative.gridwidth = GridBagConstraints.REMAINDER;
		pnlToolbar.add(chbRelative, gbcRelative);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;

		lblDrag = new JLabel(CitrixUtils.getResString("clause_builder_drag", false), DND_ICON, SwingConstants.CENTER);
		lblDrag.setBorder(BorderFactory.createEtchedBorder());
		lblDrag.setEnabled(false);
		lblDrag.setMinimumSize(new Dimension(100, 50));
		pnlToolbar.add(lblDrag, gbc.clone());

		pnlPosition = new PositionPanel();
		pnlPosition.setBorder(new TitledBorder(null, CitrixUtils.getResString("clause_builder_position", false),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlPosition.setEditable(false);
		pnlPosition.setMinimumSize(new Dimension(200, 50));
		pnlToolbar.add(pnlPosition, gbc.clone());

		pnlSelection = new SelectionPanel();
		pnlSelection.setBorder(new TitledBorder(null, CitrixUtils.getResString("clause_builder_selection", false),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlSelection.setEditable(false);
		pnlSelection.setPreferredSize(new Dimension(300, 70));
		pnlSelection.addSelectionChangedListener(e -> updateClause());
		pnlSelection.setMinimumSize(new Dimension(400, 50));
		pnlToolbar.add(pnlSelection, gbc.clone());

		JPanel pnlButtons = new JPanel(new GridBagLayout());
		pnlButtons.setMinimumSize(new Dimension(100, 50));
		pnlToolbar.add(pnlButtons, gbc.clone());
		GridBagConstraints gbcButton = new GridBagConstraints();
		gbcButton.fill = GridBagConstraints.HORIZONTAL;
		gbcButton.gridwidth = GridBagConstraints.REMAINDER;

		btngCheckTypes = new ButtonGroup();

		for (CheckType checkType : CheckType.ASSERTION_CHECKS) {
			JToggleButton button = new JToggleButton(
					CitrixUtils.getResString("clause_builder_cmd_" + checkType.name().toLowerCase(), false));
			button.setActionCommand(checkType.name());
			button.addActionListener(this);
			btngCheckTypes.add(button);
			pnlButtons.add(button, gbcButton);
		}

		taClauseValue = new JTextArea(3, 32);
		taClauseValue.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(taClauseValue);
		pnlToolbar.add(scrollPane, gbc.clone());

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(lblDrag, DnDConstants.ACTION_MOVE, this);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(pnlToolbar, BorderLayout.WEST);
		return wrapper;
	}

	private void updateSelectionInToolbar(Rectangle selection) {
		// Compute offset for relative clause
		if (selection != null && pnlImage.hasVisibleHighlight()) {
			selection = new Rectangle(selection);
			Rectangle fgWindowArea = pnlImage.getHighlight();
			selection.translate(-fgWindowArea.x, -fgWindowArea.y);
		}
		pnlSelection.setSelection(selection);
	}

	private void updatePositionInToolbar(Point position) {
		// Compute offset for relative clause
		if (position != null && pnlImage.hasVisibleHighlight()) {
			position = new Point(position);
			Rectangle fgWindowArea = pnlImage.getHighlight();
			position.translate(-fgWindowArea.x, -fgWindowArea.y);
		}
		pnlPosition.setPosition(position);
	}

	private void initialize() {
		pnlImage = new ImagePanel();
		pnlImage.setAutoscrolls(true);
		pnlImage.addImageHoverListener(e -> {
			lastPosition = e.getPoint();
			updatePositionInToolbar(lastPosition);
		});
		pnlImage.addSelectionChangedListener(e -> {
			updateSelectionInToolbar(e.getArea());
			btngCheckTypes.clearSelection();
			taClauseValue.setText("");
			updateClause();
		});
		pnlImage.addHighlightChangedListener(e -> {
			chbRelative.setVisible(e.getArea() != null);
			updatePositionInToolbar(lastPosition);
			updateSelectionInToolbar(pnlImage.getSelection());
		});
		pnlImage.addPropertyChangeListener(e -> {
			if (ImagePanel.HIGHLIGHT_VISIBLE_PROP_NAME.equals(e.getPropertyName())) {
				chbRelative.setSelected(Boolean.TRUE.equals(e.getNewValue()));
			}
		});
		pnlImage.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				pnlImage.scrollRectToVisible(r);
			}
		});

		JPanel pnlToolbar = createToolbar();
		add(pnlToolbar, BorderLayout.NORTH);
		JScrollPane scrollScreenshotPane = new JScrollPane(pnlImage);
		scrollScreenshotPane.setPreferredSize(new Dimension(800, 600));
		add(scrollScreenshotPane, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CheckType checkType = Enum.valueOf(CheckType.class, e.getActionCommand());
		try {
			String value = checkType.assess(pnlImage.getImage(), pnlImage.getSelection());
			taClauseValue.setText(value);
		} catch (ClauseComputationException ex) {
			DialogHelper.showError(CitrixUtils.getResString("clause_builder_computation_error", false));
			taClauseValue.setText("");
		}
		updateClause();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		if (clause != null) {
			dge.startDrag(DND_CURSOR, new Transferable() {

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return DATAFLAVORS[0].equals(flavor);
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return DATAFLAVORS;
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					if (!isDataFlavorSupported(flavor)) {
						throw new UnsupportedFlavorException(flavor);
					}

					// Build assertion from clause property
					CitrixAssertion assertion = new CitrixAssertion();
					assertion.setProperty(TestElement.TEST_CLASS, CitrixAssertion.class.getName());
					assertion.setProperty(TestElement.GUI_CLASS, CitrixAssertionGUI.class.getName());
					assertion.setName(
							MessageFormat.format(CitrixUtils.getResString("clause_builder_new_assertion_fmt", false),
									clause.getCheckType()));
					assertion.setClause(clause);

					return new JMeterTreeNode[] {
							new JMeterTreeNode(assertion, GuiPackage.getInstance().getTreeModel()) };
				}
			});
		}
	}

	public void addClauseChangedListener(ClauseChangedListener listener) {
		listenerList.add(ClauseChangedListener.class, listener);
	}

	public void removeClauseChangedListener(ClauseChangedListener listener) {
		listenerList.remove(ClauseChangedListener.class, listener);
	}

	public static interface ClauseChangedListener extends EventListener {
		void onClauseChanged(ClauseChangedEvent event);
	}

	public static class ClauseChangedEvent extends EventObject {

		private static final long serialVersionUID = 5042634902733713372L;

		private final Clause clause;

		public ClauseChangedEvent(Object source, Clause clause) {
			super(source);
			this.clause = clause;
		}

		public Clause getClause() {
			return clause;
		}
	}

}
