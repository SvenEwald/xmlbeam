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

import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.JJTSTART;
import static org.xmlbeam.util.intern.duplex.XParserTreeConstants.*;

import org.w3c.dom.Node;

/**
 * @author se
 *
 */
public class GetExpressionFormatPatternVisitor implements XParserVisitor {

    @Override
    public Object visit(SimpleNode node, Node data) {
        switch (node.getID()) {
        case JJTSTART:
            return node.childrenAccept(this, data);
        case JJTEXPRESSIONFORMAT:
            return node.getValue();
        default:
            return null;
        }
    }
}
