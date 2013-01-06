/**
 *  Copyright 2012 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam.tutorial.e09_dreambox;

import org.xmlbeam.XPathProjection;

public interface Event {

    @XPathProjection("//e2eventid")
    String getID();

    @XPathProjection("//e2eventstart * 1000")
    long getStart();

    @XPathProjection("//e2eventduration div 60")
    long getDurationInMinutes();

    @XPathProjection("//e2eventcurrenttime")
    long getCurrentTime();

    @XPathProjection("//e2eventtitle")
    String getTitle();

    @XPathProjection("//e2eventdescription")
    String getDescription();

    @XPathProjection("//e2eventdescriptionextended")
    String getDescriptionExtended();

    @XPathProjection("//e2eventservicereference")
    String getServiceReference();

    @XPathProjection("//e2eventservicename")
    String getServiceName();
}
