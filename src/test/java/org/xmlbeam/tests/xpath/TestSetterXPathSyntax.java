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
package org.xmlbeam.tests.xpath;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBWrite;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.tests.GenericXPathProjection;

@SuppressWarnings("javadoc")
public class TestSetterXPathSyntax {

    private XBProjector projector;
    private GenericXPathProjection projection;

    @Before
    public void init() throws IOException {
        projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        projection = projector.projectEmptyDocument(GenericXPathProjection.class);
    }

    @Test
    public void rootElementAccessAllowed() {
        projection.setterXPathProjection("/*", projector.projectEmptyElement("value", GenericXPathProjection.class));
        assertEquals("value", projection.getDOMOwnerDocument().getDocumentElement().getNodeName());
    }

    @Test(expected = XBException.class)
    public void emptyAccessNotAllowed() {
        projection.setterXPathString("", "");
    }

    @Test(expected = XBException.class)
    public void rootElementValueAccessAllowed() {
        projection.setterXPathString("/", "");
    }

    @Test(expected = XBException.class)
    public void emptyrootElementAttributeAccessNotAllowed() {
        projection.setterXPathString("/@attribute", "value");
    }

    @Test(expected = XBException.class)
    public void emptyrootElementEmptyAttributeNameNotAllowed() {
        projection.setterXPathString("/@", "");
    }

    @Test
    public void createRootElementWithAttribute() {
        projection.setterXPathString("/newRoot/@newAttribute", "value");
        assertEquals("newRoot", projection.getDOMOwnerDocument().getDocumentElement().getNodeName());
        assertEquals("value", projection.getDOMOwnerDocument().getDocumentElement().getAttribute("newAttribute"));
    }

    @Test
    public void createDeeperElementWithAttribute() {
        projection.setterXPathString("/newRoot/someElement/anotherOne/@newAttribute", "value");
        assertEquals("value", projection.getXPathValue("/newRoot/someElement/anotherOne/@newAttribute"));
    }

    public interface SubTest {
    }

    @XBDocURL("resource://testsetter.xml")
    public interface SetterTest {
        @XBRead("/a/b/c")
        SubTest getSubtest();

        @XBWrite("/a/d/*")
        public void set(SubTest s);

    }

    @Test
    public void testSetSubProjection() throws IOException {
        SetterTest test = projector.io().fromURLAnnotation(SetterTest.class);
        SubTest subtest = test.getSubtest();
        test.set(subtest);
        ((DOMAccess) test).getDOMOwnerDocument().normalizeDocument();
        assertEquals(projector.io().url("resource://testsetter_expected.xml").read(SetterTest.class), test);
    }

}
