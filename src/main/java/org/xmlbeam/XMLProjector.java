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

import java.text.MessageFormat;

import java.lang.reflect.Modifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.XMLFactoriesConfig;
import org.xmlbeam.types.DefaultTypeConverter;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.DOMUtils;

/**
 * <p>
 * Overview<br>
 * The class XMLProjector is a tool to create, read or write so called
 * "projections". Projections are Java interfaces associated to XML documents.
 * Projections may contain methods annotated with XPath selectors. These XPath
 * expressions define the subset of XML data which is "projected" to Java values
 * and objects.
 * </p>
 * <p>
 * Getters<br>
 * For getter methods (methods with a name-prefix "get", returning a value) the
 * XPath-selected nodes are converted to the method return type. This works with
 * all java primitive types, Strings, and lists or arrays containing primitives
 * or Strings.
 * </p>
 * <p>
 * Setters<br>
 * Setter methods (method with a name starting with "set", having a parameter)
 * can be defined to modify the content of the associated XML document. Not all
 * XPath capabilities define writable projections, so the syntax is limited to
 * selectors of elements and attributes. In contrast to Java Beans a setter
 * method may define a return value with the projection interface type. If this
 * return value is defined, the current projection instance is returned. This
 * allows the definition of projections according to the fluent interface
 * pattern (aka Builder Pattern).
 * </p>
 * <p>
 * Sub Projections<br>
 * For the purpose of accessing structured data elements in the XML document you
 * may define "sub projections" which are projections associated to elements
 * instead to documents. Sub projections can be used as return type of getters
 * and as parameters of setters. This works even in arrays or lists. Because of
 * the infamous Java type erasure you have to specify the component type of the
 * sub projection for a getter returning a list of sub projections. This type is
 * defined as second parameter "targetType" in the {@link Xpath} annotation.
 * </p>
 * <p>
 * Dynamic Projections<br>
 * XPath expressions are evaluated during runtime when the corresponding methods
 * are called. Its possible to use placeholder ("{0}, {1}, {2},... ) in the
 * expression that will be substituted with method parameters before the
 * expression is evaluated. Therefore getters and setters may have multiple
 * parameters which will be applied via a {@link MessageFormat} to build up the
 * final XPath expression. The first parameter of a setter will be used for
 * both, setting the document value and replacing the placeholder "{0}".
 * </p>
 * <p>
 * Projection Mixins<br>
 * A mixin is defined as an object implementing a super interface of a
 * projection. You may associate a mixin with a projection type to add your own
 * code to a projection. This way you can implement validators, make a
 * projection comparable or even share common business logic between multiple
 * projections.
 * </p>
 * 
 * @author sven
 * 
 */
public class XMLProjector implements Serializable {

    private final XMLFactoriesConfig xMLFactoriesConfig;
    private final Map<Class<?>, Map<Class<?>, Object>> customInvokers = new HashMap<Class<?>, Map<Class<?>, Object>>();
    private TypeConverter typeConverter = new DefaultTypeConverter();

    /**
     * A variation of the builder pattern. All methods to configure the projector are hidden in this builder class.
     */
    public class ConfigBuilder {
        TypeConverter getTypeConverter() {
            return XMLProjector.this.typeConverter;
        }

        ConfigBuilder setTypeConverter(TypeConverter converter) {
            XMLProjector.this.typeConverter = converter;
            return this;
        }

        public Object getCustomInvoker(Class<?> projectionInterface, Class<?> declaringClass) {
            if (!customInvokers.containsKey(projectionInterface)) {
                return null;
            }
            return XMLProjector.this.customInvokers.get(projectionInterface).get(declaringClass);
        }

        DocumentBuilder getDocumentBuilder() {
            return xMLFactoriesConfig.createDocumentBuilder();
        }

        Transformer getTransformer() {
            return xMLFactoriesConfig.createTransformer();
        }

        /**
         * @return
         */
        XPath getXPath(Document document) {
            return xMLFactoriesConfig.createXPath(document);
        }
    }

    public XMLProjector() {
        xMLFactoriesConfig = new DefaultXMLFactoriesConfig();
    }

    public XMLProjector(XMLFactoriesConfig xMLFactoriesConfig) {
        this.xMLFactoriesConfig = xMLFactoriesConfig;
    }

    /**
     * Marker interface to determine if a Projection instance was created by a
     * Projector. This will be applied automatically to projections.
     */
    interface Projection extends Serializable {

        Node getXMLNode();

        Class<?> getProjectionInterface();
    }

