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
package org.xmlbeam;

import java.net.URISyntaxException;

import java.text.Format;
import java.text.MessageFormat;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.externalizer.Externalizer;
import org.xmlbeam.externalizer.NotExternalizedExternalizer;
import org.xmlbeam.io.XBFileIO;
import org.xmlbeam.io.XBStreamInput;
import org.xmlbeam.io.XBStreamOutput;
import org.xmlbeam.io.XBUrlIO;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.ReflectionHelper;

/**
 * <p>
 * Overview<br>
 * The class XMLProjector is a tool to create, read or write so called "projections". Projections
 * are Java interfaces associated to XML documents. Projections may contain methods annotated with
 * XPath selectors. These XPath expressions define the subset of XML data which is "projected" to
 * Java values and objects.
 * </p>
 * <p>
 * Getters<br>
 * For getter methods (methods with a name-prefix "get", returning a value) the XPath-selected nodes
 * are converted to the method return type. This works with all java primitive types, Strings, and
 * lists or arrays containing primitives or Strings.
 * </p>
 * <p>
 * Setters<br>
 * Setter methods (method with a name starting with "set", having a parameter) can be defined to
 * modify the content of the associated XML document. Not all XPath capabilities define writable
 * projections, so the syntax is limited to selectors of elements and attributes. In contrast to
 * Java Beans a setter method may define a return value with the projection interface type. If this
 * return value is defined, the current projection instance is returned. This allows the definition
 * of projections according to the fluent interface pattern (aka Builder Pattern).
 * </p>
 * <p>
 * Sub Projections<br>
 * For the purpose of accessing structured data elements in the XML document you may define
 * "sub projections" which are projections associated to elements instead to documents. Sub
 * projections can be used as return type of getters and as parameters of setters. This works even
 * in arrays or lists. Because of the infamous Java type erasure you have to specify the component
 * type of the sub projection for a getter returning a list of sub projections. This type is defined
 * as second parameter "targetType" in the {@link XBRead} annotation.
 * </p>
 * <p>
 * Dynamic Projections<br>
 * XPath expressions are evaluated during runtime when the corresponding methods are called. Its
 * possible to use placeholder ("{0}, {1}, {2},... ) in the expression that will be substituted with
 * method parameters before the expression is evaluated. Therefore getters and setters may have
 * multiple parameters which will be applied via a {@link MessageFormat} to build up the final XPath
 * expression. The first parameter of a setter will be used for both, setting the document value and
 * replacing the placeholder "{0}".
 * </p>
 * <p>
 * Projection Mixins<br>
 * A mixin is defined as an object implementing a super interface of a projection. You may associate
 * a mixin with a projection type to add your own code to a projection. This way you can implement
 * validators, make a projection comparable or even share common business logic between multiple
 * projections.
 * </p>
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings("serial")
public class XBProjector implements Serializable {

    private static final Externalizer NOOP_EXTERNALIZER = new NotExternalizedExternalizer();

    private final ConfigBuilder configBuilder = new ConfigBuilder();

    public Externalizer externalizer = NOOP_EXTERNALIZER;

    /**
     * A variation of the builder pattern. All methods to configure the projector are hidden in this
     * builder class.
     */
    public class ConfigBuilder implements XMLFactoriesConfig {

        /**
         * Access the {@link XMLFactoriesConfig} as the given subtype to conveniently access
         * additional methods.
         * 
         * @param clazz
         * @return
         */
        @SuppressWarnings("unchecked")
        public <T extends XMLFactoriesConfig> T as(Class<T> clazz) {
            return (T) xMLFactoriesConfig;
        }

        public TypeConverter getTypeConverter() {
            return XBProjector.this.typeConverter;
        }

        public ConfigBuilder setTypeConverter(TypeConverter converter) {
            XBProjector.this.typeConverter = converter;
            return this;
        }

        /**
         * Every String literal used in a annotation may be externalized (e.g. to a property file).
         * You may register a Externalizer instance here and reference it in a projection
         * definition.
         * 
         * @param e10r
         * @return
         */
        public ConfigBuilder setExternalizer(Externalizer e10r) {
            XBProjector.this.externalizer = e10r == null ? NOOP_EXTERNALIZER : e10r;
            return this;
        }

        public Externalizer getExternalizer() {
            return XBProjector.this.externalizer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TransformerFactory createTransformerFactory() {
            return XBProjector.this.xMLFactoriesConfig.createTransformerFactory();
        }

        /**
         * {@inheritDoc}
         */

        @Override
        public DocumentBuilderFactory createDocumentBuilderFactory() {
            return XBProjector.this.xMLFactoriesConfig.createDocumentBuilderFactory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XPathFactory createXPathFactory() {
            return XBProjector.this.xMLFactoriesConfig.createXPathFactory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Transformer createTransformer(Document... document) {
            return XBProjector.this.xMLFactoriesConfig.createTransformer(document);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentBuilder createDocumentBuilder() {
            return XBProjector.this.xMLFactoriesConfig.createDocumentBuilder();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XPath createXPath(Document... document) {
            return XBProjector.this.xMLFactoriesConfig.createXPath(document);
        }
    }

    /**
     * A variation of the builder pattern. Mixin related methods are grouped behind this builder
     * class.
     */
    public class MixinBuilder {
        /**
         * Register a new mixin for a projection interface. By letting a projection extend another
         * interface you are able to add custom behavior to projections by registering an
         * implementation (called a mixin) of this interface here. A mixin is registered per
         * projection type. Only one mixin implementation per projection and mixin type is possible.
         * All existing and all future projection instances will change. Notice that you will break
         * projection serialization if you register a non serializeable mixin.
         * 
         * @param projectionInterface
         * @param mixinImplementation
         * @return
         */
        public <S, M extends S, P extends S> XBProjector addProjectionMixin(Class<P> projectionInterface, M mixinImplementation) {
            if (!isValidProjectionInterface(projectionInterface)) {
                throw new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface.");
            }
            Map<Class<?>, Object> map = mixins.containsKey(projectionInterface) ? mixins.get(projectionInterface) : new HashMap<Class<?>, Object>();
            for (Class<?> type : ReflectionHelper.findAllCommonSuperInterfaces(projectionInterface, mixinImplementation.getClass())) {
                map.put(type, mixinImplementation);
            }
            mixins.put(projectionInterface, map);
            return XBProjector.this;

        }

        /**
         * Get the mixin implementation registered for the given projection.
         * 
         * @param projectionInterface
         * @param mixinInterface
         * @return the registered mixin implementation. null if none is present.
         */
        @SuppressWarnings("unchecked")
        public <S, M extends S, P extends S> M getProjectionMixin(Class<P> projectionInterface, Class<M> mixinInterface) {
            if (!mixins.containsKey(projectionInterface)) {
                return null;
            }
            return (M) mixins.get(projectionInterface).get(mixinInterface);
        }

        /**
         * Remove the mixin implementation registered for the given projection.
         * 
         * @param projectionInterface
         * @param mixinInterface
         * @return the registered mixin implementation. null if none was present.
         */
        @SuppressWarnings("unchecked")
        public <S, M extends S, P extends S> M removeProjectionMixin(Class<P> projectionInterface, Class<M> mixinInterface) {
            if (!mixins.containsKey(projectionInterface)) {
                return null;
            }
            return (M) mixins.get(projectionInterface).remove(mixinInterface);
        }
    }

    /**
     * A variation of the builder pattern. IO related methods are grouped behind this builder class.
     */
    public class IOBuilder {

        public XBFileIO file(File file) {
            return new XBFileIO(XBProjector.this, file);
        }

        public XBFileIO file(String fileName) {
            return new XBFileIO(XBProjector.this, fileName);
        }

        public XBUrlIO url(String url) {
            return new XBUrlIO(XBProjector.this, url);
        }

        public XBStreamInput stream(InputStream is) {
            return new XBStreamInput(XBProjector.this, is);
        }

        public XBStreamOutput stream(OutputStream os) {
            return new XBStreamOutput(XBProjector.this, os);
        }

        /**
         * Create a new projection using a {@link XBDocURL} annotation on this interface. When the
         * XBDocURL starts with the protocol identifier "resource://" the class loader of the
         * projection interface will be used to read the resource from the current class path.
         * 
         * @param projectionInterface
         *            a public interface.
         * @return a new projection instance
         * @throws IOException
         */
        public <T> T fromURLAnnotation(final Class<T> projectionInterface, Object... params) throws IOException {
            org.xmlbeam.annotation.XBDocURL doc = projectionInterface.getAnnotation(org.xmlbeam.annotation.XBDocURL.class);
            if (doc == null) {
                throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + XBDocURL.class.getName() + " annotation linking to the document source.");
            }
            XBUrlIO urlIO = url(MessageFormat.format(doc.value(), params));
            urlIO.addRequestProperties(filterRequestParamsFromParams(doc.value(), params));
            return urlIO.read(projectionInterface);
        }

        /**
         * @param projectionInterface
         * @param params
         * @return
         */
        @SuppressWarnings("unchecked")
        Map<String, String> filterRequestParamsFromParams(final String url, final Object... params) {
            Map<String, String> requestParams = new HashMap<String, String>();
            Format[] formats = new MessageFormat(url).getFormatsByArgumentIndex();
            for (int i = 0; i < params.length; ++i) {
                if (i >= formats.length) {
                    if ((params[i] instanceof Map)) {
                        requestParams.putAll((Map<? extends String, ? extends String>) params[i]);
                    }
                    continue;
                }
                if (formats[i] == null) {
                    if ((params[i] instanceof Map)) {
                        requestParams.putAll((Map<? extends String, ? extends String>) params[i]);
                    }
                }
            }
            return requestParams;
        }

        /**
         * Write projected document to url (file or http post) of {@link XBDocURL} annotation.
         * 
         * @param projection
         * @return response of http post or null for file urls.
         * @throws IOException
         * @throws URISyntaxException
         */
        public String toURLAnnotationViaPOST(final Object projection, Object... params) throws IOException, URISyntaxException {
            Class<?> projectionInterface = checkProjectionInstance(projection).getProjectionInterface();
            org.xmlbeam.annotation.XBDocURL doc = projectionInterface.getAnnotation(org.xmlbeam.annotation.XBDocURL.class);
            if (doc == null) {
                throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + XBDocURL.class.getName() + " annotation linking to the document source.");
            }

            XBUrlIO urlIO = url(MessageFormat.format(doc.value(), params));
            urlIO.addRequestProperties(filterRequestParamsFromParams(doc.value(), params));

            return urlIO.write(projection);

        }
    }

    /**
     * Create a new projection for an empty document. Use this to create new documents.
     * 
     * @param projectionInterface
     * @return a new projection instance
     */
    public <T> T projectEmptyDocument(Class<T> projectionInterface) {
        Document document = xMLFactoriesConfig.createDocumentBuilder().newDocument();
        return projectDOMNode(document, projectionInterface);
    }

    /**
     * Create a new projection for an empty element. Use this to create new elements.
     * 
     * @param name
     *            Element name
     * @param projectionInterface
     * @return a new projection instance
     */
    public <T> T projectEmptyElement(final String name, Class<T> projectionInterface) {
        Document document = xMLFactoriesConfig.createDocumentBuilder().newDocument();
        Element element = document.createElement(name);
        return projectDOMNode(element, projectionInterface);
    }

    /**
     * Creates a projection from XML Documents or Elements to Java.
     * 
     * @param documentOrElement
     *            XML DOM Node. May be a document or just an element.
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new instance of projectionInterface.
     */
    @SuppressWarnings("unchecked")
    public <T> T projectDOMNode(final Node documentOrElement, final Class<T> projectionInterface) {
        if (!isValidProjectionInterface(projectionInterface)) {
            throw new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface.");
        }
        if (documentOrElement == null) {
            throw new IllegalArgumentException("Parameter node must not be null");
        }

        return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), new Class[] { projectionInterface, InternalProjection.class, Serializable.class }, new ProjectionInvocationHandler(XBProjector.this, documentOrElement, projectionInterface)));
    }

    /**
     * Creates a projection from XML content to Java.
     * 
     * @param xmlContent
     *            a string with XML content
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new instance of projectionInterface.
     */
    public <T> T projectXMLString(final String xmlContent, final Class<T> projectionInterface) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes("utf-8"));
            return new XBStreamInput(this, inputStream).read(projectionInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marker interface to determine if a Projection instance was created by a Projector. This will
     * be applied automatically to projections.
     */
    interface InternalProjection extends DOMAccess {
    }

    private final XMLFactoriesConfig xMLFactoriesConfig;

    private final Map<Class<?>, Map<Class<?>, Object>> mixins = new HashMap<Class<?>, Map<Class<?>, Object>>();

    private TypeConverter typeConverter = new DefaultTypeConverter();

    /**
     * Constructor. Use me to create a projector with defaults.
     */
    public XBProjector() {
        xMLFactoriesConfig = new DefaultXMLFactoriesConfig();
    }

    /**
     * @param xMLFactoriesConfig
     */
    public XBProjector(XMLFactoriesConfig xMLFactoriesConfig) {
        this.xMLFactoriesConfig = xMLFactoriesConfig;
    }

    /**
     * Shortcut for creating a {@link ConfigBuilder} object to change the projectors configuration.
     * 
     * @return a new ConfigBuilder for this projector.
     */
    public ConfigBuilder config() {
        return configBuilder;
    }

    /**
     * Shortcut for creating a {@link MixinBuilder} object add or remove mixins to projections.
     * 
     * @return a new MixinBuilder for this projector.
     */
    public MixinBuilder mixins() {
        return new MixinBuilder();
    }

    /**
     * Ensures that the given object is a projection created by a projector.
     * 
     * @param projection
     * @return
     */
    private InternalProjection checkProjectionInstance(Object projection) {
        if (!(projection instanceof InternalProjection)) {
            throw new IllegalArgumentException("Given object " + projection + " is not a projection.");
        }
        return (InternalProjection) projection;
    }

    /**
     * @param projectionInterface
     * @return true if param is a public interface.
     */
    private <T> boolean isValidProjectionInterface(final Class<T> projectionInterface) {
        return (projectionInterface != null) && (projectionInterface.isInterface()) && ((projectionInterface.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC);
    }

    /**
     * Access to the input/output features of this projector.
     * 
     * @return A new IOBuilder providing methods to read or write projections.
     */
    public IOBuilder io() {
        return new IOBuilder();
    }

}
