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
package org.xmlbeam.tutorial.e26_mondial;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tutorial.TutorialTestCase;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestMondialAccess extends TutorialTestCase {

    
    @XBDocURL("http://www.dbis.informatik.uni-goettingen.de/Mondial/mondial.xml")
    public interface Mondial {

        public interface Location {
            @XBRead("./longitude")
            float getLongitude();

            @XBRead("./latitude")
            float getLatitude();
        }

        public interface PoulatedEntity {
            @XBRead("./population")
            int getPopulation();
        }

        public interface NamedEntity {
            @XBRead("./name")
            String getName();
        }

        public interface City extends NamedEntity, PoulatedEntity, Location {

        }

        public interface Country extends NamedEntity {
            @XBRead("./city|./province/city")
            List<City> getCities();
        }

        @XBRead("/mondial/country")
        List<Country> getCountries();

        @XBRead("/mondial/*/name")
        List<String> getSubs();

        @XBRead("count(//*)")
        int getNodeCount();
    }

    @Ignore
    public void testStructure() throws IOException {
        final long start = System.currentTimeMillis();
        final Mondial mondial = new XBProjector().io().fromURLAnnotation(Mondial.class);
        int hash = 0;
        for (Mondial.Country country : mondial.getCountries()) {
            //System.out.println(""+country.getName());           
            for (Mondial.City city : country.getCities()) {
                country.getName();
                city.getName();
                city.getLongitude();
                city.getLongitude();
                city.getPopulation();

                //String s= country.getName()+": "+city.getName()+ " "+ city.getLongitude()+"/"+city.getLongitude()+" Population:"+city.getPopulation();
//               hash+=country.getName().hashCode();
//               hash+=city.getName().hashCode();
//               hash+=city.getLongitude().hashCode();
//               hash+=city.getName().hashCode();
//               hash+=city.getName().hashCode();
//               hash+=city.getName().hashCode();
//               hash+=city.getName().hashCode();
//               hash+=city.getName().hashCode();

            }

        }
        final long end = System.currentTimeMillis();
        System.out.println("Test run:" + (end - start) + "ms");
        System.out.println(mondial.getNodeCount() + "/" + hash);
    }

}
