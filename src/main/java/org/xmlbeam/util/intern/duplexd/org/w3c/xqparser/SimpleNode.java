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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.NodeList;
import org.xmlbeam.util.intern.DOMHelper;

// ONLY EDIT THIS FILE IN THE GRAMMAR ROOT DIRECTORY!
// THE ONE IN THE ${spec}-src DIRECTORY IS A COPY!!!
public class SimpleNode implements Node {
    protected Node parent;

    protected SimpleNode[] children;

    protected int id;

    protected XParser parser;

    public int beginLine, beginColumn, endLine, endColumn;

    public SimpleNode(final int i) {
        id = i;
    }

    public SimpleNode(final XParser p, final int i) {
        this(i);
        parser = p;
    }

    // Factory method
    public static Node jjtCreate(final XParser p, final int id) {
        return new SimpleNode(p, id);
    }

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

    public void jjtSetChildren(final SimpleNode[] n) {
        children = n;
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

    public org.w3c.dom.Node allChildrenAccept(final INodeEvaluationVisitor visitor, final org.w3c.dom.Node target) {
        org.w3c.dom.Node result = target;
        for (SimpleNode child : children) {
            result = (org.w3c.dom.Node) child.jjtAccept(visitor, result);
        }
        return result;
    }

    public org.w3c.dom.Node allButNotLastChildrenAccept(final INodeEvaluationVisitor visitor, final org.w3c.dom.Node target) {
        org.w3c.dom.Node result = target;
        for (int i = 0; i < (children.length - 1); ++i) {
            result = (org.w3c.dom.Node) children[i].jjtAccept(visitor, result);
        }
        return result;
    }

    /** Accept the visitor. * */
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
                result = (org.w3c.dom.Node) newResult; // proceed step expression
            }
        }
        return result;
    }

    public Object firstChildAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        assert children.length > 0 : "No child found for node " + this;
        return children[0].jjtAccept(visitor, data);
    }

    public Object secondChildAccept(final XParserVisitor visitor, final org.w3c.dom.Node data) {
        assert children.length > 1 : "No second child found for node " + this;
        return children[1].jjtAccept(visitor, data);
    }

    /**
     * @param results
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object unwrap(final Object results) {
        if (results instanceof NodeList) {
            return unwrap((NodeList) results);
        }
        if (!(results instanceof List)) {
            return results;
        }
        if (((List<?>) results).isEmpty()) {
            return null;
        }
        for (ListIterator<Object> i = ((List) results).listIterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof List) {
                i.set(unwrap(o));
                continue;
            }
            if (o instanceof NodeList) {
                i.set(unwrap((NodeList) o));
            }
        }
        return ((List) results).size() == 1 ? ((List) results).get(0) : results;
    }

    private Object unwrap(final NodeList o) {
        if (o.getLength() == 1) {
            return o.item(0);
        }
        return DOMHelper.asList(o);
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
        ps.print(" [" + (beginLine + 1) + ":" + beginColumn + " - " + endLine + ":" + endColumn + "]");
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

    protected String m_value;

    public void processToken(final Token t) {
        m_value = t.image;
    }

    public void processValue(final String val) {
        m_value = val;
    }

    public void printValue(final java.io.PrintStream ps) {
        if (null != m_value) {
            ps.print(" " + m_value);
        }
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(final String m_value) {
        this.m_value = m_value;
    }

    private Object _userValue;

    protected Object getUserValue() {
        return _userValue;
    }

    protected void setUserValue(final Object userValue) {
        _userValue = userValue;
    }

    public int getID() {
        return id;
    }

    public Object childrenFilteredAccept(final XParserVisitor visitor, final int filterID, final org.w3c.dom.Node data) {
        org.w3c.dom.Node result = data;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                if (children[i].getID() == filterID) {
                    result = (org.w3c.dom.Node) children[i].jjtAccept(visitor, result);
                }
            }
        }
        return result;
    }

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

    public List<SimpleNode> getChildren() {
        return Arrays.asList(children);
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
     */
    public void eachChild(final org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.INodeEvaluationVisitor.VisitorClosure visitorClosure, final org.w3c.dom.Node data) {
        for (SimpleNode child : children) {
            visitorClosure.apply(child, data);
        }
    }

//    public List<SimpleNode> findChildrenById(final int... ids) {
//        FindByPredicateVisitor<SimpleNode> v = new FindByPredicateVisitor<SimpleNode>(new ByIdsPredicate(ids));
//        childrenAccept(v, null);
//        return v.getHits();
//    }
//
//    public <T> T findByVisitor(final Transformer<T> predicate) {
//        TransformingVisitor<T> v = new TransformingVisitor<T>(predicate);
//        childrenAccept(v, null);
//        return v.getFirstHit();
//    }
}
