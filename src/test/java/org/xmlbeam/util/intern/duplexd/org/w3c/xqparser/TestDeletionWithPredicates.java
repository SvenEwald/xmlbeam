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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.dom.DOMAccess;

@SuppressWarnings("javadoc")
public class TestDeletionWithPredicates {

    private final static String simpleExample = "<root><foo><bar id=\"3\"/><bar id=\"4\"/></foo></root>";
    private Document document;
    private DOMAccess projection;

    @Before
    public void init() {
        projection = new XBProjector(Flags.TO_STRING_RENDERS_XML).projectXMLString(simpleExample, DOMAccess.class);
        document = projection.getDOMOwnerDocument();
    }

    @Test
    public void testDelete() {
        DuplexExpression expression = new DuplexXPathParser().compile("/root/foo/bar[@id='4']");
        Element parent = expression.ensureParentExistence(document);
        assertEquals("foo", parent.getNodeName());
        assertEquals(2, parent.getChildNodes().getLength());
        expression.deleteAllMatchingChildren(parent);
        assertEquals(1, parent.getChildNodes().getLength());
    }
}
