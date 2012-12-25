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

import org.xmlbeam.Xpath;

/**
 * Most basic real life example. A good start to get an impression of the
 * capabilities of a projection. We demonstrate access to attributes, automatic
 * type conversion to primitives as integer and double and even a projection to
 * a structured sub-projection.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
public interface WeatherData {

	@Xpath("/weatherdata/weather/@searchlocation")
	String getLocation();

	@Xpath("/weatherdata/weather/current/@temperature")
	int getTemperature();

	@Xpath("/weatherdata/weather/@degreetype")
	String getDegreeType();

	@Xpath("/weatherdata/weather/current/@skytext")
	String getSkytext();

	/**
	 * This would be our "sub projection". A structure grouping two attribute
	 * values in one object.
	 */
	interface Location {
		@Xpath("@lon")
		double getLongitude();

		@Xpath("@lat")
		double getLatitude();
	}

	@Xpath("/weatherdata/weather")
	Location getCoordinates();
}
