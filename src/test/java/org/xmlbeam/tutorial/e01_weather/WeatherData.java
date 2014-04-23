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
package org.xmlbeam.tutorial.e01_weather;

import org.xmlbeam.annotation.XBRead;

/**
 * Most basic real life example. A good start to get an impression of the
 * capabilities of a projection. We demonstrate access to attributes, automatic
 * type conversion to primitives as integer and double and even a projection to
 * a structured sub-projection.
 * 
 * The unit test will get some weather data like this:
 * <weatherdata>
 *     <weather alert="" degreetype="F" entityid="8172902" imagerelativeurl="http://blu.stc.s-msn.com/as/wea3/i/en-us/" isregion="False" lat="50.5520210266113" lon="6.24060010910034" provider="Foreca" region="" searchdistance="0" searchlocation="Monschau, Stadt Aachen, NW, Germany" searchresult="Monschau, North Rhine-Westphalia, Germany" searchscore="0.95" url="http://local.msn.com/worldweather.aspx?eid=8172902&amp;q=Monschau-DEU" weatherfullname="Monschau, North Rhine-Westphalia, Germany" weatherlocationcode="wc:8172902" weatherlocationname="Monschau, DEU" zipcode="">
 *         <current skycode="31" skytext="Clear" temperature="46"/><
 *     /weather>
 * </weatherdata>
 * 
 * This interface will be used to access a selection of values from the xml data.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@SuppressWarnings("javadoc")
// START SNIPPET: WeatherDataInterface
public interface WeatherData {

    @XBRead("/weatherdata/weather/@searchlocation")    
    String getLocation();

    @XBRead("/weatherdata/weather/current/@temperature")
    int getTemperature();

    @XBRead("/weatherdata/weather/@degreetype")
    String getDegreeType();

    @XBRead("/weatherdata/weather/current/@skytext")
    String getSkytext();

    /**
     * This would be our "sub projection". A structure grouping two attribute
     * values in one object.
     */
    interface Coordinates {
        @XBRead("@lon")
        double getLongitude();

        @XBRead("@lat")
        double getLatitude();
    }

    @XBRead("/weatherdata/weather")
    Coordinates getCoordinates();
}
// END SNIPPET: WeatherDataInterface