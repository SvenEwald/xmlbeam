package org.xmlbeam.tutorial.e06_xhtml;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.junit.Test;
import org.xmlbeam.XMLProjector;
import org.xmlbeam.config.DefaultFactoriesConfiguration;

/**
 * Create and print some XHTML text. 
 * (Not that it would be productive to create a website this way, just a demonstration.)
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 *
 */
public class TestCreationOfXHTMLDocument {

	private final XMLProjector projector = new XMLProjector(new DefaultFactoriesConfiguration() {
		@Override
		public Transformer createTransformer() {
			Transformer transformer = super.createTransformer();
			// Enable some pretty printing of the resulting xml.
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			return transformer;

		}
	});

	@Test
	public void testCreateWellFormedXHTML() {
		XHTML xhtml = projector.createEmptyDocumentProjection(XHTML.class);

		xhtml.setRootNameSpace("http://www.w3.org/1999/xhtml").setRootLang("en");
		xhtml.setTitle("This Is My Fine Title");
		xhtml.setBody("Here some text...");
		
				
		System.out.println(xhtml.toString());
	}
}
