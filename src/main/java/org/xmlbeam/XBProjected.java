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

import java.util.Iterator;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.Projected;
import org.xmlbeam.util.intern.DOMHelper;

/**
 *
 */
class XBProjected<E> implements Projected<E>, DOMChangeListener {

    private final InvocationContext invocationContext;
    private Node dataNode;
    private Node baseNode;
//    private Element parent;

    private final XBDomChangeTracker domChangeTracker = new XBDomChangeTracker() {
        @Override
        void refresh(boolean forWrite) throws XPathExpressionException {
            final NodeList nodes = (NodeList) invocationContext.getxPathExpression().evaluate(baseNode, XPathConstants.NODESET);;
            if (nodes.getLength() == 0 && forWrite) {
                //  parent = invocationContext.getDuplexExpression().ensureParentExistence(baseNode);
                dataNode = invocationContext.getDuplexExpression().ensureExistence(baseNode);
                return;
            } else {
                // parent = nodes.getLength() == 0 ? null : (Element) nodes.item(0).getParentNode();
            }
            dataNode = nodes.getLength() == 0 ? null : nodes.item(0);
        }
    };

    /**
     * @param baseNode
     * @param dataNode
     * @param invocationContext
     */
    public XBProjected(Node baseNode, Node dataNode, InvocationContext invocationContext) {
        this.baseNode = baseNode;
        this.dataNode = dataNode;
        this.invocationContext = invocationContext;

    }

    @Override
    public E get() {
        domChangeTracker.refreshForReadIfNeeded();
        return DefaultXPathEvaluator.convertToComponentType(invocationContext, dataNode, invocationContext.getTargetComponentType());
    }

    @Override
    public E set(E element) {
        if (dataNode == null) {
            domChangeTracker.domChanged();
        }
        domChangeTracker.refreshForWriteIfNeeded();
        E result = DefaultXPathEvaluator.convertToComponentType(invocationContext, dataNode, invocationContext.getTargetComponentType());
        Node oldNode = dataNode;
        if (element instanceof Node) {
            Node newNode = ((Node) element).cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            dataNode = newNode;
            return result;
        }
        if (element instanceof DOMAccess) {
            Node newNode = ((DOMAccess) element).getDOMBaseElement().cloneNode(true);
            oldNode.getParentNode().replaceChild(oldNode, newNode);
            dataNode = newNode;
            return result;
        }

        final String asString = invocationContext.getProjector().config().getStringRenderer().render(element.getClass(), element, invocationContext.getDuplexExpression().getExpressionFormatPattern());
        oldNode.setTextContent(asString);
        return result;
    }

    @Override
    public E remove() {
        //domChangeTracker.refreshForReadIfNeeded();
        E oldValue = get();
        if (dataNode == null) {
            return oldValue;
        }
        if (dataNode.getNodeType() == Node.ATTRIBUTE_NODE) {
            DOMHelper.removeAttribute((Attr) dataNode);
            dataNode = null;
            return oldValue;
        }

        if (dataNode.getParentNode() == null) {
            return oldValue;
        }
        DOMHelper.trim(dataNode);
        dataNode.getParentNode().removeChild(dataNode);
        dataNode = null;
        return oldValue;
    }

    @Override
    public boolean isPresent() {
        domChangeTracker.refreshForReadIfNeeded();
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void domChanged() {
        domChangeTracker.domChanged();
    }
}
