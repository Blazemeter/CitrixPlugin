package com.blazemeter.jmeter.citrix.gui;

import java.util.EventListener;

public interface SelectionChangedListener extends EventListener {
	void onSelectionChanged(AreaChangedEvent event);
}