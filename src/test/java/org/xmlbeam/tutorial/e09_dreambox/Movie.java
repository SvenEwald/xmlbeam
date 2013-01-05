package org.xmlbeam.tutorial.e09_dreambox;

import org.xmlbeam.Xpath;

public interface Movie {

    @Xpath("child::e2servicereference")
    String getServiceReference();

    @Xpath("child::e2time")
    long getTime();

    @Xpath("child::e2length")
    long getLength();

    @Xpath("child:e2filesize")
    long getFileSize();

    @Xpath("child::e2title")
    String getTitle();

    @Xpath("child::e2description")
    String getDescription();

    @Xpath("child::e2descriptionextended")
    String getDescriptionExtended();

    @Xpath("child::e2tags")
    String getTags();

}
