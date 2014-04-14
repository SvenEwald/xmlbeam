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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xmlbeam.XBProjector;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * Default configuration for {@link XBProjector} which uses Java default factories to create
 * {@link Transformer} {@link DocumentBuilder} and {@link XPath}. You may want to inherit from this
 * class to change this behavior.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings("serial")
public class DefaultXMLFactoriesConfig implements XMLFactoriesConfig {

    /**
     * This configuration can use one of three different ways to configure namespace handling.
     * Namespaces may be ignored (NIHILISTIC), handled user defined prefix mappings
     * (AGNOSTIC) or mapped automatically to the document prefixes (HEDONISTIC).
     */
    public static enum NamespacePhilosophy {

        /**
         * There is no such thing as a namespace. Only elements and attributes without a namespace
         * will be visible to projections. Using this option prevents getting exceptions when an
         * XPath expression tries to select a non defined namespace. (You can't get errors if you
         * deny the existence of errors.) DocumentBuilders are created with namespace awareness set
         * to false.
         */
        NIHILISTIC,

        /**
         * Maybe there are namespaces. Maybe not. You have to decide for yourself. Neither the xml
         * parser, nor the XPath instances bill be modified by this confiruration. Using this option
         * will require you to subclass this configuration and specify namespace handling yourself.
         * This way allowes you to control prefix to namespace mapping for the XPath expressions.
         * The namespace awareness flag of created DocumentBuilders won't be touched.
         */
        AGNOSTIC,

        /**
         * Fun without pain. This is the default option in this configuration. If namespaces are
         * defined in the document, the definition will be applied to your XPath expressions. Thus
         * you may just use existing namespaces without bothering about prefix mapping.
         * DocumentBuilders are created with namespace awareness set to false.
         */
        HEDONISTIC
    }

    private static final String NON_EXISTING_URL = "http://xmlbeam.org/nonexisting_namespace";

    private NamespacePhilosophy namespacePhilosophy = NamespacePhilosophy.HEDONISTIC;
    private boolean isPrettyPrinting = true;
    private boolean isOmitXMLDeclaration = true;

    /**
     * Empty default constructor, a Configuration has no state.
     */
    public DefaultXMLFactoriesConfig() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilder documentBuilder = createDocumentBuilderFactory().newDocumentBuilder();
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
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
    public Transformer createTransformer(Document... document) {
        try {
            Transformer transformer = createTransformerFactory().newTransformer();
            if (isPrettyPrinting()) {
                // Enable some pretty printing of the resulting xml.
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            }
            if (isOmitXMLDeclaration()) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            return transformer;
        } catch (TransformerConfigurationException e) {
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
    public XPath createXPath(final Document... document) {
        final XPath xPath = createXPathFactory().newXPath();
        if ((document == null) || (document.length == 0) || (!NamespacePhilosophy.HEDONISTIC.equals(namespacePhilosophy))) {
            return xPath;
        }
        // For hedonistic name space philosophy we aspire a reasonable name space mapping.
        final Map<String, String> nameSpaceMapping = DOMHelper.getNamespaceMapping(document[0]);
        final NamespaceContext ctx = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if (prefix == null) {
                    throw new IllegalArgumentException("null not allowed as prefix");
                }
                if (nameSpaceMapping.containsKey(prefix)) {
                    return nameSpaceMapping.get(prefix);
                }
                // Default is a global unique string uri to prevent xpath expression exeptions on
                // nonexisting ns.
                return NON_EXISTING_URL;
            }

            @Override
            public String getPrefix(String uri) {
                for (Entry<String, String> e : nameSpaceMapping.entrySet()) {
                    if (e.getValue().equals(uri)) {
                        return e.getKey();
                    }
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String val) {
                return nameSpaceMapping.keySet().iterator();
            }
        };
        xPath.setNamespaceContext(ctx);
        return xPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XPathFactory createXPathFactory() {
        return XPathFactory.newInstance();
    }

    /**
     * @return the namespacePhilosophy
     */
    public NamespacePhilosophy getNamespacePhilosophy() {
        return namespacePhilosophy;
    }

    /**
     * Getter for pretty printing option.
     * @return true if output will be formatted
     */
    public boolean isPrettyPrinting() {
        return isPrettyPrinting;
    }

    /**
     * @param namespacePhilosophy
     * @return this for convenience
     */
    public XMLFactoriesConfig setNamespacePhilosophy(NamespacePhilosophy namespacePhilosophy) {
        this.namespacePhilosophy = namespacePhilosophy;
        return this;
    }

    
    /**
     * Setter for pretty printing option
     * @param on (true == output will be formatted)
     * @return this for convenience
     */
    public DefaultXMLFactoriesConfig setPrettyPrinting(boolean on) {
        this.isPrettyPrinting = on;
        return this;
    }

    /**
     * @return the isOmitXMLDeclaration
     */
    public boolean isOmitXMLDeclaration() {
        return isOmitXMLDeclaration;
    }

    /**
     * @param isOmitXMLDeclaration the isOmitXMLDeclaration to set
     * @return this for convenience
     */
    public DefaultXMLFactoriesConfig setOmitXMLDeclaration(boolean isOmitXMLDeclaration) {
        this.isOmitXMLDeclaration = isOmitXMLDeclaration;
        return this;
    }

}
