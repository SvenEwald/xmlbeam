package org.xmlbeam.tests.doc;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface Node {

    @XBRead("./xbdefaultns:data[@key='d4']")
    String getURL();

    @XBRead("normalize-space(./xbdefaultns:data/y:ShapeNode/y:NodeLabel)")
    String getLabel();

    @XBRead("./@id")
    String getID();

}
