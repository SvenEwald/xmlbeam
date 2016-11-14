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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xmlbeam.XBException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.types.XBAutoFileList;
import org.xmlbeam.types.CloseableList;
import org.xmlbeam.types.CloseableValue;
import org.xmlbeam.util.intern.ReflectionHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;

/**
 * This class is used to provide an fluid interface for direct evaluation of XPath expressions.
 *
 * @author sven
 */
public final class DefaultXPathBinder implements XPathBinder {

    private final DocumentResolver documentProvider;
    private final DuplexExpression duplexExpression;
    private final XBProjector projector;
    private final Closeable documentWriter;

    /**
     * Constructor for DefaultXPathEvaluator.
     *
     * @param projector
     * @param documentProvider
     * @param xpath
     * @param documentWriter 
     */
    public DefaultXPathBinder(final XBProjector projector, final DocumentResolver documentProvider, final String xpath, final Closeable documentWriter) {
        this.projector = projector;
        this.documentProvider = documentProvider;
        this.duplexExpression = new DuplexXPathParser(projector.config().getUserDefinedNamespaceMapping()).compile(xpath);
        this.documentWriter = documentWriter;
    }

    /**
     * Evaluates the XPath as a boolean value. This method is just a shortcut for as(Boolean.TYPE);
     *
     * @return true when the selected value equals (ignoring case) 'true'
     */
    @Override
    public CloseableValue<Boolean> asBoolean() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindSingeValue(Boolean.TYPE, callerClass);
    }

    /**
     * Evaluates the XPath as a int value. This method is just a shortcut for as(Integer.TYPE);
     *
     * @return int value of evaluation result.
     */
    @Override
    public CloseableValue<Integer> asInt() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindSingeValue(Integer.TYPE, callerClass);
    }

    /**
     * Evaluates the XPath as a String value. This method is just a shortcut for as(String.class);
     *
     * @return String value of evaluation result.
     */
    @Override
    public CloseableValue<String> asString() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindSingeValue(String.class, callerClass);
    }

    /**
     * Evaluates the XPath as a Date value. This method is just a shortcut for as(Date.class); You
     * probably want to specify ' using ' followed by some formatting pattern consecutive to the
     * XPAth.
     *
     * @return Date value of evaluation result.
     */
    @Override
    public CloseableValue<Date> asDate() {
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindSingeValue(Date.class, callerClass);
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
    public <T> CloseableValue<T> as(final Class<T> returnType) {
        validateEvaluationType(returnType);
        final Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindSingeValue(returnType, callerClass);
    }

    private <T> CloseableValue<T> bindSingeValue(final Class<T> returnType, final Class<?> callerClass) {
        validateEvaluationType(returnType);
        try {
            Document document = documentProvider.resolve(returnType, callerClass);

            XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());

            InvocationContext invocationContext = new InvocationContext(duplexExpression.getExpressionAsStringWithoutFormatPatterns(), //
                    null, expression, duplexExpression, null, returnType, projector);

            return new DefaultFileValue<T>(document, invocationContext, documentWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new XBException("Error during binding", e);
        }

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
     * Evaluate the XPath as a list of the given type.
     *
     * @param componentType
     *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any
     *            class with a String constructor or a String factory method, and org.w3c.Node
     * @return List of return type that reflects the evaluation result.
     */
    @Override
    public <T> CloseableList<T> asListOf(final Class<T> componentType) {
        Class<?> callerClass = ReflectionHelper.getDirectCallerClass();
        return bindMultiValues(componentType, callerClass);
    }

    private <T> CloseableList<T> bindMultiValues(final Class<T> componentType, final Class<?> callerClass) {
        validateEvaluationType(componentType);
        try{
        Document document = documentProvider.resolve(componentType, callerClass);

        XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());

        InvocationContext invocationContext = new InvocationContext(duplexExpression.getExpressionAsStringWithoutFormatPatterns(), //
                null, expression, duplexExpression, null, componentType, projector);

        return new DefaultFileList<T>(document,invocationContext,documentWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
           throw new XBException("Error during evaluation", e);
        }
        
//        try {
//            Document document = documentProvider.resolve(componentType, callerClass);
//            XPathExpression expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
//            InvocationContext invocationContext = new InvocationContext(null, null, expression, duplexExpression, null, componentType, projector);
//            return (List<T>) evaluateAsList(expression, document, null, invocationContext);
//        } catch (XPathExpressionException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

}
