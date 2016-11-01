/**
 *  Copyright 2016 Sven Ewald
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.types.ProjectedList;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author sven
 * @param <E>
 *            Component type
 */
class XBProjectedList_hide<E> implements ProjectedList<E> {

    /**
     */
    private final class IteratorImplementation implements ListIterator<E> {
        private int pos;
        private NodeList nodes;
        private Node currentItem = null;

        IteratorImplementation(int startpos) {
            pos = startpos;
            updateNodeLIst();
        }

        private void updateNodeLIst() {
            try {
                nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                throw new XBException("Unexpected error on evaluation", e);
            }
        }

        @Override
        public boolean hasNext() {
            return pos < nodes.getLength();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return convertToComponentType(currentItem = nodes.item(pos++), invocationContext.getTargetComponentType());
        }

        @Override
        public void remove() {
            if (currentItem == null) {
                throw new IllegalStateException();
            }
            DOMHelper.trim(currentItem.getParentNode());
            currentItem.getParentNode().removeChild(currentItem);
            currentItem = null;
            updateNodeLIst();
        }

        @Override
        public boolean hasPrevious() {
            return pos > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return convertToComponentType(currentItem = nodes.item(--pos), invocationContext.getTargetComponentType());

        }

        @Override
        public int nextIndex() {
            return pos + 1;
        }

        @Override
        public int previousIndex() {
            return pos - 1;
        }

        @Override
        public void set(E o) {
            if (currentItem == null) {
                throw new IllegalStateException();
            }
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            currentItem.setTextContent(asString);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.ListIterator#add(java.lang.Object)
         */
        @Override
        public void add(E o) {
            Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            newElement.setTextContent(asString);
            if (pos + 1 >= nodes.getLength()) {
                parent.appendChild(newElement);
            } else {
                parent.insertBefore(newElement, nodes.item(pos + 1));
            }
            updateNodeLIst();
        }
    }

    private final static Object[] EMPTY_ARRAY = {};
    final Element parent;
    List<Node> content = new LinkedList<Node>();
    private final InvocationContext invocationContext;
    private final XPathExpression expression;
    private Node baseNode;

