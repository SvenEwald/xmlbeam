/**
 *  Copyright 2017 Sven Ewald
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.testutils.DOMDiagnoseHelper;
import org.xmlbeam.types.XBAutoMap;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestAutoMaps {
    XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);

    @SuppressWarnings("deprecation")
    @Test
    public void testMapKeyPresent() {
        XBAutoMap<String> map = projector.onXMLString("<root><value>foo</value></root>").createMapOf(String.class);
        assertFalse(map.containsKey("/root/value/notthere"));
        assertTrue(map.containsKey("/root/value"));
        assertTrue(map.containsKey((Object) "/root/value"));
        assertTrue(map.containsValue("foo"));
        assertFalse(map.containsValue("bar"));
        assertFalse(map.containsValue(null));
    }

    @Test
    public void ensureDOMAccessImplementation() {
        XBAutoMap<String> map = projector.onXMLString("<root><value>foo</value></root>").createMapOf(String.class);
        DOMAccess da = ((DOMAccess) map);
        assertEquals(XBAutoMap.class, da.getProjectionInterface());
        DOMDiagnoseHelper.assertXMLStringsEquals("<root><value>foo</value></root>", da.asString());
        assertEquals(Node.DOCUMENT_NODE, da.getDOMNode().getNodeType());
        assertTrue(da.getDOMNode() == da.getDOMOwnerDocument());
        assertNotNull(da.getDOMBaseElement());
    }
}
