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
package org.xmlbeam.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class DOMHelper {

    public static void removeAllChildrenByName(Node element, String nodeName) {
        NodeList nodeList = element.getChildNodes();
        List<Element> toBeRemoved = new LinkedList<Element>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeName.equals(nodeList.item(i).getNodeName())) {
                toBeRemoved.add((Element) nodeList.item(i));
            }
        }
        for (Element e : toBeRemoved) {
            element.removeChild(e);
        }
    }

    public static Document getDocumentFromURL(DocumentBuilder documentBuilder, final String url, Map<String, String> requestParams, final Class<?> resourceAwareClass) throws IOException {
        try {
            if (url.startsWith("resource://")) {
                return documentBuilder.parse(resourceAwareClass.getResourceAsStream(url.substring("resource://".length())));
            }
            if (url.startsWith("http:")||url.startsWith("https:")) {
                return documentBuilder.parse(IOHelper.httpGet(url, requestParams), url);
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
        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (!XMLConstants.XMLNS_ATTRIBUTE.equals(attribute.getPrefix())) {
                continue;
            }
            map.put(attribute.getLocalName(), attribute.getNodeValue());
        }
        return map;
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

        for (String expectedElementName : splitme.split("/")) {
            if (expectedElementName.equals(element.getNodeName())) {
                continue;
            }
            NodeList nodeList = element.getElementsByTagName(expectedElementName);
            if (nodeList.getLength() == 0) {
                element = (Element) element.appendChild(document.createElement(expectedElementName));
                continue;
            }
            element = (Element) nodeList.item(0);
        }
        return element;
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
}
