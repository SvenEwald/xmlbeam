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

import java.util.List;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;

//START SNIPPET: Tutorial09
/* START SNIPPET: TutorialDescription
~~
 This tutorial introduces the concept of "external projections". You may specify a document
 origin per projection method. This way a projection may project data of multiple documents into
 one Java object. As example we take the XML interfaces of a Dreambox.
 A Dreambox is a hard disk video recorder with a web based interface that works with XML data
 exchange from multiple URLs. This example is provided to show a real life use case for
 integration of multiple external documents to one Java interface. You are surly not able to run
 this example without access to specific version of this hard disk recorder. Therefore there is no
 example code provided which uses this interface.
    
//END SNIPPET: TutorialDescription */


/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a> 
 */
//START SNIPPET: Dreambox
public interface Dreambox {

    // This is the root XBDocURL of my device.
    final static String BASE_URL = "http://192.168.1.44/web";

    @XBDocURL(BASE_URL + "/movielist?dirname={0}")
    @XBRead(value = "/e2movielist/e2movie")
    List<Movie> getMovies(String location);

    @XBDocURL(BASE_URL + "/getservices?sRef=1%3A7%3A1%3A0%3A0%3A0%3A0%3A0%3A0%3A0%3AFROM%20BOUQUET%20%22userbouquet.favourites.tv%22%20ORDER%20BY%20bouquet")
    @XBRead(value = "/e2servicelist/e2service")
    List<Service> getServices();

    @XBDocURL(BASE_URL + "/web/epgservice?sRef={0}")
    @XBRead(value = "/e2eventlist/e2event")
    List<Event> getEvents(String serviceReference);

    @XBDocURL(BASE_URL + "/web/getlocations")
    @XBRead(value = "e2locations/e2location")
    List<String> getLocations();

}
// END SNIPPET: Dreambox
// END SNIPPET: Tutorial09
