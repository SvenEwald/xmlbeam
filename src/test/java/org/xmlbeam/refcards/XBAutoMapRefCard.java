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
package org.xmlbeam.refcards;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBAuto;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.XBAutoMap;

@SuppressWarnings({ "javadoc", "unused" })
public class XBAutoMapRefCard {

    Projection projection = new XBProjector().projectXMLString("<root><subelement attribute='' /></root>", Projection.class);

    //START SNIPPET: ProjectedMapRefCardExample
    public interface Projection {
        @XBRead("/root/foo")
        XBAutoMap<String> entries();
    }
    {
        XBAutoMap<String> map = projection.entries();

        // Read attribute of subelement below /root/foo/..
        String attributeValue = map.get("subelement/@attribute");

        // Create new elements and set value of element 'structure'
        map.put("new/sub/structure", "new value");

    }
    //END SNIPPET: ProjectedMapRefCardExample

    public interface Projection2 {
        //START SNIPPET: ProjectedMapRefCardExampleA
        @XBAuto("/root/foo")
        Map<String,String> entries();
        //END SNIPPET: ProjectedMapRefCardExampleA

    }

    private static final String XML = "<xml>\n  <entries>\n    <first>foo</first>\n    <second>bar</second>›\n    <third>something</third>\n  </entries>\n</xml>\n";

    @Test
    public void automapdemo() {

        if (true) {
            return;
        }
        Projection example = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectXMLString(XML, Projection.class);

        assertEquals("foo", example.entries().get("first"));
        assertEquals("bar", example.entries().get("second"));
        assertEquals("something", example.entries().get("third"));

    //START SNIPPET: ProjectedMapRefCardExample2
     // Read values
     Map<String,String> entries= example.entries();
     entries.get("first"); // returns "foo"
     entries.get("second"); // returns "bar"
     entries.get("third"); // returns "something"

     // Remove second entry
     entries.remove("second");

     // Create new entries
     entries.put("newElement", "newValue");

    //END SNIPPET: ProjectedMapRefCardExample2
        System.out.println(example.entries());
        System.out.println(example);
    }

    @Test
    public void textCreateXMLByMap() {
        //START SNIPPET: ProjectedMapRefCardExample3
        XBProjector projector = new XBProjector();
        Map<String,Integer> map = projector.autoMapEmptyDocument(Integer.class);
        map.put("/root/foo/@bar", 13);
        System.out.println(projector.asString(map));
        //END SNIPPET: ProjectedMapRefCardExample3
    }

    @Test
    public void testCreateXMLFileByMap() throws IOException {
        //START SNIPPET: ProjectedMapRefCardExample3
        CloseableMap<String> map = new XBProjector().io().file("example.xml").bindXPath("/rootpath").asMapOf(String.class);
        map.put("foo", "bar");
        map.close(); // <- writes new content to the bound file. Since Java 7, try with resources can be used.
        //END SNIPPET: ProjectedMapRefCardExample3
        assertTrue(new File("example.xml").delete());
    }

    @Test
    public void testCreateXMLFileByMap2() throws IOException {
        //START SNIPPET: ProjectedMapRefCardExample3
        CloseableMap<String> map = new XBProjector().io().file("example.xml").bindAsMapOf(String.class);
        map.put("foo", "bar");
        map.close(); // <- writes new content to the bound file. Since Java 7, try with resources can be used.
        //END SNIPPET: ProjectedMapRefCardExample3
        assertTrue(new File("example.xml").delete());
    }

    @Test
    public void testMapWithFormatPattern() {
        XBAutoMap<String> map = new XBProjector().autoMapEmptyDocument(String.class);
        map.put("/root/value", "19990102");
        Date date = map.get("/root/value using yyyymmdd", Date.class);
        assertEquals("19990102", new SimpleDateFormat("yyyymmdd").format(date));
    }
}
