package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

public interface INodeEvaluationVisitor extends XParserVisitor {
    @Override
    public org.w3c.dom.Node visit(SimpleNode node, org.w3c.dom.Node data);
}
