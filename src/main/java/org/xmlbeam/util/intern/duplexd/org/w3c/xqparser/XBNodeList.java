/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author sven
 */
public class XBNodeList implements List<org.w3c.dom.Node> {

    private final List<org.w3c.dom.Node> delegate = new LinkedList<org.w3c.dom.Node>();

    public static XBNodeList of(final NodeList nodelist) {
        XBNodeList list = new XBNodeList();
        list.addAll(DOMHelper.asList(nodelist));
        return list;
    }

    public static XBNodeList of(final org.w3c.dom.Node node) {
        XBNodeList list = new XBNodeList();
        list.add(node);
        return list;
    }

    @Override
    public boolean add(final org.w3c.dom.Node e) {
        if (e == null) {
            return false;
        }
        return delegate.add(e);
    }

    @Override
    public void add(final int index, final org.w3c.dom.Node element) {
        if (element == null) {
            return;
        }
        delegate.add(index, element);
    }

    @Override
    public boolean addAll(final Collection<? extends org.w3c.dom.Node> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends org.w3c.dom.Node> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public org.w3c.dom.Node get(final int index) {
        if (delegate.isEmpty()) {
            return null;
        }
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<org.w3c.dom.Node> iterator() {
        return delegate.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<org.w3c.dom.Node> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<org.w3c.dom.Node> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    @Override
    public org.w3c.dom.Node remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public org.w3c.dom.Node set(final int index, final org.w3c.dom.Node element) {
        return delegate.set(index, element);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<org.w3c.dom.Node> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

}
