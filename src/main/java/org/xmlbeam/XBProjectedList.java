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

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.types.ProjectedList;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.DOMHelper;

/**
 *
 */
public class XBProjectedList<E> extends AbstractList<E> implements ProjectedList<E> {

    private InvocationContext invocationContext;
    private Element parent;
    private XPathExpression expression;
    private Node baseNode;

    public XBProjectedList(Node node, XPathExpression expression, InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
        this.expression = expression;
        this.baseNode = node;
        final NodeList nodes = getNodes();
        if (nodes.getLength() == 0) {
            parent = invocationContext.getDuplexExpression().ensureParentExistence(node);
        } else {
            parent = (Element) nodes.item(0).getParentNode();
        }
    }

    @Override
    public E get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        NodeList nodes = getNodes();
        if (index >= nodes.getLength()) {
            throw new IndexOutOfBoundsException();
        }
        return convertToComponentType(nodes.item(index), invocationContext.getTargetComponentType());
    }

    @Override
    public int size() {
        return getNodes().getLength();
    }

    @Override
    public E set(int index, E element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        NodeList nodes = getNodes();
        if (index >= nodes.getLength()) {
            throw new IndexOutOfBoundsException();
        }
        E result = convertToComponentType(nodes.item(index), invocationContext.getTargetComponentType());
        Node oldNode=nodes.item(index);
        if (element instanceof Node) {
          
            Node newNode=((Node)element).cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            return result;
        }
        if (element instanceof DOMAccess) {
            Node newNode=((DOMAccess)element).getDOMBaseElement().cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            return result;
        }
        
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(element.getClass(), element, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        oldNode.setTextContent(asString);
        
        return result;
    }

    @Override
    public boolean add(E e) {
        if (e==null) {
            return false;
        }
        if (e instanceof Node) {
            DOMHelper.appendClone(parent, (Node) e);
            return true;
        }
        if (e instanceof DOMAccess) {
            DOMHelper.appendClone(parent, ((DOMAccess)e).getDOMBaseElement());
            return true;
        }
        
        Node newElement = invocationContext.getDuplexExpression().createChildWithPredicate(parent);
        final String asString = invocationContext.getProjector().config().getStringRenderer().render(e.getClass(), e, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        newElement.setTextContent(asString);
        parent.appendChild(newElement);
        return true;
    }

    @Override
    public void add(int index, E o) {
        if (o==null) {
            throw new IllegalArgumentException("Can not add null to a ProjectedList. I don't know how to render that.");
        }
        NodeList nodes = getNodes();
        if (o instanceof Node) {
            Node newValue=((Node)o).cloneNode(true);
            
            DOMHelper.appendClone(parent, (Node) e);
            return ;
        }
        if (o instanceof DOMAccess) {
            DOMHelper.appendClone(parent, ((DOMAccess)e).getDOMBaseElement());
            return ;
        }
        
        
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

    /**
     * @return a fresh view to the current list content
     */
    private NodeList getNodes() {
        try {
            return (NodeList) expression.evaluate(baseNode, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new XBException("Unexpected error during evaluation.", e);
        }
    }
}
