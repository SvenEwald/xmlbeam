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

import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.CommandList;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.DOMCommand;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserTreeConstants;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserVisitor;

/**
 * @author sven
 */
public class StepBuilderVisitor implements XParserVisitor {

    @Override
    public CommandList visit(final SimpleNode node, final CommandList cmdlist) {
        switch (node.getID()) {
        case XParserTreeConstants.JJTSTART:
            return node.childrenAccept(this, cmdlist);
        case XParserTreeConstants.JJTXPATH:
            return node.childrenAccept(this, cmdlist);
        case XParserTreeConstants.JJTEXPR:
            return node.childrenAccept(this, cmdlist);
        case XParserTreeConstants.JJTPATHEXPR:
            return node.childrenAccept(this, cmdlist);
        case XParserTreeConstants.JJTSLASHSLASH:
            throw new XBPathParsingException("Step // not useable for write opreations yet. ", node.beginLine, node.beginColumn, node.endColumn);
        case XParserTreeConstants.JJTSLASH:
            return cmdlist.add(DOMCommand.MOVE_CURSOR_TO_DOCUMENT);
        case XParserTreeConstants.JJTSTEPEXPR:
            return visitStepExpr(node, cmdlist);
        default:
            throw new XBPathParsingException("Node '" + node.toString() + "' not yet implemented.", node.beginLine, node.beginColumn, node.endColumn);
        }
    }

    /**
     * @param node
     * @param data
     * @return
     */
    private CommandList visitStepExpr(final SimpleNode node, final CommandList data) {
        FindByTypeVisitor byTypeVisitor = new FindByTypeVisitor(XParserTreeConstants.JJTNAMETEST);
        node.childrenAccept(byTypeVisitor, null);
        if (byTypeVisitor.getNode() == null) {
            throw new XBPathParsingException("Step node '" + node + "' has no nodetest.", node.beginLine, node.beginColumn, node.endColumn);
        }
        return data;
    }
}
