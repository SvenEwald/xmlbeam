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

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.SimpleNode.StepListFilter;

/**
 * @author sven
 */
public class DuplexExpression {

    private final static StepListFilter ALL_BUT_LAST = new StepListFilter() {

        @Override
        public List<org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.Node> filter(SimpleNode[] children) {
            List<org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.Node>
            return null;
        }
    };

    @Override
    public String toString() {
        return "DuplexExpression [xpath=" + xpath + "]";
    }

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
        try { //TODO: cache expression type ?
            final ExpressionType expressionType = node.firstChildAccept(new ExpressionTypeEvaluationVisitor(), null);
            return expressionType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please report this bug: Can not determine type of XPath:" + xpath, e);
        }
    }

    public org.w3c.dom.Node ensureExistence(final org.w3c.dom.Node contextNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(contextNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        //node.dump("");
        return ((List<org.w3c.dom.Node>) node.firstChildAccept(new BuildDocumentVisitor(namespaceMapping), contextNode)).get(0);
    }

    /**
     * @param contextNode
     */
    public org.w3c.dom.Node ensureParentExistence(final Node contextNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(contextNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        //node.dump("");
        return ((List<org.w3c.dom.Node>) node.firstChildAccept(new BuildDocumentVisitor(namespaceMapping, ALL_BUT_LAST), contextNode)).get(0);
    }

}
