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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBAutoBind;
import org.xmlbeam.types.CloseableMap;

@SuppressWarnings({ "javadoc" })
public class XBAutoMapRefCard {

    //START SNIPPET: ProjectedMapRefCardExample
    public interface Example {

        @XBAutoBind("/xml/entries")
        Map<String,String> entries();

    }
    //END SNIPPET: ProjectedMapRefCardExample

    private static final String XML = "<xml>\n  <entries>\n    <first>foo</first>\n    <second>bar</second>›\n    <third>something</third>\n  </entries>\n</xml>\n";

    @Test
    public void automapdemo() {
        Example example = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectXMLString(XML, Example.class);

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
    public void createXMLByMap() {
        //START SNIPPET: ProjectedMapRefCardExample3
        XBProjector projector = new XBProjector();
        Map<String,Integer> map = projector.automapEmptyDocument(Integer.class);
        map.put("/root/foo/@bar", 13);        
        System.out.println(projector.asString(map));
        //END SNIPPET: ProjectedMapRefCardExample3
    }

    @Test
    public void createXMLFileByMap() throws IOException {
        //START SNIPPET: ProjectedMapRefCardExample3
        CloseableMap<String> map = new XBProjector().io().file("example.xml").bindXPath("/rootpath").asMapOf(String.class);
        map.put("foo", "bar");        
        map.close(); // <- writes new content to the bound file. Since Java 7, try with resources can be used. 
        //END SNIPPET: ProjectedMapRefCardExample3
        assertTrue(new File("example.xml").delete());
    }
    
    @Test
    public void createXMLFileByMap2() throws IOException {
        //START SNIPPET: ProjectedMapRefCardExample3
        CloseableMap<String> map = new XBProjector().io().file("example.xml").bindAsMapOf(String.class);
        map.put("foo", "bar");        
        map.close(); // <- writes new content to the bound file. Since Java 7, try with resources can be used. 
        //END SNIPPET: ProjectedMapRefCardExample3
        assertTrue(new File("example.xml").delete());
    }

    
}
