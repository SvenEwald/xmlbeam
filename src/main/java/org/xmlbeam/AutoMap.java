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
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.exceptions.XBPathException;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;

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
     * @deprecated use get(CharSequence) instead. Key needs to be of CharSequence/String. No sense
     *             in using object in this case.
     * @param path
     * @return value at relative path
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    @Deprecated
    public T get(final Object path) {
        if (!(path instanceof CharSequence)) {
            throw new IllegalArgumentException("parameter path must be a CharSequence containing a relative XPath expression.");
        }
        return get(CharSequence.class.cast(path));
    }

    /**
     * Use given relative xpath to resolve the value.
     *
     * @param path
     *            relative xpath
     * @return value in DOM tree. null if no value is present.
     */
    public T get(final CharSequence path) {
        if ((path == null) || (path.length() == 0)) {
            throw new IllegalArgumentException("Parameter path must not be empty or null");
        }
        domChangeTracker.refreshForReadIfNeeded();

        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(path);
        try {
            final XPathExpression expression = invocationContext.getProjector().config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            Node prevNode = (Node) expression.evaluate(boundNode, XPathConstants.NODE);
            final T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, prevNode, invocationContext.getTargetComponentType());
            return value;
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, path);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(final String path, final T value) {
        if (path == null) {
            throw new IllegalArgumentException("Parameter path must not be null");
        }
        if (value == null) {
            return remove(path);
        }
        domChangeTracker.refreshForWriteIfNeeded();
        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(path);
        try {
            final XPathExpression expression = invocationContext.getProjector().config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            Node prevNode = (Node) expression.evaluate(boundNode, XPathConstants.NODE);
            final T previousValue = DefaultXPathEvaluator.convertToComponentType(invocationContext, prevNode, invocationContext.getTargetComponentType());
            if (ProjectionInvocationHandler.isStructureChangingValue(value)) {
                final Element parent = duplexExpression.ensureParentExistence(boundNode);
                if (Node.class.isAssignableFrom(invocationContext.getTargetComponentType())) {
                    if (!(value instanceof Node)) {
                        throw new IllegalArgumentException("Parameter value is not a DOM node.");
                    }
                    parent.appendChild((Node) value);
                    return previousValue;
                }
                if (invocationContext.getTargetComponentType().isInterface()) {
                    if (!(value instanceof DOMAccess)) {
                        throw new IllegalArgumentException("Parameter value is not a subprojection.");
                    }
                    parent.appendChild(DOMAccess.class.cast(value).getDOMNode());
                }
                return previousValue;
            }

            Node node = duplexExpression.ensureExistence(boundNode);
            node.setTextContent(value.toString());

            return previousValue;
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, path);
        }
    }

    /**
     * @deprecated use remove(CharSequence) instead.
     * @param path
     * @return previous value
     * @see java.util.AbstractMap#remove(java.lang.Object)
     */
    @Deprecated
    @Override
    public T remove(final Object path) {
        if (!(path instanceof CharSequence)) {
            throw new IllegalArgumentException("parameter path must be a CharSequence containing a relative XPath expression.");
        }
        return remove(CharSequence.class.cast(path));
    }

    /**
     * Remove element at relative location.
     *
     * @param xpath
     * @return previous value.
     */
    public T remove(final CharSequence xpath) {
        if ((xpath == null) || (xpath.length() == 0)) {
            throw new IllegalArgumentException("Parameter path must not be empty or null");
        }
        domChangeTracker.refreshForReadIfNeeded();

        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(xpath);
        try {
            final XPathExpression expression = invocationContext.getProjector().config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            Node prevNode = (Node) expression.evaluate(boundNode, XPathConstants.NODE);
            final T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, prevNode, invocationContext.getTargetComponentType());
            duplexExpression.deleteAllMatchingChildren(prevNode.getParentNode());
            return value;
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, xpath);
        }
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
                    set.add(new SimpleEntry<String, T>(path + "/@" + attributes.item(i).getNodeName(), (T) (attributes.item(i).getNodeValue())));
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
