/**
 *  Copyright 2014 Sven Ewald
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

import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.util.intern.DOMHelper;

class DefaultDOMAccessInvoker implements DOMAccess, Serializable {

    final static class DefaultObjectInvoker extends DefaultDOMAccessInvoker {
        private DefaultObjectInvoker(final Class<?> projectionInterface, final Node documentOrElement, final XBProjector projector) {
            super(documentOrElement, projectionInterface, projector);
        }

        @Override
        public String toString() {
            final String typeDesc = getDOMNode().getNodeType() == Node.DOCUMENT_NODE ? "document '" + getDOMNode().getBaseURI() + "'" : "element " + "'" + getDOMNode().getNodeName() + "[" + Integer.toString(getDOMNode().hashCode(), 16) + "]'";
            return "Projection [" + getProjectionInterface().getName() + "]" + " to " + typeDesc;
        }
    }

    final static class XMLRenderingObjectInvoker extends DefaultDOMAccessInvoker {
        private XMLRenderingObjectInvoker(final Class<?> projectionInterface, final Node documentOrElement, final XBProjector projector) {
            super(documentOrElement, projectionInterface, projector);
        }

        @Override
        public String toString() {
            return super.asString();
        }
    }

    private final Node documentOrElement;
    private final Class<?> projectionInterface;
    private final XBProjector projector;

    /**
     * @param documentOrElement
     * @param projectionInterface
     */
    private DefaultDOMAccessInvoker(final Node documentOrElement, final Class<?> projectionInterface, final XBProjector projector) {
        this.documentOrElement = documentOrElement;
        this.projectionInterface = projectionInterface;
        this.projector = projector;
    }

    @Override
    public Class<?> getProjectionInterface() {
        return projectionInterface;
    }

    @Override
    public Node getDOMNode() {
        return documentOrElement;
    }

    @Override
    public Document getDOMOwnerDocument() {
        return DOMHelper.getOwnerDocumentFor(documentOrElement);
    }

    @Override
    public Element getDOMBaseElement() {
        if (Node.DOCUMENT_NODE == documentOrElement.getNodeType()) {
            return ((Document) documentOrElement).getDocumentElement();
        }
        assert Node.ELEMENT_NODE == documentOrElement.getNodeType();
        return (Element) documentOrElement;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DOMAccess)) {
            return false;
        }
        DOMAccess op = (DOMAccess) o;
        if (!projectionInterface.equals(op.getProjectionInterface())) {
            return false;
        }
        // Unfortunately Node.isEqualNode() is implementation specific and does
        // not need to match our hashCode implementation.
        // So we define our own node equality.
        return DOMHelper.nodesAreEqual(documentOrElement, op.getDOMNode());
    }

    @Override
    public int hashCode() {
        return (31 * projectionInterface.hashCode()) + (27 * DOMHelper.nodeHashCode(documentOrElement));
    }

    @Override
    public String asString() {
        try {
            final StringWriter writer = new StringWriter();
            projector.config().createTransformer().transform(new DOMSource(getDOMNode()), new StreamResult(writer));
            final String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param projectionInterface
     * @param node
     * @param projector
     * @param toStringRendersXML
     * @return a new instance
     */
    public static DefaultDOMAccessInvoker create(final Class<?> projectionInterface, final Node node, final XBProjector projector, final boolean toStringRendersXML) {
        return toStringRendersXML ? new XMLRenderingObjectInvoker(projectionInterface, node, projector) : new DefaultObjectInvoker(projectionInterface, node, projector);
    }

    @Override
    public DOMAccess create(final String path, final Object value) {
        throw new IllegalStateException("This method should not be called.");
    }

    @Override
    public boolean isModified() {
        return (Boolean) getDOMOwnerDocument().getUserData("org.xmlbeam.domaccess.modification.flag");
    }

    @Override
    public void setModified(final boolean b) {
        getDOMOwnerDocument().setUserData("org.xmlbeam.domaccess.modification.flag", Boolean.valueOf(b), null);
    }

}