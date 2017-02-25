/**
 *  Copyright 2012 Sven Ewald
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
package org.xmlbeam.util.intern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlbeam.XBProjector;

/**
 * A set of tiny helper methods internally used in the projection framework. This methods are
 * <b>not</b> part of the public framework API and might change in minor version updates.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class DOMHelper {

    /**
     * Null safe comparator for DOM nodes.
     */
    private static final Comparator<? super Node> ATTRIBUTE_NODE_COMPARATOR = new Comparator<Node>() {
        private int compareMaybeNull(final Comparable<Object> a, final Object b) {
            if (a == b) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return a.compareTo(b);
        }

        @Override
        public int compare(final Node o1, final Node o2) {
            Comparable<Object>[] c1 = getNodeAttributes(o1);
            Comparable<Object>[] c2 = getNodeAttributes(o2);
            assert c1.length == c2.length;
            for (int i = 0; i < c1.length; ++i) {
                int result = compareMaybeNull(c1[i], c2[i]);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    };

    /**
     * Parse namespace prefixes defined anywhere in the document.
     *
     * @param document
     *            source document.
     * @return map with prefix-&gt;uri relationships.
     */
    public static Map<String, String> getNamespaceMapping(final Document document) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("xmlns", "http://www.w3.org/2000/xmlns/");
        map.put("xml", "http://www.w3.org/XML/1998/namespace");
//      if (childName.equals("xmlns") || childName.startsWith("xmlns:")) {
//      return "http://www.w3.org/2000/xmlns/";
//  }
//  if (childName.startsWith("xml:")) {
//      return "http://www.w3.org/XML/1998/namespace";
//  }

        Element root = document.getDocumentElement();
        if (root == null) {
            // No document, no namespaces.
            return map;
        }

        fillNSMapWithPrefixesDeclaredInElement(map, root);
        return map;
    }

    /**
     * Search for prefix definitions in element and all children. There still is an issue for
     * documents that use the same prefix on differen namespaces in disjunct subtrees. This might be
     * possible but we won't support this. Same is with declaring multiple default namespaces.
     * XMLBeams behaviour will be undefined in that case. There is a workaround by defining a custom
     * namespace/prefix mapping, so the effort to support this is not justified.
     *
     * @param nsMap
     * @param element
     * @throws DOMException
     */
    private static void fillNSMapWithPrefixesDeclaredInElement(final Map<String, String> nsMap, final Element element) throws DOMException {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if ((!XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getPrefix())) && (!XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getLocalName()))) {
                continue;
            }
            if (XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getLocalName())) {
                nsMap.put("xbdefaultns", attribute.getNodeValue());
                continue;
            }
            nsMap.put(attribute.getLocalName(), attribute.getNodeValue());
        }
        NodeList childNodes = element.getChildNodes();
        for (Node n : nodeListToIterator(childNodes)) {
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            fillNSMapWithPrefixesDeclaredInElement(nsMap, (Element) n);
        }
    }

    /**
     * Replace the current root element. If element is null, the current root element will be
     * removed.
     *
     * @param document
     * @param element
     */
    public static void setDocumentElement(final Document document, final Element element) {
        Element documentElement = document.getDocumentElement();
        if (documentElement != null) {
            document.removeChild(documentElement);
        }
        if (element != null) {
            if (element.getOwnerDocument().equals(document)) {
                document.appendChild(element);
                return;
            }
            Node node = document.adoptNode(element);
            document.appendChild(node);
        }
    }

    /**
     * Implementation independent version of the Node.isEqualNode() method. Matches the same
     * algorithm as the nodeHashCode method. <br>
     * Two nodes are equal if and only if the following conditions are satisfied:
     * <ul>
     * <li>The two nodes are of the same type.</li>
     * <li>The following string attributes are equal: <code>nodeName</code>, <code>localName</code>,
     * <code>namespaceURI</code>, <code>prefix</code>, <code>nodeValue</code> . This is: they are
     * both <code>null</code>, or they have the same length and are character for character
     * identical.</li>
     * <li>The <code>attributes</code> <code>NamedNodeMaps</code> are equal. This is: they are both
     * <code>null</code>, or they have the same length and for each node that exists in one map
     * there is a node that exists in the other map and is equal, although not necessarily at the
     * same index.</li>
     * <li>The <code>childNodes</code> <code>NodeLists</code> are equal. This is: they are both
     * <code>null</code>, or they have the same length and contain equal nodes at the same index.
     * Note that normalization can affect equality; to avoid this, nodes should be normalized before
     * being compared.</li>
     * </ul>
     * <br>
     * For two <code>DocumentType</code> nodes to be equal, the following conditions must also be
     * satisfied:
     * <ul>
     * <li>The following string attributes are equal: <code>publicId</code>, <code>systemId</code>,
     * <code>internalSubset</code>.</li>
     * <li>The <code>entities</code> <code>NamedNodeMaps</code> are equal.</li>
     * <li>The <code>notations</code> <code>NamedNodeMaps</code> are equal.</li>
     * </ul>
     *
     * @param a
     * @param b
     * @return true if and only if the nodes are equal in the manner explained above
     */
    public static boolean nodesAreEqual(final Node a, final Node b) {
        if (a == b) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        if (!Arrays.equals(getNodeAttributes(a), getNodeAttributes(b))) {
            return false;
        }
        if (!namedNodeMapsAreEqual(a.getAttributes(), b.getAttributes())) {
            return false;
        }
        if (!nodeListsAreEqual(a.getChildNodes(), b.getChildNodes())) {
            return false;
        }
        return true;
    }

    /**
     * NodelLists are equal if and only if their size is equal and the containing nodes at the same
     * indexes are equal.
     *
     * @param a
     * @param b
     * @return
     */
    private static boolean nodeListsAreEqual(final NodeList a, final NodeList b) {
        if (a == b) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        if (a.getLength() != b.getLength()) {
            return false;
        }
        for (int i = 0; i < a.getLength(); ++i) {
            if (!nodesAreEqual(a.item(i), b.item(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * NamedNodeMaps (e.g. the attributes of a node) are equal if for each containing node an equal
     * node exists in the other map.
     *
     * @param a
     * @param b
     * @return
     */
    private static boolean namedNodeMapsAreEqual(final NamedNodeMap a, final NamedNodeMap b) {
        if (a == b) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        if (a.getLength() != b.getLength()) {
            return false;
        }

        List<Node> listA = new ArrayList<Node>(a.getLength());
        List<Node> listB = new ArrayList<Node>(a.getLength());

        for (int i = 0; i < a.getLength(); ++i) {
            listA.add(a.item(i));
            listB.add(b.item(i));
        }

        Collections.sort(listA, ATTRIBUTE_NODE_COMPARATOR);
        Collections.sort(listB, ATTRIBUTE_NODE_COMPARATOR);
        for (Node n1 : listA) {
            if (!nodesAreEqual(n1, listB.remove(0))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static Comparable<Object>[] getNodeAttributes(final Node node) {
        return new Comparable[] { Short.valueOf(node.getNodeType()), node.getNodeName(), node.getLocalName(), node.getNamespaceURI(), node.getPrefix(), node.getNodeValue() };
    }

    /**
     * hashCode() implementation that is compatible with equals().
     *
     * @param node
     * @return hash code for node
     */
    public static int nodeHashCode(final Node node) {
        assert node != null;
        int hash = 1 + node.getNodeType();
        hash = (hash * 17) + Arrays.hashCode(getNodeAttributes(node));
        if (node.hasAttributes()) {
            NamedNodeMap nodeMap = node.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); ++i) {
                hash = (31 * hash) + nodeHashCode(nodeMap.item(i));
            }
        }
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                hash = (hash * 47) + nodeHashCode(childNodes.item(i));
            }
        }
        return hash;
    }

    /**
     * @param element
     * @param attributeName
     * @param value
     */
    public static void setOrRemoveAttribute(final Element element, final String attributeName, final String value) {
        if (value == null) {
            element.removeAttribute(attributeName);
            return;
        }
        element.setAttribute(attributeName, value);
    }

    /**
     * @param node
     * @param newName
     * @return a new Element instance with desired name and content.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> T renameNode(final T node, final String newName) {
        if (node instanceof Attr) {
            Attr attributeNode = (Attr) node;
            final Element owner = attributeNode.getOwnerElement();
            if (owner == null) {
                throw new IllegalArgumentException("Attribute has no owner " + node);
            }
            owner.removeAttributeNode(attributeNode);
            owner.setAttribute(newName, attributeNode.getValue());
            return (T) owner.getAttributeNode(newName);
        }
        if (node instanceof Element) {
            Element element = (Element) node;
            Node parent = element.getParentNode();
            Document document = element.getOwnerDocument();
            // Element newElement = document.createElement(newName);
            final Element newElement = createElement(document, newName);
            NodeList nodeList = element.getChildNodes();
            List<Node> toBeMoved = new LinkedList<Node>();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                toBeMoved.add(nodeList.item(i));
            }
            for (Node e : toBeMoved) {
                element.removeChild(e);
                newElement.appendChild(e);
            }
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                newElement.setAttributeNode((Attr) attributes.item(i));
            }
            if (parent != null) {
                parent.replaceChild(newElement, element);
            }
            return (T) newElement;
        }
        throw new IllegalArgumentException("Can not rename node " + node);
    }

    /**
     * @param ownerDocument
     * @param node
     */
    public static void ensureOwnership(final Document ownerDocument, final Node node) {
        if (ownerDocument != node.getOwnerDocument()) {
            ownerDocument.adoptNode(node);
        }
    }

    /**
     * @param documentOrElement
     * @return document that owns the given node
     */
    public static Document getOwnerDocumentFor(final Node documentOrElement) {
        if (Node.DOCUMENT_NODE == documentOrElement.getNodeType()) {
            return (Document) documentOrElement;
        }
        return documentOrElement.getOwnerDocument();
    }

    private static Element createElement(final Document document, final String elementName) {
        final String prefix = getPrefixOfQName(elementName);// .replaceAll("(:.*)|([^:])*", "");
        final String namespaceURI = prefix.isEmpty() ? null : document.lookupNamespaceURI(prefix);

        final Element element;
        if (namespaceURI == null) {
            element = document.createElement(elementName);
        } else {
            element = document.createElementNS(namespaceURI, elementName);
        }

        return element;
    }

    /**
     * @param elementName
     * @return
     */
    private static String getPrefixOfQName(final String elementName) {
        if (elementName.contains(":")) {
            return elementName.replaceAll(":.*", "");
        }
        return "";
    }

    /**
     * @param domNode
     */
    public static void trim(final Node domNode) {
        assert domNode != null;
        assert (Node.TEXT_NODE != domNode.getNodeType());
        List<Text> removeMe = new LinkedList<Text>();
        NodeList childNodes = domNode.getChildNodes();
        for (Node child : nodeListToIterator(childNodes)) {
            if (Node.TEXT_NODE == child.getNodeType()) {
                if ((child.getNodeValue() == null) || child.getNodeValue().trim().isEmpty()) {
                    removeMe.add((Text) child);
                }
                continue;
            }
            trim(child);
        }
        for (Text node : removeMe) {
            Node parent = node.getParentNode();
            if (parent != null) {
                parent.removeChild(node);
            }
        }
    }

    /**
     * @param childNodes
     * @return
     */
    private static Iterable<Node> nodeListToIterator(final NodeList nodeList) {
        return new Iterable<Node>() {

            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {

                    private int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return nodeList.getLength() > pos;
                    }

                    @Override
                    public Node next() {
                        return nodeList.item(pos++);
                    }

                    @Override
                    public void remove() {
                        throw new IllegalStateException();
                    }

                };
            }

        };
    }

    /**
     * @param node
     * @return either a List with this node, or an empty list if node is null.
     */
    public static <T> List<T> asList(final T node) {
        if (node == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(node);
    }

    /**
     * @param nodes
     * @return true if and only if all nodes have the same parent
     */
//    public static boolean haveSameParent(final List<Node> nodes) {
//        if ((nodes == null) || (nodes.isEmpty())) {
//            return true;
//        }
//        Iterator<Node> i = nodes.iterator();
//        Node firstParent = i.next().getParentNode();
//        while (i.hasNext()) {
//            if (firstParent != i.next().getParentNode()) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * @param previous
     * @param newNode
     */
    public static void replaceElement(final Element previous, final Element newNode) {
        assert previous.getParentNode() != null;
        Element parent = (Element) previous.getParentNode();
        Document document = DOMHelper.getOwnerDocumentFor(parent);
        DOMHelper.ensureOwnership(document, newNode);
        parent.replaceChild(newNode, previous);
    }

    /**
     * @param data
     * @param name
     * @return list of children with tag name
     */
//    public static List<Node> getChildrendByName(final Node data, final String name) {
//        if (data.getNodeType() == Node.ELEMENT_NODE) {
//            return asList(((Element) data).getElementsByTagName(name));
//        }
//        if (data.getNodeType() == Node.DOCUMENT_NODE) {
//            return asList(((Document) data).getElementsByTagName(name));
//        }
//        throw new IllegalArgumentException("Only Elements and Documents have child nodes");
//    }

    /**
     * Set a text value to a node whether it is an element or an attribute.
     *
     * @param newNode
     * @param value
     */
    public static void setStringValue(final Node newNode, final String value) {
        assert newNode.getNodeType() != Node.DOCUMENT_NODE;
//        if (newNode.getNodeType() == Node.ATTRIBUTE_NODE) {
//            if ("xmlns".equals(newNode.getNodeName())) {
//                ((Attr) newNode).getOwnerElement().setAttribute("xmlns", value);
//                return;
//            }
//        }
//            ((Attr) newNode).getOwnerElement().
//             ((Attr) newNode).getOwnerElement().setAttributeNS(newNode.getNamespaceURI(), newNode.getNodeName(), value);
//            return;
//        }
        newNode.setTextContent(value);
    }

    /**
     * Simply removes all child nodes.
     *
     * @param element
     */
    public static void removeAllChildren(final Element element) {
        for (Node n = element.getFirstChild(); n != null; n = element.getFirstChild()) {
            element.removeChild(n);
        }
    }

    /**
     * @param attributeNode
     */
    public static void removeAttribute(final Attr attributeNode) {
        if (attributeNode == null) {
            return;
        }
        final Element owner = attributeNode.getOwnerElement();
        if (owner == null) {
            return;
        }
        owner.removeAttributeNode(attributeNode);
    }

    /**
     * @param node
     */
    private static void removeNode(final Node node) {
        if (node == null) {
            return;
        }

        while (node.hasChildNodes()) {
            removeNode(node.getFirstChild());
        }

        final Node parent = node.getParentNode();
        if (parent == null) {
            return;
        }
        parent.removeChild(node);
    }

    /**
     * @param parentElement
     * @param o
     * @return the new clone
     */
    public static Node appendClone(final Element parentElement, final Node o) {
        Node clone = o.cloneNode(true);
        ensureOwnership(DOMHelper.getOwnerDocumentFor(parentElement), clone);
        parentElement.appendChild(clone);
        return clone;
    }

    /**
     * @param existingNodes
     */
    public static void removeNodes(final Iterable<? extends Node> existingNodes) {
        for (Node e : existingNodes) {
            removeNode(e);
        }

    }

    /**
     * @param projector
     * @param domNode
     * @return rendered XML as String
     */
    public static String toXMLString(final XBProjector projector, final Node domNode) {
        try {
            final StringWriter writer = new StringWriter();
            projector.config().createTransformer().transform(new DOMSource(domNode), new StreamResult(writer));
            final String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param item
     * @return Text content of this node, without child content.
     */
    public static String directTextContent(final Node item) {
        NodeList childNodes = item.getChildNodes();
        if (childNodes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.TEXT_NODE) {
                continue;
            }
            sb.append(child.getNodeValue());
        }
        return sb.toString();
    }
}
