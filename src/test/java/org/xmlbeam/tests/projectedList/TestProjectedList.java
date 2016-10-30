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

import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.types.ProjectedList;

/**
 * @author sven
 */
public class TestProjectedList {

    private final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    private final static String XML = "<root><list><e>1</e><e>2</e><e>3</e></list></root>";

    interface Projection {

        @XBRead("/root/list/e")
        ProjectedList<String> projectList();

        @XBRead("/root/list2/e2")
        ProjectedList<String> projectList2();

    }

    @Test
    public void testAdd() {
        Projection projection = projector.projectXMLString(XML, Projection.class);
        List<String> list = projection.projectList();
        list.add("4");
        System.out.println(projection);
    }
    
    @Test
    public void testAdd2() {
        Projection projection = projector.projectXMLString(XML, Projection.class);
        List<String> list = projection.projectList2();
        list.add("4");
        System.out.println(projection);
    }

}
