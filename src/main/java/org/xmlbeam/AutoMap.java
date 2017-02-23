/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.XBAutoMap;

/**
 * @author sven
 */
public class AutoMap<T> extends AbstractMap<String, T> implements XBAutoMap<T>, DOMChangeListener {

    private final InvocationContext invocationContext;
    private final Node baseNode;
    private Node parent;
    private final DomChangeTracker domChangeTracker = new DomChangeTracker() {
        @Override
        void refresh(final boolean forWrite) throws XPathExpressionException {
            final NodeList nodes = (NodeList) invocationContext.getxPathExpression().evaluate(baseNode, XPathConstants.NODESET);;
            if ((nodes.getLength() == 0) && forWrite) {
                parent = invocationContext.getDuplexExpression().ensureParentExistence(baseNode);
            } else {
                parent = nodes.getLength() == 0 ? null : (Element) nodes.item(0).getParentNode();
            }
        }
    };

    /**
     * @param baseNode
     * @param invocationContext
     */
    public AutoMap(final Node baseNode, final InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
        this.baseNode = baseNode;
        this.invocationContext.getProjector().addDOMChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(final String key, final T value) {
        domChangeTracker.refreshForWriteIfNeeded();
        return null;
    }

    /**
     * @see org.xmlbeam.intern.DOMChangeListener#domChanged()
     */
    @Override
    public void domChanged() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        domChangeTracker.refreshForReadIfNeeded();
        if (parent == null) {
            return Collections.emptySet();
        }
        final Map<String, T> map = new HashMap<String, T>();
        collectChildren(map, parent, ".");
        return new Set<Map.Entry<String, T>>() {

            @Override
            public int size() {
                return map.size();
            }

            @Override
            public boolean isEmpty() {
                return map.isEmpty();
            }

            @Override
            public boolean contains(final Object o) {
                return false;
            }

            @Override
            public Iterator<java.util.Map.Entry<String, T>> iterator() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object[] toArray() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T[] toArray(final T[] a) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean add(final java.util.Map.Entry<String, T> e) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean remove(final Object o) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean containsAll(final Collection<?> c) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean addAll(final Collection<? extends java.util.Map.Entry<String, T>> c) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean retainAll(final Collection<?> c) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean removeAll(final Collection<?> c) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void clear() {
                // TODO Auto-generated method stub

            }

        };
    }

    private <T> void collectChildren(final Map<String, T> map, final Node n, final String path) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            return;
        }
        NamedNodeMap attributes = n.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                map.put(path + "/@" + attributes.item(i).getNodeName(), attributes.item(i));
            }
        }
        NodeList childNodes = n.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node child = childNodes.item(i);
                String childPath = path + "/" + child.getNodeName();
                map.put(childPath, child);
                collectChildren(map, child, childPath);
            }
        }
    }

}
