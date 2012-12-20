package org.xmlbeam.tests.reallife.e03_eclipse;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;

/**
 * This example is about accessing eclipse configuration profiles with a
 * parameterized projection.
 * 
 * See {@link EclipseFormatterConfigFile} for further description.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 * 
 */
public class TestEclipseCodeFormatterConfig {

	@Test
	public void profilesTest() throws SAXException, IOException, ParserConfigurationException {
		EclipseFormatterConfigFile configFile = new XMLProjector().readFromURIAnnotation(EclipseFormatterConfigFile.class);
		System.out.println(configFile.getProfiles());
		System.out.println(configFile.getAllSettingsForProfile("Some Profile").size());
	}
}
