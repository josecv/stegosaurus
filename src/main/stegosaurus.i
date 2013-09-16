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
/* Now tell swig to parse the header files */
%include "cpp/jpeg_component.h"
%include "cpp/jpeg_image.h"
%include "cpp/coefficient_accessor.h"
%include "cpp/jpeg_context.h"
