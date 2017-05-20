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

import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTABBREVFORWARDSTEP;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTABBREVREVERSESTEP;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTCOMPARISONEXPR;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTCONTEXTITEMEXPR;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTDECIMALLITERAL;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTDOUBLELITERAL;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTEXPR;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTINTEGERLITERAL;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTNAMETEST;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTNODETEST;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTPATHEXPR;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTPREDICATE;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTPREDICATELIST;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTQNAME;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSLASH;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSLASHSLASH;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSTEPEXPR;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSTRINGLITERAL;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTVARNAME;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTXPATH;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplex.SimpleNode.StepListFilter;

/**
 */
class BuildDocumentVisitor implements XParserVisitor {

    enum MODE {
        CREATE_IF_NOT_EXISTS(true, false, true), JUST_CREATE(false, false, true), DELETE(true, true, false);
        final private boolean resolveExisting;
        final private boolean deleteExisting;
        final private boolean createNew;

        MODE(final boolean resolveExisting, final boolean deleteExisting, final boolean createNew) {
            this.resolveExisting = resolveExisting;
            this.deleteExisting = deleteExisting;
            this.createNew = createNew;
        }

        public boolean shouldResolve() {
            return resolveExisting;
        }

        public boolean shouldDelete() {
            return deleteExisting;
        }

        public boolean shouldCreate() {
            return createNew;
        }
    };

    private final MODE mode;

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

    private class EvaluateStepExprVisitor implements INodeEvaluationVisitor<List<Node>> {

        final private boolean onAttribute;

        EvaluateStepExprVisitor(final boolean onAttribute) {
            this.onAttribute = onAttribute;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Node> visit(final SimpleNode node, final Node data) {
            switch (node.getID()) {
            case JJTSTEPEXPR:
                Object result = node.childrenAccept(this, data);
                return (List<Node>) result;
            case JJTABBREVFORWARDSTEP:
                return (List<Node>) node.childrenAccept(new EvaluateStepExprVisitor("@".equals(node.getValue())), data);
            case JJTNODETEST:
                return (List<Node>) node.childrenAccept(this, data);
            case JJTNAMETEST:
                return (List<Node>) node.childrenAccept(this, data);
            case JJTQNAME:
                String name = node.getValue();
                if (onAttribute) {
                    assert data instanceof Element;
                    return DOMHelper.<Node> asList(getAttributeNodeByName(asElement(data), name));
                }
                List<Node> list = new LinkedList<Node>();
                findChildElementsByName(asElement(data), name, list);
                return list;
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
                        if (((Document) data).getDocumentElement()==null) {
                            throw new XBXPathExprNotAllowedForWriting(node, "There is no root element yet and I can not create one without being given a name");
                        }
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
        ApplyPredicatesVisitor() {
        };

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
                    throw new XBXPathExprNotAllowedForWriting(node, "Operator " + node.getValue() + " leads to non writable predicates.");
                }
                Object first = node.firstChildAccept(this, data);
                if (first instanceof List) {
                    first=((List)first).get(0);
                }
                if (!(first instanceof Node)) {
                    throw new XBXPathExprNotAllowedForWriting(node, "A non writable predicate");
                }
                Object second = node.secondChildAccept(this, data);
                DOMHelper.setDirectTextContent((Node) first, second.toString());
                /*
                 * if (first instanceof Attr) { assert data instanceof Element; ((Element)
                 * data).setAttributeNS(null, ((Attr) first).getNodeName(), second.toString()); //
                 * ((Element) data).setAttribute(((Attr) first).getNodeName(), second.toString());
                 * return data; } ((Node) first).setTextContent(second.toString());
                 */
                return data;
            case JJTSTEPEXPR:
                return node.jjtAccept(BuildDocumentVisitor.this, data);
            case JJTSTRINGLITERAL:
            case JJTINTEGERLITERAL:
            case JJTDECIMALLITERAL:
            case JJTDOUBLELITERAL:
                return node.jjtAccept(new LiteralVisitor(), data);
            case JJTQNAME:
                return QName.valueOf(node.getValue());
            case JJTVARNAME:
                return resolveVariable(node, data);
            case  JJTPATHEXPR:
                return node.childrenAcceptWithFilter(this, data, stepListFilter);
            default:
                throw new XBXPathExprNotAllowedForWriting(node, "Not expetced here.");
            }
        }

    }

    private class EvaluatePredicateListVisitor implements XParserVisitor {

