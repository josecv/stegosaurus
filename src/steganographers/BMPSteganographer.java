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
     * Hide the data given in the target.
     * @param data the data to hide.
     */
    @Override
    public void Hide(byte[] data) {
        try {
            instream = new FileInputStream(target);
            byte[] header = new byte[14];
            instream.read(header);
            /* Fuck Java's array methods, man */
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
            int pixel_size = IntFromBytes(Arrays.copyOfRange(dib, 14, 16), 2);
            int data_size = IntFromBytes(Arrays.copyOfRange(dib, 20, 24), 4);
            //byte[] imgdata = new byte[data_size];
            /* TODO Transition so this is done in place */
            byte[] newimgdata = new byte[data_size];
            //instream.read(imgdata);
            BitInputStream datastream = new BitInputStream(data);
            /* Number of bytes read */
            int bytes_read = 0;
            while (datastream.available() > 0) {
                byte[] pixel_bytes = new byte[pixel_size];
                instream.read(pixel_bytes);
                int pixel = IntFromBytes(Arrays.copyOfRange(pixel_bytes, 0,
                        pixel_size), pixel_size);
                pixel &= datastream.read();
                bytes_read += pixel_size;
                if (bytes_read % pixel_size == 0) {
                    instream.skip((4 * (bytes_read / 4  + 1) - bytes_read) % 4);
                }
                for (int i = 0; i < pixel_size; i++) {
                    /* Remove bits more significant than the ones we care about
                     * by anding the pixel with the apropriate power of two
                     * minus one, and then shift as to the right to lose
                     * bits less significant than the ones we care about.
                     */
                    byte val = (byte) ((((1 << ((1 + i) * 8)) - 1) & pixel) >> (i * 8));
                    newimgdata[bytes_read - pixel_size + 1] = val;
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
