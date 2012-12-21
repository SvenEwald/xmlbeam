package org.xmlbeam.config;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

public class DefaultConfiguration implements Configuration {

	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private final Transformer transformer = createTransformer();
	private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

	public DefaultConfiguration() {

	}

	private Transformer createTransformer() {
		try {
			return transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public Transformer getTransformer() {
		return transformer;
	}

	@Override
	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	@Override
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}
}
