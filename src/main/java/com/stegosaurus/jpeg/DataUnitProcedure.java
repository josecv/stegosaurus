package com.stegosaurus.jpeg;

/**
 * A function to be executed for each data unit in a given scan.
 */
public interface DataUnitProcedure {
  /**
   * Call the procedure.
   * @param mcu the MCU being processed
   * @param component the component in the mcu (eg Luma -> 00)
   * @param hor the horizontal position of the component.
   * @param vert the vertical position of the component.
   * @param count the number of data units that have been processed so far.
   * @param scan the scan being worked on.
   */
  void call(int mcu, byte component, byte hor, byte vert, int count,
            Scan scan);
}
