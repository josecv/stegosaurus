/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers;

import steganographers.coders.UnHider;
import stegutils.StegUtils;

/**
 * Mediates with an UnHider to unhide a payload from a carrier.
 *
 * @author joe
 */
public class Desteganographer {

    private UnHider unhider;

    public Desteganographer(UnHider u) {
        this.unhider = u;
    }

    /**
     * UnHide an entire message from the carrier.
     *
     * @return a byte array, the bytes found in there.
     * @throws Exception
     */
    public byte[] UnHide() throws Exception {
        byte[] unhidden = unhider.UnHide(4);
        int size = StegUtils.IntFromBytes(unhidden, 4);
        byte[] retval = unhider.UnHide(size);
        /* We don't want the starting four bytes in the message, so trim them
         * by not returning unhider.close
         */
        unhider.close();
        return retval;
    }
}
