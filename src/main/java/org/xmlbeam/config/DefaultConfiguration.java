package org.xmlbeam.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

public class DefaultConfiguration implements Configuration {

	public DefaultConfiguration() {

	}

	public Transformer createTransformer() {
		try {
			return getTransformerFactory().newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public DocumentBuilder createDocumentBuilder() {
		try {
			return getDocumentBuilderFactory().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public TransformerFactory getTransformerFactory() {
		return TransformerFactory.newInstance();
	}

	@Override
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return DocumentBuilderFactory.newInstance();
	}
}
