/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;

import java.io.IOException;
import java.io.InputStream;

/*
 * The JPEG standard splits a file into chunks delimited by markers which are
 * the 0xFF byte followed by any non-zero byte. Should an actual 0xFF be
 * desired, it is escaped by placing a zero byte after it. Should a 0xFF be
 * followed by one or more 0xFFs, it is not a marker but padding, usually
 * preceding a marker, and should be ignored.
 */
/**
 * Deals with JPG images as carriers.
 *
 * @author joe
 */
public abstract class JPGCoder extends ImgCoder {

    /**
     * Code for the start of image marker.
     */
    protected static final int SOI_MARKER = 0xDB;
    /**
     * Code for the start of frame marker. Indicates this as a baseline DCT JPG.
     * Gives width height number of components and component subsampling.
     */
    protected static final int SOF0_MARKER = 0xC0;
    /**
     * Code for the start of frame marker. Indicates this as a progressive DCT
     * JPG. Same as with SOF0.
     */
    protected static final int SOF2_MARKER = 0xC2;
    /**
     * Code for the Huffman tables marker.
     */
    protected static final int DHT_MARKER = 0xC4;
    /**
     * Code for the quantization tables marker.
     */
    protected static final int DQT_MARKER = 0xDB;
    /**
     * Code for the Define Restart Interval marker. Specifies the intervals
     * between RSTn markers.
     */
    protected static final int DRI_MARKER = 0xDD;
    /**
     * Code for the start of scan marker. Starts the image scan, from top to
     * bottom.
     */
    protected static final int SOS_MARKER = 0xDA;
    /**
     * Code for a text comment.
     */
    protected static final int COM_MARKER = 0xFE;
    /**
     * Code for the end of image marker.
     */
    protected static final int EOI_MARKER = 0xD9;
    /**
     * A Buffer with the entire JPG file. Used so that we can look ahead.
     */
    private byte[] buffer;

    /*
     * Unfortunately, I think we have no real choice in the matter here: We
     * _have_ to read in the entire image in one go, since the InputStreams are
     * non-rewindable and we need to look at the next marker to figure out when
     * a piece of file is over.
     */
    /**
     * Initialize the JPGCoder.
     *
     * @param in the InputStream with the JPEG image.
     * @throws Exception
     */
    public JPGCoder(InputStream in) throws Exception {
        super(in);
        buffer = new byte[instream.available()];
        instream.read(buffer);
    }

    /**
     * Look at the buffer, starting at the location of the previous marker,
     * until the next marker is found, and return the index where the marker
     * starts.
     *
     * @param start the location of the preceding marker.
     *
     * @return where to find the next marker.
     */
    protected int FindMarker(int start) throws IOException {
        /*
         * The first two bytes are a marker, so skip them
         */
        int c = start + 2;
        int i = 0, j = 0;
        while (i != 0xFF || j == 0xFF || j == 0) {
            i = buffer[c] & 0xFF;
            j = buffer[c + 1] & 0xFF;
            c++;
        }
        return c;
    }
}
