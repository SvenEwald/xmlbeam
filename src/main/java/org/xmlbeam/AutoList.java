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
import java.util.ListIterator;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.util.intern.DOMHelper;

/**
 *
 */
class AutoList<E> extends AbstractList<E> implements XBAutoList<E>, DOMChangeListener {

    static class EmptyAutoList<F> extends AbstractList<F> implements XBAutoList<F> {

        @Override
        public F get(final int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 0;
        }

    }

    @SuppressWarnings("rawtypes")
    private static final XBAutoList EMPTY = new EmptyAutoList<Object>();
    private final InvocationContext invocationContext;
    private Element parent;
    private final Node baseNode;
    private final List<Node> content = new ArrayList<Node>();
    private final DomChangeTracker domChangeTracker = new DomChangeTracker() {
        @Override
        void refresh(final boolean forWrite) throws XPathExpressionException {
            final NodeList nodes = (NodeList) invocationContext.getxPathExpression().evaluate(baseNode, XPathConstants.NODESET);
            if ((nodes.getLength() == 0) && forWrite) {
                parent = invocationContext.getDuplexExpression().ensureParentExistence(baseNode);
            } else {
                parent = nodes.getLength() == 0 ? null : (Element) nodes.item(0).getParentNode();
            }
            content.clear();
            for (int i = 0; i < nodes.getLength(); ++i) {
                content.add(nodes.item(i));
            }
        }
    };

    /**
     * @param baseNode
     * @param invocationContext
     */
    public AutoList(final Node baseNode, final InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
        this.baseNode = baseNode;
        this.invocationContext.getProjector().addDOMChangeListener(this);
    }

    @Override
    public E get(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        domChangeTracker.refreshForReadIfNeeded();
        if (index >= content.size()) {
            throw new IndexOutOfBoundsException();
        }
        return DefaultXPathEvaluator.convertToComponentType(invocationContext, content.get(index), invocationContext.getTargetComponentType());
    }

    @Override
    public int size() {
        domChangeTracker.refreshForReadIfNeeded();
        return content.size();
    }

    @Override
    public E set(final int index, final E element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        domChangeTracker.refreshForReadIfNeeded();
        if (index >= content.size()) {
            throw new IndexOutOfBoundsException();
        }
        Node oldNode = content.get(index);
        E result = DefaultXPathEvaluator.convertToComponentType(invocationContext, oldNode, invocationContext.getTargetComponentType());
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
    public boolean add(final E e) {
        if (e == null) {
            return false;
        }
        if (parent == null) {
            domChangeTracker.domChanged();
        }
        domChangeTracker.refreshForWriteIfNeeded();

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
    public void add(final int index, final E o) {
        if (o == null) {
            throw new IllegalArgumentException("Can not add null to a ProjectedList. I don't know how to render that.");
        }

        domChangeTracker.refreshForWriteIfNeeded();

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
    public E remove(final int index) {
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

        domChangeTracker.refreshForReadIfNeeded(); // No creation of parent wanted

        if (content.isEmpty()) {
            return false;
        }

        if (o instanceof DOMAccess) {
            o = ((DOMAccess) o).getDOMBaseElement();
        }
        if (o instanceof Node) {
            for (Node contentNode : content) {
                if (DOMHelper.nodesAreEqual(contentNode, ((Node) o))) {
                    o = contentNode;
                    break;
                }
            }

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

        for (ListIterator<Node> i = content.listIterator(); i.hasNext();) {
            Node item = i.next();
            if (!asString.equals(item.getTextContent())) {
                continue;
            }
            final Node parentNode = item.getParentNode();
            assert parentNode != null : "How can child be in list without parent?";
            parentNode.removeChild(item);
            DOMHelper.trim(parentNode);
            i.remove();
            return true;
        }
        return false;
    }

    @Override
    public int indexOf(final Object o) {
        if (!(o instanceof Node)) {
            return super.indexOf(o);
        }
        domChangeTracker.refreshForReadIfNeeded();
        Node oNode = (Node) o;
        ListIterator<Node> e = content.listIterator();
        while (e.hasNext()) {
            if (DOMHelper.nodesAreEqual(oNode, e.next())) {
                return e.previousIndex();
            }
        }
        return -1;
    }

    @Override
    public void domChanged() {
        domChangeTracker.domChanged();
    }

    /**
     * @return parent node holding the data nodes
     */
    public Node getNode() {
        domChangeTracker.refreshForReadIfNeeded();
        return this.parent;
    }

    /**
     * @return empty instance
     */
    @SuppressWarnings("unchecked")
    public static <E> XBAutoList<E> emptyList() {
        return EMPTY;
    }

}
