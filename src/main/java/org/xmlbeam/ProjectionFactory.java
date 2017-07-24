/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam;

import org.w3c.dom.Node;
import org.xmlbeam.io.ProjectionIO;

/**
 */
public interface ProjectionFactory {

    /**
     * Create a new projection for an empty document. Use this to create new documents.
     * 
     * @param projectionInterface
     * @return a new projection instance
     */
    <T> T projectEmptyDocument(Class<T> projectionInterface);

    /**
     * Create a new projection for an empty element. Use this to create new elements.
     * 
     * @param name
     *            Element name
     * @param projectionInterface
     * @return a new projection instance
     */
    <T> T projectEmptyElement(final String name, Class<T> projectionInterface);

    /**
     * Creates a projection from XML Documents or Elements to Java.
     * 
     * @param documentOrElement
     *            XML DOM Node. May be a document or just an element.
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new instance of projectionInterface.
     */
    <T> T projectDOMNode(final Node documentOrElement, final Class<T> projectionInterface);

    /**
     * Creates a projection from XML content to Java.
     * 
     * @param xmlContent
     *            a string with XML content
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new instance of projectionInterface.
     */
    <T> T projectXMLString(final String xmlContent, final Class<T> projectionInterface);

    /**
     * Convert a projection to XML string.
     * @param projection
     * @return XML string.
     */
    String asString(Object projection);

    /**
     * Access a projection IO factory.
     * @return a {@link org.xmlbeam.io.ProjectionIO}
     */
    ProjectionIO io();
}
