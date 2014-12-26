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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.xmlbeam.XBProjector;
import org.xmlbeam.evaluation.CanEvaluate;
import org.xmlbeam.evaluation.XPathEvaluator;
import org.xmlbeam.util.IOHelper;
import org.xmlbeam.util.intern.DOMHelper;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class XBUrlIO implements CanEvaluate {

    private final XBProjector projector;
    private final Map<String, String> requestProperties = new HashMap<String, String>();
    private final String url;

    /**
     * Constructor with defaults for request parameters.
     *
     * @param projector
     * @param url
     */
    public XBUrlIO(final XBProjector projector, final String url) {
        this.projector = projector;
        this.url = url;
        requestProperties.put("Content-Type", "text/xml");
    }

    /**
     * Create a new projection using a given URL parameter. When the URL starts with the protocol
     * identifier "resource://" the classloader of projection interface will be used to read the
     * resource from the current class path.
     *
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new projection instance.
     * @throws IOException
     */
    public <T> T read(final Class<T> projectionInterface) throws IOException {
        Document document = DOMHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), url, requestProperties, projectionInterface);
        return projector.projectDOMNode(document, projectionInterface);
    }

    /**
     * Post the projected document to a HTTP URL. The response is provided as a raw string.
     *
     * @param projection
     * @return response as String
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public String write(final Object projection) throws IOException {
        return IOHelper.inputStreamToString(IOHelper.httpPost(url, projection.toString(), requestProperties));
    }

    /**
     * Allows to add some request properties.
     *
     * @param params
     * @return this for convenience.
     */
    public XBUrlIO addRequestProperties(final Map<String, String> params) {
        requestProperties.putAll(params);
        return this;
    }

    /**
     * Allows to add a single request property.
     *
     * @param name
     * @param value
     * @return this for convenience.
     */
    public XBUrlIO addRequestProperty(final String name, final String value) {
        requestProperties.put(name, value);
        return this;
    }

    @Override
    public XPathEvaluator evalXPath(final String xpath) throws IOException {
        final Document document = DOMHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), url, requestProperties, null);
        return new XPathEvaluator(projector, document, xpath);
    }

}
