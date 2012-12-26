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
package org.xmlbeam.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.util.DOMUtils;

/**
 * Default configuration for {@link XMLProjector} which uses Java default
 * factories to create {@link Transformer} {@link DocumentBuilder} and
 * {@link XPath}.
 * 
 * You may want to inherit from this class to change this behavior.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
public class DefaultFactoriesConfiguration implements FactoriesConfiguration {

	/**
	 * This configuration can use one of three different ways to configure
	 * namespace handling. Namespaces may be ignored ({@link NIHILISTIC}),
	 * handled user defined prefix mappings {@link AGNOSTIC} or mapped
	 * automatically to the document prefixes {@link HEDONISTIC}.
	 * 
	 */
	public enum NamespacePhilosophy {

		/**
		 * There is no such thing as a namespace. Only elements and attributes
		 * without a namespace will be visible to projections. Using this option
		 * prevents getting exceptions when an XPath expression tries to select
		 * a non defined namespace. (You can't get errors if you deny the
		 * existence of errors.)
		 */
		NIHILISTIC,

		/**
		 * Maybe there are namespaces. Maybe not. You have to decide for
		 * yourself. Neither the xml parser, nor the XPath instances bill be
		 * modified by this confiruration. Using this option will require you to
		 * subclass this configuration and specify namespace handling yourself.
		 * This way allowes you to control prefix to namespace mapping for the
		 * XPath expressions.
		 */
		AGNOSTIC,

		/**
		 * Fun without pain. This is the default option in this configuration.
		 * If namespaces are defined in the document, the definition will be
		 * applied to your XPath expressions. Thus you may just use existing
		 * namespaces without bothering about prefix mapping.
		 */
		HEDONISTIC
	};

	private final NamespacePhilosophy namespacePhilosophy = NamespacePhilosophy.HEDONISTIC;

	/**
	 * Empty default constructor, a Configuration has no state.
	 */
	public DefaultFactoriesConfiguration() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transformer createTransformer(Document... document) {
		try {
			return createTransformerFactory().newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentBuilder createDocumentBuilder() {
		try {
			return createDocumentBuilderFactory().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransformerFactory createTransformerFactory() {
		return TransformerFactory.newInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentBuilderFactory createDocumentBuilderFactory() {
		DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
		if (!NamespacePhilosophy.AGNOSTIC.equals(namespacePhilosophy)) {
			instance.setNamespaceAware(NamespacePhilosophy.HEDONISTIC.equals(namespacePhilosophy));
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XPathFactory createXPathFactory() {
		return XPathFactory.newInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XPath createXPath(Document... document) {

		if ((document == null) || (!NamespacePhilosophy.HEDONISTIC.equals(namespacePhilosophy))) {
			return createXPathFactory().newXPath();
		}

		final Map<String, String> nameSpaceMapping = DOMUtils.getNamespaceMapping(document[0]);
		NamespaceContext ctx = new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix == null) {
					throw new IllegalArgumentException("null not allowed as prefix");
				}
				return nameSpaceMapping.get(prefix);
			}

			@Override
			public Iterator<String> getPrefixes(String val) {
				return nameSpaceMapping.keySet().iterator();
			}

			@Override
			public String getPrefix(String uri) {
				for (Entry<String, String> e:nameSpaceMapping.entrySet()) {
					if (e.getValue().equals(uri )) {
						return e.getKey();
					}
				}
				return null;
			}
		};
		XPath xPath = createXPathFactory().newXPath();
		xPath.setNamespaceContext(ctx);
		return xPath;
	}

	/**
	 * @return the namespacePhilosophy
	 */
	public NamespacePhilosophy getNamespacePhilosophy() {
		return namespacePhilosophy;
	}
}
