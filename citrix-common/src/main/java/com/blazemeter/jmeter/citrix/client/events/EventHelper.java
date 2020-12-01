package com.blazemeter.jmeter.citrix.client.events;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventHelper.class);

  private EventHelper() {
  }

  public static Set<MouseButton> toButtons(final int value) {
    Set<MouseButton> result = EnumSet.noneOf(MouseButton.class);
    int copy = value;
    for (MouseButton button : MouseButton.values()) {
      final int bValue = button.getValue();
      if ((bValue & value) == bValue) {
        result.add(button);
        copy -= bValue;
      }
    }
    if (copy != 0) {
      LOGGER.warn(
          "Converting integer value to set of buttons: Value {} contains unsupported flags {}",
          value,
          Integer.toBinaryString(copy));
    }
    return result;
  }

  public static int fromButtons(Set<MouseButton> buttons) {
    int flags = 0;
    if (buttons != null) {
      for (MouseButton button : buttons) {
        flags += button.getValue();
      }
    }
    return flags;
  }

  public static Set<Modifier> toModifiers(final int value) {
    Set<Modifier> result = EnumSet.noneOf(Modifier.class);
    int copy = value;
    for (Modifier modifier : Modifier.values()) {
      final int mValue = modifier.getValue();
      if ((mValue & value) == mValue) {
        result.add(modifier);
        copy -= mValue;
      }
    }
    if (copy != 0) {
      LOGGER.warn(
          "Converting integer value to set of modifiers: Value {} contains unsupported flags {}",
          value,
          Integer.toBinaryString(copy));
    }
    return result;
  }

  public static String getModifiersText(final Set<Modifier> modifiers) {
    return modifiers.stream()
        .map(Modifier::name)
        .collect(Collectors.joining("+"));
  }

  public static int fromModifiers(Set<Modifier> modifiers) {
    int flags = 0;
    if (modifiers != null) {
      for (Modifier modifier : modifiers) {
        flags += modifier.getValue();
      }
    }
    return flags;
  }
}
