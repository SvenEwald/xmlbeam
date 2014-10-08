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
package org.xmlbeam.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.dom.DOMAccess;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class TestDOMAccess {

    @XBDocURL("res://testsuite.xml")
    public interface Projection {
        @XBRead("//item[.='{0}']")
        Node getItem(String string);

        @XBRead("//item")
        List<Node> getAllItems();

        @XBWrite("/gluerootnode/intermediate/fourthElement[1]/innerStructureB/*")
        void setOneItem(Node newItem);

        @XBWrite("/gluerootnode/intermediate/fourthElement[1]/innerStructureB/*")
        void setItems(List<Node> nodes);

    }

    private Projection projection;

    @Before
    public void prepare() throws IOException {
        projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).io().fromURLAnnotation(Projection.class);
    }

    @Test
    public void ensureDOMAccessCast() {
        assertTrue(projection instanceof DOMAccess);
    }

    @Test
    public void testBasicDOMAccess() {
        Node node = projection.getItem("A");
        assertEquals(Node.ELEMENT_NODE, node.getNodeType());
        assertEquals("A", node.getTextContent());
    }

    @Test
    public void testMultiDOMAccess() {
        List<Node> items = projection.getAllItems();
        assertEquals(6, items.size());
    }

    @Test
    public void testWriteSingleNode() {
        Node node = projection.getItem("A").cloneNode(true);
        node.setTextContent("foo");
        projection.setOneItem(node);
        assertNull(projection.getItem("A"));
        assertNull(projection.getItem("B"));
    }

    @Test
    public void testWriteMultileNodes() {
        List<Node> nodes = projection.getAllItems();
        projection.setItems(nodes);
        assertEquals(9, projection.getAllItems().size());
    }

}
