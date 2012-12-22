package org.xmlbeam.tests;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import java.io.IOException;

import org.junit.Test;
import org.xmlbeam.URI;
import org.xmlbeam.XMLProjector;

/**
 * Tests to ensure the function of toString(), equals() and hashCode() for
 * projections.
 * 
 * @author sven
 * 
 */
public class TestObjectInvoker {

	@Test
	public void testToString() throws IOException {
		XMLBeamTestSuite testSuite = new XMLProjector().readFromURIAnnotation(XMLBeamTestSuite.class);
		testSuite.toString();
		String orig = new Scanner(TestObjectInvoker.class.getResourceAsStream(XMLBeamTestSuite.class.getAnnotation(URI.class).value().substring("resource://".length()))).useDelimiter("\\A").next();
		assertEquals(orig.replaceAll("\\s", ""), testSuite.toString().replaceAll("\\s", ""));
	}
}
