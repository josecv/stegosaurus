/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegosaurus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import steganographers.Desteganographer;
import steganographers.Steganographer;
import steganographers.coders.BMPHider;
import steganographers.coders.BMPUnHider;
import stegostreams.BitInputStream;

/**
 * A nifty tester thingy.
 * @author joe
 */
public class Stegosaurus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String t = args[0];
        String m = args[1];
        try {
            Steganographer stego = new Steganographer(new BMPHider(new FileInputStream(t)));
            MessageHandler h = new MessageHandler(m);
            byte[] hidden = stego.Hide(new BitInputStream(h.AsByteArray()));
            try (FileOutputStream out = new FileOutputStream(t)) {
                out.write(hidden);
            }
            System.out.println("Encoded!");
            Desteganographer destego = new Desteganographer(new BMPUnHider(new FileInputStream(t)));
            for (byte b : destego.UnHide()) {
                System.out.print((char) (b));
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("We are setting sail!");
    }
}
