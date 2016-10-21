package com.stegosaurus.stegosaurus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import com.google.inject.Inject;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.cpp.JoctetArray;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;
import com.stegosaurus.steganographers.Extractor;
import com.stegosaurus.steganographers.ExtractorFactory;
import com.stegosaurus.stegutils.NativeUtils;

/**
 * Implements the stegosaurus facade.
 */
class StegosaurusFacadeImpl implements StegosaurusFacade {

  /**
   * The embedder factory.
   */
  private EmbedderFactory embedderFactory;

  /**
   * The extractor factory.
   */
  private ExtractorFactory extractorFactory;

  /**
   * CTOR.
   *
   * @param embedderFactory the embedder factory
   * @param extractorFactory the extractor factory
   */
  @Inject
  StegosaurusFacadeImpl(EmbedderFactory embedderFactory,
                        ExtractorFactory extractorFactory) {
    this.embedderFactory = embedderFactory;
    this.extractorFactory = extractorFactory;
  }

  @Override
  public void embed(InputStream in, OutputStream out, String message, String key)
      throws IOException {
    NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
    JPEGImage cover = new JPEGImage(arr.cast(), arr.length());
    EmbedRequest request = new EmbedRequest(cover, message.getBytes(), key);
    Embedder embedder = embedderFactory.build();
    JPEGImage result = embedder.embed(request);
    JoctetArray outArray = JoctetArray.frompointer(result.getData());
    NativeUtils.writeOctetArray(out, outArray, result.getDataLen());
  }

  @Override
  public String extract(InputStream in, String key) throws IOException {
    NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
    JPEGImage cover = new JPEGImage(arr.cast(), arr.length());
    Extractor ex = extractorFactory.build();
    byte[] byteArray = ex.extract(cover, key);
    return new String(byteArray);
  }
}
