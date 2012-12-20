package org.xmlbeam.util;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

}
