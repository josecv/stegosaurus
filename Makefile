# THIS MAKEFILE IS AN EXTREMELY TEMPORARY SOLUTION!!
# The compiler
CC=gcc
CXX=g++
# Compile with debug symbols for now.
FLAGS=-Wall -Wextra -g -fPIC -pedantic
CFLAGS=$(FLAGS)
CXXFLAGS=$(FLAGS)
SRCROOT=src/main
CROOT=$(SRCROOT)/c
CXXROOT=$(SRCROOT)/cpp
TESTROOT=src/test
CXXTEST=$(TESTROOT)/cpp
LDFLAGS=-ljpeg
OBJECTS=build/blockiness.o build/crop.o build/dest_mgr.o build/src_mgr.o \
	build/jpeg_image.o build/jpeg_context.o build/jpeg_component.o \
	build/coefficient_accessor.o build/steg_utils.o

all: libstegosaurus.so steg_tests

test: all
	./steg_tests

steg_tests: $(OBJECTS)
	$(CXX) -lgtest $(LDFLAGS) $(CXXFLAGS) $(OBJECTS) $(CXXTEST)/test_run.cpp -o $@

libstegosaurus.so: $(OBJECTS)
	$(CXX) -shared -Wl,-soname,$@ -o $@ $(OBJECTS) $(LDFLAGS)

build/%.o : $(CROOT)/%.c $(CROOT)/%.h build/
	$(CXX) $(CFLAGS) -c $< -o $@

build/%.o: $(CXXROOT)/%.cpp $(CXXROOT)/%.h build/
	$(CXX) $(CXXFLAGS) -c $< -o $@

build/:
	mkdir -p build/

clean:
	rm -f build/*.o libstegosaurus.so steg_tests

