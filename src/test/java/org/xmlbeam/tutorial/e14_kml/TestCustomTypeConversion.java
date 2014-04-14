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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;
import org.xmlbeam.types.DefaultTypeConverter;
//START SNIPPET: Tutorial14

/* START SNIPPET: TutorialDescription
~~
 This tutorial demonstrates the usage of a custom made type converter.
 We implement a new Java List "CoordinateList" which will handle our user defined type Coordinate.
 So far this is not related to data projection, it's just the scenario build up to show how
 easy user defined typed may be integrated. The actual configuration of the projector is
 basically just one line of code.
END SNIPPET: TutorialDescription */

@SuppressWarnings({"serial","javadoc"})
//START SNIPPET: TestCustomTypeConversion
public class TestCustomTypeConversion {
   
    /**
     * This Conversion defines how String data is converted to our target type and
     * what default is to be applied when no data is available.
     */
    private final class CoordinateListConversion extends DefaultTypeConverter.Conversion<CoordinateList> {
        private CoordinateListConversion() {
            super(new CoordinateList(""));
        }

        @Override
        public CoordinateList convert(String data) {
            return new CoordinateList(data);
        }
    }

    @Test
    public void testApplyOffsetToCoordinates() throws IOException {
        XBProjector projector = new XBProjector(new DefaultXMLFactoriesConfig().setNamespacePhilosophy(NamespacePhilosophy.AGNOSTIC));        
        DefaultTypeConverter converter = new DefaultTypeConverter().setConversionForType(CoordinateList.class, new CoordinateListConversion());
        projector.config().setTypeConverter(converter);
        KML kml = projector.io().fromURLAnnotation(KML.class);
        
        // Extract the list of coordinates
        CoordinateList coordinates = kml.getCoordinates();
        
        assertTrue(coordinates.iterator().hasNext());
        
        // Apply some offset
        for (Coordinate a:coordinates) {
            a.setX(a.getX()+10);
        }
        
        // Set the list again
        kml.setCoordinates(coordinates);
    }

}
//END SNIPPET: TestCustomTypeConversion
//END SNIPPET: Tutorial14