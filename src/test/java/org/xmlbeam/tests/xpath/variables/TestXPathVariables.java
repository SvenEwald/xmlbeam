/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.tests.xpath.variables;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;

@SuppressWarnings("javadoc")
public class TestXPathVariables {

    public static String XML = "<root><value id=\"1\">first</value><value id=\"2\">second</value></root>";

    public interface Projection {
        @XBRead("//value[@id=$PARAM0]")
        String getValue(int id);
    }

    @Test
    public void testVariableSubstitution() {
        Projection projection = new XBProjector().onXMLString(XML).createProjection(Projection.class);
        long before1 = System.nanoTime();
        assertEquals("first", projection.getValue(1));
        long duration1 = System.nanoTime() - before1;
        assertEquals("second", projection.getValue(2));
        long before2 = System.nanoTime();
        assertEquals("first", projection.getValue(1));
        long duration2 = System.nanoTime() - before2;
        System.out.println("Duration1:" + duration1);
        System.out.println("Duration2:" + duration2);
        System.out.println(duration1 / duration2);

        // Assert caching the invocation context has some effect.
        //assertTrue(duration1> 2*duration2);
    }
}
