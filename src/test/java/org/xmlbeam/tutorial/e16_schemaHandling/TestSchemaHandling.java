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
package org.xmlbeam.tutorial.e16_schemaHandling;

import static org.junit.Assert.assertEquals;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.tutorial.Tutorial;
import org.xmlbeam.tutorial.TutorialTestCase;

//START SNIPPET: Tutorial16

/*START SNIPPET: TutorialDescription
~~
 This example shows how to use a schema with your projections.

 This time we read some data from xml files which use a defined schema. This way we can handle
 default values for attributes and honor restrictions.

 The solution to this shows:

 * Usage of a schema for validation xml source.

 * Reading default values specified in a schema works as expected.

 []

END SNIPPET: TutorialDescription */
@SuppressWarnings("javadoc")
@Category(Tutorial.class)
//START SNIPPET: SchemaHandling
public class TestSchemaHandling extends TutorialTestCase {

    Vegetables vegetables;

    @Before
    public void readVegetables() throws Exception {
        XBProjector projector = new XBProjector(new DefaultXMLFactoriesConfig(){

            private static final long serialVersionUID = 1L;

            @Override
            public DocumentBuilderFactory createDocumentBuilderFactory() {
                DocumentBuilderFactory factory = super.createDocumentBuilderFactory();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                try {
                    Schema schema = schemaFactory.newSchema(getClass().getResource("schema.xsd"));
                    factory.setSchema(schema);
                } catch (SAXException e) {
                    throw new RuntimeException("Error loading schema", e);
                }
                return factory;
            }
        });

        vegetables = projector.io().fromURLAnnotation(Vegetables.class);
    }

    @Test
    public void testReadActualValue() {
        assertEquals("red", vegetables.getVegetable("Tomato").getColor());
    }

    @Test
    public void testReadDefaultValueDefinedInSchema() {
        assertEquals("green", vegetables.getVegetable("Cucumber").getColor());
    }
}
//END SNIPPET: SchemaHandling
