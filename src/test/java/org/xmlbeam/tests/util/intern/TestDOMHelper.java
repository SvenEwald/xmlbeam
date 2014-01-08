/**
 *  Copyright 2013 Sven Ewald
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
package org.xmlbeam.tests.util.intern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.util.intern.DOMHelper;

public class TestDOMHelper {

    private Document document;

    @Before
    public void init() throws Exception {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    public interface HelperProjection extends DOMAccess {
        @XBRead("{0}")
        HelperProjection selectXPath(String xpath);
    }
    
    @Test
    public void trivialNodeEquality() {
        assertTrue(DOMHelper.nodesAreEqual(null, null));
        assertTrue(DOMHelper.nodesAreEqual(document, document));
        assertFalse(DOMHelper.nodesAreEqual(document, null));
        assertFalse(DOMHelper.nodesAreEqual(null,document));
    }
    
    @Test
    public void testElementCreationByPath() {
        Element third = DOMHelper.ensureElementExists(document, "/root/second/third");
        assertEquals("third", third.getNodeName());
        assertEquals("second", third.getParentNode().getNodeName());
        assertEquals("root", third.getParentNode().getParentNode().getNodeName());
        assertEquals("root", document.getDocumentElement().getNodeName());

        Element rootElement = document.getDocumentElement();
        Element forth = DOMHelper.ensureElementExists(document, "/root/forth");
        assertEquals("forth", forth.getNodeName());
        assertSame(rootElement, forth.getParentNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testElementCreationBoundaries1() {
        DOMHelper.ensureElementExists(document, "/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testElementCreationBoundaries2() {
        DOMHelper.ensureElementExists(document, "");
    }
    
    @Test
    public void testElementRemoval() {
        DOMHelper.ensureElementExists(document, "/root/a/b/c/d");
        DOMHelper.ensureElementExists(document, "/root/a/b/e/f");
        DOMHelper.ensureElementExists(document, "/root/a/b2/e/f");
        HelperProjection projection = new XBProjector().projectDOMNode(document, HelperProjection.class);
        Element b = (Element) projection.selectXPath("/root/a/b").getDOMNode();
        DOMHelper.removeAllChildrenBySelector(b, "e");
        assertNull(projection.selectXPath("/root/a/b/e/f"));
        assertNull(projection.selectXPath("/root/a/b/e"));
        assertNotNull(projection.selectXPath("/root/a/b/c"));
        assertNotNull(projection.selectXPath("/root/a/b/c/d"));
        assertNotNull(projection.selectXPath("/root/a/b2/e/f"));
    }

    @Test    
    public void testElementCreationByRelativePath() {
        DOMHelper.ensureElementExists(document, "/root/a");
        HelperProjection projection = new XBProjector().projectDOMNode(document, HelperProjection.class);
        Element a = (Element) projection.selectXPath("/root/a").getDOMNode();
        assertSame(a, DOMHelper.ensureElementExists(document, a, "."));
        assertSame(a, DOMHelper.ensureElementExists(document, a, "a"));
        DOMHelper.ensureElementExists(document, a, "a/b/c/d");
        assertNotNull(projection.selectXPath("/root/a/b/c/d"));
    }
    
    @Test
    public void testElementCreationByParentPath() {
        DOMHelper.ensureElementExists(document, "/root/a");
        HelperProjection projection = new XBProjector().projectDOMNode(document, HelperProjection.class);
        Element a = (Element) projection.selectXPath("/root/a").getDOMNode();
        DOMHelper.ensureElementExists(document, a, "a/../b/c/d");
        assertNotNull(projection.selectXPath("/root/b/c/d"));
    }
    
    @Test
    public void testElementCreationByRelativeParentPath() {
        DOMHelper.ensureElementExists(document, "/root/a");
        HelperProjection projection = new XBProjector().projectDOMNode(document, HelperProjection.class);
        Element a = (Element) projection.selectXPath("/root/a").getDOMNode();
        DOMHelper.ensureElementExists(document, a, "../b/c/d");
        assertNotNull(projection.selectXPath("/root/b/c/d"));
    }
    
    @Test
    public void testNamespacedElementCreation() {                
        Element element = DOMHelper.ensureElementExists(document, "/root[@xmlns:a='huhu']/a:b");
        HelperProjection projection = new XBProjector().projectDOMNode(document, HelperProjection.class);
        // System.out.println(new XBProjector().projectDOMNode(document,
// HelperProjection.class).asString());
        assertNotNull(projection.selectXPath("/root/a:b"));
        assertNull(projection.selectXPath("/root/b"));
        assertNull(projection.selectXPath("/root/a"));
        assertEquals("b", element.getLocalName());
        assertEquals("a", element.getPrefix());
    }   
    
    @Test
    public void testPredicateElementCreation() {
        Element element = DOMHelper.ensureElementExists(document, "/root/e1/e2[@att=value]");
        assertTrue(element.hasAttribute("att"));
        assertEquals("value", element.getAttributeNode("att").getValue());
        Element element2 = DOMHelper.ensureElementExists(document, "/root/e1/e2[@att=value]");
        assertSame(element, element2);
    }
}
