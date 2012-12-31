/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;

import java.io.InputStream;

/**
 * A coder which operates on images.
 *
 * @author joe
 */
public abstract class ImgCoder implements Coder {

    /**
     * An InputStream representing the image data.
     */
    protected InputStream instream;

    /**
     * Initialize the ImgCoder and read the header in.
     *
     * @param in the InputStream with the image data.
     */
    public ImgCoder(InputStream in) throws Exception {
        instream = in;
    }
}
