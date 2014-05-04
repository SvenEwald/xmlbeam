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
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.SimpleNode;
import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserVisitor;

/**
 * @author sven
 */
public class FindByTypeVisitor implements XParserVisitor {

    private final int id;
    private SimpleNode node;

    @Override
    public CommandList visit(final SimpleNode node, final CommandList data) {
        if (node.getID() == this.id) {
            this.node = node;
            return null;
        }
        return node.childrenAccept(this, data);
    }

    public FindByTypeVisitor(final int id) {
        this.id = id;
    }

    /**
     * @return the node
     */
    public SimpleNode getNode() {
        return node;
    }

}
