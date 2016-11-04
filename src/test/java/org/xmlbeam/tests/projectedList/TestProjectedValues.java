/**
 *  Copyright 2016 Sven Ewald
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
package org.xmlbeam.tests.projectedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.Projected;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestProjectedValues {
    private final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

    interface EntryWithAttributes {
        @XBRead("./@key")
        Projected<String> key();

        @XBRead("./@value")
        Projected<String> value();
    }

    interface EntryWithSubelements {
        @XBRead("./key")
        Projected<String> key();

        @XBRead("./value")
        Projected<String> value();
    }

    
    @Test
    public void testProjecetedAttributes() {
        EntryWithAttributes entry = projector.projectEmptyElement("entry", EntryWithAttributes.class);
        assertEquals("<entry/>", entry.toString().trim());
        entry.key().set("key");
        assertEquals("<entry key=\"key\"/>", entry.toString().trim());
        entry.value().set("value");
        assertEquals("<entry key=\"key\" value=\"value\"/>", entry.toString().trim());
        entry.value().remove();
        assertEquals("<entry key=\"key\"/>", entry.toString().trim());
        assertTrue(entry.key().isPresent());
        entry.key().rename("huhu");
        assertEquals("<entry huhu=\"key\"/>", entry.toString().trim());
        assertFalse(entry.value().isPresent());
        assertFalse(entry.key().isPresent());
    }

    @Test
    public void testProjecetedElements() {
        EntryWithSubelements entry = projector.projectEmptyElement("entry", EntryWithSubelements.class);
        assertEquals("<entry/>", entry.toString().trim());
        entry.key().set("key");
        assertEquals("<entry><key>key</key></entry>", entry.toString().replaceAll("\\s", ""));
        entry.value().set("value");
        assertEquals("<entry><key>key</key><value>value</value></entry>", entry.toString().replaceAll("\\s", ""));
        entry.value().remove();
        assertEquals("<entry><key>key</key></entry>", entry.toString().replaceAll("\\s", ""));
        assertTrue(entry.key().isPresent());
        entry.key().rename("huhu");
        assertEquals("<entry><huhu>key</huhu></entry>", entry.toString().replaceAll("\\s", ""));
        assertFalse(entry.value().isPresent());
        assertFalse(entry.key().isPresent());
    }
    
    @Test public void testIterator() {
        EntryWithSubelements entry = projector.projectEmptyElement("entry", EntryWithSubelements.class);
        for (String s:entry.key()) {
            s.toString();
            assertTrue(false);
        }
        Iterator<String> iterator = entry.key().iterator();
        iterator.next();
        assertFalse(iterator.hasNext());
        
    }

}
