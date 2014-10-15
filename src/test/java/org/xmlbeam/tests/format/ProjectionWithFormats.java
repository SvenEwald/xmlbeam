package org.xmlbeam.tests.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface ProjectionWithFormats {

    static DateFormat dateFormat = new SimpleDateFormat("%Y");

//   static Date asDate(final String string) {
//        return new Date();
//    }

    @XBRead(value = "/foo/bar/date", format = "%YYYYMMDD")
    Date getDate();

    @XBRead("/some/date")
    Date getOtherDate(DateFormat format);
}
