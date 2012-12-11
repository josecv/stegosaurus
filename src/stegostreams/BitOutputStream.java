package stegostreams;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Converts bit-by-bit input to a byte array. It is assumed that the data is
 * being given in little endian.
 * @author joe
 */
public class BitOutputStream extends OutputStream {
    /**
     * The byte array.
     */
    private ArrayList<Byte> data;
    /**
     * How many bits have been written.
     */
    int i;
    
    public BitOutputStream() {
        super();
        data = new ArrayList<>();
    }
    
    /**
     * Write a new bit to the stream.
     * @param b the bit to write
     */
    @Override
    public void write(int b) {
        if (i / 8 == data.size()) {
            data.add((byte) (0));
        }
        byte current = data.get(i / 8);
        current |= ((byte) (b)) << (i % 8);
        data.set(i / 8, current);
        i++;
    }
    
    /**
     * Return the data as a Byte array.
     * @return the collected data.
     */
    public byte[] data() {
        /* TODO: This sucks. Is there a one liner for this? */
        byte[] retval = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            retval[i] = data.get(i);
        }
        return retval;
    }
}
