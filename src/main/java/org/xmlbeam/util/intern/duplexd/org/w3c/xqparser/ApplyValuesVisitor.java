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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTPATHEXPR;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserTreeConstants.JJTXPATH;

import java.util.Collection;
import java.util.List;

import org.w3c.dom.Node;
import org.xmlbeam.util.intern.duplexd.org.w3c.xqparser.XParserVisitor;

/**
 *
 */
public class ApplyValuesVisitor implements XParserVisitor {

    private final Collection<?> values;

    public ApplyValuesVisitor(final Collection<?> values) {
        this.values = values;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        assert data instanceof Node;
        switch (node.getID()) {
        case JJTSTART:
            return node.childrenAccept(this, data);
        case JJTXPATH:
            return node.childrenAccept(this, data);
        case JJTEXPR:
            return node.childrenAccept(this, data);
        case JJTPATHEXPR:
            List<Node> changedNodes = (List<Node>) node.childrenAccept(this, data);
            return Integer.valueOf(changedNodes.size());
            
        default:
            throw new XBXPathExprNotAllowedForWriting(node, "Not implemented");
        }
    }

}
