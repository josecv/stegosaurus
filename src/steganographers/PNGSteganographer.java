/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers;

import java.io.InputStream;
import stegostreams.BitInputStream;


public class PNGSteganographer extends Steganographer {
    /* Our constants */
    
    /**
     * The size of the PNG header, in bytes.
     */
    public static final int HEADER_SIZE = 8;
    /**
     * The size of the first bytes of data in each header chunk, in bytes.
     * These first few bytes contain things such as the nature and size of
     * the chunk.
     */
    public static final int CHUNK_HEAD_SIZE = 8;
    /**
     * The size of the cyclic redundancy checksum for each chunk, in bytes.
     */
    public static final int CHUNK_CRC_SIZE = 4;
    /**
     * The PNG header. Quite useless.
     */
    private byte[] header;
    
    /**
     * The image's IHDR chunk.
     */
    private byte[] IHDR;
    
    /**
     * The image's palette.
     */
    private byte[] PLTE;
    
    
    public PNGSteganographer(InputStream carrier) {
        super(carrier);
        header = new byte[HEADER_SIZE];
        try {
            carrier.read(header);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public byte[] UnHide(int count) throws Exception {
        return new byte[3];
    }
    
    @Override
    public byte[] Hide(BitInputStream datastream) throws Exception {
        return new byte[3];
    }
}
