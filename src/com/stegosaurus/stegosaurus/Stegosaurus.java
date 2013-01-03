package com.stegosaurus.stegosaurus;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.stegosaurus.steganographers.Desteganographer;
import com.stegosaurus.steganographers.Steganographer;
import com.stegosaurus.steganographers.coders.BMPHider;
import com.stegosaurus.steganographers.coders.BMPUnHider;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.MessageHandler;


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
