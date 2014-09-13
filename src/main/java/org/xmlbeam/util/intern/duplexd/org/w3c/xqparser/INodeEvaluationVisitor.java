package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

public interface INodeEvaluationVisitor<R> extends XParserVisitor {

    public interface VisitorClosure {
        void apply(SimpleNode node, org.w3c.dom.Node data);;
    }

    @Override
    public R visit(SimpleNode node, org.w3c.dom.Node data);
}
