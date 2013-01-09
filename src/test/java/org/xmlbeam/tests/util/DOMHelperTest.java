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
package org.xmlbeam.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlbeam.util.DOMHelper;

public class DOMHelperTest {

    private Document document;
    
    @Before
    public void init() throws Exception {
        document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();        
    }
    
    @Test
    public void testElementCreationByPath() {
        Element third = DOMHelper.ensureElementExists(document, "/root/second/third");
        assertEquals("third",third.getNodeName());
        assertEquals("second",third.getParentNode().getNodeName());
        assertEquals("root",third.getParentNode().getParentNode().getNodeName());
        assertEquals("root", document.getDocumentElement().getNodeName());
                
        Element rootElement = document.getDocumentElement();        
        Element forth = DOMHelper.ensureElementExists(document, "/root/forth");
        assertEquals("forth",forth.getNodeName());
        assertSame(rootElement, forth.getParentNode());        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testElementCreationBoundaries() {
        DOMHelper.ensureElementExists(document, "/");
    }
}
