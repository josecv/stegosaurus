package steganographers;
import java.io.FileInputStream;
import stegostreams.BitOutputStream;

/**
 * De-steganographies messages hidden in BMP files.
 * @author joe
 */
public class BMPDesteganographer extends Desteganographer {
    FileInputStream instream;
    
    public BMPDesteganographer (String carrier) {
        super(carrier);
        try {
            instream = new FileInputStream(carrier);
        } catch (java.io.FileNotFoundException fex) {
            System.out.println(fex.getMessage());
        }
    }
    
    @Override
    public byte[] UnHide(int count) {
        BitOutputStream bitostream = new BitOutputStream();
        /* Read a bit at a time */
        try {
            for (int read = 0; read < count * 8; read++) {
                bitostream.write(instream.read() & 1);
            }
        } catch (java.io.IOException iox) {
            System.out.println(iox.getMessage());
        }
        return bitostream.data();
    }
}
