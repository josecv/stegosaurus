package steganographers;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import stegosaurus.utils.ArrayUtils;
import stegostreams.BitInputStream;
import stegostreams.BitOutputStream;


/**
 * Hides/Unhides data into a BMP file.
 * @author joe
 */
public class BMPSteganographer extends Steganographer {
    
    /**
     * BMP File header.
     */
    private byte[] header;
    /**
     * Dib header.
     */
    private byte[] dib;
    
    /**
     * The number of bytes that go into each pixel.
     */
    private int pixel_size;
    
    /**
     * The size of the data portion of the BMP file, in bytes.
     */
    private int data_size;
    
    /**
     * Create a new BMP steganographer to hide data in the file given.
     * @param target the path to the file where the data will be hidden.
     */
    public BMPSteganographer(InputStream carrier) {
        super(carrier);
        try {
            header = new byte[14];
            instream.read(header);
            /* Where the actual data begins */
            int offset = IntFromBytes(Arrays.copyOfRange(header, 10, 14), 4);
            /* The size of the dib header */
            int dib_size = offset - 14;
            dib = new byte[dib_size];
            instream.read(dib);
            /* How many bytes are in each pixel? */
            pixel_size = IntFromBytes(Arrays.copyOfRange(dib, 14, 16), 2) / 8;
            data_size = IntFromBytes(Arrays.copyOfRange(dib, 20, 24), 4);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    /**
     * Hide the given bit in the LSB of the carrier int.
     * @param bit either 0 or 1, the bit to place in the carrier
     * @param carrier the int whose LSB will be modified
     * @return the modified carrier.
     */
    protected static int HideInLSB(int bit, int carrier) {
        int retval;
        if (bit == 0) {
            /* If we have a zero, zero out the last bit */
            retval = carrier & 0xfffffffe;
        } else {
            retval = carrier | 1;
        }
        return retval;
    }
    
    
    /**
     * Return the next pixel inside the bmp image, as an int.
     * @return int the next pixel to be read.
     */
    private int NextPixel() throws IOException {
        byte[] pixel_bytes = new byte[pixel_size];
        instream.read(pixel_bytes);
        return IntFromBytes(Arrays.copyOfRange(pixel_bytes, 0, pixel_size),
                pixel_size);
    }
    
    /**
     * Hide the data given in the target.
     * @param data the data to hide.
     * 
     * @return a byte array with the hidden data.
     */
    @Override
    public byte[] Hide(BitInputStream datastream) throws IOException {
        /* TODO: This is in want of some serious refactoring */
        /* TODO: Stop assuming that no compression is being used */
        byte[] newimgdata = new byte[datastream.available() * pixel_size];
        /* Number of bytes read */
        int bytes_read = 0;
        /* The read loop; while there are bytes to be read, read them. */
        while (datastream.available() > 0) {
            int pixel = NextPixel();
            /* Actually place the bit in the lsb of the pixel */
            pixel = HideInLSB(datastream.read(), pixel);
            bytes_read += pixel_size;
            /* Transform pixels into sets of bytes */
            for (int i = 0; i < pixel_size; i++) {
                /* Remove bits more significant than the ones we care about
                 * by anding the pixel with the apropriate power of two
                 * minus one, and then shift as to the right to lose
                 * bits less significant than the ones we care about.
                 */
                byte val = (byte) ((((1 << ((1 + i) * 8)) - 1) & pixel) >> (i * 8));
                newimgdata[bytes_read - pixel_size + i] = val;
            }
        }
        byte[] rest = new byte[data_size - newimgdata.length];
        instream.read(rest);
        instream.close();
        /* TODO: This part sucks big time */
        return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(header,
                dib), newimgdata), rest);
    }
    
    @Override
    public byte[] UnHide(int count) throws IOException {
        byte[] retval;
        try (BitOutputStream ostream = new BitOutputStream()) {
            for (int i = 0; i < count * 8; i++) {
                ostream.write(NextPixel() & 1);
            }
            retval = ostream.data();
        }
        return retval;
    }
}
