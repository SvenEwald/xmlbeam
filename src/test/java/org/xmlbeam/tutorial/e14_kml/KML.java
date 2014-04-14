/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tutorial.e14_kml;

import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
//START SNIPPET: KML
@XBDocURL("resource://polygon-simple.kml")
public interface KML {

    @XBRead("/kml/Placemark/Polygon/outerBoundaryIs/LinearRing/coordinates")
    public CoordinateList getCoordinates();
    
    @XBWrite("/kml/Placemark/Polygon/outerBoundaryIs/LinearRing/coordinates")
    public KML setCoordinates(CoordinateList coordinates);
    
}
//END SNIPPET: KML