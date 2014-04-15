/**
 *  Copyright 2014 Sven Ewald
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

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBWrite;

@SuppressWarnings("javadoc")
public class TestElementCreationWithDotInName {

    
    public interface Projection {
        @XBWrite("/root/{0}/content")
        Projection write(String elementName);
    }
    
    @Test
    public void testDotInSetterName() {
        Projection projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(Projection.class);
        projection.write("abc.def");
        System.out.println(projection);
        //assertEquals("<root><abc.def><content/>")
    }
    
}
