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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.xmlbeam.util.DOMUtils;
import org.xmlbeam.util.TypeConverter;

class ProjectionInvocationHandler implements InvocationHandler, Serializable {
    private static final String LEGAL_XPATH_SELECTORS_FOR_SETTERS = "^(/[a-zA-Z]+)+(/@[a-z:A-Z]+)?$";
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
                return node;
            }

            @Override
            public Class<?> getProjectionInterface() {
                return projectionInterface;
            }

            /*
             * @Override public Map<Class<?>, Object> getInvokers() { return
             * customInvokers; }
             */
        };
        Object objectInvoker = new Serializable() {

            @Override
            public String toString() {
                try {
                    StringWriter writer = new StringWriter();
                    xmlProjector.getTransformer().transform(new DOMSource(node), new StreamResult(writer));
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
        defaultInvokers.put(ProjectionInvocationHandler.class, this);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        Object defaultInvoker = defaultInvokers.get(method.getDeclaringClass());
        if (defaultInvoker != null) {
            return method.invoke(defaultInvoker, args);
        }
        Object customInvoker = xmlProjector.getCustomInvoker(projectionInterface, method.getDeclaringClass());
        if (customInvoker != null) {
            injectMeAttribute((Projection) proxy, customInvoker);
            return method.invoke(customInvoker, args);
        }

        if ((!hasReturnType(method)) && (!hasParameters(method))) {
            throw new IllegalArgumentException("Invoking void method " + method + " without parameters. What should I do?");
        }
        
        if (isSetter(method) || (!hasReturnType(method))) {
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
        if (!path.matches(LEGAL_XPATH_SELECTORS_FOR_SETTERS)) {
            throw new IllegalArgumentException("Method " + method + " was invoked as setter and did not have an XPATH expression with an absolute path to an element or attribute:\"" + path + "\"");
        }
        final String pathToElement = path.replaceAll("/@.*", "");
        Node settingNode = getDocumentForMethod(method, args);
        Element rootElement = Node.DOCUMENT_NODE == settingNode.getNodeType() ? ((Document) settingNode).getDocumentElement() : settingNode.getOwnerDocument().getDocumentElement();
        if (rootElement==null) {
            assert Node.DOCUMENT_NODE == settingNode.getNodeType();
            String rootElementName=path.replaceAll("(^/)|(/.*$)"    , "");
            rootElement = ((Document) settingNode).createElement(rootElementName);
            settingNode.appendChild(rootElement);
        }
        if (path.contains("@")) {
            String attributeName = path.replaceAll(".*@", "");
            Element element = ensureElementExists(rootElement, pathToElement);
            element.setAttribute(attributeName, args[0].toString());
        } else {
            if (args[0] instanceof Projection) {
                Element element = ensureElementExists(rootElement, pathToElement);
                applySingleSetProjectionOnElement((Projection) args[0], element, method.getDeclaringClass());
            }
            if (args[0] instanceof Collection) {
                Element parent = ensureElementExists(rootElement, pathToElement.replaceAll("/[^/]+$", ""));
                String elementName= pathToElement.replaceAll("^.*/", "");
                applyCollectionSetProjectionOnelement((Collection<?>) args[0], parent,elementName);

            } else {
                Element element = ensureElementExists(rootElement, pathToElement);
                element.setTextContent(args[0].toString());
            }
        }

        if (!hasReturnType(method)) {
            return null;
        }
        if (method.getReturnType().equals(method.getDeclaringClass())) {
            return proxy;
        }
        throw new IllegalArgumentException("Method " + method + " has illegal return type \"" + method.getReturnType() + "\". I don't know what do return");
    }

    private void applyCollectionSetProjectionOnelement(Collection<?> collection, Element parentElement, String elementName) {
        DOMUtils.removeAllChildrenByName(parentElement, elementName);
        for (Object o : collection) {
            if (!(o instanceof Projection)) {
                throw new IllegalArgumentException("Setter argument collection contains an object of type " + o.getClass().getName() + ". When setting a collection on a Projection, the collection must not contain other types than Projections.");
            }
            Projection p = (Projection) o;
            parentElement.appendChild(p.getXMLNode());
        }
    }

    private void applySingleSetProjectionOnElement(final Projection projection, final Element element, final Class<?> projectionClass) {
        DOMUtils.removeAllChildrenByName(element, projection.getXMLNode().getNodeName());
        element.appendChild(projection.getXMLNode());
    }

    private Element ensureElementExists(final Element settingNode, final String pathToElement) {
        String splitme = pathToElement.replaceAll("(^/)|(/$)", "");
        Element element = settingNode;
        for (String elementName : splitme.split("/")) {
            if (elementName.equals(element.getNodeName())) {
                continue;                
            }
            NodeList nodeList = element.getElementsByTagName(elementName);
            if (nodeList.getLength() == 0) {
                element.getOwnerDocument().createElement(elementName);
                element = (Element) element.appendChild(element.getOwnerDocument().createElement(elementName));
                continue;
            }
            element = (Element) nodeList.item(0);
        }
        return element;
    }

    private String getXPathExpression(final Method method, final Object[] args) {
        Xpath annotation = method.getAnnotation(org.xmlbeam.Xpath.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method " + method + " needs a " + org.xmlbeam.Xpath.class.getSimpleName() + " annotation.");
        }
        String path = MessageFormat.format(annotation.value(), args);
        return path;
    }

    private Node getDocumentForMethod(final Method method, final Object[] args) throws SAXException, IOException, ParserConfigurationException {
        Node evaluationNode = node;
        if (method.getAnnotation(URL.class) != null) {
            String uri = method.getAnnotation(URL.class).value();
            uri = MessageFormat.format(uri, args);
            evaluationNode = DOMUtils.getXMLNodeFromURI(xmlProjector.getDocumentBuilder(), uri, projectionInterface);
        }
        return evaluationNode;
    }

    private Object invokeGetter(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String path = getXPathExpression(method, args);
        final Node node = getDocumentForMethod(method, args);
        final Document document = Node.DOCUMENT_NODE == node.getNodeType() ? ((Document) node) : node.getOwnerDocument();
        final XPath xPath = xmlProjector.getXPath(document);
        final XPathExpression expression = xPath.compile(path);
        final Class<?> returnType = method.getReturnType();
        if (TypeConverter.CONVERTERS.containsKey(returnType)) {
            String data = (String) expression.evaluate(node, XPathConstants.STRING);
            if (data == null) {
                return null;
            }
            Object convert = TypeConverter.CONVERTERS.get(returnType).convert(data);
            return convert;
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
        throw new IllegalArgumentException("Return type " + returnType + " of method " + method + " is not supported. Please change to an interface, a List, an Array or one of " + TypeConverter.CONVERTERS.keySet());
    }

    private boolean isSetter(final Method method) {
        return method.getName().toLowerCase().startsWith("set") && hasParameters(method);
    }

    private boolean hasReturnType(final Method method) {
        if (method.getReturnType() == null) {
            return false;
        }
        if (Void.class.equals(method.getReturnType())) {
            return false;
        }

        return !Void.TYPE.equals(method.getReturnType());
    }

    private boolean hasParameters(final Method method) {
        return (method.getParameterTypes().length > 0);
    }

    List<?> evaluateAsList(final XPathExpression expression, final Node node, final Method method) throws XPathExpressionException {
        NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        List<Object> linkedList = new LinkedList<Object>();
        Class<?> targetType;
        if (method.getReturnType().isArray()) {
            targetType = method.getReturnType().getComponentType();
        } else {
            targetType = method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType();
            if (Xpath.class.equals(targetType)) {
                throw new IllegalArgumentException("When using List as return type for method " + method + ", please specify the list content type in the " + Xpath.class.getSimpleName() + " annotaion. I can not determine it from the method signature.");
            }
        }

        TypeConverter<?> converter = TypeConverter.CONVERTERS.get(targetType);
        if (converter != null) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(converter.convert(nodes.item(i).getTextContent()));
            }
            return linkedList;
        }
        if (targetType.isInterface()) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node n = nodes.item(i).cloneNode(true);
                Projection subprojection = (Projection) xmlProjector.projectXML(n, method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType());
                linkedList.add(subprojection);
            }
            return linkedList;
        }
        throw new IllegalArgumentException("Return type " + method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType() + " is not valid for list or array component type returning from method " + method + ". Try one of " + TypeConverter.CONVERTERS.keySet());
    }
}