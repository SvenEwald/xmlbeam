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

public class DOMUtils {

    public static void removeAllChildrenByName(Element element, String nodeName) {
        NodeList nodeList = element.getElementsByTagName(nodeName);
        List<Element> toBeRemoved = new LinkedList<Element>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            toBeRemoved.add((Element) nodeList.item(i));
        }
        for (Element e : toBeRemoved) {
            element.removeChild(e);
        }
    }

    public static Document getXMLNodeFromURI(DocumentBuilder documentBuilder, final String uri, final Class<?> resourceAwareClass) throws IOException {
        try {
            if (uri.startsWith("resource://")) {
                return documentBuilder.parse(resourceAwareClass.getResourceAsStream(uri.substring("resource://".length())));
            }
            Document document = documentBuilder.parse(uri);
            if (document == null) {
                throw new IOException("Document could not be created form uri " + uri);
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

}
