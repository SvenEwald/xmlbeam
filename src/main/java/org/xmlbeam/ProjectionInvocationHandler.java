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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector.InternalProjection;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBUpdate;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.externalizer.Externalizer;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.ReflectionHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;
import org.xmlbeam.util.intern.duplex.ExpressionType;
import org.xmlbeam.util.intern.duplex.XBPathParsingException;

/**
 * This class implements the "magic" behind projection methods. Each projection is linked with a
 * ProjectionInvocatonHandler which handles method invocations on the projections. Notice that this
 * class is not part of the public API. You should not get in touch with this class at all. See
 * {@link org.xmlbeam.XBProjector} for API usage.
 *
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings("serial")
final class ProjectionInvocationHandler implements InvocationHandler, Serializable {

    private static class DefaultDOMAccessInvoker implements DOMAccess, Serializable {
        private final Node documentOrElement;
        private final Class<?> projectionInterface;
        private final XBProjector projector;

        /**
         * @param documentOrElement
         * @param projectionInterface
         */
        private DefaultDOMAccessInvoker(final Node documentOrElement, final Class<?> projectionInterface, final XBProjector projector) {
            this.documentOrElement = documentOrElement;
            this.projectionInterface = projectionInterface;
            this.projector = projector;
        }

        @Override
        public Class<?> getProjectionInterface() {
            return projectionInterface;
        }

        @Override
        public Node getDOMNode() {
            return documentOrElement;
        }

        @Override
        public Document getDOMOwnerDocument() {
            return DOMHelper.getOwnerDocumentFor(documentOrElement);
        }

        @Override
        public Element getDOMBaseElement() {
            if (Node.DOCUMENT_NODE == documentOrElement.getNodeType()) {
                return ((Document) documentOrElement).getDocumentElement();
            }
            assert Node.ELEMENT_NODE == documentOrElement.getNodeType();
            return (Element) documentOrElement;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof DOMAccess)) {
                return false;
            }
            DOMAccess op = (DOMAccess) o;
            if (!projectionInterface.equals(op.getProjectionInterface())) {
                return false;
            }
            // Unfortunately Node.isEqualNode() is implementation specific and does
            // not need to match our hashCode implementation.
            // So we define our own node equality.
            return DOMHelper.nodesAreEqual(documentOrElement, op.getDOMNode());
        }

        @Override
        public int hashCode() {
            return (31 * projectionInterface.hashCode()) + (27 * DOMHelper.nodeHashCode(documentOrElement));
        }

        @Override
        public String asString() {
            try {
                final StringWriter writer = new StringWriter();
                projector.config().createTransformer().transform(new DOMSource(getDOMNode()), new StreamResult(writer));
                final String output = writer.getBuffer().toString();
                return output;
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final static class DefaultObjectInvoker extends DefaultDOMAccessInvoker {
        private DefaultObjectInvoker(final Class<?> projectionInterface, final Node documentOrElement, final XBProjector projector) {
            super(documentOrElement, projectionInterface, projector);
        }

        @Override
        public String toString() {
            final String typeDesc = getDOMNode().getNodeType() == Node.DOCUMENT_NODE ? "document '" + getDOMNode().getBaseURI() + "'" : "element " + "'" + getDOMNode().getNodeName() + "[" + Integer.toString(getDOMNode().hashCode(), 16) + "]'";
            return "Projection [" + getProjectionInterface().getName() + "]" + " to " + typeDesc;
        }
    }

    private final static class XMLRenderingObjectInvoker extends DefaultDOMAccessInvoker {
        private XMLRenderingObjectInvoker(final Class<?> projectionInterface, final Node documentOrElement, final XBProjector projector) {
            super(documentOrElement, projectionInterface, projector);
        }

        @Override
        public String toString() {
            return super.asString();
        }
    }

    private Map<MethodSignature, InvocationHandler> getDefaultInvokers(final Object defaultInvokerObject) {
        final ReflectionInvoker reflectionInvoker = new ReflectionInvoker(defaultInvokerObject);
        final Map<MethodSignature, InvocationHandler> invokers = new HashMap<MethodSignature, InvocationHandler>();
        for (Method m : DOMAccess.class.getMethods()) {
            invokers.put(MethodSignature.forMethod(m), reflectionInvoker);
        }

        invokers.put(MethodSignature.forVoidMethod("toString"), reflectionInvoker);
        invokers.put(MethodSignature.forSingleParam("equals", Object.class), reflectionInvoker);
        invokers.put(MethodSignature.forVoidMethod("hashCode"), reflectionInvoker);
        return invokers;//Collections.unmodifiableMap(invokers);
    }

    private static class ReflectionInvoker implements InvocationHandler, Serializable {
        protected final Object obj;

        ReflectionInvoker(final Object obj) {
            this.obj = obj;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
                return method.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw e.getCause() == null ? e : e.getCause();
            }
        }
    }

    private class MixinInvoker extends ReflectionInvoker {
        MixinInvoker(final Object obj) {
            super(obj);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            injectMeAttribute((InternalProjection) proxy, obj);
            return super.invoke(proxy, method, args);
        }
    }

    private static abstract class ProjectionMethodInvocationHandler implements InvocationHandler, Serializable {
        private final Method method;
        protected final String annotationValue;
        private final Externalizer externalizer;

        ProjectionMethodInvocationHandler(final Method method, final String annotationValue, final Externalizer externalizer) {
            this.method = method;
            this.annotationValue = annotationValue;
            this.externalizer = externalizer;
        }

        protected String resolveXPath(final Object[] args) {
            return ProjectionInvocationHandler.applyParams(externalizer.resolveXPath(annotationValue, method, args), method, args);
        }

    }

    private static abstract class XPathInvocationHandler extends ProjectionMethodInvocationHandler {
        XPathInvocationHandler(final Method method, final String annotationValue, final Externalizer externalizer) {
            super(method, annotationValue, externalizer);
        }

        /**
         * Determine a methods return value that does not depend on the methods execution. Possible
         * values are void or the proxy itself (would be "this").
         *
         * @param method
         * @return
         */
        protected Object getProxyReturnValueForMethod(final Object proxy, final Method method, final Integer numberOfChanges) {
            if (!ReflectionHelper.hasReturnType(method)) {
                return null;
            }
            if (method.getReturnType().equals(method.getDeclaringClass())) {
                return proxy;
            }
            if ((numberOfChanges != null) && (method.getReturnType().isAssignableFrom(Integer.class) || method.getReturnType().isAssignableFrom(int.class))) {
                return numberOfChanges;
            }
            throw new IllegalArgumentException("Method " + method + " has illegal return type \"" + method.getReturnType() + "\". I don't know what to return. I expected void or " + method.getDeclaringClass().getSimpleName());
        }
    }

    private class ReadInvocationHandler extends XPathInvocationHandler {
        ReadInvocationHandler(final Method method, final String annotationValue, final Externalizer externalizer) {
            super(method, annotationValue, externalizer);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String resolvedXpath = applyParams(resolveXPath(args), method, args);
            final Node node = getNodeForMethod(method, args);
            final Document document = DOMHelper.getOwnerDocumentFor(node);
            final XPath xPath = projector.config().createXPath(document);
            // Automatic propagation of parameters as XPath variables
            // disabled so far...
//            try {
//            if (ReflectionHelper.mayProvideParameterNames()) {
//                xPath.setXPathVariableResolver(new MethodParamVariableResolver(method,args,xPath.getXPathVariableResolver()));
//            }
            final DuplexExpression duplexExpression = new DuplexXPathParser().compile(resolvedXpath);
            final ExpressionType expressionType = duplexExpression.getExpressionType();
            final XPathExpression expression = xPath.compile(resolvedXpath);

            final boolean wrappedInOptional = ReflectionHelper.isOptional(method.getGenericReturnType());
            final Class<?> returnType = wrappedInOptional ? ReflectionHelper.getParameterType(method.getGenericReturnType()) : method.getReturnType();
            if (projector.config().getTypeConverter().isConvertable(returnType)) {
                String data;
                if (expressionType.isMustEvalAsString()) {
                    data = (String) expression.evaluate(node, XPathConstants.STRING);
                } else {
                    Node dataNode = (Node) expression.evaluate(node, XPathConstants.NODE);
                    data = dataNode == null ? null : dataNode.getTextContent();
                }
                if ((data == null) && (absentIsEmpty)) {
                    data = "";
                }

                try {
                    final Object result = projector.config().getTypeConverter().convertTo(returnType, data);
                    return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(e.getMessage() + " XPath was:" + resolvedXpath);
                }
            }
            if (Node.class.isAssignableFrom(returnType)) {
                // Try to evaluate as node
                // if evaluated type does not match return type, ClassCastException will follow
                final Object result = expression.evaluate(node, XPathConstants.NODE);
                return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
            }
            if (List.class.equals(returnType)) {
                final Object result = evaluateAsList(expression, node, method);
                return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
            }
            if (returnType.isArray()) {
                List<?> list = evaluateAsList(expression, node, method);
                return list.toArray((Object[]) java.lang.reflect.Array.newInstance(returnType.getComponentType(), list.size()));
            }
            if (returnType.isInterface()) {
                Node newNode = (Node) expression.evaluate(node, XPathConstants.NODE);
                if (newNode == null) {
                    return wrappedInOptional ? ReflectionHelper.createOptional(null) : null;
                }
                InternalProjection subprojection = (InternalProjection) projector.projectDOMNode(newNode, returnType);
                return wrappedInOptional ? ReflectionHelper.createOptional(subprojection) : subprojection;
            }
            throw new IllegalArgumentException("Return type " + returnType + " of method " + method + " is not supported. Please change to an projection interface, a List, an Array or one of current type converters types:" + projector.config().getTypeConverter());
            // Automatic propagation of parameters as XPath variables
            // disabled so far...
//            } finally {
//                xPath.reset();
//            }

        }
    }

    private class UpdateInvocationHandler extends XPathInvocationHandler {

        /**
         * @param m
         * @param value
         * @param externalizer
         */
        public UpdateInvocationHandler(final Method m, final String value, final Externalizer externalizer) {
            super(m, value, externalizer);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String resolvedXpath = applyParams(resolveXPath(args), method, args);
            if (!ReflectionHelper.hasParameters(method)) {
                throw new IllegalArgumentException("Method " + method + " was invoked as updater but has no parameter. Please add a parameter so this method could actually change the DOM.");
            }
            if (method.getAnnotation(XBDocURL.class) != null) {
                throw new IllegalArgumentException("Method " + method + " was invoked as updater but has a @" + XBDocURL.class.getSimpleName() + " annotation. Defining updaters on external projections is not valid because there is no DOM attached.");
            }
            final Node node = getNodeForMethod(method, args);
            final Document document = DOMHelper.getOwnerDocumentFor(node);
            final XPath xPath = projector.config().createXPath(document);
            final XPathExpression expression = xPath.compile(resolvedXpath);
            final int findIndexOfValue = findIndexOfValue(method);
            final Object valueToSet = args[findIndexOfValue];
            final Class<?> typeToSet = method.getParameterTypes()[findIndexOfValue];
            final boolean isMultiValue = isMultiValue(typeToSet);
            if (isMultiValue) {
                throw new IllegalArgumentException("Method " + method + " was invoked as updater but with multiple values. Update is possible for single values only. Consider using @XBWrite.");
            }
            NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
            final int count = nodes.getLength();
            for (int i = 0; i < count; ++i) {
                final Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                if (Node.ATTRIBUTE_NODE == n.getNodeType()) {
                    Element e = ((Attr) n).getOwnerElement();
                    if (e == null) {
                        continue;
                    }
                    DOMHelper.setOrRemoveAttribute(e, n.getNodeName(), valueToSet == null ? null : valueToSet.toString());
                    continue;
                }
                if (valueToSet instanceof Element) {
                    if (!(n instanceof Element)) {
                        throw new IllegalArgumentException("XPath for element update need to select elements only");
                    }
                    DOMHelper.replaceElement((Element) n, (Element) ((Element) valueToSet).cloneNode(true));
                    continue;
                }
                n.setTextContent(valueToSet == null ? null : valueToSet.toString());
            }
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
        }

    }

    private class DeleteInvocationHandler extends XPathInvocationHandler {

        /**
         * @param m
         * @param value
         * @param externalizer
         */
        public DeleteInvocationHandler(final Method m, final String value, final Externalizer externalizer) {
            super(m, value, externalizer);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String resolvedXpath = applyParams(resolveXPath(args), method, args);
            final Document document = DOMHelper.getOwnerDocumentFor(node);
            final XPath xPath = projector.config().createXPath(document);
//            try {
//                if (ReflectionHelper.mayProvideParameterNames()) {
//                    xPath.setXPathVariableResolver(new MethodParamVariableResolver(method, args, xPath.getXPathVariableResolver()));
//               }

            final XPathExpression expression = xPath.compile(resolvedXpath);
            NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
            int count = 0;
            for (int i = 0; i < nodes.getLength(); ++i) {
                if (Node.ATTRIBUTE_NODE == nodes.item(i).getNodeType()) {
                    Attr attr = (Attr) nodes.item(i);
                    attr.getOwnerElement().removeAttributeNode(attr);
                    ++count;
                    continue;
                }
                Node parentNode = nodes.item(i).getParentNode();
                if (parentNode == null) {
                    continue;
                }
                parentNode.removeChild(nodes.item(i));
                ++count;
            }
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
//            } finally {
//                xPath.reset();
//            }
        }

    }

    private class WriteInvocationHandler extends XPathInvocationHandler {

        /**
         * @param m
         * @param value
         * @param externalizer
         */
        public WriteInvocationHandler(final Method m, final String value, final Externalizer externalizer) {
            super(m, value, externalizer);
        }

        private Object handeRootElementReplacement(final Object proxy, final Method method, final Document document, final Object valueToSet) {
            int count = document.getDocumentElement() == null ? 0 : 1;
            if (valueToSet == null) {
                DOMHelper.setDocumentElement(document, null);
                return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
            }
            if (valueToSet instanceof Element) {
                Element clone = (Element) ((Element) valueToSet).cloneNode(true);
                document.adoptNode(clone);
                if (document.getDocumentElement() == null) {
                    document.appendChild(clone);
                    return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                }
                document.replaceChild(document.getDocumentElement(), clone);
                return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
            }
            if (!(valueToSet instanceof InternalProjection)) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter changing the document root element. Expected value type was a projection so I can determine a element name. But you provided a " + valueToSet);
            }
            InternalProjection projection = (InternalProjection) valueToSet;
            Element element = projection.getDOMBaseElement();
            assert element != null;
            DOMHelper.setDocumentElement(document, element);
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String resolvedXpath = applyParams(resolveXPath(args), method, args);
            if (!ReflectionHelper.hasParameters(method)) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter but has no parameter. Please add a parameter so this method could actually change the DOM.");
            }
            if (method.getAnnotation(XBDocURL.class) != null) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter but has a @" + XBDocURL.class.getSimpleName() + " annotation. Defining setters on external projections is not valid because there is no DOM attached.");
            }
            final String pathToElement = resolvedXpath.replaceAll("\\[@", "[attribute::").replaceAll("/?@.*", "").replaceAll("\\[attribute::", "[@");
            final Document document = DOMHelper.getOwnerDocumentFor(node);
            assert document != null;
            final int findIndexOfValue = findIndexOfValue(method);
            final Object valueToSet = args[findIndexOfValue];
            final boolean isMultiValue = isMultiValue(method.getParameterTypes()[findIndexOfValue]);

            // ROOT element update
            if ("/*".equals(resolvedXpath)) { // Setting a new root element.
                if (isMultiValue) {
                    throw new IllegalArgumentException("Method " + method + " was invoked as setter changing the document root element, but tries to set multiple values.");
                }
                return handeRootElementReplacement(proxy, method, document, valueToSet);
            }
            final boolean wildCardTarget = resolvedXpath.endsWith("/*");
            try {
                final DuplexExpression duplexExpression = wildCardTarget ? new DuplexXPathParser().compile(resolvedXpath.substring(0, resolvedXpath.length() - 2)) : new DuplexXPathParser().compile(resolvedXpath);
                if (duplexExpression.getExpressionType().isMustEvalAsString()) {
                    throw new XBPathException("Unwriteable xpath selector used ", method, resolvedXpath);
                }
                // MULTIVALUE
                if (isMultiValue) {
                    if (duplexExpression.getExpressionType().equals(ExpressionType.ATTRIBUTE)) {
                        throw new IllegalArgumentException("Method " + method + " was invoked as setter changing some attribute, but was declared to set multiple values. I can not create multiple attributes for one path.");
                    }
                    final Collection<?> collection2Set = valueToSet == null ? Collections.emptyList() : (valueToSet.getClass().isArray()) ? ReflectionHelper.array2ObjectList(valueToSet) : (Collection<?>) valueToSet;
                    if (wildCardTarget) {
                        // TODO: check support of ParameterizedType e.g. Supplier
                        final Element parentElement = (Element) duplexExpression.ensureExistence(node);
                        DOMHelper.removeAllChildren(parentElement);
                        int count = 0;
                        for (Object o : collection2Set) {
                            if (o == null) {
                                continue;
                            }
                            ++count;
                            if (o instanceof Node) {
                                DOMHelper.appendClone(parentElement, (Node) o);
                                continue;
                            }
                            if (o instanceof InternalProjection) {
                                DOMHelper.appendClone(parentElement, ((InternalProjection) o).getDOMBaseElement());
                                continue;
                            }
                            throw new XBPathException("When using a wildcard target, the type to set must be a DOM Node or another projection. Otherwise I can not determine the element name.", method, resolvedXpath);
                        }
                        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
                    }
                    final Element parentElement = duplexExpression.ensureParentExistence(node);
                    duplexExpression.deleteAllMatchingChildren(parentElement);
                    int count = applyCollectionSetOnElement(collection2Set, parentElement, duplexExpression);
                    return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
                }

                // ATTRIBUTES
                if (duplexExpression.getExpressionType().equals(ExpressionType.ATTRIBUTE)) {
                    if (wildCardTarget) {
                        //TODO: This may never happen, right?
                        throw new XBPathException("Wildcards are not allowed when writing to an attribute. I need to know to which Element I should set the attribute", method, resolvedXpath);
                    }
                    Attr attribute = (Attr) duplexExpression.ensureExistence(node);
                    if (valueToSet == null) {
                        attribute.getOwnerElement().removeAttributeNode(attribute);
                        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                    }

                    DOMHelper.setStringValue(attribute, valueToSet.toString());
                    return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                }

                if ((valueToSet instanceof Node) || (valueToSet instanceof InternalProjection)) {
                    if (valueToSet instanceof Attr) {
                        if (wildCardTarget) {
                            throw new XBPathException("Wildcards are not allowed when writing an attribute. I need to know to which Element I should set the attribute", method, resolvedXpath);
                        }
                        Element parentNode = duplexExpression.ensureParentExistence(node);
                        if (((Attr) valueToSet).getNamespaceURI() != null) {
                            parentNode.setAttributeNodeNS((Attr) valueToSet);
                            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                        }
                        parentNode.setAttributeNode((Attr) valueToSet);
                        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                    }
                    final Element newNodeOrigin = valueToSet instanceof InternalProjection ? ((InternalProjection) valueToSet).getDOMBaseElement() : (Element) valueToSet;
                    final Element newNode = (Element) newNodeOrigin.cloneNode(true);
                    DOMHelper.ensureOwnership(document, newNode);
                    if (wildCardTarget) {
                        Element parentElement = (Element) duplexExpression.ensureExistence(node);
                        DOMHelper.removeAllChildren(parentElement);
                        parentElement.appendChild(newNode);
                        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                    }
                    Element previousElement = (Element) duplexExpression.ensureExistence(node);

                    DOMHelper.replaceElement(previousElement, newNode);
                    return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
                }

                final Element elementToChange = (Element) duplexExpression.ensureExistence(node);
                if (valueToSet == null) {
                    //TODO: This should depend on the parameter type?
                    // If param type == String, no structural change might be expected.
                    DOMHelper.removeAllChildren(elementToChange);
                } else {
                    elementToChange.setTextContent(valueToSet.toString());
                }
                return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
            } catch (XBPathParsingException e) {
                throw new XBPathException(e, method, pathToElement);
            }
        }

    }

    private static final Pattern DOUBLE_LBRACES = Pattern.compile("{{", Pattern.LITERAL);
    private static final Pattern DOUBLE_RBRACES = Pattern.compile("}}", Pattern.LITERAL);
    private static final InvocationHandler DEFAULT_METHOD_INVOCATION_HANDLER = new InvocationHandler() {

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return ReflectionHelper.invokeDefaultMethod(method, args, proxy);
        }
    };
    private final Node node;
    private final Class<?> projectionInterface;
    private final XBProjector projector;

    // treat absent nodes as empty nodes
    private final boolean absentIsEmpty;

    private final Map<MethodSignature, InvocationHandler> handlers = new HashMap<MethodSignature, InvocationHandler>();
    private final Map<MethodSignature, InvocationHandler> mixinHandlers = new HashMap<MethodSignature, InvocationHandler>();

    ProjectionInvocationHandler(final XBProjector projector, final Node node, final Class<?> projectionInterface, final Map<Class<?>, Object> mixins, final boolean toStringRendersXML, final boolean absentIsEmpty) {
        this.projector = projector;
        this.node = node;
        this.projectionInterface = projectionInterface;
        this.absentIsEmpty = absentIsEmpty;
        final Object defaultInvokerObject = toStringRendersXML ? new XMLRenderingObjectInvoker(projectionInterface, node, projector) : new DefaultObjectInvoker(projectionInterface, node, projector);

        final Map<MethodSignature, InvocationHandler> defaultInvocationHandlers = getDefaultInvokers(defaultInvokerObject);

        for (Entry<Class<?>, Object> e : mixins.entrySet()) {
            for (Method m : e.getKey().getMethods()) {
                mixinHandlers.put(MethodSignature.forMethod(m), new MixinInvoker(e.getValue()));
            }
        }

        handlers.putAll(defaultInvocationHandlers);

        List<Class<?>> allSuperInterfaces = ReflectionHelper.findAllSuperInterfaces(projectionInterface);
        for (Class<?> i7e : allSuperInterfaces) {
            for (Method m : i7e.getDeclaredMethods()) {
                final MethodSignature methodSignature = MethodSignature.forMethod(m);
                if (ReflectionHelper.isDefaultMethod(m)) {
                    handlers.put(methodSignature, DEFAULT_METHOD_INVOCATION_HANDLER);
                    continue;
                }
                if (defaultInvocationHandlers.containsKey(methodSignature)) {
                    continue;
                }
                {
                    final XBRead readAnnotation = m.getAnnotation(XBRead.class);
                    if (readAnnotation != null) {
                        handlers.put(methodSignature, new ReadInvocationHandler(m, readAnnotation.value(), projector.config().getExternalizer()));
                        continue;
                    }
                }
                {
                    final XBUpdate updateAnnotation = m.getAnnotation(XBUpdate.class);
                    if (updateAnnotation != null) {
                        handlers.put(methodSignature, new UpdateInvocationHandler(m, updateAnnotation.value(), projector.config().getExternalizer()));
                        continue;
                    }
                }
                {
                    final XBWrite writeAnnotation = m.getAnnotation(XBWrite.class);
                    if (writeAnnotation != null) {
                        handlers.put(methodSignature, new WriteInvocationHandler(m, writeAnnotation.value(), projector.config().getExternalizer()));
                        continue;
                    }
                }
                {
                    final XBDelete delAnnotation = m.getAnnotation(XBDelete.class);
                    if (delAnnotation != null) {
                        handlers.put(methodSignature, new DeleteInvocationHandler(m, delAnnotation.value(), projector.config().getExternalizer()));
                        continue;
                    }
                }

                if (mixinHandlers.containsKey(methodSignature)) {
                    continue;
                }

                throw new IllegalArgumentException("I don't known how to handle method " + m + ". Did you forget to add a XB*-annotation or to register a mixin?");
            }
        }

    }

    /**
     * @param typeToSet
     * @param collection
     * @param parentElement
     * @param duplexExpression
     * @param elementSelector
     */
    private int applyCollectionSetOnElement(final Collection<?> collection, final Element parentElement, final DuplexExpression duplexExpression) {
        for (Object o : collection) {
            if (o == null) {
                continue;
            }
            if (!isStructureChangingValue(o)) {
                Node newElement = duplexExpression.createChildWithPredicate(parentElement);
                newElement.setTextContent(o.toString());
                continue;
            }
            Element elementToAdd;

            if (o instanceof Node) {
                final Node n = (Node) o;
                elementToAdd = (Element) (Node.DOCUMENT_NODE != n.getNodeType() ? n : n.getOwnerDocument() == null ? null : n.getOwnerDocument().getDocumentElement());
            } else {
                final InternalProjection p = (InternalProjection) o;
                elementToAdd = p.getDOMBaseElement();
            }
            if (elementToAdd == null) {
                continue;
            }

            Element clone = (Element) elementToAdd.cloneNode(true);
            Element childWithPredicate = (Element) duplexExpression.createChildWithPredicate(parentElement);
            final String elementName = childWithPredicate.getNodeName();
            if (!elementName.equals(clone.getNodeName())) {
                if (!"*".equals(elementName)) {
                    clone = DOMHelper.renameElement(clone, elementName);
                }
            }
            DOMHelper.replaceElement(childWithPredicate, clone);

        }
        return collection.size();
    }

    /**
     * @param o
     * @return
     */
    private boolean isStructureChangingValue(final Object o) {
        return (o instanceof InternalProjection) || (o instanceof Node);
    }

    private List<?> evaluateAsList(final XPathExpression expression, final Node node, final Method method) throws XPathExpressionException {
        final NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        final List<Object> linkedList = new LinkedList<Object>();
        final Class<?> targetType = findTargetComponentType(method);
        final TypeConverter typeConverter = projector.config().getTypeConverter();
        if (typeConverter.isConvertable(targetType)) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(typeConverter.convertTo(targetType, nodes.item(i).getTextContent()));
            }
            return linkedList;
        }
        if (Node.class.equals(targetType)) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(nodes.item(i));
            }
            return linkedList;
        }
        if (targetType.isInterface()) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                InternalProjection subprojection = (InternalProjection) projector.projectDOMNode(nodes.item(i), targetType);
                linkedList.add(subprojection);
            }
            return linkedList;
        }
        throw new IllegalArgumentException("Return type " + targetType + " is not valid for list or array component type returning from method " + method + " using the current type converter:" + projector.config().getTypeConverter()
                + ". Please change the return type to a sub projection or add a conversion to the type converter.");
    }

    /**
     * Setter projection methods may have multiple parameters. One of them may be annotated with
     * {@link XBValue} to select it as value to be set.
     *
     * @param method
     * @return index of fist parameter annotated with {@link XBValue} annotation.
     */
    private int findIndexOfValue(final Method method) {
        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation a : annotations) {
                if (XBValue.class.equals(a.annotationType())) {
                    return index;
                }
            }
            ++index;
        }
        return 0; // If no attribute is annotated, the first one is taken.
    }

    /**
     * When reading collections, determine the collection component type.
     *
     * @param method
     * @return
     */
    private Class<?> findTargetComponentType(final Method method) {
        if (method.getReturnType().isArray()) {
            return method.getReturnType().getComponentType();
        }
        assert List.class.equals(method.getReturnType());
        final Type type = method.getGenericReturnType();
        if (!(type instanceof ParameterizedType) || (((ParameterizedType) type).getActualTypeArguments() == null) || (((ParameterizedType) type).getActualTypeArguments().length < 1)) {
            throw new IllegalArgumentException("When using List as return type for method " + method + ", please specify a generic type for the List. Otherwise I do not know which type I should fill the List with.");
        }
        assert ((ParameterizedType) type).getActualTypeArguments().length == 1 : "";
        Type componentType = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (!(componentType instanceof Class)) {
            throw new IllegalArgumentException("I don't know how to instantiate the generic type for the return type of method " + method);
        }
        return (Class<?>) componentType;
    }

    private Node getNodeForMethod(final Method method, final Object[] args) throws SAXException, IOException, ParserConfigurationException {
        final XBDocURL docURL = method.getAnnotation(XBDocURL.class);
        if (docURL != null) {
            String uri = projector.config().getExternalizer().resolveURL(docURL.value(), method, args);
            final Map<String, String> requestParams = projector.io().filterRequestParamsFromParams(uri, args);
            uri = applyParams(uri, method, args);
            return DOMHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), uri, requestParams, projectionInterface);
        }
        return node;
    }

    /**
     * @param uri
     * @param method
     * @param args
     * @return a string with all place holders filled by given parameters
     */
    private static String applyParams(String string, final Method method, final Object[] args) {
        if (args != null) {
            int c = 0;
            for (String param : ReflectionHelper.getMethodParameterNames(method)) {
                if (args[c] == null) {
                    continue;
                }
                string = replaceAllIfNotQuoted(string, "{" + param + "}", args[c++].toString());
                //string = string.replaceAll("[^\\{]\\{" + Pattern.quote(param) + "\\}", "" + args[c++]);
            }
            for (c = 0; c < args.length; ++c) {
                if (args[c] == null) {
                    continue;
                }
                //string = string.replaceAll("[^\\{]\\{" + c + "\\}", "" + args[c]);
                string = replaceAllIfNotQuoted(string, "{" + c + "}", args[c].toString());
            }
        }
        string = DOUBLE_LBRACES.matcher(string).replaceAll("{");
        string = DOUBLE_RBRACES.matcher(string).replaceAll("}");
        return string;
    }

    /**
     * Replace all occurrences of pattern in string with replacement, but only if they are not
     * quoted out.
     *
     * @param string
     * @param pattern
     * @param object
     * @return replaced string
     */
    private static String replaceAllIfNotQuoted(final String string, final String pattern, String replacement) {
        replacement = Matcher.quoteReplacement(replacement);
        Pattern compile = Pattern.compile(pattern, Pattern.LITERAL);
        Matcher matcher = compile.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if ((matcher.start() > 1) && (Character.valueOf('{').equals(string.charAt(matcher.start() - 1)))) {
                continue;
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Find the "me" attribute (which is a replacement for "this") and inject the projection proxy
     * instance.
     *
     * @param me
     * @param target
     */
    private void injectMeAttribute(final InternalProjection me, final Object target) {
        //final Class<?> projectionInterface = me.getProjectionInterface();
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!isValidMeField(field, projectionInterface)) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(target, me);
                return;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Mixin " + target.getClass().getSimpleName() + " needs an attribute \"private " + projectionInterface.getSimpleName() + " me;\" to be able to access the projection.");
    }

    private boolean isValidMeField(final Field field, final Class<?> projInterface) {
        if (field == null) {
            return false;
        }
        if (!"me".equalsIgnoreCase(field.getName())) {
            return false;
        }
        if (DOMAccess.class.equals(field.getType())) {
            return true;
        }
        return field.getType().isAssignableFrom(projInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        unwrapArgs(method.getParameterTypes(), args);
        if (!mixinHandlers.isEmpty()) {
            MethodSignature methodSignature = MethodSignature.forMethod(method);
            if (mixinHandlers.containsKey(methodSignature)) {
                return mixinHandlers.get(methodSignature).invoke(proxy, method, args);
            }
        }

        final InvocationHandler invocationHandler = handlers.get(MethodSignature.forMethod(method));
        if (invocationHandler != null) {
            try {
                return invocationHandler.invoke(proxy, method, args);
            } catch (XPathExpressionException e) {
                throw new XBPathException(e, method, "??");
            }
        }

        throw new IllegalArgumentException("I don't known how to invoke method " + method + ". Did you forget to add a XB*-annotation or to register a mixin?");
    }

    /**
     * If parameter is instance of Callable or Supplier then resolve its value.
     *
     * @param args
     * @param args2
     */
    private void unwrapArgs(final Class<?>[] types, final Object[] args) {
        if (args == null) {
            return;
        }
        try {
            for (int i = 0; i < args.length; ++i) {
                args[i] = ReflectionHelper.unwrap(types[i], args[i]);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * @param typeToSet
     * @return
     */
    private boolean isMultiValue(final Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

}
