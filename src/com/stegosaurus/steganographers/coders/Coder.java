package com.stegosaurus.steganographers.coders;

/**
 * Either a Hider or an Un-Hider.
 * @author joe
 */
public interface Coder {
    /**
     * Close the coder for operations, and return either the payload or the
     * carrier.
     * @return either the payload or the carrier, depending on what kind of
     * coder this is.
     */
    public byte[] close() throws Exception;
}
