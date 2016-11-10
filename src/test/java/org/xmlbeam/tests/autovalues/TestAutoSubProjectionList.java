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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.omg.Messaging.SyncScopeHelper;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tests.autovalues.TestAutoStingList.Projection;
import org.xmlbeam.tests.projectionvalidation.TestProjectionValidation.E;
import org.xmlbeam.types.XBAutoValue;
import org.xmlbeam.types.XBAutoList;

/**
 * @author sven
 *
 */
public class TestAutoSubProjectionList {
    private final XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
    private final static String XML = "<root><list><e>1</e><e>2</e><e>3</e></list></root>";
    private final Projection projection = projector.projectXMLString(XML, Projection.class);
    
    interface Projection {
        
        interface E {
            @XBRead("./value/int")
            XBAutoValue<Integer> value();
        }
        
        @XBRead("/root/list/e")
        List<E> reference();
        
        @XBRead("/root/list/e")
        XBAutoList<E> projectList();

        @XBRead("/root/list2/e2")
        List<E> reference2();
        
        @XBRead("/root/list2/e2")
        XBAutoList<E> projectList2();

    }

   
    @Test
    public void testSimpleAdd() {
        List<Projection.E> list = projection.projectList();
        assertFalse(list.isEmpty());
        assertEquals("[<e>1</e>,<e>2</e>,<e>3</e>]",projection.reference().toString().replaceAll("\\s", ""));
       Projection.E e = projector.projectEmptyElement("e",Projection.E.class);
       e.value().set(17);
        list.add(e);
        System.out.println(list);
//        list.add("4");      
//        assertEquals("[1, 2, 3, 4]",projection.reference().toString());
//        list.add(0, "0");
//        assertEquals("[0, 1, 2, 3, 4]",projection.reference().toString());
//        list.add(4, "x");
//        assertEquals("[0, 1, 2, 3, x, 4]",projection.reference().toString());
//        list.add(list.size(),"z");
//        assertEquals("[0, 1, 2, 3, x, 4, z]",projection.reference().toString());
//        list.clear();
//        assertTrue(projection.reference().isEmpty());
//        assertTrue(list.isEmpty());
//        list.addAll(Arrays.asList("a","b","c"));
//        assertEquals("[a, b, c]",projection.reference().toString());
//        list.addAll(1,Arrays.asList("a2","b2","c2"));
//        assertEquals("[a, a2, b2, c2, b, c]",projection.reference().toString());
    }
    
}
