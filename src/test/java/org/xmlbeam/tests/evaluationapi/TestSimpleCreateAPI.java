/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.tests.evaluationapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.dom.DOMAccess;

public class TestSimpleCreateAPI {       

    @Test
    public void testCreateViaDOMAccess() {
        DOMAccess projection = new XBProjector().projectEmptyDocument(DOMAccess.class);
        assertSame(projection,projection.create("/foo/bar/@value", "123"));
        assertEquals(new XBProjector().projectXMLString("<foo><bar value=\"123\"/></foo>", DOMAccess.class).asString(), projection.asString());
        System.out.println(projection.asString());
    }
    
}
