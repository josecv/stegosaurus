/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegosaurus;

import java.io.FileInputStream;
import java.io.FileOutputStream;

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
        try {
            BMPSteganographer stego = new BMPSteganographer(new FileInputStream(t));
            MessageHandler h = new MessageHandler(m);
            byte[] hidden = stego.Hide(new BitInputStream(h.AsByteArray()));
            FileOutputStream out = new FileOutputStream(t);
            out.write(hidden);
            out.close();
            BMPSteganographer destego = new BMPSteganographer(new FileInputStream(t));
            for (byte b : destego.UnHide()) {
                System.out.println((char) (b));
            }
        } catch (Exception e) {
            System.out.println("Tragedy!!");
        }
        System.out.println("We are setting sail!");
    }
}
