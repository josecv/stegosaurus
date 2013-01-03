/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;

import steganographers.coders.BMPCoder;
import com.stegosaurus.steganographers.coders.UnHider;
import com.stegosaurus.stegostreams.BitOutputStream;
import com.stegosaurus.stegutils.ArrayUtils;


/**
 * Remove payloads from BMP carriers.
 * @author joe
 */
public class BMPUnHider extends BMPCoder implements UnHider {
    
    /**
     * The payload to be returned on closing the coder.
     */
    private byte[] payload;
    
    public BMPUnHider(InputStream in) throws Exception {
        super(in);
        payload = new byte[0];
    }
    
    @Override
    public byte[] UnHide(int count) throws IOException {
        byte[] retval;
        try (BitOutputStream ostream = new BitOutputStream()) {
            for (int i = 0; i < count * 8; i++) {
                ostream.write(imgdata[NextPixel()] & 1);
            }
            retval = ostream.data();
        }
        payload = ArrayUtils.addAll(payload, retval);
        return retval;
    }
    
    @Override
    public byte[] close() throws Exception {
        instream.close();
        return payload;
    }
}
