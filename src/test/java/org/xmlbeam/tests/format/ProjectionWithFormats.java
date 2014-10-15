package org.xmlbeam.tests.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface ProjectionWithFormats {

    static DateFormat format = new SimpleDateFormat("%Y");

    @XBRead(value = "/foo/bar/date", format = "%YYYYMMDD")
    Date getDate();

    @XBRead("/some/date")
    Date getOtherDate(DateFormat format);
}
