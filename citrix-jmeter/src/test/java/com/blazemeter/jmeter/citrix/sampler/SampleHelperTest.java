package com.blazemeter.jmeter.citrix.sampler;

import static org.junit.Assert.assertEquals;

import com.blazemeter.jmeter.citrix.client.CitrixClient;
import com.blazemeter.jmeter.citrix.client.events.EventHelper;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent;
import com.blazemeter.jmeter.citrix.client.events.InteractionEvent.KeyState;
import com.blazemeter.jmeter.citrix.client.events.Modifier;
import com.blazemeter.jmeter.citrix.client.windows.WinCitrixClient;
import com.blazemeter.jmeter.citrix.recorder.CaptureItem;
import java.awt.event.KeyEvent;
import org.junit.Test;

public class SampleHelperTest {

  // This class is created to simplify the creation of events, just to be used in this test.
  private static class CustomKeyCaptureGroup extends SamplerHelper.KeyCaptureGroup {

    CustomKeyCaptureGroup(SamplerHelper.KeySamplerType type) {
      super(type);
    }

    protected void  addCaptureKeyEvent(
        CitrixClient client, int keyCode, int modifier) {
      this.items.add(new CaptureItem(
          new InteractionEvent(client, KeyState.KEY_DOWN, keyCode,
              EventHelper.toModifiers(modifier)
          )
      ));
      this.items.add(new CaptureItem(
          new InteractionEvent(client, KeyState.KEY_UP, keyCode,
              EventHelper.toModifiers(0) // Always KeyUp lost the modifier state
          )
      ));
    }
  }

  @Test
  public void generateTextFromCapturedKeyEvents() {
    CustomKeyCaptureGroup group = new CustomKeyCaptureGroup(SamplerHelper.KeySamplerType.TEXT);

    WinCitrixClient client = new WinCitrixClient();

    // The test write "Ctrx" using Key Events.
    // Use ASCII to ensure all virtual keyboard used are compatible to this

    group.addCaptureKeyEvent(client, KeyEvent.VK_C, Modifier.SHIFT.getValue());
    group.addCaptureKeyEvent(client, KeyEvent.VK_T, 0);
    group.addCaptureKeyEvent(client, KeyEvent.VK_R, 0);
    group.addCaptureKeyEvent(client, KeyEvent.VK_X, 0);

    String text = SamplerHelper.getKeyCaptureGroupText(group);
    String expected = "Ctrx";
    assertEquals(expected, text);
  }

}
