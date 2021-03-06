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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.xmlbeam.testutils.DOMDiagnoseHelper.assertXMLStringsEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.testutils.TestIOUtils;
import org.xmlbeam.types.CloseableList;
import org.xmlbeam.types.CloseableValue;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.util.IOHelper;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestAutoStringList {

    private final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    private final static String XML = "<root><list><e>1</e><e>2</e><e>3</e></list></root>";
    private final Projection projection = projector.projectXMLString(XML, Projection.class);

    interface Projection {

        @XBRead("/root/list/e")
        List<String> reference();

        @XBRead("/root/list/e")
        XBAutoList<String> projectList();

        @XBRead("/root/list2/e2")
        List<String> reference2();

        @XBRead("/root/list2/e2")
        XBAutoList<String> projectList2();

    }

    @Test
    public void testSimpleAdd() {
        List<String> list = projection.projectList();
        assertFalse(list.isEmpty());
        assertEquals("[1, 2, 3]", projection.reference().toString());
        list.add("4");
        assertEquals("[1, 2, 3, 4]", projection.reference().toString());
        list.add(0, "0");
        assertEquals("[0, 1, 2, 3, 4]", projection.reference().toString());
        list.add(4, "x");
        assertEquals("[0, 1, 2, 3, x, 4]", projection.reference().toString());
        list.add(list.size(), "z");
        assertEquals("[0, 1, 2, 3, x, 4, z]", projection.reference().toString());
        list.clear();
        assertTrue(projection.reference().isEmpty());
        assertTrue(list.isEmpty());
        list.addAll(Arrays.asList("a", "b", "c"));
        assertEquals("[a, b, c]", projection.reference().toString());
        list.addAll(1, Arrays.asList("a2", "b2", "c2"));
        assertEquals("[a, a2, b2, c2, b, c]", projection.reference().toString());
    }

    @Test
    public void testAddForNonExistingParent() {
        List<String> list = projection.projectList2();
        assertTrue(projection.reference2().isEmpty());
        assertTrue(list.isEmpty());
        list.add("4");
        assertEquals("[4]", projection.reference2().toString());
    }

    @Test
    public void testContains() {
        assertFalse(projection.projectList().contains(null));
        assertTrue(projection.projectList().contains("1"));
        assertTrue(projection.projectList().contains("2"));
        assertTrue(projection.projectList().contains("3"));
        assertFalse(projection.projectList().contains("Y"));
        assertTrue(projection.projectList().containsAll(Arrays.asList("1", "2", "3")));
        assertFalse(projection.projectList().containsAll(Arrays.asList("1", "T", "3")));
    }

    @Test
    public void testIndexOf() {
        assertEquals(0, projection.projectList().indexOf("1"));
        assertEquals(1, projection.projectList().indexOf("2"));
        assertEquals(2, projection.projectList().indexOf("3"));
        assertEquals(-1, projection.projectList().indexOf("X"));
    }

    @Test
    public void testIndexOfNode() {
        assertEquals(-1, projection.projectList().indexOf((Node) null));
        assertEquals(-1, projection.projectList().indexOf(((DOMAccess) projection).getDOMNode()));
    }

    @Test
    public void testGet() {
        assertEquals("1", projection.projectList().get(0));
        assertEquals("2", projection.projectList().get(1));
        assertEquals("3", projection.projectList().get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFail() {
        projection.projectList().get(4);
    }

    @Test
    public void testIterator() {
        Iterator<String> iterator = projection.projectList().iterator();
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorRemove() {
        Iterator<String> iterator = projection.projectList().iterator();
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        iterator.remove();
        assertEquals("[2, 3]", projection.reference().toString());
    }

    @Test
    public void testRemoveByIndex() {
        assertEquals("2", projection.projectList().remove(1));
        assertEquals("[1, 3]", projection.reference().toString());
        assertEquals("1", projection.projectList().remove(0));
        assertEquals("[3]", projection.reference().toString());
        assertEquals("3", projection.projectList().remove(0));
        assertTrue(projection.reference().isEmpty());
    }

    @Test
    public void testRemoveByValue() {
        assertFalse(projection.projectList().remove("not there"));
        assertEquals("[1, 2, 3]", projection.reference().toString());
        assertTrue(projection.projectList().remove("2"));
        assertEquals("[1, 3]", projection.reference().toString());
    }

    @Test
    public void testEvaluationAPIForAutoList() throws IOException {
        File file = new File("bindTest.xml");
        if (file.exists()) {
            file.delete();
        }
        CloseableList<String> list = projector.io().file(file).bindXPath("/root/list/e").asListOf(String.class);
        list.clear();
        list.add("a");
        list.add("b");
        list.add("c");
        list.close();
        assertXMLStringsEquals("<root><list><e>a</e><e>b</e><e>c</e></list></root>", TestIOUtils.file2String(file));
        assertXMLStringsEquals("<list><e>a</e><e>b</e><e>c</e></list>", projector.asString(list));
        assertTrue(file.delete());
    }

    @Test
    public void testEvaluationAPIForAutoValue() throws IOException {
        final File file = new File("bindTest.xml");
        FileWriter writer = new FileWriter(file);
        writer.write("<root><list><a>1</a><b>2</b><c>true</c></list></root>");
        writer.close();

        FileInputStream fileInputStream = new FileInputStream(file);
        IOHelper.loadDocument(projector, fileInputStream);
        fileInputStream.close();

        CloseableValue<Integer> valueA = projector.io().file(file).bindXPath("/root/list/a").asInt();
        assertEquals(Integer.valueOf(1), valueA.set(14));
        valueA.close();
        assertXMLStringsEquals("<root><list><a>14</a><b>2</b><c>true</c></list></root>", TestIOUtils.file2String(file));

        CloseableValue<String> string = projector.io().file(file).bindXPath("/root/list/b").asString();
        assertEquals("2", string.get());
        string.set("foo");
        string.close();
        assertXMLStringsEquals("<root><list><a>14</a><b>foo</b><c>true</c></list></root>", TestIOUtils.file2String(file));

        CloseableValue<Boolean> bool = projector.io().file(file).bindXPath("/root/list/c").asBoolean();
        assertTrue(bool.get());
        bool.set(false);
        bool.close();
        assertXMLStringsEquals("<root><list><a>14</a><b>foo</b><c>false</c></list></root>", TestIOUtils.file2String(file));
        assertTrue(file.delete());
    }

    @Test
    public void testEvaluationAPIWithDateAndFormat() throws IOException {
        final File file = new File("bindTest.xml");
        FileWriter writer = new FileWriter(file);
        writer.write("<root><list><a>19990102</a></list></root>");
        writer.close();
        CloseableValue<Date> valueA = projector.io().file(file).bindXPath("/root/list/a using yyyymmdd").asDate();
        assertEquals(915235260000L, valueA.set(new Date(0)).getTime());
        valueA.close();
        assertXMLStringsEquals("<root><list><a>19700001</a></list></root>", TestIOUtils.file2String(file));
        assertEquals("a", valueA.getName());
    }

    @Test
    public void testEvaluationAPIWithDateAndFormatInEvalAPI() throws IOException {
        Date valueA = projector.onXMLString("<root><list><a>19990102</a></list></root>").evalXPath("/root/list/a using yyyymmdd").asDate();
        assertEquals(915235260000L, valueA.getTime());
    }
}
