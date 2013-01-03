/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.stegosaurus.stegutils.StegUtils;

import steganographers.coders.ImgCoder;

/**
 * A BMP Hider or Unhider.
 *
 * @author joe
 */
public abstract class BMPCoder extends ImgCoder {

    /**
     * Dib header.
     */
    protected byte[] dib;
    /**
     * The number of bytes that go into each pixel.
     */
    protected int pixel_size;
    /**
     * The size of the data portion of the BMP file, in bytes.
     */
    protected int data_size;
    /**
     * The number of bytes that have been read so far.
     */
    protected int bytes_read;
    /**
     * The Width of the image.
     */
    protected int width;
    /**
     * A buffer into which the image's data is read into every time a new pixel
     * is read.
     */
    protected byte[] imgdata;
    /**
     * The image's header.
     */
    protected byte[] header;

    public BMPCoder(InputStream in) throws Exception {
        super(in);
        header = ReadHeader();
        bytes_read = 0;
        /*
         * Where the actual data begins
         */
        int offset = StegUtils.IntFromBytes(Arrays.copyOfRange(header, 10, 14), 4);
        /*
         * The size of the dib header
         */
        int dib_size = offset - 14;
        dib = new byte[dib_size];
        instream.read(dib);
        /*
         * How many bytes are in each pixel?
         */
        pixel_size = StegUtils.IntFromBytes(Arrays.copyOfRange(dib, 14, 16), 2) / 8;
        width = StegUtils.IntFromBytes(Arrays.copyOfRange(dib, 4, 8), 4);
        /*
         * TODO: Investigate why this does not work
         */
        //data_size = IntFromBytes(Arrays.copyOfRange(dib, 20, 24), 4);
        data_size = instream.available();
        imgdata = new byte[data_size];
    }

    /**
     * Read and return the image header.
     *
     * @return the BMP's header.
     * @throws Exception
     */
    private byte[] ReadHeader() throws Exception {
        byte[] retval = new byte[14];
        instream.read(retval);
        return retval;
    }

    /**
     * Read the next pixel of the image into imgdata, and return the offset in
     * which its LSB may be found.
     *
     * @return int the offset in which to find the just read pixel's LSB.
     */
    protected int NextPixel() throws IOException {
        /*
         * Do we have to account for an offset?
         */
        if (bytes_read % width == 0 && width % 4 != 0) {
            /*
             * Essentially we want to find the next multiple of 4 and then
             * substract the width from it so as to know how many bytes to skip
             */
            int skip = ((width / 4) + 1) * 4 - width;
            instream.read(imgdata, bytes_read, skip);
            bytes_read += skip;
        }
        instream.read(imgdata, bytes_read, pixel_size);
        bytes_read += pixel_size;
        return bytes_read - pixel_size;
    }

    /**
     * Hide the given bit in the LSB of the carrier int.
     *
     * @param bit either 0 or 1, the bit to place in the carrier
     * @param carrier the int whose LSB will be modified
     * @return the modified carrier.
     */
    protected static int HideInLSB(int bit, int carrier) {
        int retval;
        if (bit == 0) {
            /*
             * If we have a zero, zero out the last bit
             */
            retval = carrier & 0xfffffffe;
        } else {
            retval = carrier | 1;
        }
        return retval;
    }
}
