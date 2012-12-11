package steganographers;
import java.io.FileInputStream;
import java.util.Arrays;
import stegostreams.BitOutputStream;

/**
 * De-steganographies messages hidden in BMP files.
 * @author joe
 */
public class BMPDesteganographer extends Desteganographer {
    FileInputStream instream;
    byte[] header;
    byte[] dib;
    
    public BMPDesteganographer (String carrier) {
        super(carrier);
        try {
            instream = new FileInputStream(carrier);
            header = new byte[14];
            instream.read(header);
            /* Where the actual data begins */
            int offset = IntFromBytes(Arrays.copyOfRange(header, 10, 14), 4);
            /* The size of the dib header */
            int dib_size = offset - 14;
            dib = new byte[dib_size];
            instream.read(dib);
        } catch (java.io.IOException fex) {
            System.out.println(fex.getMessage());
        }
    }
    
    @Override
    public byte[] UnHide(int count) {
        BitOutputStream bitostream = new BitOutputStream();
        /* Read a bit at a time */
        try {
            for (int read = 0; read < (count * 8); read++) {
                int i = instream.read() & 1;
                System.out.println(i);
                bitostream.write(instream.read() & 1);
            }
        } catch (java.io.IOException iox) {
            System.out.println(iox.getMessage());
        }
        return bitostream.data();
    }
}
