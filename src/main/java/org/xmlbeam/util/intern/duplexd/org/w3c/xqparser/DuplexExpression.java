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

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;

/**
 * @author sven
 */
public class DuplexExpression implements XPathExpression {

    private final SimpleNode node;
    private final String xpath;

    /**
     * @param node
     */
    DuplexExpression(final SimpleNode node, final String xpath) {
        this.node = node;
        this.xpath = xpath;
    }

    public ExpressionType getExpressionType() {
        try {
            final ExpressionType expressionType = node.firstChildAccept(new ExpressionTypeEvaluationVisitor(), null);
            return expressionType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please report this bug: Can not determine type of XPath:" + xpath, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPathExpression#evaluate(java.lang.Object)
     */
    @Override
    public String evaluate(final Object arg0) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPathExpression#evaluate(org.xml.sax.InputSource)
     */
    @Override
    public String evaluate(final InputSource arg0) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPathExpression#evaluate(java.lang.Object, javax.xml.namespace.QName)
     */
    @Override
    public Object evaluate(final Object arg0, final QName arg1) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.xpath.XPathExpression#evaluate(org.xml.sax.InputSource,
     * javax.xml.namespace.QName)
     */
    @Override
    public Object evaluate(final InputSource arg0, final QName arg1) throws XPathExpressionException {
        // TODO Auto-generated method stub
        return null;
    }

}
