package org.xmlbeam.util.intern.duplex;

import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserVisitor;

interface INodeEvaluationVisitor<R> extends XParserVisitor {

    interface VisitorClosure {
        void apply(SimpleNode node, org.w3c.dom.Node data);;
    }

    @Override
    public R visit(SimpleNode node, org.w3c.dom.Node data);
}
