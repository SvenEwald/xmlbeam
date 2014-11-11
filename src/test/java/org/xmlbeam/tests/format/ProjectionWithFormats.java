package org.xmlbeam.tests.format;

import java.util.Date;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
public interface ProjectionWithFormats {

    //   static DateFormat dateFormat = new SimpleDateFormat("yyyy");

//   static Date asDate(final String string) {
//        return new Date();
//    }

    @XBRead("/foo/bar/date using YYYYMMDD")
    Date getDate3();
    
    @XBWrite("/foo/bar/date using YYYYMMDD")
    void writeDate3(Date date);
    
    @XBWrite("/foo/bar/date(:using YYYYMMDD:)")
    void writeDate3b(Date date);

    
    @XBWrite("/foo[date='{0 using MMDD}']/bar")
    void writeDate4(Date date,@XBValue String value);

    @XBWrite("/foo[date='{0}(:using MMDD:)']/bar")
    void writeDate4b(Date date,@XBValue String value);
   
    
    @XBWrite("/foo[date='{0(:using MMDD:)}']/bar")
    void writeDate4v(Date date,@XBValue String value);

    
    @XBWrite("/foo[date='{birthdate using MMDD}']/bar")
    void writeDate5(Date birthdate,@XBValue String value);
    
    @XBWrite("/foo[date='{birthdate}(:using MMDD:)']/bar")
    void writeDate5b(Date birthdate,@XBValue String value);


    @XBWrite("/foo[date=$PARAM0(:using MMDD:)]/bar")
    void writeDate6(Date birthdate,@XBValue String value);

    @XBWrite("/foo[date=$BIRHDATE(:using MMDD:)]/bar")
    void writeDate7(Date birthdate,@XBValue String value);

    
    //@XBRead("/foo/bar/date (:YYYYMMDD:)")
    @XBRead("(:YYYY:)/foo/bar/date")
    Date getDate();

    @XBRead("(:YYYYMMDD:)/foo/bar/date")
    Date getDate2();
    
//    @XBRead("/some/date using DateTimeFormatter.ofPattern(\"yyyy MM dd\")")
//    Date getOtherDate(DateFormat format);
}
