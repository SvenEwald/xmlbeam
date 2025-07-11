/*
 * Copyright 2025 Sven Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    @XBRead("/foo/bar/int using $000")
    int getInt();

    @XBWrite("/foo/bar/int using $000")
    void setInt(int i);

}
//END SNIPPET: ProjectionWithFormats