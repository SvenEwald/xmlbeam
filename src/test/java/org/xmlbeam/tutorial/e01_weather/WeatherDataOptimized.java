/**
 *  Copyright 2014 Sven Ewald
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
import java.util.List;

import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tutorial.e01_weather.WeatherDataOptimized.WeatherData.Forecast;

/**
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@SuppressWarnings("javadoc")
public class WeatherDataOptimized {

    @XBDocURL("http://weather.service.msn.com/data.aspx?search&weasearchstr={0}")
    public interface WeatherData {

        @XBRead("/weatherdata/weather/current/@temperature")
        int getTemperature();

        @XBRead("/weatherdata/weather/@degreetype")
        String getDegreeType();

        @XBRead("/weatherdata/weather/current/@skytext")
        String getSkytext();

        interface Forecast {
            @XBRead("./@low")
            int getLow();

            @XBRead("./@high")
            int getHigh();

            @XBRead("./@day")
            String getDay();

            @XBRead("./@skytextday")
            String getSkytext();
        }

        @XBRead("/weatherdata/weather/forecast")
        List<Forecast> getForecasts();
    }

    public static void main(final String... args) throws IOException {
        WeatherData weatherData = new XBProjector().io().fromURLAnnotation(WeatherData.class, "monschau,%20DEU&weadegreetype=C&src=outlook");
        System.out.println(weatherData.getSkytext() + ", " + weatherData.getTemperature() + "°" + weatherData.getDegreeType());
        for (Forecast f : weatherData.getForecasts()) {
            System.out.println(f.getDay() + " " + f.getLow() + "/" + f.getHigh() + " " + f.getSkytext());
        }
    }

}
