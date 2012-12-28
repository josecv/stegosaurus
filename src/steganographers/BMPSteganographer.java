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
     * The number of bytes that have been read so far.
     */
    private int bytes_read;
    
    /**
     * The Width of the image.
     */
    private int width;
    
    private byte[] newimgdata;
    
    /**
     * Create a new BMP steganographer to hide data in the file given.
     * @param target the path to the file where the data will be hidden.
     */
    public BMPSteganographer(InputStream carrier) {
        super(carrier);
        bytes_read = 0;
        width = 0;
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
            width = IntFromBytes(Arrays.copyOfRange(dib, 4, 8), 4);
            /* TODO: Investigate why this does not work */
            //data_size = IntFromBytes(Arrays.copyOfRange(dib, 20, 24), 4);
            data_size = instream.available();
            newimgdata = new byte[data_size];
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
     * @return int the offset in which to find the just read pixel's LSB.
     */
    private int NextPixel() throws IOException {
        /* Do we have to account for an offset? */
        if (bytes_read % width == 0 && width % 4 != 0) {
            /* Essentially we want to find the next multiple of 4 and then
             * substract the width from it so as to know how many bytes
             * to skip */
            int skip = ((width / 4) + 1) * 4 - width;
            instream.read(newimgdata, bytes_read, skip);
            bytes_read += skip;
        }
        instream.read(newimgdata, bytes_read, pixel_size);
        bytes_read += pixel_size;
        return bytes_read - pixel_size;
    }
    
    /**
     * Hide the data given in the target.
     * @param data the data to hide.
     * 
     * @return a byte array with the hidden data.
     */
    @Override
    public byte[] Hide(BitInputStream datastream) throws IOException {
        /* TODO: Make me go byte by byte */
        /* TODO: This is in want of some serious refactoring */
        /* TODO: Stop assuming that no compression is being used */
        /* The read loop; while there are bytes to be read, read them. */
        while (datastream.available() > 0) {
            int off = NextPixel();
            /* Actually place the bit in the lsb of the pixel */
            newimgdata[off] = (byte) HideInLSB(datastream.read(),
                    newimgdata[off]);
        }
        instream.read(newimgdata, bytes_read, data_size - bytes_read);
        instream.close();
        return ArrayUtils.addAll(ArrayUtils.addAll(header, dib), newimgdata);
    }
    
    @Override
    public byte[] UnHide(int count) throws IOException {
        byte[] retval;
        try (BitOutputStream ostream = new BitOutputStream()) {
            for (int i = 0; i < count * 8; i++) {
                ostream.write(newimgdata[NextPixel()] & 1);
            }
            retval = ostream.data();
        }
        return retval;
    }
}
