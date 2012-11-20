/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers;

import stegostreams.BitInputStream;


/**
 * Hides data into a given file. Completely agnostic as to what the data may
 * be, only makes use of byte arrays.
 * @author joe
 */
public abstract class Steganographer {
    protected String target;
    /**
     * Initialize a steganographer.
     * @param target the file where the data will be hidden.
     */
    public Steganographer(String target) {
        this.target = target;
    }

    /**
     * Hide the data given into this steganographer's target. Notice that this
     * make no effort to preserve any previous data that may have been hidden
     * there.
     * @param datastream the data to hide, as a stream of bits.
     */
    public abstract void Hide(BitInputStream datastream);

    /**
     * Interpret the byte array given as a little endian int composed of size
     * bytes.
     * @param bytes the byte array in question
     * @param size the number of bytes composing the int
     * @return the int worked out from the byte array
     */
    protected static int IntFromBytes(byte[] bytes, int size) {
        int retval = 0;
        for (int i = 0; i < size; i++) {
            retval += ((int) bytes[i]) << (i * 8);
        }
        return retval;
    }
}
