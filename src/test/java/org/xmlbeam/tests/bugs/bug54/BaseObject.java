package org.xmlbeam.tests.bugs.bug54;

import java.util.List;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

public interface BaseObject {

    @XBRead ("/base/@id")
    public int getId();

    @XBWrite ("/base/@id")
    public void setId(int id);

    @XBRead ("/base/color")
    public int getColor();

    @XBWrite ("/base/color")
    public void setColor(int color);

    @XBRead ("/base/foo/bar/subs/subObject")
    public List< SubObject > getSubObjects();

    @XBWrite ("/base/foo/bar/subs/subObject")
    public void setSubObjects(List< SubObject > subObjects);

}
