package com.blazemeter.jmeter.citrix.recorder.gui;

class ActionItem {
  private final String command;
  private final String description;

  ActionItem(String command, String description) {
    this.command = command;
    this.description = description;
  }

  public String getCommand() {
    return command;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    return description;
  }
}
