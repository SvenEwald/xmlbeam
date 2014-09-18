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
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTABBREVREVERSESTEP;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCOMPARISONEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTCONTEXTITEMEXPR;
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
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;

/**
 */
class BuildDocumentVisitor implements XParserVisitor {

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

    public static class EvaluateStepExprVisitor implements INodeEvaluationVisitor<List<Node>> {

        final boolean onAttribute;

        public static EvaluateStepExprVisitor create(final boolean onAttribute) {
            return new EvaluateStepExprVisitor(onAttribute);
        }

        private EvaluateStepExprVisitor(final boolean onAttribute) {
            this.onAttribute = onAttribute;
        }

        @Override
        public List<Node> visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTSTEPEXPR:
                Object result = node.childrenAccept(this, data);
                return (List<Node>) result;
            case JJTABBREVFORWARDSTEP:
                return (List<Node>) node.childrenAccept(create("@".equals(node.getValue())), data);
            case JJTNODETEST:
                return (List<Node>) node.childrenAccept(this, data);
            case JJTNAMETEST:
                return (List<Node>) node.childrenAccept(this, data);
            case JJTQNAME:
                String name = node.getValue();
                if (onAttribute) {
                    assert data instanceof Element;
                    return DOMHelper.asList(asElement(data).getAttributeNodeNS(null, name));
                }
                return DOMHelper.asList(asElement(data).getElementsByTagNameNS(null, name));
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expeced here.");
            }
        }

    }

    private static class FindNameTestVisitor implements XParserVisitor {

        String name;
        boolean isAttribute;
        boolean resolved = false;;

        @Override
        public Object visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTABBREVFORWARDSTEP:
                this.isAttribute = "@".equals(node.getValue());
                return node.childrenAccept(this, data);
            case JJTABBREVREVERSESTEP:
                this.resolved = true;
                return data.getParentNode();
            case JJTNODETEST:
                return node.childrenAccept(this, data);
            case JJTNAMETEST:
                return node.childrenAccept(this, data);
            case JJTQNAME:
                this.name = node.getValue();
                return data;
            case JJTCONTEXTITEMEXPR:
                if (".".equals(node.getValue())) {
                    this.resolved = true;
                    if (data.getNodeType() == Node.DOCUMENT_NODE) {
                        return ((Document) data).getDocumentElement();
                    }
                    return data;
                }
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expeced here.");
            }
        }

        /**
         * @return true if nextNode is determined by this expression
         */
        public boolean isAlreadyResolved() {
            return resolved;
        }

    }

    private class ApplyPredicatesVisitor implements XParserVisitor {

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
                return node.jjtAccept(BuildDocumentVisitor.this, data);
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
                Object first = node.firstChildAccept(this, data);
                Object second = node.secondChildAccept(this, data);
                return Boolean.valueOf(compare(node, unList(first), unList(second)));
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
         * @param first
         * @return
         */
        private Object unList(final Object o) {
            if (!(o instanceof List)) {
                return o;
            }
            List<?> list = (List<?>) o;
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
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

    private final Map<String, String> namespaceMapping;

    /**
     * @param namespaceMapping
     */
    public BuildDocumentVisitor(final Map<String, String> namespaceMapping) {
        assert (namespaceMapping == null) || (!namespaceMapping.isEmpty());
        this.namespaceMapping = namespaceMapping == null ? Collections.<String, String> emptyMap() : Collections.unmodifiableMap(namespaceMapping);
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
            Object result = node.firstChildAccept(nameTest, data);
            if (nameTest.isAlreadyResolved()) {
                return result;
            }
            String childName = nameTest.name;
            boolean isAttribute = nameTest.isAttribute;
            if (isAttribute) {
                if (data.getNodeType() == Node.DOCUMENT_NODE) {
                    throw new XBXPathExprNotAllowedForWriting(node, "You can not set or get attributes on the document. You need a root element.");
                }
                assert data.getNodeType() == Node.ELEMENT_NODE;
                //TODO: Fix namespace setting on root node
                Attr attributeNode = ((org.w3c.dom.Element) data).getAttributeNodeNS(namespaceURL(childName), local(childName));
                if (attributeNode != null) {
                    return attributeNode;
                }
                Attr newAttribute = "xmlns".equals(childName) ? DOMHelper.getOwnerDocumentFor(data).createAttribute("xmlns") : DOMHelper.getOwnerDocumentFor(data).createAttributeNS(namespaceURL(childName), local(childName));
                //   newAttribute.setTextContent("huhu");
                if ("xmlns".equals(childName)) {
                    ((Element) data).setAttributeNode(newAttribute);
                } else {
                    ((Element) data).setAttributeNodeNS(newAttribute);
                }
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
        assert childName != null;
        assert data != null;
        Document document = DOMHelper.getOwnerDocumentFor(data);
        final Element newElement = (childName.contains(":")) ? document.createElementNS(namespaceURL(childName), local(childName)) : document.createElement(childName);
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
     * @param childName
     * @return
     */
    private String local(final String childName) {
//        int i = childName.indexOf(":");
//        if (i < 0) {
//            return childName;
//        }
//        return childName.substring(i + 1, childName.length());
        return childName;
    }

    /**
     * @param childName
     * @return
     */
    private String namespaceURL(final String childName) {
        if ("xmlns".equals(childName)) {
            return namespaceMapping.get(childName);
        }

        int i = childName.indexOf(":");
        if (i < 0) {
            return null;
        }
        String prefix = childName.substring(0, i);
        return namespaceMapping.get(prefix);

//        if (childName.equals("xmlns") || childName.startsWith("xmlns:")) {
//            return "http://www.w3.org/2000/xmlns/";
//        }
//        if (childName.startsWith("xml:")) {
//            return "http://www.w3.org/XML/1998/namespace";
//        }
//        return null;

//        int i = childName.indexOf(":");
//        if (i < 0) {
//            return null;
//        }
//        String prefix = childName.substring(0, i);
//
//        return prefix;
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
