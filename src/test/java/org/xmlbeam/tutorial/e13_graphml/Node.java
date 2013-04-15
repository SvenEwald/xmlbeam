package org.xmlbeam.tutorial.e13_graphml;



import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

@XBDocURL("resource://NodeTemplate.xml")
public interface Node {
    @XBRead("/g:node")
    Node rootElement();
    
    @XBWrite("./@id")
    Node setID(String id);

    @XBWrite("./g:data/y:ShapeNode/y:Geometry/@height")
    Node setHeight(float h);

    @XBWrite("./g:data/y:ShapeNode/y:Geometry/@width")
    Node setWidth(float w);

    @XBWrite("./g:data/y:ShapeNode/y:Geometry/@x")
    Node setX(float x);

    @XBWrite("./g:data/y:ShapeNode/y:Geometry/@y")
    Node setY(float y);

    @XBWrite("./g:data/y:ShapeNode/y:NodeLabel")
    void setLabel(String string);

    @XBRead("./g:data/y:ShapeNode/y:NodeLabel")
    String getLabel();
    
    @XBRead("./@id")
    String getID();
    
    @XBRead("{0}")
    String xpath(String path);
}