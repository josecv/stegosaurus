# The compiler
CC=gcc
CXX=g++
# Compile with debug symbols for now.
CFLAGS=-Wall -Wextra -g -fPIC -pedantic -std=c99
SRCROOT=src/main
CROOT=$(SRCROOT)/c
CXXROOT=$(SRCROOT)/cpp
CXXFLAGS=$(CFLAGS)
LDFLAGS=-ljpeg
OBJECTS=build/blockiness.o build/crop.o build/dest_mgr.o build/src_mgr.o

all: libstegosaurus.so

libstegosaurus.so: $(OBJECTS)
	$(CC) -shared -Wl,-soname,$@ -o $@ $(OBJECTS) $(LDFLAGS)

build/%.o : $(CROOT)/%.c $(CROOT)/%.h build/
	$(CC) $(CFLAGS) -c $< -o $@

build/%.o: $(CXXROOT)/%.cpp $(CXXROOT)/%.h build/
	$(CXX) $(CXXFLAGS) -c $< -o $@

build/:
	mkdir -p build/

clean:
	rm -f build/*.o libstegosaurus.so
