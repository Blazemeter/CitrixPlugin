package com.blazemeter.jmeter.citrix.sampler;

import static org.junit.Assert.assertArrayEquals;

import com.blazemeter.jmeter.citrix.sampler.InteractionSampler.Keystroke;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class InteractionSamplerTest {

  @Test
  public void generateKeystrokesFromText() {
    // From written text it gets the virtual key code to be used
    List<Keystroke> keystrokes = InteractionSampler.sampleTextKeystrokes("Ctrx");

    List<Keystroke> expected = new ArrayList<>();
    expected.add(new Keystroke(16, false, false));
    expected.add(new Keystroke(67, false, true));
    expected.add(new Keystroke(67, true, false));
    expected.add(new Keystroke(16, true, false));
    expected.add(new Keystroke(84, false, true));
    expected.add(new Keystroke(84, true, false));
    expected.add(new Keystroke(82, false, true));
    expected.add(new Keystroke(82, true, false));
    expected.add(new Keystroke(88, false, true));
    expected.add(new Keystroke(88, true, true));

    assertArrayEquals(expected.toArray(), keystrokes.toArray());
  }

}
