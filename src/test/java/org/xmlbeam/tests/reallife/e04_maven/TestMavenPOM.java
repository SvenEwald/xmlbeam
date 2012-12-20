package org.xmlbeam.tests.reallife.e04_maven;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlbeam.XMLProjector;

public class TestMavenPOM {

	@Test
	public void testProjectNameWriting() throws SAXException, IOException, ParserConfigurationException {
		MavenPOM pom = new XMLProjector().readFromURIAnnotation(MavenPOM.class);
		System.out.println(pom.toString());
		assertEquals("Maven core", pom.getName());
		pom.setName("New name");
		System.out.println(pom.toString());
		assertEquals("New name", pom.getName());

	}
}
