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
package com.stegosaurus.concurrent;

import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * An object capable of providing an ExecutorService for stegosaurus classes
 * that need access to one.
 * Only provides a single executor service, which is built when first
 * requested and provided on every subsequent request.
 */
@Singleton
public class ListeningExecutorServiceProvider
  implements Provider<ListeningExecutorService> {

  private ListeningExecutorService service = null;

  /**
   * Get an executor service for this application.
   * @return the executor service.
   */
  public ListeningExecutorService get() {
    if(service == null) {
      service =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    }
    return service;
  }
}
