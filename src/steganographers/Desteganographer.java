package steganographers;
import stegostreams.BitOutputStream;


/**
 * De-steganographer: Charged with un-hiding hidden data.
 * @author joe
 */
public abstract class Desteganographer {
    /* TODO: Make me an inputstream! */
    /**
     * The file containing the hidden data.
     */
    protected String carrier;
    
   /**
    * Constructor.
    * @param carrier the file where we will be reading from.
    */
    public Desteganographer(String carrier) {
        this.carrier = carrier;
    }
    
    /**
     * UnHide an entire message from the carrier. Assumes that the first four
     * bytes are the length of the message.
     * @return 
     */
    public byte[] UnHide() {
        int size = Desteganographer.IntFromBytes(this.UnHide(4), 4);
        return this.UnHide(size);
    }
    
    /**
     * Un-hide count bytes from the carrier. Does not process the bytes in any
     * way, and makes no assumptions.
     * @param count the number of bytes to retrieve from the carrier.
     * @return 
     */
    public abstract byte[] UnHide(int count);
    
    /* Rule of three, bitches */
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
