/**
 *  Copyright 2012 Sven Ewald
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlbeam.XBProjector;
import org.xmlbeam.tests.XMLBeamTestSuite.InnerStructure;
import org.xmlbeam.tests.XMLBeamTestSuite.Setting;
@SuppressWarnings("javadoc")
public class TestXMLProjector {

    private XMLBeamTestSuite suite;

    @Before
    public void init() throws Exception {
        suite = new XBProjector().io().fromURLAnnotation(XMLBeamTestSuite.class);
        assertNotNull(suite);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterWithTrailingSlash() {
        suite.setterWithTrailingSlash("nope");
    }

    @Test
    public void testStringContent() {
        assertEquals("String content öäüÖÄÜß", suite.getStringContent());
    }

    @Test
    public void testbyteContent() {
        assertEquals((byte) 0, suite.getbyteContent(2));
        assertEquals((byte) 1, suite.getbyteContent(3));
        assertEquals((byte) -1, suite.getbyteContent(5));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalbyte() {
        suite.getbyteContent(4);
    }

    @Test
    public void testByteContent() {
        assertEquals(Byte.valueOf((byte) 0), suite.getByteContent(2));
        assertEquals(Byte.valueOf((byte) 1), suite.getByteContent(3));
        assertEquals(Byte.valueOf((byte) -1), suite.getByteContent(5));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalByte() {
        suite.getByteContent(4);
    }

    @Test
    public void testshortContent() {
        assertEquals((short) 0, suite.getshortContent(2));
        assertEquals((short) 1, suite.getshortContent(3));
        assertEquals((short) -1, suite.getshortContent(5));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalshort() {
        suite.getshortContent(4);
    }

    @Test
    public void testShortContent() {
        assertEquals(Short.valueOf((short) 0), suite.getShortContent(2));
        assertEquals(Short.valueOf((short) 1), suite.getShortContent(3));
        assertEquals(Short.valueOf((short) -1), suite.getShortContent(5));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalShort() {
        suite.getShortContent(4);
    }

    @Test
    public void testintContent() {
        assertEquals(0, suite.getintContent(2));
        assertEquals(1, suite.getintContent(3));
        assertEquals(65536, suite.getintContent(4));
        assertEquals(-1, suite.getintContent(5));
    }

    @Test
    public void testIntContent() {
        assertEquals(Integer.valueOf(0), suite.getIntegerContent(2));
        assertEquals(Integer.valueOf(1), suite.getIntegerContent(3));
        assertEquals(Integer.valueOf(65536), suite.getIntegerContent(4));
        assertEquals(Integer.valueOf(-1), suite.getIntegerContent(5));
    }

    @Test
    public void testlongContent() {
        assertEquals(0L, suite.getlongContent(2));
        assertEquals(1L, suite.getlongContent(3));
        assertEquals(65536L, suite.getlongContent(4));
        assertEquals(-1L, suite.getlongContent(5));
    }

    @Test
    public void testLongContent() {
        assertEquals(Long.valueOf(0L), suite.getLongContent(2));
        assertEquals(Long.valueOf(1L), suite.getLongContent(3));
        assertEquals(Long.valueOf(65536L), suite.getLongContent(4));
        assertEquals(Long.valueOf(-1L), suite.getLongContent(5));
    }

    @Test
    public void testfloatContent() {
        assertEquals(0f, suite.getfloatContent(2), 0);
        assertEquals(1f, suite.getfloatContent(3), 0);
        assertEquals(65536f, suite.getfloatContent(4), 0);
        assertEquals(-1f, suite.getfloatContent(5), 0);
    }

    @Test
    public void testFloatContent() {
        assertEquals(Float.valueOf(0L), suite.getFloatContent(2));
        assertEquals(Float.valueOf(1L), suite.getFloatContent(3));
        assertEquals(Float.valueOf(65536L), suite.getFloatContent(4));
        assertEquals(Float.valueOf(-1L), suite.getFloatContent(5));
    }

    @Test
    public void testdoubleContent() {
        assertEquals(0d, suite.getdoubleContent(2), 0);
        assertEquals(1d, suite.getdoubleContent(3), 0);
        assertEquals(65536d, suite.getdoubleContent(4), 0);
        assertEquals(-1d, suite.getdoubleContent(5), 0);
    }

    @Test
    public void testDoubleContent() {
        assertEquals(Double.valueOf(0L), suite.getDoubleContent(2));
        assertEquals(Double.valueOf(1L), suite.getDoubleContent(3));
        assertEquals(Double.valueOf(65536L), suite.getDoubleContent(4));
        assertEquals(Double.valueOf(-1L), suite.getDoubleContent(5));
    }

    @Test
    public void testbooleanContent() {
        assertTrue(suite.getbooleanContent());
    }

    @Test
    public void testAttributeContent() {
        assertEquals("with Attribute", suite.getAttributeValue());
    }

    @Test
    public void testStringlist() {
        assertEquals("[a, b, c]", suite.getAStringList().toString());
    }

    @Test
    public void testStringArray() {
        org.junit.Assert.assertArrayEquals(new String[] { "a", "b", "c" }, suite.getAStringArray());
    }

    @Test
    public void testAttributeStringlist() {
        assertEquals("[a, b, c]", suite.getAttributeValuesAsStringlist().toString());
    }

    @Test
    public void innerStructure() {
        final InnerStructure firstInnerStructure = suite.getFirstInnerStructure();
        assertNotNull(firstInnerStructure);
        assertEquals("A", firstInnerStructure.getA());
        assertEquals("[A, B, C]", firstInnerStructure.getB().toString());
    }

    @Test
    public void listOfInnerStructures() {
        final List<InnerStructure> structures = suite.getAllInnerStructures();
        assertEquals(2, structures.size());

        assertEquals("A", structures.get(0).getA());
        assertEquals("[A, B, C]", structures.get(0).getB().toString());

        assertEquals("A2", structures.get(1).getA());
        assertEquals("[A2, B2, C2]", structures.get(1).getB().toString());
    }

    @Test
    public void listOfInnerStructuresEqualsArray() {
        Assert.assertArrayEquals(suite.getAllInnerStructures().toArray(), suite.getAllInnerStructuresAsArray());
    }

    @Test
    public void externalDocuments() {
        final List<Setting> externalSettings = suite.getExternalSettings("xml");
        assertEquals(3, externalSettings.size());
        assertEquals("A1", externalSettings.get(0).getName());
        assertEquals("B1", externalSettings.get(0).getValue().toString());
        assertFalse(externalSettings.get(0).hasOption());
        assertEquals("A2", externalSettings.get(1).getName());
        assertEquals("B2", externalSettings.get(1).getValue().toString());
        assertFalse(externalSettings.get(1).hasOption());
        assertEquals("A3", externalSettings.get(2).getName());
        assertEquals("B3", externalSettings.get(2).getValue().toString());
        assertTrue(externalSettings.get(2).hasOption());
    }

    @Test
    public void getXMLDocument() throws Exception {
        suite.setDescription("This is my description");
        final Document document = suite.getDOMOwnerDocument();
        assertEquals("gluerootnode", document.getDocumentElement().getNodeName());
    }

    @Test
    public void getXMLDocumentFromInnerStructure() throws Exception {
        final Document document = suite.getFirstInnerStructure().getDOMOwnerDocument();
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer transformer = tf.newTransformer();
        final StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        writer.getBuffer().toString();
    }

    @Test
    public void documentFromScratch() {
        final XMLBeamTestSuite emptyDocumentProjection = new XBProjector().projectEmptyDocument(XMLBeamTestSuite.class);
        final Document xmlDocForProjection = emptyDocumentProjection.getDOMOwnerDocument();
        assertNull(xmlDocForProjection.getDocumentElement());
    }
}
