package steganographers;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import stegostreams.BitInputStream;


/**
 * Hides data into a BMP file.
 * @author joe
 */
public class BMPSteganographer extends Steganographer {
    
    private FileInputStream instream;
    private FileOutputStream ostream;
    
    /**
     * Create a new BMP steganographer to hide data in the file given.
     * @param target the path to the file where the data will be hidden.
     */
    public BMPSteganographer(String target) {
        super(target);
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
    
    protected void WriteToTarget(byte[] header, byte[] dib, byte[] data, byte[] rest) {
        try {
            ostream = new FileOutputStream(target);
            ostream.write(header);
            ostream.write(dib);
            ostream.write(data);
            ostream.write(rest);
            ostream.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    /**
     * Hide the data given in the target.
     * @param data the data to hide.
     */
    @Override
    public void Hide(BitInputStream datastream) {
        /* TODO: This is in want of some serious refactoring */
        try {
            instream = new FileInputStream(target);
            byte[] header = new byte[14];
            instream.read(header);
            /* The standard says the first thing we want is the size of the
             * entire BMP image
             */
            int size = IntFromBytes(Arrays.copyOfRange(header, 2, 6), 4);
            /* Where the actual data begins */
            int offset = IntFromBytes(Arrays.copyOfRange(header, 10, 14), 4);
            /* The size of the dib header */
            int dib_size = offset - 14;
            byte[] dib = new byte[dib_size];
            instream.read(dib);
            /* Dimensions of the picture in pixels */
            int w = IntFromBytes(Arrays.copyOfRange(dib, 4, 8), 4);
            int h = IntFromBytes(Arrays.copyOfRange(dib, 8, 12), 4);
            /* TODO: Stop assuming that no compression is being used */
            /* How many bytes are in each pixel? */
            int pixel_size = IntFromBytes(Arrays.copyOfRange(dib, 14, 16), 2) / 8;
            int data_size = IntFromBytes(Arrays.copyOfRange(dib, 20, 24), 4);
            /* TODO Transition so this is done in place */
            byte[] newimgdata = new byte[datastream.available() * pixel_size];
            //instream.read(newimgdata);
            /* Number of bytes read */
            int bytes_read = 0;
            /* The read loop; while there are bytes to be read, read them. */
            while (datastream.available() > 0) {
                byte[] pixel_bytes = new byte[pixel_size];
                instream.read(pixel_bytes);
                System.out.println("Dr. Fluttershy expected that.");
                int pixel = IntFromBytes(Arrays.copyOfRange(pixel_bytes, 0,
                        pixel_size), pixel_size);
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
                    System.out.println(bytes_read - pixel_size + i);
                    byte val = (byte) ((((1 << ((1 + i) * 8)) - 1) & pixel) >> (i * 8));
                    newimgdata[bytes_read - pixel_size + i] = val;
                }
                System.out.println("Sing with me now, sing for the year");
            }
            byte[] rest = new byte[data_size - newimgdata.length];
            instream.read(rest);
            instream.close();
            this.WriteToTarget(header, dib, newimgdata, rest);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
