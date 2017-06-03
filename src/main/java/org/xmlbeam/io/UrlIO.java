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

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import org.w3c.dom.Document;
import org.xmlbeam.AutoMap;
import org.xmlbeam.DefaultXPathBinder;
import org.xmlbeam.XBProjector;
import org.xmlbeam.evaluation.CanEvaluate;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.DocumentResolver;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.evaluation.XPathEvaluator;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.IOHelper;
import org.xmlbeam.util.intern.DocScope;
import org.xmlbeam.util.intern.ReflectionHelper;
import org.xmlbeam.util.intern.Scope;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class UrlIO implements CanEvaluate {

    private final XBProjector projector;
    private final Map<String, String> requestProperties = new HashMap<String, String>();
    private final String url;

    /**
     * Constructor with defaults for request parameters.
     *
     * @param projector
     * @param url
     */
    public UrlIO(final XBProjector projector, final String url) {
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
    @Scope(DocScope.IO)
    public <T> T read(final Class<T> projectionInterface) throws IOException {
        Class<?> callerClass = null;
        if (IOHelper.isResourceProtocol(url)) {
            callerClass = ReflectionHelper.getDirectCallerClass();
        }
        Document document = IOHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), url, requestProperties, projectionInterface, callerClass);
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
    @Scope(DocScope.IO)
    public String write(final Object projection) throws IOException {
        return IOHelper.inputStreamToString(IOHelper.httpPost(url, projection.toString(), requestProperties));
    }

    /**
     * Allows to add some request properties.
     *
     * @param params
     * @return this for convenience.
     */
    @Scope(DocScope.IO)
    public UrlIO addRequestProperties(final Map<String, String> params) {
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
    @Scope(DocScope.IO)
    public UrlIO addRequestProperty(final String name, final String value) {
        requestProperties.put(name, value);
        return this;
    }

    /**
     * Evaluate XPath on the url document.
     *
     * @param xpath
     * @return xpath evaluator
     * @see org.xmlbeam.evaluation.CanEvaluate#evalXPath(java.lang.String)
     */
    @Override
    @Scope(DocScope.IO)
    public XPathEvaluator evalXPath(final String xpath) {
        return new DefaultXPathEvaluator(projector, new DocumentResolver() {
            @Override
            public Document resolve(final Class<?>... resourceAwareClasses) throws IOException {
                return IOHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), url, requestProperties, resourceAwareClasses);
            }
        }, xpath);
    }

    /**
     * Read complete document to a Map.
     *
     * @param valueType
     * @return Closeable map bound to complete document.
     * @throws IOException
     */
    @Scope(DocScope.IO)
    public <T> XBAutoMap<T> asMapOf(final Class<T> valueType) throws IOException {
        DefaultXPathBinder.validateEvaluationType(valueType);
        final Class<?> resourceAwareClass = ReflectionHelper.getDirectCallerClass();
        Document document = IOHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), url, requestProperties, resourceAwareClass);
        InvocationContext invocationContext = new InvocationContext(null, null, null, null, null, valueType, projector);
        return new AutoMap<T>(document, invocationContext, valueType);
    }

}
