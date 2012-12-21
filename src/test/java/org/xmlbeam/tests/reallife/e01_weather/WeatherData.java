package org.xmlbeam.tests.reallife.e01_weather;

import org.xmlbeam.Xpath;

/**
 * Most basic real life example. A good start to get an impression of the
 * capabilities of a projection. We demonstrate access to attributes, automatic
 * type conversion to primitives as integer and double and even a projection to
 * a structured sub-projection.
 * 
 * @author sven
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

	interface Location {
		@Xpath("@lon")
		double getLongitude();

		@Xpath("@lat")
		double getLatitude();
	}

	@Xpath("/weatherdata/weather")
	Location getCoordinates();
}
