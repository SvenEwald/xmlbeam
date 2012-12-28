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
package org.xmlbeam.tutorial.e03_eclipse;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.tutorial.Tutorial;

/**
 * This example is about accessing eclipse configuration profiles with a
 * parameterized projection.
 * 
 * See {@link EclipseFormatterConfigFile} for further description.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
@Category(Tutorial.class)
public class TestEclipseCodeFormatterConfig {

	@Test
	public void profilesTest() throws SAXException, IOException, ParserConfigurationException {
		EclipseFormatterConfigFile configFile = new XMLProjector().readFromURIAnnotation(EclipseFormatterConfigFile.class);
		System.out.println(configFile.getProfiles());
		System.out.println(configFile.getAllSettingsForProfile("Some Profile").size());
	}
}