        /**
         *
         */
        public EvaluatePredicateListVisitor() {
            // TODO Auto-generated constructor stub
        }

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
                return node.jjtAccept(new EvaluateStepExprVisitor(false), data);
            case JJTSTRINGLITERAL:
            case JJTINTEGERLITERAL:
            case JJTDECIMALLITERAL:
            case JJTDOUBLELITERAL:
                return node.jjtAccept(new LiteralVisitor(), data);
            case JJTVARNAME:
                return resolveVariable(node, data);
            case JJTQNAME:
                return QName.valueOf(node.getValue());
            //TODO: check if this case is needed or wrong.    
            case  JJTPATHEXPR:
                return node.childrenAcceptWithFilter(this, data, stepListFilter);
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
    private final StepListFilter stepListFilter;
    private final XPathVariableResolver variableResolver;

    /**
     * @param variableResolver
     * @param namespaceMapping
     */
    public BuildDocumentVisitor(final XPathVariableResolver variableResolver, final Map<String, String> namespaceMapping) {
        assert (namespaceMapping == null) || (!namespaceMapping.isEmpty());
        this.mode = MODE.CREATE_IF_NOT_EXISTS;
        this.namespaceMapping = namespaceMapping == null ? Collections.<String, String> emptyMap() : Collections.unmodifiableMap(namespaceMapping);
        this.stepListFilter = null;
        this.variableResolver = variableResolver;
    }

    /**
     * @param variableResolver
     * @param namespaceMapping
     * @param stepListFilter
     * @param mode
     */
    public BuildDocumentVisitor(final XPathVariableResolver variableResolver, final Map<String, String> namespaceMapping, final StepListFilter stepListFilter, final MODE mode) {
        assert stepListFilter != null;
        assert (namespaceMapping == null) || (!namespaceMapping.isEmpty());
        assert mode != null;
        this.mode = mode;
        this.stepListFilter = stepListFilter;
        this.namespaceMapping = namespaceMapping == null ? Collections.<String, String> emptyMap() : Collections.unmodifiableMap(namespaceMapping);
        this.variableResolver = variableResolver;
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
            return node.childrenAcceptWithFilter(this, data, stepListFilter);
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
            //boolean isAttribute = nameTest.isAttribute;
            if (nameTest.isAttribute) {
                if (data.getNodeType() == Node.DOCUMENT_NODE) {
                    throw new XBXPathExprNotAllowedForWriting(node, "You can not set or get attributes on the document. You need a root element.");
                }
                assert data.getNodeType() == Node.ELEMENT_NODE;
                Attr attributeNode = mode.shouldResolve() ? ((org.w3c.dom.Element) data).getAttributeNodeNS(namespaceURI(childName), childName) : null;
                if (attributeNode != null) {
                    if (mode.shouldDelete()) {
                        DOMHelper.removeAttribute(attributeNode);
                    }
                    return attributeNode;
                }
                if (mode.shouldCreate()) {
                    Attr newAttribute = createAttribute((Element) data, childName);
                    return newAttribute;
                }
                return null;
            }
            Node nextNode = null;
            if (mode.shouldResolve()) {
                List<Element> existingNodes = findAlltMatchingChildElements(data, childName, node.getFirstChildWithId(JJTPREDICATELIST), node);
                if (!existingNodes.isEmpty()) {
                    if (mode.shouldDelete()) {
                        DOMHelper.trim(existingNodes.get(0).getParentNode());                        
                        DOMHelper.removeNodes(existingNodes);                        
                        return existingNodes;
                    }
                    // If there are multiple possible hits, take the first one.
                    // This might have unexpected results when creating paths,
                    // but should be resolvable by adding more predicates.
                    nextNode = existingNodes.get(0);
                }
            }
            if (nextNode == null) {

                return mode.shouldCreate() ? createChildElement(data, childName, node.getFirstChildWithId(JJTPREDICATELIST)) : null;
            }
            return nextNode;

        case JJTVARNAME:
            return resolveVariable(node, data);
        default:
            throw new XBXPathExprNotAllowedForWriting(node, "Not implemented");
        }
    }

