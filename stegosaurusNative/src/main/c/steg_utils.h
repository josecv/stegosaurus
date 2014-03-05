#ifndef STEG_UTILS
#define STEG_UTILS
#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"

/**
 * Read an entire file into a buffer. The buffer will be allocated for you.
 * @param buf pointer to the buffer.
 * @param bufsize pointer to some size info for the buffer.
 * @param fp the file
 */
void read_file(JOCTET **buf, long *bufsize, FILE *fp);

#endif
