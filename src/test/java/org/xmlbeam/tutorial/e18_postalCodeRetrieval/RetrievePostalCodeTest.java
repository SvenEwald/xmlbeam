/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
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

