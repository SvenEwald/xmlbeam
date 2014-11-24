/*
Copyright (c) 2005 W3C(r) (http://www.w3.org/) (MIT (http://www.lcs.mit.edu/),
INRIA (http://www.inria.fr/), Keio (http://www.keio.ac.jp/)),
All Rights Reserved.
See http://www.w3.org/Consortium/Legal/ipr-notice-20000612#Copyright.
W3C liability
(http://www.w3.org/Consortium/Legal/ipr-notice-20000612#Legal_Disclaimer),
trademark
(http://www.w3.org/Consortium/Legal/ipr-notice-20000612#W3C_Trademarks),
document use
(http://www.w3.org/Consortium/Legal/copyright-documents-19990405),
and software licensing rules
(http://www.w3.org/Consortium/Legal/copyright-software-19980720)
apply.
 */
package org.xmlbeam.util.intern.duplex;

import java.util.List;

import org.xmlbeam.util.intern.DOMHelper;

class SimpleNode implements Node {

    /**
     * Sometimes it's useful to not process all steps.
     *
     * @author sven
     */
    public interface StepListFilter {

        /**
         * @param children
         * @return filtered list of child nodes.
         */
        List<SimpleNode> filter(SimpleNode[] children);
    };

    private Node parent;

    private SimpleNode[] children;

    final int id;

    private final XParser parser;

    public int beginLine, beginColumn, endLine, endColumn;

    public SimpleNode(final XParser p, final int i) {
        id = i;
        parser = p;
    }

    // Factory method
//    public static Node jjtCreate(final XParser p, final int id) {
//        return new SimpleNode(p, id);
//    }

    @Override
    public void jjtOpen() {
        beginLine = parser.token.beginLine;
        beginColumn = parser.token.beginColumn;
    }

    @Override
    public void jjtClose() {
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }

    @Override
    public void jjtSetParent(final Node n) {
        parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(final Node n, final int i) {
        if ((id == XParserTreeConstants.JJTNCNAME) && (((SimpleNode) n).id == XParserTreeConstants.JJTQNAME)) {
            m_value = ((SimpleNode) n).m_value;
            if (m_value.indexOf(':') >= 0) {
                throw new PostParseException("Parse Error: NCName can not contain ':'!");
            }
            return;
        }
        // Don't expose the functionQName as a child of a QName!
        else if ((id == XParserTreeConstants.JJTQNAME) && (((SimpleNode) n).id == XParserTreeConstants.JJTFUNCTIONQNAME)) {
            m_value = ((SimpleNode) n).m_value;
            return;
        }
        if (children == null) {
            children = new SimpleNode[i + 1];
        } else if (i >= children.length) {
            SimpleNode c[] = new SimpleNode[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = (SimpleNode) n;
    }

    @Override
    public Node jjtGetChild(final int i) {
        return children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /** Accept the visitor. * */
    @Override
    public Object jjtAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        return visitor.visit(this, data);
    }

    /*
     * public List<org.w3c.dom.Node> allChildrenAccept(final INodeEvaluationVisitor visitor, final
     * org.w3c.dom.Node target) { org.w3c.dom.Node result = target; for (SimpleNode child :
     * children) { result = ((List<org.w3c.dom.Node>) child.jjtAccept(visitor, result)).get(0); }
     * return DOMHelper.asList(result); }
     */
    /*
     * public org.w3c.dom.Node allButNotLastChildrenAccept(final INodeEvaluationVisitor visitor,
     * final org.w3c.dom.Node target) { org.w3c.dom.Node result = target; for (int i = 0; i <
     * (children.length - 1); ++i) { result = (org.w3c.dom.Node) children[i].jjtAccept(visitor,
     * result); } return result; }
     */
    /**
     * Accept the visitor. *
     *
     * @param visitor
     * @param data
     * @return evaluation result: Boolean, List, Number or Node
     **/
    public Object childrenAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        org.w3c.dom.Node result = data;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                Object newResult = (children[i].jjtAccept(visitor, result));
                if (Boolean.FALSE.equals(newResult)) { // Boolean end early exit
                    return Boolean.FALSE;
                }
                if (Boolean.TRUE.equals(newResult)) { // No early exit yet
                    if ((i + 1) == children.length) {
                        return Boolean.TRUE;
                    }
                    continue;
                }
                if (newResult instanceof List) {
                    newResult = ((List<?>) newResult).isEmpty() ? null : ((List<?>) newResult).get(0);
                }
                if (newResult instanceof Number) {
                    return newResult;
                }
                result = (org.w3c.dom.Node) newResult; // proceed step expression
            }
        }
        return DOMHelper.asList(result);
    }

