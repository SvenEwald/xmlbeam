/*
 * Copyright 2025 Sven Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlbeam.tutorial.e18_postalCodeRetrieval;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;

/* START SNIPPET: TutorialDescription
~~
 This example demonstrates using XMLBeam without a projection interface.
 
 * Using direct evaluation API
  
 * Retrieve data from multiple address components without iterating manually through elements

END SNIPPET: TutorialDescription */

@SuppressWarnings("javadoc")
@Category(Tutorial.class)
public class RetrievePostalCodeTest extends TutorialTestCase{
    @Test
// START SNIPPET: RetrievePostalCode    
    public void postalCodeRetrieval() {
        String searchURL="http://maps.google.com/maps/api/geocode/xml?address=Infinite%20Loop,%20Cupertino,%20CA%2095014,%20USA%22";
        String pathToElement="/GeocodeResponse/result/address_component[type='postal_code']/long_name";

        long postalCode = new XBProjector().io().url(searchURL).evalXPath(pathToElement).as(Long.TYPE);

        assertEquals(95014L,postalCode);
    }
// END SNIPPET: RetrievePostalCode    
}

