/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tutorial.e11_freemind;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.e11_freemind.MindMap.Node;

/**
 *
 */
public class DumpMindMap {

    @Test
    public void dump() throws IOException {
        MindMap mindMap = new XBProjector().io().fromURLAnnotation(MindMap.class);
        dumpNode("", mindMap.getRootNode());
    }

    /**
     * @param rootNode
     */
    private void dumpNode(String prefix, Node node) {
        System.out.println(prefix + node.getText());
        for (Node subnode : node.getSubNodes()) {
            dumpNode(prefix + "  ", subnode);
        }
    }

}
