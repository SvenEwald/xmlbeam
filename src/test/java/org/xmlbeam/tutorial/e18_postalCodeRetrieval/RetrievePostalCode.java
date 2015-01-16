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
public class RetrievePostalCode extends TutorialTestCase{
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

