package com.stegosaurus.jpeg;

import org.apache.commons.lang3.mutable.MutableInt;

import gnu.trove.list.TIntList;

/**
 * Allows for the quantization and dequantization of JPEG image scans.
 */
public class JPEGQuantizer {
  /**
   * Private CTOR.
   */
  private JPEGQuantizer() { }

  /**
   * Get the quantization table for a given compoent.
   * @param comp the component
   * @param scan the scan.
   * @return the table.
   */
  private static byte[] getQuantizationTable(byte comp, Scan scan) {
    /* Oftentimes both chroma channels use the same table, but the
     * header doesn't bother to tell us that, so we have to guess.
     */
    if(scan.hasQuantizationTable(comp)) {
      return scan.getQuantizationTable(comp);
    } else if(comp != 0 && scan.hasQuantizationTable(1)) {
      return scan.getQuantizationTable(1);
    } else {
      /* XXX */
      throw new RuntimeException("This should probably be replaced");
    }
  }

  /**
   * Go through the scan and either multiply or divide every coefficient
   * by its corresponding quantization table.
   * @param scan the scan
   * @param multiply whether to multiply. If false, a division is performed.
   */
  private static void runThrough(DecompressedScan scan,
      final boolean multiply) {
    final TIntList list = scan.getCoefficients();
    final MutableInt index = new MutableInt();
    scan.forEachDataUnit(new DataUnitProcedure() {
      public void call(int mcu, byte comp, byte hor, byte vert, Scan scan) {
        byte[] table = getQuantizationTable(comp, scan);
        for(int i = index.intValue(), j = 0; j < 64; j++, i ++) {
          int coeff = list.get(i);
          if(multiply) {
            /* If we're going to multiply, best not bother with floating
             * point arithmetic at all.
             */
            coeff *= table[j];
          } else {
            float q = table[j];
            coeff = Math.round(coeff / q);
          }
          list.set(i, coeff);
        }
        index.add(64);
      }
    });
  }

  /**
   * Divide the scan given by its quantization matrices. This is obviously
   * a lossy operation. The resulting data is placed on the coefficient buffers
   * again.
   * @param scan the scan.
   */
  public static void quantize(DecompressedScan scan) {
    runThrough(scan, false);
  }

  /**
   * Multiply the scan given by its quantization matrices. The resulting
   * data is then placed on the Scan's coefficient buffers.
   * @param scan the scan.
   */
  public static void deQuantize(DecompressedScan scan) {
    runThrough(scan, true);
  }

}
