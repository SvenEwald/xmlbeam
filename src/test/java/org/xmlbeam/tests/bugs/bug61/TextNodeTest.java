/**
 *  Copyright 2022 Sven Ewald
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
package org.xmlbeam.tests.bugs.bug61;

import static org.junit.Assert.assertEquals;

import org.jcp.xml.dsig.internal.dom.DOMUtils;
import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.annotation.XBRead;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.util.intern.DOMHelper;

public class TextNodeTest {

    interface Item {
        @XBRead("/item/text()")
        String getText();
    }
 
    @Test
    public void testWhitespaceOnlyTextNode() {
        XBProjector projector = new XBProjector(Flags.TO_STRING_RENDERS_XML);
        final Item item = projector.onXMLString("<item> </item>").createProjection(Item.class);
       // ((DOMAccess)item).getDOMNode();
        //DOMHelper.
        //System.out.println(item.toString());
        
        assertEquals(" ", item.getText());
    }
}