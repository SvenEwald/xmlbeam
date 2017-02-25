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
import java.util.Collections;
import java.util.Comparator;
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
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author sven
 */
public class AutoMap<T> extends AbstractMap<String, T> implements XBAutoMap<T>, DOMChangeListener {

    private static final Comparator<Entry<String, ?>> ENTRY_COMPARATOR = new Comparator<Entry<String, ?>>() {
        @Override
        public int compare(final Entry<String, ?> o1, final Entry<String, ?> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    };

    private final InvocationContext invocationContext;
    private final Node baseNode;
    private Node boundNode;
    private final TypeConverter typeConverter;
    private final DomChangeTracker domChangeTracker = new DomChangeTracker() {
        @Override
        void refresh(final boolean forWrite) throws XPathExpressionException {
            final NodeList nodes = (NodeList) invocationContext.getxPathExpression().evaluate(baseNode, XPathConstants.NODESET);;
            if ((nodes.getLength() == 0) && forWrite) {
                boundNode = invocationContext.getDuplexExpression().ensureExistence(baseNode);
            } else {
                boundNode = nodes.getLength() == 0 ? null : (Element) nodes.item(0);
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
        this.typeConverter = invocationContext.getProjector().config().getTypeConverter();
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
        if (boundNode == null) {
            return Collections.emptySet();
        }
        final Set<Map.Entry<String, T>> set = new TreeSet<Map.Entry<String, T>>(ENTRY_COMPARATOR);
        if ((invocationContext.getTargetComponentType().isInterface()) || (Node.class.isAssignableFrom(invocationContext.getTargetComponentType()))) {
            collectChildren(set, boundNode, ".");
        } else {
            collectChildrenValues(set, boundNode, ".");
        }
        return set;
    }

    private <T> void collectChildren(final Set<Entry<String, T>> set, final Node n, final String path) {
        if (n.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }
        NodeList childNodes = n.getChildNodes();
        if (childNodes == null) {
            return;
        }
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String childPath = path + "/" + child.getNodeName();
            T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, child, invocationContext.getTargetComponentType());
            if (value != null) {
                set.add(new SimpleEntry<String, T>(childPath, value));
            }
            collectChildren(set, child, childPath);
        }
    }

    private <T> void collectChildrenValues(final Set<Entry<String, T>> set, final Node n, final String path) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            return;
        }
        // Iterate attributes only when target type is String
        if (String.class.isAssignableFrom(invocationContext.getTargetComponentType())) {
            NamedNodeMap attributes = n.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    // map.put(path + "/@" + attributes.item(i).getNodeName(), attributes.item(i));
                    set.add(new SimpleEntry<String, T>(path + "/@" + attributes.item(i).getNodeName(), (T) attributes.item(i).getNodeValue()));
                }
            }
        }
        NodeList childNodes = n.getChildNodes();
        if (childNodes == null) {
            return;
        }
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            final String childPath = path + "/" + child.getNodeName();
            if (typeConverter.isConvertable(invocationContext.getTargetComponentType())) {
                final String stringContent = DOMHelper.directTextContent(child);
                if ((stringContent != null) && (!stringContent.isEmpty())) {
                    final T value = (T) typeConverter.convertTo(invocationContext.getTargetComponentType(), stringContent, invocationContext.getExpressionFormatPattern());
                    //T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, child, invocationContext.getTargetComponentType());
                    if (value != null) {
                        set.add(new SimpleEntry<String, T>(childPath, value));
                    }
                }
            }
            collectChildrenValues(set, child, childPath);
        }
    }

}
