#include "stegosaurus_error_manager.h"
#include "jpeg_lib_exception.h"

static void error_exit(j_common_ptr cinfo) {
  char msg[JMSG_LENGTH_MAX];
  (*cinfo->err->format_message) (cinfo, msg);
  JPEGLibException ex(msg);
  throw ex;
}

struct jpeg_error_mgr* stegosaurus_error_mgr(struct jpeg_error_mgr *err) {
  err = jpeg_std_error(err);
  err->error_exit = error_exit;
  return err;
}
