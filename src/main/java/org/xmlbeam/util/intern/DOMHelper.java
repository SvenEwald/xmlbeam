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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlbeam.util.IOHelper;

/**
 * A set of tiny helper methods internally used in the projection framework. This methods are
 * <b>not</b> part of the public framework API and might change in minor version updates.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class DOMHelper {

    private static final Comparator<? super Node> ATTRIBUTE_NODE_COMPARATOR = new Comparator<Node>() {
        private int compareMaybeNull(Comparable<Object> a, Object b) {
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
        public int compare(Node o1, Node o2) {
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
     * Remove all child elements with given node name. If nodeSelector is "*", then remove all children.
     * 
     * @param element
     * @param nodeName
     */
    public static void removeAllChildrenBySelector(Node element, String nodeSelector) {
        assert nodeSelector != null;
        NodeList nodeList = element.getChildNodes();
        List<Node> toBeRemoved = new LinkedList<Node>();
        if ("*".equals(nodeSelector)) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                toBeRemoved.add((Node) nodeList.item(i));
            }
        } else {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node=nodeList.item(i);
                if (Node.ELEMENT_NODE!=node.getNodeType()) {
                    continue;
                }
                if (selectorMatches(nodeSelector, (Element)node)) {
                    toBeRemoved.add((Element) nodeList.item(i));
                }
            }
        }
        for (Node e : toBeRemoved) {
            element.removeChild(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Document getDocumentFromURL(DocumentBuilder documentBuilder, final String url, Map<String, String> requestProperties, final Class<?> resourceAwareClass) throws IOException {
        try {
            if (url.startsWith("resource://")) {
                InputStream is = resourceAwareClass.getResourceAsStream(url.substring("resource://".length()));
                InputSource source = new InputSource(is);
                // source.setEncoding("MacRoman");
                return documentBuilder.parse(source);
            }
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return documentBuilder.parse(IOHelper.httpGet(url, requestProperties), url);
            }
            Document document = documentBuilder.parse(url);
            if (document == null) {
                throw new IOException("Document could not be created form uri " + url);
            }
            return document;
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse namespace prefixes defined in the documents root element.
     * 
     * @param document
     *            source document.
     * @return map with prefix->uri relationships.
     */
    public static Map<String, String> getNamespaceMapping(Document document) {
        Map<String, String> map = new HashMap<String, String>();
        Element root = document.getDocumentElement();
        if (root == null) {
            // No document, no namespaces.
            return Collections.emptyMap();
        }
        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if ((!XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getPrefix()))&&(!XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getLocalName()))) {
                continue;
            }
            if (XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getLocalName())) {
                map.put("xbdefaultns", attribute.getNodeValue());
                continue;
            }
            map.put(attribute.getLocalName(), attribute.getNodeValue());
        }
        return map;
    }

    /**
     * Treat the given path as relative path from a base element to an element and return this
     * element. If any element on this path does not exist, create it.
     * 
     * @param base
     *            the relative path will start here
     * @param pathToElement
     *            relative path to element.
     * @return element with absolute path.
     */
    public static Element ensureElementExists(final Document document, final Element base, final String pathToElement) {
        assert base != null;
        assert pathToElement != null;               
        if ((pathToElement.isEmpty())||(".".equals(pathToElement))) {
            return base;
        }
        Element element = base;
        String splitme = pathToElement.replaceAll("(^/)|(/$)", "");
        if (splitme.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty. I don't know which element to return.");
        }
        for (String expectedElementName : splitme.split("/")) {
            if (".".equals(expectedElementName)) {
                continue;
            }
            if ("..".equals(expectedElementName)) {
                Node parent=element.getParentNode();
                if ((parent==null)||(Node.ELEMENT_NODE!=parent.getNodeType())) {
                    throw new IllegalArgumentException("Reference to parent node via '..' bounced out of scope.");
                }
                element=(Element) parent;
                continue;
            }
            if (expectedElementName.equals(element.getNodeName())) {
                continue;
            }
            
            String name=expectedElementName.replaceAll("\\[.*", "");
            String selector=expectedElementName.contains("[")? expectedElementName.replaceAll(".*\\[", "").replaceAll("\\]$", ""):"";
            
            Element child = findElementByTagNameAndSelector(element,name,selector);
            if (child==null) {
                //element = (Element) element.appendChild(document.createElement(expectedElementName));
                element = (Element) element.appendChild(createElementByTagNameAndSelector(document,name,selector));
                continue;
            }
            element = child;
        }
        return element;
    }

    /**
     * @param document
     * @param expectedElementName
     * @param selector
     * @return
     */
    private static Node createElementByTagNameAndSelector(Document document, String name, String selector) {
        Element element = document.createElement(name);
        if (selector.isEmpty()) {
            return element;
        }
        String[] selectorValues = splitSelector(selector);
        if (selectorValues[0].startsWith("@")) {
            element.setAttribute(selectorValues[0].substring(1), selectorValues[1]);
            return element;
        }
        Element child = document.createElement(selectorValues[0]);
        child.setTextContent(selectorValues[1]);
        element.appendChild(child);
        return element;
    }

    /**
     * @param element
     * @param expectedElementName
     * @return
     */
    private static Element findElementByTagNameAndSelector(Element element, String name,String selector) {               
        NodeList nodeList = element.getElementsByTagName(name);
        for (int i=0;i<nodeList.getLength();++i) {
            if (selectorMatches(selector,(Element)nodeList.item(i)) ) {
                return (Element) nodeList.item(i);
            }
        }
        return null;
    }

    /**
     * @param selector
     * @param item
     * @return
     */
    private static boolean selectorMatches(String selector, Element item) {
        if (item==null) {
            return false;
        }
        if ((selector==null)||(selector.isEmpty())) {
            return true;
        }
        if (!selector.contains("[")) {
            return selector.equals(item.getNodeName());
        }
        String[] selectorValues = splitSelector(selector);
        if (selectorValues[0].startsWith("@")) {
            return selectorValues[1].equals(item.getAttribute(selectorValues[0].substring(1)));
        }        
         NodeList nodeList = item.getElementsByTagName(selectorValues[0]);
         for (int i=0;i<nodeList.getLength();++i) {
             if (selectorValues[1].equals(nodeList.item(i).getTextContent())) {
                 return true;
             }
         }
         return false;
    }

    /**
     * @param selector
     * @return
     */
    private static String[] splitSelector(String selector) {
        if (!selector.matches("@?[^=]+=[^=]+")) {
            throw new IllegalArgumentException("When using a predicate to create elements, predicate expressions must assign values via '=' to an attribute or direct child element.");
        }
        selector=selector.replaceAll("(^\\[)|(\\]$)", "");
        String[] split = selector.split("=");
        split[1]=split[1].replaceAll("(^')|('$)", "");
        return split;
    }

    /**
     * Treat the given path as absolute path to an element and return this element. If any element
     * on this path does not exist, create it.
     * 
     * @param document
     *            document
     * @param pathToElement
     *            absolute path to element.
     * @return element with absolute path.
     */
    public static Element ensureElementExists(final Document document, final String pathToElement) {
        assert document != null;
        String splitme = pathToElement.replaceAll("(^/)|(/$)", "");
        if (splitme.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty. I don't know which element to return.");
        }
        Element element = document.getDocumentElement();
        if (element == null) { // No root element yet
            element = document.createElement(splitme.replaceAll("/.*", ""));
            document.appendChild(element);
        }

        return ensureElementExists(document, element, pathToElement);
    }

    /**
     * Replace the current root element. If element is null, the current root element will be
     * removed.
     * 
     * @param document
     * @param element
     */
    public static void setDocumentElement(Document document, Element element) {
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
     * @param node
     * @param xmlNode
     * @return
     */
    public static boolean nodesAreEqual(Node a, Node b) {
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
    private static boolean nodeListsAreEqual(NodeList a, NodeList b) {
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
    private static boolean namedNodeMapsAreEqual(NamedNodeMap a, NamedNodeMap b) {
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
    private static Comparable<Object>[] getNodeAttributes(Node node) {
        return new Comparable[] { Short.valueOf(node.getNodeType()), node.getNodeName(), node.getLocalName(), node.getNamespaceURI(), node.getPrefix(), node.getNodeValue() };
    }

    /**
     * @param node
     * @return
     */
    public static int nodeHashCode(Node node) {
        assert node != null;
        int hash = 1 + node.getNodeType();
        hash = hash * 17 + Arrays.hashCode(getNodeAttributes(node));
        if (node.hasAttributes()) {
            NamedNodeMap nodeMap = node.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); ++i) {
                hash = 31 * hash + nodeHashCode(nodeMap.item(i));
            }
        }
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                hash = hash * 47 + nodeHashCode(childNodes.item(i));
            }
        }
        return hash;
    }

    public static void trimTextNodes(Node e) {
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (Node.TEXT_NODE != child.getNodeType()) {
                trimTextNodes(child);
                continue;
            }
            String content = child.getNodeValue();
            if (content != null) {
                content = content.trim();
                if (!content.isEmpty()) {
                    child.setNodeValue(content);
                    continue;
                }
            }
            e.removeChild(child);
        }
    }

    /**
     * @param element
     * @param attributeName
     * @param value
     */
    public static void setOrRemoveAttribute(Element element, String attributeName, String value) {
        if (value == null) {
            element.removeAttribute(attributeName);
            return;
        }
        element.setAttribute(attributeName, value);
    }

    /**
     * @param element
     * @param newName
     * @return
     */
    public static Element renameElement(Element element, String newName) {
        Document document = element.getOwnerDocument();
        Element newElement = document.createElement(newName);
        NodeList nodeList = element.getChildNodes();
        List<Node> toBeMoved = new LinkedList<Node>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            toBeMoved.add(nodeList.item(i));
        }
        for (Node e : toBeMoved) {
            element.removeChild(e);
            newElement.appendChild(e);
        }
        return newElement;
    }

    /**
     * @param ownerDocument
     * @param element
     */
    public static void ensureOwnership(Document ownerDocument, Element element) {
        if (ownerDocument != element.getOwnerDocument()) {
            ownerDocument.adoptNode(element);
        }
    }

    /**
     * @param documentOrElement
     * @return
     */
    public static Document getOwnerDocumentFor(Node documentOrElement) {
            if (Node.DOCUMENT_NODE == documentOrElement.getNodeType()) {
                return (Document) documentOrElement;
            }
            return documentOrElement.getOwnerDocument();       
    }
}
