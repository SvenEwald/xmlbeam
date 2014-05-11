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

import org.xmlbeam.util.intern.duplex.commands.DOMCommand;
import org.xmlbeam.util.intern.duplex.commands.EnsureChildExists;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.ByIds;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.CommandList;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.TransformingVisitor;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.ValueOfChildWithID;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserVisitor;

import static org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserTreeConstants.*;
/**
 * @author sven
 */
public class StepBuilderVisitor implements XParserVisitor {

    @Override
    public CommandList visit(final SimpleNode node, final CommandList cmdlist) {
        switch (node.getID()) {
        case JJTSTART:
            return node.childrenAccept(this, cmdlist);
        case JJTXPATH:
            return node.childrenAccept(this, cmdlist);
        case JJTEXPR:
            return node.childrenAccept(this, cmdlist);
        case JJTPATHEXPR:
            return node.childrenAccept(this, cmdlist);
        case JJTSLASHSLASH:
            throw new XBPathParsingException("Step // not useable for write opreations yet. ", node.beginLine, node.beginColumn, node.endColumn,node.endLine);
        case JJTSLASH:
            return cmdlist.add(DOMCommand.MOVE_CURSOR_TO_DOCUMENT);
        case JJTSTEPEXPR:
            return visitStepExpr(node, cmdlist);
        default:
            throw new XBPathParsingException("Node '" + node.toString() + "' not yet implemented.", node.beginLine, node.beginColumn, node.endColumn,node.endLine);
        }
    }

    /**
     * @param node
     * @param data
     * @return
     */
    private CommandList visitStepExpr(final SimpleNode node, final CommandList data) {

        TransformingVisitor<SimpleNode> findAxis = new TransformingVisitor<SimpleNode>(new ByIds(JJTFORWARDAXIS, JJTREVERSEAXIS));
        node.childrenAccept(findAxis, null);
       // final SimpleNode axisNode = findAxis.hasHit() ? findAxis.getFirstHit() : new SimpleNode(JJTFORWARDAXIS);                               
        if (findAxis.hasHit() && (findAxis.getFirstHit().getID()!=JJTFORWARDAXIS)) {
            throw new XBPathParsingException("Currently only forward axis is supported.", node.beginLine, node.beginColumn, node.endColumn,node.endLine);
        }
        TransformingVisitor<SimpleNode> findNameTest = new TransformingVisitor<SimpleNode>(new ByIds(JJTNAMETEST));
        node.childrenAccept(findNameTest, null);
        if (!findNameTest.hasHit()) {
            throw new XBPathParsingException("Step node '" + node + "' has no nodetest.", node.beginLine, node.beginColumn, node.endColumn,node.endLine);
        }        
        final SimpleNode nodeTest = findNameTest.getFirstHit();       
        //nodeTest.findChildrenById(JJTQNAME);
        final String name = nodeTest.findByVisitor(new ValueOfChildWithID(JJTQNAME));
        
        
        
        data.add(new EnsureChildExists(name));
        return data;
    }
}
