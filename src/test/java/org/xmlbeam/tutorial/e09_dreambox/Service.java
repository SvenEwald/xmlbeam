package org.xmlbeam.tutorial.e09_dreambox;

import org.xmlbeam.Xpath;

public interface Service {

    @Xpath("//e2servicereference")
    String getReference();

    @Xpath("e2servicename")
    String getName();
}
