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
package org.xmlbeam.util.intern.duplex;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplex.BuildDocumentVisitor.MODE;
import org.xmlbeam.util.intern.duplex.INodeEvaluationVisitor.VisitorClosure;
import org.xmlbeam.util.intern.duplex.SimpleNode.StepListFilter;

/**
 * @author sven
 */
public class DuplexExpression {

    private final static StepListFilter ALL_BUT_LAST = new StepListFilter() {

        @Override
        public List<SimpleNode> filter(final SimpleNode[] children) {
            if (children.length < 2) {
                return Collections.emptyList();
            }
            final List<SimpleNode> list = Arrays.asList(children).subList(0, children.length - 1);
            assert list.size() == (children.length - 1);
            return list;
        }
    };

    private final static StepListFilter ONLY_LAST_STEP = new StepListFilter() {

        @Override
        public List<SimpleNode> filter(final SimpleNode[] children) {
            if (children.length < 1) {
                return Collections.emptyList();
            }
            assert children[children.length - 1] != null;
            return Collections.singletonList(children[children.length - 1]);
        }
    };

    private XPathVariableResolver variableResolver = null;

    /**
     * @param resolver
     * @return this for convenience
     */
    public DuplexExpression setXPathVariableResolver(final XPathVariableResolver resolver) {
        this.variableResolver = resolver;
        return this;
    }

    @Override
    public String toString() {
        return "DuplexExpression [xpath=" + xpath + "]";
    }

    private final SimpleNode node;
    private final String xpath;

    private final Map<String, String> variableFormatPatterns = new HashMap<String, String>();

    private final String strippedXPath;

    private final String expressionFormatPattern;

