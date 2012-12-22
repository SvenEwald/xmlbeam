package org.xmlbeam.util;

import java.util.LinkedList;
import java.util.List;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

}
