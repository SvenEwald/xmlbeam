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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector.IOBuilder;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBOverride;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBUpdate;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.evaluation.DefaultXPathEvaluator;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.util.IOHelper;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.MethodParamVariableResolver;
import org.xmlbeam.util.intern.Preprocessor;
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

    private Map<MethodSignature, InvocationHandler> getDefaultInvokers(final Object defaultInvokerObject) {
        final ReflectionInvoker reflectionInvoker = new ReflectionInvoker(defaultInvokerObject);
        final Map<MethodSignature, InvocationHandler> invokers = new HashMap<MethodSignature, InvocationHandler>();
        for (Method m : DOMAccess.class.getMethods()) {
            if (m.getAnnotation(XBWrite.class) == null) {
                invokers.put(MethodSignature.forMethod(m), reflectionInvoker);
            }
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

    private static class MixinInvoker extends ReflectionInvoker {
        private final Class<?> projectionInterface;

        MixinInvoker(final Object obj, final Class<?> projectionInterface) {
            super(obj);
            this.projectionInterface = projectionInterface;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            injectMeAttribute((DOMAccess) proxy, obj, projectionInterface);
            try {
                return super.invoke(proxy, method, args);
            } catch (InvocationTargetException e) {
                throw e.getCause() == null ? e : e.getCause();
            }
        }
    }

    private static abstract class ProjectionMethodInvocationHandler implements InvocationHandler, Serializable {

        private static final InvocationContext EMPTY_INVOCATION_CONTEXT = new InvocationContext(null, null, null, null, null, null, null);

        protected final Method method;
        protected final String annotationValue;
        protected final XBProjector projector;
        protected final Node node;
        private final String docAnnotationValue;
        private final boolean isVoidMethod;
        protected InvocationContext lastInvocationContext = EMPTY_INVOCATION_CONTEXT;
        protected final Map<String, Integer> methodParameterIndexes;

        ProjectionMethodInvocationHandler(final Node node, final Method method, final String annotationValue, final XBProjector projector) {
            this.method = method;
            this.annotationValue = annotationValue;
            this.projector = projector;
            this.node = node;
            final XBDocURL annotation = method.getAnnotation(XBDocURL.class);
            this.docAnnotationValue = annotation == null ? null : annotation.value();
            this.isVoidMethod = !ReflectionHelper.hasReturnType(method);
            methodParameterIndexes = ReflectionHelper.getMethodParameterIndexes(method);
        }

        protected Node getNodeForMethod(final Method method, final Object[] args) throws SAXException, IOException, ParserConfigurationException {
            if (docAnnotationValue != null) {
                String uri = projector.config().getExternalizer().resolveURL(docAnnotationValue, method, args);
                final Map<String, String> requestParams = ((IOBuilder) projector.io()).filterRequestParamsFromParams(uri, args);
                uri = Preprocessor.applyParams(uri, methodParameterIndexes, args);
                Class<?> callerClass = null;
                if (IOHelper.isResourceProtocol(uri)) {
                    callerClass = ReflectionHelper.getCallerClass(8);
                }
                return IOHelper.getDocumentFromURL(projector.config().createDocumentBuilder(), uri, requestParams, method.getDeclaringClass(), callerClass);
            }
            return node;
        }

        protected String resolveXPath(final Object[] args) {
            return Preprocessor.applyParams(projector.config().getExternalizer().resolveXPath(annotationValue, method, args), methodParameterIndexes, args);
        }

        /**
         * Determine a methods return value that does not depend on the methods execution. Possible
         * values are void or the proxy itself (would be "this").
         *
         * @param method
         * @return
         */
        protected Object getProxyReturnValueForMethod(final Object proxy, final Method method, final Integer numberOfChanges) {
            if (isVoidMethod) {
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

        abstract protected Object invokeProjection(final String resolvedXpath, final Object proxy, final Object[] args) throws Throwable;

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String xPath = resolveXPath(args);
            final String resolvedXpath = Preprocessor.applyParams(xPath, methodParameterIndexes, args);
            return invokeProjection(resolvedXpath, proxy, args);
        }

    }

    private static abstract class XPathInvocationHandler extends ProjectionMethodInvocationHandler {

        private XPathInvocationHandler(final Node node, final Method method, final String annotationValue, final XBProjector projector) {
            super(node, method, annotationValue, projector);
        }

        @Override
        final protected Object invokeProjection(final String resolvedXpath, final Object proxy, final Object[] args) throws Throwable {
            final XPath xPath = projector.config().createXPath(DOMHelper.getOwnerDocumentFor(node));

            if (!lastInvocationContext.isStillValid(resolvedXpath)) {
                final DuplexExpression duplexExpression = new DuplexXPathParser().compile(resolvedXpath);
                String strippedXPath = duplexExpression.getExpressionAsStringWithoutFormatPatterns();
                MethodParamVariableResolver resolver = null;
                if (duplexExpression.isUsingVariables()) {
                    XPathVariableResolver peviousResolver = xPath.getXPathVariableResolver();
                    resolver = new MethodParamVariableResolver(method, args, duplexExpression, projector.config().getStringRenderer(), peviousResolver);
                    xPath.setXPathVariableResolver(resolver);

                }
                final XPathExpression xPathExpression = xPath.compile(strippedXPath);
                final Class<?> targetComponentType = findTargetComponentType(method);
                lastInvocationContext = new InvocationContext(resolvedXpath, xPath, xPathExpression, duplexExpression, resolver, targetComponentType, projector);
            }
            lastInvocationContext.updateMethodArgs(args);
            return invokeXpathProjection(lastInvocationContext, proxy, args);
        }

        abstract protected Object invokeXpathProjection(final InvocationContext invocationContext, final Object proxy, final Object[] args) throws Throwable;
    }

    private static class ReadInvocationHandler extends XPathInvocationHandler {
        private final boolean absentIsEmpty;
        private final boolean wrappedInOptional;
        private final Class<?> returnType;
        private final Class<?> exceptionType;
        private final boolean isConvertable;
        private final boolean isReturnAsNode;
        private final boolean isEvaluateAsList;
        private final boolean isEvaluateAsArray;
        private final boolean isEvaluateAsSubProjection;
        private final boolean isThrowIfAbsent;

        ReadInvocationHandler(final Node node, final Method method, final String annotationValue, final XBProjector projector, final boolean absentIsEmpty) {
            super(node, method, annotationValue, projector);
            wrappedInOptional = ReflectionHelper.isOptional(method.getGenericReturnType());
            returnType = wrappedInOptional ? ReflectionHelper.getParameterType(method.getGenericReturnType()) : method.getReturnType();
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            exceptionType = exceptionTypes.length > 0 ? exceptionTypes[0] : null;
            this.isConvertable = projector.config().getTypeConverter().isConvertable(returnType);
            this.isReturnAsNode = Node.class.isAssignableFrom(returnType);
            this.isEvaluateAsList = List.class.equals(returnType);
            this.isEvaluateAsArray = returnType.isArray();
            if (wrappedInOptional && (isEvaluateAsArray || isEvaluateAsList)) {
                throw new IllegalArgumentException("Method " + method + " must not declare an optional return type of list or array. Lists and arrays may be empty but will never be null.");
            }
            this.isEvaluateAsSubProjection = returnType.isInterface();
            this.isThrowIfAbsent = exceptionType != null;

            // Throwing exception overrides empty default value.
            this.absentIsEmpty = absentIsEmpty && (!isThrowIfAbsent);
        }

        @Override
        public Object invokeXpathProjection(final InvocationContext invocationContext, final Object proxy, final Object[] args) throws Throwable {
            final Object result = invokeReadProjection(invocationContext, proxy, args);
            if ((result == null) && (isThrowIfAbsent)) {
                XBDataNotFoundException dataNotFoundException = new XBDataNotFoundException(invocationContext.getResolvedXPath());
                if (XBDataNotFoundException.class.equals(exceptionType)) {
                    throw dataNotFoundException;
                }
                ReflectionHelper.throwThrowable(exceptionType, args, dataNotFoundException);
            }
            return result;
        }

        private Object invokeReadProjection(final InvocationContext invocationContext, final Object proxy, final Object[] args) throws Throwable {
            final Node node = getNodeForMethod(method, args);
            final ExpressionType expressionType = invocationContext.getDuplexExpression().getExpressionType();
            final XPathExpression expression = invocationContext.getxPathExpression();

            if (isConvertable) {
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
                    final Object result = projector.config().getTypeConverter().convertTo(returnType, data, invocationContext.getExpressionFormatPattern());
                    return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(e.getMessage() + " XPath was:" + invocationContext.getResolvedXPath());
                }
            }
            if (isReturnAsNode) {
                // Try to evaluate as node
                // if evaluated type does not match return type, ClassCastException will follow
                final Object result = expression.evaluate(node, XPathConstants.NODE);
                return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
            }
            if (isEvaluateAsList) {
                assert !wrappedInOptional : "Projection methods returning list will never return null";
                final Object result = DefaultXPathEvaluator.evaluateAsList(expression, node, method, invocationContext);
                return result;
            }
            if (isEvaluateAsArray) {
                assert !wrappedInOptional : "Projection methods returning array will never return null";
                final List<?> list = DefaultXPathEvaluator.evaluateAsList(expression, node, method, invocationContext);
                return list.toArray((Object[]) java.lang.reflect.Array.newInstance(returnType.getComponentType(), list.size()));
            }
            if (isEvaluateAsSubProjection) {
                final Node newNode = (Node) expression.evaluate(node, XPathConstants.NODE);
                if (newNode == null) {
                    return wrappedInOptional ? ReflectionHelper.createOptional(null) : null;
                }
                final DOMAccess subprojection = (DOMAccess) projector.projectDOMNode(newNode, returnType);
                return wrappedInOptional ? ReflectionHelper.createOptional(subprojection) : subprojection;
            }
            throw new IllegalArgumentException("Return type " + returnType + " of method " + method + " is not supported. Please change to an projection interface, a List, an Array or one of current type converters types:" + projector.config().getTypeConverter());
        }
    }

    private static class UpdateInvocationHandler extends XPathInvocationHandler {

        private final int findIndexOfValue;

        /**
         * @param node
         * @param m
         * @param value
         * @param projector
         */
        public UpdateInvocationHandler(final Node node, final Method m, final String value, final XBProjector projector) {
            super(node, m, value, projector);
            findIndexOfValue = findIndexOfValue(m);
            if (isMultiValue(m.getParameterTypes()[findIndexOfValue])) {
                throw new IllegalArgumentException("Method " + m + " was declated as updater but with multiple values. Update is possible for single values only. Consider using @XBWrite.");
            }
        }

        @Override
        public Object invokeXpathProjection(final InvocationContext invocationContext, final Object proxy, final Object[] args) throws Throwable {
            assert ReflectionHelper.hasParameters(method);
            final Node node = getNodeForMethod(method, args);
//            final Document document = DOMHelper.getOwnerDocumentFor(node);
//            final XPath xPath = projector.config().createXPath(document);
            final XPathExpression expression = invocationContext.getxPathExpression();

            final Object valueToSet = args[findIndexOfValue];
            //      final Class<?> typeToSet = method.getParameterTypes()[findIndexOfValue];
            //     final boolean isMultiValue = isMultiValue(typeToSet);
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

    private static class DeleteInvocationHandler extends XPathInvocationHandler {

        /**
         * @param node
         * @param m
         * @param value
         * @param projector
         */
        public DeleteInvocationHandler(final Node node, final Method m, final String value, final XBProjector projector) {
            super(node, m, value, projector);
        }

        @Override
        public Object invokeXpathProjection(final InvocationContext invocationContext, final Object proxy, final Object[] args) throws Throwable {

//            try {
//                if (ReflectionHelper.mayProvideParameterNames()) {
//                    xPath.setXPathVariableResolver(new MethodParamVariableResolver(method, args, xPath.getXPathVariableResolver()));
//               }

            final XPathExpression expression = invocationContext.getxPathExpression();
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

    private static class WriteInvocationHandler extends ProjectionMethodInvocationHandler {

        private final int findIndexOfValue;

        /**
         * @param node
         * @param m
         * @param value
         * @param projector
         */
        public WriteInvocationHandler(final Node node, final Method m, final String value, final XBProjector projector) {
            super(node, m, value, projector);
            findIndexOfValue = findIndexOfValue(m);
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
            if (!(valueToSet instanceof DOMAccess)) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter changing the document root element. Expected value type was a projection so I can determine a element name. But you provided a " + valueToSet);
            }
            DOMAccess projection = (DOMAccess) valueToSet;
            Element element = projection.getDOMBaseElement();
            assert element != null;
            DOMHelper.setDocumentElement(document, element);
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
        }

        /**
         * @param typeToSet
         * @param iterable
         * @param parentElement
         * @param duplexExpression
         * @param elementSelector
         */
        private int applyIterableSetOnElement(final Iterable<?> iterable, final Element parentElement, final DuplexExpression duplexExpression) {
            int changeCount=0;
            for (Object o : iterable) {
                if (o == null) {
                    continue;
                }
                if (!isStructureChangingValue(o)) {
                    final Node newElement = duplexExpression.createChildWithPredicate(parentElement);
                    final String asString = projector.config().getStringRenderer().render(o.getClass(), o, duplexExpression.getExpressionFormatPattern());
                    newElement.setTextContent(asString);
                    ++changeCount;
                    continue;
                }
                Element elementToAdd;

                if (o instanceof Node) {
                    final Node n = (Node) o;
                    elementToAdd = (Element) (Node.DOCUMENT_NODE != n.getNodeType() ? n : n.getOwnerDocument() == null ? null : n.getOwnerDocument().getDocumentElement());
                } else {
                    final DOMAccess p = (DOMAccess) o;
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
                ++changeCount;
            }
            return changeCount;
        }

        @Override
        public Object invokeProjection(final String resolvedXpath, final Object proxy, final Object[] args) throws Throwable {
            //   final String pathToElement = resolvedXpath.replaceAll("\\[@", "[attribute::").replaceAll("/?@.*", "").replaceAll("\\[attribute::", "[@");
            lastInvocationContext.updateMethodArgs(args);
            final Document document = DOMHelper.getOwnerDocumentFor(node);
            assert document != null;
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
                if (!lastInvocationContext.isStillValid(resolvedXpath)) {
                    final DuplexExpression duplexExpression = wildCardTarget ? new DuplexXPathParser().compile(resolvedXpath.substring(0, resolvedXpath.length() - 2)) : new DuplexXPathParser().compile(resolvedXpath);
                    MethodParamVariableResolver resolver = null;
                    if (duplexExpression.isUsingVariables()) {
                        resolver = new MethodParamVariableResolver(method, args, duplexExpression, projector.config().getStringRenderer(), null);
                        duplexExpression.setXPathVariableResolver(resolver);
                    }
                    Class<?> targetComponentType = findTargetComponentType(method);
                    lastInvocationContext = new InvocationContext(resolvedXpath, null, null, duplexExpression, resolver, targetComponentType, projector);
                }
                final DuplexExpression duplexExpression = lastInvocationContext.getDuplexExpression();
                if (duplexExpression.getExpressionType().isMustEvalAsString()) {
                    throw new XBPathException("Unwriteable xpath selector used ", method, resolvedXpath);
                }
                // MULTIVALUE
                if (isMultiValue) {
                    if (duplexExpression.getExpressionType().equals(ExpressionType.ATTRIBUTE)) {
                        throw new IllegalArgumentException("Method " + method + " was invoked as setter changing some attribute, but was declared to set multiple values. I can not create multiple attributes for one path.");
                    }
                    final Iterable<?> iterable2Set = valueToSet == null ? Collections.emptyList() : (valueToSet.getClass().isArray()) ? ReflectionHelper.array2ObjectList(valueToSet) : (Iterable<?>) valueToSet;
                    if (wildCardTarget) {
                        // TODO: check support of ParameterizedType e.g. Supplier
                        final Element parentElement = (Element) duplexExpression.ensureExistence(node);
                        DOMHelper.removeAllChildren(parentElement);
                        int count = 0;
                        for (Object o : iterable2Set) {
                            if (o == null) {
                                continue;
                            }
                            ++count;
                            if (o instanceof Node) {
                                DOMHelper.appendClone(parentElement, (Node) o);
                                continue;
                            }
                            if (o instanceof DOMAccess) {
                                DOMHelper.appendClone(parentElement, ((DOMAccess) o).getDOMBaseElement());
                                continue;
                            }
                            throw new XBPathException("When using a wildcard target, the type to set must be a DOM Node or another projection. Otherwise I can not determine the element name.", method, resolvedXpath);
                        }
                        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
                    }
                    final Element parentElement = duplexExpression.ensureParentExistence(node);
                    duplexExpression.deleteAllMatchingChildren(parentElement);
                    int count = applyIterableSetOnElement(iterable2Set, parentElement, duplexExpression);
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

                if ((valueToSet instanceof Node) || (valueToSet instanceof DOMAccess)) {
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
                    final Element newNodeOrigin = valueToSet instanceof DOMAccess ? ((DOMAccess) valueToSet).getDOMBaseElement() : (Element) valueToSet;
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
                    final String asString = projector.config().getStringRenderer().render(valueToSet.getClass(), valueToSet, duplexExpression.getExpressionFormatPattern());
                    elementToChange.setTextContent(asString);
                }
                return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
            } catch (XBPathParsingException e) {
                throw new XBPathException(e, method, resolvedXpath);
            }
        }
    }

    private static final InvocationHandler DEFAULT_METHOD_INVOCATION_HANDLER = new InvocationHandler() {

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return ReflectionHelper.invokeDefaultMethod(method, args, proxy);
        }
    };

    private static final class OverrideByDefaultMethodInvocationHandler implements InvocationHandler {
        private final Method defaultMethod;

        OverrideByDefaultMethodInvocationHandler(final Method defaultMethod) {
            this.defaultMethod = defaultMethod;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return DEFAULT_METHOD_INVOCATION_HANDLER.invoke(proxy, defaultMethod, args);
        }
    }

    private final Map<MethodSignature, InvocationHandler> handlers = new HashMap<MethodSignature, InvocationHandler>();
    private final Map<MethodSignature, InvocationHandler> mixinHandlers = new HashMap<MethodSignature, InvocationHandler>();

    ProjectionInvocationHandler(final XBProjector projector, final Node node, final Class<?> projectionInterface, final Map<Class<?>, Object> mixins, final boolean toStringRendersXML, final boolean absentIsEmpty) {
        final Object defaultInvokerObject = DefaultDOMAccessInvoker.create(projectionInterface, node, projector, toStringRendersXML);
        final Map<MethodSignature, InvocationHandler> defaultInvocationHandlers = getDefaultInvokers(defaultInvokerObject);

        for (Entry<Class<?>, Object> e : mixins.entrySet()) {
            for (Method m : e.getKey().getMethods()) {
                mixinHandlers.put(MethodSignature.forMethod(m), new MixinInvoker(e.getValue(), projectionInterface));
            }
        }

        handlers.putAll(defaultInvocationHandlers);

        List<Class<?>> allSuperInterfaces = ReflectionHelper.findAllSuperInterfaces(projectionInterface);
        for (Class<?> i7e : allSuperInterfaces) {
            for (Method m : i7e.getDeclaredMethods()) {
                final MethodSignature methodSignature = MethodSignature.forMethod(m);
                if (ReflectionHelper.isDefaultMethod(m)) {
                    handlers.put(methodSignature, DEFAULT_METHOD_INVOCATION_HANDLER);
                    final XBOverride xbOverride = m.getAnnotation(XBOverride.class);
                    if (xbOverride != null) {
                        handlers.put(methodSignature.overridenBy(xbOverride.value()), new OverrideByDefaultMethodInvocationHandler(m));
                    }
                    continue;
                }
                if (defaultInvocationHandlers.containsKey(methodSignature)) {
                    continue;
                }
                {
                    final XBRead readAnnotation = m.getAnnotation(XBRead.class);
                    if (readAnnotation != null) {
                        handlers.put(methodSignature, new ReadInvocationHandler(node, m, readAnnotation.value(), projector, absentIsEmpty));
                        continue;
                    }
                }
                {
                    final XBUpdate updateAnnotation = m.getAnnotation(XBUpdate.class);
                    if (updateAnnotation != null) {
                        handlers.put(methodSignature, new UpdateInvocationHandler(node, m, updateAnnotation.value(), projector));
                        continue;
                    }
                }
                {
                    final XBWrite writeAnnotation = m.getAnnotation(XBWrite.class);
                    if (writeAnnotation != null) {
                        handlers.put(methodSignature, new WriteInvocationHandler(node, m, writeAnnotation.value(), projector));
                        continue;
                    }
                }
                {
                    final XBDelete delAnnotation = m.getAnnotation(XBDelete.class);
                    if (delAnnotation != null) {
                        handlers.put(methodSignature, new DeleteInvocationHandler(node, m, delAnnotation.value(), projector));
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
     * @param o
     * @return
     */
    private static boolean isStructureChangingValue(final Object o) {
        return (o instanceof DOMAccess) || (o instanceof Node);
    }

    /**
     * Setter projection methods may have multiple parameters. One of them may be annotated with
     * {@link XBValue} to select it as value to be set.
     *
     * @param method
     * @return index of fist parameter annotated with {@link XBValue} annotation.
     */
    private static int findIndexOfValue(final Method method) {
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
     * Find the "me" attribute (which is a replacement for "this") and inject the projection proxy
     * instance.
     *
     * @param me
     * @param target
     */
    private static void injectMeAttribute(final DOMAccess me, final Object target, final Class<?> projectionInterface) {
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

    private static boolean isValidMeField(final Field field, final Class<?> projInterface) {
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
    private static void unwrapArgs(final Class<?>[] types, final Object[] args) {
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
    private static boolean isMultiValue(final Class<?> type) {
       return type.isArray() || Iterable.class.isAssignableFrom(type);
    }

    /**
     * When reading collections, determine the collection component type.
     *
     * @param method
     * @return
     */
    private static Class<?> findTargetComponentType(final Method method) {
        if (method.getReturnType().isArray()) {
            return method.getReturnType().getComponentType();
        }
        if (!List.class.equals(method.getReturnType())) {
            return null;
        }
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

}
