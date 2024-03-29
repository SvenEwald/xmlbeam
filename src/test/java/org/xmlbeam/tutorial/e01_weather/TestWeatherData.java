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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;
import org.xmlbeam.tutorial.e01_weather.WeatherData.Coordinates;

/**
 * This test demonstrates simple reading and printing of live weather data. Please see projection
 * interface {@link WeatherData} for further description.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@Category(Tutorial.class)
@SuppressWarnings("javadoc")
public class TestWeatherData extends TutorialTestCase {

    @Test
    public void getWeatherData() throws SAXException, IOException, ParserConfigurationException {
        try {
            printWeatherData("Monschau");
        } catch (IOException e) {
            // As this is more an example than a unit test. Drop the exception when no
            // Internet connection is available.
            // Maybe set your proxy via -Dhttp.proxyHost=myproxyserver.com
            // -Dhttp.proxyPort=80
            e.printStackTrace();
        }
    }

// 

/* START SNIPPET: TutorialDescription
~~
 This example should give you a first impression of the basic XMLBeam features:
 
 * Accessing XML attributes with automatic type conversion.
  
 * Sub projections grouping data

END SNIPPET: TutorialDescription */
// START SNIPPET: WeatherDataCode
private void printWeatherData(String location) throws IOException {
    
    // The weather service was discontinued. Changed to read from file.
    //final String BaseURL = "http://weather.service.msn.com/find.aspx?outputview=search&weasearchstr=";
    final String fileURL = "resource://WeatherData.xml";
    
    // We let the projector fetch the data for us
    WeatherData weatherData = new XBProjector().io().url(fileURL).read(WeatherData.class);
    
    // Print some values
    System.out.println("The weather in " + weatherData.getLocation() + ":");
    System.out.println(weatherData.getSkytext());
    System.out.println("Temperature: " + weatherData.getTemperature() + "°"
                                       + weatherData.getDegreeType());
    
    // Access our sub projection
    Coordinates coordinates = weatherData.getCoordinates();
    System.out.println("The place is located at " + coordinates.getLatitude() + ","
                                                  + coordinates.getLongitude());
 }
// END SNIPPET: WeatherDataCode
}
