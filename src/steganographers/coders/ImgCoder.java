/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;
import java.io.InputStream;

/**
 * A coder which operates on images.
 * @author joe
 */
public abstract class ImgCoder implements Coder {
    /**
     * An InputStream representing the image data.
     */
    protected InputStream instream;
    
    /**
     * The image's header.
     */
    protected byte[] header;
    
    /**
     * Initialize the ImgCoder and read the header in.
     * @param in the InputStream with the image data.
     */
    public ImgCoder(InputStream in) throws Exception {
        instream = in;
        header = this.ReadHeader();
    }
    
    /**
     * Read and return the image header from the input stream.
     * 
     * @return the image's header as a byte array.
     */
    protected abstract byte[] ReadHeader() throws Exception;
}
