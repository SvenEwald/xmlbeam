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
package org.xmlbeam.tests.autovalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDelete;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.types.XBAutoMap;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestAutoListOnEmptyDocuments {

    private final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

    interface Projection {
        @XBRead("/root/entry")
        XBAutoList<String> mapRootList();

        @XBDelete("/root/entry[text()='{0}']")
        Projection deleteEntry(String entry);
    }

    @Test
    public void testCreateElementsViaProjectedList() {
        Projection projection = projector.projectEmptyDocument(Projection.class);
        List<String> list = projection.mapRootList();
        assertTrue(list.isEmpty());
        list.add("a");
        list.add(0, "b");
        projection.deleteEntry("a");
        list.add(0, "c");
        Collections.sort(list);
        assertEquals("[b, c]", list.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyList() {
        XBAutoMap<String> map = projector.autoMapEmptyDocument(String.class);
        XBAutoList<String> list = map.getList("non/existing/path");
        assertTrue(list.isEmpty());
        list.get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyList2() {
        XBAutoMap<String> map = projector.onXMLString("<root/>").evalXPath("/foo").asMapOf(String.class);
        XBAutoList<String> list = map.getList("non/existing/path");
        assertTrue(list.isEmpty());
        list.get(0);
    }
}
