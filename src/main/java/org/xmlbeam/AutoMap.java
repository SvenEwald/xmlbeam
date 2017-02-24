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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.XBAutoMap;
import java.util.Map.Entry;

/**
 * @author sven
 */
public class AutoMap<T> extends AbstractMap<String, T> implements XBAutoMap<T>, DOMChangeListener {

//    private static class Entry<T> implements java.util.Map.Entry<String, T> {
//        private final String key;
//        private T value;
//
//        Entry(final String key, final T value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public String getKey() {
//            return key;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public T getValue() {
//            return value;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//
//        @Override
//        public T setValue(T value) {
//            T old = this.value;
//            this.value = value;
//            return old;
//        }
//
//    }

    private static final Comparator<Entry<String, ?>> ENTRY_COMPARATOR = new Comparator<Entry<String, ?>>() {
        @Override
        public int compare(Entry<String, ?> o1, Entry<String, ?> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    };

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
        final Set<Map.Entry<String, T>> set = new TreeSet<Map.Entry<String, T>>(ENTRY_COMPARATOR);
        collectChildren(set, parent, ".");
        return set;
    }

    private <T> void collectChildren(final Set<Entry<String, T>> set, final Node n, final String path) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            return;
        }
        // Iterate attributes only when target type is String
        if (String.class.isAssignableFrom(invocationContext.getTargetComponentType())) {
            NamedNodeMap attributes = n.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    // map.put(path + "/@" + attributes.item(i).getNodeName(), attributes.item(i));                
                    set.add(new SimpleEntry<String, T>(path + "/@" + attributes.item(i).getNodeName(), (T) attributes.item(i)));
                }
            }
        }
        NodeList childNodes = n.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node child = childNodes.item(i);
                String childPath = path + "/" + child.getNodeName();
                T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, child, invocationContext.getTargetComponentType());
                if (value != null) {
                    set.add(new SimpleEntry<String, T>(childPath, value));
                }
                collectChildren(set, child, childPath);
            }
        }
    }

}
