/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegosaurus;
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
        System.out.println("If you only see this you're a lucky pirate");
    }
}
