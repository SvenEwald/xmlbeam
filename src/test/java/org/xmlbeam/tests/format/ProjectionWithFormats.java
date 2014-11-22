package org.xmlbeam.tests.format;

import java.util.Date;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
//START SNIPPET: ProjectionWithFormats
public interface ProjectionWithFormats {

    @XBRead("/foo/bar/date using yyyyMMdd")
    Date getDate();

    @XBWrite("/foo/bar/date using yyyyMMdd")
    void setDate(Date date);

    @XBRead("/foo/bar/date(:using yyyyMMdd:)")
    Date getDate2();

    @XBWrite("/foo/bar/date(:using yyyyMMdd:)")
    void setDate2(Date date);

    @XBRead("/foo[@date=$PARAM0(:using MMdd:)]/bar")
    String getBar(Date birthdate);

    @XBRead("/foo[@date=$BIRHDATE(:using MMdd:)]/bar")
    String getBar2(Date birthdate);

    @XBWrite("/foo[@date=$PARAM0(:using MMdd:)]/bar")
    void setBar(Date birthdate, @XBValue String value);

    @XBWrite("/foo[@date=$BIRHDATE(:using MMdd:)]/bar")
    void setBar2(Date birthdate, @XBValue String value);
}
//END SNIPPET: ProjectionWithFormats