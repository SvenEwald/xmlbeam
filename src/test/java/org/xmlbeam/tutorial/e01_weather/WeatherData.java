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
