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
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.testutils.DOMDiagnoseHelper;
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.types.XBAutoMap;

@SuppressWarnings({ "javadoc", "unused" })
public class XBAutoMapRefCardTest {

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

    @Test
    public void testMapXMLFileToAutoMap() throws IOException {
        XBAutoMap<String> map = new XBProjector().onXMLString("<xml/>").createMapOf(String.class);
        map.put("/xml/foo", "bar");

        DOMDiagnoseHelper.assertXMLStringsEquals("<xml><foo>bar</foo></xml>", ((DOMAccess) map).asString());
    }

    @Test
    public void testMapToListSimpleAccess() {
        XBAutoMap<Integer> map = new XBProjector(Flags.TO_STRING_RENDERS_XML).onXMLString("<root><list><entry>1</entry><entry>2</entry><entry>3</entry></list></root>").createMapOf(Integer.class);
        XBAutoList<String> list = map.getList("/root/list/entry", String.class);
        assertEquals("[1, 2, 3]", list.toString());
    }

    @Test
    public void testMapToListSimpleAccessDeep() {
        XBAutoMap<Integer> map = new XBProjector(Flags.TO_STRING_RENDERS_XML).onXMLString("<root><list><entry>1</entry><entry>2</entry><entry>3</entry></list></root>").evalXPath("/root").asMapOf(Integer.class);
        XBAutoList<String> list = map.getList("./list/entry", String.class);
        assertEquals("[1, 2, 3]", list.toString());
    }

    @Test
    public void testMapToListAccessTwoLists() {
        XBAutoMap<Integer> map = new XBProjector(Flags.TO_STRING_RENDERS_XML).onXMLString("<root><list1><entry>1</entry><entry>2</entry><entry>3</entry></list1><list2><entry>4</entry><entry>5</entry><entry>6</entry></list2></root>").evalXPath("/root").asMapOf(Integer.class);
        XBAutoList<String> list1 = map.getList("/root/list1/entry", String.class);
        assertEquals("[1, 2, 3]", list1.toString());
        XBAutoList<String> list2 = map.getList("list2/entry", String.class);
        assertEquals("[4, 5, 6]", list2.toString());
    }

    @Test
    public void testMapToListAccessUnionOfTwoLists() {
        XBAutoMap<Integer> map = new XBProjector(Flags.TO_STRING_RENDERS_XML).onXMLString("<root><list1><entry>1</entry><entry>2</entry><entry>3</entry></list1><list2><entry>4</entry><entry>5</entry><entry>6</entry></list2></root>").evalXPath("/root").asMapOf(Integer.class);
        XBAutoList<String> list3 = map.getList("//entry", String.class);
        assertEquals("[1, 2, 3, 4, 5, 6]", list3.toString());
    }
}
