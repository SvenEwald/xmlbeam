/**
 *  Copyright 2015 Sven Ewald
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
package org.xmlbeam.tests.namespaces.create;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.XBProjector.Flags;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;
import org.xmlbeam.testutils.DOMDiagnoseHelper;

/**
 * @author sven
 */
@SuppressWarnings("javadoc")
public class CreateDocumentWithDefaultNSFromScratchTest {

    final static String expectedXML = "<root xmlns=\"http://xmlbeam.org/tests/nsdef\">\n" + 
            "  <p2:first xmlns:p2=\"http://xmlbeam.org/tests/ns2\">\n" + 
            "    <p1:second xmlns:p1=\"http://xmlbeam.org/tests/ns1\">\n" + 
            "      <value>huhu1</value>\n" + 
            "      <p2:first>\n" + 
            "        <value>huhu3</value>\n" + 
            "      </p2:first>\n" + 
            "    </p1:second>\n" + 
            "  </p2:first>\n" + 
            "  <p2:first2 xmlns:p2=\"http://xmlbeam.org/tests/ns2\">\n" + 
            "    <p1:second2 xmlns:p1=\"http://xmlbeam.org/tests/ns1\">\n" + 
            "      <value>huhu2</value>\n" + 
            "    </p1:second2>\n" + 
            "  </p2:first2>\n" + 
            "</root>";
    DefaultXMLFactoriesConfig config = new DefaultXMLFactoriesConfig();
    {
        config.createNameSpaceMapping().add("p1", "http://xmlbeam.org/tests/ns1");
        config.createNameSpaceMapping().add("p2", "http://xmlbeam.org/tests/ns2");
        config.createNameSpaceMapping().add("xbdefaultns", "http://xmlbeam.org/tests/nsdef");
    }
    final private XBProjector projector = new XBProjector(config,Flags.TO_STRING_RENDERS_XML);

    @Test
    public void createWithNSHandling() {
        DOMAccess doc = projector.projectEmptyDocument(DOMAccess.class);
        doc.create("/xbdefaultns:root/p2:first/p1:second/value", "huhu1");
        doc.create("/xbdefaultns:root/p2:first2/p1:second2/value", "huhu2");
        doc.create("/xbdefaultns:root/p2:first/p1:second/p2:first/value", "huhu3");
        DOMDiagnoseHelper.assertXMLStringsEquals(expectedXML, doc.asString());        
    }

    @Test
    public void createDocWithNSByMap() {
        Map<String, String> map = projector.autoMapEmptyDocument(String.class);
        map.put("/xbdefaultns:root/p2:first/p1:second/value", "huhu1");
        map.put("/xbdefaultns:root/p2:first2/p1:second2/value", "huhu2");
        map.put("/xbdefaultns:root/p2:first/p1:second/p2:first/value", "huhu3");
        DOMDiagnoseHelper.assertXMLStringsEquals(expectedXML, projector.asString(map));
        assertEquals("huhu1",map.get("/xbdefaultns:root/p2:first/p1:second/value"));

    }
}
