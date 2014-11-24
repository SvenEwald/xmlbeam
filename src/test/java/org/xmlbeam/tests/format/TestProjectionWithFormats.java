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
package org.xmlbeam.tests.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestProjectionWithFormats {

    private ProjectionWithFormats projection;

    private final static Date date = new Date(549849600000L);

    @Before
    public void init() throws IOException {
        final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        projector.config().as(DefaultXMLFactoriesConfig.class).setPrettyPrinting(false);
        projection = projector.projectEmptyDocument(ProjectionWithFormats.class);
    }

    @Test
    public void testVariant1() {
        projection.setDate(date);
        assertTrue(projection.toString().contains("<date>19870605</date>"));
        assertEquals(date, projection.getDate());
    }

    @Test
    public void testVariant2() {
        projection.setDate2(date);
        assertTrue(projection.toString().contains("<date>19870605</date>"));
        assertEquals(date, projection.getDate2());
    }

    @Test
    public void testFormatInVariable() {
        projection.setBar(date, "foobar");
        assertEquals("<foo date=\"0605\"><bar>foobar</bar></foo>", projection.toString());
        assertEquals("foobar", projection.getBar(date));
    }

    @Ignore
    // needs Java8
    public void testFormatInVariableVariant2() {
        projection.setBar2(date, "foobar");
        assertEquals("<foo date=\"0605\"><bar>foobar</bar></foo>", projection.toString());
        assertEquals("foobar", projection.getBar2(date));
    }
    
    @Test
    public void testFormatInVariableVariant3() {
        projection.setBar(date, "foobar");
        assertEquals("<foo date=\"0605\"><bar>foobar</bar></foo>", projection.toString());
        assertEquals("foobar", projection.getBar(date));
    }
}
