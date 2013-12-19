/* File: stegosaurus.i */
%module stegosaurus
%{
/* For good measure */
#include "jpeglib.h"
/* Pull in some includes */
#include "cpp/jpeg_component.h"
#include "cpp/jpeg_image.h"
#include "cpp/coefficient_accessor.h"
#include "cpp/jpeg_context.h"
%}

/* Create the JoctetArray class, to wrap around (gasp) JOCTET arrays */
%apply signed char {JOCTET};
%apply unsigned short {JCOEF};
%include "carrays.i"
%array_class(JOCTET, JoctetArray);

/* Now bring in our classes */

%include "cpp/jpeg_component.h"

/* JPEGLibException has to be properly mapped to a java exception. */

%typemap(javabase) JPEGLibException "java.lang.RuntimeException"
%typemap(javacode) JPEGLibException %{
  public String getMessage() {
    return what();
  }
%}

%include "cpp/jpeg_lib_exception.h"

/* We need to ensure that the JPEGImages returned by other JPEGImages are
 * garbage collected apropriately.
 * In other words, any images constructed by writeNew must be freed by the
 * Java side (since no pointer is kept on the native side)
 */

%newobject JPEGImage::writeNew();

%typemap(throws, throws="JPEGLibException") JPEGLibException {
  jclass excep = jenv->FindClass("stegosaurus/cpp/JPEGLibException");
  if(excep) {
    jenv->ThrowNew(excep, $1.what());
  }
  return $null;
}

%include "cpp/jpeg_image.h"

%include "cpp/coefficient_accessor.h"
%include "cpp/jpeg_context.h"
