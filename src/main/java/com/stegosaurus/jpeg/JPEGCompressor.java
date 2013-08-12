package com.stegosaurus.jpeg;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import com.stegosaurus.huffman.HuffmanEncoder;
import com.stegosaurus.stegostreams.BitOutputStream;
import com.stegosaurus.stegostreams.JPEGBitOutputStream;
import com.stegosaurus.stegutils.NumUtils;
import com.stegosaurus.stegutils.ZigZag;

/**
 * Re-compresses JPEG images, going from the products of a JPEGDecompressor
 * to their final state.
 * @see JPEGDecompressor
 */
public class JPEGCompressor {

  /**
   * CTOR.
   */
  public JPEGCompressor() { }

  /**
   * The huffman encoders for the scan currently being worked on.
   */
  private TIntObjectMap<HuffmanEncoder> encoders;

  /**
   * Encode the value given as a magnitude plus additional raw bits.
   * The array returned features the magnitude on the 0th index, and the
   * bits on the 1st index.
   * @param value the value to encode
   * @return the magnitude and additional bits.
   */
  private static int[] encodeValue(int value) {
    int magnitude, bits;
    if(value >= 0) {
      bits = value;
    } else {
      value = -value;
      bits = ~value;
    }
    magnitude = 0;
    while(value != 0) {
      value >>= 1;
      magnitude++;
    }
    int[] retval = { magnitude, bits };
    return retval;
  }

  /**
   * Encode the dc value given into the output stream given.
   * @param dc the dc value to encode.
   * @param lastDC the previously encoded dc value for this component.
   * @param encoder the HuffmanEncoder in use.
   * @param os the bit output stream to place the value in.
   */
  private void encodeDC(int dc, int lastDC, HuffmanEncoder encoder,
                        BitOutputStream os) {
    int diff = dc - lastDC;
    int[] encoded = encodeValue(diff);
    encoder.encode((byte) encoded[0], os);
    os.writeInt(encoded[1], encoded[0]);
  }

  /**
   * Encode the 63 AC coefficients in the dataUnit given, using the Huffman
   * encoder given, and place them in the output stream given.
   * @param dataUnit the data unit to work with, should contain 63 AC coeffs
   * @param encoder the huffman encoder to make use of
   * @param os the output stream to place the results in.
   */
  private void encodeACs(int[] dataUnit, HuffmanEncoder encoder,
                         BitOutputStream os) {
    int zeroRun = 0;
    for(int i = 1; i < dataUnit.length; i++) {
      int value = dataUnit[i];
      if(value == 0) {
        zeroRun++;
      } else {
        while(zeroRun >= 16) {
          /* We need to encode a run of 16 zeroes */
          encoder.encode((byte) 0xF0, os);
          zeroRun -= 16;
        }
        int[] encode = encodeValue(value);
        encoder.encode((byte) ((zeroRun << 4) | (byte) encode[0]), os);
        zeroRun = 0;
        os.writeInt(encode[1], encode[0]);
      }
    }
    if(zeroRun != 0) {
      encoder.encode((byte) 0, os);
    }
  }

  /**
   * Get the encoder with the id given for the scan given. If we have
   * it on hand, it is immediately returned; otherwise, it will be constructed
   * from a corresponding decoder and returned.
   * @param id the encoder's huffman table id
   * @param scan the scan in question
   * @return the huffman encoder.
   */
  private HuffmanEncoder getEncoder(int id, Scan scan) {
    if(!encoders.containsKey(id)) {
      encoders.put(id, new HuffmanEncoder(scan.getDecoder(id)));
    }
    return encoders.get(id);
  }

  /**
   * Compress the data given and place it in the list given.
   * @param scan the scan we're working with.
   * @param data the data to compress
   * @param output where the data should be placed.
   * TODO Optimize, refactor
   */
  private void compress(DecompressedScan scan, TIntList data,
      BitOutputStream os) {
    int index = 0;
    int rst = 0;
    boolean isRST = scan.isRSTEnabled();
    int mcus = scan.getNumberOfMCUsPerIteration();
    while(index < data.size()) {
      int[] lastDCs = new int[scan.getScanComponents()];
      /* TODO A solution for the repetition between this and the decompressor */
      for(int mcu = 0; mcu < mcus; mcu++) {
        for(byte cmp = 0; cmp < scan.getScanComponents(); cmp++) {
          for(byte hor = 0; hor < scan.getSubsampling()[cmp][0]; hor++) {
            for(byte vert = 0; vert < scan.getSubsampling()[cmp][1]; vert++) {
              /* TODO This looks to be incredibly slow */
              int[] dataUnit = data.toArray(index, 64);
              dataUnit = ZigZag.sequentialToZigZag(dataUnit);
              int tableId = scan.getTableId(cmp + 1);
              int dcTable = (tableId & 0xF0) >> 4;
              int acTable = (tableId & 0x0F) | 0x10;
              int dc = dataUnit[0];
              encodeDC(dc, lastDCs[cmp], getEncoder(dcTable, scan), os);
              lastDCs[cmp] = dc;
              encodeACs(dataUnit, getEncoder(acTable, scan), os);
              index += 64;
            }
          }
        }
      }
      os.writeToEndOfByte(1);
      if(isRST) {
        //os.writeInt(0xFF, 8);
        //os.writeInt(0xD0 & (rst % 8), 8);
        rst++;
      }
    }
  }

  /**
   * Compress the image scan given.
   * @param scan the scan to compress
   */
  public DecompressedScan process(DecompressedScan scan) {
    encoders = new TIntObjectHashMap<>();
    BitOutputStream os = new JPEGBitOutputStream();
    compress(scan, scan.getCoefficients(), os);
    scan.setData(os.data());
    return scan;
  }
}