    /**
     * @param data
     * @param name
     * @return
     */
    private Attr createAttribute(final Element data, final String name) {
        final Document doc = data.getOwnerDocument();
        if (needNS(name)) {
            final Attr attr = doc.createAttributeNS(namespaceURI(name), name);
            data.setAttributeNodeNS(attr);
            return attr;
        }
        final Attr attr = doc.createAttribute(name);
        data.setAttributeNode(attr);
        return attr;
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
        final Element newElement = (childName.contains(":")) ? document.createElementNS(namespaceURI(childName), childName) : document.createElement(childName);
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
    private String namespaceURI(final String childName) {
        if ("xmlns".equals(childName)) {
            return namespaceMapping.get(childName);
        }

        int i = childName.indexOf(":");
        if (i < 0) {
            return null;
        }
        String prefix = childName.substring(0, i);
        return namespaceMapping.get(prefix);
    }

    /**
     * @param data
     * @param childName
     * @param firstChildWithId
     * @return
     */
    private List<Element> findAlltMatchingChildElements(final Node data, final String childName, final SimpleNode predicateList, final SimpleNode stepNode) {
        if (data instanceof Document) { // Child must be the root element
            final Element root = ((Document) data).getDocumentElement();
            if (root == null) {
                return Collections.emptyList();
            }
            if (!root.getNodeName().equals(childName)) {
                return Collections.emptyList();
            }
            if (predicateList == null) {
                return DOMHelper.asList(root);
            }
            Object accept = predicateList.childrenAccept(new EvaluatePredicateListVisitor(), root);
            if (Boolean.TRUE.equals(accept)) {
                return DOMHelper.asList(root);
            }
            if (accept instanceof Integer) {
                throw new XBXPathExprNotAllowedForWriting(predicateList, "No position predicate on document element allowed");
            }
            return Collections.emptyList();
        }
        List<Element> childElements = new LinkedList<Element>();
        findChildElementsByName((Element) data, childName, childElements);
        if (childElements.isEmpty()) {
            return Collections.emptyList();
        }
        if (predicateList == null) {
            // if (mode.shouldDelete()) {
            return childElements;
            // }
            // if (childElements.size() > 1) {
            //     throw new XBXPathExprNotAllowedForWriting(stepNode, "Ambigous step expression. I can not decide which path to follow. Please add a predicate to specify which element should be selected.");
            // }
            // return childElements.get(0);
        }
        List<Element> allMatchingElements = new LinkedList<Element>();
        int i = 0;
        for (Element e : childElements) {
            ++i;
            Object accept = predicateList.childrenAccept(new EvaluatePredicateListVisitor(), e);
            if (Boolean.TRUE.equals(accept)) {
//                  return e;
                allMatchingElements.add(e);
                continue;
            }
            if (Integer.valueOf(i).equals(accept)) {
                //return e;
                allMatchingElements.add(e);
                continue;
            }
            //this are not the elements you are looking for
        }
        if (allMatchingElements.isEmpty()) {
            return Collections.emptyList();
        }
        if (allMatchingElements.size() > 1) {
            throw new XBXPathExprNotAllowedForWriting(stepNode, "Ambigous step expression. I can not decide which path to follow. Please add more predicates.");
        }
        return allMatchingElements;
    }

    /**
     * @param e
     * @param childName
     * @return
     */
    private boolean elementNameMatches(final Element e, final String childName) {
        if ((childName == null) || (childName.isEmpty())) {
            throw new IllegalArgumentException("You tried to find an elment without a name. How did you get this through the parser?");
        }
        if (!needNS(childName)) {
            return childName.equals(e.getNodeName());
        }
        String url = namespaceURI(childName);
        if ((url != null) && (!url.equals(e.getNamespaceURI()))) {
            return false;
        }

        return childName.equals(e.getTagName());
    }

    /**
     * @param element
     * @param name
     * @return attribute with name or null
     */
    private Attr getAttributeNodeByName(final Element element, final String name) {
        return (needNS(name)) ? element.getAttributeNodeNS(namespaceURI(name), name) : element.getAttributeNode(name);
    }

//    /**
//     * @param name
//     * @return
//     */
//    private Object prefix(final String name) {
//        int i = name.indexOf(":");
//        if (i < 0) {
//            return null;
//        }
//        String prefix = name.substring(0, i);
//        return prefix;
//    }

    /**
     * @param name
     * @return
     */
    private boolean needNS(final String name) {
        return name.contains(":") || "xmlns".equals(name);
    }

    /**
     * @param element
     * @param childName
     */
    private void findChildElementsByName(final Element element, final String childName, final List<? super Element> result) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node n = nodeList.item(i);
            if (Node.ELEMENT_NODE != n.getNodeType()) {
                continue;
            }
            Element e = (Element) n;
            if (!elementNameMatches(e, childName)) {
                continue;
            }
            result.add(e);
        }
    }

    private Object resolveVariable(final SimpleNode node, final Node data) {
        final QName name = (QName) node.firstChildAccept(new EvaluatePredicateListVisitor(), data);
        if (variableResolver == null) {
            throw new XBPathParsingException("Variable '" + name + "' used, but no resolver defined.", 1, node.getStartColumn(), node.getEndColumn(), 1);
        }
        Object value = variableResolver.resolveVariable(name);
        if (value != null) {
            return value;
        }
        throw new XBPathParsingException("Variable '" + name + "' has no value.", 1, node.getStartColumn(), node.getEndColumn(), 1);
    }
}
