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
package org.xmlbeam.refcards.namespaces;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;

@SuppressWarnings({ "javadoc", "serial" })
public class NSDemoTest {

    @Test
    public void testNoNamespacedElementAccess() throws IOException {
        XBProjector projector = new XBProjector(new DefaultXMLFactoriesConfig() {
            @Override
            public DocumentBuilderFactory createDocumentBuilderFactory() {
                DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
                instance.setNamespaceAware(false);
                return instance;
            }
        });
        projector.config().as(DefaultXMLFactoriesConfig.class).setNamespacePhilosophy(NamespacePhilosophy.AGNOSTIC);

        NameSpaceProjection projection = projector.io().fromURLAnnotation(NameSpaceProjection.class);
        assertTrue(projection.getTable().contains("African Coffee Table"));
        assertNull(projection.getNamepsacedTable());
        assertNull(projection.getDefaultNamepsacedTable());
//        System.out.println(projection.getTable());
//         System.out.println(projection.getNamepsacedTable());
//System.out.println(projection.getDefaultNamepsacedTable());
    }

}
