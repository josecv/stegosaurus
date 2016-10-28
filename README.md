Stegosaurus JPEG steganography library.
=======================================

Usage
-----

Stegosaurus uses [Guice](https://github.com/google/guice) to do dependency
injection, so it is really simple to integrate into any Guice-using project.

All you have to do to integrate it is install the module
`com.stegosaurus.stegosaurus.StegosaurusModule`.
For example:

```java
  Injector injector = Guice.createInjector(new StegosaurusModule())
```

Once you've done that, to use Stegosaurus you just have get your hands on an instance
of `com.stegosaurus.stegosaurus.StegosaurusFacade`, then to create the steganographic image:

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

You may want to fiddle with the parameters in use by the genetic algorithms.
In order to do this, just write a new module that extends from `StegosaurusModule` and
replaces the appropriate protected parameter getter with one that returns what you want.

Thus, say you wish to have 100 generations instead of the default 50 for the blockiness
optimizer:

```java
  public class MyCustomModule extends StegosaurusModule {
    @Override
    protected int getBNumberOfGenerations() {
      return 100;
    }
  }
```

And install this module instead:

```java
  Injector = Guice.createInjector(new MyCustomModule());
```

See `StegosaurusModule` to see the list of all available parameters
and their default values.

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
