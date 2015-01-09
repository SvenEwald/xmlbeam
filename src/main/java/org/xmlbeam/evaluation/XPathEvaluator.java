package org.xmlbeam.evaluation;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.types.TypeConverter;
import org.xmlbeam.util.intern.ReflectionHelper;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;

/**
 * This class is used to provide an alternative fluid interface for direct evaluation of XPath
 * expressions.
 *
 * @author sven
 */
public final class XPathEvaluator {

    private final XPathExpression expression;
    private final Document document;
    private DuplexExpression duplexExpression;
    private final XBProjector projector;

    public XPathEvaluator(final XBProjector projector, final InputStream is, final String xpath) {
        this(projector, loadDocument(projector, is), xpath);
    }

    /**
     * @param is
     * @return
     */
    private static Document loadDocument(final XBProjector projector, final InputStream is) {
        final DocumentBuilder documentBuilder = projector.config().createDocumentBuilder();
        try {
            return documentBuilder.parse(is, "");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param document
     * @param xpath
     */
    public XPathEvaluator(final XBProjector projector, final Document document, final String xpath) {
        this.projector = projector;
        this.document = document;
        try {
            this.duplexExpression = new DuplexXPathParser().compile(xpath);
            this.expression = projector.config().createXPath(document).compile(duplexExpression.getExpressionAsStringWithoutFormatPatterns());
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T as(final Class<T> returnType) {
        validateEvaluationType(returnType);
        try {
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
                return projector.projectDOMNode(node, returnType);
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalPathStateException();
    }

    private <T> void validateEvaluationType(final Class<T> returnType) {
        if (ReflectionHelper.isOptional(returnType)) {
            throw new IllegalArgumentException("Type Optional is only allowed as a method return type");
        }
    }

    public <T> T[] asArrayOf(final Class<T> returnType) {
        validateEvaluationType(returnType);
        List<T> list = asListOf(returnType);
        return (T[]) list.toArray((Object[]) java.lang.reflect.Array.newInstance(returnType, list.size()));
    }

    public <T> List<T> asListOf(final Class<T> returnType) {
        validateEvaluationType(returnType);
        InvocationContext invocationContext = new InvocationContext(null, null, this.expression, duplexExpression, null, returnType, projector);
        try {
            return (List<T>) evaluateAsList(expression, document, null, invocationContext);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

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
}
