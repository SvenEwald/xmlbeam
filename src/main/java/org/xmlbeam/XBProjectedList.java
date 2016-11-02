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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.ProjectedList;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.DOMHelper;

/**
 *
 */
public class XBProjectedList<E> extends AbstractList<E> implements ProjectedList<E>, DOMChangeListener {

    private InvocationContext invocationContext;
    private Element parent;
    private XPathExpression expression;
    private Node baseNode;
    private boolean needRefresh;
    private final List<Node> content = new ArrayList<Node>();

    public XBProjectedList(Node baseNode, XPathExpression expression, InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
        this.expression = expression;
        this.baseNode = baseNode;
        this.needRefresh = true;
        this.invocationContext.getProjector().addDOMChangeListener(this);
    }

    @Override
    public E get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        refreshForReadIfNeeded();
        if (index >= content.size()) {
            throw new IndexOutOfBoundsException();
        }
        return convertToComponentType(content.get(index), invocationContext.getTargetComponentType());
    }

    @Override
    public int size() {
        refreshForReadIfNeeded();
        return content.size();
    }

    @Override
    public E set(int index, E element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        refreshForReadIfNeeded();
        if (index >= content.size()) {
            throw new IndexOutOfBoundsException();
        }
        Node oldNode = content.get(index);
        E result = convertToComponentType(oldNode, invocationContext.getTargetComponentType());
        if (element instanceof Node) {
            Node newNode = ((Node) element).cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            content.set(index, newNode);
            return result;
        }
        if (element instanceof DOMAccess) {
            Node newNode = ((DOMAccess) element).getDOMBaseElement().cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            content.set(index, newNode);
            return result;
        }

        final String asString = invocationContext.getProjector().config().getStringRenderer().render(element.getClass(), element, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        oldNode.setTextContent(asString);
        return result;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            return false;
        }
        refreshForWriteIfNeeded();
        if (e instanceof Node) {
            content.add(DOMHelper.appendClone(parent, (Node) e));
            return true;
        }
        if (e instanceof DOMAccess) {
            content.add(DOMHelper.appendClone(parent, ((DOMAccess) e).getDOMBaseElement()));
            return true;
        }

        Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(e.getClass(), e, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        newElement.setTextContent(asString);
        parent.appendChild(newElement);
        content.add(newElement);
        return true;
    }

    @Override
    public void add(int index, E o) {
        if (o == null) {
            throw new IllegalArgumentException("Can not add null to a ProjectedList. I don't know how to render that.");
        }

        refreshForWriteIfNeeded();

        if ((index < 0) || (index > content.size())) {
            throw new IndexOutOfBoundsException();
        }

        if (index == content.size()) {
            add(o);
            return;
        }

        Node previousNode = content.get(index);

        if (o instanceof Node) {
            Node newValue = ((Node) o).cloneNode(true);
            previousNode.getParentNode().insertBefore(newValue, previousNode);
            content.add(index, newValue);
            return;
        }
        if (o instanceof DOMAccess) {
            Node newValue = ((DOMAccess) o).getDOMBaseElement().cloneNode(true);
            previousNode.getParentNode().insertBefore(newValue, previousNode);
            content.add(index, newValue);
            return;
        }

        Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        newElement.setTextContent(asString);
        parent.insertBefore(newElement, previousNode);
        content.add(index, newElement);
    }

    @Override
    public E remove(int index) {
        E result = get(index);
        Node remove = content.remove(index);
        Node p = remove.getParentNode();
        if (p != null) {
            p.removeChild(remove);
            DOMHelper.trim(p);
        }
        return result;

    };

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        refreshForReadIfNeeded(); // No creation of parent wanted

        if (content.isEmpty()) {
            return false;
        }

        if (o instanceof DOMAccess) {
            o = ((DOMAccess) o).getDOMBaseElement();
        }
        if (o instanceof Node) {
            boolean changed = content.remove(o);
            if (changed) {
                Node p = ((Node) o).getParentNode();
                if (p != null) {
                    p.removeChild((Node) o);
                }
                DOMHelper.trim(p);
            }

            return changed;
        }

        final String asString = invocationContext.getProjector().config().getStringRenderer().render(o.getClass(), o, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        if (asString == null) {
            return false;
        }

        for (Node item : content) {
            if (!asString.equals(item.getTextContent())) {
                continue;
            }
            DOMHelper.trim(item.getParentNode());
            item.getParentNode().removeChild(item);
            content.remove(item);//TODO: increase performance by using list iterator
            return true;
        }
        return false;
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

    private void refresh(boolean forWrite) {
        try {
            final NodeList nodes = (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);;
            if (nodes.getLength() == 0) {
                parent = invocationContext.getDuplexExpression().ensureParentExistence(baseNode);
            } else {
                parent = (Element) nodes.item(0).getParentNode();
            }
            content.clear();
            for (int i = 0; i < nodes.getLength(); ++i) {
                content.add(nodes.item(i));
            }
            needRefresh = false;
        } catch (XPathExpressionException e) {
            needRefresh = true;
            throw new XBException("Unexpected error during evaluation.", e);
        }
    }

    private void refreshForReadIfNeeded() {
        if (needRefresh) {
            refresh(false);
        }
    }

    private void refreshForWriteIfNeeded() {
        if (needRefresh) {
            refresh(true);
        }
    }

    @Override
    public void domChanged() {
        needRefresh = true;
    }

}
