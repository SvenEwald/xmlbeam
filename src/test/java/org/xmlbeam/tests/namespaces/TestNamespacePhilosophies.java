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
package org.xmlbeam.tests.namespaces;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.DefaultXMLFactoriesConfig.NamespacePhilosophy;
import org.xmlbeam.config.XMLFactoriesConfig;

public class TestNamespacePhilosophies {

    @Test
    public void testHedonisticPhilosophy() throws Exception {
        XMLFactoriesConfig config = new DefaultXMLFactoriesConfig().setNamespacePhilosophy(NamespacePhilosophy.HEDONISTIC);
        NamespacedProjection projection = new XBProjector(config).io().fromURLAnnotation(NamespacedProjection.class);
        assertEquals("I have no namespace.", projection.getElementContentWithoutNamespace().trim());
        assertEquals("I have a prefix.", projection.getElementContentWithPrefix().trim());
        assertEquals("I have a default namespace.", projection.getElementWithDefaultNamespace().trim());

        assertEquals("", projection.findElementContentWithPrefixOmittingNS().trim());
        assertEquals("", projection.findElementWithDefaultNamespaceOmittingNS().trim());

 //       try {
            assertEquals("", projection.findElementWithNonExistingPrefix());
//            fail("RuntimeException expected for missing prefix");
//        } catch (RuntimeException e) {

    }

    @Test
    public void testNihilisticPhilosophy() throws Exception {
        XMLFactoriesConfig config = new DefaultXMLFactoriesConfig().setNamespacePhilosophy(NamespacePhilosophy.NIHILISTIC);
        NamespacedProjection projection = new XBProjector(config).io().fromURLAnnotation(NamespacedProjection.class);        
        assertEquals("I have no namespace.", projection.getElementContentWithoutNamespace().trim());
        assertEquals("", projection.getElementContentWithPrefix().trim());
        assertEquals("", projection.getElementWithDefaultNamespace().trim());

        assertEquals("", projection.findElementContentWithPrefixOmittingNS().trim());
        assertEquals("I have a default namespace.", projection.findElementWithDefaultNamespaceOmittingNS().trim());

        assertEquals("", projection.findElementWithNonExistingPrefix());
    }

    @Test
    public void testAgnosticPhilosophy() throws Exception {
        DefaultXMLFactoriesConfig config = new DefaultXMLFactoriesConfig() {
            public DocumentBuilderFactory createDocumentBuilderFactory() {
                DocumentBuilderFactory factory = super.createDocumentBuilderFactory();                
                factory.setValidating(true);
                factory.setIgnoringElementContentWhitespace(true);
                return factory;
            }
            
            public DocumentBuilder createDocumentBuilder() {
                DocumentBuilder builder = super.createDocumentBuilder();
                
                builder.setErrorHandler(new DefaultHandler());                
                return builder;
            }
            
        };
        config.setNamespacePhilosophy(NamespacePhilosophy.AGNOSTIC);
        //XMLFactoriesConfig config = new DefaultXMLFactoriesConfig();
        NamespacedProjection projection = new XBProjector(config).io().fromURLAnnotation(NamespacedProjection.class);
        assertEquals("I have no namespace.", projection.getElementContentWithoutNamespace().trim());
        assertEquals("", projection.getElementContentWithPrefix().trim());
        assertEquals("", projection.getElementWithDefaultNamespace().trim());

        assertEquals("", projection.findElementContentWithPrefixOmittingNS().trim());
        assertEquals("I have a default namespace.", projection.findElementWithDefaultNamespaceOmittingNS().trim());

        assertEquals("", projection.findElementWithNonExistingPrefix());
    }

}
