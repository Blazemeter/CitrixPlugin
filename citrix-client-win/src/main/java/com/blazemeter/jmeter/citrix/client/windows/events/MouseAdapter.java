package com.blazemeter.jmeter.citrix.client.windows.events;

import com.blazemeter.jmeter.citrix.client.windows.com4j.events._IMouseEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MouseAdapter extends _IMouseEvents {

  private static final Logger LOGGER = LoggerFactory.getLogger(MouseAdapter.class);

  @Override
  public void onDoubleClick() {
    LOGGER.debug("onDoubleClick");
  }

  @Override
  public void onMouseDown(int buttonState, int modifierState, int xPos, int yPos) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("onMouseDown: buttonState=0b{}, modifierState=0b{}, xPos={}, yPos={}",
          Integer.toBinaryString(buttonState), Integer.toBinaryString(modifierState), xPos, yPos);
    }
  }

  @Override
  public void onMouseUp(int buttonState, int modifierState, int xPos, int yPos) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("onMouseUp: buttonState=0b{}, modifierState=0b{}, xPos={}, yPos={}",
          Integer.toBinaryString(buttonState), Integer.toBinaryString(modifierState), xPos, yPos);
    }
  }

  @Override
  public void onMove(int buttonState, int modifierState, int xPos, int yPos) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("onMove: buttonState=0b{}, modifierState=0b{}, xPos={}, yPos={}",
          Integer.toBinaryString(buttonState), Integer.toBinaryString(modifierState), xPos, yPos);
    }
  }
}
