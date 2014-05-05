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

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.annotation.XBDocURL;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.annotation.XBUpdate;

@SuppressWarnings("javadoc")
public class TestUpdatetInvoker {

    @XBDocURL("resource://testsuite.xml")
    public interface Update {
        @XBRead("/gluerootnode/intermediate/firstelement/innerElement1")
        String getInner1();

        @XBUpdate("/gluerootnode/intermediate/firstelement/innerElement1")
        void setInner1(String newValue);

        @XBRead("/gluerootnode/intermediate/secondElement/list/entry")
        List<String> getList();

        @XBUpdate("/gluerootnode/intermediate/secondElement/list/entry")
        int setEntries(String value);
        
        @XBRead("//@glueattribute")
        String getAttribute();
        
        @XBUpdate("//@glueattribute")
        int setAttribute(String value);

    }

    private Update projection;

    @Before
    public void prepare() throws IOException {
        projection = new XBProjector().io().fromURLAnnotation(Update.class);
    }

    @Test
    public void testBasicUpdate() {
        assertEquals("String content öäüÖÄÜß", projection.getInner1());
        projection.setInner1("foo");
        assertEquals("foo", projection.getInner1());
    }

    @Test
    public void multipleUpdates() {
        assertEquals("[a, b, c]", projection.getList().toString());
        projection.setEntries("xxx");
        assertEquals("[xxx, xxx, xxx]", projection.getList().toString());
    }

    @Test
    public void attributeUpdate() {
        assertEquals("with Attribute",projection.getAttribute());
        assertEquals(1,projection.setAttribute("Huhu"));
        assertEquals("Huhu",projection.getAttribute());
        assertEquals(1,projection.setAttribute(null));
        assertEquals(0,projection.setAttribute("foo"));
        assertEquals("",projection.getAttribute());
    }
    
}
