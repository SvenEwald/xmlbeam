/**
 *  Copyright 2014 Sven Ewald
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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import java.io.StringReader;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.xml.sax.InputSource;

/**
 * @author sven
 */
public class DuplexXPathParser implements XPath {

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#compile(java.lang.String)
     */
    @Override
    public DuplexExpression compile(final String expression) throws XPathExpressionException {
        XParser parser = new XParser(new StringReader(expression));
        try {
            SimpleNode node = parser.START();
            return new DuplexExpression(node, expression);
        } catch (ParseException e) {
            throw new XPathExpressionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#evaluate(java.lang.String, java.lang.Object)
     */
    @Override
    public String evaluate(final String expression, final Object item) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#evaluate(java.lang.String, org.xml.sax.InputSource)
     */
    @Override
    public String evaluate(final String expression, final InputSource source) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#evaluate(java.lang.String, java.lang.Object,
     * javax.xml.namespace.QName)
     */
    @Override
    public Object evaluate(final String expression, final Object item, final QName returnType) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#evaluate(java.lang.String, org.xml.sax.InputSource,
     * javax.xml.namespace.QName)
     */
    @Override
    public Object evaluate(final String expression, final InputSource source, final QName returnType) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#getNamespaceContext()
     */
    @Override
    public NamespaceContext getNamespaceContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#getXPathFunctionResolver()
     */
    @Override
    public XPathFunctionResolver getXPathFunctionResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#getXPathVariableResolver()
     */
    @Override
    public XPathVariableResolver getXPathVariableResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#reset()
     */
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#setNamespaceContext(javax.xml.namespace.NamespaceContext)
     */
    @Override
    public void setNamespaceContext(final NamespaceContext nsContext) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#setXPathFunctionResolver(javax.xml.xpath.XPathFunctionResolver)
     */
    @Override
    public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPath#setXPathVariableResolver(javax.xml.xpath.XPathVariableResolver)
     */
    @Override
    public void setXPathVariableResolver(final XPathVariableResolver resolver) {
        // TODO Auto-generated method stub

    }
}
