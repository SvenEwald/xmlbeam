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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBUpdate;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.CanEvaluateOrProject;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.DocumentResolver;
import org.xmlbeam.evaluation.XPathEvaluator;
import org.xmlbeam.externalizer.Externalizer;
import org.xmlbeam.externalizer.ExternalizerAdapter;
import org.xmlbeam.io.XBFileIO;
import org.xmlbeam.io.XBStreamInput;
import org.xmlbeam.io.XBStreamOutput;
import org.xmlbeam.io.XBUrlIO;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.StringRenderer;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.IOHelper;
import org.xmlbeam.util.intern.DOMHelper;
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
public class XBProjector implements Serializable, ProjectionFactory {

    private static final Externalizer NOOP_EXTERNALIZER = new ExternalizerAdapter();

    private final ConfigBuilder configBuilder = new ConfigBuilder();

    private Externalizer externalizer = NOOP_EXTERNALIZER;

    private final Set<Flags> flags;

    /**
     * A variation of the builder pattern. All methods to configure the projector are hidden in this
     * builder class.
     */
    public class ConfigBuilder implements ProjectionFactoryConfig {

        /**
         * Access the {@link XMLFactoriesConfig} as the given subtype to conveniently access
         * additional methods.
         *
         * @param clazz
         * @return casted XMLFactoriesConfig
         */
        public <T extends XMLFactoriesConfig> T as(final Class<T> clazz) {
            return clazz.cast(xMLFactoriesConfig);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeConverter getTypeConverter() {
            return XBProjector.this.typeConverter;
        }

        /**
         * Cast the type converter to the current type.
         *
         * @param clazz
         * @return Type converter casted down to clazz.
         */
        public <T extends TypeConverter> T getTypeConverterAs(final Class<T> clazz) {
            return clazz.cast(getTypeConverter());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigBuilder setTypeConverter(final TypeConverter converter) {
            XBProjector.this.typeConverter = converter;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigBuilder setExternalizer(final Externalizer e10r) {
            XBProjector.this.externalizer = e10r == null ? NOOP_EXTERNALIZER : e10r;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Externalizer getExternalizer() {
            return XBProjector.this.externalizer;
        }

        /**
         * @param clazz
         * @return Externalizer cast down to the type clazz
         */
        public <T extends Externalizer> T getExternalizerAs(final Class<? extends T> clazz) {
            return clazz.cast(getExternalizer());
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
        public Transformer createTransformer(final Document... document) {
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
        public XPath createXPath(final Document... document) {
            return XBProjector.this.xMLFactoriesConfig.createXPath(document);
        }

        /**
         * @return StringRenderer used to convert objects into strings
         */
        public StringRenderer getStringRenderer() {
            return XBProjector.this.stringRenderer;
        }

        /**
         * Cast the type StringRenderer to the current type.
         *
         * @param clazz
         * @return StringRenderer casted down to clazz.
         */
        public <T extends StringRenderer> T getStringRendererAs(final Class<T> clazz) {
            return clazz.cast(getTypeConverter());
        }

        /**
         * @param renderer
         *            to be used to convert objects into strings
         * @return this for convenience
         */
        public ConfigBuilder setStringRenderer(final StringRenderer renderer) {
            XBProjector.this.stringRenderer = renderer;
            return this;
        }

        @Override
        public Map<String, String> getUserDefinedNamespaceMapping() {
            return xMLFactoriesConfig.getUserDefinedNamespaceMapping();
        }

    }

    /**
     * A variation of the builder pattern. Mixin related methods are grouped behind this builder
     * class.
     */
    class MixinBuilder implements MixinHolder {
        /**
         * {@inheritDoc}
         */
        @Override
        public <S, M extends S, P extends S> XBProjector addProjectionMixin(final Class<P> projectionInterface, final M mixinImplementation) {
            ensureIsValidProjectionInterface(projectionInterface);
            Map<Class<?>, Object> map = mixins.containsKey(projectionInterface) ? mixins.get(projectionInterface) : new HashMap<Class<?>, Object>();
            for (Class<?> type : ReflectionHelper.findAllCommonSuperInterfaces(projectionInterface, mixinImplementation.getClass())) {
                map.put(type, mixinImplementation);
            }
            mixins.put(projectionInterface, map);
            return XBProjector.this;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public <S, M extends S, P extends S> M getProjectionMixin(final Class<P> projectionInterface, final Class<M> mixinInterface) {
            if (!mixins.containsKey(projectionInterface)) {
                return null;
            }
            return (M) mixins.get(projectionInterface).get(mixinInterface);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public <S, M extends S, P extends S> M removeProjectionMixin(final Class<P> projectionInterface, final Class<M> mixinInterface) {
            if (!mixins.containsKey(projectionInterface)) {
                return null;
            }
            return (M) mixins.get(projectionInterface).remove(mixinInterface);
        }
    }

    /**
     * A variation of the builder pattern. IO related methods are grouped behind this builder class.
     */
    class IOBuilder implements ProjectionIO {

        /**
         * {@inheritDoc}
         */
        @Override
        public XBFileIO file(final File file) {
            return new XBFileIO(XBProjector.this, file);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XBFileIO file(final String fileName) {
            return new XBFileIO(XBProjector.this, fileName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XBUrlIO url(final String url) {
            return new XBUrlIO(XBProjector.this, url);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XBStreamInput stream(final InputStream is) {
            return new XBStreamInput(XBProjector.this, is);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XBStreamOutput stream(final OutputStream os) {
            return new XBStreamOutput(XBProjector.this, os);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T fromURLAnnotation(final Class<T> projectionInterface, final Object... optionalParams) throws IOException {
            org.xmlbeam.annotation.XBDocURL doc = projectionInterface.getAnnotation(org.xmlbeam.annotation.XBDocURL.class);
            if (doc == null) {
                throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + XBDocURL.class.getName() + " annotation linking to the document source.");
            }
            XBUrlIO urlIO = url(MessageFormat.format(doc.value(), optionalParams));
            urlIO.addRequestProperties(filterRequestParamsFromParams(doc.value(), optionalParams));
            return urlIO.read(projectionInterface);
        }

        /**
         * @param projectionInterface
         * @param optionalParams
         * @return
         */
        @SuppressWarnings("unchecked")
        Map<String, String> filterRequestParamsFromParams(final String url, final Object... optionalParams) {
            Map<String, String> requestParams = new HashMap<String, String>();
            Format[] formats = new MessageFormat(url).getFormatsByArgumentIndex();
            for (int i = 0; i < optionalParams.length; ++i) {
                if (i >= formats.length) {
                    if ((optionalParams[i] instanceof Map<?, ?>)) {
                        requestParams.putAll((Map<String, String>) optionalParams[i]);
                    }
                    continue;
                }
                if (formats[i] == null) {
                    if ((optionalParams[i] instanceof Map<?, ?>)) {
                        requestParams.putAll((Map<String, String>) optionalParams[i]);
                    }
                }
            }
            return requestParams;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toURLAnnotationViaPOST(final Object projection, final Object... optionalParams) throws IOException, URISyntaxException {
            Class<?> projectionInterface = checkProjectionInstance(projection).getProjectionInterface();
            org.xmlbeam.annotation.XBDocURL doc = projectionInterface.getAnnotation(org.xmlbeam.annotation.XBDocURL.class);
            if (doc == null) {
                throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + XBDocURL.class.getName() + " annotation linking to the document source.");
            }
            XBUrlIO urlIO = url(MessageFormat.format(doc.value(), optionalParams));
            urlIO.addRequestProperties(filterRequestParamsFromParams(doc.value(), optionalParams));
            return urlIO.write(projection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T projectEmptyDocument(final Class<T> projectionInterface) {
        Document document = xMLFactoriesConfig.createDocumentBuilder().newDocument();
        return projectDOMNode(document, projectionInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T projectEmptyElement(final String name, final Class<T> projectionInterface) {
        Document document = xMLFactoriesConfig.createDocumentBuilder().newDocument();
        Element element = document.createElement(name);
        return projectDOMNode(element, projectionInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T projectDOMNode(final Node documentOrElement, final Class<T> projectionInterface) {
        ensureIsValidProjectionInterface(projectionInterface);

        if (documentOrElement == null) {
            throw new IllegalArgumentException("Parameter node must not be null");
        }

        final Map<Class<?>, Object> mixinsForProjection = mixins.containsKey(projectionInterface) ? Collections.unmodifiableMap(mixins.get(projectionInterface)) : Collections.<Class<?>, Object> emptyMap();
        final ProjectionInvocationHandler projectionInvocationHandler = new ProjectionInvocationHandler(XBProjector.this, documentOrElement, projectionInterface, mixinsForProjection, flags.contains(Flags.TO_STRING_RENDERS_XML), flags.contains(Flags.ABSENT_IS_EMPTY));
        final Set<Class<?>> interfaces = new HashSet<Class<?>>();
        //.toArray() new Class[] { projectionInterface, DOMAccess.class, Serializable.class };
        interfaces.add(projectionInterface);
        interfaces.add(DOMAccess.class);
        interfaces.add(Serializable.class);
        if (flags.contains(Flags.SYNCHRONIZE_ON_DOCUMENTS)) {
            final Document document = DOMHelper.getOwnerDocumentFor(documentOrElement);
            InvocationHandler synchronizedInvocationHandler = new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                    synchronized (document) {
                        return projectionInvocationHandler.invoke(proxy, method, args);
                    }
                }
            };
            return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), interfaces.toArray(new Class<?>[interfaces.size()]), synchronizedInvocationHandler));
        }
        return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), interfaces.toArray(new Class<?>[interfaces.size()]), projectionInvocationHandler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T projectXMLString(final String xmlContent, final Class<T> projectionInterface) {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes("utf-8"));
            return new XBStreamInput(this, inputStream).read(projectionInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param xmlContent
     * @return {@link DefaultXPathEvaluator}
     */
    public CanEvaluateOrProject onXMLString(final String xmlContent) {
        return new CanEvaluateOrProject() {

            @Override
            public XPathEvaluator evalXPath(final String xpath) {
                try {
                    final ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes("utf-8"));

                    return new DefaultXPathEvaluator(XBProjector.this, new DocumentResolver() {

                        @Override
                        public Document resolve(final Class<?>... resourceAwareClass) {
                            return IOHelper.loadDocument(XBProjector.this, inputStream);
                        }
                    }, xpath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> T createProjection(final Class<T> projectionInterface) {
                return projectXMLString(xmlContent, projectionInterface);
            }
        };

    }

    private final XMLFactoriesConfig xMLFactoriesConfig;

    private final Map<Class<?>, Map<Class<?>, Object>> mixins = new HashMap<Class<?>, Map<Class<?>, Object>>();

    private TypeConverter typeConverter = new DefaultTypeConverter(Locale.getDefault(), TimeZone.getTimeZone("GMT"));
    private StringRenderer stringRenderer = (StringRenderer) typeConverter;

    /**
     * Global projector configuration options.
     */
    public enum Flags {

        /**
         * Enables thread safety by removing concurrent DOM access. Useful if the underlying DOM
         * implementation is not thread safe.
         */
        SYNCHRONIZE_ON_DOCUMENTS,
        /**
         * Let the projections toString() method render the projection target as XML. Be careful if
         * your documents get large. toString() might be used frequently by the IDE your debugging
         * in.
         */
        TO_STRING_RENDERS_XML,
        /**
         * Option to strip empty nodes from the result.
         */
        OMIT_EMPTY_NODES,
        /**
         * If a node is not present, handle it like it is empty.
         */
        ABSENT_IS_EMPTY
    }

    /**
     * Constructor. Use me to create a projector with defaults.
     *
     * @param optionalFlags
     */
    public XBProjector(final Flags... optionalFlags) {
        this(new DefaultXMLFactoriesConfig(), optionalFlags);
    }

    private static <T extends Enum<T>> Set<T> unfold(final T[] array) {
        if ((array == null) || (array.length == 0)) {
            return Collections.emptySet();
        }
        EnumSet<T> enumSet = EnumSet.of(array[0]);
        for (int i = 1; i < array.length; ++i) {
            enumSet.add(array[i]);
        }
        return enumSet;
    }

    /**
     * @param xMLFactoriesConfig
     * @param optionalFlags
     */
    public XBProjector(final XMLFactoriesConfig xMLFactoriesConfig, final Flags... optionalFlags) {
        this.xMLFactoriesConfig = xMLFactoriesConfig;
        this.flags = unfold(optionalFlags);
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
     * Shortcut for creating a {@link MixinHolder} object add or remove mixins to projections.
     *
     * @return a new MixinBuilder for this projector.
     */
    public MixinHolder mixins() {
        return new MixinBuilder();
    }

    /**
     * Ensures that the given object is a projection created by a projector.
     *
     * @param projection
     * @return
     */
    private DOMAccess checkProjectionInstance(final Object projection) {
        if (java.lang.reflect.Proxy.isProxyClass(projection.getClass())) {
            InvocationHandler invocationHandler = java.lang.reflect.Proxy.getInvocationHandler(projection);
            if (invocationHandler instanceof ProjectionInvocationHandler) {
                if (projection instanceof DOMAccess) {
                    return (DOMAccess) projection;
                }
            }
        }
        throw new IllegalArgumentException("Given object " + projection + " is not a projection.");
    }

    /**
     * @param projectionInterface
     */
    private void ensureIsValidProjectionInterface(final Class<?> projectionInterface) {
        if (projectionInterface == null) {
            throw new NullPointerException("Parameter projectionInterface must not be null, but is.");
        }
        if ((!projectionInterface.isInterface())) {
            throw new IllegalArgumentException("Parameter "+projectionInterface+" is not an interface"); 
        }
        if (projectionInterface.isAnnotation()) {
            throw new IllegalArgumentException("Parameter " + projectionInterface + " is an annotation interface. Remove the @ and try again.");
        }
        for (Method method : projectionInterface.getMethods()) {
            final boolean isRead = (method.getAnnotation(XBRead.class) != null);
            final boolean isWrite = (method.getAnnotation(XBWrite.class) != null);
            final boolean isDelete = (method.getAnnotation(XBDelete.class) != null);
            final boolean isUpdate = (method.getAnnotation(XBUpdate.class) != null);
            final boolean isExternal = (method.getAnnotation(XBDocURL.class) != null);
            final boolean isThrowsException = (method.getExceptionTypes().length > 0);
            if (isRead ? isUpdate || isWrite || isDelete : (isUpdate ? isWrite || isDelete : isWrite && isDelete)) {
                throw new IllegalArgumentException("Method " + method + " has to many annotations. Decide for one of @" + XBRead.class.getSimpleName() + ", @" + XBWrite.class.getSimpleName() + ", @" + XBUpdate.class.getSimpleName() + ", or @" + XBDelete.class.getSimpleName());
            }
            if (isExternal && (isWrite || isUpdate || isDelete)) {
                throw new IllegalArgumentException("Method " + method + " was declared as writing projection but has a @" + XBDocURL.class.getSimpleName() + " annotation. Defining external projections is only possible when reading because there is no DOM attached.");
            }
            if (isRead) {
                if (!ReflectionHelper.hasReturnType(method)) {
                    throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() + " annotation, but has no return type.");
                }
                if (ReflectionHelper.isRawType(method.getGenericReturnType())) {
                    throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() + " annotation, but has a raw return type.");
                }
                if (method.getExceptionTypes().length > 1) {
                    throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() + " annotation, but declares to throw multiple exceptions. Which one should I throw?");
                }
                if (ReflectionHelper.isOptional(method.getReturnType()) && isThrowsException) {
                    throw new IllegalArgumentException("Method " + method + " has an Optional<> return type, but declares to throw an exception. Exception will never be thrown because return value must not be null.");
                }
            }
            if ((isWrite || isUpdate || isDelete) && isThrowsException) {
                throw new IllegalArgumentException("Method " + method + " declares to throw exception " + method.getExceptionTypes()[0].getSimpleName() + " but is not a reading projection method. When should this exception be thrown?");
            }
            if (isWrite) {
                if (!ReflectionHelper.hasParameters(method)) {
                    throw new IllegalArgumentException("Method " + method + " has @" + XBWrite.class.getSimpleName() + " annotaion, but has no paramerter");
                }
            }
            if (isUpdate) {
                if (!ReflectionHelper.hasParameters(method)) {
                    throw new IllegalArgumentException("Method " + method + " has @" + XBUpdate.class.getSimpleName() + " annotaion, but has no paramerter");
                }
            }
            for (Class<?> clazz : method.getParameterTypes()) {
                if (ReflectionHelper.isOptional(clazz)) {
                    throw new IllegalArgumentException("Method " + method + " has java.util.Optional as a parameter type. You simply never should not do this.");
                }
            }
            int count = 0;
            for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
                for (Annotation a : paramAnnotations) {
                    if (XBValue.class.equals(a.annotationType())) {
                        if (!(isWrite || isUpdate)) {
                            throw new IllegalArgumentException("Method " + method + " is not a writing projection method, but has an @" + XBValue.class.getSimpleName() + " annotaion.");
                        }
                        if (count > 0) {
                            throw new IllegalArgumentException("Method " + method + " has multiple @" + XBValue.class.getSimpleName() + " annotaions.");
                        }
                        ++count;
                    }
                }
            }
        }

    }

    /**
     * Access to the input/output features of this projector.
     *
     * @return A new IOBuilder providing methods to read or write projections.
     */
    @Override
    public ProjectionIO io() {
        return new IOBuilder();
    }

    /**
     * @param projection
     * @return an XML string of the projection target.
     */
    @Override
    public String asString(final Object projection) {
        if (!(projection instanceof DOMAccess)) {
            throw new IllegalArgumentException("Argument is not a projection.");
        }
        final DOMAccess domAccess = (DOMAccess) projection;
        return domAccess.asString();
    }

    /**
     * read only access to flags. Use constructor to set.
     *
     * @return flags.
     */
    public Set<Flags> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

}
