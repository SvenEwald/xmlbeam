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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector.Projection;
import org.xmlbeam.util.DOMHelper;
import org.xmlbeam.util.ReflectionHelper;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
class ProjectionInvocationHandler implements InvocationHandler, Serializable {
    //private static final String LEGAL_XPATH_SELECTORS_FOR_SETTERS = "^(/)|(/[a-zA-Z]+)+((/@[a-z:A-Z]+)?|(/\\*))$";
    private static final Pattern LEGAL_XPATH_SELECTORS_FOR_SETTERS = Pattern.compile("^(/)|(/[a-zA-Z]+)+((/@[a-z:A-Z]+)?|(/\\*))$");
    private final Node node;
    private final Class<?> projectionInterface;
    private final XMLProjector xmlProjector;
    private final Map<Class<?>, Object> defaultInvokers = new HashMap<Class<?>, Object>();

    ProjectionInvocationHandler(final XMLProjector xmlProjector, final Node node, final Class<?> projectionInterface) {
        this.xmlProjector = xmlProjector;
        this.node = node;
        this.projectionInterface = projectionInterface;
        Projection projectionInvoker = new Projection() {
            @Override
            public Node getXMLNode() {
                return ProjectionInvocationHandler.this.node;
            }

            @Override
            public Class<?> getProjectionInterface() {
                return ProjectionInvocationHandler.this.projectionInterface;
            }

        };
        Object objectInvoker = new Serializable() {
            @Override
            public String toString() {
                try {
                    StringWriter writer = new StringWriter();
                    xmlProjector.config().getTransformer().transform(new DOMSource(node), new StreamResult(writer));
                    String output = writer.getBuffer().toString();
                    return output;
                } catch (TransformerConfigurationException e) {
                    throw new RuntimeException(e);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Projection)) {
                    return false;
                }
                Projection op = (Projection) o;
                if (!ProjectionInvocationHandler.this.projectionInterface.equals(op.getProjectionInterface())) {
                    return false;
                }
                return node.equals(op.getXMLNode());
            }

            @Override
            public int hashCode() {
                return 31 * ProjectionInvocationHandler.this.projectionInterface.hashCode() + 27 * node.hashCode();
            }
        };

        defaultInvokers.put(Projection.class, projectionInvoker);
        defaultInvokers.put(Object.class, objectInvoker);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        Object defaultInvoker = defaultInvokers.get(method.getDeclaringClass());
        if (defaultInvoker != null) {
            return method.invoke(defaultInvoker, args);
        }
        Object customInvoker = xmlProjector.mixins().getProjectionMixin(projectionInterface, method.getDeclaringClass());
        if (customInvoker != null) {
            injectMeAttribute((Projection) proxy, customInvoker);
            return method.invoke(customInvoker, args);
        }

        if ((!ReflectionHelper.hasReturnType(method)) && (!ReflectionHelper.hasParameters(method))) {
            throw new IllegalArgumentException("Invoking void method " + method + " without parameters. What should I do?");
        }

