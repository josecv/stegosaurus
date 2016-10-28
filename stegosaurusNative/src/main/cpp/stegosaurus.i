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
/* File: stegosaurus.i */
%module stegosaurus
%{
/* For good measure */
#include "jpeglib.h"
/* Pull in some includes */
#include "jpeg_component.h"
#include "jpeg_image.h"
#include "coefficient_accessor.h"
%}

/* Create the JoctetArray class, to wrap around (gasp) JOCTET arrays */
%apply signed char {JOCTET};
%apply unsigned short {JCOEF};
%include "carrays.i"
%array_class(JOCTET, JoctetArray);
%array_class(int, cppIntArray);

/* Now bring in our classes */

%include "jpeg_component.h"

/* JPEGLibException has to be properly mapped to a java exception. */

%typemap(javabase) JPEGLibException "java.lang.RuntimeException"
%typemap(javacode) JPEGLibException %{
  public String getMessage() {
    return what();
  }
%}

%include "jpeg_lib_exception.h"

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

%include "jpeg_image.h"

%include "coefficient_accessor.h"
