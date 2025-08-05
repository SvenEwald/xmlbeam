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



import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
@SuppressWarnings("javadoc")
//START SNIPPET: Node
@XBDocURL("resource://NodeTemplate.xml")
public interface Node {
    @XBRead("/node")
    Node rootElement();
    
    @XBWrite("./@id")
    Node setID(String id);

    @XBWrite("./data/y:ShapeNode/y:Geometry/@height")
    Node setHeight(float h);

    @XBWrite("./data/y:ShapeNode/y:Geometry/@width")
    Node setWidth(float w);

    @XBWrite("./data/y:ShapeNode/y:Geometry/@x")
    Node setX(float x);

    @XBWrite("./data/y:ShapeNode/y:Geometry/@y")
    Node setY(float y);

    @XBWrite("./data/y:ShapeNode/y:NodeLabel")
    void setLabel(String string);

    @XBRead("./data/y:ShapeNode/y:NodeLabel")
    String getLabel();
    
    @XBRead("./@id")
    String getID();
    
    @XBRead("{0}")
    String xpath(String path);
}
//END SNIPPET: Edge