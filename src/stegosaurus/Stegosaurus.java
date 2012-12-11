/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegosaurus;
import steganographers.BMPDesteganographer;
import steganographers.BMPSteganographer;
import stegostreams.BitInputStream;

/**
 *
 * @author joe
 */
public class Stegosaurus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String t = args[0];
        String m = args[1];
        BMPSteganographer stego = new BMPSteganographer(t);
        MessageHandler h = new MessageHandler(m);
        stego.Hide(new BitInputStream(h.AsByteArray()));
        BMPDesteganographer destego = new BMPDesteganographer(t);
        for (byte b : destego.UnHide()) {
            System.out.println((char) (b));
        }
        System.out.println("If you only see this you're a lucky pirate");
    }
}
