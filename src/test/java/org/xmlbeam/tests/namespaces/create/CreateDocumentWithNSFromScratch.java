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

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.dom.DOMAccess;

/**
 * @author sven
 *
 */
@SuppressWarnings("javadoc")
public class CreateDocumentWithNSFromScratch {

    @Test
    public void createWithNSHandling() {
        DefaultXMLFactoriesConfig config = new DefaultXMLFactoriesConfig();
        config.createNameSpaceMapping().add("p1", "http://xmlbeam.org/tests/ns1");
        config.createNameSpaceMapping().add("p2", "http://xmlbeam.org/tests/ns2");
        DOMAccess doc = new XBProjector(config).projectEmptyDocument(DOMAccess.class);
        doc.create("/p1:root/p2:first/p1:second/value", "huhu1");
        doc.create("/p1:root/p2:first2/p1:second2/value", "huhu2");
        doc.create("/p1:root/p2:first/p1:second/p2:first/value", "huhu3");
        System.out.println(doc.asString());
    }
}