    public Object firstChildAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        assert children.length > 0 : "No child found for node " + this;
        return children[0].jjtAccept(visitor, data);
    }

    public Object secondChildAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        assert children.length > 1 : "No second child found for node " + this;
        return children[1].jjtAccept(visitor, data);
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to customize the way the node
     * appears when the tree is dumped. If your output uses more than one line you should override
     * toString(String), otherwise overriding toString() is probably all you need to do.
     */

    @Override
    public String toString() {
        return XParserTreeConstants.jjtNodeName[id];
    }

    public String toString(final String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its children.
     */

    public void dump(final String prefix) {
        dump(prefix, System.out);
    }

    public void dump(final String prefix, final java.io.PrintStream ps) {
        ps.print(toString(prefix));
        printValue(ps);
        ps.print(" [" + firstToken.beginLine + ":" + firstToken.beginColumn + "-" + lastToken.endLine + ":" + lastToken.endColumn + "]");
        ps.println();
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = children[i];
                if (n != null) {
                    n.dump(prefix + "   ", ps);
                }
            }
        }
    }

    // Manually inserted code begins here

    private String m_value;

    private Token firstToken;

    private Token lastToken;

    public void processToken(final Token t) {
        m_value = t.image;
    }

    public void processValue(final String val) {
        m_value = val;
    }

    public void printValue(final java.io.PrintStream ps) {
        if (null != m_value) {
            ps.print(" \"" + m_value + "\"");
        }
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(final String m_value) {
        this.m_value = m_value;
    }

//    private Object _userValue;
//
//    protected Object getUserValue() {
//        return _userValue;
//    }
//
//    protected void setUserValue(final Object userValue) {
//        _userValue = userValue;
//    }

    public int getID() {
        return id;
    }

    /*
     * public Object childrenFilteredAccept(final XParserVisitor visitor, final int filterID, final
     * org.w3c.dom.Node data) { org.w3c.dom.Node result = data; if (children != null) { for (int i =
     * 0; i < children.length; ++i) { if (children[i].getID() == filterID) { result =
     * (org.w3c.dom.Node) children[i].jjtAccept(visitor, result); } } } return result; }
     */
    public SimpleNode getFirstChildWithId(final int id) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                if (children[i].getID() == id) {
                    return children[i];
                }
            }
        }
        return null;
    }

    public <T> T firstChildAccept(final INodeEvaluationVisitor<T> visitor, final org.w3c.dom.Node data) {
        if (children.length < 1) {
            throw new IllegalArgumentException("Node " + this + " is supposed to have children");
        }
        return visitor.visit(children[0], data);
    }

    public <T> T lastChildAccept(final INodeEvaluationVisitor<T> visitor, final org.w3c.dom.Node data) {
        if (children.length < 1) {
            throw new IllegalArgumentException("Node " + this + " is supposed to have children");
        }
        return visitor.visit(children[children.length - 1], data);
    }

    /**
     * @param visitorClosure
     * @param data
     */
    public void eachDirectChild(final org.xmlbeam.util.intern.duplex.INodeEvaluationVisitor.VisitorClosure visitorClosure, final org.w3c.dom.Node data) {
        if (children == null) {
            return;
        }
        for (SimpleNode child : children) {
            visitorClosure.apply(child, data);
        }
    }

    public void eachChild(final org.xmlbeam.util.intern.duplex.INodeEvaluationVisitor.VisitorClosure visitorClosure, final org.w3c.dom.Node data) {
        if (children == null) {
            return;
        }
        for (SimpleNode child : children) {
            visitorClosure.apply(child, data);
            child.eachChild(visitorClosure, data);
        }
    }

    /**
     * @param visitor
     * @param data
     * @param stepListFilter
     * @return a List<Node> containing the selection results
     */
    @SuppressWarnings("unchecked")
    public List<org.w3c.dom.Node> childrenAcceptWithFilter(final XParserVisitor visitor, final org.w3c.dom.Node data, final StepListFilter stepListFilter) {
        if (stepListFilter == null) {
            return (List<org.w3c.dom.Node>) childrenAccept(visitor, data);
        }
        List<SimpleNode> filteredChildren = stepListFilter.filter(children);
        org.w3c.dom.Node result = data;
        for (Node child : filteredChildren) {
            Object newResult = (child.jjtAccept(visitor, result));
            if (newResult instanceof List) {
                newResult = ((List<?>) newResult).isEmpty() ? null : ((List<?>) newResult).get(0);
            }
            result = (org.w3c.dom.Node) newResult; // proceed step expression
        }
        return DOMHelper.asList(result);
    }

    /**
     * @param token
     */
    void jjtSetFirstToken(final Token token) {
        this.firstToken = token;
    }

    /**
     * @param token
     */
    void jjtSetLastToken(final Token token) {
        this.lastToken = token;
    }

    public int getStartColumn() {
        return firstToken.beginColumn - 1;
    }

    public int getEndColumn() {
        return lastToken.endColumn - 1;
    }
}
