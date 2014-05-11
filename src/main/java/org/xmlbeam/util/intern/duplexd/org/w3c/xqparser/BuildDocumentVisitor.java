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

import static org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserTreeConstants.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;
import org.xmlbeam.util.intern.duplex.XBPathParsingException;

/**
 */
public class BuildDocumentVisitor implements XParserVisitor {

    private static class FindNameTestVisitor implements XParserVisitor {

        String name;
        boolean isAttribute;
        
        @Override
        public Object visit(SimpleNode node, Object data) {
            switch (node.getID()) {
            case JJTABBREVFORWARDSTEP:
                this.isAttribute = "@".equals(node.getValue());
                return node.childrenAccept(this, data);
            case JJTNODETEST : 
                return node.childrenAccept(this, data);
            case JJTNAMETEST :
                return node.childrenAccept(this, data);
            case JJTQNAME:
                this.name=node.getValue();
                return data;
            default:            
                throw new XBXPathExprNotAllowedForWriting(node,"Not expeced here.");
            }
        }
        
    }
    private static class ApplyPredicatesVisitor implements XParserVisitor {

        public Object visit(SimpleNode node, Object data ) {
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
                Object first = node.jjtGetChild(0).jjtAccept(this, data);
                if (!(first instanceof Node)) {
                    throw new XBXPathExprNotAllowedForWriting(node, "A nonwritable predicate"); 
                }
                Object second = node.jjtGetChild(1).jjtAccept(this, data);
                ((Node)first).setTextContent(second.toString());
                return data;
            case JJTSTEPEXPR:
                return node.jjtAccept(new BuildDocumentVisitor(), data);
            case JJTSTRINGLITERAL:
                return node.getValue();
            default:            
                throw new XBXPathExprNotAllowedForWriting(node,"Not expetced here.");
            }
        }
        
    }
    
    private static class EvaluatePredicateListVisitor implements XParserVisitor {
        private boolean isMatch=true;
        @Override
        public Object visit(SimpleNode node, Object data ) {
            switch (node.getID()) {
            case JJTPREDICATELIST:
                return node.childrenAccept(this, data);
            case JJTPREDICATE:
                return node.childrenAccept(this, data);
            case JJTEXPR:
                return node.childrenAccept(this, data);
            case JJTCOMPARISONEXPR:
                Object first = node.jjtGetChild(0).jjtAccept(this, data);
                Object second = node.jjtGetChild(1).jjtAccept(this, data);
                if (!compare(node,first,second)) {
                    isMatch=false;
                }
                return data;
            default:            
                throw new XBXPathExprNotAllowedForWriting(node,"Not expeced here.");
            }
        }

        /**
         * @param value
         * @param first
         * @param second
         * @return
         */
        private boolean compare(SimpleNode value, Object first, Object second) {
            switch (value.getValue().charAt(0)) {
            case '=' : 
                return toString(first).equals(toString(second));
                default:
                    throw new XBXPathExprNotAllowedForWriting(value, "Operator "+value.getValue()+" not implemented");
            }
        }

        /**
         * @param first
         * @return
         */
        private String toString(Object o) {
            if (o instanceof Node) {
                return ((Node)o).getTextContent();
            }
            return o==null ? "<null>" : o.toString();
        }

        public boolean matches() {
            return isMatch;
        }
    }
    
  //  private final FindNameTestVisitor NAME_TEST = new FindNameTestVisitor();
    
    

    @Override
    public Object visit(SimpleNode node, Object data) {
        switch (node.getID()) {
        case JJTSTART:
            return node.childrenAccept(this, data);
        case JJTXPATH:
            return node.childrenAccept(this, data);
        case JJTEXPR:
            return node.childrenAccept(this, data);
        case JJTPATHEXPR:
            return node.childrenAccept(this, data);
        case JJTSLASHSLASH:
            throw new XBXPathExprNotAllowedForWriting(node,"Ambiguous locator");
        case JJTSLASH:
            return DOMHelper.getOwnerDocumentFor((Node) data);
        case JJTSTEPEXPR:            
            FindNameTestVisitor nameTest=new FindNameTestVisitor();
            node.jjtGetChild(0).jjtAccept(nameTest, data);
            String childName = nameTest.name;
            boolean isAttribute = nameTest.isAttribute;            
            if (isAttribute) {
                assert ((Node) data).getNodeType() == Node.ELEMENT_NODE;
                Attr attributeNode = ((org.w3c.dom.Element)data).getAttributeNode(childName);
                if (attributeNode!=null) {
                    return attributeNode;
                }
                Attr newAttribute = DOMHelper.getOwnerDocumentFor((Node) data).createAttribute(childName);
                return ((org.w3c.dom.Element)data).appendChild(newAttribute);
            }
            Node nextNode = findFirstMatchingChildElement((Node) data,childName,node.getFirstChildWithId(JJTPREDICATELIST));            
            if (nextNode==null) {
                return createChildElement((Node) data,childName,node.getFirstChildWithId(JJTPREDICATELIST));
            }            
            return nextNode; 
        default:            
            throw new XBXPathExprNotAllowedForWriting(node,"Not implemented");
        }
    }


    /**
     * @param data
     * @param childName
     * @param firstChildWithId
     * @return
     */
    private Element createChildElement(Node data, String childName, SimpleNode predicateList) {
        Document document = DOMHelper.getOwnerDocumentFor(data);
        Element newElement = document.createElement(childName);
        data.appendChild(newElement);
        ApplyPredicatesVisitor applyPredicatesVisitor = new ApplyPredicatesVisitor();
        predicateList.jjtAccept(applyPredicatesVisitor, data);
        return newElement;
    }


    /**
     * @param data
     * @param childName
     * @param firstChildWithId
     * @return
     */
    private Element findFirstMatchingChildElement(Node data, String childName, SimpleNode predicateList) {
        if (data instanceof Document) {
            final Element root= ((Document)data).getDocumentElement();
            if (root==null) {
                return null;
            }
            if (!root.getNodeName().equals(childName)) {
                return null;
            }
            EvaluatePredicateListVisitor predicateVisitor = new EvaluatePredicateListVisitor();
            predicateList.childrenAccept(predicateVisitor, root);
            if (predicateVisitor.matches()) {
                return root;
            }
            return null;
        }
        NodeList nodeList =((Element) data).getElementsByTagName(childName);
        for (int i=0; i< nodeList.getLength();++i) {
            Element e = (Element) nodeList.item(i);
            EvaluatePredicateListVisitor predicateVisitor = new EvaluatePredicateListVisitor();
            predicateList.childrenAccept(predicateVisitor, e);
            if (predicateVisitor.matches()) {
                return e;
            }
        }
        return null;
    }

}
