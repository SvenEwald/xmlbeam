package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

public interface INodeEvaluationVisitor<R> extends XParserVisitor {
    @Override
    public R visit(SimpleNode node, org.w3c.dom.Node data);
}
