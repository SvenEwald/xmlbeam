/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
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