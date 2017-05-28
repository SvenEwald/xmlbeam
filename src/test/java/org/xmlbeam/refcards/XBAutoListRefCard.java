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

import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBAuto;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.XBAutoList;

@SuppressWarnings({ "javadoc", "unused" })
public class XBAutoListRefCard {

    Projection projection = new XBProjector().projectXMLString("<root><entries><entry/><entry/><entry/><entry/><entry/><entry/></entries></root>", Projection.class);

  //START SNIPPET: ProjectedListRefCardExample1
    public interface Projection {
        @XBRead("/root/entries/entry")
        XBAutoList<String> entries();
    }
    {
        XBAutoList<String> entries = projection.entries();

        // get third value of sequence
        String string = entries.get(2);

        // set third value of sequence;
        entries.set(2, "entry value");

        // append a new value
        entries.add("new value");

        //remove first 5 entries
        entries.subList(0, 5).clear();
    }
    //END SNIPPET: ProjectedListRefCardExample1

    //START SNIPPET: ProjectedListRefCardExample
    public interface Example {

        @XBAuto("/xml/list/entry")
        List<String> entries();

    }
    //END SNIPPET: ProjectedListRefCardExample

    public interface Projection2 {
        //START SNIPPET: ProjectedListRefCardExample2
        @XBAuto("/root/entries/entry")
        List<String> entries();
        //END SNIPPET: ProjectedListRefCardExample2
    }

    private static final String XML = "<xml>\n  <list>\n    <entry>foo</entry>\n    <entry>bar</entry>›\n    <entry>something</entry>\n  </list>\n</xml>\n";

    @Test
    public void autolistdemo() {
        Example example = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectXMLString(XML, Example.class);
    //START SNIPPET: ProjectedListRefCardExample3
       // Remove the first two entries
       example.entries().subList(0, 2).clear();
       // Add a new entry
       example.entries().add("New Entry");
    //END SNIPPET: ProjectedListRefCardExample3
        System.out.println(example);
    }
}
