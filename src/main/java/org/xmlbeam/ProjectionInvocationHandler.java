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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
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
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.ASMHelper;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.ReflectionHelper;

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
    private final static String NONEMPTY = "(?!^$)";
    private final static String XML_NAME_START_CHARS = ":A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d" + "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff" + "\\uf900-\\ufdcf\\ufdf0-\\ufffd"
            + String.valueOf(Character.toChars(0x10000)) + "-" + String.valueOf(Character.toChars(0xEFFFF));
    private final static String XML_NAME_CHARS = XML_NAME_START_CHARS + "\\-\\.0-9\\u00B7\\u0300-\\u036F\\u203F-\\u2040";
    private final static String XML_ELEMENT = "[" + XML_NAME_START_CHARS + "]" + "[" + XML_NAME_CHARS + "]*";
    private final static String ELEMENT_PATH = "(/" + XML_ELEMENT + "(\\[@?" + XML_ELEMENT + "='.+'\\])?)";
    private final static String ATTRIBUTE_PATH = "(/?@" + XML_ELEMENT + ")";
    private final static String PARENT_PATH = "(/\\.\\.)";
    private static final Pattern LEGAL_XPATH_SELECTORS_FOR_SETTERS = Pattern.compile(NONEMPTY + "(^\\.?(" + ELEMENT_PATH + "*" + PARENT_PATH + "*)*(" + ATTRIBUTE_PATH + "|(/\\*))?$)");
    private static final Pattern DOUBLE_LBRACES = Pattern.compile("{{", Pattern.LITERAL);
    private static final Pattern DOUBLE_RBRACES = Pattern.compile("}}", Pattern.LITERAL);
    private final Node node;
    private final Class<?> projectionInterface;
    private final XBProjector projector;

    // Used to handle invocations on Java6 Mixins and Object methods.
    private final Map<Class<?>, Object> defaultInvokers;

    // Used to handle invocations on Java8 default methods.
    private transient Object defaultMethodInvoker;

    ProjectionInvocationHandler(final XBProjector projector, final Node node, final Class<?> projectionInterface, final Map<Class<?>, Object> defaultInvokers) {
        this.projector = projector;
        this.node = node;
        this.projectionInterface = projectionInterface;
        this.defaultInvokers = defaultInvokers;
    }

    /**
     * @param typeToSet
     * @param collection
     * @param parentElement
     * @param elementSelector
     */
    private int applyCollectionSetOnElement(final Type typeToSet, final Collection<?> collection, final Element parentElement, final String elementSelector) {
        final Document document = parentElement.getOwnerDocument();
        DOMHelper.removeAllChildrenBySelector(parentElement, elementSelector);
        assert !elementSelector.contains("/") : "Selector should be the trail of the path.";
        final String elementName = elementSelector.replaceAll("\\[.*", "");
        if (collection == null) {
            return 0;
        }
        Class<?> componentClass = Object.class;
        if (typeToSet instanceof ParameterizedType) {
            Type componentType = ((ParameterizedType) typeToSet).getActualTypeArguments()[0];
            componentClass = ReflectionHelper.upperBoundAsClass(componentType);
        }

        for (Object o : collection) {
            try {
                o = ReflectionHelper.unwrap(componentClass, o);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            if (o == null) {
                continue;
            }
            if (!isStructureChangingValue(o)) {
                final Element newElement = document.createElement(elementName);
                newElement.setTextContent(o.toString());
                parentElement.appendChild(newElement);
                continue;
            }
            if (o instanceof Node) {
                Node newNode = ((Node) o).cloneNode(true);
                DOMHelper.ensureOwnership(parentElement.getOwnerDocument(), newNode);
                parentElement.appendChild(newNode);
                continue;
            }
            final InternalProjection p = (InternalProjection) o;
            Element pElement = Node.DOCUMENT_NODE == p.getDOMNode().getNodeType() ? p.getDOMOwnerDocument().getDocumentElement() : (Element) p.getDOMNode();
            if (pElement == null) {
                continue;
            }
            Element clone = (Element) pElement.cloneNode(true);
            if (!elementName.equals(clone.getNodeName())) {
                if (!"*".equals(elementName)) {
                    clone = DOMHelper.renameElement(clone, elementName);
                }
            }
            DOMHelper.ensureOwnership(document, clone);
            parentElement.appendChild(clone);
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

    private void applySingleSetElementOnElement(final Element element, final Node parentNode, final String elementSelector) {
        //final Element newElement = (Element) projection.getDOMBaseElement().cloneNode(true);
        final Element newElement = (Element) element.cloneNode(true);
        DOMHelper.removeAllChildrenBySelector(parentNode, elementSelector);
        DOMHelper.ensureOwnership(parentNode.getOwnerDocument(), newElement);
        parentNode.appendChild(newElement);
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
    private String applyParams(String string, final Method method, final Object[] args) {
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
    private String replaceAllIfNotQuoted(final String string, final String pattern, String replacement) {
//        int p = string.indexOf(pattern);
//        if (p<0) {
//            return string;
//        }
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
     * Determine a methods return value that does not depend on the methods execution. Possible
     * values are void or the proxy itself (would be "this").
     *
     * @param method
     * @return
     */
    private Object getProxyReturnValueForMethod(final Object proxy, final Method method, final Integer numberOfChanges) {
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
        {
            String resolvedXpath = null;
            try {
                {
                    final XBRead readAnnotation = method.getAnnotation(XBRead.class);
                    if (readAnnotation != null) {
                        resolvedXpath = applyParams(projector.config().getExternalizer().resolveXPath(readAnnotation.value(), method, args), method, args);
                        return invokeGetter(proxy, method, resolvedXpath, args);
                    }
                }
                {
                    final XBUpdate updateAnnotation = method.getAnnotation(XBUpdate.class);
                    if (updateAnnotation != null) {
                        resolvedXpath = applyParams(projector.config().getExternalizer().resolveXPath(updateAnnotation.value(), method, args), method, args);
                        return invokeUpdater(proxy, method, resolvedXpath, args);
                    }
                }
                {
                    final XBWrite writeAnnotation = method.getAnnotation(XBWrite.class);
                    if (writeAnnotation != null) {
                        resolvedXpath = applyParams(projector.config().getExternalizer().resolveXPath(writeAnnotation.value(), method, args), method, args);
                        return invokeSetter(proxy, method, resolvedXpath, args);
                    }
                }
                {
                    final XBDelete delAnnotation = method.getAnnotation(XBDelete.class);
                    if (delAnnotation != null) {
                        resolvedXpath = applyParams(projector.config().getExternalizer().resolveXPath(delAnnotation.value(), method, args), method, args);
                        return invokeDeleter(proxy, method, resolvedXpath, args);
                    }
                }
            } catch (XPathExpressionException e) {
                throw new XBPathException(e, method, resolvedXpath);
            }
        }
        final Class<?> methodsDeclaringInterface = ReflectionHelper.findDeclaringInterface(method, projectionInterface);
        final Object customInvoker = projector.mixins().getProjectionMixin(projectionInterface, methodsDeclaringInterface);

        if (customInvoker != null) {
            injectMeAttribute((InternalProjection) proxy, customInvoker);
            return method.invoke(customInvoker, args);
        }

        final Object defaultInvoker = defaultInvokers.get(methodsDeclaringInterface);
        if (defaultInvoker != null) {
            return method.invoke(defaultInvoker, args);
        }

        if (ReflectionHelper.isDefaultMethod(method)) {
            if (defaultMethodInvoker == null) {
                defaultMethodInvoker = ASMHelper.createDefaultMethodProxy(projectionInterface, proxy);
            }
            try {
                return method.invoke(defaultMethodInvoker, args);
            } catch (InvocationTargetException e) {
                if (e.getCause() != null) {
                    throw e.getCause();
                }
                throw e;
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
     * @param proxy
     * @param format
     */
    private Object invokeDeleter(final Object proxy, final Method method, final String path, final Object[] args) throws Throwable {
        final Document document = DOMHelper.getOwnerDocumentFor(node);
        final XPath xPath = projector.config().createXPath(document);
//        try {
//            if (ReflectionHelper.mayProvideParameterNames()) {
//                xPath.setXPathVariableResolver(new MethodParamVariableResolver(method, args, xPath.getXPathVariableResolver()));
//           }

        final XPathExpression expression = xPath.compile(path);
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
//        } finally {
//            xPath.reset();
//        }
    }

    private Object invokeGetter(final Object proxy, final Method method, final String path, final Object[] args) throws Throwable {
        final Node node = getNodeForMethod(method, args);
        final Document document = DOMHelper.getOwnerDocumentFor(node);
        final XPath xPath = projector.config().createXPath(document);
// Automatic propagation of parameters as XPath variables
// disabled so far...
//        try {
//        if (ReflectionHelper.mayProvideParameterNames()) {
//            xPath.setXPathVariableResolver(new MethodParamVariableResolver(method,args,xPath.getXPathVariableResolver()));
//        }
        final XPathExpression expression = xPath.compile(path);
        final boolean wrappedInOptional = ReflectionHelper.isOptional(method.getGenericReturnType());
        final Class<?> returnType = wrappedInOptional ? ReflectionHelper.getParameterType(method.getGenericReturnType()) : method.getReturnType();
        if (projector.config().getTypeConverter().isConvertable(returnType)) {
            String data = (String) expression.evaluate(node, XPathConstants.STRING);
            try {
                final Object result = projector.config().getTypeConverter().convertTo(returnType, data);
                return wrappedInOptional ? ReflectionHelper.createOptional(result) : result;
            } catch (NumberFormatException e) {
                throw new NumberFormatException(e.getMessage() + " XPath was:" + path);
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
//        } finally {
//            xPath.reset();
//        }
    }

    private Object invokeUpdater(final Object proxy, final Method method, final String path, final Object[] args) throws Throwable {
        if (!ReflectionHelper.hasParameters(method)) {
            throw new IllegalArgumentException("Method " + method + " was invoked as updater but has no parameter. Please add a parameter so this method could actually change the DOM.");
        }
        if (method.getAnnotation(XBDocURL.class) != null) {
            throw new IllegalArgumentException("Method " + method + " was invoked as updater but has a @" + XBDocURL.class.getSimpleName() + " annotation. Defining updaters on external projections is not valid because there is no DOM attached.");
        }
        final Node node = getNodeForMethod(method, args);
        final Document document = DOMHelper.getOwnerDocumentFor(node);
        final XPath xPath = projector.config().createXPath(document);
        final XPathExpression expression = xPath.compile(path);
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

    private Object invokeSetter(final Object proxy, final Method method, final String path, final Object[] args) throws Throwable {
        if (!LEGAL_XPATH_SELECTORS_FOR_SETTERS.matcher(path).matches()) {
            throw new IllegalArgumentException("Method " + method + " was invoked as setter and did not have an XPATH expression with an absolute path to an element or attribute:\"" + path + "\"");
        }
        if (!ReflectionHelper.hasParameters(method)) {
            throw new IllegalArgumentException("Method " + method + " was invoked as setter but has no parameter. Please add a parameter so this method could actually change the DOM.");
        }
        if (method.getAnnotation(XBDocURL.class) != null) {
            throw new IllegalArgumentException("Method " + method + " was invoked as setter but has a @" + XBDocURL.class.getSimpleName() + " annotation. Defining setters on external projections is not valid because there is no DOM attached.");
        }
        final String pathToElement = path.replaceAll("\\[@", "[attribute::").replaceAll("/?@.*", "").replaceAll("\\[attribute::", "[@");
        final Node settingNode = getNodeForMethod(method, args);
        final Document document = DOMHelper.getOwnerDocumentFor(settingNode);
        assert document != null;
        final int findIndexOfValue = findIndexOfValue(method);
        final Object valueToSet = args[findIndexOfValue];
        final Type typeToSet = method.getGenericParameterTypes()[findIndexOfValue];
        final boolean isMultiValue = isMultiValue(method.getParameterTypes()[findIndexOfValue]);

        if ("/*".equals(pathToElement)) { // Setting a new root element.
            if (isMultiValue) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter changing the document root element, but tries to set multiple values.");
            }
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

        if (isMultiValue) {
            if (path.contains("@")) {
                throw new IllegalArgumentException("Method " + method + " was invoked as setter changing some attribute, but was declared to set multiple values. I can not create multiple attributes for one path.");
            }
            final String path2Parent = pathToElement.replaceAll("/[^/]+$", "");
            final String elementSelector = pathToElement.replaceAll(".*/", "");
            final Element parentElement = DOMHelper.ensureElementExists(document, path2Parent);
            //   DOMHelper.removeAllChildrenBySelector(parentElement, elementSelector);
//            if (valueToSet == null) {
//                return getProxyReturnValueForMethod(proxy, method);
//            }
            Collection<?> collection2Set = (valueToSet != null) && (valueToSet.getClass().isArray()) ? ReflectionHelper.array2ObjectList(valueToSet) : (Collection<?>) valueToSet;
            int count = applyCollectionSetOnElement(typeToSet, collection2Set, parentElement, elementSelector);
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(count));
        }
        if ((valueToSet instanceof Element) || (valueToSet instanceof InternalProjection)) {
            String pathToParent = pathToElement.replaceAll("/[^/]*$", "");
            String elementSelector = pathToElement.replaceAll(".*/", "");
            Element parentNode = DOMHelper.ensureElementExists(document, pathToParent);
            applySingleSetElementOnElement(valueToSet instanceof InternalProjection ? ((InternalProjection) valueToSet).getDOMBaseElement() : (Element) valueToSet, parentNode, elementSelector);
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
        }

        Element elementToChange;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            elementToChange = DOMHelper.ensureElementExists(document, pathToElement);
        } else {
            assert node.getNodeType() == Node.ELEMENT_NODE;
            elementToChange = DOMHelper.ensureElementExists(document, (Element) node, pathToElement);
        }
        if (valueToSet instanceof Node) {
            Node newNode = ((Node) valueToSet).cloneNode(true);
            String pathToParent = pathToElement.replaceAll("/[^/]*$", "");
            String elementSelector = pathToElement.replaceAll(".*/", "");
            Element parentNode = DOMHelper.ensureElementExists(document, pathToParent);
            DOMHelper.removeAllChildrenBySelector(parentNode, elementSelector);
            DOMHelper.ensureOwnership(parentNode.getOwnerDocument(), newNode);
            elementToChange.appendChild(newNode);
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
        }

        if (path.replaceAll("\\[@", "[attribute::").contains("@")) {
            String attributeName = path.replaceAll(".*@", "");
            DOMHelper.setOrRemoveAttribute(elementToChange, attributeName, valueToSet == null ? null : valueToSet.toString());
            return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
        }
        if (valueToSet == null) {
            DOMHelper.removeAllChildrenBySelector(elementToChange, "*");
        } else {
            elementToChange.setTextContent(valueToSet.toString());
        }
        return getProxyReturnValueForMethod(proxy, method, Integer.valueOf(1));
    }

    /**
     * @param typeToSet
     * @return
     */
    private boolean isMultiValue(final Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

}
