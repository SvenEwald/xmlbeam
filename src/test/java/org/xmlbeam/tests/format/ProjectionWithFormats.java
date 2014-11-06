package org.xmlbeam.tests.format;

import java.util.Date;

import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public interface ProjectionWithFormats {

    //   static DateFormat dateFormat = new SimpleDateFormat("yyyy");

//   static Date asDate(final String string) {
//        return new Date();
//    }

//    @XBRead("/foo/bar/date using %YYYYMMDD")
//    Date getDate();

    //@XBRead("/foo/bar/date (:YYYYMMDD:)")
    @XBRead("(:YYYY:)/foo/bar/date")
    Date getDate();

//    @XBRead("/some/date using DateTimeFormatter.ofPattern(\"yyyy MM dd\")")
//    Date getOtherDate(DateFormat format);
}
