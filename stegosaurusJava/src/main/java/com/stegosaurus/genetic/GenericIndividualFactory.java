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
package com.stegosaurus.genetic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A generic factory for Individual instances, capable of building any
 * Individual with a constructor that only takes a Chromosome as a parameter.
 * @param <T> the type of the built instances.
 */
public class GenericIndividualFactory<T extends Individual<T>>
  implements IndividualFactory<T> {

  /**
   * The constructor for the class we're providing instances of.
   */
  private Constructor<? extends T> ctor;

  /**
   * CTOR.
   * @param klass the type of the built instances.
   * @throws NoSuchMethodException if the constructor is missing.
   */
  public GenericIndividualFactory(Class<? extends T> klass)
    throws NoSuchMethodException {
    this.ctor = klass.getConstructor(Chromosome.class);
  }

  /**
   * Construct a new Individual instance.
   * @param c the chromosome.
   * @throws ConstructionException if the Individual's constructor can't run.
   */
  public T build(Chromosome c) {
    try {
      return ctor.newInstance(c);
    } catch(IllegalAccessException | InstantiationException |
            InvocationTargetException e) {
      throw new ConstructionException(e);
    }
  }

  /* It makes sense for the following to be a runtime exception: the user
   * provides the class. If it can't be built because the constructor throws,
   * we don't have access to it, or whatever else may happen, that's the
   * caller's fault for providing us a bad constructor.
   */
  /**
   * Wraps around any exceptions that we get from attempting to build an
   * individual.
   */
  public static class ConstructionException extends RuntimeException {
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 123123123123123L;

    /**
     * Construct an instance, wrapping around the exception given.
     * @param e the exception to wrap around.
     */
    public ConstructionException(Exception e) {
      super(e.getClass().toString() + " : " + e.getMessage(), e);
    }
  }
}
