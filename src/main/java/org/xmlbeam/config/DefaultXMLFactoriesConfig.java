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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.XMLConstants;
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
import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.util.UnionIterator;
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
     * A facade to provide user defined namespace mappings. This way a document with namespaces can be
     * created from scratch.
     *
     * @author sven
     */
    public interface NSMapping {
        /**
         * @param prefix
         * @param uri
         * @return the current mapping for convenience.
         */
        NSMapping add(String prefix, String uri);

        /**
         * @param uri
         * @return the current mapping for convenience.
         */
        NSMapping addDefaultNamespace(String uri);
    }

    /**
     * This configuration can use one of three different ways to configure namespace handling.
     * Namespaces may be ignored (NIHILISTIC), handled user defined prefix mappings (AGNOSTIC) or mapped
     * automatically to the document prefixes (HEDONISTIC).
     */
    public static enum NamespacePhilosophy {

        /**
         * There is no such thing as a namespace. Only elements and attributes without a namespace will be
         * visible to projections. Using this option prevents getting exceptions when an XPath expression
         * tries to select a non defined namespace. (You can't get errors if you deny the existence of
         * errors.) DocumentBuilders are created with namespace awareness set to false.
         */
        NIHILISTIC,

        /**
         * Maybe there are namespaces. Maybe not. You have to decide for yourself. Neither the xml parser,
         * nor the XPath instances bill be modified by this confiruration. Using this option will require
         * you to subclass this configuration and specify namespace handling yourself. This way allowes you
         * to control prefix to namespace mapping for the XPath expressions. The namespace awareness flag of
         * created DocumentBuilders won't be touched.
         */
        AGNOSTIC,

        /**
         * Fun without pain. This is the default option in this configuration. If namespaces are defined in
         * the document, the definition will be applied to your XPath expressions. Thus you may just use
         * existing namespaces without bothering about prefix mapping. DocumentBuilders are created with
         * namespace awareness set to false.
         */
        HEDONISTIC
    }

    private static final String NON_EXISTING_URL = "http://xmlbeam.org/nonexisting_namespace";
    private static final String[] FEATURE_DEFAULTS = new String[] { "http://apache.org/xml/features/disallow-doctype-decl#true", //
            "http://xml.org/sax/features/external-general-entities#false", //
            "http://xml.org/sax/features/external-parameter-entities#false", //
            "http://apache.org/xml/features/nonvalidating/load-external-dtd#false" };

    private final Map<String, String> USER_DEFINED_MAPPING = new TreeMap<String, String>();

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
            throw new XBException("Error on creating document builder",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentBuilderFactory createDocumentBuilderFactory() {
        DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setXIncludeAware(false);
        instance.setExpandEntityReferences(false);
        for (String featureDefault : FEATURE_DEFAULTS) {
            String[] featureValue = featureDefault.split("#");
            try {
                instance.setFeature(featureValue[0], Boolean.valueOf(featureValue[1]));
            } catch (ParserConfigurationException e) {
                // No worries if one feature is not supported.
            }
        }
        if (!NamespacePhilosophy.AGNOSTIC.equals(namespacePhilosophy)) {
            instance.setNamespaceAware(NamespacePhilosophy.HEDONISTIC.equals(namespacePhilosophy));
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transformer createTransformer(final Document... document) {
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
            throw new XBException("Error on creating transformer",e);
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
            public String getNamespaceURI(final String prefix) {
                if (prefix == null) {
                    throw new IllegalArgumentException("null not allowed as prefix");
                }
                if (nameSpaceMapping.containsKey(prefix)) {
                    return nameSpaceMapping.get(prefix);
                }
                if (USER_DEFINED_MAPPING.containsKey(prefix)) {
                    return USER_DEFINED_MAPPING.get(prefix);
                }
                // Default is a global unique string uri to prevent xpath expression exeptions on
                // nonexisting ns.
                return NON_EXISTING_URL;
            }

            @Override
            public String getPrefix(final String uri) {
                for (Entry<String, String> e : nameSpaceMapping.entrySet()) {
                    if (e.getValue().equals(uri)) {
                        return e.getKey();
                    }
                }
                for (Entry<String, String> e : USER_DEFINED_MAPPING.entrySet()) {
                    if (e.getValue().equals(uri)) {
                        return e.getKey();
                    }
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(final String val) {
                return new UnionIterator<String>(nameSpaceMapping.keySet().iterator(), USER_DEFINED_MAPPING.keySet().iterator());
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
     *
     * @return true if output will be formatted
     */
    public boolean isPrettyPrinting() {
        return isPrettyPrinting;
    }

    /**
     * @param namespacePhilosophy
     * @return this for convenience
     */
    public XMLFactoriesConfig setNamespacePhilosophy(final NamespacePhilosophy namespacePhilosophy) {
        this.namespacePhilosophy = namespacePhilosophy;
        return this;
    }

    /**
     * Setter for pretty printing option
     *
     * @param on
     *            (true == output will be formatted)
     * @return this for convenience
     */
    public DefaultXMLFactoriesConfig setPrettyPrinting(final boolean on) {
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
     * @param isOmitXMLDeclaration
     *            the isOmitXMLDeclaration to set
     * @return this for convenience
     */
    public DefaultXMLFactoriesConfig setOmitXMLDeclaration(final boolean isOmitXMLDeclaration) {
        this.isOmitXMLDeclaration = isOmitXMLDeclaration;
        return this;
    }

    /**
     * @return A NSMapping that can be used to create documents with namespaces from scratch. Just add
     *         your prefixes and ns uris.
     */
    public NSMapping createNameSpaceMapping() {
        if (!NamespacePhilosophy.HEDONISTIC.equals(namespacePhilosophy)) {
            throw new IllegalStateException("To use a namespace mapping, you need to use the HEDONISTIC NamespacePhilosophy.");
        }
        return new NSMapping() {

            @Override
            public NSMapping add(final String prefix, final String uri) {
                if ((prefix == null) || (prefix.isEmpty())) {
                    throw new IllegalArgumentException("prefix must not be empty");
                }
                if ((uri == null) || (uri.isEmpty())) {
                    throw new IllegalArgumentException("uri must not be empty");
                }
                if (USER_DEFINED_MAPPING.containsKey(prefix) && (!uri.equals(USER_DEFINED_MAPPING.get(prefix)))) {
                    throw new IllegalArgumentException("The prefix '" + prefix + "' is bound to namespace '" + USER_DEFINED_MAPPING.get(prefix) + " already.");
                }
                USER_DEFINED_MAPPING.put(prefix, uri);
                return this;
            }

            @Override
            public NSMapping addDefaultNamespace(String uri) {
                return add("xbdefaultns", uri);
            }

        };
    }

    @Override
    public Map<String, String> getUserDefinedNamespaceMapping() {
        return Collections.unmodifiableMap(USER_DEFINED_MAPPING);
    }

}
