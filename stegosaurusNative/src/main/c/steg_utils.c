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
#include "steg_utils.h"

void read_file(JOCTET **buf, long *bufsize, FILE *fp) {
  if(fseek(fp, 0L, SEEK_END) == 0) {
    *bufsize = ftell(fp);
    if(*bufsize == -1) {
      perror("read_file");
      return;
    }
    *buf = (JOCTET *) malloc(sizeof(JOCTET) * (*bufsize));
    if(!fseek(fp, 0L, SEEK_SET)) {
      *bufsize = fread(*buf, sizeof(JOCTET), *bufsize, fp);
    }
  }
}
