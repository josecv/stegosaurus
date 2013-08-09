package com.stegosaurus.steganographers.coders;

import gnu.trove.TCollections;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.tuple.Pair;

import com.stegosaurus.jpeg.DecompressedScan;
import com.stegosaurus.jpeg.JPEGCompressor;
import com.stegosaurus.jpeg.JPEGDecompressor;

/**
 * Acts as a facade for the OutGuess hider; should be used in its stead
 * invariably.
 */
public class OutGuessHelper extends OutGuess {
  /**
   * Construct a new OutGuessHider instance.
   * @param key the key for the pseudo random number generator to use.
   */
  public OutGuessHelper(String key) {
    super(key);
  }


  /**
   * Calculate the frequency of every DCT component in the cover image given.
   * @param cover the image to calculate frequencies for.
   * @return the DCT frequency table.
   */
  private TIntIntMap calculateFrequencies(int[] cover) {
    TIntIntMap retval = new TIntIntHashMap();
    for(int i : cover) {
      retval.adjustOrPutValue(i, 1, 1);
    }
    return retval;
  }


  /**
   * Hide the message given in the cover image data provided.
   * The data provided should be some JPEG scan data. Note that
   * it is mutated.
   * @param cover the cover image data to use.
   * @param message the message to hide.
   * @throws IOException on read error from the cover.
   */
  public void hide(int[] cover, byte[] message) {
    TIntIntMap freq = calculateFrequencies(cover);
    TIntDoubleMap tolerances = TCollections.
      synchronizedMap(new TIntDoubleHashMap());
    OutGuessHider hider = new OutGuessHider(cover, getKey(),
      TCollections.unmodifiableMap(freq), tolerances);
    Pair<int[], Integer> result = hider.hide(message);
    System.arraycopy(result.getLeft(), 0, cover, 0, cover.length);
  }

  /**
   * Hide the message given in the cover image provided. Return the stegano
   * image containing the message.
   * @param cover the cover image to use.
   * @param message the message to hide.
   * @return the image, containing the message.
   * @throws IOException on read error from the cover.
   */
  public byte[] hide(InputStream cover, byte[] message) throws IOException {
    JPEGDecompressor jpeg = new JPEGDecompressor(cover);
    jpeg.init();
    DecompressedScan scan = getBestScan(jpeg.processImage());
    /* TODO Don't just get the 0th buffer, for fucks sake */
    TIntList data = scan.getCoefficientBuffers().get(0);
    hide(data, message);
    JPEGCompressor comp = new JPEGCompressor();
    comp.process(scan);
    jpeg.refresh();
    return jpeg.getProcessed();
  }

  /**
   * Hide the message given in the cover image data provided.
   * The data provided should be some JPEG scan data. Note that
   * it is mutated.
   * @param cover the cover image data to use.
   * @param message the message to hide.
   * @throws IOException on read error from the cover.
   */
  public void hide(TIntList cover, byte[] message) {
    int[] data = cover.toArray();
    hide(data, message);
    cover.set(0, data);
  }
}
