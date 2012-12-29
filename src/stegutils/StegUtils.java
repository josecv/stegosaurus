/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegutils;

/**
 * Provides utility methods used throughout.
 *
 * @author joe
 */
public final class StegUtils {

    private StegUtils() {
    }

    /**
     * Interpret the byte array given as a little endian int composed of size
     * bytes.
     *
     * @param bytes the byte array in question
     * @param size the number of bytes composing the int
     * @return the int worked out from the byte array
     */
    public static int IntFromBytes(byte[] bytes, int size) {
        int retval = 0;
        for (int i = 0; i < size; i++) {
            retval += ((int) bytes[i]) << (i * 8);
        }
        return retval;
    }
}
