package org.xmlbeam.tests.reallife.e06_xhtml;

import javax.xml.transform.OutputKeys;

import org.junit.Test;
import org.xmlbeam.XMLProjector;

/**
 * Create and print some XHTML text. 
 * (Not that it would be productive to create a website this way, just a demonstration.)
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 *
 */
public class TestCreationOfXHTMLDocument {

	private XMLProjector projector = new XMLProjector();

	@Test
	public void testCreateWellFormedXHTML() {
		XHTML xhtml = projector.createEmptyDocumentProjection(XHTML.class);

		xhtml.setRootNameSpace("http://www.w3.org/1999/xhtml").setRootLang("en");
		xhtml.setTitle("This Is My Fine Title");
		xhtml.setBody("Here some text...");
		
		// Enable some pretty printing of the resulting xml.
		projector.getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		projector.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
				
		System.out.println(xhtml.toString());
	}
}
