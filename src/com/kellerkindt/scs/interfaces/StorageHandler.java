/*
 * ShowCaseStandalone
 * Copyright (c) 2016-01-10 19:40 +01 by Kellerkindt, <copyright at kellerkindt.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.kellerkindt.scs.interfaces;

import java.io.IOException;
import java.util.Collection;


public interface StorageHandler<E> extends Threaded {
    
    /**
     * Loads as many elements as possible
     * @return {@link Collection} of loaded elements
     * @throws IOException On an critical issue, preventing further loading
     */
    Collection<E> loadAll() throws IOException;

    /**
     * Enqueues the given elements to be saved
     *
     * @param elements {@link Iterable} to save the elements of
     */
    void save(Iterable<E> elements);

    /**
     * Enqueues the given element to be saved
     *
     * @param element Element to save
     * @return given element
     */
    E save(E element);

    /**
     * Enqueues the given element to be deleted
     *
     * @param element Element to delete
     */
    void delete(E element);
    
    /**
     * Writes all cached data to the {@link StorageHandler}'s destination
     * @throws IOException On an critical issue, preventing further flushing
     */
    void flush() throws IOException;
}
