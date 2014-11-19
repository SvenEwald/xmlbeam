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
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;

/**
 * @author sven
 */
public class ProjectionWithFormatsTests {

    private ProjectionWithFormats projection;

    private final static Date date = new Date(549849600000L);

    @Before
    public void init() throws IOException {
        projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(ProjectionWithFormats.class);
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
}
