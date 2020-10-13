package com.blazemeter.jmeter.citrix.gui;

public interface SelectionChangedSubject {

  void addSelectionChangedListener(SelectionChangedListener listener);

  void removeSelectionChangedListener(SelectionChangedListener listener);

}
