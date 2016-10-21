package com.stegosaurus.stegosaurus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.stegosaurus.testing.TestWithInjection;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests the StegosaurusFacadeImpl class.
 * TODO It would be nice to have a unit test that mocks various objects
 */
public class StegosaurusFacadeImplTest extends TestWithInjection {
  /**
   * The message.
   */
  private static String MSG = "This is my message\n" +
                              "How nice it is to have a message";

  /**
   * The key.
   */
  private static String KEY = "Lionel Messi";

  /**
   * Test the facade end to end.
   */
  @Test
  public void integrationTest() throws Exception {
    StegosaurusFacadeImpl facade =
      injector.getInstance(StegosaurusFacadeImpl.class);
    InputStream in =
      StegosaurusFacadeImplTest.class.getResourceAsStream("napoleon.jpg");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    facade.embed(in, out, MSG, KEY);
    ByteArrayInputStream readBack = new ByteArrayInputStream(out.toByteArray());
    String result = facade.extract(readBack, KEY);
    assertEquals(MSG, result);
  }
}
