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
package org.xmlbeam.tests.binding;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBAutoBind;
import org.xmlbeam.types.XBAutoValue;

/**
 *
 */
@SuppressWarnings("javadoc")
public class TestBindAnnotation {

    private final Projection projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectEmptyDocument(Projection.class);

    interface Projection {

        @XBAutoBind("/root/first/second/@attr")
        XBAutoValue<String> attr();

        @XBAutoBind("/root/list/element")
        List<String> list();
    }

    @Test
    public void testProjectionBindMethod() {
        projection.attr().set("foo");
        assertEquals("<root><first><second attr=\"foo\"/></first></root>", projection.toString().replaceAll(">\\s+", ">"));
    }

    @Test
    public void testProjectionBindList() {
        List<String> list = projection.list();
        list.add("foo");
        list.add("bar");
        System.out.println(list.toString());
        
    }

}
