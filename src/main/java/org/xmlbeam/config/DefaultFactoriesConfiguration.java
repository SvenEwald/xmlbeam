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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;

import org.xmlbeam.XMLProjector;

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
	 * Empty default constructor, a Configuration has no state.
	 */
	public DefaultFactoriesConfiguration() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transformer createTransformer() {
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
		return DocumentBuilderFactory.newInstance();
	}
}
