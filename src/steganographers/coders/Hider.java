/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographers.coders;

import stegostreams.BitInputStream;


/**
 * Hides data in a carrier. Close returns the carrier.
 * @author joe
 */
public interface Hider extends Coder {
    /**
     * Hide count bits from the datastream in the carrier. 
     * @param datastrem the stream containing the payload.
     * @param count the number of bits from the payload to hide.
     */
    public void Hide(BitInputStream datastream, int count) throws Exception;
}
