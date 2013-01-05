package org.xmlbeam.tutorial.e09_dreambox;

import org.xmlbeam.Xpath;

public interface Event {

    @Xpath("//e2eventid")
    String getID();

    @Xpath("//e2eventstart * 1000")
    long getStart();

    @Xpath("//e2eventduration div 60")
    long getDurationInMinutes();

    @Xpath("//e2eventcurrenttime")
    long getCurrentTime();

    @Xpath("//e2eventtitle")
    String getTitle();

    @Xpath("//e2eventdescription")
    String getDescription();

    @Xpath("//e2eventdescriptionextended")
    String getDescriptionExtended();

    @Xpath("//e2eventservicereference")
    String getServiceReference();

    @Xpath("//e2eventservicename")
    String getServiceName();
}
