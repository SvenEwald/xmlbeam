package org.xmlbeam.refcards.namespaces;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

@XBDocURL("resource://nstest.xml")
public interface NameSpaceProjection {

    @XBRead("//table")
    String getTable();

    @XBRead("//h:table")
    String getNamepsacedTable();

    @XBRead("//xbdefaultns:table")
    String getDefaultNamepsacedTable();

}
