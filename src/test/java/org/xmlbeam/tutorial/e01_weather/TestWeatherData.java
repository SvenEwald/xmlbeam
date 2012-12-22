package org.xmlbeam.tutorial.e01_weather;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;

/**
 * This test demonstrates simple reading and printing of live weather data.
 * Please see projection interface {@link WeatherData} for further description.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
public class TestWeatherData {

	@Test
	public void getWeatherData() throws SAXException, IOException,
			ParserConfigurationException {
		try {
			printWeatherData("Monschau,DE");
		} catch (IOException e) {
			// As this is more an example than a unit test, drop it when no
			// Internet connection is available.
			// Maybe set your proxy via -Dhttp.proxyHost=myproxyserver.com
			// -Dhttp.proxyPort=80
			e.printStackTrace();
		}
	}

	private void printWeatherData(String location) throws SAXException,
			IOException, ParserConfigurationException {
		String BaseURL = "http://weather.service.msn.com/find.aspx?outputview=search&weasearchstr=";
		WeatherData weatherData = new XMLProjector().readFromURI(BaseURL
				+ location, WeatherData.class);
		System.out.println("The weather in " + weatherData.getLocation() + ":");
		System.out.println(weatherData.getSkytext());
		System.out.println("Temperature: " + weatherData.getTemperature() + "°"
				+ weatherData.getDegreeType());
		System.out.println("The place is located at "
				+ weatherData.getCoordinates().getLatitude() + ","
				+ weatherData.getCoordinates().getLongitude());
	}

}
