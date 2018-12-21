package com.blazemeter.jmeter.citrix.gui;

import java.awt.Rectangle;

import javax.swing.event.EventListenerList;

public class SelectionChangedManager implements SelectionChangedSubject {

	private final EventListenerList listenerList;

	public SelectionChangedManager(EventListenerList listenerList) {
		if (listenerList == null) {
			throw new IllegalArgumentException("listeners cannot be null.");
		}
		this.listenerList = listenerList;
	}

	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		listenerList.add(SelectionChangedListener.class, listener);
	}

	@Override
	public void removeSelectionChangedListener(SelectionChangedListener listener) {
		listenerList.remove(SelectionChangedListener.class, listener);
	}

	public void trigger(Rectangle selection) {
		SelectionChangedListener[] listeners = listenerList.getListeners(SelectionChangedListener.class);
		for (int index = 0; index < listeners.length; index++) {
			listeners[index].onSelectionChanged(
					new AreaChangedEvent(this, selection != null ? new Rectangle(selection) : null));
		}
	}

}
