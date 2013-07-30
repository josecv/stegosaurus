package com.stegosaurus.stegosaurus;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.stegosaurus.steganographers.Desteganographer;
import com.stegosaurus.steganographers.Steganographer;
import com.stegosaurus.steganographers.coders.BMPHider;
import com.stegosaurus.steganographers.coders.BMPUnHider;


/**
 * A nifty tester thingy.
 * @author joe
 */
public final class Stegosaurus {

  /**
   * Private CTOR.
   */
  private Stegosaurus() { }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String t = args[0];
    String m = args[1];
    try {
      Steganographer stego = new Steganographer(new BMPHider(new FileInputStream(t)));
      byte[] hidden = stego.hide(m);
      try (FileOutputStream out = new FileOutputStream(t)) {
        out.write(hidden);
      }
      Desteganographer destego = new Desteganographer(new BMPUnHider(new FileInputStream(t)));
      for (byte b : destego.unHide()) {
        System.out.print((char) (b));
      }
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("We are setting sail!!");
  }
}
