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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.util.IOHelper;

/**
 * Tests to ensure the function of toString(), equals() and hashCode() for projections.
 * 
 * @author sven
 */
public class TestObjectInvoker {

    public interface A {
    };

    public interface B {
    };

    @Test
    public void testToString() throws IOException {
        DefaultXMLFactoriesConfig config = new DefaultXMLFactoriesConfig().setOmitXMLDeclaration(false);
        XMLBeamTestSuite testSuite = new XBProjector(config).io().fromURLAnnotation(XMLBeamTestSuite.class);
        String orig = IOHelper.inputStreamToString(TestObjectInvoker.class.getResourceAsStream(XMLBeamTestSuite.class.getAnnotation(XBDocURL.class).value().substring("resource://".length())), "UTF-8");
        assertEquals(orig.replaceAll("\\s", ""), new XBProjector(config).asString(testSuite).replaceAll("\\s", ""));
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

    @Test
    public void testDifferentInstanceOrigin() {
        assertFalse(new A() {
        }.equals(new XBProjector().projectEmptyDocument(A.class)));
        assertFalse(Integer.valueOf(new A() {
        }.hashCode()).equals(new XBProjector().projectEmptyDocument(A.class).hashCode()));
        
        assertFalse(new XBProjector().projectEmptyDocument(A.class).equals(new A() {
        }));
        assertFalse(Integer.valueOf(new XBProjector().projectEmptyDocument(A.class).hashCode()).equals(new A() {
        }.hashCode()));
    }
    
    @Test
    public void testSameDocumentDifferentProjections() {
        A a = new XBProjector().projectXMLString("<foo/>", A.class);
        B b = new XBProjector().projectXMLString("<foo/>", B.class);
        assertFalse(a.equals(b));
    }

    @Test
    public void testEmptyProjectionsEqualButNotSame() {
        assertEquals(new XBProjector().projectEmptyDocument(A.class), new XBProjector().projectEmptyDocument(A.class));
        assertEquals(new XBProjector().projectEmptyDocument(A.class).hashCode(), new XBProjector().projectEmptyDocument(A.class).hashCode());
        assertNotSame(new XBProjector().projectEmptyDocument(A.class), new XBProjector().projectEmptyDocument(A.class));

        assertNotSame(new XBProjector().projectEmptyElement("foo", A.class), new XBProjector().projectEmptyElement("foo", A.class));
        assertEquals(new XBProjector().projectEmptyElement("foo", A.class), new XBProjector().projectEmptyElement("foo", A.class));
        assertEquals(new XBProjector().projectEmptyElement("foo", A.class).hashCode(), new XBProjector().projectEmptyElement("foo", A.class).hashCode());
    }
}
