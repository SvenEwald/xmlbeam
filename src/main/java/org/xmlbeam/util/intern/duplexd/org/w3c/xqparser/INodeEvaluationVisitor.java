package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

interface INodeEvaluationVisitor<R> extends XParserVisitor {

    interface VisitorClosure {
        void apply(SimpleNode node, org.w3c.dom.Node data);;
    }

    @Override
    public R visit(SimpleNode node, org.w3c.dom.Node data);
}
