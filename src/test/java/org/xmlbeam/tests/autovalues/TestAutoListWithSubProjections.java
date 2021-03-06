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

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.tests.autovalues.TestAutoListWithSubProjections.Projection.Entry;
import org.xmlbeam.types.XBAutoList;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestAutoListWithSubProjections {
    private final XBProjector projector=new XBProjector(Flags.TO_STRING_RENDERS_XML);

    interface Projection {
        interface Entry{
//            @XBRead("./@key") 
//            Projected<String> key();
//            
//            @XBRead("./@value") 
//            Projected<String> value();
            
           @XBWrite("./@key")
           Entry setKey(String key);
           
           @XBWrite("./@value") 
           Entry setValue(String value);
        }
        
        @XBRead("/root/mid/entry")
        XBAutoList<Entry> mapRootList();
               
    }
    
    @Test
    public void testSubProjections() {
        
        Projection projection = projector.projectEmptyDocument(Projection.class);
        Entry entry = projector.projectEmptyElement("entry", Entry.class);
//        entry.key().set("key");
//        entry.value().set("value");
        projection.mapRootList().add(entry.setKey("key").setValue("value"));
        projection.mapRootList().add(entry.setKey("key2").setValue("value2"));
        projection.mapRootList().add(entry.setKey("key3").setValue("value3"));
        projection.mapRootList().remove(entry);
        System.out.println(projection);
    }
}
