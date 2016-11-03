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

import java.util.Map;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.tests.projectedList.TestProjectedValues.Projection.Entry;
import org.xmlbeam.types.Projected;
import org.xmlbeam.types.ProjectedList;

/**
 *
 */
public class TestProjectedValues {
    private final XBProjector projector=new XBProjector(Flags.TO_STRING_RENDERS_XML);

    interface Projection {
        interface Entry{
            @XBRead("./@key") 
            Projected<String> key();
            
            @XBRead("./@value") 
            Projected<String> value();                       
      }
        
        @XBRead("/root/mid/entry")
        ProjectedList<Entry> mapRootList();
               
    }
    
    @Test
    public void testSubProjections() {        
        Projection projection = projector.projectEmptyDocument(Projection.class);
        Entry entry = projector.projectEmptyElement("entry", Entry.class);
        entry.key().set("key");
        entry.value().set("value");
        entry.value().remove();
        
        
        
        System.out.println(projection);
    }
}
