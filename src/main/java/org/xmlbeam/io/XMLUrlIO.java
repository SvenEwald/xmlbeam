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
package org.xmlbeam.io;

import java.util.Map;

import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.util.IOHelper;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class XMLUrlIO {

    private final XMLProjector projector;

    public XMLUrlIO(XMLProjector projector) {
        this.projector = projector;
    }

    /**
     * Create a new projection using a given uri parameter. When the uri starts with the protocol
     * identifier "resource://" the classloader of projection interface will be used to read the
     * resource from the current class path.
     * 
     * @param uri
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new projection instance.
     * @throws IOException
     */
    public <T> T fromURL(final String uri, final Class<T> projectionInterface) throws IOException {
        if (uri.startsWith("resource://")) {
            return new XMLStreamIO(projector).read(projectionInterface.getResourceAsStream(uri.substring("resource://".length())), projectionInterface);
        }
        try {
            Document document = projector.config().getDocumentBuilder().parse(uri);
            return projector.projectXML(document, projectionInterface);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post the projected document to a http url. The response is provided as a raw string.
     * 
     * @param projection
     * @param httpurl
     * @return response as String
     * @throws IOException
     */
    public String toHTTPURLviaPOST(Object projection, String httpurl, Map<String, String> additionalRequestParams) throws IOException {
        return IOHelper.httpPost(httpurl, projection.toString(), additionalRequestParams);
    }
}
