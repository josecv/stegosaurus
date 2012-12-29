/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;

/**
 * UnHides payloads from carriers. Close returns the entire payload.
 * @author joe
 */
public interface UnHider extends Coder {
    /**
     * Un-hide count bytes of the payload from the carrier.
     * @param count the number of bytes to decode.
     * 
     * @return the UnHidden bytes.
     */
    public byte[] UnHide(int count) throws Exception;
}
