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

import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTABBREVFORWARDSTEP;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCOMPARISONEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDECIMALLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTDOUBLELITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTINTEGERLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNAMETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTNODETEST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPATHEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPREDICATE;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPREDICATELIST;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTQNAME;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSLASHSLASH;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTEPEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTRINGLITERAL;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTXPATH;

import java.util.Collections;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;

/**
 */
public class BuildDocumentVisitor implements XParserVisitor {

    private static class LiteralVisitor implements XParserVisitor {

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTSTRINGLITERAL:
                return node.getValue().replaceAll("'(.*)'", "$1").replaceAll("\"(.*)\"", "$1");
            case JJTINTEGERLITERAL:
                return Integer.valueOf(node.getValue());
            case JJTDECIMALLITERAL:
                return Float.valueOf(node.getValue());
            case JJTDOUBLELITERAL:
                return Double.valueOf(node.getValue());
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expetced here.");
            }
        }

    }

    public static class EvaluateStepExprVisitor implements XParserVisitor {

        final boolean onAttribute;

        public static EvaluateStepExprVisitor create(final boolean onAttribute) {
            return new EvaluateStepExprVisitor(onAttribute);
        }

        private EvaluateStepExprVisitor(final boolean onAttribute) {
            this.onAttribute = onAttribute;
        }

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTSTEPEXPR:
                Object result = node.childrenAccept(this, data);
                return result;
            case JJTABBREVFORWARDSTEP:
                return node.childrenAccept(create("@".equals(node.getValue())), data);
            case JJTNODETEST:
                return node.childrenAccept(this, data);
            case JJTNAMETEST:
                return node.childrenAccept(this, data);
            case JJTQNAME:
                String name = node.getValue();
                if (onAttribute) {
                    assert data instanceof Element;
                    return asElement(data).getAttributeNodeNS(null, name);
                }
                return asElement(data).getElementsByTagNameNS(null, name);
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expeced here.");
            }
        }

    }

    private static class FindNameTestVisitor implements XParserVisitor {

        String name;
        boolean isAttribute;

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTABBREVFORWARDSTEP:
                this.isAttribute = "@".equals(node.getValue());
                return node.childrenAccept(this, data);
            case JJTNODETEST:
                return node.childrenAccept(this, data);
            case JJTNAMETEST:
                return node.childrenAccept(this, data);
            case JJTQNAME:
                this.name = node.getValue();
                return data;
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expeced here.");
            }
        }

    }

    private static class ApplyPredicatesVisitor implements XParserVisitor {

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTPREDICATELIST:
                return node.childrenAccept(this, data);
            case JJTPREDICATE:
                return node.childrenAccept(this, data);
            case JJTEXPR:
                return node.childrenAccept(this, data);
            case JJTCOMPARISONEXPR:
                if (!"=".equals(node.getValue())) {
//                    throw new XBXPathExprNotAllowedForWriting(node, "Operator "+node.getValue()+" not implemented");
                }
                Object first = node.firstChildAccept(this, data);
                if (!(first instanceof Node)) {
                    throw new XBXPathExprNotAllowedForWriting(node, "A nonwritable predicate");
                }
                Object second = node.secondChildAccept(this, data);
                if (first instanceof Attr) {
                    assert data instanceof Element;
                    ((Element) data).setAttributeNS(null, ((Attr) first).getNodeName(), second.toString());
//                    ((Element) data).setAttribute(((Attr) first).getNodeName(), second.toString());
                    return data;
                }
                ((Node) first).setTextContent(second.toString());
                return data;
            case JJTSTEPEXPR:
                return node.jjtAccept(new BuildDocumentVisitor(), data);
            case JJTSTRINGLITERAL:
            case JJTINTEGERLITERAL:
            case JJTDECIMALLITERAL:
            case JJTDOUBLELITERAL:
                return node.jjtAccept(new LiteralVisitor(), data);
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expetced here.");
            }
        }

    }

    private static class EvaluatePredicateListVisitor implements XParserVisitor {

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTPREDICATELIST:
                return node.childrenAccept(this, data);
            case JJTPREDICATE:
                return node.childrenAccept(this, data);
            case JJTEXPR:
                return node.childrenAccept(this, data);
            case JJTCOMPARISONEXPR:
                NodeList list = (NodeList) node.firstChildAccept(this, data);
                if (list.getLength() == 0) {
                    return false;
                }
                Node first = unwrapNodeList(list);
                Object second = node.secondChildAccept(this, data);
                return Boolean.valueOf(compare(node, first, second));
            case JJTSTEPEXPR:
                return node.jjtAccept(EvaluateStepExprVisitor.create(false), data);
            case JJTSTRINGLITERAL:
            case JJTINTEGERLITERAL:
            case JJTDECIMALLITERAL:
            case JJTDOUBLELITERAL:
                return node.jjtAccept(new LiteralVisitor(), data);
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expeced here.");
            }
        }

        /**
         * @param value
         * @param first
         * @param second
         * @return
         */
        private boolean compare(final SimpleNode value, final Object first, final Object second) {
            switch (value.getValue().charAt(0)) {
            case '=':
                return toString(first).equals(toString(second));
            default:
                throw new XBXPathExprNotAllowedForWriting(value, "Operator " + value.getValue() + " not implemented");
            }
        }

        /**
         * @param first
         * @return
         */
        private String toString(final Object o) {
            if (o instanceof Node) {
                return ((Node) o).getTextContent();
            }
            return o == null ? "<null>" : o.toString();
        }
    }

    @Override
    public Object visit(final SimpleNode node, final Node data) {
        switch (node.getID()) {
        case JJTSTART:
            return node.childrenAccept(this, data);
        case JJTXPATH:
            return node.childrenAccept(this, data);
        case JJTEXPR:
            return node.childrenAccept(this, data);
        case JJTPATHEXPR:
            return asListofNodes(node.childrenAccept(this, data));
        case JJTSLASHSLASH:
            throw new XBXPathExprNotAllowedForWriting(node, "Ambiguous locator");
        case JJTSLASH:
            return DOMHelper.getOwnerDocumentFor(data);
        case JJTSTEPEXPR:
            FindNameTestVisitor nameTest = new FindNameTestVisitor();
            node.firstChildAccept(nameTest, data);
            String childName = nameTest.name;
            boolean isAttribute = nameTest.isAttribute;
            if (isAttribute) {
                assert data.getNodeType() == Node.ELEMENT_NODE;
                Attr attributeNode = ((org.w3c.dom.Element) data).getAttributeNode(childName);
                if (attributeNode != null) {
                    return attributeNode;
                }
                Attr newAttribute = DOMHelper.getOwnerDocumentFor(data).createAttributeNS(null, childName);
                return newAttribute;
                // return ((org.w3c.dom.Element) data).appendChild(newAttribute);
            }
            Node nextNode = findFirstMatchingChildElement(data, childName, node.getFirstChildWithId(JJTPREDICATELIST));
            if (nextNode == null) {

                return createChildElement(data, childName, node.getFirstChildWithId(JJTPREDICATELIST));
            }
            return nextNode;
        default:
            throw new XBXPathExprNotAllowedForWriting(node, "Not implemented");
        }
    }

    /**
     * @param childrenAccept
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Node> asListofNodes(final Object childrenAccept) {
        if (childrenAccept == null) {
            return Collections.emptyList();
        }
        if (childrenAccept instanceof Node) {
            return Collections.singletonList((Node) childrenAccept);
        }
        return (List<Node>) childrenAccept;
    }

    /**
     * @param first
     * @return first node of nodelist if its not empty. null instead.
     */
    public static Node unwrapNodeList(final NodeList first) {
        if (first == null) {
            return null;
        }
        if (first.getLength() == 0) {
            return null;
        }
        assert first.getLength() == 1;
        return first.item(0);
    }

    /**
     * @param data
     * @return
     */
    private static Element asElement(final Object data) {
        assert data instanceof Element;
        return (Element) data;
    }

    /**
     * @param data
     * @param childName
     * @param firstChildWithId
     * @return
     */
    private Element createChildElement(final Node data, final String childName, final SimpleNode predicateList) {
        Document document = DOMHelper.getOwnerDocumentFor(data);
        Element newElement = document.createElementNS(null, childName);
        if (data instanceof Document) {
            if (null != ((Document) data).getDocumentElement()) {
                ((Document) data).removeChild(((Document) data).getDocumentElement());
            }
        }
        data.appendChild(newElement);

        if (predicateList != null) {
            ApplyPredicatesVisitor applyPredicatesVisitor = new ApplyPredicatesVisitor();
            predicateList.jjtAccept(applyPredicatesVisitor, newElement);
        }
        return newElement;
    }

    /**
     * @param data
     * @param childName
     * @param firstChildWithId
     * @return
     */
    private Element findFirstMatchingChildElement(final Node data, final String childName, final SimpleNode predicateList) {
        if (data instanceof Document) {
            final Element root = ((Document) data).getDocumentElement();
            if (root == null) {
                return null;
            }
            if (!root.getNodeName().equals(childName)) {
                return null;
            }
            if (predicateList == null) {
                return root;
            }
            Object accept = predicateList.childrenAccept(new EvaluatePredicateListVisitor(), root);
            if (Boolean.TRUE.equals(accept)) {
                return root;
            }
            if (accept instanceof Integer) {
                throw new XBXPathExprNotAllowedForWriting(predicateList, "No position predicate on document element allowed");
            }
            return null;
        }
        final NodeList nodeList = ((Element) data).getElementsByTagName(childName);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element e = (Element) nodeList.item(i);
            if (predicateList == null) {
                // If no predicate is set, no restriction applies, return first.
                return e;
            }
            Object accept = predicateList.childrenAccept(new EvaluatePredicateListVisitor(), e);
            if (Boolean.TRUE.equals(accept)) {
                return e;
            }
            if (Integer.valueOf(i + 1).equals(accept)) {
                return e;
            }
        }
        return null;
    }

}
