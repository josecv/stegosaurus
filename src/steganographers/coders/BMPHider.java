/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;

import java.io.InputStream;
import stegostreams.BitInputStream;
import stegutils.ArrayUtils;

/**
 * Hides payload data in a BMP carrier.
 *
 * @author joe
 */
public class BMPHider extends BMPCoder implements Hider {

    public BMPHider(InputStream in) throws Exception {
        super(in);
    }

    @Override
    public byte[] close() throws Exception {
        instream.read(imgdata, bytes_read, data_size - bytes_read);
        instream.close();
        return ArrayUtils.addAll(ArrayUtils.addAll(header, dib), imgdata);
    }

    @Override
    public void Hide(BitInputStream datastream, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            int off = NextPixel();
            /*
             * Actually place the bit in the lsb of the pixel
             */
            imgdata[off] = (byte) HideInLSB(datastream.read(), imgdata[off]);
        }
    }
}
