/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xmlbeam.evaluation;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.AutoMap;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.exceptions.XBPathException;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.ReflectionHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;

/**
 * This class is used to provide an fluid interface for direct evaluation of XPath expressions.
 *
 * @author sven
 */
public final class DefaultXPathEvaluator implements XPathEvaluator {

    private final DocumentResolver documentProvider;
    private final DuplexExpression duplexExpression;
    private final XBProjector projector;

    /**
     * Constructor for DefaultXPathEvaluator.
     *
     * @param projector
     * @param documentProvider
     * @param xpath
     */
    public DefaultXPathEvaluator(final XBProjector projector, final DocumentResolver documentProvider, final String xpath) {
        this.projector = projector;
        this.documentProvider = documentProvider;
        this.duplexExpression = new DuplexXPathParser(projector.config().getUserDefinedNamespaceMapping()).compile(xpath);
    }

    /**
     * Evaluates the XPath as a boolean value. This method is just a shortcut for as(Boolean.TYPE);
     *
     * @return true when the selected value equals (ignoring case) 'true'
     */
    @Override
    public boolean asBoolean() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateSingeValue(Boolean.TYPE, callerClass);
    }

    /**
     * Evaluates the XPath as a int value. This method is just a shortcut for as(Integer.TYPE);
     *
     * @return int value of evaluation result.
     */
    @Override
    public int asInt() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateSingeValue(Integer.TYPE, callerClass);
    }

    /**
     * Evaluates the XPath as a String value. This method is just a shortcut for as(String.class);
     *
     * @return String value of evaluation result.
     */
    @Override
    public String asString() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateSingeValue(String.class, callerClass);
    }

    /**
     * Evaluates the XPath as a Date value. This method is just a shortcut for as(Date.class); You
     * probably want to specify ' using ' followed by some formatting pattern consecutive to the
     * XPAth.
     *
     * @return Date value of evaluation result.
     */
    @Override
    public Date asDate() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateSingeValue(Date.class, callerClass);
    }

    /**
     * Evaluate the XPath as a value of the given type.
     *
     * @param returnType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return a value of return type that reflects the evaluation result.
     */
    @Override
    public <T> T as(final Class<T> returnType) {
        validateEvaluationType(returnType);
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateSingeValue(returnType, callerClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T evaluateSingeValue(final Class<T> returnType, final Class<?> callerClass) {
        try {
            Document document = documentProvider.resolve(returnType, callerClass);

            XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());

            if (projector.config().getTypeConverter().isConvertable(returnType)) {
                String data;
                if (duplexExpression.getExpressionType().isMustEvalAsString()) {
                    data = (String) expression.evaluate(document, XPathConstants.STRING);
                } else {
                    Node dataNode = (Node) expression.evaluate(document, XPathConstants.NODE);
                    data = dataNode == null ? null : dataNode.getTextContent();
                }
                if ((data == null) && (projector.getFlags().contains(Flags.ABSENT_IS_EMPTY))) {
                    data = "";
                }

                final Object result = projector.config().getTypeConverter().convertTo(returnType, data, duplexExpression.getExpressionFormatPattern());
                return (T) result;
            }

            if (Node.class.isAssignableFrom(returnType)) {
                final Object result = expression.evaluate(document, XPathConstants.NODE);
                return (T) result;
            }

            if (returnType.isInterface()) {
                final Node node = (Node) expression.evaluate(document, XPathConstants.NODE);
                if (node == null) {
                    return null;
                }
                return projector.projectDOMNode(node, returnType);
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalPathStateException();
    }

    private <T> void validateEvaluationType(final Class<T> returnType) {
        if (ReflectionHelper.isOptional(returnType)) {
            throw new IllegalArgumentException("Type Optional is only allowed as a method return type.");
        }
        if (Collection.class.isAssignableFrom(returnType)) {
            throw new IllegalArgumentException("A collection type can not be component type.");
        }
    }

    /**
     * Evaluate the XPath as an array of the given type.
     *
     * @param componentType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return an array of return type that reflects the evaluation result.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] asArrayOf(final Class<T> componentType) {
        Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        List<T> list = evaluateMultiValues(componentType, callerClass);
        return list.toArray((T[]) java.lang.reflect.Array.newInstance(componentType, list.size()));
    }

    /**
     * Evaluate the XPath as a list of the given type.
     *
     * @param componentType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return List of return type that reflects the evaluation result.
     */
    @Override
    public <T> List<T> asListOf(final Class<T> componentType) {
        Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return evaluateMultiValues(componentType, callerClass);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> evaluateMultiValues(final Class<T> componentType, final Class<?> callerClass) {
        validateEvaluationType(componentType);
        try {
            Document document = documentProvider.resolve(componentType, callerClass);
            XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            InvocationContext invocationContext = new InvocationContext(null, null, expression, duplexExpression, null, componentType, projector);
            return (List<T>) evaluateAsList(expression, document, null, invocationContext);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Perform an XPath evaluation on an invocation context.
     *
     * @param expression
     * @param node
     * @param method
     * @param invocationContext
     * @return a list of evaluation results
     * @throws XPathExpressionException
     */
    public static List<?> evaluateAsList(final XPathExpression expression, final Node node, final Method method, final InvocationContext invocationContext) throws XPathExpressionException {
        //assert targetComponentType != null;
        final Class<?> targetComponentType = invocationContext.getTargetComponentType();
        final NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
        final List<Object> linkedList = new LinkedList<Object>();

        final TypeConverter typeConverter = invocationContext.getProjector().config().getTypeConverter();
        if (typeConverter.isConvertable(targetComponentType)) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(typeConverter.convertTo(targetComponentType, nodes.item(i).getTextContent(), invocationContext.getExpressionFormatPattern()));
            }
            return linkedList;
        }
        if (Node.class.equals(targetComponentType)) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                linkedList.add(nodes.item(i));
            }
            return linkedList;
        }
        if (targetComponentType.isInterface()) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                Object subprojection = invocationContext.getProjector().projectDOMNode(nodes.item(i), targetComponentType);
                linkedList.add(subprojection);
            }
            return linkedList;
        }
        throw new IllegalArgumentException("Return type " + targetComponentType + " is not valid for list or array component type returning from method " + method + " using the current type converter:" + invocationContext.getProjector().config().getTypeConverter()
                + ". Please change the return type to a sub projection or add a conversion to the type converter.");
    }

    /**
     * @param invocationContext
     *            invocation context
     * @param item
     * @param targetComponentType
     * @return node content as target type
     */
    @SuppressWarnings("unchecked")
    public static <E> E convertToComponentType(final InvocationContext invocationContext, final Node item, final Class<?> targetComponentType) {
        TypeConverter typeConverter = invocationContext.getProjector().config().getTypeConverter();
        if (typeConverter.isConvertable(invocationContext.getTargetComponentType())) {
            return (E) typeConverter.convertTo(targetComponentType, item != null ? DOMHelper.directTextContent(item) : null, invocationContext.getExpressionFormatPattern());
        }
        if (Node.class.equals(targetComponentType)) {
            return (E) item;
        }
        if (targetComponentType.isInterface()) {
            if (item == null) {
                return null;
            }
            Object subprojection = invocationContext.getProjector().projectDOMNode(item, targetComponentType);
            return (E) subprojection;
        }
        throw new IllegalArgumentException("Return type " + targetComponentType + " is not valid for a ProjectedList using the current type converter:" + invocationContext.getProjector().config().getTypeConverter()
                + ". Please change the return type to a sub projection or add a conversion to the type converter.");
    }

    /**
     * @param componentType
     * @return map bound to the element resolved by XPath.
     * @see org.xmlbeam.evaluation.XPathEvaluator#asMapOf(java.lang.Class)
     */
    @Override
    public <T> XBAutoMap<T> asMapOf(final Class<T> componentType) {
        try {
            final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
            Document document = documentProvider.resolve(componentType, callerClass);
            XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
            final Node baseNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if (baseNode.getNodeType() != Node.ELEMENT_NODE) {
                throw new XBException("XPath expression does not resolve to an element. Maps can only be created for elements.");
            }
            InvocationContext invocationContext = new InvocationContext(null, null, null, null, null, componentType, projector);
            return new AutoMap<T>(baseNode, invocationContext, componentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new XBPathException(e, duplexExpression.getExpressionAsStringWithoutFormatPatterns());
        }
    }

}
