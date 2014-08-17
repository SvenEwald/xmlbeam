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

import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPATHEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASHSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTXPATH;

import org.w3c.dom.Node;
import org.xmlbeam.XBProjector.InternalProjection;
import org.xmlbeam.util.intern.DOMHelper;

/**
 *
 */
public class ApplySingleValueVisitor implements INodeEvaluationVisitor {

    private final Object value;

    public ApplySingleValueVisitor(final Object value) {
        this.value = value;
    }

    @Override
    public Node visit(final SimpleNode simpleNode, final Node domTarget) {
        switch (simpleNode.getID()) {
        case JJTSTART:
            return simpleNode.allChildrenAccept(this, domTarget);
        case JJTXPATH:
            return simpleNode.allChildrenAccept(this, domTarget);
        case JJTEXPR:
            return simpleNode.allChildrenAccept(this, domTarget);
        case JJTPATHEXPR:
            validatePathExpr(simpleNode);
            Node domNode = simpleNode.allChildrenAccept(this, domTarget);
            applyValueToDOMNode(domNode, value);
            return domNode;
        case JJTSLASHSLASH:
            throw new XBXPathExprNotAllowedForWriting(simpleNode, "Ambiguous locator");
        case JJTSLASH:
            return DOMHelper.getOwnerDocumentFor(domTarget);
        default:
            throw new XBXPathExprNotAllowedForWriting(simpleNode, "Not implemented");
        }
    }

    /**
     * @param domNode
     * @param value2
     */
    private void applyValueToDOMNode(final Node domNode, final Object value2) {
        if (value2 instanceof ProjectionInvocationHandler.InternalProjection)

    }

    /**
     * @param node
     */
    private void validatePathExpr(final SimpleNode node) {
        if (node.jjtGetNumChildren() < 1) {
            throw new XBXPathExprNotAllowedForWriting(node, "Path expression has no steps");
        }
    }
}
