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

import java.util.Map;

import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * A XMLFactoriesConfig defines factories for the underlying Java XML implementations. By
 * implementing your own XMLFactoriesConfig you may inject other XPath parsers, DocumentBuilders or
 * Transformers in an {@link org.xmlbeam.XBProjector}. See
 * {@link org.xmlbeam.config.DefaultXMLFactoriesConfig} for further information.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public interface XMLFactoriesConfig extends Serializable {

    /**
     * Factory method to provide a {@link javax.xml.transform.TransformerFactory}.
     *
     * @return a new instance.
     */
    TransformerFactory createTransformerFactory();

    /**
     * Factory method to provide a {@link javax.xml.parsers.DocumentBuilderFactory}.
     *
     * @return a new instance.
     */
    DocumentBuilderFactory createDocumentBuilderFactory();

    /**
     * Factory method to provide a {@link javax.xml.xpath.XPathFactory}.
     *
     * @return a new instance.
     */
    XPathFactory createXPathFactory();

    /**
     * Factory method to provide a {@link javax.xml.transform.Transformer}. Creation and
     * configuration may depend on the content of a document.
     *
     * @param document
     *            (optional)
     * @return a new instance.
     */
    Transformer createTransformer(Document... document);

    /**
     * Factory method to provide a {@link javax.xml.parsers.DocumentBuilder}.
     *
     * @return a new instance.
     */
    DocumentBuilder createDocumentBuilder();

    /**
     * Factory method to provide a {@link XPath}. Creation and configuration may
     * depend on the content of a document. This may happen when you want to use the namespace
     * mapping of the document in your xpath expresssions.
     *
     * @param document
     *            (optional)
     * @return a new instance.
     */
    XPath createXPath(Document... document);

    /**
     * Get a prefix to namespace mapping that can be used to access or create documents with
     * namespaces.
     * 
     * @return A map prefix to uri.
     */
    Map<String, String> getUserDefinedNamespaceMapping();

}
