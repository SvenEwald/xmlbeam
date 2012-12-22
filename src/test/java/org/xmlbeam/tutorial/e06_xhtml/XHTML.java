package org.xmlbeam.tutorial.e06_xhtml;

import org.xmlbeam.Xpath;

/**
 * Finally we go the other way around. We create a new document and fill the content via this projection interface.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 *
 */
public interface XHTML {

	@Xpath("/html/@xmlns")
	XHTML setRootNameSpace(String ns);
	
	@Xpath("/html/@xml:lang")
	XHTML setRootLang(String lang);
	
	@Xpath("/html/head/title")
	XHTML setTitle(String title);
	
	@Xpath("/html/body")
	XHTML setBody(String body);
}
