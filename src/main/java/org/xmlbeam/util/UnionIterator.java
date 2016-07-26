/**
 *  Copyright 2015 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam.util;

import java.util.Iterator;

/**
 * A read only Iterator implementation that unions two other iterators.
 *
 * @author sven
 * @param <T>
 *            Iterator type
 */
public class UnionIterator<T> implements Iterator<T> {

    private final Iterator<T> first;
    private final Iterator<T> second;

    /**
     * @param first
     * @param second
     */
    public UnionIterator(final Iterator<T> first, final Iterator<T> second) {
        if (first == null) {
            throw new NullPointerException("first iterator must not be null.");
        }
        if (second == null) {
            throw new NullPointerException("second iterator must not be null.");
        }
        if (first == second) {
            throw new NullPointerException("first and second iterators must not be the same.");
        }

        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    @Override
    public T next() {
        return first.hasNext() ? first.next() : second.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
