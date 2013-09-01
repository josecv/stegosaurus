# The compiler
CC=gcc
CXX=g++
# Compile with debug symbols for now.
CFLAGS=-Wall -Wextra -g -fPIC -pedantic -std=c99
CXXFLAGS=$(CFLAGS)
LDFLAGS=-ljpeg
OBJECTS=build/blockiness.o build/crop.o build/dest_mgr.o build/src_mgr.o

all: libstegosaurus.so

libstegosaurus.so: $(OBJECTS)
	$(CC) -shared -Wl,-soname,$@ -o $@ $(OBJECTS) $(LDFLAGS)

build/%.o : src/c/%.c src/c/%.h build/
	$(CC) $(CFLAGS) -c $< -o $@

build/%.o: src/c/%.cpp src/c/%.h build/
	$(CXX) $(CXXFLAGS) -c $< -o $@

build/:
	mkdir -p build/

clean:
	rm -f build/*.o libstegosaurus.so
