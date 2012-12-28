package steganographers;

import java.io.InputStream;
import stegostreams.BitInputStream;

/*
 * TODO: Make some kind of abstract Stegano-thing and then have it have two
 * abstract children: Stego and DeStego, and etc.
 */
/**
 * Hides/Unhides a payload from a carrier. Can only do one of those two things,
 * since the carrier stream will be closed after performing the operation.
 *
 * @author joe
 */
public abstract class Steganographer {

    protected String target;
    /**
     * The carrier input stream.
     */
    protected InputStream instream;

    /**
     * Initialize a steganographer.
     *
     * @param target the file where the data will be hidden.
     */
    public Steganographer(InputStream carrier) {
        this.instream = carrier;
    }

    /**
     * Hide the data given into this steganographer's target. Notice that this
     * make no effort to preserve any previous data that may have been hidden
     * there.
     *
     * @param datastream the data to hide, as a stream of bits.
     * @return a byte array with the hidden data.
     * @throws Exception
     */
    public abstract byte[] Hide(BitInputStream datastream) throws Exception;

    /**
     * UnHide an entire message from the carrier.
     * @return a byte array, the bytes found in there.
     * @throws Exception 
     */    
    public byte[] UnHide() throws Exception {
    	byte[] unhidden = UnHide(4);
        int size = IntFromBytes(unhidden, 4);
        byte[] retval = UnHide(size);
        instream.close();
        return retval;
    }
    
    /**
     * Unhides count bytes from the carrier.
     * @param count the number of bytes to take out.
     * @return a portion of the payload.
     * @throws Exception 
     */
    public abstract byte[] UnHide(int count) throws Exception;
    
    /**
     * Interpret the byte array given as a little endian int composed of size
     * bytes.
     *
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
