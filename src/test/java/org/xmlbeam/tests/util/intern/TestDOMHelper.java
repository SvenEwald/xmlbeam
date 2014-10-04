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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.util.intern.DOMHelper;

@SuppressWarnings("javadoc")
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
        assertFalse(DOMHelper.nodesAreEqual(null, document));
    }
}
