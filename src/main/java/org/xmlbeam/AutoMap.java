/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.xmlbeam.evaluation.InvocationContext;
import org.xmlbeam.intern.DOMChangeListener;
import org.xmlbeam.types.XBAutoMap;

/**
 * @author sven
 */
public class AutoMap<T> extends AbstractMap<String, T> implements XBAutoMap<T>, DOMChangeListener {

    private final InvocationContext invocationContext;
    private final Node baseNode;

    /**
     * @param baseNode
     * @param invocationContext
     */
    public AutoMap(final Node baseNode, final InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
        this.baseNode = baseNode;
        this.invocationContext.getProjector().addDOMChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(final String key, final T value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.xmlbeam.intern.DOMChangeListener#domChanged()
     */
    @Override
    public void domChanged() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        collectChildren(baseNode, ".");
        return Collections.emptySet();
    }

    private static void collectChildren(final Node n, final String path) {
        // ((Document)n).()
    }

}
