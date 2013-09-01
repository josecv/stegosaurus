# THIS MAKEFILE IS AN EXTREMELY TEMPORARY SOLUTION!!
# The compiler
CC=gcc
CXX=g++
# Compile with debug symbols for now.
FLAGS=-Wall -Wextra -g -fPIC -pedantic
CFLAGS=$(FLAGS) -std=c99
CXXFLAGS=$(FLAGS)
SRCROOT=src/main
CROOT=$(SRCROOT)/c
CXXROOT=$(SRCROOT)/cpp
TESTROOT=src/test
CXXTEST=$(TESTROOT)/cpp
LDFLAGS=-ljpeg
OBJECTS=build/blockiness.o build/crop.o build/dest_mgr.o build/src_mgr.o \
	build/bit_input_stream.o

all: libstegosaurus.so

test: steg_tests
	./steg_tests

steg_tests: libstegosaurus.so
	$(CXX) -lgtest $(LDFLAGS) $(OBJECTS) $(CXXTEST)/test_run.cpp -o steg_tests

libstegosaurus.so: $(OBJECTS)
	$(CXX) -shared -Wl,-soname,$@ -o $@ $(OBJECTS) $(LDFLAGS)

build/%.o : $(CROOT)/%.c $(CROOT)/%.h build/
	$(CC) $(CFLAGS) -c $< -o $@

build/%.o: $(CXXROOT)/%.cpp $(CXXROOT)/%.h build/
	$(CXX) $(CXXFLAGS) -c $< -o $@

build/:
	mkdir -p build/

clean:
	rm -f build/*.o libstegosaurus.so steg_tests

