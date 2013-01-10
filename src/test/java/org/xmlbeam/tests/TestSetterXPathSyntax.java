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
package org.xmlbeam.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;

public class TestSetterXPathSyntax {

    private XBProjector projector;
    private GenericXPathProjection projection;

    @Before
    public void init() throws IOException {
        projector = new XBProjector();
        projection = projector.create().createEmptyDocumentProjection(GenericXPathProjection.class);
    }

    @Test
    public void rootElementAccessAllowed() {
        projection.setterXPathProjection("/*", projector.create().createEmptyElementProjection("value", GenericXPathProjection.class));
        assertEquals("value",projector.getXMLDocForProjection(projection).getDocumentElement().getNodeName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAccessNotAllowed() {
        projection.setterXPathString("", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rootElementValueAccessAllowed() {
        projection.setterXPathString("/", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyrootElementAttributeAccessNotAllowed() {
        projection.setterXPathString("/@attribute", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyrootElementEmptyAttributeNameNotAllowed() {
        projection.setterXPathString("/@", "");
    }
    
    @Test
    public void createRootElementWithAttribute() {
        projection.setterXPathString("/newRoot/@newAttribute","value");
        assertEquals("newRoot",projector.getXMLDocForProjection(projection).getDocumentElement().getNodeName());
        assertEquals("value",projector.getXMLDocForProjection(projection).getDocumentElement().getAttribute("newAttribute"));        
    }
    
    @Test
    public void createDeeperElementWithAttribute() {
        projection.setterXPathString("/newRoot/someElement/anotherOne/@newAttribute","value");       
        assertEquals("value",projection.getXPathValue("/newRoot/someElement/anotherOne/@newAttribute"));
    }

}