    /**
     * @param node
     */
    DuplexExpression(final SimpleNode node, final String xpath) {
        this.node = node;
        this.xpath = xpath;
        final Deque<Integer> removeStartPositions = new LinkedList<Integer>();
        final Deque<Integer> removeEndPositions = new LinkedList<Integer>();
        node.getFirstChildWithId(XParserTreeConstants.JJTXPATH).eachChild(new VisitorClosure() {

            @Override
            public void apply(final SimpleNode node, final Node data) {
                if (node.getID() != XParserTreeConstants.JJTVARNAME) {
                    return;
                }
                SimpleNode qnameNode = node.getFirstChildWithId(XParserTreeConstants.JJTQNAME);
                if (qnameNode == null) {
                    return;
                }
                SimpleNode formatNode = node.getFirstChildWithId(XParserTreeConstants.JJTVARIABLEFORMAT);
                if (formatNode == null) {
                    variableFormatPatterns.put(qnameNode.getValue(), null);
                    return;
                }
                variableFormatPatterns.put(qnameNode.getValue(), stripFormatMarkers(formatNode.getValue()));
                removeStartPositions.push(formatNode.getStartColumn());
                removeEndPositions.push(formatNode.getEndColumn());
            }
        }, null);

        SimpleNode formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTEXPRESSIONFORMAT);
        if (formatPatternNode != null) {
            this.expressionFormatPattern = formatPatternNode.getValue();
            removeStartPositions.push(formatPatternNode.getStartColumn());
            removeEndPositions.push(formatPatternNode.getEndColumn());
        } else {
            formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTVARIABLEFORMAT);
            if (formatPatternNode != null) {
                this.expressionFormatPattern = stripFormatMarkers(formatPatternNode.getValue());

                removeStartPositions.push(formatPatternNode.getStartColumn());
                removeEndPositions.push(formatPatternNode.getEndColumn());
            } else {
                this.expressionFormatPattern = null;
            }
        }

        StringBuilder stringBuilder = new StringBuilder(xpath);
        while (!removeEndPositions.isEmpty()) {
            stringBuilder.delete(removeStartPositions.pop(), removeEndPositions.pop() + 1);
        }
        strippedXPath = stringBuilder.toString();
    }

    /**
     * @return true if expression makes use of XPath variables
     */
    public boolean isUsingVariables() {
        return !variableFormatPatterns.keySet().isEmpty();
    }

    /**
     * @param value
     * @return
     */
    private String stripFormatMarkers(String value) {
        value = value.substring(2, value.length() - 2);
        return value.startsWith("using ") ? value.substring("using ".length()) : value;
    }

    /**
     * Calculates the return type of the expression.
     *
     * @return ExpressionType
     */
    public ExpressionType getExpressionType() {
        try { //TODO: cache expression type ?
            final ExpressionType expressionType = node.firstChildAccept(new ExpressionTypeEvaluationVisitor(), null);
            return expressionType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please report this bug: Can not determine type of XPath:" + xpath, e);
        }
    }

    /**
     * Creates nodes until selecting such a path would return something.
     *
     * @param contextNode
     * @return the node that this expression would select.
     */
    @SuppressWarnings("unchecked")
    public org.w3c.dom.Node ensureExistence(final org.w3c.dom.Node contextNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(contextNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        //node.dump("");
        return ((List<org.w3c.dom.Node>) node.firstChildAccept(new BuildDocumentVisitor(variableResolver, namespaceMapping), contextNode)).get(0);
    }

    /**
     * @param contextNode
     * @return the parent element
     */
    @SuppressWarnings("unchecked")
    // due to JCC-API
    public Element ensureParentExistence(final Node contextNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(contextNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        //node.dump("");
        return (Element) ((List<org.w3c.dom.Node>) node.firstChildAccept(new BuildDocumentVisitor(variableResolver, namespaceMapping, ALL_BUT_LAST, MODE.CREATE_IF_NOT_EXISTS), contextNode)).get(0);
    }

    /**
     * @param parentNode
     */
    public void deleteAllMatchingChildren(final Node parentNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(parentNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        BuildDocumentVisitor visitor = new BuildDocumentVisitor(variableResolver, namespaceMapping, ONLY_LAST_STEP, MODE.DELETE);
        List<?> result;
        int lastLength = -1;
        do {
            result = (List<?>) node.firstChildAccept(visitor, parentNode);
            if (result.size() == lastLength) {
                throw new IllegalStateException("Infinite loop detected. Please report issue with example.");
            }
            lastLength = result.size();
        } while ((!result.isEmpty()) && (null != result.get(0)));
    }

    /**
     * @param parentNode
     * @return fresh new node
     */
    @SuppressWarnings("unchecked")
    public Node createChildWithPredicate(final Node parentNode) {
        final Document document = DOMHelper.getOwnerDocumentFor(parentNode);
        final Map<String, String> namespaceMapping = DOMHelper.getNamespaceMapping(document);
        BuildDocumentVisitor visitor = new BuildDocumentVisitor(variableResolver, namespaceMapping, ONLY_LAST_STEP, MODE.JUST_CREATE);
        List<Node> nodes = (List<Node>) node.firstChildAccept(visitor, parentNode);
        assert nodes.size() == 1;
        return nodes.get(0);
    }

    /**
     * Dumps the tree behind the expression for debugging purpose.
     */
    public void dump() {
        this.node.dump("");
    }

    /**
     * @return String representation of expressions pattern
     */
    public String getExpressionFormatPattern() {
        return expressionFormatPattern;
//        SimpleNode formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTEXPRESSIONFORMAT);
//        if (formatPatternNode != null) {
//            return formatPatternNode.getValue();
//        }
//        formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTVARIABLEFORMAT);
//        if (formatPatternNode == null) {
//            return null;
//        }
//        String value = formatPatternNode.getValue();
//        value = value.substring(2, value.length() - 2);
//        return value.startsWith("using ") ? value.substring("using ".length()) : value;
//        //return (String) node.secondChildAccept(new GetExpressionFormatPatternVisitor(),null);
//        //node.getFirstChildWithId(XParserTreeConstants.JJTEXPRESSIONFORMAT);
    }

    /**
     * @return xpath compileable with regular xpath engine.
     */
    public String getExpressionAsStringWithoutFormatPatterns() {
        return strippedXPath;
//        SimpleNode formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTEXPRESSIONFORMAT);
//        if (formatPatternNode != null) {
//            return removeStringPart(xpath, formatPatternNode.getStartColumn(), formatPatternNode.getEndColumn() + 1);
//        };
//
//        formatPatternNode = node.getFirstChildWithId(XParserTreeConstants.JJTVARIABLEFORMAT);
//        if (formatPatternNode != null) {
//            return removeStringPart(xpath, formatPatternNode.getStartColumn(), formatPatternNode.getEndColumn() + 1);
//        };
//
//        return this.xpath;
    }

//    /**
//     * @param xpath2
//     * @param begin
//     * @param end
//     * @return
//     */
//    private final static String removeStringPart(final String string, final int begin, final int end) {
//        return string.substring(0, begin) + (end > string.length() ? "" : string.substring(end, string.length()));
//    }

    /**
     * @param name
     * @return pattern for variable or null if no format is defined.
     */
    public String getVariableFormatPattern(final String name) {
        return variableFormatPatterns.get(name);
    }

    /**
     * @return true if getExpressionFormat is not null.
     */
    public boolean hasExpressionFormatPattern() {
        return expressionFormatPattern != null;
    }
}
