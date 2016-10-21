Stegosaurus JPEG steganography library.
=======================================


Building
--------

To build stegosaurus you will need:
  - a jdk
  - gradle
  - a gcc toolset
  - libgtest (headers and library)
  - libjpeg  (headers and library)
  - swig 2

Then just go `gradle build`

Usage
-----

Stegosaurus uses [Guice](https://github.com/google/guice) to do dependency
injection, so it is really simple to integrate into any Guice-using project.

All you have to do is get your hands on an instance of `com.stegosaurus.stegosaurus.StegosaurusFacade`,
then to create the steganographic image:

```java
  StegosaurusFacade stegosaurus = injector.getInstance(StegosaurusFacade.class);
  InputStream myImageStream = openMyImage();
  OutputStream myResultingImage = openOutputImage();
  String message = "My nice message";
  String key = "Secret!";
  /* This will place the JPEG image containing the steganographic payload in the
   * myResultingImage stream */
  stegosaurus.embed(myImageStream, myResultingImage, message, key);
```

Getting a message out of an image is equally simple:

```java
  StegosaurusFacade stegosaurus = injector.getInstance(StegosaurusFacade.class);
  InputStream myImageWithPayload = openImage();
  String key = "Secret!";
  String result = stegosaurus.extract(myImageWithPayload, key);
```

Algorithm details
-----------------

Stegosaurus makes use of an exceedingly clever [algorithm](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.705.779&rep=rep1&type=pdf)
described by Lifang Yu et al. 

The algorithm embeds messages into JPEG images by permutting the image's DC
coefficients using a permutation initially derived from the user's key, then
rekeyed with a seed selected to minimize changes to the image. A plus-minus
sequence is used to decide how each bit of data is embedded into the image:
a + in the sequence indicates the coefficient is incremented, while a -
idicates it is decremeted. Both the new seed and the plus-minus sequence
are derived using genetic algorithms.

In particular, the genetic algorithm for the plus-minus sequence seeks to
minimize a second order property of the image known as "blockiness" which
represents the degree to which 64-pixel blocks differ from their neighbors
in the decompressed, color-space image.
