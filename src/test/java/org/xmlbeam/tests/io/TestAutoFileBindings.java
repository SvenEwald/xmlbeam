/**
 *  Copyright 2017 Sven Ewald
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
package org.xmlbeam.tests.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.testutils.DOMDiagnoseHelper;
import org.xmlbeam.types.CloseableList;
import org.xmlbeam.types.CloseableMap;
import org.xmlbeam.types.CloseableValue;
import org.xmlbeam.types.XBAutoList;
import org.xmlbeam.types.XBAutoMap;

@SuppressWarnings("javadoc")
public class TestAutoFileBindings {
    XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    File file;
    private String origEOL;

    @Before
    public void createFile() throws IOException {
        origEOL = System.getProperty("line.separator");
        setEOL("\r\n");
        System.setProperty("line.separator", "\r\n");
        File tempFile = File.createTempFile(this.getClass().getSimpleName(), Long.toBinaryString(System.currentTimeMillis()));
        DOMAccess domAccess = projector.projectXMLString("<root><foo><bar>huhu</bar></foo></root>", DOMAccess.class);
        projector.io().file(tempFile).write(domAccess);
        this.file = tempFile;
    }

    @After
    public void deleteFile() {
        file.delete();
        System.setProperty("line.separator", origEOL);
        setEOL(origEOL);
    }

    /**
     * @param origEOL2
     */
    private void setEOL(String eol) {
       try {
        Field field = System.class.getDeclaredField("lineSeparator");
        field.setAccessible(true);
        field.set(null, eol);
    } catch (NoSuchFieldException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        
    }

    @Test
    public void testMapExistingFile() throws IOException {
        assertEquals(57, file.length());
        CloseableMap<String> map = projector.io().file(file).bindAsMapOf(String.class);
        assertEquals("huhu", map.get("root/foo/bar"));
        map.put("root/foo2/bar2", "huhu2");
        map.close();
        assertEquals(102, file.length());
    }

    @Test
    public void testMapExistingFileWithXpath() throws IOException {
        assertEquals(57, file.length());
        CloseableMap<String> map = projector.io().file(file).bindXPath("root").asMapOf(String.class);
        assertEquals("huhu", map.get("foo/bar"));
        map.put("foo2/bar2", "huhu2");
        map.close();
        assertEquals(102, file.length());
    }

    @Test
    public void testMapNonExistingFile() throws IOException {
        File file = File.createTempFile(this.getClass().getSimpleName(), Long.toBinaryString(System.currentTimeMillis()));
        file.delete();
        assertEquals(0, file.length());
        CloseableMap<String> map = projector.io().file(file).bindAsMapOf(String.class);
        assertTrue(map.isEmpty());
        map.put("root/foo2/bar2", "huhu2");
        map.close();
        assertEquals(62, file.length());
        file.delete();
    }

    @Test(expected = FileNotFoundException.class)
    public void testMapNonExistingFileFails() throws IOException {
        projector.io().file("doesNot.Exist").failIfNotExists().bindAsMapOf(String.class);
    }

    @Test
    public void testListExistingFile() throws IOException {
        assertEquals(57, file.length());
        CloseableList<String> list = projector.io().file(file).bindXPath("/root/foo/bar").asListOf(String.class);
        assertEquals(1, list.size());
        assertEquals("huhu", list.get(0));
        list.add("huhu2");
        list.close();
        assertEquals(79, file.length());
    }

    @Test
    public void testMapExistingFileReadOnly() throws IOException {
        assertEquals(57, file.length());
        XBAutoMap<String> map = projector.io().file(file).readAsMapOf(String.class);
        assertEquals("huhu", map.get("root/foo/bar"));
        map.put("root/foo2/bar2", "huhu2");
        if (map instanceof Closeable) {
            ((Closeable) map).close();
        }
        assertEquals(57, file.length());
    }

    @Test
    public void testvalueBindToFile() throws IOException {
        assertEquals(57, file.length());
        CloseableValue<String> value = projector.io().file(file).bindXPath("/root/foo/bar").as(String.class);
        assertEquals("huhu", value.get());
        value.set("huhu2");
        value.close();
        assertEquals(58, file.length());
    }

    @Test
    public void testMapExistingFileReadOnlyThenToList() throws IOException {
        assertEquals(57, file.length());
        XBAutoMap<String> map = projector.io().file(file).readAsMapOf(String.class);
        assertEquals("huhu", map.get("root/foo/bar"));
        XBAutoList<String> list = map.getList("root/foo/bar");
        assertEquals("[huhu]", list.toString());
        list.add("huhu2");
        System.out.println(projector.asString(map));
        DOMDiagnoseHelper.assertXMLStringsEquals("<root><foo><bar>huhu</bar><bar>huhu2</bar></foo></root>", projector.asString(map));
    }

}
