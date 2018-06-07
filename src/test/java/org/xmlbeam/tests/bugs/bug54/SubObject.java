package org.xmlbeam.tests.bugs.bug54;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

public interface SubObject {

    @XBRead("./@id")
    public int getId();

    @XBWrite("./@id")
    public void setId(int id);

    @XBRead(".")
    public String getText();

    @XBWrite(".")
    public void setText(String text);

}
