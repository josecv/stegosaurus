/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
