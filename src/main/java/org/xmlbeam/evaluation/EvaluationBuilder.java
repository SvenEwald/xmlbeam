package org.xmlbeam.evaluation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.util.intern.duplex.DuplexExpression;
import org.xmlbeam.util.intern.duplex.DuplexXPathParser;

/**
 * This class is used to provide an alternative fluid interface for direct evaluation of XPath
 * expressions.
 *
 * @author sven
 */
public final class EvaluationBuilder {

    private final XPathExpression xpath;
    private final Document document;
    private DuplexExpression duplexExpression;

    public EvaluationBuilder(final XBProjector projector, final InputStream is, final String xpath) {
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
    public EvaluationBuilder(final XBProjector projector, final Document document, final String xpath) {
        this.document = document;
        try {
            this.xpath = projector.config().createXPath(document).compile(xpath);
            this.duplexExpression = new DuplexXPathParser().compile(xpath);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T as(final Class<T> returnType) {

        return null;
    }

    public <T> T[] asArrayOf(final Class<T> returnType) {
        return null;
    }

    public <T> List<T> asListOf(final Class<T> returnType) {
        return null;
    }
}
