/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.tests.doc;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestRailRoadDiag {

    @Test
    public void testNodeURLS() throws IOException {
        //String rootNodeId = new XBProjector(Flags.TO_STRING_RENDERS_XML).io().url("res://XBProjector-Sheet-compact.graphml").evalXPath("").asString();
        GraphML graph = new XBProjector(Flags.TO_STRING_RENDERS_XML).io().fromURLAnnotation(GraphML.class);
        Node rootNode = graph.getRootNode();
        System.out.println(rootNode.getID() + ":" + rootNode.getLabel() + " url:" + rootNode.getURL());
        for (String childId : graph.getChildrenOf(rootNode.getID())) {
            System.out.println(childId + ":" + graph.getNode(childId).getLabel() + " url:" + graph.getNode(childId).getURL());
        }
    }
}
