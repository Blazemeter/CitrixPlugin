package com.blazemeter.jmeter.citrix.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.Objects;

import javax.swing.JPanel;

import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;

/**
 * Panel that contains a screenshot within the user can select a rectangle area.
 */
public class ImagePanel extends JPanel implements SelectionChangedSubject {

	private static final long serialVersionUID = 7191970450803599004L;

	public final static String HIGHLIGHT_VISIBLE_PROP_NAME = "highlightVisible";
	  
	private static final Logger LOG = LoggerFactory.getLogger(ImagePanel.class);

	private static final Color DEFAULT_SELECTION_COLOR = getColorFromProperty(
			JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "selection_color", "0,255,0"));

	private static final Color DEFAULT_HIGHLIGHT_COLOR = getColorFromProperty(
			JMeterUtils.getPropDefault(CitrixUtils.PROPERTIES_PFX + "hightlight_color", "255,0,0"));

	private final transient SelectionChangedManager selectionChangedManager = new SelectionChangedManager(listenerList);

	private transient BufferedImage image;
	private Rectangle imageBounds;

	private Rectangle selection;
	private Color selectionColor = DEFAULT_SELECTION_COLOR;

	private Rectangle highlight;
	private Color highlightColor = DEFAULT_HIGHLIGHT_COLOR;
	private boolean highlightVisible = true;

	private Point mouseStartPos;

	private static Color getColorFromProperty(String colorRGB) {
		try {
			String[] colors = colorRGB.split(",");
			return new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
		} catch (NumberFormatException ex) {
			LOG.error("Error parsing value {} for property {}, defaulting to {}", colorRGB,
					CitrixUtils.PROPERTIES_PFX + "selection_color", Color.GREEN, ex);
			return Color.GREEN;
		}
	}

	private static Rectangle normalizeArea(Rectangle area) {
		Rectangle result = null;
		if (area != null) {
			result = new Rectangle(area);
			if (result.width < 0) {
				result.width = -result.width;
				result.x -= result.width;
			}

			if (result.height < 0) {
				result.height = -result.height;
				result.y -= result.height;
			}
		}
		return result;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		if (this.image != image) {
			this.image = image;
			if (image != null) {
				this.imageBounds = new Rectangle(0, 0, image.getWidth(), image.getHeight());
				this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			} else {
				this.imageBounds = null;
			}
			repaint();
		}
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color color) {
		this.selectionColor = color;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color color) {
		this.highlightColor = color;
	}

	public Rectangle getSelection() {
		// return copy to protect mutable property
		return selection != null ? new Rectangle(selection) : null;
	}

	private void setSelection(Rectangle selection, boolean normalize) {
		if (normalize) {
			selection = normalizeArea(selection);
		}
		if (!Objects.equals(this.selection, selection)) {
			Rectangle oldSelection = this.selection;
			this.selection = selection;
			if (oldSelection != null) {
				repaint(oldSelection.getBounds());
			}
			if (selection != null) {
				repaint(selection.getBounds());
			}
			selectionChangedManager.trigger(selection);
		}
	}

	public void setSelection(Rectangle selection) {
		setSelection(selection, true);
	}

	public Rectangle getHighlight() {
		// Return copy to protect mutable property
		return highlight != null ? new Rectangle(highlight) : null;
	}

	public void setHightlight(Rectangle highlight) {
		highlight = normalizeArea(highlight);
		if (!Objects.equals(highlight, this.highlight)) {
			Rectangle oldHighlight = this.highlight;
			this.highlight = highlight;
			if (highlightVisible) {
				if (oldHighlight != null) {
					repaint(oldHighlight.getBounds());
				}
				if (this.highlight != null) {
					repaint(this.highlight.getBounds());
				}
			}
			triggerHighlightChanged();
		}
	}

	public boolean isHighlightVisible() {
		return highlightVisible;
	}

	public void setHighlightVisible(boolean visible) {
		if (this.highlightVisible != visible) {
			this.highlightVisible = visible;
			if (this.highlight != null) {
				repaint(this.highlight.getBounds());
			}
			firePropertyChange(HIGHLIGHT_VISIBLE_PROP_NAME, !visible, visible);
		}
	}

	public boolean hasVisibleHighlight() {
		return highlightVisible && highlight != null;
	}

	/**
	 * Instantiate the screenshot in the panel and set its events handlers.
	 *
	 */
	public ImagePanel() {
		init();
	}

	private void triggerImageHover(MouseEvent event) {
		ImageHoverListener[] listeners = listenerList.getListeners(ImageHoverListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onImageHover(event);
		}
	}

	private void triggerHighlightChanged() {
		HighlightChangedListener[] listeners = listenerList.getListeners(HighlightChangedListener.class);
		AreaChangedEvent event = new AreaChangedEvent(this, highlight != null ? new Rectangle(highlight) : null);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onHighlightChanged(event);
		}
	}

	private Rectangle constraintArea(Rectangle area) {
		Rectangle result = null;
		if (area != null && imageBounds != null) {
			result = area.intersection(imageBounds);
		}
		return result;
	}

	private void init() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseStartPos = e.getPoint();
				setSelection(null);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseStartPos = null;
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (mouseStartPos != null) {
					Rectangle mouseBox = new Rectangle(mouseStartPos,
							new Dimension(e.getX() - mouseStartPos.x, e.getY() - mouseStartPos.y));
					mouseBox = normalizeArea(mouseBox);
					setSelection(constraintArea(mouseBox), false);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (imageBounds != null && imageBounds.contains(e.getPoint())) {
					triggerImageHover(e);
				}
			}
		});

	}

	/**
	 * Paint the selected rectangle in green if the selection is good (means within
	 * the screenshot). Else, the rectangle is not valid and is painted in red.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;

		// Paint image
		graphics.drawImage(image, 0, 0, null);

		// Paint highlight
		if (highlight != null && highlightVisible) {
			graphics.setPaint(highlightColor);
			graphics.drawRect(highlight.x, highlight.y, highlight.width - 1, highlight.height - 1);
		}

		// Paint selection
		if (selection != null) {
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
			graphics.setPaint(selectionColor);
			graphics.fill(selection);
		}

	}

	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		selectionChangedManager.addSelectionChangedListener(listener);
	}

	@Override
	public void removeSelectionChangedListener(SelectionChangedListener listener) {
		selectionChangedManager.removeSelectionChangedListener(listener);
	}

	public void addImageHoverListener(ImageHoverListener listener) {
		listenerList.add(ImageHoverListener.class, listener);
	}

	public void removeImageHoverListener(ImageHoverListener listener) {
		listenerList.remove(ImageHoverListener.class, listener);
	}

	public void addHighlightChangedListener(HighlightChangedListener listener) {
		listenerList.add(HighlightChangedListener.class, listener);
	}

	public void removeHighlightChangedListener(HighlightChangedListener listener) {
		listenerList.remove(HighlightChangedListener.class, listener);
	}

	public static interface ImageHoverListener extends EventListener {
		void onImageHover(MouseEvent event);
	}

	public static interface HighlightChangedListener extends EventListener {
		void onHighlightChanged(AreaChangedEvent event);
	}
}