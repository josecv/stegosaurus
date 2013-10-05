# THIS MAKEFILE IS AN EXTREMELY TEMPORARY SOLUTION!!
# The compiler
CC=gcc
CXX=g++
# Compile with debug symbols for now.
FLAGS=-Wall -Wextra -g -fPIC -pedantic -Wsign-compare
CFLAGS=$(FLAGS)
CXXFLAGS=$(FLAGS)
SRCROOT=stegosaurus-native/src/main
CROOT=$(SRCROOT)/c
CXXROOT=$(SRCROOT)/cpp
TESTROOT=stegosaurus-native/src/test
CXXTEST=$(TESTROOT)/cpp
LDFLAGS=-ljpeg
OBJECTS=build/crop.o build/dest_mgr.o build/src_mgr.o build/jpeg_image.o \
	build/jpeg_context.o build/jpeg_component.o build/coefficient_accessor.o \
	build/steg_utils.o
SWIGDIR=stegosaurus-java/src/main/java/com/stegosaurus/cpp
SWIGPACKAGE=com.stegosaurus.cpp
SWIGFLAGS=-package com.stegosaurus.cpp -outdir $(SWIGDIR)
SWIG=swig
SWIGWRAP=$(SRCROOT)/stegosaurus_wrap.cxx
WRAPOBJ=build/stegosaurus_wrap.o
JAVAINC=-I/usr/lib/jvm/icedtea-bin-7/include -I/usr/lib/jvm/icedtea-bin-7/include/linux

all: libstegosaurus.so steg_tests

test: all
	./steg_tests

steg_tests: $(OBJECTS)
	$(CXX) -lgtest $(LDFLAGS) $(CXXFLAGS) $(OBJECTS) $(CXXTEST)/test_run.cpp -o $@

libstegosaurus.so: $(OBJECTS) $(WRAPOBJ)
	$(CXX) -shared -Wl,-soname,$@ -o $@ $^ $(LDFLAGS)

build/%.o : $(CROOT)/%.c $(CROOT)/%.h build/
	$(CXX) $(CFLAGS) -c $< -o $@

build/%.o: $(CXXROOT)/%.cpp $(CXXROOT)/%.h build/
	$(CXX) $(CXXFLAGS) -c $< -o $@

build/stegosaurus_wrap.o: $(SWIGWRAP)
	$(CXX) $(JAVAINC) $(CXXFLAGS) -c $< -o $@

build/:
	mkdir -p build/

$(SWIGWRAP): $(SRCROOT)/stegosaurus.i $(OBJECTS) $(SWIGDIR)
	$(SWIG) $(SWIGFLAGS) -c++ -java $<

$(SWIGDIR):
	mkdir -p $(SWIGDIR)

clean:
	rm -f build/*.o libstegosaurus.so steg_tests $(SWIGWRAP)
	rm -f $(SWIGDIR)/*.java
