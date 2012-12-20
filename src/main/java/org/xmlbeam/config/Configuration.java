package org.xmlbeam.config;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

public interface Configuration {

	TransformerFactory getTransformerFactory();

	DocumentBuilderFactory getDocumentBuilderFactory();

}
