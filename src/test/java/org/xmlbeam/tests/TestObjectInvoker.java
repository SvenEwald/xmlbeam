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
package org.xmlbeam.tests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.util.IOHelper;

/**
 * Tests to ensure the function of toString(), equals() and hashCode() for
 * projections.
 * 
 * @author sven
 * 
 */
public class TestObjectInvoker {

    @Test
    public void testToString() throws IOException {
        DefaultXMLFactoriesConfig config = new DefaultXMLFactoriesConfig().setOmitXMLDeclaration(false);
        XMLBeamTestSuite testSuite = new XBProjector(config).io().fromURLAnnotation(XMLBeamTestSuite.class);
        String orig = IOHelper.inputStreamToString(TestObjectInvoker.class.getResourceAsStream(XMLBeamTestSuite.class.getAnnotation(XBDocURL.class).value().substring("resource://".length())), "UTF-8");
        assertEquals(orig.replaceAll("\\s", ""), testSuite.toString().replaceAll("\\s", ""));
    }
    
    @Test
    public void testEqualsAndHashCode() {
        GenericXPathProjection projectionA = new XBProjector().projectXMLString("<foo a=\"b\" c=\"d\"/>", GenericXPathProjection.class);
        GenericXPathProjection projectionB = new XBProjector().projectXMLString("<foo c=\"d\" a=\"b\" ></foo>", GenericXPathProjection.class);        
        assertNotSame(projectionA,projectionB);
        assertEquals(projectionA, projectionB);
        assertEquals(projectionA.hashCode(), projectionB.hashCode());
    }
    
    @Test
    public void testNotEqualsAttributes() {
        GenericXPathProjection projectionA = new XBProjector().projectXMLString("<foo a=\"b\" c=\"d\"/>", GenericXPathProjection.class);
        GenericXPathProjection projectionB = new XBProjector().projectXMLString("<foo c=\"d\" a=\"b2\" ></foo>", GenericXPathProjection.class);        
        assertFalse(projectionA.equals(projectionB));
        assertFalse(projectionB.equals(projectionA));
        assertTrue(projectionA.hashCode()!=projectionB.hashCode());
    }
    
    @Test
    public void testNotEqualsValues() {
        GenericXPathProjection projectionA = new XBProjector().projectXMLString("<foo><bar/></foo> ", GenericXPathProjection.class);
        GenericXPathProjection projectionB = new XBProjector().projectXMLString("<foo></foo>", GenericXPathProjection.class);        
        assertFalse(projectionA.equals(projectionB));
        assertFalse(projectionB.equals(projectionA));
        assertTrue(projectionA.hashCode()!=projectionB.hashCode());
    }
    
}
