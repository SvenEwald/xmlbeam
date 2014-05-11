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
package org.xmlbeam.util.intern.duplex.org.w3c.xqparser;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xmlbeam.util.intern.duplex.org.w3c.xqparser.XParserVisitor;

/**
 * @author sven
 */
public class TransformingVisitor<T> implements XParserVisitor, Iterable<T> {

    private List<T> nodes = new LinkedList<T>();
    private final Transformer<T> transformer;
    
    @Override
    public CommandList visit(final SimpleNode node, final CommandList data) {
        T result = transformer.transform(node);
        if (result!=null) {
            nodes.add(result);
        }
        return node.childrenAccept(this, data);
    }

    public TransformingVisitor(final Transformer<T> transformer) {
        this.transformer = transformer;
        
    }

    /**
     * @return the node
     */
    public T getFirstHit() {
        return nodes.get(0);
    }
        

    public boolean hasHit() {
        return !nodes.isEmpty();
    }
   
    @Override
    public Iterator<T> iterator() {
        return nodes.iterator();
    }

    /**
     * @return
     */
    public List<T> getHits() {
        return Collections.unmodifiableList(nodes);
    }
}
