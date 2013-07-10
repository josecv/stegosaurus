/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.steganographers.coders;

import java.io.IOException;

import com.stegosaurus.steganographers.coders.Coder;

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
    byte[] unHide(int count) throws IOException;
}
