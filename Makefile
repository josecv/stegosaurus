# The compiler
CC=gcc
# Compile with symbols for now.
CFLAGS=-Wall -Wextra -g -fPIC
LDFLAGS=-ljpeg
OBJECTS=build/blockiness.o build/crop.o build/dest_mgr.o build/src_mgr.o

all: libstegosaurus.so

libstegosaurus.so: $(OBJECTS)
	$(CC) -shared -Wl,-soname,$@ -o $@ $(OBJECTS) $(LDFLAGS)

build/%.o : src/c/%.c src/c/%.h
	mkdir -p build/
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f build/*.o libstegosaurus.so
