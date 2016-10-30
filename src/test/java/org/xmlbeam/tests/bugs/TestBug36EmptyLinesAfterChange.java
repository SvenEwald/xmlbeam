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
package org.xmlbeam.tests.bugs;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;

/**
 * This test ensures that after changing a list of elements, no empty lines in the result
 * document are created. See bug https://github.com/SvenEwald/xmlbeam/issues/36
 */
@SuppressWarnings("javadoc")
public class TestBug36EmptyLinesAfterChange {

    private final static XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    
    private final static String XML = "<root>\n<list>\n<entry>a</entry>\n<entry>b</entry>\n<entry>c</entry>\n</list>\n</root>";
    
    private interface TestProjection {
        interface Entry{
            @XBRead(".")
            String getValue();
            @XBWrite(".")
            Entry setValue(String value);
        }
        
        @XBRead("/root/list/entry")
        List<Entry> getEntries();
        @XBWrite("/root/list/entry")
        TestProjection setEntries(List<Entry> entries);
        
        @XBRead("/root/list/entry")
        List<String> getStringEntries();
        @XBWrite("/root/list/entry")
        TestProjection setStringEntries(List<String> entries);
    }
    
    @Test
    public void testNoEmptyLinesAfterChange() {
        TestProjection projection = projector.projectXMLString(XML, TestProjection.class);
       // System.out.println(projection.toString());
        List<TestProjection.Entry> entries = projection.getEntries();
        entries.remove(0);
        entries.add(projector.projectEmptyElement("entry", TestProjection.Entry.class).setValue("x"));
        projection.setEntries(entries);
       // System.out.println(projection.toString());
        ensureNoEmptyLine(projection.toString());
    }

    @Test
    public void testNoEmptyLinesAfterChange2() {
        TestProjection projection = projector.projectXMLString(XML, TestProjection.class);
       // System.out.println(projection.toString());
        List<String> entries = projection.getStringEntries();
        entries.remove(0);
        entries.add("x");
        projection.setStringEntries(entries);
       // System.out.println(projection.toString());
        ensureNoEmptyLine(projection.toString());
    }
   
    private void ensureNoEmptyLine(String string) {
        Scanner scanner = new Scanner(string).useDelimiter("\n");
        while (scanner.hasNext()) {
            String line=scanner.next();
            assertFalse(line.trim().isEmpty());
        }
    }
    
}