        if (ReflectionHelper.isSetter(method) || (!ReflectionHelper.hasReturnType(method))) {
            return invokeSetter(proxy, method, args);
        }
        return invokeGetter(proxy, method, args);
    }

    private void injectMeAttribute(Projection me, Object target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!me.getProjectionInterface().equals(field.getType())) {
                continue;
            }
            if (!"me".equalsIgnoreCase(field.getName())) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(target, me);
                return;
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object invokeSetter(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String path = getXPathExpression(method, args);        
        if (!LEGAL_XPATH_SELECTORS_FOR_SETTERS.matcher(path).matches()) {
            throw new IllegalArgumentException("Method " + method + " was invoked as setter and did not have an XPATH expression with an absolute path to an element or attribute:\"" + path + "\"");
        }
        final String pathToElement = path.replaceAll("/@.*", "");
        if (pathToElement.isEmpty()) {
            // changing the root node.
            // FIXME

        }
        final Node settingNode = getNodeForMethod(method, args);
        final Document document =  Node.DOCUMENT_NODE == settingNode.getNodeType() ? ((Document) settingNode) : settingNode.getOwnerDocument();
        Element rootElement = document.getDocumentElement();
//        if (rootElement == null) {
//            assert Node.DOCUMENT_NODE == settingNode.getNodeType();
//            String rootElementName = path.replaceAll("(^/)|(/.*$)", "");
//            rootElement = ((Document) settingNode).createElement(rootElementName);           
//            settingNode.appendChild(rootElement);
//        }

        final Object valuetToSet = args[findIndexOfValue(method)];
        Element elementToChange = DOMHelper.ensureElementExists(document, pathToElement);
        if (path.contains("@")) {
            String attributeName = path.replaceAll(".*@", "");            
            elementToChange.setAttribute(attributeName, valuetToSet.toString());
        } else {
            if (valuetToSet instanceof Projection) {
                applySingleSetProjectionOnElement((Projection) args[0], elementToChange);
            }
            if (valuetToSet instanceof Collection) {
                applyCollectionSetProjectionOnelement((Collection<?>) valuetToSet, elementToChange);
            } else {
                elementToChange.setTextContent(valuetToSet.toString());
            }
        }

        if (!ReflectionHelper.hasReturnType(method)) {
            return null;
        }
        if (method.getReturnType().equals(method.getDeclaringClass())) {
            return proxy;
        }
        throw new IllegalArgumentException("Method " + method + " has illegal return type \"" + method.getReturnType() + "\". I don't know what do return");
    }

    /**
     * @param method
     * @return index of fist parameter annotated with {@link SetterValue} annotation.
     */
    private int findIndexOfValue(Method method) {
        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            for (Annotation a : annotations) {
                if (SetterValue.class.equals(a)) {
                    return index;
                }
            }
            ++index;
        }

        // If no attribute i annotated, the first one is taken.
        return 0;
    }

    /**
     * 
     * @param collection
     * @param parentElement
     */
    private void applyCollectionSetProjectionOnelement(Collection<?> collection, Element parentElement) {
        Set<String> elementNames = new HashSet<String>();
        for (Object o : collection) {
            if (!(o instanceof Projection)) {
                throw new IllegalArgumentException("Setter argument collection contains an object of type " + o.getClass().getName() + ". When setting a collection on a Projection, the collection must not contain other types than Projections.");
            }
            Projection p = (Projection) o;
            elementNames.add(p.getXMLNode().getNodeName());
        }
        for (String elementName : elementNames) {
            DOMHelper.removeAllChildrenByName(parentElement, elementName);
        }
        for (Object o : collection) {
            Projection p = (Projection) o;
            parentElement.appendChild(p.getXMLNode());
        }
    }

    private void applySingleSetProjectionOnElement(final Projection projection, final Element element) {
        DOMHelper.removeAllChildrenByName(element, projection.getXMLNode().getNodeName());
        element.appendChild(projection.getXMLNode());
    }

    private String getXPathExpression(final Method method, final Object[] args) {
        XBRead annotation = method.getAnnotation(org.xmlbeam.XBRead.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method " + method + " needs a " + org.xmlbeam.XBRead.class.getSimpleName() + " annotation.");
        }
        String path = MessageFormat.format(annotation.value(), args);
        return path;
    }

    private Node getNodeForMethod(final Method method, final Object[] args) throws SAXException, IOException, ParserConfigurationException {
        Node evaluationNode = node;
        if (method.getAnnotation(DocumentURL.class) != null) {
            String uri = method.getAnnotation(DocumentURL.class).value();
            uri = MessageFormat.format(uri, args);            
            evaluationNode = DOMHelper.getDocumentFromURI(xmlProjector.config().getDocumentBuilder(), uri, projectionInterface);
        }
        return evaluationNode;
    }

    private Object invokeGetter(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String path = getXPathExpression(method, args);
        final Node node = getNodeForMethod(method, args);
        final Document document = Node.DOCUMENT_NODE == node.getNodeType() ? ((Document) node) : node.getOwnerDocument();
        final XPath xPath = xmlProjector.config().getXPath(document);
        final XPathExpression expression = xPath.compile(path);
        final Class<?> returnType = method.getReturnType();
        if (xmlProjector.config().getTypeConverter().isConvertable(returnType)) {
            String data = (String) expression.evaluate(node, XPathConstants.STRING);
            try {
                return xmlProjector.config().getTypeConverter().convertTo(returnType, data);
            } catch (NumberFormatException e) {
                throw new NumberFormatException(e.getMessage() + " XPath was:" + path);
            }
        }
        if (List.class.equals(returnType)) {
            return evaluateAsList(expression, node, method);
        }
        if (returnType.isArray()) {
            List<?> list = evaluateAsList(expression, node, method);
            return list.toArray((Object[]) java.lang.reflect.Array.newInstance(returnType.getComponentType(), list.size()));
        }
        if (returnType.isInterface()) {
            Node newNode = (Node) expression.evaluate(node, XPathConstants.NODE);
            Projection subprojection = (Projection) xmlProjector.projectXML(newNode, returnType);

            return subprojection;
        }
        throw new IllegalArgumentException("Return type " + returnType + " of method " + method + " is not supported. Please change to an projection interface, a List, an Array or one of current type converters types:" + xmlProjector.config().getTypeConverter());
    }

    private List<?> evaluateAsList(final XPathExpression expression, final Node node, final Method method) throws XPathExpressionException {
        NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        List<Object> linkedList = new LinkedList<Object>();
        Class<?> targetType;
        if (method.getReturnType().isArray()) {
            targetType = method.getReturnType().getComponentType();
        } else {
            targetType = method.getAnnotation(org.xmlbeam.XBRead.class).targetComponentType();
            if (XBRead.class.equals(targetType)) {
                throw new IllegalArgumentException("When using List as return type for method " + method + ", please specify the list content type in the " + XBRead.class.getSimpleName() + " annotaion. I can not determine it from the method signature.");
            }
        }

        if (xmlProjector.config().getTypeConverter().isConvertable(targetType)) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(xmlProjector.config().getTypeConverter().convertTo(targetType, nodes.item(i).getTextContent()));
            }
            return linkedList;
        }
        if (targetType.isInterface()) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node n = nodes.item(i).cloneNode(true);
                Projection subprojection = (Projection) xmlProjector.projectXML(n, method.getAnnotation(org.xmlbeam.XBRead.class).targetComponentType());
                linkedList.add(subprojection);
            }
            return linkedList;
        }
        throw new IllegalArgumentException("Return type " + method.getAnnotation(org.xmlbeam.XBRead.class).targetComponentType() + " is not valid for list or array component type returning from method " + method + " using the current type converter:"
                + xmlProjector.config().getTypeConverter());
    }
}