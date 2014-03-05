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
