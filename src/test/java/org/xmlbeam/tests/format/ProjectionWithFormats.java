/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xmlbeam.tests.format;

import java.util.Date;

import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBValue;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
//START SNIPPET: ProjectionWithFormats
public interface ProjectionWithFormats {

//START SNIPPET: ProjectionWithFormats1

    @XBRead("/foo/bar/date using yyyyMMdd")
    Date getDate();

//END SNIPPET: ProjectionWithFormats1
//START SNIPPET: ProjectionWithFormats2

    @XBWrite("/foo/bar/date using yyyyMMdd")
    void setDate(Date date);

//END SNIPPET: ProjectionWithFormats2
//START SNIPPET: ProjectionWithFormats3

    @XBRead("/foo[@date=$PARAM0(:using MMdd:)]/bar")
    String getBar(Date birthdate);

//END SNIPPET: ProjectionWithFormats3
//START SNIPPET: ProjectionWithFormats4

    @XBRead("/foo[@date=$BIRHDATE(:using MMdd:)]/bar")
    String getBar2(Date birthdate);

//END SNIPPET: ProjectionWithFormats4
    @XBRead("/foo[@date=$PARAM0(:MMdd:)]/bar")
    String getBar3(Date birthdate);

    @XBWrite("/foo[@date=$PARAM0(:using MMdd:)]/bar")
    void setBar(Date birthdate, @XBValue String value);

    @XBWrite("/foo[@date=$BIRHDATE(:using MMdd:)]/bar")
    void setBar2(Date birthdate, @XBValue String value);

    @XBWrite("/foo[@date=$PARAM0(:MMdd:)]/bar")
    void setBar3(Date birthdate, @XBValue String value);

    @XBRead("/foo/bar/date(:using yyyyMMdd:)")
    Date getDate2();

    @XBWrite("/foo/bar/date(:using yyyyMMdd:)")
    void setDate2(Date date);

}
//END SNIPPET: ProjectionWithFormats