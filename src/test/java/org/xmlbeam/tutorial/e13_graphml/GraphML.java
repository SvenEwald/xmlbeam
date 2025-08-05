/*
 * Copyright 2025 Sven Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlbeam.tutorial.e13_graphml;

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;
@SuppressWarnings("javadoc")
//START SNIPPET: GraphML
@XBDocURL("resource://GraphMLTemplate.xml")
public interface GraphML {

    @XBWrite("/graphml/graph/node[@id='{0}']")
    GraphML addNode(String id, @XBValue Node node);

    @XBWrite("/graphml/graph/edge[@id='{0}']")
    GraphML addEdge(String id, @XBValue Edge edge);

    @XBRead("//edge[@target='{0}']/@source")
    String getParentOf(String node);

    @XBRead(value="//edge[@source='{0}']/@target")
    List<String> getChildrenOf(String node);

    @XBRead("//node[@id='{0}']")
    Node getNode(String id);
    
    @XBRead(value="//node")
    List<Node> getAllNodes();
    
    @XBRead("{0}")
    String xpath(String path);
    
}
//END SNIPPET: GraphML