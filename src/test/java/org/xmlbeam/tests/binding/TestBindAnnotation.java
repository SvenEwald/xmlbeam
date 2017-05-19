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
package org.xmlbeam.tests.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omg.Messaging.SyncScopeHelper;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBAutoBind;
import org.xmlbeam.types.XBAutoMap;
import org.xmlbeam.types.XBAutoValue;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestBindAnnotation {

    private final static XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    
    private final Projection projection = projector.projectEmptyDocument(Projection.class);

    private final static String XMLFORMAP = "<root><map><element1>value1</element1><element2><element3 att1=\"attvalue1\" >value2</element3></element2></map></root>";
    private final Projection mapProjection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectXMLString(XMLFORMAP, Projection.class);

    interface Projection {

        @XBAutoBind("/root/first/second/@attr")
        XBAutoValue<String> attr();

        @XBAutoBind("/root/list/element")
        List<String> list();

        @XBAutoBind("/root/map")
        XBAutoMap<String> map();

        @XBAutoBind("/root/map")
        XBAutoMap<String> mapSubProjection();

    }

    @Test
    public void testProjectionBindMethod() {
        projection.attr().set("foo");
        assertEquals("<root><first><second attr=\"foo\"/></first></root>", projection.toString().replaceAll(">\\s+", ">"));
    }

    @Test
    public void testProjectionBindList() {
        List<String> list = projection.list();
        list.add("foo");
        list.add("bar");
      //  System.out.println(list.toString());
        assertEquals("[foo, bar]",list.toString());
    }

    @Test
    public void testProjectionBindMapCreation() {
//        System.out.println(mapProjection);
        Map<String, String> map = mapProjection.map();
        assertEquals(null,map.get("a/b/c"));
        map.put("./a/b/c", "newValue");        
//System.out.println(mapProjection);
        
        assertEquals("value1",map.get("./element1"));
        assertEquals("newValue",map.get("a/b/c"));

    }
    
    @Test
    public void testProjectionAutoMapRemove() {
        Map<String, String> map = mapProjection.map();
        assertEquals("value2", map.get("element2/element3"));
        map.remove("element2");
        assertEquals(null, map.get("element2/element3"));
    }

    @Test
    public void testProjectionBindMapEmpty() {
        // assertTrue(projection.map().isEmpty());
        //   projection.map().put("./a/b/c", "someValue);
        Map<String, String> map = mapProjection.map();
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());

//        System.out.println(mapProjection);
//        System.out.println(mapProjection.map().entrySet());
//        System.out.println(mapProjection.map().values());
    }
    
    @Test
    public void testProjectionBindMapValues() {
        Map<String, String> map = mapProjection.map();
        assertEquals("[value1, value2, attvalue1]",map.values().toString());
        map.clear();
        assertEquals("[]",map.values().toString());
        map.put("ele1/ele2/@att", "someAttValue");
        assertEquals("[./ele1/ele2/@att=someAttValue]",map.entrySet().toString());
    }
    
    @Test
    public void testProjectionAutoMapFullDocument() {
        XBAutoMap<String> map = projector.automapEmptyDocument(String.class);
        map.put("someroot/elements/element1","value1");
        map.put("someroot/elements/element2","value2");
        map.put("someroot/elements[with/subelement='oink']/element3","value3");
        map.put("someroot/elements[with/subelement='oink']/element4","value4");
        map.put("someroot/elements/element5","value5");
        System.out.println(projector.asString(map));
    }
    @Test
    public void testAmbigousPaths() {
        XBAutoMap<String> map = projector.automapEmptyDocument(String.class);
        map.put("someroot/elements[@pos='first']/sub[@pos='first']/element1","value1");
        map.put("someroot/elements[@pos='second']/sub[@pos='second']/element2","value2");
        map.put("someroot/elements[@pos='second']/sub[@pos='second']/element3","value3");
        System.out.println(projector.asString(map));
    }
    
}