    /**
     * @param expression
     * @param node
     * @param invocationContext
     * @throws XPathExpressionException
     */
    public XBProjectedList_hide(Node node, XPathExpression expression, InvocationContext invocationContext) throws XPathExpressionException {
        this.invocationContext = invocationContext;
        final NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        if (nodes.getLength() == 0) {
            parent = invocationContext.getDuplexExpression().ensureParentExistence(node);
        } else {
            parent = (Element) nodes.item(0).getParentNode();
            for (int i = 0; i < nodes.getLength(); ++i) {
                content.add(nodes.item(i));
            }
        }
        this.expression = expression;
        this.baseNode = node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(E o) {
        Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        newElement.setTextContent(asString);
        parent.appendChild(newElement);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, E o) {
        try {
            Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            newElement.setTextContent(asString);
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            parent.insertBefore(newElement, nodes.item(index));
        } catch (XPathExpressionException e) {
            throw new XBException("Unexcpeted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> arg0) {
        if (arg0.isEmpty()) {
            return false;
        }
        for (E o : arg0) {
            Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            newElement.setTextContent(asString);
            parent.appendChild(newElement);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> arg1) {
        if (arg1.isEmpty()) {
            return false;
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            Node refNode = nodes.item(index);
            for (E o : arg1) {
                Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
                final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
                newElement.setTextContent(asString);
                parent.insertBefore(newElement, refNode);
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexcpeted evaluation error", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
        invocationContext.getDuplexExpression().deleteAllMatchingChildren(parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());

        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                if (asString.equals(nodes.item(i).getTextContent())) {
                    return true;
                }
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> arg0) {
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            nextObj: for (Object o : arg0) {
                final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
                for (int i = 0; i < nodes.getLength(); ++i) {
                    if (asString.equals(nodes.item(i).getTextContent())) {
                        continue nextObj;
                    }
                }
                return false;
            }
            return true;
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#get(int)
     */
    @Override
    public E get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            if (index >= nodes.getLength()) {
                throw new IndexOutOfBoundsException();
            }

            return convertToComponentType(nodes.item(index), invocationContext.getTargetComponentType());
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /**
     * @param item
     * @param targetComponentType
     * @return
     */
    private E convertToComponentType(Node item, Class<?> targetComponentType) {
        TypeConverter typeConverter = invocationContext.getProjector().config().getTypeConverter();
        if (typeConverter.isConvertable(invocationContext.getTargetComponentType())) {
            return (E) typeConverter.convertTo(targetComponentType, item.getTextContent(), invocationContext.getExpressionFormatPattern());
        }
        if (Node.class.equals(targetComponentType)) {
            return (E) item;
        }
        if (targetComponentType.isInterface()) {
            Object subprojection = invocationContext.getProjector().projectDOMNode(item, targetComponentType);
            return (E) subprojection;
        }
        throw new IllegalArgumentException("Return type " + targetComponentType + " is not valid for a ProjectedList using the current type converter:" + invocationContext.getProjector().config().getTypeConverter()
                + ". Please change the return type to a sub projection or add a conversion to the type converter.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            return -1;
        }
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());

        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                if (asString.equals(nodes.item(i).getTextContent())) {
                    return i;
                }
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return size() < 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return new IteratorImplementation(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            return -1;
        }
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());

        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            for (int i = nodes.getLength() - 1; i > 0; --i) {
                if (asString.equals(nodes.item(i).getTextContent())) {
                    return i;
                }
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return new IteratorImplementation(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(int arg0) {
        return new IteratorImplementation(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        if (asString == null) {
            return false;
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node item = nodes.item(i);
                if (asString.equals(item.getTextContent())) {
                    continue;
                }
                DOMHelper.trim(item.getParentNode());
                item.getParentNode().removeChild(item);
                return true;
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            if (index >= nodes.getLength()) {
                throw new IndexOutOfBoundsException();
            }
            Node item = nodes.item(index);
            DOMHelper.trim(item.getParentNode());
            item.getParentNode().removeChild(item);
            return convertToComponentType(item, invocationContext.getTargetComponentType());
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> arg0) {
        if (arg0.isEmpty()) {
            return false;
        }
        boolean changed = false;

        final Set<String> matches = new HashSet<String>();
        for (Object o : arg0) {
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            matches.add(asString);
        }

        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node item = nodes.item(i);
                if (!matches.contains(item.getTextContent())) {
                    continue;
                }
                DOMHelper.trim(item.getParentNode());
                item.getParentNode().removeChild(item);
                changed = true;
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return changed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> arg0) {
        boolean changed = false;

        final Set<String> matches = new HashSet<String>();
        for (Object o : arg0) {
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            matches.add(asString);
        }

        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node item = nodes.item(i);
                if (matches.contains(item.getTextContent())) {
                    continue;
                }
                DOMHelper.trim(item.getParentNode());
                item.getParentNode().removeChild(item);
                changed = true;
            }
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
        return changed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E o) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            if (index >= nodes.getLength()) {
                throw new IndexOutOfBoundsException();
            }
            final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
            E result = convertToComponentType(nodes.item(index), invocationContext.getTargetComponentType());
            nodes.item(index).setTextContent(asString);
            return result;
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#size()
     */
    @Override
    public int size() {
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            return nodes.getLength();
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<E> subList(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        if (isEmpty()) {
            return EMPTY_ARRAY;
        }
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            Object[] result = new Object[nodes.getLength()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = convertToComponentType(nodes.item(i), invocationContext.getTargetComponentType());
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        try {
            NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
            final int size = nodes.getLength();
            Class<?> componentType = a.getClass().getComponentType();
            if (a.length < size) {
                a = (T[]) java.lang.reflect.Array.newInstance(componentType, size);
            }
            for (int i = 0; i < a.length; ++i) {
                a[i] = (T) convertToComponentType(nodes.item(i), componentType);
            }
            if (a.length > size)
                a[size] = null;
            return a;
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpexted evaluation error", e);
        }
    }

}
