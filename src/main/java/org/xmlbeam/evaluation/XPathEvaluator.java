package org.xmlbeam.evaluation;

import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
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
        return null;
    }

    public <T> List<T> asListOf(final Class<T> returnType) {
        validateEvaluationType(returnType);
        return null;
    }
}
