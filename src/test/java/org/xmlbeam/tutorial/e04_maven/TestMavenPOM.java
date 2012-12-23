package org.xmlbeam.tutorial.e04_maven;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;

public class TestMavenPOM {

	/**
	 * Show how to modify the project name in a maven POM.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testProjectNameWriting() throws SAXException, IOException, ParserConfigurationException {
		MavenPOM pom = new XMLProjector().readFromURIAnnotation(MavenPOM.class);
		assertEquals("Maven core", pom.getName());
		pom.setName("New name");
		assertEquals("New name", pom.getName());
	}
}
