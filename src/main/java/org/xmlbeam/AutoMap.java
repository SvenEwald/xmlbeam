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
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;
import org.xmlbeam.util.intern.duplex.ExpressionType;

/**
 * @author sven
 */
public class AutoMap<T> extends AbstractMap<String, T> implements XBAutoMap<T>, DOMAccess {

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
            if (invocationContext.getxPathExpression() == null) {
                boundNode = baseNode;
                return;//xPath expression is optional for maps
            }
            final NodeList nodes = (NodeList) invocationContext.getxPathExpression().evaluate(baseNode, XPathConstants.NODESET);
            if ((nodes.getLength() == 0) && forWrite) {
                boundNode = invocationContext.getDuplexExpression().ensureExistence(baseNode);
            } else {
                boundNode = nodes.getLength() == 0 ? null : (Element) nodes.item(0);
            }
        }
    };

    private final Class<?> valueType;

    /**
     * @param baseNode
     * @param invocationContext
     * @param valueType
     *            map value type
     */
    public AutoMap(final Node baseNode, final InvocationContext invocationContext, final Class<?> valueType) {
        this.invocationContext = invocationContext;
        this.baseNode = baseNode;
        this.invocationContext.getProjector().addDOMChangeListener(domChangeTracker);
        this.typeConverter = invocationContext.getProjector().config().getTypeConverter();
        this.valueType = valueType;
    }

    /**
     * @see java.util.AbstractMap#clear()
     */
    @Override
    public void clear() {
        domChangeTracker.refreshForReadIfNeeded();
        if (boundNode != null) {
            DOMHelper.removeAllChildren(boundNode);
        }
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
    @SuppressWarnings("unchecked")
    @Override
    public T get(final CharSequence path) {
        return (T) get(path, invocationContext.getTargetComponentType());
    }

    @Override
    public <E> E get(final CharSequence path, final Class<E> asType) {
        if ((path == null) || (path.length() == 0)) {
            throw new IllegalArgumentException("Parameter path must not be empty or null");
        }
        domChangeTracker.refreshForReadIfNeeded();
        if (boundNode == null) {
            // we need not to evaluate, because our context node does not even exist.
            return null;
        }

        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(path);
        try {
            final XPathExpression expression = invocationContext.getProjector().config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            Node prevNode = (Node) expression.evaluate(boundNode, XPathConstants.NODE);
            InvocationContext tempContext = new InvocationContext(invocationContext.getResolvedXPath(), invocationContext.getxPath(), expression, duplexExpression, null, asType, invocationContext.getProjector());
            final E value = DefaultXPathEvaluator.convertToComponentType(tempContext, prevNode, asType);
            return value;
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, path);
        }
    }

    /**
     * Use given relative xpath to resolve the value.
     *
     * @param path
     *            relative xpath
     * @return value in DOM tree. null if no value is present.
     */
    @SuppressWarnings("unchecked")
    @Override
    public XBAutoList<T> getList(final CharSequence path) {
        return (XBAutoList<T>) getList(path, invocationContext.getTargetComponentType());
    }

    @Override
    public <E> XBAutoList<E> getList(final CharSequence path, final Class<E> oType) {
        if ((path == null) || (path.length() == 0)) {
            throw new IllegalArgumentException("Parameter path must not be empty or null");
        }
        domChangeTracker.refreshForReadIfNeeded();
        if (boundNode == null) {
            // Get is a readonly operation, thus
            // we can not create the context node here.
            // We can not even return a writeable instance.
            return AutoList.emptyList();
        }
        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(path);
        try {
            final XPathExpression expression = invocationContext.getProjector().config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            final InvocationContext tempContext = new InvocationContext(invocationContext.getResolvedXPath(), invocationContext.getxPath(), expression, duplexExpression, null, oType, invocationContext.getProjector());
            return new AutoList<E>(boundNode, tempContext);
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, path);
        }
    }

    /**
     * @deprecated
     * @see java.util.AbstractMap#containsKey(java.lang.Object)
     */
    @Deprecated
    @Override
    public boolean containsKey(final Object path) {
        if (!(path instanceof CharSequence)) {
            throw new IllegalArgumentException("parameter path must be a CharSequence containing a relative XPath expression.");
        }
        return containsKey(CharSequence.class.cast(path));
    }

    /**
     * Checks existence of value at given xpath.
     *
     * @param path
     * @return true if nonnull value exists at given path
     */
    @Override
    public boolean containsKey(final CharSequence path) {
        return get(path) != null;
    }

    /**
     * Like map.containsValue, but this map can not store null values.
     *
     * @param value
     * @return true if value is found in any element or attribute
     * @see java.util.AbstractMap#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        }
        return super.containsValue(value);
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
        if (boundNode == null) {
            // If there is no context node yet, ignore if
            // we had read the dom before. We need to create it now.
            domChangeTracker.domChanged();
        }
        domChangeTracker.refreshForWriteIfNeeded();
        assert boundNode != null : "Bound node does not exist. No evaluation possible";
        final Document document = DOMHelper.getOwnerDocumentFor(baseNode);
        final DuplexExpression duplexExpression = new DuplexXPathParser(invocationContext.getProjector().config().getUserDefinedNamespaceMapping()).compile(path);
        if ((ExpressionType.ATTRIBUTE == duplexExpression.getExpressionType()) && ProjectionInvocationHandler.isStructureChangingType(valueType)) {
            throw new IllegalArgumentException("Value of type " + valueType + "can not be written to XML attributes. Choose a different xpath expression or use a different map component type");
        }
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
                    // Dont't add the value, add a copy.
                    //parent.appendChild(DOMAccess.class.cast(value).getDOMNode());
                    DOMHelper.appendClone(parent, DOMAccess.class.cast(value).getDOMNode());
                }
                return previousValue;
            }
            assert boundNode != null : "Without bound node, there is no context where something could be created in.";
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
            throw new IllegalArgumentException("parameter path must be a CharSequence or String containing a relative XPath expression.");
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
            if (prevNode == null) {
                return null;
            }
            final T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, prevNode, invocationContext.getTargetComponentType());
            duplexExpression.deleteAllMatchingChildren(prevNode.getParentNode());
            return value;
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, xpath);
        }
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
        final Set<Map.Entry<String, T>> set = new TreeSet<Map.Entry<String, T>>(ENTRY_COMPARATOR) {

        };
        if ((invocationContext.getTargetComponentType().isInterface()) || (Node.class.isAssignableFrom(invocationContext.getTargetComponentType()))) {
            collectChildren(set, boundNode, ".");
        } else {
            collectChildrenValues(set, boundNode, ".");
        }
        return set;
    }

    private <E> void collectChildren(final Set<Entry<String, E>> set, final Node n, final String path) {
        if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            return;
        }
//        NodeList childNodes = (n.getNodeType()==Node.DOCUMENT_NODE) ? ((Document)n).getd n.getChildNodes();

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
            E value = DefaultXPathEvaluator.convertToComponentType(invocationContext, child, invocationContext.getTargetComponentType());
            if (value != null) {
                set.add(new SimpleEntry<String, E>(childPath, value));
            }
            collectChildren(set, child, childPath);
        }
    }

    private <E> void collectChildrenValues(final Set<Entry<String, E>> set, final Node n, final String path) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            return;
        }
        // Iterate attributes only when target type is String
        if (String.class.isAssignableFrom(invocationContext.getTargetComponentType())) {
            NamedNodeMap attributes = n.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    // map.put(path + "/@" + attributes.item(i).getNodeName(), attributes.item(i));
                    set.add(new SimpleEntry<String, E>(path + "/@" + attributes.item(i).getNodeName(), (E) (attributes.item(i).getNodeValue())));
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
                    final E value = (E) typeConverter.convertTo(invocationContext.getTargetComponentType(), stringContent, invocationContext.getExpressionFormatPattern());
                    //T value = DefaultXPathEvaluator.convertToComponentType(invocationContext, child, invocationContext.getTargetComponentType());
                    if (value != null) {
                        set.add(new SimpleEntry<String, E>(childPath, value));
                    }
                }
            }
            collectChildrenValues(set, child, childPath);
        }
    }

    /**
     * @return bound node for this automap
     */
    public Node getNode() {
        domChangeTracker.refreshForReadIfNeeded();
        return boundNode;
    }

    /**
     * @return XBAutoMap.class
     * @see org.xmlbeam.dom.DOMAccess#getProjectionInterface()
     */
    @Override
    public Class<?> getProjectionInterface() {
        return XBAutoMap.class;
    }

    /**
     * @return element that this map is bound to.
     * @see org.xmlbeam.dom.DOMAccess#getDOMNode()
     */
    @Override
    public Node getDOMNode() {
        return boundNode;
    }

    /**
     * @return document for this map
     * @see org.xmlbeam.dom.DOMAccess#getDOMOwnerDocument()
     */
    @Override
    public Document getDOMOwnerDocument() {
        return DOMHelper.getOwnerDocumentFor(boundNode);
    }

    /**
     * @return base element that was used when this map was created.
     * @see org.xmlbeam.dom.DOMAccess#getDOMBaseElement()
     */
    @Override
    public Element getDOMBaseElement() {
        if (baseNode.getNodeType() == Node.DOCUMENT_NODE) {
            return ((Document) baseNode).getDocumentElement();
        }
        return (Element) baseNode;
    }

    /**
     * @return XML String representation for this map.
     * @see org.xmlbeam.dom.DOMAccess#asString()
     */
    @Override
    public String asString() {
        return this.invocationContext.getProjector().asString(this);
    }

    /**
     * @param path
     * @param value
     * @return this
     * @see org.xmlbeam.dom.DOMAccess#create(java.lang.String, java.lang.Object)
     */
    @Override
    public DOMAccess create(final String path, final Object value) {
        if (!valueType.isInstance(value)) {
            throw new IllegalArgumentException("value must be the component type");
        }
        put(path, (T) value);
        return this;
    }

}
