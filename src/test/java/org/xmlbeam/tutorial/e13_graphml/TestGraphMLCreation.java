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
package org.xmlbeam.tutorial.e13_graphml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;

//START SNIPPET: Tutorial13

/*START SNIPPET: TutorialDescription
~~
 This tutorial demonstrates the creation of elements via template elements.
 This is achieved by reading template documents and using this projections as
 sub projections in other template projections. The transition from document projections
 to element sub projections works just as desired. 
//END SNIPPET: TutorialDescription */

//START SNIPPET: TestGraphMLCreation
public class TestGraphMLCreation {
    
    @Test
    public void testGraphCreation() throws IOException {
        Node node = new XBProjector().io().fromURLAnnotation(Node.class).rootElement();
        node.setID("Nodeid");
        node.setLabel("NodeLabel");
        assertEquals("NodeLabel", node.getLabel());
        assertEquals("Nodeid", node.getID());
    }

    @Test
    public void testGraphNodeSetting() throws IOException {
        GraphML graph = new XBProjector().io().fromURLAnnotation(GraphML.class);
        Edge edge = new XBProjector().io().fromURLAnnotation(Edge.class).rootElement();
        Node node = new XBProjector().io().fromURLAnnotation(Node.class).rootElement();
        graph.addEdge("wutz"   , edge);
        graph.addNode("huhu", node);
    }

}

//END SNIPPET: TestGraphMLCreation
// END SNIPPET: Tutorial13