    /**
     * Create a new projection using a given uri parameter. When the uri starts
     * with the protocol identifier "resource://" the classloader of projection
     * interface will be used to read the resource from the current class path.
     * 
     * @param uri
     * @param clazz
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public <T> T readFromURL(final String uri, final Class<T> clazz) throws IOException {
        try {
            Document document = xMLFactoriesConfig.createDocumentBuilder().parse(uri);
            return projectXML(document, clazz);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a projection from XML to Java.
     * 
     * @param node
     *            XML DOM Node. May be a document or just an element.
     * @param projectionInterface
     *            A Java interface to project the data on.
     * @return a new instance of projectionInterface.
     */
    @SuppressWarnings("unchecked")
    public <T> T projectXML(final Node node, final Class<T> projectionInterface) {
        if (!isValidProjectionInterface(projectionInterface)) {
            throw new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface.");
        }
        return ((T) java.lang.reflect.Proxy.newProxyInstance(projectionInterface.getClassLoader(), new Class[] { projectionInterface, Projection.class, Serializable.class }, new ProjectionInvocationHandler(this, node, projectionInterface)));
    }

    /**
     * @param projectionInterface
     * @return true if param is a public interface.
     */
    private <T> boolean isValidProjectionInterface(final Class<T> projectionInterface) {
        return (projectionInterface != null) && (projectionInterface.isInterface()) && ((projectionInterface.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC);
    };

    /**
     * Use this method to obtain the DOM tree behind a projection. Changing the
     * DOM of a projection is a valid action and may change the results of the
     * projections methods.
     * 
     * @param projection
     * @return Document holding projections data.
     */
    public Document getXMLDocForProjection(final Object projection) {
        if (!(projection instanceof Projection)) {
            throw new IllegalArgumentException("Given projection " + projection + " was not created by me.");
        }
        Node node = ((Projection) projection).getXMLNode();
        if (Node.DOCUMENT_NODE == node.getNodeType()) {
            return (Document) node;
        }
        return node.getOwnerDocument();
    }

    /**
     * Create a new projection using a {@link URL} annotation on this interface.
     * When the URL starts with the protocol identifier "resource://" the class
     * loader of the projection interface will be used to read the resource from
     * the current class path.
     * 
     * @param projectionInterface
     *            a public interface.
     * @return a new projection instance
     * @throws IOException
     */
    public <T> T readFromURLAnnotation(final Class<T> projectionInterface) throws IOException {
        org.xmlbeam.URL doc = projectionInterface.getAnnotation(org.xmlbeam.URL.class);
        if (doc == null) {
            throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + URL.class.getName() + " annotation linking to the document source.");
        }
        final Document document = DOMUtils.getXMLNodeFromURI(xMLFactoriesConfig.createDocumentBuilder(), doc.value(), projectionInterface);

        return projectXML(document, projectionInterface);
    }

    /**
     * Create a new projection for an empty document. Use this to create new
     * documents.
     * 
     * @param projection
     * @return a new projection instance
     */
    public <T> T createEmptyDocumentProjection(Class<T> projection) {
        Document document = xMLFactoriesConfig.createDocumentBuilder().newDocument();
        return projectXML(document, projection);
    }

    /**
     * Register a new mixin for a projection interface. By letting a projection
     * extend another interface you are able to add custom behavior to
     * projections by registering an implementation (called a mixin) of this
     * interface here. Notice that a mixin is registered per projection type.
     * All existing and all future projection instances will change.
     * 
     * Notice that you will break projection serialization if you register a non
     * serializeable mixin.
     * 
     * 
     * @param projectionInterface
     * @param mixinImplementation
     * @return
     */
    public <S, T extends S, P extends S> XMLProjector addProjectionMixin(Class<P> projectionInterface, T mixinImplementation) {
        if (!isValidProjectionInterface(projectionInterface)) {
            throw new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface.");
        }
        Map<Class<?>, Object> map = customInvokers.containsKey(projectionInterface) ? customInvokers.get(projectionInterface) : new HashMap<Class<?>, Object>();
        for (Class<?> type : findAllCommonSuperInterfaces(projectionInterface, mixinImplementation.getClass())) {
            map.put(type, mixinImplementation);
        }
        customInvokers.put(projectionInterface, map);
        return this;

    }

    private Set<Class<?>> findAllCommonSuperInterfaces(Class<?> a, Class<?> b) {
        Set<Class<?>> seta = new HashSet<Class<?>>(findAllSuperInterfaces(a));
        Set<Class<?>> setb = new HashSet<Class<?>>(findAllSuperInterfaces(b));
        seta.retainAll(setb);
        return seta;
    }

    private Collection<? extends Class<?>> findAllSuperInterfaces(Class<?> a) {
        Set<Class<?>> set = new HashSet<Class<?>>();
        if (a.isInterface()) {
            set.add(a);
        }
        for (Class<?> i : a.getInterfaces()) {
            set.addAll(findAllSuperInterfaces(i));
        }
        return set;
    }



    public ConfigBuilder config() {
        return new ConfigBuilder();
    }
    
    
}
