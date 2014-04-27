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
package org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser;

import org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser.Node;
import org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser.Token;
import org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser.XParser;
import org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser.XParserTreeConstants;
import org.xmlbeam.util.intern.prepocessor.org.w3c.xqparser.XParserVisitor;

// ONLY EDIT THIS FILE IN THE GRAMMAR ROOT DIRECTORY!
// THE ONE IN THE ${spec}-src DIRECTORY IS A COPY!!!
public class SimpleNode implements Node {
    protected Node parent;

    protected Node[] children;

    protected int id;

    protected XParser parser;
    
    public int beginLine, beginColumn, endLine, endColumn;

    public SimpleNode(int i) {
        id = i;
    }

    public SimpleNode(XParser p, int i) {
        this(i);
        parser = p;
    }

    // Factory method
    public static Node jjtCreate(XParser p, int id) {
        return new SimpleNode(p, id);
    }

    public void jjtOpen() {
        beginLine = parser.token.beginLine;
        beginColumn = parser.token.beginColumn;
    }

    public void jjtClose() {
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }

    public void jjtSetParent(Node n) {
        parent = n;
    }

    public void jjtSetChildren(Node[] n) {
        children = n;
    }

    public Node jjtGetParent() {
        return parent;
    }

    public void jjtAddChild(Node n, int i) {
        if (id == XParserTreeConstants.JJTNCNAME
                && ((SimpleNode) n).id == XParserTreeConstants.JJTQNAME) {
            m_value = ((SimpleNode) n).m_value;
            if (m_value.indexOf(':') >= 0)
                throw new PostParseException(
                        "Parse Error: NCName can not contain ':'!");
            return;
        }
        // Don't expose the functionQName as a child of a QName!
        else if (id == XParserTreeConstants.JJTQNAME
                && ((SimpleNode) n).id == XParserTreeConstants.JJTFUNCTIONQNAME) {
            m_value = ((SimpleNode) n).m_value;
            return;
        }
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    public Node jjtGetChild(int i) {
        return children[i];
    }

    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /** Accept the visitor. * */
    public Object jjtAccept(XParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /** Accept the visitor. * */
    public Object childrenAccept(XParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                children[i].jjtAccept(visitor, data);
            }
        }
        return data;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */

    public String toString() {
        return XParserTreeConstants.jjtNodeName[id];
    }

    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its
     * children.
     */

    public void dump(String prefix) {
        dump(prefix, System.out);
    }

    public void dump(String prefix, java.io.PrintStream ps) {
        ps.print(toString(prefix));
        printValue(ps);
        ps.print(" ["+(beginLine+1)+":"+beginColumn+" - "+endLine+":"+endColumn+"]");
        ps.println();
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    n.dump(prefix + "   ", ps);
                }
            }
        }
    }

    // Manually inserted code begins here

    protected String m_value;

    public void processToken(Token t) {
        m_value = t.image;
    }

    public void processValue(String val) {
        m_value = val;
    }

    public void printValue(java.io.PrintStream ps) {
        if (null != m_value)
            ps.print(" " + m_value);
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String m_value) {
        this.m_value = m_value;
    }

    private Object _userValue;

    protected Object getUserValue() {
        return _userValue;
    }

    protected void setUserValue(Object userValue) {
        _userValue = userValue;
    }

}

